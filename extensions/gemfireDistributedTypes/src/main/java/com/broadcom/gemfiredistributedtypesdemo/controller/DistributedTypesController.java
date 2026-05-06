package com.broadcom.gemfiredistributedtypesdemo.controller;

import dev.gemfire.dtype.*;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DistributedTypesController {

    private final ClientCache cache;
    private DTypeFactory factory;
    private DSnowflake snowflake;

    public DistributedTypesController(ClientCache cache) {
        this.cache = cache;
    }

    @PostConstruct
    public void init() {
        this.factory = new DTypeFactory(cache);
        // Worker ID 1L (You could differentiate this via environment variables if desired)
        this.snowflake = DSnowflake.builder().build();
    }

    // -------------------------------------------------------------------------
    // Sync — The "Global Truth" heartbeat for the UI
    // -------------------------------------------------------------------------
    @GetMapping("/sync")
    public Map<String, Object> sync() {
        // Add the <String> types here
        List<String> dset = new ArrayList<>(factory.<String>createDSet("demo-dset"));
        List<String> dlist = new ArrayList<>(factory.<String>createDList("demo-dlist"));

        // Also include your queues in the sync so the UI sees them!
        List<String> dqueue = new ArrayList<>(factory.<String>createDBlockingQueue("demo-dbq", 100));
        List<String> dcirc = new ArrayList<>(factory.<String>createDCircularQueue("demo-dcq", 3));

        return Map.of(
                "dset", dset,
                "dlist", dlist,
                "dqueue", dqueue,
                "dcirc", dcirc,
                "dcounter", factory.createDCounter("demo-dcounter", 0).get(),
                "datomiclong", factory.createDAtomicLong("demo-dal", 0L).get(),
                "datomicreference", factory.createDAtomicReference("demo-dar", "{\"status\": \"IDLE\"}").get(),
                "latchCount", factory.createDCountDownLatch("demo-dcdl", 3).getCount()
        );
    }

    // -------------------------------------------------------------------------
    // DSet
    // -------------------------------------------------------------------------
    @PostMapping("/dset/add")
    public Map<String, Object> dsetAdd(@RequestParam String value) {
        DSet dSet = factory.createDSet("demo-dset");
        boolean wasAdded = dSet.add(value);
        System.out.println("[GemFire Report] Adding '" + value + "' result: " + wasAdded);
        return Map.of("added", wasAdded);
    }


    @PostMapping("/dset/remove")
    public Map<String, Object> dsetRemove(@RequestParam String value) {
        return Map.of("removed", factory.createDSet("demo-dset").remove(value));
    }

    // -------------------------------------------------------------------------
    // DList
    // -------------------------------------------------------------------------
    @PostMapping("/dlist/add")
    public Map<String, Object> dlistAdd(@RequestParam String value) {
        return Map.of("added", factory.createDList("demo-dlist").add(value));
    }

    @PostMapping("/dlist/remove")
    public Map<String, Object> dlistRemove(@RequestParam int index) {
        DList<String> list = factory.createDList("demo-dlist");
        String removed = list.remove(index);
        return Map.of("removed", removed != null);
    }

    // -------------------------------------------------------------------------
// DBlockingQueue
// -------------------------------------------------------------------------
    @PostMapping("/dblockingqueue/produce")
    public Map<String, Object> dblockingQueueProduce(@RequestParam String value) throws InterruptedException {
        DBlockingQueue<String> queue = factory.createDBlockingQueue("demo-dbq", 100);
        queue.put(value);
        return Map.of("produced", value);
    }

    @PostMapping("/dblockingqueue/consume")
    public Map<String, Object> dblockingQueueConsume() throws InterruptedException {
        DBlockingQueue<String> queue = factory.createDBlockingQueue("demo-dbq", 100);
        String consumed = queue.take();
        return Map.of("consumed", consumed);
    }

    // -------------------------------------------------------------------------
    // DCircularQueue
    // -------------------------------------------------------------------------
    @PostMapping("/dcircularqueue/add")
    public Map<String, Object> dcircularQueueAdd(@RequestParam String value) {
        return Map.of("added", factory.createDCircularQueue("demo-dcq", 3).add(value));
    }

    // -------------------------------------------------------------------------
    // DAtomics
    // -------------------------------------------------------------------------
    @PostMapping("/datomiclong/increment")
    public Map<String, Object> datomicLongIncrement() {
        return Map.of("value", factory.createDAtomicLong("demo-dal", 0L).addAndGet(1L));
    }

    @PostMapping("/datomiclong/decrement")
    public Map<String, Object> datomicLongDecrement() {
        return Map.of("value", factory.createDAtomicLong("demo-dal", 0L).addAndGet(-1L));
    }

    @PostMapping("/datomicreference/set")
    public Map<String, Object> datomicReferenceSet(@RequestParam String value) {
        factory.createDAtomicReference("demo-dar", "{\"status\": \"IDLE\"}").set(value);
        return Map.of("set", value);
    }

    @PostMapping("/dcounter/add")
    public Map<String, Object> dcounterAdd(@RequestParam long amount) {
        long value = factory.createDCounter("demo-dcounter", 0).increment(amount);
        return Map.of("value", value);
    }

    // -------------------------------------------------------------------------
    // Concurrency
    // -------------------------------------------------------------------------
    @PostMapping("/dsemaphore/acquire")
    public Map<String, Object> dsemaphoreAcquire() throws InterruptedException {
        factory.createDSemaphore("demo-dsemaphore", 3).acquire();
        return Map.of("acquired", true);
    }

    @PostMapping("/dsemaphore/release")
    public Map<String, Object> dsemaphoreRelease() {
        factory.createDSemaphore("demo-dsemaphore", 3).release();
        return Map.of("released", true);
    }

    @PostMapping("/dcountdownlatch/countdown")
    public Map<String, Object> dcountDownLatchCountdown() {
        long remaining = factory.createDCountDownLatch("demo-dcdl", 3).countDown();
        return Map.of("countedDown", true, "remaining", remaining);
    }

    @PostMapping("/dcountdownlatch/await")
    public Map<String, Object> dcountDownLatchAwait() throws InterruptedException {
        factory.createDCountDownLatch("demo-dcdl", 3).await();
        return Map.of("completed", true);
    }

    @PostMapping("/dcountdownlatch/reset")
    public Map<String, Object> dcountDownLatchReset() {
        try {
            factory.createDCountDownLatch("demo-dcdl", 3).destroy();
        } catch (Exception ignored) {}
        factory.createDCountDownLatch("demo-dcdl", 3);
        return Map.of("reset", true, "count", 3);
    }

    // -------------------------------------------------------------------------
    // DSnowflake — Now with Distributed History!
    // -------------------------------------------------------------------------
    @PostMapping("/dsnowflake/generate")
    public Map<String, Object> dsnowflakeGenerate() {
        long id = snowflake.nextId();
        String idStr = String.valueOf(id);

        DList<String> dlist = factory.createDList("demo-dlist");
        dlist.add(0, idStr);
        if (dlist.size() > 10) dlist.remove(10);

        return Map.of("id", idStr);
    }
}