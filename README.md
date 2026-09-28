# LibroTrack — Library Book Issue and Return Manager

Complete Spring Boot + MySQL library project with REST API and a built-in HTML/CSS/JavaScript UI.

## Requirements
- Java 21
- MySQL 8.x
- VS Code
- MySQL Workbench (recommended)

## 1. MySQL password
Open `src/main/resources/application.properties` and change this line if needed:

```properties
spring.datasource.password=root
```

The default database is `librotrack` and it is created automatically.

## 2. Run the project in VS Code
Open the terminal in the `Libro-Track` folder and run:

```powershell
mvnw.cmd spring-boot:run
```

If your terminal does not recognize it, use:

```powershell
.\mvnw.cmd spring-boot:run
```

You need MySQL Server running before starting Spring Boot.

## 3. Open the website

After the application starts, open:

http://localhost:8080/

The UI supports dashboard, books, students, issue, return, search and automatic fine calculation.

## 4. MySQL Workbench

You can optionally run `sql/librotrack_setup.sql`. It creates the database and inserts sample books/students. Spring Boot creates/updates the tables using JPA.

## 5. Postman

Import `postman_collection.json` into Postman.

Main endpoints:

- POST `/api/books`
- GET `/api/books`
- GET `/api/books?keyword=java`
- PUT `/api/books/{id}`
- DELETE `/api/books/{id}`
- POST `/api/students`
- GET `/api/students`
- PUT `/api/students/{id}`
- DELETE `/api/students/{id}`
- POST `/api/issues`
- GET `/api/issues`
- GET `/api/issues/student/{studentId}?activeOnly=true`
- PUT `/api/issues/{issueId}/return`
- GET `/api/dashboard`

## Business rules
1. A book cannot be issued if all copies are checked out.
2. Due date is automatically 14 days after issue date.
3. Fine is ₹5 for each late day.
4. A returned issue cannot be returned again.
5. A book cannot be deleted while it has an active issue.
6. A student cannot be deleted while they have an active issue.
7. ISBN and student email are unique.
8. Invalid requests return clear JSON errors through the global exception handler.

## Folder structure

```text
Libro-Track/
├── pom.xml
├── README.md
├── postman_collection.json
├── sql/
│   └── librotrack_setup.sql
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/maven-wrapper.properties
└── src/
    ├── main/java/com/example/Libro/Track/
    │   ├── LibroTrackApplication.java
    │   ├── controller/
    │   ├── entity/
    │   ├── dto/
    │   ├── repository/
    │   ├── service/
    │   └── exception/
    └── main/resources/
        ├── application.properties
        └── static/
            ├── index.html
            ├── style.css
            └── app.js
```
