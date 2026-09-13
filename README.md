# 🎓 Campus Placement Management System

A simple web-based **Campus Placement Management System** built using **Java, Jakarta Servlets, JDBC, MySQL, HTML, and CSS**.

---

## 🎯 Aim

To develop a simple and centralized web-based platform for managing campus placement activities.

## 🎯 Objective

- Provide students with an easy way to explore available placement opportunities and apply for jobs.
- Allow administrators to manage job openings and track student applications.
- Demonstrate practical integration of Java, Servlets, JDBC, and MySQL in a full-stack web application.

---

## ⚙️ Main Working

The application follows this basic architecture:

```text
HTML + CSS
     ↓
Java Servlets
     ↓
JDBC
     ↓
MySQL
```

**Student Flow:** Register → Login → Browse Jobs → Apply → Track Application Status

**Admin Flow:** Register → Login → Add Job → View Applications → Update Application Status

Application statuses: `Applied` → `Shortlisted` → `Selected` / `Rejected`

The system uses a MySQL database (`placement_management`) with four tables: `students`, `admins`, `jobs`, and `applications` (linked via foreign keys).

---

## 🚀 How to Set Up

### Prerequisites

- Java JDK 25
- Apache Maven 3.9+
- MySQL Server 8.0+
- Apache Tomcat 10.1

### Step 1: Start MySQL

Ensure MySQL Server is running on `localhost:3306`.

### Step 2: Create the Database

```sql
CREATE DATABASE IF NOT EXISTS placement_management;
USE placement_management;

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS jobs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    company_name VARCHAR(100) NOT NULL,
    job_title VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    eligibility VARCHAR(255),
    salary VARCHAR(100),
    description TEXT
);

CREATE TABLE IF NOT EXISTS applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    job_id INT NOT NULL,
    status VARCHAR(50) DEFAULT 'Applied',
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (job_id) REFERENCES jobs(id)
);
```

### Step 3: Configure MySQL Credentials

Open `src/main/java/util/DBConnection.java` and update:

```java
private static final String URL =
        "jdbc:mysql://localhost:3306/placement_management";
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";
```

### Step 4: Build the Project

From the project root:

```powershell
mvn clean package
```

This generates a `.war` file inside `target/`.

### Step 5: Deploy to Tomcat

Copy the `.war` file into the `webapps` folder of your Tomcat installation:

```text
apache-tomcat-10.1.59/webapps/
```

### Step 6: Start Tomcat

Run `startup.bat` from the Tomcat `bin` folder.

### Step 7: Open the Application

Visit:

```text
http://localhost:8080/placement-management-system-1.0-SNAPSHOT/
```

### Stopping the Application

Run `shutdown.bat` from the Tomcat `bin` folder. Stored data remains in MySQL and does not need to be reset.
