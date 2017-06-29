# Customer REST API

A Demo CRUD REST application that manages customers

### Live Demo
https://customers.view-this.info/


### Deployment
1. Make sure you have Java 8, Maven and MySQL installed
2. Create a database on MySQL if you don't have one ready. 
3. Then setup Customers table using the attached sql script.
4. Go to the project directory and build the application
```sh
mvn package
```
5. Create an application.properties file beside your application and configure the following
```
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
```
6. Run the application
```sh
java -jar target/customers-1.0.0-SNAPSHOT.jar
```


### REST API

* Get the customer endpoints
```
http://<host:port>/api/profile/customers
```

