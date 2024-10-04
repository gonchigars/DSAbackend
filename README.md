# Java Playground

This is a web-based Java learning platform that allows users to send Java code to a backend server, which compiles and executes the code, then returns the response.

## Prerequisites

- Java 11 or higher
- Maven

## Building the Project

To build the project, run the following command in the project root directory:

```
mvn clean install
```

This command will compile the source code, run tests, and package the application.

## Running the Application

To run the application, use the following command:

```
mvn spring-boot:run
```

The application will start and listen on `http://localhost:8080`.

## Running Tests

To run the tests, use the following command:

```
mvn test
```

This will execute all the unit tests in the project.

## Testing with Postman

1. Open Postman
2. Create a new POST request to `http://localhost:8080/compile`
3. Set the body to raw and select JSON (application/json) as the content type
4. Enter your Java code as a string in the request body. For example:

```json
{
  "code": "public class DynamicClass { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }"
}
```

5. Send the request
6. The response will contain the output of the compiled and executed Java code

## Note

This is a simplified implementation for demonstration purposes. In a production environment, you would need to implement proper security measures and error handling.
