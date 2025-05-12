# Services
A Java implementation/copy/derivative of .NET Core's dependency and service description framework.
Probably terrible. Coded late in the night.

## Key Terms

| Thing                    | Purpose                                                                  |
|:-------------------------|--------------------------------------------------------------------------|
| `IServiceDescriptor<T>`  | Basic interface for all service resolution.                              |
| `IServiceScope`          | A service scope. Holds a bunch of service instances and can create more. |


# Reference Example: C# Singleton

```csharp
IServiceCollection services;
IServiceCollection services = services.AddSingleton<MyService>( /* OPTIONS */ );

IServiceProvider resolvableServices = services.BuildServiceProvider(); // construct once, typically

// resolve
MyService hooray = resolvableServices.GetService<MyService>(); // get the singleton back
```

Singletons in particular can be registered multiple ways, like so: 

| Type                                       | Description                              |
|--------------------------------------------|------------------------------------------|
| `INSTANCE` (MyService)                     | pass instance directly, resolve directly |
| `Func<MyService>`                          | a function that returns MyService        | 
| `Func<in IServiceProvider, out MyService>` | resolver function                        |

Basically all of these in .NET turn into a `ServiceDescriptor` which has the following STUFF in it:
- Service Type (Actual type info)
- Implementation Factory (How to construct given a `ServiceProvider` and known details)
- Service Lifetime (`Scoped`, `Transient`, `Singleton`)

# Note: .NET and C# get to "cheat" here
C# has full type information, Java has **type erasure**. So, we need to get creative when storing our
resolver info with Java since we need that type information during runtime to make sure we
construct everything correctly.

# Difference: Service Scopes


# Difference: C# IDisposable vs Java AutoClosable

Due to these two having a pile of subtle but nonetheless implementation differences, this implementation
opts to ignore .NET's `Transient` lifetime in favor of mapping `Scoped` as close to 1:1 as possible.

This ends up looking like this in practice:

```csharp
void CSharpDisposable() {
    IServiceProvider services; // given

    using var myDisposable = services.GetRequiredService<MyDisposableService>();
    // do stuff w/ myDisposable
    myDisposable.DoThings();
    
    // closed @ logical scope release
}
```

```java
void JavaClosable() {
    IServiceScope scope; // given
    
    try(var myCloseable = scope.service(DEFINITION)) {
        // use myCloseable, Java cleans up after itself
        myCloseable.doThings();
    }
}
```