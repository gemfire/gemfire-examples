# Tanzu GemFire Distributed Types Lab

An interactive, browser-based lab demonstrating the power of the Tanzu GemFire Distributed Types extension.

This project demonstrates how state can be shared across independent JVMs. It runs two separate Spring Boot backends connected to a single GemFire cluster. The frontend is designed to maintain no local state; instead, it reflects the real-time data held within the GemFire cluster by polling each backend independently.

---

## Documentation & API Reference

For detailed guides, configuration options, and method signatures, refer to the official documentation:

* **[Tanzu GemFire Distributed Types Product Documentation](https://techdocs.broadcom.com/us/en/vmware-tanzu/data-solutions/tanzu-gemfire-distributed-types/1-0/gf-distributed-types/overview.html)**

* **[Tanzu GemFire Distributed Types Java API Reference (Javadocs)](https://developer.broadcom.com/xapis/tanzu-gemfire-distributed-types-java-api-reference/latest/dev/gemfire/dtype/package-summary.html)**

---

## Architecture Overview

```text
+-------------------------------------------------------------+
|                      GemFire Cluster                        |
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

## Prerequisites

### Maven Credentials

Distributed Types are hosted on the Broadcom Maven Repository. Ensure your credentials are set in your `~/.m2/settings.xml`. Registry tokens are available at the [Broadcom Support Portal](https://support.broadcom.com/) under *My Downloads -> Registry Tokens*.

```xml
<server>
  <id>gemfire-repository</id>
  <username>YOUR-BROADCOM-EMAIL</username>
  <password>YOUR-REGISTRY-TOKEN</password>
</server>

```

---

## How to Run the Lab

1. **Start the GemFire Cluster:**
    ```bash
    docker-compose up -d
    ```
  The locator and both servers have health checks configured. Wait until all three containers show `(healthy)` before starting the clients. You can watch the status live with:

  ```bash
  watch docker ps
  ```

  Or run this one-liner that blocks until the entire cluster is confirmed ready:

  ```bash
  docker compose wait gemfire-server-1 gemfire-server-2 || \
    until [ "$(docker inspect --format='{{.State.Health.Status}}' gemfire-server-1)" = "healthy" ] && \
          [ "$(docker inspect --format='{{.State.Health.Status}}' gemfire-server-2)" = "healthy" ]; \
    do echo "Waiting for cluster..."; sleep 5; done && echo "Cluster ready."
  ```

2. **Start the Clients (in separate terminal windows):**
* **Client A:** 
   ```bash
    ./mvnw spring-boot:run
  ```

* **Client B:** 
   ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
  ```

---

## The 30-Second Test Sequence

1. **Open the Lab:** Navigate to http://localhost:8080.
2. **The Consistency Test:** Go to the **DSet** tab. Add an item on **Client A**. Watch **Client B** update automatically.
3. **The Blocking Test:** Go to **DBlockingQueue**. Click "Consume" on **Client B**. Notice the "THREAD BLOCKED" overlay. Now, click "Produce" on **Client A**. Watch Client B unblock and receive the job instantly.
4. **The Resiliency Test:** Kill the **Client B** terminal process (`CTRL+C`). Watch the connection dot in the UI turn **Red**. Add data on Client A. Restart Client B and watch it instantly synchronize to the cluster state.

---

## How to Stop the Lab

To cleanly shut down the environment and release your ports:

1. Press `CTRL+C` in the terminal windows running your Spring Boot clients.
2. Run the following command to stop the GemFire cluster:
```bash
docker-compose down -v
```
*(The `-v` flag is recommended to wipe the GemFire data directories and ensure a clean start for your next session).*

---

## Distributed Types Reference

| Type | Description |
| --- | --- |
| **DSet** | A distributed and highly available `Set` that is backed by a GemFire cluster. |
| **DList** | A distributed and highly available `List` that is backed by a GemFire cluster. |
| **DBlockingQueue** | An implementation of a `BlockingDeque` that is distributed and highly available. |
| **DCircularQueue** | An implementation of a `Queue` that provides a FIFO queue with a fixed size that replaces its oldest element if full. |
| **DAtomicLong** | A distributed and highly-available implementation of `AtomicLong`. |
| **DAtomicReference** | A distributed and highly-available implementation of `AtomicReference`. |
| **DCounter** | Similar to `DAtomicLong`, but designed for higher throughput and less potential contention. |
| **DSemaphore** | A highly-available, distributed version of the JDK's `Semaphore`. |
| **DCountDownLatch** | A distributed version of a synchronization aid that allows one or more threads to wait until a set of operations completes. |
| **DSnowflake** | A cluster-unique ID generator based on the Twitter/X Snowflake design. |

---

## Troubleshooting

* **CORS Errors:** If one client works but the other shows as "Red" (disconnected) in the UI, check the browser console. The backend `CorsConfig.java` uses `.allowedOriginPatterns("*")` to permit cross-origin polling.
* **NoAvailableServersException:** The Spring Boot app started before the GemFire servers finished booting. Wait until both `gemfire-server-1` and `gemfire-server-2` show `(healthy)` in `docker ps`, then restart the client.
* **Zombie Processes:** If ports are already in use, run `jps -l` to find and kill any orphaned Java processes, or `docker-compose down -v` to reset the container state.

---

## Under the Hood

The `docker-compose.yml` file automates the following orchestration:

* Starts a Locator on port 10334.
* Starts two Data Servers.
* Auto-deploys the Distributed Types extension JAR (bundled inside the `gemfire-all` image).