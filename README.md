# Employee Management REST API

A simple Spring Boot REST API for managing employees, with unit tests (JUnit 5 + Mockito), a Docker image, and a Jenkins CI/CD pipeline.

## Tech stack

| Technology       | Purpose                  |
|------------------|--------------------------|
| Java 17          | Backend development      |
| Spring Boot 3    | REST API                 |
| Maven            | Build management         |
| JUnit 5, Mockito | Unit testing             |
| H2               | In-memory database       |
| Git + GitHub     | Source code management   |
| Jenkins          | CI/CD automation         |
| Docker           | Containerization         |

## Endpoints

| Method | Path                  | Description          | Success |
|--------|-----------------------|----------------------|---------|
| POST   | `/api/employees`      | Create an employee   | 201     |
| GET    | `/api/employees`      | Get all employees    | 200     |
| GET    | `/api/employees/{id}` | Get employee by ID   | 200     |
| DELETE | `/api/employees/{id}` | Delete an employee   | 204     |

Unknown IDs return `404`; invalid request bodies return `400`.

Example body:

```json
{
  "name": "Alice",
  "email": "alice@example.com",
  "department": "Engineering",
  "salary": 90000
}
```

## Run locally

```bash
mvn spring-boot:run
```

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com","department":"Engineering","salary":90000}'

curl http://localhost:8080/api/employees
curl http://localhost:8080/api/employees/1
curl -X DELETE http://localhost:8080/api/employees/1
```

H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:employeedb`, user `sa`).

## Tests

```bash
mvn test
```

## Docker

```bash
docker build -t employee-management .
docker run -p 8080:8080 employee-management
```

## Jenkins

The `Jenkinsfile` runs: Checkout → Build → Test (publishes JUnit reports) → Package → Docker Build → Deploy.

Requirements on the Jenkins agent:
- JDK 17 and Maven configured under **Manage Jenkins → Tools** with the names `jdk17` and `maven3`.
- Docker installed, and the `jenkins` user allowed to run it.

Create a **Pipeline** job (or Multibranch Pipeline) pointing at the GitHub repository and choose "Pipeline script from SCM".
