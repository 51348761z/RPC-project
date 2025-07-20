# RPC-project

This repo is for learning about RPC (Remote Procedure Call) framework.

## Features

- **Service Registration & Discovery:** Uses Apache ZooKeeper to dynamically register and discover RPC services.
- **Netty & Socket Transport:** Supports both Netty-based and plain Socket-based RPC client/server implementations.
- **Unified Factory Pattern:** Easily switch between Netty and Socket transports using the `config.properties` file.
- **Extensible Architecture:** Pluggable client and server factories for future protocol or feature additions.
- **Quality Improvements:** Refactored codebase for readability, maintainability, and added inline comments for clarity.

## Recent Changes

### 2025-07-20

- Integrated Zookeeper-based service registration.
- Updated service provider and client for dynamic service registry.

### 2025-07-19

- Implemented ZooKeeper-based service discovery.
- Refactored client-side RPC logic.

### 2025-07-18

- Added unified factory for client/server selection via config.
- Added Netty-based RPC server/client and improved demo code.

## Contributing

Feel free to open issues or PRs for improvements, bug fixes, or new features!

