# Train Handler - server

*Train Handler server component of the FAIR Data Train*

## Usage

Train Handler is intended to be used together with [client](https://github.com/FAIRDataTeam/TrainHandler-client) via Docker (unless for development purposes).

*To be done: repository with docker-compose and configuration*

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
