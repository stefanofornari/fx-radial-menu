<!--
  Copyright 2026 the original author or authors from the WakeMeApp project (https://github.com/stefanofornari/WakeMeApp)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

# Coding Standard

## Purpose

This standard applies to all Java source and tests in the `nb-uzzo-workflow` NetBeans module.

## General Principles

- Prefer clear, readable, and maintainable code.
- Keep components small and focused.
- Favour composition over duplication.
- Write code that is easy to test.
- Respect the NetBeans Platform lifecycle and threading rules: UI work on the Event Dispatch Thread, long-running work off it.

## Java Conventions

- Target Java 17 (`maven.compiler.release` in `pom.xml`).
- Use the `ste.uzzo.nb` package for module code.
- Prefer `final` classes and fields where inheritance or mutation is not required.
- Keep NetBeans API registrations (`@NodeFactory.Registration`, `@OnShowing`) on small, dedicated classes.
- Expose only the package `ste.uzzo.nb` as a public package, as declared in the `nbm-maven-plugin` configuration.

## Testing Conventions

 - Use JUnit 5 for tests.
 - Use AssertJ with BDD style assertions via `then()` rather than `assertThat()`.
 - Use TestFX for JavaFX UI testing.
 - Support headless test execution.
 - Test names must use snake_case format.
 - Prefer behavior-focused test names.
 - Use snake_case naming only for test method names.

## Documentation Conventions

- Document public and non-obvious classes and methods with Javadoc.
- Keep comments focused on intent and rationale, not on restating the code.
- Update `README.md` when user-visible behaviour or requirements change.

### Keywords
- coding-standard
- conventions
- style
- quality
- testing
