# Manga Library REST API
A simple REST API for tracking manga built with Spring Boot.

## Overview
This project is a RESTful API built using Spring Boot that provides endpoints for searching for manga, managing user progress and collection, and creating curated lists.

## Prerequisites
- Java 21 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, VS Code, Eclipse, etc.)

## Getting Started
### Clone the repository
```shell
git clone "https://github.com/playerblair/kuro"
cd kuro
```
### Build the application
```shell
mvn clean install
```
### Run the application
```shell
mvn spring-boot:run
```
The application will be available at: http://localhost:8080

## API Endpoints
### MangaController
| Method | URL                           | Description                                          |
|--------|-------------------------------|------------------------------------------------------|
| GET    | /api/manga/search             | Search external API for manga.                       |
| GET    | /api/manga/{malId}}           | Retrieves manga from external API via malID.         |

### LibraryController

| Method | URL                        | Description                              |
|--------|----------------------------|------------------------------------------|
| GET    | /api/manga/library         | Retrieves all manga entries.             |
| GET    | /api/manga/library/{malId} | Retrieves specific MangaEntry via malID. |
| POST   | /api/manga/library         | Create a new MangaEntry.                 |
| PUT    | /api/manga/library/{malId} | Updates specific MangaEntry via malID.   |
| DELETE | /api/manga/library/{malId} | Deletes specific MangaEntry via malID.   |

### CollectionEntryController

| Method | URL                           | Description                                          |
|--------|-------------------------------|------------------------------------------------------|
| GET    | /api/manga/collection         | Retrieves all collection entries.                    |
| GET    | /api/manga/collection/{id}    | Retrieves specific CollectionEntry via ID.           |
| GET    | /api/manga/{malId}/collection | Retrieves all collection entries for specific Manga. |
| POST   | /api/manga/collection         | Create a new CollectionEntry.                        |
| PUT    | /api/manga/collection/{id}    | Updates specific CollectionEntry via ID.             |
| DELETE | /api/manga/collection/{id}    | Deletes specific CollectionEntry via ID.             | |



### MangaListController

| Method | URL                   | Description                           |
|--------|-----------------------|---------------------------------------|
| GET    | /api/manga/lists      | Retrieves all manga lists.            |
| GET    | /api/manga/lists/{id} | Retrieves specific MangaEntry via ID. |
| POST   | /api/manga/lists      | Create a new MangaList.               |
| PUT    | /api/manga/lists/{id} | Updates specific MangaList via ID.    |
| DELETE | /api/manga/lists/{id} | Deletes specific MangaList via ID.    |

## Configuration
The application can be configured through the `application.yml` file:
```yaml
# Server port
server:
  port: 8008
```