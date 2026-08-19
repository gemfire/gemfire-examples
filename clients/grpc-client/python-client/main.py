# Copyright 2026 Broadcom. All Rights Reserved.

"""
@AI-Generated
Generated in whole or in part by Claude
Description:
2026-08-12: Add grpc python client to gemfire-examples clients.
"""

import sys
import grpc
from google.protobuf import any_pb2
from grpc.experimental import session_cache

# Add generated code to python path
import os
sys.path.append(os.path.join(os.path.dirname(__file__), 'gemfire/v1'))
sys.path.append(os.path.join(os.path.dirname(__file__), 'test/v1'))

import gemfire_pb2 as pb
import gemfire_pb2_grpc as pb_grpc
import person_pb2 as personpb

# default_target is the only dial target this example ever uses — the real customer-facing
# discovery path, resolved through the locator's xDS ADS server.
DEFAULT_TARGET = "xds:///gemfire_grpc"

def main():
    # Set up the gRPC channel
    # In Python, we can use insecure_channel directly for xDS if TLS is not required.
    # However, to support both TLS and plaintext via xDS, we use xds_channel_credentials
    # with local_channel_credentials as the plaintext fallback (since our demo runs locally).
    fallback_creds = grpc.local_channel_credentials(grpc.LocalConnectionType.LOCAL_TCP)
    channel_credentials = grpc.xds_channel_credentials(fallback_creds)

    print(f"Connecting to {DEFAULT_TARGET}...")
    with grpc.secure_channel(DEFAULT_TARGET, channel_credentials) as channel:
        client = pb_grpc.CacheServiceStub(channel)

        # Test PUT operations with different key types
        print("\n=== Testing PUT operations ===")
        
        # String keys
        put_string(client, "region1", "key1", "value1")
        put_string(client, "region1", "key2", "value2")
        
        # Integer keys
        put_int(client, "region1", 100, "value3")
        put_int(client, "region2", 200, "value4")

        # Test GET_AND_PUT operations with different key types
        print("\n=== Testing GET_AND_PUT operations ===")
        
        # String keys
        get_and_put_string(client, "region1", "key1", "new_value1")
        get_and_put_string(client, "region1", "new_key", "new_value2")
        
        # Integer keys
        get_and_put_int(client, "region1", 100, "new_value3")
        get_and_put_int(client, "region1", 999, "new_value4")

        # Test GET operations with different key types
        print("\n=== Testing GET operations ===")
        
        # String keys
        get_string(client, "region1", "key1")
        get_string(client, "region1", "key2")
        get_string(client, "region1", "nonexistent")
        
        # Integer keys
        get_int(client, "region1", 100)
        get_int(client, "region2", 200)
        get_int(client, "region1", 999)
        
        # Test non-existent region
        get_string(client, "nonexistent", "key1")

        # Test REMOVE operations with different key types
        print("\n=== Testing REMOVE operations ===")
        
        # String keys
        remove_string(client, "region1", "key1")
        remove_string(client, "region1", "nonexistent")
        
        # Integer keys
        remove_int(client, "region1", 100)
        remove_int(client, "region1", 999)

        # Test GET_AND_REMOVE operations with different key types
        print("\n=== Testing GET_AND_REMOVE operations ===")
        
        # String keys
        get_and_remove_string(client, "region1", "key2")
        get_and_remove_string(client, "region1", "nonexistent")
        
        # Integer keys
        get_and_remove_int(client, "region2", 200)
        get_and_remove_int(client, "region1", 999)

        # Test Person with Any
        print("\n=== Testing Person with Any ===")
        test_person_with_any(client, "region1", "person_key")

def put_string(client, region_name, key, value):
    try:
        req = pb.PutRequest(
            region_name=region_name,
            key=pb.Key(string=key),
            value=pb.Value(string=value)
        )
        client.Put(req, timeout=10)
        print("PUT successful (string key)")
    except grpc.RpcError as e:
        print(f"PUT failed (string key): {e.details()}")

