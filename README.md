# AI Recruiter & Employee Lifecycle Management System

## Overview

The AI Recruiter & Employee Lifecycle Management System is a web-based application that automates the complete employee lifecycle, from recruitment to offboarding.

The system allows HR teams to manage:

- Resume Upload
- Candidate Screening
- Interview Scheduling
- Offer Letter Generation
- Onboarding
- Employee Management
- Knowledge Transfer (KT)
- Asset Management
- Offboarding
- Alumni Management

---

# Technology Stack

### Frontend
- Angular
- TypeScript
- HTML
- CSS
- Bootstrap

### Backend
- Java
- Spring Boot
- Spring Security
- REST API

### Database
- MongoDB

### Cloud Services
- AWS S3 (File Storage)
- Google Calendar API
- Email Service

---

# Prerequisites

Before running the project, install the following software.

| Software | Version |
|----------|----------|
| Java JDK | 17+ |
| Maven | Latest |
| Node.js | 18+ |
| Angular CLI | Latest |
| MongoDB | Latest |
| Git | Latest |

Verify the installation:

```bash
java -version
mvn -version
node -v
npm -v
ng version
mongod --version
```

---

# Clone the Repository

```bash
git clone <repository-url>
```

Move into the project directory.

```bash
cd AI-Recruiter
```

---

# Backend Setup

Navigate to the backend project.

```bash
cd backend
```

Install Maven dependencies.

```bash
mvn clean install
```

---

# Configure Database

Start MongoDB locally.

Default MongoDB URL:

```
mongodb://localhost:27017/airecruiter
```

Update your configuration inside:

```
src/main/resources/application.properties
```

Example:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/airecruiter
```

---

# Configure Environment Variables

Update all required values inside:

```
application.properties
```

Example:

```properties
spring.data.mongodb.uri=

aws.accessKey=

aws.secretKey=

aws.bucketName=

google.client.id=

google.client.secret=

mail.username=

mail.password=
```

---

# Run Backend

```bash
mvn spring-boot:run
```

or

```bash
./mvnw spring-boot:run
```

Backend starts at:

```
http://localhost:8081
```

---

# Frontend Setup

Open another terminal.

Navigate to frontend.

```bash
cd frontend
```

Install dependencies.

```bash
npm install
```

Run Angular.

```bash
ng serve
```

Frontend starts at:

```
http://localhost:4200
```

---

# Login

Open

```
http://localhost:4200
```

Login using your credentials.

---

# API Documentation

Swagger UI

```
http://localhost:8081/swagger-ui/index.html
```

---

# Project Structure

```
AI-Recruiter
│
├── backend
│   ├── controller
│   ├── service
│   ├── repository
│   ├── model
│   ├── dto
│   ├── config
│   ├── util
│   └── resources
│
├── frontend
│   ├── src
│   ├── app
│   ├── assets
│   ├── environments
│   └── styles
│
└── README.md
```

---

# Features

- AI Recruitment
- Resume Management
- Candidate Tracking
- Interview Scheduling
- Offer Letter Generation
- Employee Onboarding
- Employee Dashboard
- HR Dashboard
- Knowledge Transfer (KT)
- Asset Management
- Offboarding
- Alumni Management
- Role-Based Access Control
- Email Notifications
- AWS S3 File Upload
- Google Calendar Integration

---

# Common Commands

Backend

```bash
mvn clean install
```

```bash
mvn spring-boot:run
```

Frontend

```bash
npm install
```

```bash
ng serve
```

Angular Production Build

```bash
ng build
```

---

# Troubleshooting

## MongoDB Connection Error

Ensure MongoDB service is running.

---

## Port Already in Use

Backend:

```
8081
```

Frontend:

```
4200
```

Stop the process using the port or change the port configuration.

---

## npm install Fails

Clear npm cache.

```bash
npm cache clean --force
```

Then run:

```bash
npm install
```

---

## Maven Build Failure

Clean the project.

```bash
mvn clean
```

Then rebuild.

```bash
mvn install
```

---

# Authors

Developed by the AI Recruiter Development Team.
