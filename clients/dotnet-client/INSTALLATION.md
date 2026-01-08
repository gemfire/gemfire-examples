# VMware Tanzu GemFire .NET Client Installation Guide

Copyright 2026 Broadcom. All Rights Reserved.

This guide covers how to install the VMware Tanzu GemFire .NET Client library in your .NET applications.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Installation Methods](#installation-methods)
3. [NuGet Package Installation](#nuget-package-installation)
4. [Installation from ZIP Archive](#installation-from-zip-archive)
5. [Verifying Installation](#verifying-installation)
6. [Troubleshooting](#troubleshooting)

## Prerequisites

Before installing the VMware Tanzu GemFire .NET Client, ensure you have the following:

### Required

- **.NET 8.0 SDK or later** - The client requires .NET 8.0 runtime
  - Download from: https://dotnet.microsoft.com/en-us/download/dotnet/8.0
  - Verify installation:
    ```bash
    dotnet --version
    ```
    Should output: `8.0.x` or later

- **VMware Tanzu GemFire Server 10.2.0 or later** - The client requires VMware Tanzu GemFire server version 10.2.0 or later

### Supported Operating Systems

- Windows Server 2022
- Ubuntu 22.04
- RHEL 9

### Development Tools (Optional but Recommended)

- **Visual Studio 2022** (Community, Professional, or Enterprise)
- **Visual Studio Code** with C# extension
- **JetBrains Rider** 2023.1 or later

## Installation Methods

The VMware Tanzu GemFire .NET Client can be installed using one of the following methods:

1. **NuGet Package** (Recommended) - Easiest and most common method
2. **ZIP Archive** - For scenarios where NuGet is not available or for manual installation

## NuGet Package Installation

The NuGet package installation is the recommended method as it automatically handles dependencies and version management.

### Package Information

The VMware Tanzu GemFire .NET Client consists of two NuGet packages:

1. **VMware Tanzu GemFire .NET Client** - Core client library for connecting to VMware Tanzu GemFire
   - **Package ID**: `GemFire.Client`
   - **Package Name**: VMware Tanzu GemFire .NET Client

2. **VMware Tanzu GemFire .NET Client Session** - Session state management for ASP.NET Core applications
   - **Package ID**: `GemFire.Client.Session`
   - **Package Name**: VMware Tanzu GemFire .NET Client Session
   - **Dependencies**: Requires `GemFire.Client` to be installed first

Both packages:
- **Target Framework**: .NET 8.0
- **NuGet Feed**: Configure your NuGet source as provided by your VMware Tanzu GemFire distribution

**Note**: For most applications, you'll want to install both packages. The session package is optional if you're not using ASP.NET Core session state management.

### Method 1: Using Visual Studio

#### Step 1: Open Your Project

1. Open your .NET project in Visual Studio 2022
2. Ensure your project targets .NET 8.0

#### Step 2: Configure NuGet Package Source (if needed)

If using a private NuGet feed:

1. Go to `Tools` → `NuGet Package Manager` → `Package Manager Settings`
2. Click `Package Sources` in the left panel
3. Click the `+` button to add a new source
4. Enter:
   - **Name**: `GemFire Client` (or your preferred name)
   - **Source**: URL or path to your NuGet feed
5. Click `Update` and `OK`

#### Step 3: Install the Package

1. Right-click on your project in Solution Explorer
2. Select `Manage NuGet Packages...`
3. In the NuGet Package Manager window:
   - Select the `Browse` tab
   - If using a custom source, select it from the `Package source` dropdown
   - Search for `GemFire.Client`
   - Select the package and choose the desired version
   - Click `Install`
4. Review the changes in the `Preview` window and click `OK`

#### Step 4: Install Session Package (Optional)

If you need ASP.NET Core session state management:

1. In the NuGet Package Manager, search for `GemFire.Client.Session`
2. Select the package and click `Install`

#### Step 5: Verify Installation

Check that the package references were added to your `.csproj` file:

```xml
<ItemGroup>
  <PackageReference Include="GemFire.Client" Version="1.0.0" />
  <PackageReference Include="GemFire.Client.Session" Version="1.0.0" />
</ItemGroup>
```

**Note**: Only `GemFire.Client` is required. Add `GemFire.Client.Session` only if you need ASP.NET Core session state management.

### Method 2: Using .NET CLI

#### Step 1: Navigate to Your Project Directory

```bash
cd /path/to/your/project
```

#### Step 2: Configure NuGet Source (if needed)

If using a private NuGet feed, add it to your NuGet configuration:

```bash
dotnet nuget add source <feed-url> --name "GemFire Client"
```

Or for a local directory:

```bash
dotnet nuget add source /path/to/local/packages --name "GemFire Client"
```

#### Step 3: Install the Packages

Install the core client package:

```bash
dotnet add package GemFire.Client
```

To install a specific version:

```bash
dotnet add package GemFire.Client --version 1.0.0
```

If you need ASP.NET Core session state management, also install the session package:

```bash
dotnet add package GemFire.Client.Session
```

Or with a specific version:

```bash
dotnet add package GemFire.Client.Session --version 1.0.0
```

#### Step 4: Verify Installation

Check your project file (`.csproj`) to confirm the packages were added:

```bash
cat YourProject.csproj
```

You should see:

```xml
<ItemGroup>
  <PackageReference Include="GemFire.Client" Version="1.0.0" />
  <PackageReference Include="GemFire.Client.Session" Version="1.0.0" />
</ItemGroup>
```

**Note**: Only `GemFire.Client` is required. Add `GemFire.Client.Session` only if you need ASP.NET Core session state management.

### Method 3: Using Package Manager Console

#### Step 1: Open Package Manager Console

In Visual Studio, go to `Tools` → `NuGet Package Manager` → `Package Manager Console`

#### Step 2: Install the Packages

Install the core client package:

```powershell
Install-Package GemFire.Client
```

To install a specific version:

```powershell
Install-Package GemFire.Client -Version 1.0.0
```

If you need ASP.NET Core session state management, also install the session package:

```powershell
Install-Package GemFire.Client.Session
```

Or with a specific version:

```powershell
Install-Package GemFire.Client.Session -Version 1.0.0
```

### Method 4: Manual .csproj Edit

You can manually add the package references to your project file:

1. Open your `.csproj` file in a text editor
2. Add the following within the `<Project>` element:

```xml
<ItemGroup>
  <PackageReference Include="GemFire.Client" Version="1.0.0" />
  <PackageReference Include="GemFire.Client.Session" Version="1.0.0" />
</ItemGroup>
```

**Note**: Only `GemFire.Client` is required. Add `GemFire.Client.Session` only if you need ASP.NET Core session state management.

3. Save the file
4. Restore packages:

```bash
dotnet restore
```

### Restore Dependencies

After installation, restore all NuGet packages:

```bash
dotnet restore
```

Or in Visual Studio, right-click the solution and select `Restore NuGet Packages`.

## Installation from ZIP Archive

If you have received the VMware Tanzu GemFire .NET Client as a ZIP archive, follow these steps to install it manually.

### Step 1: Extract the Archive

Extract the ZIP archive to a location of your choice:

**Windows:**
```powershell
Expand-Archive -Path gemfire.client.zip -DestinationPath "C:/libraries/gemfire.client"
```

**Linux/macOS:**
```bash
unzip gemfire.client.zip -d ~/libraries/gemfire.client
```

### Step 2: Understand the Archive Structure

The extracted archive should contain both client DLLs:

```
gemfire.client/
├── lib/
│   └── net8.0/
│       ├── GemFire.Client.dll
│       └── GemFire.Client.Session.dll
│       └── GemFire.Client.deps.json
│       └── GemFire.Client.Session.deps.json
│
├── GemFire.Client.1.0.0.nupkg
├── GemFire.Client.Session.1.0.0.nupkg
└── [other package files]
```

**Note**: The ZIP archive includes both `GemFire.Client.dll` (core client) and `GemFire.Client.Session.dll` (session state management). You can reference one or both depending on your needs.

### Step 3: Add References to Your Project

#### Option A: Using Project File (Recommended)

1. Open your `.csproj` file
2. Add references to both DLLs:

```xml
<ItemGroup>
  <Reference Include="GemFire.Client">
    <HintPath>path/to/gemfire.client/lib/net8.0/GemFire.Client.dll</HintPath>
  </Reference>
  <Reference Include="GemFire.Client.Session">
    <HintPath>path/to/gemfire.client/lib/net8.0/GemFire.Client.Session.dll</HintPath>
  </Reference>
</ItemGroup>
```

Or use relative paths:

```xml
<ItemGroup>
  <Reference Include="GemFire.Client">
    <HintPath>../libraries/gemfire.client/lib/net8.0/GemFire.Client.dll</HintPath>
  </Reference>
  <Reference Include="GemFire.Client.Session">
    <HintPath>../libraries/gemfire.client/lib/net8.0/GemFire.Client.Session.dll</HintPath>
  </Reference>
</ItemGroup>
```

**Note**: Only `GemFire.Client.dll` is required. Add `GemFire.Client.Session.dll` only if you need ASP.NET Core session state management.

#### Option B: Using Visual Studio

1. Right-click on your project in Solution Explorer
2. Select `Add` → `Reference...`
3. Click `Browse...`
4. Navigate to the extracted archive location: `gemfire.client/lib/net8.0/`
5. Select both `GemFire.Client.dll` and `GemFire.Client.Session.dll` (or just `GemFire.Client.dll` if you don't need session management)
6. Click `Add` and `OK`

#### Option C: Using .NET CLI

Add the core client:

```bash
dotnet add reference path/to/gemfire.client/lib/net8.0/GemFire.Client.dll
```

If you need session management, also add:

```bash
dotnet add reference path/to/gemfire.client/lib/net8.0/GemFire.Client.Session.dll
```

### Step 4: Install Dependencies

The VMware Tanzu GemFire .NET Client packages have the following NuGet dependencies that must be installed:

**For GemFire.Client:**
```xml
<ItemGroup>
  <PackageReference Include="Nito.AsyncEx.Context" Version="5.1.2" />
  <PackageReference Include="Serilog" Version="4.0.1" />
  <PackageReference Include="DotNetty.Buffers" Version="0.7.6" />
  <PackageReference Include="DotNetty.Codecs" Version="0.7.6" />
  <PackageReference Include="DotNetty.Common" Version="0.7.6" />
  <PackageReference Include="DotNetty.Transport" Version="0.7.6" />
  <PackageReference Include="DotNetty.Handlers" Version="0.7.6" />
  <PackageReference Include="Serilog.Enrichers.Thread" Version="4.0.0" />
  <PackageReference Include="Serilog.Sinks.Console" Version="6.0.0" />
  <PackageReference Include="Serilog.Sinks.File" Version="5.0.0" />
  <PackageReference Include="System.Diagnostics.DiagnosticSource" Version="8.0.1" />
</ItemGroup>
```

**For GemFire.Client.Session (if installed):**
```xml
<ItemGroup>
  <PackageReference Include="Microsoft.Extensions.Caching.Abstractions" Version="8.0.0" />
  <PackageReference Include="Microsoft.Extensions.DependencyInjection.Abstractions" Version="8.0.0" />
  <PackageReference Include="Microsoft.Extensions.Options" Version="8.0.2" />
</ItemGroup>
```

Install these dependencies using one of the NuGet installation methods above, or add them manually to your `.csproj` file.

### Step 5: Verify Installation

Build your project to ensure all references are resolved:

```bash
dotnet build
```

## Verifying Installation

### Method 1: Check Project File

Verify the package or reference is listed in your `.csproj` file:

**For NuGet installation:**
```xml
<ItemGroup>
  <PackageReference Include="GemFire.Client" Version="1.0.0" />
  <PackageReference Include="GemFire.Client.Session" Version="1.0.0" />
</ItemGroup>
```

**For ZIP installation:**
```xml
<ItemGroup>
  <Reference Include="GemFire.Client">
    <HintPath>path/to/GemFire.Client.dll</HintPath>
  </Reference>
  <Reference Include="GemFire.Client.Session">
    <HintPath>path/to/GemFire.Client.Session.dll</HintPath>
  </Reference>
</ItemGroup>
```

**Note**: Only `GemFire.Client` is required. Add `GemFire.Client.Session` only if you need ASP.NET Core session state management.

### Method 2: Build the Project

Build your project to verify all dependencies are resolved:

```bash
dotnet build
```

If the build succeeds, the installation is correct.

### Method 3: Test with Code

Create a simple test to verify the client is accessible:

```csharp
using GemFire.Client;

class Program
{
    static void Main()
    {
        // This will compile if the package is correctly installed
        var factory = new CacheFactory();
        Console.WriteLine("VMware Tanzu GemFire .NET Client is installed correctly!");
    }
}
```

Build and run:

```bash
dotnet build
dotnet run
```

### Method 4: List Installed Packages

Check installed packages:

```bash
dotnet list package
```

You should see `GemFire.Client` (and optionally `GemFire.Client.Session`) in the list.

## Troubleshooting

### Issue: Package Not Found

**Symptoms:**

- Error: `NU1101: Unable to find package GemFire.Client`
- Package source not found

**Solutions:**

1. Verify your NuGet package source is configured correctly
2. Check that you have access to the NuGet feed
3. For private feeds, ensure authentication is set up
4. Try clearing NuGet cache:
   ```bash
   dotnet nuget locals all --clear
   ```

### Issue: Version Conflict

**Symptoms:**

- Error: `NU1107: Version conflict detected`
- Dependency resolution errors

**Solutions:**

1. Update to the latest version of the package
2. Check for conflicting package versions
3. Use `dotnet restore --force` to force restore

### Issue: Missing Dependencies

**Symptoms:**

- Build errors about missing types
- Runtime errors about missing assemblies

**Solutions:**

1. Ensure all dependencies are installed (see Step 4 in ZIP installation)
2. Run `dotnet restore`
3. Clean and rebuild:
   ```bash
   dotnet clean
   dotnet restore
   dotnet build
   ```

### Issue: Wrong Target Framework

**Symptoms:**

- Error: `The project does not reference any .NET framework`
- Package requires .NET 8.0

**Solutions:**

1. Update your project to target .NET 8.0:
   ```xml
   <PropertyGroup>
     <TargetFramework>net8.0</TargetFramework>
   </PropertyGroup>
   ```
2. Verify .NET 8.0 SDK is installed:
   ```bash
   dotnet --version
   ```

### Issue: DLL Not Found (ZIP Installation)

**Symptoms:**

- Runtime error: `Could not load file or assembly 'GemFire.Client'`
- Build succeeds but runtime fails

**Solutions:**

1. Verify the HintPath in your `.csproj` is correct
2. Ensure the DLL exists at the specified path
3. Use absolute paths or ensure relative paths are correct
4. Copy the DLL to your output directory if needed:
   ```xml
   <ItemGroup>
    <Reference Include="GemFire.Client">
      <HintPath>path/to/GemFire.Client.dll</HintPath>
      <Private>True</Private>
    </Reference>
   </ItemGroup>
   ```

### Issue: Platform-Specific Errors

**Symptoms:**

- Errors on unsupported operating systems
- Architecture mismatch errors

**Solutions:**

1. Verify your operating system is supported (Windows Server 2022, Ubuntu 22.04, or RHEL 9)
2. Ensure you're running on x64 architecture

## Next Steps

After successful installation:

1. Review the examples in this repository to see the client in action
2. Check the main documentation for API usage
3. Explore the Quick Start Guide for basic usage

## Additional Resources

- [Examples README](README.md) - Overview of available examples
- [GitHub Repository](https://github.com/gemfire-examples/dotnet-client) - Source code and issue tracking

---

**Last Updated**: This guide was last updated for VMware Tanzu GemFire .NET Client version 1.0.0.

