# Bookstore API
Welcome to the Bookstore project! This project is inspired by the implementation of an application for an online bookstore.

## Table of Contents
- [Technologies Used](#technologies-used)
- [Domain Model](#domain-model)
- [User Roles](#user-roles)
- [User Actions](#user-actions)
  - [For Shoppers](#for-shoppers)
  - [For Managers](#for-managers)
- [Project Structure](#project-structure)
- [Running the Project](#running-the-project)
- [Additional Information](#additional-information)

## Technologies Used
- Spring Boot
- Spring Security
- Spring Web
- Spring Data JPA
- Maven
- Docker
-	Lombok
-	MySQL
-	Liquibase
-	Mapstruct
- Swagger.

## Domain Model
- User: Contains information about the registered user including their authentication details and personal information.
- Role: Represents the role of a user in the system, for example, admin or user.
- Book: Represents a book available in the store.
- Category: Represents a category that a book can belong to.
- ShoppingCart: Represents a user's shopping cart.
- CartItem: Represents an item in a user's shopping cart.
- Order: Represents an order placed by a user.
- OrderItem: Represents an item in a user's order.

### User Roles
1. Shopper (User): Someone who looks at books, puts them in a basket (shopping cart), and buys them.
2. Manager (Admin): Someone who arranges the books on the shelf and watches what gets bought.

### User Actions
#### For Shoppers:
| Action                                      | Description                                                                  |
|---------------------------------------------|------------------------------------------------------------------------------|
| Join and sign in:                           | Join the store. Sign in to look at books and buy them.                      |
| Look at and search for books:               | Look at all the books. Look closely at one book. Find a book by typing its name. |
| Look at bookshelf sections:                 | See all bookshelf sections. See all books in one section.                   |
| Use the basket:                             | Put a book in the basket. Look inside the basket. Take a book out of the basket. |
| Buying books:                               | Buy all the books in the basket. Look at past receipts.                     |
| Look at receipts:                           | See all books on one receipt. Look closely at one book on a receipt.        |
#### For Managers

| Action                                      | Description                                                                  |
|---------------------------------------------|------------------------------------------------------------------------------|
| Arrange books:                              | Add a new book to the store. Change details of a book. Remove a book from the store. |
| Organize bookshelf sections:                | Make a new bookshelf section. Change details of a section. Remove a section. |
| Look at and change receipts:               | Change the status of a receipt, like "Shipped" or "Delivered".               |

## Project Structure
```plaintext
src/main/java/mate/academy/springboot
├── config
├── controller
├── dto
├── exeption
├── mapper
├── model
├── repository
├── security
├── service
└── validation

src/main/resources
├── db.changelog
 ├──changes
 └──db.changelog-master.yaml
├── application.properties
└── liquibase.properties

src/test/java/mate/academy/springboot
├── config
├── controller
├── repository
└── service

src/test/resources
├── database
└── application.properties
```

# Running the Project
1. Clone the repository to your computer.
2. Open the project in IntelliJ IDEA.
3. Use Maven to build the project.
4. Database Setup:

Open the application.properties file in the root directory of the project.
```plaintext
//Replace with your own database settings
 spring.datasource.url=jdbc:mysql://localhost:3306/database_name
 spring.datasource.username=your_name
 spring.datasource.password=your_password
 spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
Before running the project, make sure you have created a database in your MySQL server that matches the specified database name in the configuration file.

5. Run the application.

## Additional Information
In this section, you can find additional resources and guidance for working with the project:
### API Documentation
This project uses Swagger for API documentation. Access the documentation [here](http://localhost:8080/swagger-ui/index.html#).

**You can view the endpoints and test the application.
To do this, launch the program and open the link**

### Docker
The project is Dockerized for easy deployment. Build the Docker container using the following commands:
```plaintext
 docker build -t posts-service .
 docker run -p 8081:8080 posts-service
```
## Running Tests
Ensure that the project is built and use Maven to run the tests:
```plaintext
 mvn test
```

#### Known Issues
During the writing process, I faced a problem with launching Swagger using Docker. 
The issue arose from incorrectly specifying the ports. 
**Hence, I advise paying close attention to this aspect.**
