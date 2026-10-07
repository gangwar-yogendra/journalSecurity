# Journal Security API

A Spring Boot application for user registration, secure authentication, and journal entry management using MongoDB and Spring Security.

## Features

- User registration with BCrypt password hashing
- Basic authentication for protected endpoints
- Journal creation tied to the authenticated user
- MongoDB-backed user and journal storage
- Sentiment-aware journal workflow

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data MongoDB
- Lombok
- Maven

## Project Setup

1. Clone the repository
2. Configure MongoDB connection in your `application.properties` or `application.yml`
3. Run the application:

```bash
mvn spring-boot:run
```

## API Endpoints

### 1) Register a user

Endpoint:

```http
POST http://localhost:8080/api/v1/users/register
```

No authentication required.

Request body:

```json
{
  "userName": "yogendra",
  "password": "mypassword",
  "email": "test@example.com"
}
```

The service hashes the password before saving:

```java
user.setPassword(passwordEncoder.encode(user.getPassword()));
```

Stored MongoDB document example:

```json
{
  "userName": "yogendra",
  "password": "$2a$10$....",
  "roles": ["USER"]
}
```

Important:
- Never store plaintext passwords.
- BCrypt hashing is used for secure password storage.

### 2) Create a journal entry

Endpoint:

```http
POST http://localhost:8080/api/v1/journal/create
```

Authentication:
- Type: Basic Auth
- Username: `yogendra`
- Password: `mypassword`

Request body:

```json
{
  "title": "I am Happy",
  "content": "Today I am Happy",
  "sentiment": "Happy"
}
```

## Authentication Flow

The request flow works like this:

```text
Postman
  |
  | Basic Auth
  v
Spring Security
  |
  | username = "yogendra"
  v
UserDetailsServiceImpl
  |
  | findByUserName("yogendra")
  v
MongoDB users collection
  |
  | UserEntity
  | username = yogendra
  | password = BCrypt hash
  | roles = USER
  v
Spring Security
  |
  | BCrypt password comparison
  v
Authenticated
  |
  v
JournalEntryController
  |
  | authentication.getName()
  v
"yogendra"
  |
  v
JournalEntryService.saveEntry(...)
  |
  | find user "yogendra"
  v
MongoDB
  |
  | save journal
  | add journal reference to user
```

The key logic is:

```java
UserEntity user = userService.findByUserName("yogendra");
```

Then the new journal entry is saved and linked to the user through:

```java
user.getJournalEntries().add(saved);
```

## Password Security

Spring Security verifies the incoming password by comparing it with the stored BCrypt hash:

```text
mypassword
  ↓
BCrypt matches
  ↓
stored hash
  ↓
Authentication successful
```

This means the application validates the raw password against the hashed database value without exposing the original password.

## Notes

- `UserDetailsServiceImpl` is the component responsible for loading the user from MongoDB during authentication.
- `UserEntity` represents the stored user record in the database.
- The current implementation uses the authenticated username to fetch the correct user before saving journal data.

## Example Usage

### Register

```http
POST http://localhost:8080/api/v1/users/register
Content-Type: application/json

{
  "userName": "yogendra",
  "password": "mypassword",
  "email": "test@example.com"
}
```

### Create Journal Entry

```http
POST http://localhost:8080/api/v1/journal/create
Authorization: Basic base64(yogendra:mypassword)
Content-Type: application/json

{
  "title": "I am Happy",
  "content": "Today I am Happy",
  "sentiment": "Happy"
}
```

## Summary

This project demonstrates a secure authentication flow using Spring Security and BCrypt hashing, where users are registered safely, authenticated using Basic Auth, and then authorized to create journal entries linked to their own profile.
