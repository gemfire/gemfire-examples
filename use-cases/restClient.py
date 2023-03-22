#!/usr/bin/env python3

# This assumes you have created a region called "demoRegion".

import json
import uuid
import requests

REGION = "demoRegion"
BASE_URI = "http://localhost:8080/gemfire-api/v1"

headers = {'content-type': 'application/json'}

person = {'type': 'Person',
          'firstName': 'John',
          'middleName': 'Q',
          'lastName': 'Public',
          'birthDate': '1 Jan 1900'}


def resource_uri(res=None, region=REGION):
    if res:
        return "%s/%s/%s" % (BASE_URI, region, res)
    return "%s/%s" % (BASE_URI, region)


print("[*] First, we'll empty out our demo region - DELETE %s" %
      requests.delete(resource_uri()))

r = requests.delete(resource_uri())
r.raise_for_status()

print("[*] Now, we'll create 5 demo objects")

keys = []

for i in range(1, 6):
    key = uuid.uuid1()

    keys.append(key)
    person['uuid'] = str(key)

    print("\t Creating object with key: POST %s" % key)
    r = requests.post(resource_uri(), json=person,
                      params={'key': key},
                      headers=headers)
    r.raise_for_status()

print("[*] List our keys - GET %s" % resource_uri("keys"))

r = requests.get(resource_uri("keys"))
print(r.text)

print("[*] Here's all our data - GET %s" % resource_uri())

r = requests.get(resource_uri())
print(r.text)

print("[*] Now each key one by one")

for key in keys:
    print("Fetching key - GET %s" % resource_uri(res=key))
    r = requests.get(resource_uri(res=key))
    print(r.text)

print("[*] Now grab one, change the first name to 'Jane' and save it")

print("  GET - %s" % resource_uri(res=keys[0]))
r = requests.get(resource_uri(res=keys[0]))
p = json.loads(r.text)
p['firstName'] = 'Jane'
print("  PUT - %s" % resource_uri(res=keys[0]))
r = requests.put(resource_uri(res=keys[0]), json=p,
                 headers=headers)

print("  GET - %s" % resource_uri(res=keys[0]))
r = requests.get(resource_uri(res=keys[0]))
print(r.text)
