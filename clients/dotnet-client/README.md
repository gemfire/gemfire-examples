# VMware Tanzu GemFire .NET Client Examples

The VMware Tanzu GemFire .NET Client distribution includes examples that demonstrate how the client library can be used. The examples and their source files are located in the [dotnet-client](https://github.com/gemfire-examples/dotnet-client) repository on GitHub.

## Prerequisites

Before running the examples, ensure you have the following components installed:

- **.NET 8.0 SDK** - Download from [Microsoft .NET Downloads](https://dotnet.microsoft.com/download)
- **Java 8 or Java 11 JDK** - Required to run Tanzu GemFire Locator and Server processes
- **VMware Tanzu GemFire** - Download and install from [VMware Tanzu GemFire Downloads](https://support.broadcom.com/group/ecx/productdownloads?subfamily=VMware%20Tanzu%20GemFire)
- **VMware Tanzu GemFire .NET Client** - Install the `GemFire.Client` NuGet package (see [Installation Guide](https://docs.vmware.com/en/VMware-GemFire/index.html) for installation instructions)

## Getting the Examples

Clone the examples repository from GitHub:

```bash
git clone https://github.com/gemfire-examples/dotnet-client.git
cd dotnet-client/examples
```

## Building the Examples

### 1. Set Environment Variables

Set the following environment variables:

**Windows (PowerShell):**
```powershell
$env:GEMFIRE_HOME = "C:\path\to\gemfire"
$env:JAVA_HOME = "C:\path\to\java"
```

**Linux/macOS:**
```bash
export GEMFIRE_HOME=/path/to/gemfire
export JAVA_HOME=/path/to/java
```

### 2. Build Java Utility Classes

The examples use Java classes that must be built and deployed to the Tanzu GemFire servers:

```bash
cd utilities
```

**Windows (PowerShell):**
```powershell
.\buildjarPS.ps1
```

**Linux/macOS:**
```bash
./buildjar.sh
```

This creates `example.jar` which will be deployed to servers by the example startup scripts.

### 3. Build the .NET Examples

From the examples directory:

```bash
cd ..
dotnet build
```

**Note**: On Linux and macOS, you may see build errors for `classaskey_windows_only` as it uses Windows-specific features. This is expected.

## Running the Examples

Each example includes:
- A `README.md` file with specific details and any additional configuration requirements
- Startup scripts (`startlocator_servers.sh` for Linux/macOS, `startserver.ps1` for Windows)
- Stop scripts (`stoplocator_servers.sh` for Linux/macOS, `stopserver.ps1` for Windows)

### General Steps

1. **Navigate to the example directory:**
   ```bash
   cd <example-name>
   ```

2. **Start the Tanzu GemFire cluster:**
   
   **Windows (PowerShell):**
   ```powershell
   .\startserver.ps1
   ```
   
   **Linux/macOS:**
   ```bash
   ./startlocator_servers.sh
   ```
   
   The startup script will:
   - Start a Locator for configuration management and server discovery
   - Deploy `example.jar` to the Locator (contains Java classes for authentication and serialization)
   - Start Servers to store and manage key-value data
   - Create a Tanzu GemFire Region (distributed hashmap for organizing data)

3. **Run the example:**
   ```bash
   dotnet run
   ```

4. **Stop the Tanzu GemFire cluster:**
   
   **Windows (PowerShell):**
   ```powershell
   .\stopserver.ps1
   ```
   
   **Linux/macOS:**
   ```bash
   ./stoplocator_servers.sh
   ```

## Available Examples

### Basics

#### putgetremove

Demonstrates basic cache operations: creating a cache, configuring a pool, creating a region, and performing Put, Get, and Remove operations with primitive data types.

**Location**: `putgetremove/`

**Key Features**:
- Cache creation using `CacheFactory`
- Pool configuration
- Region creation using `RegionFactory`
- Basic Put, Get, and Remove operations

### Serialization

#### dataserializable

Demonstrates how to serialize C# objects using the `IDataSerializable` interface for communication with Tanzu GemFire servers.

**Location**: `dataserializable/`

**Key Features**:
- Implementing `IDataSerializable` interface
- Registering custom types with the type registry
- Serializing and deserializing custom objects

#### classaskey

Demonstrates using custom C# classes as region keys, which requires implementing custom hash code and equality methods.

**Location**: `classaskey/`

**Key Features**:
- Using custom classes as region keys
- Implementing hash code and equality methods
- Object serialization for keys

#### classaskey_windows_only

Similar to `classaskey` but uses Windows-specific features. Only available on Windows platforms.

**Location**: `classaskey_windows_only/`

#### pdxserializable

Demonstrates PDX (Portable Data eXchange) serialization using the `IPdxSerializable` interface. PDX provides language-neutral serialization that works across Java and .NET clients.

**Location**: `pdxserializable/`

**Key Features**:
- Implementing `IPdxSerializable` interface
- PDX type registration
- Cross-language serialization compatibility

#### pdxautoserializer

Demonstrates automatic PDX serialization using reflection-based auto-serialization, which eliminates the need to manually implement serialization methods.

**Location**: `pdxautoserializer/`

**Key Features**:
- Reflection-based auto-serialization
- Automatic PDX type mapping
- Simplified serialization setup

### Security

#### authinitialize

Demonstrates client authentication by creating and registering a custom `IAuthInitialize` handler that authenticates against a Tanzu GemFire server with authentication enabled.

**Location**: `authinitialize/`

**Key Features**:
- Implementing `IAuthInitialize` interface
- Custom authentication credentials
- Server-side authentication integration

#### sslputget

Demonstrates secure communication using SSL/TLS between the client and Tanzu GemFire cluster, including certificate management and SSL configuration.

**Location**: `sslputget/`

**Key Features**:
- SSL/TLS configuration
- Certificate management
- Secure client-server communication

### Events

#### cachelistener

Demonstrates receiving cache events (create, update, destroy) by implementing and registering a cache listener on a region.

**Location**: `cachelistener/`

**Key Features**:
- Implementing `ICacheListener` interface
- Receiving region entry events
- Event handling for cache operations

#### delegatecachelistener

Demonstrates using multiple cache listeners (delegation pattern) to handle different aspects of cache events.

**Location**: `delegatecachelistener/`

**Key Features**:
- Multiple cache listeners
- Delegation pattern
- Event routing to multiple handlers

#### continuousquery

Demonstrates continuous queries (CQ) that automatically receive updates when data matching a query criteria changes in the region.

**Location**: `continuousquery/`

**Key Features**:
- Creating continuous queries
- Implementing `ICqListener` interface
- Real-time data change notifications

### Query

#### remotequery

Demonstrates executing OQL (Object Query Language) queries against Tanzu GemFire regions and processing the returned results.

**Location**: `remotequery/`

**Key Features**:
- OQL query execution
- Query result processing
- Parameterized queries

### Advanced

#### functionexecution

Demonstrates executing Tanzu GemFire functions on the server cluster, including function execution with arguments and result collection.

**Location**: `functionexecution/`

**Key Features**:
- Server-side function execution
- Function arguments and results
- Result collection

#### transaction

Demonstrates transactional operations, including beginning transactions, performing multiple operations atomically, and committing or rolling back transactions.

**Location**: `transaction/`

**Key Features**:
- Transaction management
- Atomic operations
- Commit and rollback

### Configuration

#### jsonconfig

Demonstrates loading configuration from a JSON file and converting it to a dictionary for configuring `CacheFactory` SystemProperties. Also shows cache listener usage.

**Location**: `jsonconfig/`

**Key Features**:
- JSON configuration file loading
- SystemProperties configuration
- Cache listener integration

## Example Structure

Each example follows a similar structure:

1. **Program.cs** - Main application code demonstrating the example functionality
2. **README.md** - Example-specific documentation and instructions
3. **startlocator_servers.sh** / **startserver.ps1** - Scripts to start the Tanzu GemFire cluster
4. **stoplocator_servers.sh** / **stopserver.ps1** - Scripts to stop the Tanzu GemFire cluster
5. **Additional files** - Example-specific classes, listeners, or configuration files

## Additional Resources

- [Quick Start Guide](https://docs.vmware.com/en/VMware-GemFire/index.html) - Learn the basic API usage
- [Installation Guide](https://docs.vmware.com/en/VMware-GemFire/index.html) - Install the VMware Tanzu GemFire .NET Client
- [Migration Guide](https://docs.vmware.com/en/VMware-GemFire/index.html) - Migrate from legacy Apache Geode client
- [Examples GitHub Repository](https://github.com/gemfire-examples/dotnet-client) - Source code and issue tracking

## Getting Help

If you encounter issues while running the examples:

1. Review the example's `README.md` file for specific requirements
2. Check that all prerequisites are installed and configured
3. Verify environment variables (`GEMFIRE_HOME`, `JAVA_HOME`) are set correctly
4. Ensure the Tanzu GemFire cluster is running before executing the example
5. Check the [Compatibility Matrix](https://docs.vmware.com/en/VMware-GemFire/index.html) for version requirements

For more information about Tanzu GemFire clustering and management, see the [Tanzu GemFire User Guide](https://docs.vmware.com/en/VMware-GemFire/index.html).

---

**Note**: Examples are illustrative of GemFire API usage. For clarity on the API usage, the code may not address all warnings or errors as expected in a production client application.
