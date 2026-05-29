# Deployment

This app deploys as a single Spring Boot service. The frontend is already bundled under `src/main/resources/static`, so the backend serves both the UI and APIs.

## Required Runtime

- Java 17
- MySQL database

## Required Environment Variables

Set these in your hosting platform:

```text
PORT=8080
DATABASE_URL=jdbc:mysql://<host>:<port>/<database>?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DATABASE_USERNAME=<mysql-user>
DATABASE_PASSWORD=<mysql-password>
JWT_SECRET=<at-least-32-character-random-secret>
```

Optional:

```text
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=https://your-domain.com
JPA_SHOW_SQL=false
JPA_FORMAT_SQL=false
TWILIO_ACCOUNT_SID=<sid>
TWILIO_AUTH_TOKEN=<token>
TWILIO_FROM_NUMBER=<number>
```

If the frontend and backend are served from the same deployed domain, `CORS_ALLOWED_ORIGINS` is usually not needed.

## Build

```powershell
.\mvnw.cmd clean package -DskipTests
```

The jar is created in:

```text
target/FLOWER-SHOP-BILLING-0.0.1-SNAPSHOT.jar
```

## Run Jar

```powershell
java -jar target\FLOWER-SHOP-BILLING-0.0.1-SNAPSHOT.jar
```

Open:

```text
http://localhost:8080
```

## Docker

```powershell
docker build -t flower-shop-billing .
docker run --env-file .env -p 8080:8080 flower-shop-billing
```
