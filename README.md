# Electro Inventory

_This is a next generation of my old [ElectronicStorehouse][1] project_

Electro inventory is ~~an andvanced solution~~ just another CRUD app for electronic geeks that lets you keep all info about your electronic stuff in
database.

Its purpose is to help deal with dozens/thousands of little
electronic components laying everywhere. Sometimes I couldn't find nessesary components or, on the other side, I bought some transistors/IC's again and again
because I didn't know I already have them somewhere. Having their location and some basic parameters saved in database I can easily find out if I need to buy electronic stuff or if I already have needed components hidden somewhere.

# Features
* Manage items (components) - each has _category_, _name_, _quantity_, _description_ and _website_.
* Manage categories and subcategories.
* Simple and friendly UI.
* **New awesome features are work in progress.** See [Roadmap](#roadmap). More info coming soon...

![Demo screenshot](/demo1.PNG)

# Table of contents
* [Technical stack](#technical-stack)
* [Project structure](#project-structure)
* [Running locally](#how-to-run-locally)
* [Client details and configuration](#client-details-and-configuration)
    * [File structure](#File-structure-of-`inventory-client`)
    * [Environment variables](#Client-Environment-variables)
    * [Docker server](#Docker-Server-for-Frontend-app)
* [API details and configuration](#api-details-and-configuration)
    * [Application structure](#application-structure)
    * [Configuration](#configuration)
    * [Testing](#testing)
* [Docker configuration](#docker-configuration)
* [Deploying to your server (docker)](#deploying-to-docker-server)
* [Roadmap](#roadmap)

# Technical stack
This is a docker based client-server app, that uses:
* Dockerized, container architecture - everything is a docker-compose project
* Maven based Spring Boot project for backend
* React with [react-admin][2] for frontend
* ExpressJS server for serving React frontend and acts as a gateway
for API backend.
* Dockerized or external MySQL database (or any other compatible with Spring)
* Works with dockerized [nginx-proxy][3] and [LetsEncrypt][3] SSL.

    [1]: https://github.com/barthap/ElectronicStorehouse
    [2]: https://marmelab.com/react-admin/index.html
    [3]: https://blog.ssdnodes.com/blog/host-multiple-ssl-websites-docker-nginx/

Detailed info can be found below.

# Project structure
* `config` - configuration files loaded by docker-compose
* `private_config` - folder for your custom configuration overrides
* `inventory-client` - React frontend app
    * `server` - ExpressJS subproject for serving frontend as docker container
* `InventoryClient` - Java/Spring API backend
* `docker-compose.yml` - Docker-compose project file
* `docker-compose.build.yml` - Docker-compose override used to manually build images

# How to run locally
The app is ready to run in development mode, locally.

> This section shows how to start the application on local machine, without Docker. To see Docker configuration, see [Docker configuration](#docker-configuration) and [Deployment to Docker](#deploying-to-docker-server)

Firstly, clone the app
```
git clone https://github.com/barthap/ElectroInventory.git
```

## Running frontend
_Requirements: NodeJS 8 or newer, NPM or Yarn_

Go into `inventory-client` directory and then
```
npm install
npm start
```
or, in case of yarn
```
yarn install
yarn run start
```
This will run _create-react-app_ development server at `http://localhost:3000`.
Login credentials are _demo/demo_.

## Running API backend
_Requirements: JDK 8_

Go into `InventoryServer` directory and

* For Windows
  ```
  .\mvnw.cmd spring-boot:run
  ```
* For Linux / OSX
  ```
  ./mvnw spring-boot:run
  ```
This will compile and then start API server at `http://localhost:8081/api` by default.

# Client details and configuration
Client a React app based on [react-admin](https://marmelab.com/react-admin/index.html). I've decided to use this tool because it mostly suits my needs
and has many features out of the box, e.g. pagination, filtering, CRUD forms and displays. It became much easier to create nice user interface using react-admin
than writing everything myself.

## File structure of `inventory-client`
* `public` - index.html generated by _create-react-app_
* `server` - subproject for ExpressJS project (explained below)
* `src` - project source files
    * `resources` - react-admin UI definitions for business resources:
        * `item.jsx`
        * `category.jsx`
    * `App.js` - main React app file
    * `Home.jsx` - homepage screen component
    * `restProvider.js` - provides REST communication with API
    * `authProvider.js` - basic user authentication logic support
* `.env.local` - local/development environment variables
* `.env.production` - production environment variables
* `Dockerfile`
* `package.json`

## Client Environment variables
When application is running using `npm start`, the `NODE_ENV` environment variable is set to _"development"_. When it is deployed to docker - built using Dockerfile or just built using `npm run build`, then Node environment is set to _"production"_.

Client app uses different variables depending on current Node environment. They are defined in `.env.local` for development mode, and `.env.production` for production only. Variables used by client app:
* `REACT_APP_API_URL` - URL of API backend service, can be different for development and production
* `REACT_APP_API_USER` - credentials used for `restProvider.js` to connect with backend Spring API service.
* `REACT_APP_API_PASS`
* `REACT_APP_DEMO_LOGIN` - _(dev only)_ - client app login in development mode
* `REACT_APP_DEMO_PASSWD` - _(dev only)_ - client app password in development mode

> In **production** mode, credentials are *NOT* read from environment variables. They are gathered from client's `/auth` endpoint, which is described below. Also see [`/inventory-client/authProvider.js`](/inventory-client/authProvider.js) for details.

## Docker Server for Frontend app
In production mode, the React app is hosted by an [ExpressJS](https://expressjs.com/) server. It is the only public docker container. It's simple one-file subproject, with core in [`/inventory-client/server/server.js`](/inventory-client/server/server.js).
The file does 3 things:
* Hosts React client app
* Acts as an API gateway
* Provides simple `/auth` endpoint for users

API gateway uses `http-proxy` - it acts as reverse proxy for all `/api/*` requests and redirects them to real API service specified as environment variable. It hides API's location from users and even from _nginx-proxy_, which, by the way, covers this server.

Auth endpoint just receives credentials from `authProvider.js` and compares them to those defined in it's environment variables.

> **_Caution!_** This is NOT a production-ready solution! It is for demonstration purposes only and using it may be very unsafe. It is recommended to write your own authentication logic.

### Server's environment variables
* `API_URL` (_default: `http://api:8080`_) - URL to API service (real or dockerized)
* `PORT` (_default: 4123_) - Port on which the server is hosted
* `PANEL_USER` - (_default: demo_) - Username for `/auth` endpoint
* `PANEL_PASS` - (_default: demo_) - Password for `/auth` endpoint

Those variables are defined in `docker-compose.yml`

# API details and configuration
Backend API is written in Java 8 (and some Kotlin) based on Spring Boot 2.1.5 framework. It uses Maven as build tool.
>The project has Maven wrapper included - you do not need to install Maven.

> Why there is some **Kotlin** code? This project is written mostly in Java, but
it has some _"legacy"_ code snippets copied from my Kotlin projects. It is a really nice language and some things are just easier and faster to do in Kotlin rather than using Java. However, there are some pitfalls and when using both languages together - mostly annotation processor issues, Lombok incompatibility or Java compiler not seeing Kotlin classes and vice versa.

## Application architecture
This is simple RESTful service with layered architecture:
```
REST Controllers <--Model/DTO--> Service layer <----> Spring Data JPA Persistence
```
### Package structure
`com.hapex.inventory` - base package
* `config.security` - Spring Security configuration files
* `controller` - Controllers
    * `helper` - Controller advices, handles exceptions, adds custom headers to responses etc.
* `data` - Hibernate/JPA Entities, Repositories and DTOs
* `service` - Service layer
* `utils` - Utilities, custom Exceptions etc.

Classpath resources:
* `application*.yml` - configuration files, described below
* `data.sql` - example SQL data

## Configuration
Standard Spring Boot configuration is located, as always, in `application.yml`. However, the project has defined two (even three) configuration profiles and some custom properties.
### Spring profiles
* `default` - (_application.yml_) Default profile is loaded when Spring is loaded without specifying profile. It is mostly used in development mode - it has configured an in-memory H2 database and loads some example data from `data.sql` file. The data is recreated using Hibernate's each time the app is restarted.
* `prod` - (_application-prod.yml_) - Activated with `--spring.profiles.active=prod` command line param. It overrides datasource connection to an external database, for example MySQL. It has DDL and loading `data.sql` disabled. ~~It is useful to test the app on production data~~ _NEVER DO THAT!_
* There is also an external `/config/application.yml` in projects root directory. This configuration file is injected to API container in `docker-compose.yml` and has higher priority than the two above.

### Configuration Properties
Some properties in `application.yml` are worth looking at:
* `server.port` and `server.servlet.context-path` - needed to work properly with other services and/or docker containers.
* `security.username/password` - HTTP Basic Auth credentials required to access REST API
* `client.origin` - Required for CORS requests. It is provided to `Access-Control-Allow-Origin` header. Defaults to `*` if not provided. The same value should be set in `management.endpoint.web.cors.*` properties
* `spring.datasource.*` and `spring.jpa.*` - Database configuration

## Testing
There are about 30 unit and integration tests in `src/test/java`. They cover most of application's business logic - mostly critical or unclear parts, but some trivial parts are also covered - they were implemented using TDD strategy. Always write tests when you add new functionality. It helps!

Running tests:
```
Windows:    .\mvnw.cmd test
Linux/Mac   ./mvnw test
```

# Docker configuration
The _Docker Compose_ project consists of 2 containers (and some optional external services):
* `api` - builds and runs Spring API service, by default at local (docker-network level) port `8081`, mounts `/config` volume and injects `application.yml` configuration.
* `client` - builds React app and runs ExpressJS server to host it. Runs on local port `4123`, but it is available to external `nginx-proxy` network by default, can also be shared using `ports`. Specifies environment variables needed for Express server to work.

There is also a `docker-compose.build.yml` file. It is needed if you decide to build your Docker images on target machine.


## Docker networks
* `internal` - connects API service with Express server.
* `mysql_conn (database-net)` - external network to connect with database (external project).
* `proxy (nginx-proxy)` - external network needed for nginx-proxy to see our Express server container.

## About Dockerfiles
Building Docker images just works, and nothing more. It needs some rework to do the job faster and more effortlessly.

* **Server dockerfile** - Just copies fat-jar generated by Maven and runs it.
* **Client dockerfile** - It copies all js sources and builds React and Express apps from scratch using npm and _package.json_. ~~Requires _package-lock.json_ to exist!~~

## Configuring Docker-compose.yml
* Open `docker-compose.yml`

* You can change expose port of _api_ service if you want, but it's seen only in local docker network.
* Set environment variables for _client_ service properly:
    * `PORT` (default 4123) - must be the same for all 4 places (build args, expose, PORT and VIRTUAL_PORT)
    * `API_URL` - must be `http://api_service_name:API_PORT`, if you didn't change any of these, leave it
    * `PANEL_USER` and `PANEL_PASS` - Login credentials
    * Others are described below

Then you also have to look into `docker-compose.build.yml`. Set _EXPOSE_PORT_ there and make sure that ~~generated jar in `/InventoryServer/target`~~ Maven `pom.xml` settings generates Jar which has the same name as specified in compose file (`services.api.build.args.JAR_FILE`).
> `docker-compose.build.yml` is automatically renamed to `docker-compose.override.yml` when running build script. This allows Docker to automatically load this file.

## Make your _client_ service visible!
* If you are using _nginx proxy_, you should define (uncomment) `VIRTUAL_HOST`, `VIRTUAL_PORT`, `LETSENCRYPT_HOST` and `LETSENCRYPT_EMAIL`. Don't forget to add your service to docker network to make it visible for nginx! Here, project proxy network is called `proxy` internally and `nginx-proxy` externally.

You also have to uncomment `proxy` network from `networks` section.

* If you are **NOT** using _nginx proxy_ (default), then you should expose your app to any port you want (9000 in the example)
    ```yml
    ports:
   - "9000:4123"
    ```

## Database connection
Project assumes that you already have MySQL database container running in external docker-compose project, connected to docker's global network (here named `database-net`). If so, just set `networks.mysql.conn.external.name` to your network's name to make your database visible to API service.

If not, you can add database service to project _(there are many online tutorials on how to do it)_. For example it might look like:
```yml
services:
    database:
        image: mysql:5.7
        ... # other config omitted
        expose:
            - 3306
        networks:
            - mysql_conn
    api:
        ...
        networks:
            - mysql_conn
            - internal
networks:
    internal:   # connects API with Client
    mysql_conn: # connects API with MySQL
    nginx-proxy:
        external:
            ...
```
##  Configure `application.yml` again
Open `/config/application.yml` and configure it as described [here](#Configuration-Properties). Make sure that `server.port` and `context-path` match your `docker-compose.yml` settings.

Remember to specify proper database URL:
* If you have database container in external project (with default expose port 3306), it will be:
        ```
        jdbc:mysql://container_name:3306/database_name
        ```
* If you have database as service in current project:
    ```
    jdbc:mysql://service_name:3306/database_name
    ```
* If your database is outside of Docker:
    ```
    jdbc:mysql://host_ip:3306/database_name
    ```
You also need to specify your database _username_ and _password_.

## Configure `.env.production`
In `/inventory-client` create file `.env.production` if doesn't exist, and set proper API URL:
```
REACT_APP_API_URL=http://your_client_service_address/api
```

You also need to specify `REACT_APP_API_USER` and `REACT_APP_API_PASS` - they should suit credentials from `config/application.yml`.

# Deploying to Docker server

Configure all files as described above, then open terminal/console and run build script:
```
Windows:    deploy.cmd
Linux/Mac:  ./deploy.sh
```
The script will automatically create directory named **`deploy`** by default. It contains all files needed to be copied to target machine. You can check if all of them are copied:
* `/config/*`
* `/inventory-client/src/*`
* `/inventory-client/public/*`
* `/inventory-client/server/*`
* `/inventory-client/package.json`
* `/inventory-client/Dockerfile`
* `/InventoryServer/target/*.jar` - generated jar file
* `/InventoryServer/Dockerfile`
* `/docker-compose.yml`
* `/docker-compose.override.yml`

Additional flags:
* `--skip-compile` - skips running Maven and building Jar file (useful if you do not need to rebuild API package)
* `--copy-config` - copies configuration files from `private_config` directory instead. Useful if you have custom production config. (details below)

## Run your containers
Regardless of selected deployment method, now copy contents of your `deploy` directory to target machine, then run:

```
docker-compose up -d
```

If you need to rebuild docker images, use
```
docker-compose up -d --build --force-recreate
```

You can check if they are working properly with following tools:
```
docker ps
docker inspect container_name
docker-compose logs servicew_name
```

## Override your config: `--copy-config` option
You can create directory named `private_config` and copy there your configuration files (without keeping folder strucutre - copy only files):
* `docker-compose.yml`
* `.env.production`
* `application.yml`
* `registry.txt`

Now, when you run deploy script with `--copy-config` flag, those files
will be loaded from this directory instead of the original ones.
> **Note** All of these files must exist in `private_config` directory, otherwise deploy script will crash.

## Roadmap
* ~~Add location management~~ Done
* ~~Categories displayed as a tree~~ Done
* **Create Mobile and PC client app** - apps would have device-specific features. For instance,
Mobile app would be used for scanning labels / taking photos, and PC app for management and printing labels.
* **Add label printing + QR Code support** - Integrate PC soft with label printer, and add ability to scan them
in mobile app.
* Add live-sync between mobile and PC app - using Apple Bonjour or other service discovery
* Unify configuration
* Improve build and deployment scripts