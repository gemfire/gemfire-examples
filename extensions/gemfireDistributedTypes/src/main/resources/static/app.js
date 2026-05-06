document.addEventListener('alpine:init', () => {
    Alpine.data('gemfireLab', function() {
        return {
            activeTab: 'dset',
            showCode: true,
            simLatency: false,
            isProcessing: { A: false, B: false },
            BASE_A: 'http://localhost:8080',
            BASE_B: 'http://localhost:8081',

            // Independent per-backend state buckets
            stateA: { dset: [], dlist: [], dqueue: [], dcirc: [], dcounter: 0, datomiclong: 0, datomicreference: '{"status": "IDLE"}', latchCount: 3 },
            stateB: { dset: [], dlist: [], dqueue: [], dcirc: [], dcounter: 0, datomiclong: 0, datomicreference: '{"status": "IDLE"}', latchCount: 3 },
            connected: { A: true, B: true },

            tabs: [
                {
                    id: 'dset',
                    category: 'Collections',
                    name: 'DSet',
                    desc: `A distributed implementation of Java's Set interface that contains no duplicate elements.`,
                    useCase: 'Tracking active user sessions, maintaining a list of unique visitors, or enforcing exactly-once processing tags.',
                    demo: 'Try adding the exact same string from Client A and Client B; duplicates are rejected globally.'
                },
                {
                    id: 'dlist',
                    category: 'Collections',
                    name: 'DList',
                    desc: 'An ordered distributed collection (sequence) that permits duplicate elements.',
                    useCase: 'Maintaining an ordered audit log, chat histories, or a sequence of events across the cluster.',
                    demo: 'Add items on Client A, observe them instantly on Client B. Try adding identical items to see duplicates allowed.'
                },
                {
                    id: 'dblockingqueue',
                    category: 'Collections',
                    name: 'DBlockingQueue',
                    desc: 'A distributed queue where take() operations block until an item is available.',
                    useCase: 'Producer-consumer patterns, task distribution, or work-stealing algorithms across multiple JVMs.',
                    demo: 'Click "Consume" on B while empty. B will block. Then click "Produce" on A; B will instantly receive the job and unblock.'
                },
                {
                    id: 'dcircularqueue',
                    category: 'Collections',
                    name: 'DCircularQueue',
                    desc: 'A fixed-size distributed FIFO queue that automatically evicts the oldest elements when capacity is reached.',
                    useCase: 'Storing recent log events, limited history buffers, or maintaining a "last N actions" list.',
                    demo: 'Push events into the queue (Max Capacity: 3). Notice how Item-1 is evicted globally when Item-4 arrives.'
                },
                {
                    id: 'datomiclong',
                    category: 'Atomics & Counters',
                    name: 'DAtomicLong',
                    desc: 'A 64-bit integer value that may be updated atomically across the entire cluster.',
                    useCase: 'Generating strict global sequence numbers, counting global hits, or implementing strict distributed rate limiting.',
                    demo: 'Increment on Client A, decrement on Client B. Notice how the state remains perfectly synchronized.'
                },
                {
                    id: 'datomicreference',
                    category: 'Atomics & Counters',
                    name: 'DAtomicReference',
                    desc: 'An object reference that may be updated atomically across the cluster.',
                    useCase: 'Updating shared configuration objects, state machine transitions, or feature flag toggles across nodes.',
                    demo: 'Click a state preset button on one client, instantaneously reflecting the new JSON state on the other client.'
                },
                {
                    id: 'dcounter',
                    category: 'Atomics & Counters',
                    name: 'DCounter',
                    desc: 'A highly optimized, relaxed-consistency distributed counter designed for extreme throughput.',
                    useCase: `High-frequency metric tracking, page views, or distributed telemetry where strict immediate ordering isn't required.`,
                    demo: 'Spam the +1 and +10 buttons. Notice how bulk operations update the global total rapidly.'
                },
                {
                    id: 'dsemaphore',
                    category: 'Concurrency',
                    name: 'DSemaphore',
                    desc: 'A distributed counting semaphore that maintains a cluster-wide set of permits.',
                    useCase: 'Throttling access to external APIs, limiting concurrent database connections, or resource pooling.',
                    demo: 'The cluster has 3 total permits. Have Client A acquire 2, and Client B acquire 1. Subsequent requests will block.'
                },
                {
                    id: 'dcountdownlatch',
                    category: 'Concurrency',
                    name: 'DCountDownLatch',
                    desc: 'A synchronization aid that allows one or more JVMs to wait until a set of operations completes.',
                    useCase: 'Coordinating cluster startup phases, or waiting for multiple worker nodes to finish a parallel job before proceeding.',
                    demo: 'Client B is automatically blocking on await(). Click "Count Down" on Client A 3 times to open the latch and unblock Client B.'
                },
                {
                    id: 'dsnowflake',
                    category: 'Identity',
                    name: 'DSnowflake',
                    desc: 'A decentralized, lock-free ID generator based on the Snowflake algorithm (Timestamp + Worker ID + Sequence).',
                    useCase: 'Generating highly scalable, time-sortable unique identifiers for database primary keys or massive event streams.',
                    demo: 'Generate IDs from both clients. Notice how the Global Database View automatically reflects the shared history.'
                }
            ],

            logs: [],

            init() {
                this.logSystem("Cluster initialized. Monitoring dual backends.");
                this.syncState('A');
                this.syncState('B');
                setInterval(() => this.syncState('A'), 2000);
                setInterval(() => this.syncState('B'), 2000);

                this.$watch('logs', () => {
                    this.$nextTick(() => {
                        const box = this.$refs.consoleBox;
                        if (box) box.scrollTop = box.scrollHeight;
                    });
                });
            },

            async syncState(client) {
                const base = client === 'A' ? this.BASE_A : this.BASE_B;
                try {
                    const res = await fetch(`${base}/api/sync`);
                    if (!res.ok) throw new Error();
                    const data = await res.json();
                    const target = client === 'A' ? this.stateA : this.stateB;
                    Object.assign(target, data);
                    this.connected[client] = true;
                } catch (e) {
                    this.connected[client] = false;
                }
            },

            async executeApi(client, actionLabel, path) {
                this.isProcessing[client] = true;
                const base = client === 'A' ? this.BASE_A : this.BASE_B;
                try {
                    const response = await fetch(`${base}${path}`, { method: 'POST' });
                    const data = await response.json();
                    this.isProcessing[client] = false;
                    return { success: true, data };
                } catch (error) {
                    this.isProcessing[client] = false;
                    this.logAction(client, `API Error: Could not reach backend`, 'text-red-500');
                    return { success: false, data: null };
                }
            },

            // Logger Utilities
            logSystem(msg) { this.pushLog('SYSTEM', msg, 'text-slate-400'); },
            logAction(client, msg, colorOverride) {
                const color = colorOverride ? colorOverride : (client === 'A' ? 'text-blue-400' : 'text-purple-400');
                this.pushLog(`Client ${client}`, msg, color);
            },
            pushLog(source, msg, colorClass) {
                const time = new Date().toLocaleTimeString([], { hour12: false, hour: '2-digit', minute:'2-digit', second:'2-digit', fractionalSecondDigits: 3 });
                this.logs.push({ time, source, msg, colorClass });
            },
            clearConsole() { this.logs = []; },

            triggerUpdateAnim(refName) {
                const els = document.querySelectorAll(`.${refName}`);
                els.forEach(el => {
                    el.classList.remove('animate-update');
                    void el.offsetWidth;
                    el.classList.add('animate-update');
                });
            },

            // --- 1. DSet Actions ---
            inputSetA: '',
            inputSetB: '',
            async addToSet(client) {
                const val = (client === 'A' ? this.inputSetA : this.inputSetB).trim();
                if (!val) return;

                const result = await this.executeApi(client, `dSet.add("${val}")`, `/api/dset/add?value=${encodeURIComponent(val)}`);
                if (result.success) {
                    if (result.data.added) {
                        this.logAction(client, `dSet.add("${val}") -> true`);
                    } else {
                        this.logAction(client, `dSet.add("${val}") -> false (Duplicate)`, 'text-red-400');
                    }
                    client === 'A' ? this.inputSetA = '' : this.inputSetB = '';
                }
            },
            async removeFromSet(client, item) {
                const result = await this.executeApi(client, `dSet.remove("${item}")`, `/api/dset/remove?value=${encodeURIComponent(item)}`);
                if (result.success) {
                    this.logAction(client, `dSet.remove("${item}")`);
                }
            },

            // --- 2. DList Actions ---
            inputListA: '',
            inputListB: '',
            async addToList(client) {
                const val = (client === 'A' ? this.inputListA : this.inputListB).trim();
                if (!val) return;

                const result = await this.executeApi(client, `dList.add("${val}")`, `/api/dlist/add?value=${encodeURIComponent(val)}`);
                if (result.success) {
                    this.logAction(client, `dList.add("${val}")`);
                    client === 'A' ? this.inputListA = '' : this.inputListB = '';
                }
            },
            async removeFromList(client, index) {
                const s = client === 'A' ? this.stateA : this.stateB;
                const val = s.dlist[index];
                const result = await this.executeApi(client, `dList.remove(index: ${index})`, `/api/dlist/remove?index=${index}`);
                if (result.success) {
                    this.logAction(client, `dList.remove("${val}")`);
                }
            },

            // --- 3. DBlockingQueue Actions ---
            isAwaitingQueue: { A: false, B: false },
            async produce(client) {
                const item = `Job-${Math.floor(Math.random() * 900) + 100}`;
                const result = await this.executeApi(client, `dBlockingQueue.put("${item}")`,
                    `/api/dblockingqueue/produce?value=${encodeURIComponent(item)}`);
                if (result.success) {
                    this.logAction(client, `dBlockingQueue.put("${item}") -> produced`);
                }
            },
            async consume(client) {
                const base = client === 'A' ? this.BASE_A : this.BASE_B;
                this.isProcessing[client] = true;
                this.logAction(client, `dBlockingQueue.take() -> BLOCKING...`, 'text-yellow-500');
                try {
                    const res = await fetch(`${base}/api/dblockingqueue/consume`, { method: 'POST' });
                    const data = await res.json();
                    this.isProcessing[client] = false;
                    this.logAction(client, `dBlockingQueue.take() -> "${data.consumed}"`);
                } catch (e) {
                    this.isProcessing[client] = false;
                    this.logAction(client, `API Error: consume failed`, 'text-red-500');
                }
            },

            // --- 4. DCircularQueue Actions ---
            circQueueCounter: 1,
            async enqueueCQ(client) {
                const item = `Item-${this.circQueueCounter++}`;
                const result = await this.executeApi(client, `dCircularQueue.add("${item}")`, `/api/dcircularqueue/add?value=${encodeURIComponent(item)}`);

                if (result.success) {
                    this.logAction(client, `dCircularQueue.add("${item}")`);
                    this.triggerUpdateAnim('anim-circ');
                }
            },

            // --- 5. DAtomicLong Actions ---
            async increment(client) {
                const result = await this.executeApi(client, `dAtomicLong.incrementAndGet()`, `/api/datomiclong/increment`);
                if (result.success) {
                    const s = client === 'A' ? this.stateA : this.stateB;
                    s.datomiclong = result.data.value;
                    this.logAction(client, `dAtomicLong.addAndGet(1) -> ${s.datomiclong}`);
                    this.triggerUpdateAnim('anim-atomic');
                }
            },
            async decrement(client) {
                const result = await this.executeApi(client, `dAtomicLong.decrementAndGet()`, `/api/datomiclong/decrement`);
                if (result.success) {
                    const s = client === 'A' ? this.stateA : this.stateB;
                    s.datomiclong = result.data.value;
                    this.logAction(client, `dAtomicLong.addAndGet(-1) -> ${s.datomiclong}`);
                    this.triggerUpdateAnim('anim-atomic');
                }
            },

            // --- 6. DAtomicReference Actions ---
            async updateRef(client, val) {
                const result = await this.executeApi(client, `dAtomicReference.set()`, `/api/datomicreference/set?value=${encodeURIComponent(val)}`);
                if (result.success) {
                    const s = client === 'A' ? this.stateA : this.stateB;
                    s.datomicreference = result.data.set;
                    this.logAction(client, `dAtomicReference.set('${result.data.set}')`);
                    this.triggerUpdateAnim('anim-ref');
                }
            },

            // --- 7. DCounter Actions ---
            async addCounter(client, amount) {
                const result = await this.executeApi(client, `dCounter.addAndGet(${amount})`, `/api/dcounter/add?amount=${amount}`);
                if (result.success) {
                    const s = client === 'A' ? this.stateA : this.stateB;
                    s.dcounter = result.data.value;
                    this.logAction(client, `dCounter.increment(${amount}) -> ${s.dcounter}`);
                    this.triggerUpdateAnim('anim-dcounter');
                }
            },

            // --- 8. DSemaphore Actions ---
            totalPermits: 3,
            clientPermits: { A: 0, B: 0 },
            async acquireSemaphore(client) {
                const result = await this.executeApi(client, `dSemaphore.acquire()`, `/api/dsemaphore/acquire`);
                if (result.success) {
                    this.clientPermits[client]++;
                    this.logAction(client, `dSemaphore.acquire() -> GRANTED`);
                }
            },
            async releaseSemaphore(client) {
                if (this.clientPermits[client] <= 0) return;
                const result = await this.executeApi(client, `dSemaphore.release()`, `/api/dsemaphore/release`);
                if (result.success) {
                    this.clientPermits[client]--;
                    this.logAction(client, `dSemaphore.release() -> RELEASED`);
                }
            },

            // --- 9. DCountDownLatch Actions ---
            isAwaitingLatch: { A: false, B: false },
            async countDown(client) {
                const result = await this.executeApi(client, `dCountDownLatch.countDown()`, `/api/dcountdownlatch/countdown`);
                if (result.success) {
                    const s = client === 'A' ? this.stateA : this.stateB;
                    s.latchCount = result.data.remaining;
                    this.logAction(client, `dCountDownLatch.countDown() -> ${result.data.remaining} remaining`);
                    if (result.data.remaining === 0) {
                        this.logSystem('LATCH OPENED! All awaiting threads unblocked.');
                    }
                }
            },
            async awaitLatch(client) {
                const base = client === 'A' ? this.BASE_A : this.BASE_B;
                this.isProcessing[client] = true;
                this.isAwaitingLatch[client] = true;
                this.logAction(client, `dCountDownLatch.await() -> Thread Blocked`, 'text-yellow-500');
                try {
                    const res = await fetch(`${base}/api/dcountdownlatch/await`, { method: 'POST' });
                    const data = await res.json();
                    this.isProcessing[client] = false;
                    this.isAwaitingLatch[client] = false;
                    this.logAction(client, `dCountDownLatch.await() -> Unblocked`, 'text-green-400');
                } catch (e) {
                    this.isProcessing[client] = false;
                    this.isAwaitingLatch[client] = false;
                    this.logAction(client, `await() interrupted`, 'text-orange-400');
                }
            },
            async resetLatch() {
                try {
                    const res = await fetch(`${this.BASE_A}/api/dcountdownlatch/reset`, { method: 'POST' });
                    const data = await res.json();
                    this.stateA.latchCount = data.count;
                    this.stateB.latchCount = data.count;
                    this.isAwaitingLatch = { A: false, B: false };
                    this.logSystem(`Latch destroyed and recreated with count ${data.count}`);
                } catch (e) {
                    this.logSystem('Latch reset failed');
                }
            },

            // --- 10. DSnowflake Actions ---
            async generateSnowflake(client) {
                const result = await this.executeApi(client, `dSnowflake.nextId()`, `/api/dsnowflake/generate`);
                if (result.success) {
                    this.logAction(client, `dSnowflake.nextId() -> ${result.data.id}`);
                }
            },
            get sortedSnowflakeIds() {
                // Returns newest 5 IDs from the shared history list
                return this.stateA.dlist.slice(0, 5);
            }
        }
    });
});