def put_int(client, region_name, key, value):
    try:
        req = pb.PutRequest(
            region_name=region_name,
            key=pb.Key(int=key),
            value=pb.Value(string=value)
        )
        client.Put(req, timeout=10)
        print("PUT successful (int key)")
    except grpc.RpcError as e:
        print(f"PUT failed (int key): {e.details()}")

def get_and_put_string(client, region_name, key, value):
    try:
        req = pb.GetAndPutRequest(
            region_name=region_name,
            key=pb.Key(string=key),
            value=pb.Value(string=value)
        )
        resp = client.GetAndPut(req, timeout=10)
        
        if resp.HasField("value"):
            old_value = value_to_string(resp.value)
            print(f"GET_AND_PUT successful (string key): Key={key}, OldValue={old_value}, NewValue={value}")
        else:
            print(f"GET_AND_PUT successful (string key): Key={key}, OldValue=<none>, NewValue={value}")
    except grpc.RpcError as e:
        print(f"GET_AND_PUT failed (string key): {e.details()}")

def get_and_put_int(client, region_name, key, value):
    try:
        req = pb.GetAndPutRequest(
            region_name=region_name,
            key=pb.Key(int=key),
            value=pb.Value(string=value)
        )
        resp = client.GetAndPut(req, timeout=10)
        
        if resp.HasField("value"):
            old_value = value_to_string(resp.value)
            print(f"GET_AND_PUT successful (int key): Key={key}, OldValue={old_value}, NewValue={value}")
        else:
            print(f"GET_AND_PUT successful (int key): Key={key}, OldValue=<none>, NewValue={value}")
    except grpc.RpcError as e:
        print(f"GET_AND_PUT failed (int key): {e.details()}")

def remove_string(client, region_name, key):
    try:
        req = pb.RemoveRequest(
            region_name=region_name,
            key=pb.Key(string=key)
        )
        client.Remove(req, timeout=10)
        print(f"REMOVE successful (string key): Key={key} was removed")
    except grpc.RpcError as e:
        if e.code() == grpc.StatusCode.NOT_FOUND:
            print(f"REMOVE (string key): Key={key} was not found")
        else:
            print(f"REMOVE failed (string key): {e.details()}")

def remove_int(client, region_name, key):
    try:
        req = pb.RemoveRequest(
            region_name=region_name,
            key=pb.Key(int=key)
        )
        client.Remove(req, timeout=10)
        print(f"REMOVE successful (int key): Key={key} was removed")
    except grpc.RpcError as e:
        if e.code() == grpc.StatusCode.NOT_FOUND:
            print(f"REMOVE (int key): Key={key} was not found")
        else:
            print(f"REMOVE failed (int key): {e.details()}")

def get_and_remove_string(client, region_name, key):
    try:
        req = pb.GetAndRemoveRequest(
            region_name=region_name,
            key=pb.Key(string=key)
        )
        resp = client.GetAndRemove(req, timeout=10)
        
        if resp.HasField("value"):
            value_str = value_to_string(resp.value)
            print(f"GET_AND_REMOVE successful (string key): Key={key}, RemovedValue={value_str}")
        else:
            print(f"GET_AND_REMOVE (string key): Key={key} was not found")
    except grpc.RpcError as e:
        if e.code() == grpc.StatusCode.NOT_FOUND:
            print(f"GET_AND_REMOVE (string key): Key={key} was not found")
        else:
            print(f"GET_AND_REMOVE failed (string key): {e.details()}")

def get_and_remove_int(client, region_name, key):
    try:
        req = pb.GetAndRemoveRequest(
            region_name=region_name,
            key=pb.Key(int=key)
        )
        resp = client.GetAndRemove(req, timeout=10)
        
        if resp.HasField("value"):
            value_str = value_to_string(resp.value)
            print(f"GET_AND_REMOVE successful (int key): Key={key}, RemovedValue={value_str}")
        else:
            print(f"GET_AND_REMOVE (int key): Key={key} was not found")
    except grpc.RpcError as e:
        if e.code() == grpc.StatusCode.NOT_FOUND:
            print(f"GET_AND_REMOVE (int key): Key={key} was not found")
        else:
            print(f"GET_AND_REMOVE failed (int key): {e.details()}")

