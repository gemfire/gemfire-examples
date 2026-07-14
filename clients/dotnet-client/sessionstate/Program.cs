// Copyright 2026 Broadcom. All Rights Reserved.

// This example shows how to back ASP.NET Core session state with VMware Tanzu
// GemFire. GemFire.Client.Session provides GemFireSessionStateCache, an
// IDistributedCache implementation that stores each session in a GemFire region.
// AddGemFireSessionStateCache wires it up; the standard AddSession/UseSession
// middleware then transparently reads and writes sessions through GemFire.

using System.Net;
using GemFire.Client.Session;

var builder = WebApplication.CreateBuilder(args);

// Register the standard ASP.NET Core session middleware.
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(20);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
});

// Register GemFire as the IDistributedCache that backs the session store.
// Locators and Region are required; the default CacheFactory connects with no
// SSL or authentication, matching the cluster started by the example scripts.
builder.Services.AddGemFireSessionStateCache(options =>
{
    options.Locators = new List<DnsEndPoint> { new("localhost", 10334) };
    options.Region = "exampleSessionState";
});

var app = builder.Build();

app.UseSession();

// Per-session counter: increments on each request that shares the session cookie,
// demonstrating that state survives across requests because it lives in GemFire.
app.MapGet("/", (HttpContext context) =>
{
    var count = context.Session.GetInt32("count") ?? 0;
    count++;
    context.Session.SetInt32("count", count);
    return Results.Text(count.ToString());
});

// Store a string value under an arbitrary key in the session.
app.MapPost("/session/{key}", async (HttpContext context, string key) =>
{
    using var reader = new StreamReader(context.Request.Body);
    var value = await reader.ReadToEndAsync();
    context.Session.SetString(key, value);
    return Results.Ok();
});

// Read a previously stored value back out of the session.
app.MapGet("/session/{key}", (HttpContext context, string key) =>
    Results.Text(context.Session.GetString(key) ?? ""));

// Remove a key from the session.
app.MapDelete("/session/{key}", (HttpContext context, string key) =>
{
    context.Session.Remove(key);
    return Results.Ok();
});

app.Run("http://localhost:5050");
