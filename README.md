
# A simple to-do list service

A backend service allowing basic management of a simple to-do list.


## Features

- CRUD operations
- Mark an item as 'done'
- Mark an item in 'done' state as 'not done'
- Scheduler to update status of items that are past the due date to 'past due'
- Cross platform (with docker)


## Assumptions

* It's a simple todo list service for personal use
* Description of an item is not unique (can be repeated)
* DueDateTime must be in the future when adding or updating
* Description and DueDateTime are mandatory for creating a Todo item
* Eith description or DueDateTime or both can be updated for a Todo item. If the update request doesn't have any data to update, the item is returned as is.
* 'Past due' items can only be marked done (_I think even if an item is past due, user should be able to mark it as done_). No other operations allowed.
* An item can be marked as 'not done' only if it were in 'done' state.
* Attempting to mark a 'not done' item as 'not done' again will return the same item.
* Attempting to mark a 'done' item as 'done' again will return the same item.
* GET API will by default retrieve all items that are in 'not done' or 'past due' state. (_since 'past due' state also represents a pending state_)


## Tech Stack

* Java 17
* Springboot 3.1.9
* H2 Database 2.1
* SpringDoc OpenAPI 2.3.0
* Lombok 1.18
* JUnit 5
* Gradle 8.5
* Docker


## Pre-Requisites

- Java 17  -
  _to build source code_
- Docker   -
  _to build and run using Docker_


## Build & Run the service

First, clone the project

```bash
  git clone https://github.com/dharmoumita11/todo-list.git
```

Go to the project directory

```bash
  cd todo-list
```
### Build and run the source code

Build the service

```bash
  ./gradlew build
```

Start the server

```bash
  ./gradlew bootRun
```
### Using Docker

Build and run using Docker
(_separate build step is not required_)

```bash
docker compose up -d
```


## Running Tests

To run tests, run the following command

```bash
  ./gradlew test
```


## Using the application

Once the application has started, you can use the application via the Swagger UI.

`http://localhost:8080/todo/swagger-ui/index.html`


## Production Readiness

* jacoco to be added for test coverage
* gzip log files when rolling to save space
* add release notes / changelog
* docker registry should be added and used to publish docker images built
* generate javadocs


## Roadmap

- Additional browser support

- Add more integrations


***
Allow deletion of items assuming, an item might not be relevant anymore for the user, deletion should be allowed. 
However, to prevent deletion by mistake, an additional confirmation prompt can be added on the front end.


***
- Add authentication for swagger
- use custom paths for swagger-ui as well as api-docs

***
Error handling can be improved by adding
- ErrorCode Enum containing
  - errorCode
- default message and/or explanation
- A default or parent custom exception containing
  - errorCode
  - errorMessage
  - detailedMessage
- All the other custom exceptions will extend this default or parent custom exception 

***
We can use a formatter for the date if we want a different format. But I believe that should not be a problem.