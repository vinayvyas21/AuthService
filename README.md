# User Authentication Service

This project is a user authentication service that provides functionalities such as user registration, login, logout, and session management. It is built using Java and utilizes JUnit 5 for unit testing and Mockito for mocking dependencies.

## Features

- User Registration: Allows new users to sign up with their email and password.
- User Login: Authenticates users and generates a JWT token for session management.
- User Logout: Invalidates the user's session token.
- Session Management: Ensures active session limits and handles session statuses.

## Technologies Used

- Java: Core programming language.
- JUnit 5: For writing and running unit tests.
- Mockito: For mocking dependencies in unit tests.
- Spring Framework: For dependency injection and application configuration.
- BCrypt: For password hashing and verification.
- JWT (JSON Web Token): For secure token-based authentication.

## Project Structure

- `dtos`: Contains Data Transfer Objects for requests and responses.
- `exceptions`: Custom exception classes for handling specific error scenarios.
- `models`: Entity classes representing database models.
- `repositories`: Interfaces for database operations.
- `utils`: Utility classes such as JWT token generation and validation.
- `services`: Business logic for user authentication and session management.
- `tests`: Unit tests for the service layer.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- A database (e.g., MySQL, PostgreSQL) for persistence

## Setup Instructions

1. Clone the repository:

git clone https://github.com/vinayvyas21/AuthService

2. Add the required dependencies in the `pom.xml` file.

3. Configure the database connection in the `src/main/resources/application.properties` file:

4. Build the project:
	mvn clean install

5. Run the application:
	mvn spring-boot:run

6. Access the application:
   Open your browser and navigate to `http://localhost:8080`.



