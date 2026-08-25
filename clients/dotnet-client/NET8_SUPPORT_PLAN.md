# Plan: Support .NET 8 alongside .NET 10 in the dotnet-client examples

## Status: applied, pending restore verification

All 17 `.csproj` files now target `<TargetFrameworks>net8.0;net10.0</TargetFrameworks>`
and reference `GemFire.Client`/`GemFire.Client.Session` version `1.0.1-1425`
(the version the GemFire.Client team confirmed ships a net8.0 asset).
`README.md` and `INSTALLATION.md` have been updated to describe both target
frameworks, and the `INSTALLATION.md` line 332 inconsistency mentioned below
has been fixed.

**Not yet verified:** `dotnet restore` against version `1.0.1-1425` could not
be confirmed from this environment — the configured `VMwareGemFireArtifactory`
NuGet feed returns `403 Forbidden` for both package restore and package
search here (a credential/environment issue, reproduced before and after
this change, unrelated to the version bump itself). **Before merging, run
`dotnet restore` and `dotnet build -f net8.0` / `-f net10.0` for at least one
project (e.g. `putgetremove`) on a machine with working feed credentials to
confirm `1.0.1-1425` actually resolves and ships both TFM assets.**

## Goal
Make every example under `clients/dotnet-client/` build and run against both
.NET 8 and .NET 10, instead of .NET 10 only.

## Root cause (confirmed, not guessed)
The examples themselves have no .NET 10-specific code — no `#if`/`#elif`
directives anywhere in the tree, no C# 13/14-only syntax (no `field`
keyword, no collection expressions, no primary constructors on non-records).
The blocker is entirely the `GemFire.Client` NuGet package the examples
depend on:

- `GemFire.Client` version `1.0.0-1334` (the version referenced by every
  `.csproj` in this directory) ships only `lib/net10.0/GemFire.Client.dll`.
  Its `.nuspec` declares a single `<group targetFramework="net10.0">`
  dependency group — there is no `net8.0` (or `netstandard`) asset at all.
- Verified empirically: restoring this exact package into a throwaway
  `net8.0` console app fails with:
  ```
  NU1202: Package GemFire.Client 1.0.0-1334 is not compatible with net8.0
  (.NETCoreApp,Version=v8.0). Package GemFire.Client 1.0.0-1334 supports:
  net10.0 (.NETCoreApp,Version=v10.0)
  ```
- This is not a transitive-dependency limitation — `DotNetty.*`, `Serilog`,
  and `Microsoft.Extensions.Configuration` (GemFire.Client's own
  dependencies) all publish net8.0-compatible builds. Nothing forces
  `GemFire.Client` to be net10.0-only except how it was itself built/packed.

**Conclusion:** no change to these example `.csproj` files can add net8.0
support until the `GemFire.Client` NuGet package itself publishes a
net8.0-compatible asset. Multi-targeting the examples today
(`<TargetFrameworks>net8.0;net10.0</TargetFrameworks>`) would just move the
`NU1202` failure into every example's net8.0 build leg.

## Change applied

For all 17 `.csproj` files under `clients/dotnet-client/*/`:

1. Changed
   ```xml
   <TargetFramework>net10.0</TargetFramework>
   ```
   to
   ```xml
   <TargetFrameworks>net8.0;net10.0</TargetFrameworks>
   ```

2. Bumped every `PackageReference Include="gemfire.client"` (and
   `gemfire.client.session` in `sessionstate.csproj`) from `1.0.0-1334` to
   `1.0.1-1425` in the same change as step 1, so nothing was left in a
   broken intermediate state.

3. `LangVersion`: nothing in this tree uses syntax that would fail under the
   .NET 8 SDK's default C# 12 (no `#if`/`#elif`, no C# 13/14-only syntax), so
   no `LangVersion` changes were needed. `classaskey_windows_only.csproj`'s
   existing `<LangVersion>10.0</LangVersion>` is compatible with both SDKs.
   Re-check this if future example code adds newer-only C# syntax.

4. Files affected (all under `clients/dotnet-client/`):
   `authinitialize`, `cachelistener`, `classaskey`,
   `classaskey_windows_only`, `continuousquery`, `dataserializable`,
   `delegatecachelistener`, `functionexecution`, `jsonconfig`,
   `pdxautoserializer`, `pdxserializable`, `prometheus`, `putgetremove`,
   `remotequery`, `sessionstate`, `sslputget`, `transaction`.

   `prometheus.csproj` also references
   `OpenTelemetry.Exporter.Prometheus.HttpListener 1.16.0-beta.1` — confirmed
   this ships both `lib/net8.0` and `lib/net10.0` assets locally, so it's
   safe to multi-target too.

5. Docs updated: `README.md` and `INSTALLATION.md` now say ".NET 8.0 SDK or
   .NET 10.0 SDK" instead of a hard ".NET 10.0" requirement, including the
   prerequisites list, the "Target Framework" package info line, the
   Visual-Studio/CLI reference instructions (with a note to swap `net10.0`
   for `net8.0` in HintPaths/paths as needed), the ZIP archive structure
   diagram (now shows both `lib/net8.0/` and `lib/net10.0/`), and the "Wrong
   Target Framework" troubleshooting section. The pre-existing inconsistency
   at the old line 332 (`gemfire.client/lib/net8.0/` when everything else
   said `net10.0`) is resolved now that both folders legitimately exist.

## Test plan

**Unit (build verification):**
- `dotnet build -f net8.0` and `dotnet build -f net10.0` for every project,
  both Debug and Release configs. All 17 projects, both TFMs, both configs
  must succeed with no new warnings.

**Integration:**
- Run each example's `Program.cs` against a real GemFire cluster once under
  a .NET 8 host and once under a .NET 10 host, for at least one example per
  distinct GemFire.Client feature area (PDX serialization, continuous
  query, functions, transactions, SSL, auth, sessionstate/ASP.NET Core
  Session — since `sessionstate.csproj` uses `Microsoft.NET.Sdk.Web`,
  confirm that SDK's multi-targeting behaves the same way).
- Confirm no behavioral divergence between the net8.0 and net10.0 build of
  the same example (same output, same GemFire operations succeed).

**Acceptance:**
- A consumer with only the .NET 8 SDK installed can clone this repo, build
  any example against `net8.0`, and run it against a GemFire cluster with no
  errors.
- A consumer with only the .NET 10 SDK installed can do the same against
  `net10.0`.
- CI (if/when set up for this repo) builds both TFMs for every project.
