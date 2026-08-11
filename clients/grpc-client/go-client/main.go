// Copyright 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Claude
 * Description:
 * 2026-08-11: Add grpc go client to gemfire-examples clients.
 */

package main

import (
	"context"
	"fmt"
	"log"
	"time"

	pb "github.com/gemfire/gemfire-examples/clients/grpc-client/go-client/gemfire/v1"
	personpb "github.com/gemfire/gemfire-examples/clients/grpc-client/go-client/test/v1"
	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	xdscreds "google.golang.org/grpc/credentials/xds"
	"google.golang.org/grpc/status"
	_ "google.golang.org/grpc/xds"
	"google.golang.org/protobuf/types/known/anypb"

	"google.golang.org/grpc/credentials/insecure"
)

// defaultTarget is the only dial target this example ever uses — the real customer-facing
// discovery path, resolved through the locator's xDS ADS server.
const defaultTarget = "xds:///gemfire_grpc"

func main() {
	// xdscreds.NewClientCredentials wraps the plaintext fallback so that, if the locator's CDS
	// Cluster advertises a transport_socket (TLS), the discovered CRUD server connection
	// actually uses it — bare insecure.NewCredentials() ignores CDS-advertised security
	// entirely and always dials in plaintext, regardless of what the Cluster resource says.
	creds, err := xdscreds.NewClientCredentials(xdscreds.ClientOptions{
		FallbackCreds: insecure.NewCredentials(),
	})
	if err != nil {
		log.Fatalf("Failed to create xDS client credentials: %v", err)
	}

	// Connect to the server
	conn, err := grpc.NewClient(defaultTarget, grpc.WithTransportCredentials(creds))
	if err != nil {
		log.Fatalf("Failed to connect: %v", err)
	}
	defer conn.Close()

	client := pb.NewCacheServiceClient(conn)
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	// Test PUT operations with different key types
	fmt.Println("=== Testing PUT operations ===")

	// String keys
	putString(client, ctx, "region1", "key1", "value1")
	putString(client, ctx, "region1", "key2", "value2")

	// Integer keys
	putInt(client, ctx, "region1", 100, "value3")
	putInt(client, ctx, "region2", 200, "value4")

	// Test GET_AND_PUT operations with different key types
	fmt.Println("\n=== Testing GET_AND_PUT operations ===")

	// String keys
	getAndPutString(client, ctx, "region1", "key1", "new_value1")
	getAndPutString(client, ctx, "region1", "new_key", "new_value2")

	// Integer keys
	getAndPutInt(client, ctx, "region1", 100, "new_value3")
	getAndPutInt(client, ctx, "region1", 999, "new_value4")

	// Test GET operations with different key types
	fmt.Println("\n=== Testing GET operations ===")

	// String keys
	getString(client, ctx, "region1", "key1")
	getString(client, ctx, "region1", "key2")
	getString(client, ctx, "region1", "nonexistent")

	// Integer keys
	getInt(client, ctx, "region1", 100)
	getInt(client, ctx, "region2", 200)
	getInt(client, ctx, "region1", 999)

	// Test non-existent region
	getString(client, ctx, "nonexistent", "key1")

	// Test REMOVE operations with different key types
	fmt.Println("\n=== Testing REMOVE operations ===")

	// String keys
	removeString(client, ctx, "region1", "key1")
	removeString(client, ctx, "region1", "nonexistent")

	// Integer keys
	removeInt(client, ctx, "region1", 100)
	removeInt(client, ctx, "region1", 999)

	// Test GET_AND_REMOVE operations with different key types
	fmt.Println("\n=== Testing GET_AND_REMOVE operations ===")

	// String keys
	getAndRemoveString(client, ctx, "region1", "key2")
	getAndRemoveString(client, ctx, "region1", "nonexistent")

	// Integer keys
	getAndRemoveInt(client, ctx, "region2", 200)
	getAndRemoveInt(client, ctx, "region1", 999)

	// Test Person with Any
	fmt.Println("\n=== Testing Person with Any ===")
	testPersonWithAny(client, ctx, "region1", "person_key")
}

func putString(client pb.CacheServiceClient, ctx context.Context, regionName, key, value string) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_String_{String_: key},
	}
	valueMsg := &pb.Value{
		ValueValue: &pb.Value_String_{String_: value},
	}
	req := &pb.PutRequest{
		RegionName: regionName,
		Key:        keyMsg,
		Value:      valueMsg,
	}

	_, err := client.Put(ctx, req)
	if err != nil {
		fmt.Printf("PUT failed (string key): %v\n", err)
		return
	}

	fmt.Printf("PUT successful (string key)\n")
}

