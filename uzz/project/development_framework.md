# Development Framework

## Purpose

This project is built as a JavaFX desktop component and is intended to be run directly from Maven for local validation and integration into JavaFX applications.

## Technology Stack

- **Language / Runtime**: Java 25
- **Frameworks / Libraries**: JavaFX 25, OpenJFX Controls, AtlantaFX
- **Build Tool**: Maven
- **Test Tools**: [TBD] — no automated test suite is configured yet

## Development Workflow

- Keep the radial-menu geometry and interaction state explicit in the JavaFX view code.
- Validate the UI visually by running the sample application.
- Add automated tests when the component is formalized for reuse across applications.

## Build and Execution

- **Build**: `mvn clean package`
- **Test**: `mvn test`
- **Run**: `mvn javafx:run`

### Keywords
- development-framework
- toolchain
- build
- testing
- workflow
- javafx
- java25
