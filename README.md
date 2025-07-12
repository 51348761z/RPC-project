# RPC-project

This repository is a Java-based learning project for building a simple Remote Procedure Call (RPC) framework.

## Overview

RPC-project demonstrates the core concepts behind RPC communication by allowing a client to invoke methods on a server as if they were local. The framework includes both a basic thread-per-request server and an optimized thread-pool based server, a client-side proxy for seamless method invocation, and simple message structures for requests and responses.

## Features

- **Custom RPC Protocol:** Serialize/deserialize requests and responses using Java object streams.
- **Service Registration:** Server registers service implementations that can be invoked remotely.
- **Client Proxy:** Clients use dynamic proxy to invoke remote methods transparently.
- **Threaded Servers:** 
  - `SimpleRPCServer`: Uses one thread per request.
  - `ThreadPoolRPCServer`: Uses a configurable thread pool for request handling.
- **Extensible Interface:** Message classes (`RpcRequest`, `RpcResponse`) are easily extensible for more complex use cases.
- **Easy Testing:** Example client and server included.

## How It Works

1. **Server Side**
    - Implement your service (e.g. `UserServiceImpl`).
    - Register your service interface with the `ServiceProvider`.
    - Start the server (`SimpleRPCServer` or `ThreadPoolRPCServer`) on a port.

2. **Client Side**
    - Use `ClientProxy` to generate a proxy for your service interface.
    - Call methods on the proxy as if it's a local object—the proxy sends an `RpcRequest` to the server and returns an `RpcResponse`.

## Example Usage

### Server Example

```java
UserService userService = new UserServiceImpl();
ServiceProvider serviceProvider = new ServiceProvider();
serviceProvider.provideServiceInterface(userService);

RpcServer rpcServer = new SimpleRPCServer(serviceProvider);
rpcServer.start(9999);
```

### Client Example

```java
ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
UserService proxy = clientProxy.getProxy(UserService.class);

User user = proxy.getUserById(1);
System.out.println("user from server = " + user);

User newUser = User.builder().id(100).username("xxx").sex(true).build();
Integer id = proxy.insertUserId(newUser);
System.out.println("inserted user id to server = " + id);
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── Server/
│   │   │   ├── provider/       # Service registration
│   │   │   └── server/         # Server implementations
│   │   │       └── work/       # Worker threads
│   │   ├── Client/             # Client and proxy logic
│   │   └── common/             # Shared RPC messages and service interfaces
├── test/
│   └── java/
│       ├── TestServer.java     # Sample server setup
│       └── TestClient.java     # Sample client usage
```

## Requirements

- Java 8 or higher

## Getting Started

1. Clone the repository.
2. Implement your service interface and class.
3. Register the service on the server.
4. Start the server and client using the provided examples.

## License

This project is for educational purposes and does not specify a license.

## Author

[51348761z](https://github.com/51348761z)

