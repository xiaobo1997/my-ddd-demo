# Repository Guidelines

## Project Structure & Module Organization

This is a Java 8 multi-module Maven project rooted at `pom.xml`. Source code follows the standard Maven layout: `src/main/java`, `src/main/resources`, `src/test/java`, and `src/test/resources`.

- `my-ddd-demo-api`: public command facade contracts and DTOs.
- `my-ddd-demo-app`: application services, factories, converters, and published events.
- `my-ddd-demo-domain`: domain entities, value objects, and repository interfaces.
- `myb-ddd-demo-infra`: infrastructure implementations, gateways, persistence DOs, and MQ utilities.
- `my-ddd-demo-adapter`: adapter implementations that bridge API contracts to application services.
- `my-ddd-demo-start`: startup/assembly module.

Keep package names under `com.viw.ddd.demo`. Preserve the DDD layering direction: API/adapter should call application services; application code coordinates domain and infrastructure; domain code should not depend on infrastructure.

## Build, Test, and Development Commands

- `mvn clean compile`: compile all modules from the repository root.
- `mvn clean test`: run the full Maven test lifecycle for every module.
- `mvn clean package`: build module artifacts after tests pass.
- `mvn -pl my-ddd-demo-app -am test`: test one module and any required upstream modules.

Run Maven commands from the root unless you are intentionally working inside a single module.

## Coding Style & Naming Conventions

Use Java 8-compatible code, UTF-8 files, and 4-space indentation. Follow existing class suffixes: `*Command`, `*DTO`, `*Entity`, `*VO`, `*Repository`, `*Gateway`, `*Service`, `*ServiceImpl`, and `*Convert`. Prefer small methods that keep orchestration in `app`, business state in `domain`, and external integration details in `infra`. Lombok is available in API/domain modules; use it consistently with nearby code.

## Testing Guidelines

Test directories exist, but no test framework dependency is currently configured. When adding tests, add the required Maven test dependency in the relevant module or parent POM. Place tests under the matching module’s `src/test/java` tree and name them `*Test`, mirroring the production package. Focus coverage on domain behavior, application service orchestration, and repository/gateway implementations.

## Commit & Pull Request Guidelines

The repository currently has no commit history, so use concise imperative commit messages such as `Add apply order service validation` or `Fix express event publishing`. For pull requests, include a short purpose statement, affected modules, test results from Maven, and linked issue/task references when available. Add screenshots only for UI-facing changes.

## Security & Configuration Tips

Do not commit local IDE metadata, generated build output, credentials, or environment-specific configuration. Keep secrets outside source files and pass configuration through environment variables or deployment-specific files.