def get_string(client, region_name, key):
    try:
        req = pb.GetRequest(
            region_name=region_name,
            key=pb.Key(string=key)
        )
        resp = client.Get(req, timeout=10)
        
        if resp.HasField("value"):
            value_str = value_to_string(resp.value)
            print(f"GET successful (string key): Key={key}, Value={value_str}")
        else:
            print("GET failed: Key not found")
    except grpc.RpcError as e:
        if e.code() == grpc.StatusCode.NOT_FOUND:
            print("GET failed: Key not found")
        else:
            print(f"GET failed: {e.details()}")

def get_int(client, region_name, key):
    try:
        req = pb.GetRequest(
            region_name=region_name,
            key=pb.Key(int=key)
        )
        resp = client.Get(req, timeout=10)
        
        if resp.HasField("value"):
            value_str = value_to_string(resp.value)
            print(f"GET successful (int key): Key={key}, Value={value_str}")
        else:
            print("GET failed: Key not found")
    except grpc.RpcError as e:
        if e.code() == grpc.StatusCode.NOT_FOUND:
            print("GET failed: Key not found")
        else:
            print(f"GET failed: {e.details()}")

def value_to_string(value):
    field_name = value.WhichOneof("value_value")
    if field_name == "string":
        return value.string
    elif field_name == "bytes":
        return f"[bytes:{len(value.bytes)} bytes]"
    elif field_name == "int32":
        return str(value.int32)
    elif field_name == "int64":
        return str(value.int64)
    elif field_name == "bool":
        return str(value.bool)
    elif field_name == "double":
        return str(value.double)
    elif field_name == "float":
        return str(value.float)
    elif field_name == "any":
        return f"[Any:{value.any.type_url}]"
    else:
        return "[unknown value type]"

def test_person_with_any(client, region_name, key):
    # Create a Person instance
    person = personpb.Person(
        first_name="John",
        last_name="Doe"
    )
    
    # Pack Person into Any
    any_value = any_pb2.Any()
    any_value.Pack(person)
    
    # Create the value with Any
    value_msg = pb.Value(any=any_value)
    
    # Create key
    key_msg = pb.Key(string=key)
    
    # Put request
    put_req = pb.PutRequest(
        region_name=region_name,
        key=key_msg,
        value=value_msg
    )
    
    # Put the Person
    try:
        client.Put(put_req, timeout=10)
        print(f"PUT successful (Person with Any): Key={key}, Person={person.first_name} {person.last_name}")
    except grpc.RpcError as e:
        print(f"PUT failed (Person with Any): {e.details()}")
        return
        
    # Get request
    get_req = pb.GetRequest(
        region_name=region_name,
        key=key_msg
    )
    
    # Get the Person
    try:
        get_resp = client.Get(get_req, timeout=10)
        
        if get_resp.HasField("value"):
            if get_resp.value.HasField("any"):
                # Unpack the Any back to Person
                retrieved_person = personpb.Person()
                get_resp.value.any.Unpack(retrieved_person)
                print(f"GET successful (Person with Any): Key={key}, Person={retrieved_person.first_name} {retrieved_person.last_name}")
            else:
                print("GET (Person with Any): Value is not Any type")
        else:
            print("GET failed: Key not found")
            
    except grpc.RpcError as e:
        if e.code() == grpc.StatusCode.NOT_FOUND:
            print("GET failed: Key not found")
        else:
            print(f"GET failed (Person with Any): {e.details()}")

if __name__ == '__main__':
    # Required for xDS support in Python gRPC
    import os
    os.environ["GRPC_EXPERIMENTAL_XDS_RLS_LB"] = "true"
    main()
