# Quickstart Tutorial - Inline Cache Loader
This demonstrates how to create a spring boot app that connects to GemFire that is configured as an inline cache.
More details for this configuration can be found in the [GemFire documentation](https://docs.pivotal.io/p-cloud-cache/1-12/inline-setup.html).

## Download Examples and Configure Environment

Download and install VMware GemFire from [Tanzu Network](https://network.pivotal.io). Follow the installation instructions in the [GemFire documentation](https://docs.vmware.com/en/VMware-GemFire/index.html).

--------

setup postgres

    brew install postgresql
    brew services start postgresql
    pgbench -i -s 50 postgres

    psql postgres
    create user myuser with encrypted password 'mypass';
    grant all privileges on database postgres to myuser;
    grant all privileges on all tables in schema public to myuser;

create postgres example data

    psql postgres -U myuser;
    update pgbench_tellers set filler = 'hello' where tid = 1;

download postgres driver

    curl https://jdbc.postgresql.org/download/postgresql-42.6.0.jar > postgresql-42.6.0.jar

build project

    ./gradlew build

configure gemfire

    ./<location of gemfire>/bin/gfsh
    start locator --name locator
    start server --name='server' --classpath="<directory for postgres jar downloaded above>/postgresql-42.6.0.jar:<project root directory>/cache-loader/build/libs/gemfire-inlineCaching-0.0.1-SNAPSHOT.jar" --J="-Dpostgres.username=myuser" --J="-Dpostgres.password=mypass"
    create async-event-queue --listener=io.vmware.event.ItemAsyncEventListener --id=item-writebehind-queue --batch-size=10 --batch-time-interval="20"
    create region --name=item --type=REPLICATE --cache-loader=io.vmware.event.ItemCacheLoader --async-event-queue-id=item-writebehind-queue


start webservice

    ./gradlew bootRun

## test cache read

retrieve value from webservice

    curl localhost:8080/1          # should see the value hello

change value on the postgres database

    psql postgres -U myuser;
    update pgbench_tellers set filler = 'goodbye' where tid = 1;

retrieve value from webservice

    curl localhost:8080/1          # should still see the value hello because it is cached

clear the cache in gemfire

    remove --key=1 --region=item

retrieve value from webservice

    curl localhost:8080/1          # should see the value goodbye

## test cache write

retrieve value from webservice

    curl localhost:8080/1          # should see the value goodbye (or hello if you didn't run the read test)

write value to webservice

    curl localhost:8080/1/potato -X PUT

see value in postgres

    psql postgres -U myuser;
    select filler from pgbench_tellers where tid=1;  -- should see potato

retrieve value from webservice

    curl localhost:8080/1          # should see the value potato


## Notes

* if you just want a 1:1 mapping for a region to a database table, see the [jdbc example](https://github.com/gemfire/gemfire-examples/tree/main/feature-examples/jdbc)
* if you want the cache to expire after a certain amount of time, see the [expiration example](https://github.com/gemfire/gemfire-examples/blob/main/feature-examples/expiration)
