// Copyright 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Claude
 * Description:
 * 2026-09-10: gRPC legs of the interop example: --mode=put writes Protobuf, --mode=get reads it.
 */

package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"os"
	"time"

	gemfirepb "github.com/gemfire/gemfire-examples/clients/grpc-client/interop/gemfire/v1"
	personpb "github.com/gemfire/gemfire-examples/clients/grpc-client/interop/test/v1"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	xdscreds "google.golang.org/grpc/credentials/xds"
	_ "google.golang.org/grpc/xds"
	"google.golang.org/protobuf/types/known/anypb"
)

// The only dial target this example uses. The locator's xDS ADS server resolves it, which is the
// discovery path a real application uses. grpc-xds reads its bootstrap from GRPC_XDS_BOOTSTRAP.
const defaultTarget = "xds:///gemfire_grpc"

const regionName = "people"

func main() {
	mode := flag.String("mode", "", "put writes Protobuf messages; get reads the GemFire-written entry")
	flag.Parse()

	if *mode != "put" && *mode != "get" {
		fmt.Fprintln(os.Stderr, "Usage: interop-client --mode=put|get")
		os.Exit(1)
	}

	// xdscreds wraps the plaintext fallback so that, if the locator's CDS Cluster advertises a
	// transport_socket (TLS), the discovered CRUD server connection uses it. Bare
	// insecure.NewCredentials() ignores CDS-advertised security and always dials plaintext.
	credentials, err := xdscreds.NewClientCredentials(xdscreds.ClientOptions{
		FallbackCreds: insecure.NewCredentials(),
	})
	if err != nil {
		log.Fatalf("Failed to create xDS client credentials: %v", err)
	}

	connection, err := grpc.NewClient(defaultTarget, grpc.WithTransportCredentials(credentials))
	if err != nil {
		log.Fatalf("Failed to connect: %v", err)
	}
	defer connection.Close()

	client := gemfirepb.NewCacheServiceClient(connection)
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if *mode == "put" {
		put(ctx, client, "alice", &personpb.Person{FirstName: "Alice", LastName: "Anderson"})
		put(ctx, client, "bob", &personpb.Person{FirstName: "Bob", LastName: "Barnes"})
		return
	}

	get(ctx, client, "grace")
}

func put(ctx context.Context, client gemfirepb.CacheServiceClient, key string,
	person *personpb.Person) {
	// anypb.New sets the type URL to type.googleapis.com/test.v1.Person. The server stores the
	// Any as-is, wrapped in a ProtoAnyDocument. It never needs the Person type to do that.
	anyValue, err := anypb.New(person)
	if err != nil {
		log.Fatalf("Failed to pack Person into Any: %v", err)
	}

	_, err = client.Put(ctx, &gemfirepb.PutRequest{
		RegionName: regionName,
		Key:        &gemfirepb.Key{KeyValue: &gemfirepb.Key_String_{String_: key}},
		Value:      &gemfirepb.Value{ValueValue: &gemfirepb.Value_Any{Any: anyValue}},
	})
	if err != nil {
		log.Fatalf("gRPC put %s failed: %v", key, err)
	}

	fmt.Printf("gRPC put %s -> %s %s %s\n", key, anyValue.GetTypeUrl(),
		person.GetFirstName(), person.GetLastName())
}

func get(ctx context.Context, client gemfirepb.CacheServiceClient, key string) {
	response, err := client.Get(ctx, &gemfirepb.GetRequest{
		RegionName: regionName,
		Key:        &gemfirepb.Key{KeyValue: &gemfirepb.Key_String_{String_: key}},
	})
	if err != nil {
		log.Fatalf("gRPC get %s failed: %v", key, err)
	}

	// Get reports a missing entry with an unset value, not a NOT_FOUND status.
	if response.GetValue() == nil {
		fmt.Printf("gRPC get %s -> not found\n", key)
		return
	}

	anyValue := response.GetValue().GetAny()
	if anyValue == nil {
		fmt.Printf("gRPC get %s -> value is not an Any\n", key)
		return
	}

	fmt.Printf("gRPC get %s -> %s\n", key, anyValue.GetTypeUrl())

	var person personpb.Person
	if err := anyValue.UnmarshalTo(&person); err != nil {
		fmt.Printf("  unpack failed: %v\n", err)
		return
	}

	fmt.Printf("  Person: %s %s\n", person.GetFirstName(), person.GetLastName())
}