func putInt(client pb.CacheServiceClient, ctx context.Context, regionName string, key int32, value string) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_Int{Int: key},
	}
	valueMsg := &pb.Value{
		ValueValue: &pb.Value_String_{String_: value},
	}
	req := &pb.PutRequest{
		RegionName: regionName,
		Key:        keyMsg,
		Value:      valueMsg,
	}

	_, err := client.Put(ctx, req)
	if err != nil {
		fmt.Printf("PUT failed (int key): %v\n", err)
		return
	}

	fmt.Printf("PUT successful (int key)\n")
}

func getAndPutString(client pb.CacheServiceClient, ctx context.Context, regionName, key, value string) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_String_{String_: key},
	}
	valueMsg := &pb.Value{
		ValueValue: &pb.Value_String_{String_: value},
	}
	req := &pb.GetAndPutRequest{
		RegionName: regionName,
		Key:        keyMsg,
		Value:      valueMsg,
	}

	resp, err := client.GetAndPut(ctx, req)
	if err != nil {
		fmt.Printf("GET_AND_PUT failed (string key): %v\n", err)
		return
	}

	if resp.Value != nil {
		oldValueStr := resp.Value.GetString_()
		fmt.Printf("GET_AND_PUT successful (string key): Key=%s, OldValue=%s, NewValue=%s\n", key, oldValueStr, value)
	} else {
		fmt.Printf("GET_AND_PUT successful (string key): Key=%s, OldValue=<none>, NewValue=%s\n", key, value)
	}
}

func getAndPutInt(client pb.CacheServiceClient, ctx context.Context, regionName string, key int32, value string) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_Int{Int: key},
	}
	valueMsg := &pb.Value{
		ValueValue: &pb.Value_String_{String_: value},
	}
	req := &pb.GetAndPutRequest{
		RegionName: regionName,
		Key:        keyMsg,
		Value:      valueMsg,
	}

	resp, err := client.GetAndPut(ctx, req)
	if err != nil {
		fmt.Printf("GET_AND_PUT failed (int key): %v\n", err)
		return
	}

	if resp.Value != nil {
		oldValueStr := valueToString(resp.Value)
		fmt.Printf("GET_AND_PUT successful (int key): Key=%d, OldValue=%s, NewValue=%s\n", key, oldValueStr, value)
	} else {
		fmt.Printf("GET_AND_PUT successful (int key): Key=%d, OldValue=<none>, NewValue=%s\n", key, value)
	}
}

func removeString(client pb.CacheServiceClient, ctx context.Context, regionName, key string) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_String_{String_: key},
	}
	req := &pb.RemoveRequest{
		RegionName: regionName,
		Key:        keyMsg,
	}

	_, err := client.Remove(ctx, req)
	if err != nil {
		if status.Code(err) == codes.NotFound {
			fmt.Printf("REMOVE (string key): Key=%s was not found\n", key)
		} else {
			fmt.Printf("REMOVE failed (string key): %v\n", err)
		}
		return
	}

	fmt.Printf("REMOVE successful (string key): Key=%s was removed\n", key)
}

func removeInt(client pb.CacheServiceClient, ctx context.Context, regionName string, key int32) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_Int{Int: key},
	}
	req := &pb.RemoveRequest{
		RegionName: regionName,
		Key:        keyMsg,
	}

	_, err := client.Remove(ctx, req)
	if err != nil {
		if status.Code(err) == codes.NotFound {
			fmt.Printf("REMOVE (int key): Key=%d was not found\n", key)
		} else {
			fmt.Printf("REMOVE failed (int key): %v\n", err)
		}
		return
	}

	fmt.Printf("REMOVE successful (int key): Key=%d was removed\n", key)
}

func getAndRemoveString(client pb.CacheServiceClient, ctx context.Context, regionName, key string) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_String_{String_: key},
	}
	req := &pb.GetAndRemoveRequest{
		RegionName: regionName,
		Key:        keyMsg,
	}

	resp, err := client.GetAndRemove(ctx, req)
	if err != nil {
		if status.Code(err) == codes.NotFound {
			fmt.Printf("GET_AND_REMOVE (string key): Key=%s was not found\n", key)
		} else {
			fmt.Printf("GET_AND_REMOVE failed (string key): %v\n", err)
		}
		return
	}

	if resp.Value != nil {
		valueStr := resp.Value.GetString_()
		fmt.Printf("GET_AND_REMOVE successful (string key): Key=%s, RemovedValue=%s\n", key, valueStr)
	} else {
		fmt.Printf("GET_AND_REMOVE (string key): Key=%s was not found\n", key)
	}
}

func getAndRemoveInt(client pb.CacheServiceClient, ctx context.Context, regionName string, key int32) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_Int{Int: key},
	}
	req := &pb.GetAndRemoveRequest{
		RegionName: regionName,
		Key:        keyMsg,
	}

	resp, err := client.GetAndRemove(ctx, req)
	if err != nil {
		if status.Code(err) == codes.NotFound {
			fmt.Printf("GET_AND_REMOVE (int key): Key=%d was not found\n", key)
		} else {
			fmt.Printf("GET_AND_REMOVE failed (int key): %v\n", err)
		}
		return
	}

	if resp.Value != nil {
		valueStr := valueToString(resp.Value)
		fmt.Printf("GET_AND_REMOVE successful (int key): Key=%d, RemovedValue=%s\n", key, valueStr)
	} else {
		fmt.Printf("GET_AND_REMOVE (int key): Key=%d was not found\n", key)
	}
}

