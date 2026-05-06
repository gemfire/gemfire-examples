# Tanzu GemFire Distributed Types Lab

An interactive, browser-based lab demonstrating the power of the Tanzu GemFire Distributed Types library.

This project demonstrates how state can be shared across independent JVMs. It runs two separate Spring Boot backends connected to a single GemFire cluster. The frontend is designed to maintain no local state; instead, it reflects the real-time data held within the GemFire cluster by polling each backend independently.

---

## Architecture Overview

```text
+-------------------------------------------------------------+
|                      GemFire Cluster                         |
|  [Locator: 10334]    [Server: 40404]    [Server: 40405]     |
+-------------------------------------------------------------+
        |                     |                  |
        +----------+----------+------------------+
                              | 
                 +------------+-------------+
                 |                          |
      +--------------+           +--------------+
      | Spring Boot  |           | Spring Boot  |
      |   Client A   |           |   Client B   |
      |    :8080     |           |    :8081     |
      +--------------+           +--------------+
             |                          |
             +------------+-------------+
                          |
                  +-------v-------+
                  |   Alpine.js   | <--- heartbeat
                  |   Frontend    |      (every 2s)
                  +---------------+
```

* **Heartbeat Sync:** The UI maintains no local data arrays. Every 2 seconds, it independently queries both backends to fetch the current cluster state.
* **Dual-Backend Proof:** By running on different ports, the demo proves that data consistency is enforced at the server level. What you see in the browser is a direct reflection of the GemFire data structures.

---

## Prerequisites and Setup

### 1. Maven Credentials
Distributed Types are hosted on the Broadcom Maven Repository. Add your credentials to `~/.m2/settings.xml`. Registry tokens are available at the Broadcom Support Portal under My Downloads -> Registry Tokens.

```xml
<server>
  <id>gemfire-repository</id>
  <username>YOUR-BROADCOM-EMAIL</username>
  <password>YOUR-REGISTRY-TOKEN</password>
</server>
```

### 2. Launch the Cluster
```bash
docker-compose up -d
```
Wait approximately 20 seconds for the nodes to synchronize and for the built-in extension JAR to initialize.

### 3. Start the Clients
Open two separate terminal windows and run the backends:

* **Terminal A (Client A):** `mvn spring-boot:run`
* **Terminal B (Client B):** `mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"`

---

## The 30-Second Test Sequence

1. **Open the Lab:** Navigate to http://localhost:8080.
2. **The Consistency Test:** Go to the **DSet** tab. Add an item on **Client A**. Watch **Client B** update automatically within 2 seconds.
3. **The Blocking Test:** Go to **DBlockingQueue**. Click "Consume" on **Client B**. Notice the "THREAD BLOCKED" overlay. Now, click "Produce" on **Client A**. Watch Client B unblock and receive the job instantly.
4. **The Resiliency Test:** Kill the **Client B** terminal process. Watch the connection dot in the UI turn **Red**. Add data on Client A. Restart Client B and watch it synchronize to the cluster state.

---

## Distributed Types Reference

| Type | Use Case |
| :--- | :--- |
| **DSet** | Exactly-once processing, unique session tracking. |
| **DList** | Shared audit logs, ordered event sequences. |
| **DBlockingQueue** | Work-stealing patterns, distributed task workers. |
| **DCircularQueue** | Last-N action buffers, log tailing. |
| **DAtomics** | Global counters, strict rate-limiting, shared config. |
| **DSemaphore** | Throttling access to limited external resources. |
| **DSnowflake** | High-scale, time-sortable unique IDs. |

---

## Troubleshooting

* **Networking:** If clients cannot connect, ensure `hostname-for-clients=localhost` is present in your `docker-compose.yml`. This ensures the cluster advertises its location as `localhost` rather than internal Docker IDs.
* **CORS:** If one client works but the other is "Red," check the browser console for CORS errors. The backend `CorsConfig.java` must permit both `8080` and `8081`.
* **Initialization:** If the UI shows empty lists even after backends start, ensure the `gemfire-distributed-types` JAR was correctly picked up by the servers during the Docker startup.

---

## Under the Hood
The `docker-compose.yml` file automates the following GemFire configuration:
* Starts a Locator on port 10334.
* Starts two Servers.
* Auto-deploys the Distributed Types extension JAR.