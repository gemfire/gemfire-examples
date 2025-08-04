# GemFire JSON Example

<!--
  ~ Copyright (c) VMware, Inc. 2023. All rights reserved.
  -->


The GemFire JSON-related APIs allow you to convert a JSON String into a binary form called `JsonDocument` that can be stored in a GemFire region.
`JsonDocument`s use less memory and have better performance than JSON strings.

This example starts a GemFire cluster with one locator and two servers with a replicated region called `example-region`.
It then uses the JSON-related APIs to create a `JsonDocumentFactory` which converts a JSON string into a `JsonDocument`.
The converted JSON strings are stored as region entry values.

The example uses JSON strings that contain fields of different types. For example:

```json
{
  "booleanField": true,
  "intField": 0,
  "name": "name0",
  "nestedField": 
  {
    "field1": 5,
    "field2": "something",
    "field3": 55555.0
  },
  "arrayField": [0, "Fred", 4.0, true]
}
```

The `JsonDocument` API is used to get the values of fields of a JSON document. For example, `JsonDocument.getField()`.

For details about `JsonDocumentFactory` and `JsonDocument`. Please refer to the Javadoc or GemFire documentation.

## Steps to run the JSON example

### Build the code

```
$ ../gradlew build
```

### Start a GemFire cluster with one locator and two servers

```
$ gfsh run --file=scripts/start.gfsh
```

### Run the example code

```
$ ../gradlew run
```

It first creates a GemFire client with a proxy client cache. 
Then it converts a number of JSON strings to `JsonDocument`s with the default BSON storage format and put them to the `example-region`.

BSON and PDX are two underlying storage formats for `JsonDocument`.
For the details, benefits and use cases of BSON and PDX storage formats, please refer to the GemFire documentation.

`JsonDocument.toJson()` is used to convert a `JsonDocument` back to a JSON string.

`JsonDocument.getField()` is used to get the value of a specific JSON field, including a nested field.

OQL query can be used to query JSON fields as well. For example:

Query: `select * from /example-region where name='name5'`

Query result:

```json
[
  {"booleanField": true,
    "intField": 5,
    "name": "name5",
    "nestedField": 
    {
      "field1": 5,
      "field2": "something",
      "field3": 55555.0
    },
    "arrayField": [5, "Fred", 4.0, true]
  }
]
```

Query: `select * from /example-region where arrayField[0]=6`

Query result:

```json
[
  {
    "booleanField": true,
    "intField": 6,
    "name": "name6",
    "nestedField":
    {
      "field1": 5,
      "field2": "something",
      "field3": 55555.0},
    "arrayField": [6, "Fred", 4.0, true]
  }
]
```

### Stop the GemFire cluster

```
$ gfsh run --file=scripts/stop.gfsh
```