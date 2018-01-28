# Transaction statistics

Restful API for transactions statistics. 
The main use case for the API is to calculate realtime statistic from the last 60 seconds.

## Getting started

Transaction Statistics app can be used either on your computer for local testing or production environment for hosting them.

### Prerequisites

##### Requirements

Your system needs to meet these requirements:

* Windows, Linux, Mac OS X
* Java (version 1.8.*)
* [Apache Maven](https://maven.apache.org/) (version 3.*) Dependency Management

### Installing with Maven

The best way to install application is via **Maven**. At the terminal prompt, simply run the following command to install the app:

```
mvn clean install
```

To run the application as a packaged application:

```
java -jar target/transaction-statistics.jar
```

To run the application using the Maven plugin

```
mvn spring-boot:run
```

### API documentation
To get the Transaction statistics API documentation run web browser with URL: 
```
http://[your host]/docs/index.html
```

Example for local environment:
```
http://localhost:8080/docs/index.html
```

## Authors

* **Nikolay Odintsov**
