# p4pa-organization

This application belong to the **entity** tier of the **Piattaforma Unitaria** product.

See [PU Microservice Architecture](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1405845916/Architettura+microservizi) for more details.

## 🧱 Role

* To handle brokers and api keys;
* To handle organizations and api keys;
* To handle taxonomies.

## 🌐 APIs
See [OpenAPI](openapi/generated.openapi.json), exposed through the following path:
* `/swagger-ui/index.html`

### 📌 Relevant APIs
* `PUT /brokers/{brokerId}/apiKey`: To set broker api keys;
* `GET /broker/{brokerId}/apiKey/{keyType}`: To get broker api keys;
* `PUT /organization/{organizationId}/apiKey`: To set organization api keys;
* `GET /organization/{organizationId}/apiKey/{keyType}`: To retrieve an organization api key;
* `GET /taxonomies/sync`: To ask to synchronize taxonomies.

## 🔎 Monitoring
See available actuator endpoints through the following path:
* `/actuator`

### 📌 Relevant endpoints
* Health (provide an accessToken to see details): `/actuator/health`
  * Liveness: `/actuator/health/liveness`
  * Readiness: `/actuator/health/readiness`
* Metrics: `/actuator/metrics`
  * Prometheus: `/actuator/prometheus`

Further endpoints are exposed through the JMX console.

## ✏️ Logging
See [log configured pattern](/src/main/resources/logback-spring.xml).

## 🔗 Dependencies

### 🗄️ Resources
* PostgreSQL

### 🧩 Microservices
* [p4pa-pagopa-payments](https://github.com/pagopa/p4pa-pagopa-payments):
  * To retrieve taxonomies.

## 🗃️ Entities handled
* `broker`
* `organization`
* `org_sil_service`
* `taxonomy`

## 🔧 Configuration

See [application.yml](src/main/resources/application.yml) for each configurable property.

### 📌 Relevant configurations

#### 🌐 Application Server
| ENV         | DESCRIPTION                       | DEFAULT |
|-------------|-----------------------------------|---------|
| SERVER_PORT | Application server listening port | 8080    |

#### ✏️ Logging
| ENV                                   | DESCRIPTION                                                                                                                                                                     | DEFAULT |
|---------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|
| LOG_LEVEL_ROOT                        | Base level                                                                                                                                                                      | INFO    |
| LOG_LEVEL_PAGOPA                      | Base level of custom classes                                                                                                                                                    | INFO    |
| LOG_LEVEL_SPRING                      | Level applied to Spring framework                                                                                                                                               | INFO    |
| LOG_LEVEL_SPRING_BOOT_AVAILABILITY    | To print availability events                                                                                                                                                    | DEBUG   |
| LOGGING_LEVEL_API_REQUEST_EXCEPTION   | Level applied to APIs exception                                                                                                                                                 | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG             | Level applied to [PerformanceLog](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.-Log-di-performance)                                               | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_API_REQUEST | Level applied to [API Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.1.-Log-di-perfomance-per-le-API)                            | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_REST_INVOKE | Level applied to [REST invoke Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.2.-Log-di-performance-per-i-servizi-REST-integrati) | INFO    |

#### 🔁 Integrations

##### 🗄️ Resources
| ENV                      | DESCRIPTION                                                                           | DEFAULT                                                                                                                      |
|--------------------------|---------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| SHOW_SQL                 | To print SQL statements                                                               | false                                                                                                                        |
| ORGANIZATION_DB_URL      | PostgreSQL connection string (to use in order to customize the entire string)         | jdbc:postgresql://${CLASSIFICATION_DB_HOST}:${CLASSIFICATION_DB_PORT}/${CLASSIFICATION_DB_NAME}?currentSchema=debt_positions |
| ORGANIZATION_DB_HOST     | PostgreSQL Host                                                                       | localhost                                                                                                                    |
| ORGANIZATION_DB_PORT     | PostgreSQL port                                                                       | 5432                                                                                                                         |
| ORGANIZATION_DB_NAME     | PostgreSQL Database name                                                              | payhub                                                                                                                       |
| ORGANIZATION_DB_USER     | PostgreSQL username                                                                   |                                                                                                                              |
| ORGANIZATION_DB_PASSWORD | PostgreSQL password                                                                   |                                                                                                                              |

##### 🔗 REST
| ENV                                               | DESCRIPTION                               | DEFAULT |
|---------------------------------------------------|-------------------------------------------|---------|
| DEFAULT_REST_CONNECTION_POOL_SIZE                 | Default connection pool size              | 10      |
| DEFAULT_REST_CONNECTION_POOL_SIZE_PER_ROUTE       | Default connection pool size per route    | 5       |
| DEFAULT_REST_CONNECTION_POOL_TIME_TO_LIVE_MINUTES | Default connection pool TTL (minutes)     | 10      |
| DEFAULT_REST_TIMEOUT_CONNECT_MILLIS               | Default connection timeout (milliseconds) | 120000  |
| DEFAULT_REST_TIMEOUT_READ_MILLIS                  | Default read timeout (milliseconds)       | 120000  |

##### 🧩 Microservices
| ENV                                  | DESCRIPTION                                                                       | DEFAULT |
|--------------------------------------|-----------------------------------------------------------------------------------|---------|
| PAGOPA_PAYMENTS_BASE_URL                | Organization microservice URL                                                     |         |
| PAGOPA_PAYMENTS_MAX_ATTEMPTS            | Organization API max attempts                                                     | 3       |
| PAGOPA_PAYMENTS_WAIT_TIME_MILLIS        | Organization retry waiting time (milliseconds)                                    | 500     |
| PAGOPA_PAYMENTS_PRINT_BODY_WHEN_ERROR   | To print body when an error occurs                                                | true    |

#### 🔑 keys
| ENV                            | DESCRIPTION                                                        | DEFAULT |
|--------------------------------|--------------------------------------------------------------------|---------|
| JWT_TOKEN_PUBLIC_KEY           | p4pa-auth JWT public key                                           |         |
| BROKER_ENCRYPT_PASSWORD        | Base64 encoded key (256 bit) used to encrypt broker api keys       |         |
|  ORGANIZATION_ENCRYPT_PASSWORD | Base64 encoded key (256 bit) used to encrypt organization api keys |         |

## 🛠️ Getting Started

### 📝 Prerequisites

Ensure the following tools are installed on your machine:

1. **Java 21+**
2. **Gradle** (or use the Gradle wrapper included in the repository)
3. **Docker** (to build and run on an isolated environment, optional)

### 🔐 Write Locks

```sh
./gradlew dependencies --write-locks
```

### ⚙️ Build

```sh
./gradlew clean build
```

### 🧪 Test

#### 📌 JUnit
```sh
./gradlew test
```

### 🚀 Run local

```sh
./gradlew bootRun
```

### 🐳 Build & run through Docker
```sh
docker build -t <APP_NAME> .
docker run --env-file <ENV_FILE> <APP_NAME>
```
