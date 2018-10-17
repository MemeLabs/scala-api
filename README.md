# GG - Api 2
This is a scala project for an api service for strims.gg

## Packages

### Core
This is the core library, shared models, libraries, and scripts. Here we store the things that we need to manage our data structures and run our api's. This is useful when writing tests as we can build a our main api server and test suite completely separately.

### Core-API
This is the api layer / server. It does all the things we want.

### Core-Jobs (Unused)

## Developer Guide
Guide to how to develop on this api.

### Building
The project is currently built using sbt. Installing an IDE like Intellj can be useful in doing development but it is not required.

### Formatting
Scalariform is included as a plugin to the project to provide linting, use it.

```concept
$ sbt scalariformFormat
```

#### Package commands
All packages are separate in entity, to develop / run any sbt command specifying a package is critical.

```concept
$ sbt core-api/compile
$ sbt core-api/run
```

#### Docker Images
You can leverage the docker-compose files in the root of the project, or build Docker images manually by running the corresponding docker commands similar to what the compose executes.

**Note:** There are images and commented lines in this project that support dbs / redis storage even though not in use.
##### Core-Api: Build / Run w/Docker

```concept
$ docker-compose up
$ ...
$ ... Successfully bound to /0.0.0.0:3000

```

### API Documentation
Api documentation is done through a software called [postman](https://www.getpostman.com/). Reading the routes file also indicates API behavior, but it is recommended to load up the collection in the root of this directory or follow this link [postman-api](https://documenter.getpostman.com/view/5567026/RWgp2fKK). 