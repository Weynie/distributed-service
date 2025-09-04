# Distributed Service - Distributed Database Management Prototype

> A Java RMI-based distributed database system testing concurrent transactions.

## System Architecture

**Distributed Design**: Java RMI architecture with dedicated service instances per client and centralized registry management on localhost:1099

**Technology Stack**: Java 17, SQLite, JDBC, RMI, Maven build system

**Key Features**:
- **Manual Transaction Control** with explicit commit/rollback operations
- **Concurrent Client Support** handling 10+ simultaneous connections
- **Fault Tolerance** with 50 retry attempts and exponential backoff
- **Data Recovery** with RestoreInvoker to restore clean data

## Technical Implementation

### Service Architecture
```
Client Layer (RMI Client)
    ↓
Service Layer (EMPService Interface)
    ↓  
Business Logic Layer (EMPServer)
    ↓
Data Access Layer (EMPDAOWrapper + EMPDAO)
    ↓
Database Layer (SQLite via JDBC)
```

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Windows environment (for batch file demos)

### Build & Compile
```bash
mvn compile
# Verify: Check for compiled classes in demo/target/classes/com/distributed/service/
```

### Start the System
```bash
# 1. Start the RMI server
java com.distributed.service.EMPServiceFactoryServer

# 2. Navigate to demo directory
cd demo

# 3. Test database connection
java -cp target/classes com.distributed.service.Client select
```

## Demo Scenarios

### Concurrent Write Operations
Demonstrates write lock contention and transaction serialization:
```bash
.\concurrentWrite.bat
```
**Scenario**: Two clients performing simultaneous write operations  
**Behavior**: Second client waits for first client's transaction completion

### Concurrent Read-Write Operations  
Shows read-write lock separation and concurrent access:
```bash
.\concurrentRead.bat
```
**Scenario**: One client writing, another reading simultaneously  
**Behavior**: Read operations proceed immediately without blocking

### Sequential Operations
Illustrates transaction isolation and atomicity:
```bash
.\sequential.bat
```
**Scenario**: Multiple write operations in sequence  
**Behavior**: No lock contention, predictable execution order

### Manual Transaction Testing
Execute multiple operations in a single transaction:
```bash
java -cp target/classes com.distributed.service.Client select delete E8 insert E11 name=Tom title=Eng. select
```

### Database Reset
Restore database to original state:
```bash
java -cp target/classes com.distributed.service.RestoreInvoker
```


