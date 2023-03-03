# Train Handler - server

*Train Handler server component of the FAIR Data Train*

## Usage

Train Handler is intended to be used together with [client](https://github.com/FAIRDataTeam/TrainHandler-client) via Docker (unless for development purposes).

The intended use is via Docker and Docker Compose, configured via envvars:

```yaml
  trainhandler-server:
    image: fairdata/trainhandler-server:latest
    restart: unless-stopped
    # volumes:
    #   - ${PROJECT_ROOT}/application.yml:/app/application.yml:ro
    environment:
      FDT_DISPATCH_ROOT: ${API_URL}
      FDT_DISPATCH_INTERVAL: PT60S
      FDT_POSTGRES_DB: ${POSTGRES_DB}
      FDT_POSTGRES_USERNAME: ${POSTGRES_USER}
      FDT_POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      FDT_KEYCLOAK_ENABLED: true
      FDT_KEYCLOAK_URL: ${KEYCLOAK_URL}
      FDT_KEYCLOAK_REALM: ${KEYCLOAK_REALM}
      FDT_KEYCLOAK_RESOURCE: ${KEYCLOAK_CLIENT_API}
```

## Development

### Build & Run

To run the application, a PostgreSQL database is required to be running. To configure the MongoDB with standard
connection (`postgresql://localhost:5432/train-handler`), simply instruct Spring Boot to use the `dev` profile. Then run:

```bash
$ mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Alternatively, set the `dev` profile in your IDE that is used to launch the application.

### Run tests

Run from the root of the project:

```bash
$ mvn test
```

### Package the application

Run from the root of the project:

```bash
$ mvn package
```

### Create a Docker image

You do not have to install Java and IDE locally, we supply multistage Dockerfile that first 
build `jar` file and then creates the image for deploying Train Handler:

```bash
$ docker build -t trainhandler-server:local .
```

## Contributing

We maintain a [CHANGELOG](CHANGELOG.md), you should also take a look at our [Contributing Guidelines](CONTRIBUTING.md)
and [Security Policy](SECURITY.md).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for more details.
