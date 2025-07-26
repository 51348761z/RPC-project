# RPC Framework

A simple Java RPC framework for learning distributed systems concepts.

## Overview

This project demonstrates core RPC concepts including service registration/discovery, network communication, dynamic proxies, load balancing, and fault tolerance mechanisms.

## Features

- **Service Registration & Discovery**: ZooKeeper-based service management with real-time caching
- **Load Balancing**: Multiple strategies (Consistent Hash, Random, Round Robin)
- **Retry Mechanism**: Configurable retry with service whitelist support
- **Multiple Transport Protocols**: Netty and Socket implementations
- **Custom Serialization**: JSON and Java object serialization support
- **Dynamic Proxy**: Transparent remote method calls

## Version History

### v3.0.0 (Latest)
- Added retry mechanism with Guava Retrying
- Service whitelist support for retry policies
- Enhanced fault tolerance

### v2.0.0
- Implemented load balancing strategies
- Added service caching with ZooKeeper watchers
- Improved client-server communication reliability

### v1.0.0
- Core RPC framework foundation
- Basic client-server communication with Socket and Netty support
- Dynamic proxy implementation for transparent method calls
- Pluggable serialization system (JSON and Java object serialization)
- ZooKeeper-based service registration and discovery
- Custom codec implementation for message encoding/decoding

## Quick Start

### Prerequisites

- Java 8+
- ZooKeeper
- Maven

### Running

1. Start ZooKeeper
2. Run server: `testServer`
3. Run client: `testClient`

## Architecture Components

- **Client Proxy**: Dynamic proxy for transparent RPC calls
- **Service Center**: ZooKeeper-based service discovery with caching
- **Load Balancer**: Multiple algorithms for service selection
- **Retry Handler**: Fault tolerance with configurable retry policies
- **Serialization**: Pluggable JSON/Object serializers

