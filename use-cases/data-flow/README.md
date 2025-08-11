# Real-Time Orders Demo with GemFire & Spring Cloud Data Flow

This project demonstrates how to build a low-code, real-time data pipeline using **Spring Cloud Data Flow (SCDF)**, **GemFire**, and the **GemFire Management Console**. It captures incoming order events and routes them into country-specific GemFire regions, which can be displayed on a live dashboard.

---

## Getting Started

You can use your own GemFire and SCDF setup, or get started quickly with the provided Docker Compose files and app jars.

### Option 1: Quickstart (Recommended)

- [Start GemFire and the GemFire Management Console via Docker Compose](https://gemfire.dev/quickstart/docker)
- [Start SCDF locally](https://dataflow.spring.io/docs/installation/local/manual/)
- Download and install the required Maven app jars:
    - The default Maven jars, available out of the box.
    - [GemFire Sink](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/spring-cloud-data-flow-for-tanzu-gemfire/1-0/gf-scdf/gemfire-sink-rabbitmq.html)
    - [GemFire Source](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/spring-cloud-data-flow-for-tanzu-gemfire/1-0/gf-scdf/gemfire-source-rabbitmq.html)

Then follow the steps below to deploy streams and display the dashboard.

### Option 2: Bring Your Own Cluster

If you're running your own SCDF and GemFire clusters, just make sure:
- The GemFire regions exist
- The GemFire Management Console is running and connected to the GemFire cluster
- The SCDF apps are registered
- The connection properties match your environment


---

## GemFire Cluster Setup
With your GemFire cluster running (via the provided Docker Compose file)

* **Connect to GemFire using the GemFire Management Console**

    * Open the GemFire Management Console:  http://localhost:7072
    * Click “Connect”

        * Cluster Nickname: `Awesome Products`
        * Host: `gemfire-locator-0`
        * Port: `7070`

* **Create Region**

    * Go to the **Regions** tab
    * Click **Create Region**

        * Name: `usa-orders`
        * Type: `Partition`
        * Enable Stats

You’ll repeat this process later for additional regions like `canada-orders` and `mexico-orders`.

---

## SCDF Setup

### Stream Incoming USA Orders to GemFire to the Dashboard

This set of streams:
- receives orders from the UI via HTTP POSTs
- writes them into the `usa-orders` region
- retrieves them in real-time from the `usa-orders` region
- sends them to the front end to display on the dashboard

**Stream 1: Ingest HTTP orders into a shared SCDF destination.**
1. Click 'Create Stream'
2. Drag and drop 'http' from the `SOURCE` section.
3. Set the following properties
    * General → Name: `all-incoming-orders`
    * Server → Port: `8085`
4. Drag and drop a 'destination' from the `OTHER` section at the bottom.
5. Set the following properties
    * General → Name: `orders-incoming`


6. Your text box at the top of the page should look similar to this
      ```bash
      all-incoming-orders: http --port=8085 > :orders-incoming
      ```
7. Click 'Create Stream'
8. Click `Deploy` then Click `Deploy` again (at the bottom of the page)



**Stream 2: Filter USA orders and write to GemFire.**
1. Click 'Create Stream'
2. Drag and drop a `destination` from the `OTHER` section at the bottom.
3. Set the following properties
    * General → Name: `orders-incoming` (this needs to match the same name as the previous stream)
4. Drag and drop a `filter` from the `PROCESSOR` section
5. Set the following properties
    * General → label: `filter-usa`
    * filter.function -> expression: `"payload.country == 'USA'"` (Include quotes exactly as shown)
6. Drag and drop a `gemfire-sink` from the `SINK` section
7. Set the following properties
    * General → label: `usa-orders-sink`
    * gemfire.pool -> host-addresses: `localhost:10334`
    * gemfire.region -> region-name: `usa-orders`
    * gemfire.consumer -> key-expression: `payload.getField('orderId')`
    * gemfire.consumer -> json: Check the box
8. Your text box at the top of the page should look similar to this
    ```bash
     :orders-incoming > filter-usa: filter --expression="payload.country == 'USA'" | usa-orders-sink: gemfire-sink --region-name=usa-orders --key-expression=payload.getField('orderId') --json=true --gemfire.pool.host-addresses=localhost:10334
    ```
9. Click 'Create Stream'
10. Click `Deploy` then Click `Deploy` again (at the bottom of the page)


You now have a working ingestion pipeline!
Place a USA order from `index.html` and confirm the `usa-orders` region entry count increments in the GemFire Management Console.

---

Now let’s display the orders on the dashboard

**Stream 3: Read USA orders from GemFire.**
1. Click 'Create Stream'
2. Drag and drop a `gemfire-source` from the `SOURCE` section.
3. Set the following properties
    * General → label: `usa-orders-source`
    * gemfire.pool -> subscription-enabled: Check the box
    * gemfire.pool -> connect-type: `locator`
    * gemfire.pool -> host-addresses: `localhost:10334`
    * gemfire.supplier -> query: `"SELECT * FROM /usa-orders"`
    * gemfire.region -> region-name: `usa-orders`
4. Drag and drop a `destination` from the `OTHER` section at the bottom.
5. Set the following properties
    * General → Name: `orders-for-dashboard`
6. Your text box at the top of the page should look similar to this
   ```bash
      gemfire-source --region-name=usa-orders --query="SELECT * FROM /usa-orders" --subscription-enabled=true --connect-type=locator --gemfire.pool.host-addresses=localhost:10334 > :orders-for-dashboard
   ```
7. Click 'Create Stream'
8. Click `Deploy`
9. At the top of the page click the "Freetext" tab and add the following property to the bottom of the page:
   ```bash
   app.gemfire-source.spring.cloud.stream.bindings.output.content-type=text/plain
   ```

10. Then Click `Deploy` at the bottom of the page.


**Stream 4: Send orders to the dashboard via WebSocket.**
1. Drag and drop a `destination` from the `OTHER` section at the bottom.
2. Set the following properties
    * General → Name: `orders-for-dashboard` (this must match the name from **Step 5** in **Stream 3** )
3. Drag and drop a `websocket` from the `SINK` section at the bottom.
4. Your text box at the top of the page should look similar to this
   ```bash
   :orders-for-dashboard > websocket
   ```
5. Click 'Create Stream'
6. Click `Deploy`
7. At the top of the page click the "Freetext" tab and add the following properties to the bottom of the page:
   ```properties
   app.websocket.spring.cloud.stream.bindings.input.content-type=application/json
   app.websocket.message-mapping-expression=payload
   ```
8. Then Click `Deploy` at the bottom of the page.

You now have an end-to-end data pipeline that will display all USA orders on the dashboard.

---

## Multi-Country Orders Routing

Let's imagine you now want to add additional countries. How would we go about doing this?

### Objective:
Route orders to country-specific regions (`usa-orders`, `canada-orders`, `mexico-orders`) and display them dynamically on the dashboard. For this example, we've included Mexico and Canada as options. If you'd like to add additional countries simply add them to the `index.html` page.

### Create Additional Regions

In the GemFire Management Console, create a new region for each additional country.

* `canada-orders` (Partition, Stats enabled)
* `mexico-orders` (Partition, Stats enabled)


### Additional Streams in SCDF
Now you'll need to create streams similar to what was previously done above ( with the `usa-orders` region).

**1: Create Inbound HTTP Destination**

This was already done during our creation of Stream 1. So we don't need to do anything.

**2: Create Country Filters -> Country-specific GemFire Sinks**

- Canada Filter -> Canada Sink

   ```
   :orders-in > filter-can: filter --expression="payload.country == 'CAN'" | sink-can: gemfire-sink --region-name=canada-orders --key-expression=payload.getField('orderId') --json=true --gemfire.pool.host-addresses=localhost:10334
   ```
- Mexico Filter -> Mexico Sink
   ```
   :orders-in > filter-mex: filter --expression="payload.country == 'MEX'" | sink-mex: gemfire-sink --region-name=mexico-orders --key-expression=payload.getField('orderId') --json=true --gemfire.pool.host-addresses=localhost:10334
   ```
**3: Create Country Source -> Destination**

- Canada Source -> Destination
   ```
   gemfire-source --region-name=canada-orders --query="SELECT * FROM /canada-orders" --subscription-enabled=true --connect-type=locator --gemfire.pool.host-addresses=localhost:10334 > :orders-for-dashboard
   ```
- Mexico Source -> Destination
    ```
      gemfire-source --region-name=mexico-orders --query="SELECT * FROM /mexico-orders" --subscription-enabled=true --connect-type=locator --gemfire.pool.host-addresses=localhost:10334 > :orders-for-dashboard
      ```
> Before deploying these Source -> Destination pipelines make sure to add the following properties to the `Freetext` section of the deployment.
> ```
> app.gemfire-source.spring.cloud.stream.bindings.output.content-type=text/plain```


At this point, your dashboard should display real-time orders for USA, Canada, and Mexico, with each country’s orders stored in its own GemFire region.
