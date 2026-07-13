# AI Recruiter & Employee Lifecycle Management System

## Project Overview

The AI Recruiter & Employee Lifecycle Management System is a web-based application that automates the complete employee lifecycle, from recruitment to offboarding. The system enables HR teams, recruiters, managers, administrators, and employees to manage recruitment, onboarding, employee records, knowledge transfer, asset management, and offboarding through a centralized platform.

---

## Features

- Secure Authentication and Authorization
- Role-Based Access Control
- Candidate Management
- Resume Upload and Management
- Interview Scheduling
- Offer Letter Generation
- Employee Onboarding
- Employee Management
- Knowledge Transfer (KT)
- Asset Management
- Offboarding
- Email Notifications
- Dashboard and Reports

---

## Technology Stack

### Frontend

- Angular
- TypeScript
- HTML5
- CSS3

### Backend

- Java
- Spring Boot
- Spring Security
- Maven

### Database

- MongoDB

### Cloud and Integrations

- AWS S3
- Google Calendar API
- Java Mail Sender

---

## Repository Information

### Frontend Repository

Repository: https://github.com/Vidyasagar18snc/New-UI-Code

Branch: feature/frontend-setup

### Backend Repository

Repository: https://github.com/Vidyasagar18snc/New-Version-Ai-Recruiteer

Branch: feature/project-setup

---

## Prerequisites

Install the following software before setting up the project.

- Git
- Java JDK 17 or later
- Maven
- Node.js 18 or later
- npm
- Angular CLI
- MongoDB
- Visual Studio Code or IntelliJ IDEA

Verify the installation.

bash
java -version
mvn -version
node -v
npm -v
ng version
git --version


---

## Clone the Repository

Clone the backend repository.

bash
git clone https://github.com/Vidyasagar18snc/New-Version-Ai-Recruiteer.git
cd New-Version-Ai-Recruiteer
git checkout feature/project-setup


Clone the frontend repository.

bash
git clone https://github.com/Vidyasagar18snc/New-UI-Code.git
cd New-UI-Code
git checkout feature/frontend-setup


---

## Install Dependencies

Backend

bash
mvn clean install


Frontend

```bash
npm install
```

---

## Configuration

### MongoDB

Update the MongoDB connection in the `application.properties` file.

properties
spring.data.mongodb.uri=<MongoDB Connection String>


### AWS S3

Configure the following values.

- AWS Access Key
- AWS Secret Key
- S3 Bucket Name
- AWS Region

### Google Calendar API

Configure the following values.

- Google Client ID
- Google Client Secret

- ---

## Google Cloud Authorization

When running the backend for the first time, the application requires authorization to access Google services (such as Google Calendar).

1. Start the backend application.
2. The application will generate a Google authorization URL in the console.
3. Copy the generated URL.
4. Send the authorization URL to the project maintainer.
5. Open the URL in a web browser and sign in with the authorized Google account.
6. Grant the requested permissions.
7. After successful authorization, the application will store the required credentials locally, and subsequent runs will not require authorization unless the credentials are removed or expire.

**Note:** If you are setting up the project for the first time, you must send the generated authorization link to the project maintainer so they can complete the Google Cloud authorization process.

---

### Email Configuration

Configure the SMTP details in the `application.properties` file.

---

## Run the Backend

bash
mvn spring-boot:run


Backend URL: http://localhost:8082

---

## Run the Frontend

bash
ng serve


or

bash
ng s

Frontend URL: http://localhost:4200

---

## Swagger API

Swagger URL: http://localhost:8082/swagger-ui/index.html

---

## Project Structure

Backend

src
├── controller
├── service
├── repository
├── model
├── dto
├── config
└── resources
```

Frontend


src
├── app
├── assets
├── environments
├── shared
├── styles
└── main.ts


---

## Common Commands

Backend

bash
mvn clean install
mvn spring-boot:run
mvn test


Frontend

bash
npm install
ng serve
ng build
ng test

---

## Troubleshooting

### MongoDB Connection Error

- Ensure MongoDB is running.
- Verify the MongoDB connection string.

### Port Already in Use

- Backend Port: 8081
- Frontend Port: 4200

Stop the existing process or change the application port.

### Dependency Installation Failed

Backend

```bash
mvn clean install
```

Frontend

```bash
npm install
```

---

## Contributing

1. Create a new feature branch.
2. Implement the required changes.
3. Commit the changes.
4. Push the branch to the repository.
5. Create a Pull Request.

---

## Contact

For project setup, development, or deployment issues, contact the project maintainer or the development team.

---

## License

This project is intended for internal company use only.
