# JobHunter Backend Overview

This document describes the backend folder structure and the main responsibilities of each file so a reviewer can understand the code before reading it.

## Backend structure

- `pom.xml`
  - Maven build file for the backend WAR.
  - Defines Java 17, Jakarta Servlet API, MySQL Connector/J, Gson, and WAR packaging.

- `src/main/java/com/jobhunter/controller/`
  - Contains all servlet classes that receive HTTP requests and call DAO classes.
  - Each servlet produces JSON responses.
  - Servlets and URL mappings:
    - `RegisterServlet.java` → `/register`
    - `LoginServlet.java` → `/login`
    - `LogoutServlet.java` → `/logout`
    - `JobServlet.java` → `/jobs`
    - `CompanyServlet.java` → `/company`
    - `ApplyServlet.java` → `/apply`
    - `ResumeUploadServlet.java` → `/resume/upload`
    - `SavedJobServlet.java` → `/saved`

- `src/main/java/com/jobhunter/dao/`
  - Contains data access objects for database operations.
  - Each DAO uses JDBC, `PreparedStatement`, and `DBConnection.getConnection()`.
  - DAOs:
    - `UserDAO.java` — user registration, login, lookup, update, delete, and email checks.
    - `CompanyDAO.java` — company registration, login, lookup, update, delete, search.
    - `JobDAO.java` — create jobs, read jobs, search jobs, update jobs, delete jobs.
    - `ApplicationDAO.java` — apply for jobs, read applications, update status, delete application.
    - `ResumeDAO.java` — save resume metadata, read user resumes, delete resumes.
    - `SavedJobDAO.java` — save jobs, remove saved jobs, list saved jobs, check saved status.

- `src/main/java/com/jobhunter/model/`
  - Data models representing database rows.
  - Classes:
    - `User.java`
    - `Company.java`
    - `Job.java`
    - `Resume.java`
    - `Application.java`
    - `SavedJob.java`
    - `Skill.java`
  - Models are simple Java beans with getters and setters.

- `src/main/java/com/jobhunter/util/`
  - Utility classes for database configuration.
  - `DBConnection.java` — loads `db.properties`, validates connection properties, and returns JDBC connections.
  - `DBPropertiesLoader.java` — helper that loads properties from `db.properties`.

- `src/main/resources/`
  - `db.properties` — database connection settings used at runtime.
  - `schema.sql` — MySQL database schema and sample data for the JobHunter schema.

- `src/main/webapp/`
  - Frontend resources and JSP/HTML files for the webapp.
  - Not covered in this backend overview document.

## How the backend works

1. A servlet receives an HTTP request.
2. The servlet reads request parameters and session data.
3. The servlet calls a DAO method to perform database operations.
4. The DAO uses `DBConnection.getConnection()` to open a JDBC connection.
5. The DAO executes SQL using prepared statements and returns model objects.
6. The servlet writes a JSON response using Gson.

## Deployment requirements

- Java 17
- Apache Tomcat 10 or any Jakarta Servlet 6 compatible server
- MySQL database configured in `src/main/resources/db.properties`
- The `jobhunter` database should be created using `schema.sql` or a matching schema

## Build commands

From the `backend/` folder:

```bash
mvn clean package
```

This produces `target/JobHunter.war` for deployment.

## Notes for reviewers

- Servlet URL mappings are annotation-based, so no `web.xml` file is required.
- The backend is intentionally simple: servlets + JDBC + DAOs + models.
- Most business logic is in the DAOs and servlets.
- The current backend is ready to deploy as a WAR after database configuration is provided.
