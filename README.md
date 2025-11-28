# SG Technologies POS System - Reengineering Project

**Original System**: Alpha Release (CSE216 - Software Engineering, Dec 9, 2015)  
**Reengineering Project**: Fall 2025 - Software Reengineering Course  
**Status**: Code Restructuring Phase (30% Complete)

## Project Overview

This repository contains the legacy SG Technologies POS system and its ongoing reengineering process. The project follows the Software Reengineering Process Model to transform the legacy desktop Java application into a modern web-based system.

## Current Status

### ✅ Completed Phases
1. **Inventory Analysis** - Asset catalog and classification complete
2. **Document Restructuring** - Documentation reorganized and gaps identified
3. **Reverse Engineering** - Architecture, code smells, and workflows documented
4. **Code Restructuring** (30%) - 5 refactorings completed, improving maintainability

### 🔄 In Progress
- **Code Restructuring** - Continuing safe refactorings to improve code quality

### ⏳ Planned Phases
- **Data Restructuring** - Migrate from file-based to database storage
- **Forward Engineering** - Build web-based system (Spring Boot + React)

## Repository Structure

```
├── src/                    # Legacy Java source code
├── tests/                  # Test files (JUnit)
├── Database/               # Text-based database files
├── Documentation/          # Original project documentation
├── docs/                   # Reengineering documentation
│   ├── inventory-analysis.md
│   ├── reverse-engineering.md
│   ├── document-restructuring.md
│   ├── refactoring-log.md
│   └── implementation-status.md
└── pom.xml                 # Maven build configuration
```

## Quick Start

### Prerequisites
- JDK 8+ (tested with JDK 25)
- Maven 3.6+

### Build and Test
```bash
mvn clean compile
mvn test
```

## Key Documentation

- **[Implementation Status](docs/implementation-status.md)** - Current progress and phase completion
- **[Reverse Engineering Analysis](docs/reverse-engineering.md)** - Architecture, code smells, workflows
- **[Refactoring Log](docs/refactoring-log.md)** - All refactorings with before/after code
- **[Inventory Analysis](docs/inventory-analysis.md)** - Asset catalog and classification

## Legacy System Features

- **Sales**: Direct item sales with tax calculation
- **Rentals**: Item rentals tracked by customer phone number
- **Returns**: Processing returns for rented items
- **Employee Management**: Admin functions for managing cashiers/admins
- **Inventory Management**: Real-time inventory updates

## Design Patterns Identified

- **Singleton**: `Inventory` class
- **Abstract Factory**: `PointOfSale` abstract class with POS/POR/POH implementations
- **Template Method**: `PointOfSale` defines transaction skeleton

## Technology Stack

### Legacy (Current)
- Java 8
- Swing UI
- File-based storage (.txt files)
- Maven build system

### Target (Planned)
- **Backend**: Spring Boot (Java)
- **Frontend**: React (TypeScript)
- **Database**: PostgreSQL
- **Testing**: JUnit, React Testing Library

## Contributing

This is a semester project following specific reengineering phases. See `docs/implementation-status.md` for current phase and next steps.

## License

Academic project - See original project documentation for details.
