# RPC Framework

A simple Java RPC framework for learning distributed systems concepts.

## Overview

This project demonstrates core RPC concepts including service registration/discovery, network communication, dynamic proxies, load balancing, fault tolerance mechanisms, and service protection patterns.

## Features

- **Service Registration & Discovery**: ZooKeeper-based service management with real-time caching
- **Load Balancing**: Multiple strategies (Consistent Hash, Random, Round Robin)
- **Retry Mechanism**: Configurable RetryImpl with service whitelist support
- **Circuit Breaker**: Service protection with CLOSED/OPEN/HALF_OPEN states
- **Rate Limiting**: Token bucket algorithm for service request throttling
- **Multiple Transport Protocols**: Netty and Socket implementations
- **Custom Serialization**: JSON and Java object serialization support
- **Dynamic Proxy**: Transparent remote method calls

## Version History

### v4.0.0 (Latest)
- Added circuit breaker pattern for RPC service protection
- Implemented token bucket rate limiting for service throttling
- Enhanced fault tolerance with configurable RetryImpl policies

### v3.0.0
- Added RetryImpl mechanism with Guava Retrying
- Service whitelist support for RetryImpl policies
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
- Custom codec implementation for RpcMessage encoding/decoding

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
- **Retry Handler**: Fault tolerance with configurable RetryImpl policies
- **Circuit Breaker**: Service protection against cascading failures
- **Rate Limiter**: Request throttling with token bucket algorithm
- **Serialization**: Pluggable JSON/Object serializers
