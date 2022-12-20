<!--
  ~ Copyright (c) VMware, Inc. 2022. All rights reserved.
  ~ SPDX-License-Identifier: Apache-2.0
  -->
<!--
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# VMware GemFire JDBC Connector Example

The JDBC Connector allows VMware GemFire to connect to external data sources with JDBC.

![VMware GemFire JDBC Connector](connector.svg)

## Steps:

1. Install MySQL: https://dev.mysql.com/downloads/

If your MySQL installation does not include JDBC driver, 
download it from https://dev.mysql.com/downloads/connector/j/

2. Start MySQL server with `mysql.server start`. 
Use `mysql` CLI to create database, table and populate the table:

```
create database gemfire_db;

use gemfire_db;

create table parent(id bigint, name varchar(100), income double);

insert into parent values (2, 'Parent_2', 987654321.0);
```

3. Build this example's jar file `jdbc.jar` by running `../gradlew clean build`.

The jar file `jdbc.jar` will be generated in `build/libs` directory.

4. Add MySQL JDBC driver jar and `jdbc.jar` to `CLASSPATH`.

e.g. 
```
export CLASSPATH=/path/to/mysql-connector-java-8.0.15.jar:/path/to/gemfire-examples/jdbc/build/libs/jdbc.jar
```

5. Start the GemFire cluster with `gfsh run --file=scripts/start.gfsh`.

This will start the locator and two servers. And create `Parent` region, data source and JDBC mapping.

6. Create data source and map the VMware GemFire region and MySQL table.

```
gfsh

connect

create data-source --name=mysql_data_source --url="jdbc:mysql://localhost/gemfire_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC" --username=root --password="changeme"

create jdbc-mapping --data-source=mysql_data_source --region=Parent --table=parent --pdx-name=org.apache.geode_examples.jdbc.Parent --catalog=gemfire_db --id=id

```

7. Run the example with `../gradlew run`.

This will first `put` an entry with key 1 in `Parent` region. 
The entry will be propagated to MySQL's `parent` table in database `gemfire_db`.
Then it will invoke a `get` with key 2. Since `Parent` region does not have an entry with key equals 2, 
it will trigger JDBC Connector to load the entry from `parent` table in database `gemfire_db` from MySQL.
 
You can also use `gfsh` to connect to cluster and run the following commands:
`list data-source`
`describe data-source`
`list jdbc-mapping`
`describe jdbc-mapping`
`destroy jdbc-mapping`
`destroy data-source`

And use `mysql` to query the `parent` table.

8. Shutdown the cluster with `gfsh run --file=scripts/stop.gfsh`.