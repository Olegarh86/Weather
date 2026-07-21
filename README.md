# Weather Tracking Web Application

A web application for tracking weather conditions in user-selected locations. This is a personal pet project designed to gain hands-on experience with core Java Backend development, relational databases, third-party API integration, and manual authentication mechanics.

🔗 **Live Demo:** [http://exchanger-app.ru/weather/users/sign-in](http://exchanger-app.ru/weather/users/sign-in)

---

## Features

### 🔐 User Authentication & Management
* **Registration:** Secure registration with password validation and BCrypt hashing.
* **Authorization:** Manual cookie-based and session-based user authentication.
* **Logout:** Invalidation of active user sessions and secure cleanup of auth cookies.

### 🌤️ Location & Weather Management
* **Search:** Find geographical locations worldwide by name via a third-party weather API.
* **Add Locations:** Save selected locations to a personal tracking list.
* **Dashboard View:** View the personal list of locations displaying the current temperature and location name for each city.
* **Delete Locations:** Remove locations from the tracking list at any time.

---

## Tech Stack

* **Language:** Java 17
* **Framework:** Spring MVC
* **Template Engine:** Thymeleaf
* **Database:** PostgreSQL
* **Database Migrations:** Flyway
* **Testing:** JUnit 5, Mockito, Spring Test (MockMvc)
* **Build Tool:** Maven
* **Application Server:** Apache Tomcat 11
* **Boilerplate:** Lombok

---

## Project Architecture & Learning Highlights

* **Manual Session Management:** Implemented custom session handling and authentication mechanics using Java HTTPServlet cookies and filters/interceptors instead of Spring Security.
* **External API Integration:** Leveraged asynchronous execution (`CompletableFuture`) to fetch real-time weather data from external web services safely and efficiently.
* **Database Transaction Control:** Configured explicit `DataSourceTransactionManager` and transaction isolation to ensure data integrity during user registration and session cleanup processes.

---

## Running the Project Locally

### Prerequisites
Make sure you have the following installed:
* Java 17
* Apache Maven
* Apache Tomcat 11
* PostgreSQL (with a database named `weather_test` for running tests)

### 1. Clone the Repository
```bash
git clone https://github.com/Olegarh86/Weather.git
cd Weather
```

### 2. Configure Environment Properties
Create or edit your environment properties file (e.g., `application-prod.properties` or through Tomcat environment variables) to match your local database credentials:
```properties
db.url=jdbc:postgresql://localhost:5432/your_database_name
db.username=your_username
db.password=your_password
```

### 3. Build the Project
Run the Maven wrapper or your local Maven installation to run tests and compile the application package:
```bash
mvn clean package
```

### 4. Deploy to Tomcat
1. Copy the generated `weather.war` file from the `target/` directory.
2. Paste it into the `webapps/` folder of your local Apache Tomcat server.
3. Start Tomcat. The application will be available at: `http://localhost:8080/weather/users/sign-in`
