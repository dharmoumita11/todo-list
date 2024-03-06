
# A simple to-do list service

A backend service allowing basic management of a simple to-do list.


## Features

* Add an item
* Update an item
* Get all items
* Get an item
* Delete an item
* Mark an item as 'done'
* Mark an item in 'done' state as 'not done'
* Scheduler to update status of items that are past the due date to 'past due'
* Cross platform (with docker)


## Assumptions

* It's a simple todo list service for personal use
* Description of an item is not unique (can be repeated)
* DueDateTime must be in the future when adding or updating
* Description and DueDateTime are mandatory for creating a Todo item
* Either description or DueDateTime or both can be updated for a Todo item. If the update request doesn't have any data to update, the item is returned as is.
* 'Past due' items can only be marked done (_I think even if an item is past due, user should be able to mark it as done_). No other operations allowed.
* An item can be marked as 'not done' only if it were in 'done' state.
* Attempting to mark a 'not done' item as 'not done' again will return the same item.
* Attempting to mark a 'done' item as 'done' again will return the same item.
* GET API will by default retrieve all items that are in 'not done' or 'past due' state. (_since 'past due' state also represents a pending state_)
* Allow deletion of items unrestricted assuming, an item might not be relevant anymore for the user, deletion should be allowed. To prevent deletion of an item by mistake, an additional confirmation prompt can be added on the front end.


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

* Java 17  -
  _to build source code_
* Docker   -
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
### 1. Build and run the source code

Build the service

```bash
  ./gradlew build
```

Start the server

```bash
  ./gradlew bootRun
```
### 2. Run using Docker

Build and run using Docker
(_separate build step is not required_)

```bash
docker compose up -d
```

Add --build to rebuild

```bash
docker compose up --build -d
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

* Add sonar or jacoco for test coverage
* gzip log files when rolling to save space
* Add release notes / changelog
* Docker registry should be added and used to publish docker images built
* Generate javadocs
* Use SSL
* Add authentication for user as well as swagger
* Use custom paths for swagger-ui as well as api-docs
* Add a database migration tool like Flyway to manage schema changes
* Implement backup and restoration plan
* Add actuator for health check
* Add monitoring
* Update DB with user and password.
* Configuration management for secrets.
* Externalized configuration for environment specific variables
* CI/CD pipeline
## Roadmap

* Implement Repository pattern to make changing DB more flexible, especially in case we want to switch to NoSQL in future
* Return lastModified date for each item, indicating the datetime when user has last modified an item
* Add category and priority fields
* Filter items by status or category or priority
* Default sort by created date descending
* Add sorting by fields
* Add soft delete instead of hard delete for DELETE operation to maintain history, perhaps for certain time period like 30 days
* Make description unique
* Error handling can be improved by adding
  - ErrorCode Enum containing
    - errorCode
    - default message and/or explanation
  - A default or parent custom exception containing
    - errorCode
    - errorMessage
    - detailedMessage
  - All the other custom exceptions will extend this default or parent custom exception
* Implement search by description
* Implement caching as application expands
* Option to set reminders for about to due items
* Option to set reminders for items at specific date / intervals