func getString(client pb.CacheServiceClient, ctx context.Context, regionName, key string) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_String_{String_: key},
	}
	req := &pb.GetRequest{
		RegionName: regionName,
		Key:        keyMsg,
	}

	resp, err := client.Get(ctx, req)
	if err != nil {
		if status.Code(err) == codes.NotFound {
			fmt.Printf("GET failed: Key not found\n")
		} else {
			log.Printf("GET failed: %v", err)
		}
		return
	}

	if resp.Value != nil {
		valueStr := resp.Value.GetString_()
		fmt.Printf("GET successful (string key): Key=%s, Value=%s\n", key, valueStr)
	} else {
		fmt.Printf("GET failed: Key not found\n")
	}
}

func getInt(client pb.CacheServiceClient, ctx context.Context, regionName string, key int32) {
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_Int{Int: key},
	}
	req := &pb.GetRequest{
		RegionName: regionName,
		Key:        keyMsg,
	}

	resp, err := client.Get(ctx, req)
	if err != nil {
		if status.Code(err) == codes.NotFound {
			fmt.Printf("GET failed: Key not found\n")
		} else {
			log.Printf("GET failed: %v", err)
		}
		return
	}

	if resp.Value != nil {
		valueStr := valueToString(resp.Value)
		fmt.Printf("GET successful (int key): Key=%d, Value=%s\n", key, valueStr)
	} else {
		fmt.Printf("GET failed: Key not found\n")
	}
}

func valueToString(value *pb.Value) string {
	switch v := value.ValueValue.(type) {
	case *pb.Value_String_:
		return v.String_
	case *pb.Value_Bytes:
		return fmt.Sprintf("[bytes:%d bytes]", len(v.Bytes))
	case *pb.Value_Int32:
		return fmt.Sprintf("%d", v.Int32)
	case *pb.Value_Int64:
		return fmt.Sprintf("%d", v.Int64)
	case *pb.Value_Bool:
		return fmt.Sprintf("%t", v.Bool)
	case *pb.Value_Double:
		return fmt.Sprintf("%f", v.Double)
	case *pb.Value_Float:
		return fmt.Sprintf("%f", v.Float)
	case *pb.Value_Any:
		return fmt.Sprintf("[Any:%s]", v.Any.TypeUrl)
	default:
		return "[unknown value type]"
	}
}

func testPersonWithAny(client pb.CacheServiceClient, ctx context.Context, regionName, key string) {
	// Create a Person instance
	person := &personpb.Person{
		FirstName: "John",
		LastName:  "Doe",
	}

	// Pack Person into Any
	anyValue, err := anypb.New(person)
	if err != nil {
		log.Printf("Failed to pack Person into Any: %v", err)
		return
	}

	// Create the value with Any
	valueMsg := &pb.Value{
		ValueValue: &pb.Value_Any{Any: anyValue},
	}

	// Create key
	keyMsg := &pb.Key{
		KeyValue: &pb.Key_String_{String_: key},
	}

	// Put request
	putReq := &pb.PutRequest{
		RegionName: regionName,
		Key:        keyMsg,
		Value:      valueMsg,
	}

	// Put the Person
	_, err = client.Put(ctx, putReq)
	if err != nil {
		log.Printf("PUT failed (Person with Any): %v", err)
		return
	}

	fmt.Printf("PUT successful (Person with Any): Key=%s, Person=%s %s\n", key, person.FirstName, person.LastName)

	// Get request
	getReq := &pb.GetRequest{
		RegionName: regionName,
		Key:        keyMsg,
	}

	// Get the Person
	getResp, err := client.Get(ctx, getReq)
	if err != nil {
		if status.Code(err) == codes.NotFound {
			fmt.Printf("GET failed: Key not found\n")
		} else {
			log.Printf("GET failed (Person with Any): %v", err)
		}
		return
	}

	if getResp.Value != nil {
		if anyVal := getResp.Value.GetAny(); anyVal != nil {
			// Unpack the Any back to Person
			var retrievedPerson personpb.Person
			err = anyVal.UnmarshalTo(&retrievedPerson)
			if err != nil {
				log.Printf("Failed to unpack Person from Any: %v", err)
				return
			}

			fmt.Printf("GET successful (Person with Any): Key=%s, Person=%s %s\n", key, retrievedPerson.FirstName, retrievedPerson.LastName)
		} else {
			fmt.Printf("GET (Person with Any): Value is not Any type\n")
		}
	} else {
		fmt.Printf("GET failed: Key not found\n")
	}
}
