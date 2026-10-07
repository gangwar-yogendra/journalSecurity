# Journal Security API

A Spring Boot application for user registration, secure authentication, and journal entry management using MongoDB and Spring Security.

## Features

- User registration with BCrypt password hashing
- Basic authentication for protected endpoints
- Journal creation tied to the authenticated user
- MongoDB-backed user and journal storage
- Sentiment-aware journal workflow
- Weather lookup for authenticated users
- Admin tools for user management and cache refresh
- Scheduler-driven sentiment email reporting

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

### 3) Admin endpoints

The admin controller is used for user administration and cache refresh.

- `GET /api/v1/admin/all-users` - list all users
- `POST /api/v1/admin/create-admin` - create an admin user
- `GET /api/v1/admin/clear-api-cache` - refresh the cached configuration values

### 4) User profile endpoint

The `/api/v1/users` endpoint returns a weather-based greeting for the authenticated user.

```http
GET http://localhost:8080/api/v1/users
```

It calls `WeatherService`, which uses `AppCache` to fetch the weather API URL and `WeatherResponse` to parse the external API response.

## WeatherResponse and AppCache

`WeatherResponse` is the POJO used to map the external weather API JSON into Java objects. It extracts the weather description and other fields returned by the API.

`AppCache` loads configuration values from MongoDB on startup and keeps them in memory. The weather API URL is stored in the `config_journal_app` collection and fetched through:

```java
appCache.getValue(AppCache.keys.WEATHER_API.name());
```

This keeps the weather URL out of the service code and makes it easy to update without redeploying.

## Sentiment and Scheduler

Each journal entry can store a `Sentiment` value such as:

- `HAPPY`
- `SAD`
- `ANGRY`
- `ANXIOUS`

The scheduler uses this data to analyze recent journal entries and send a weekly sentiment email.

### Scheduler flow

`UserScheduler.fetchUserAndSendSentimentalAnalysisEmail()`:

1. Loads users from the repository
2. Filters journal entries from the last 7 days
3. Counts the most frequent sentiment
4. Sends an email to the user with the result

There is also a cache refresh job:

`UserScheduler.clearAppCache()` reinitializes the in-memory cache from MongoDB.

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

## Scheduler Flow

```text
UserScheduler
  |
  | fetchUserAndSendSentimentalAnalysisEmail()
  v
UserRepositoryImpl
  |
  | users with email + sentiment enabled
  v
Recent journal entries
  |
  | last 7 days only
  v
Sentiment counts
  |
  | most frequent value
  v
EmailService
  |
  | sendEmail(...)
  v
User receives report
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
- `WeatherResponse` and `AppCache` power the weather greeting endpoint.
- `UserScheduler` drives the weekly sentiment report and cache refresh.

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
