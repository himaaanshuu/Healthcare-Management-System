# Hospital Management System

A comprehensive Java GUI-based application for managing hospital operations including patients, doctors, and appointments using a PostgreSQL database.

## Features
- **Patient Management** – Add, view, update, and delete patients  
- **Doctor Management** – Manage doctors, specializations, and availability  
- **Appointment Scheduling** – Book, reschedule, and view appointments  
- **Medical Records** – Store diagnoses, prescriptions, and consultation fees  
- **PostgreSQL Database** – Reliable persistence with relationships  

## Technologies Used
- Java Swing (GUI)  
- PostgreSQL  
- JDBC  
- MVC Architecture  
- DAO Pattern  

## Prerequisites
- Java JDK 8 or higher  
- PostgreSQL 12 or higher  
- PostgreSQL JDBC Driver  
- Any IDE (NetBeans, IntelliJ, Eclipse) – optional  

---

# Quick Setup Guide

## 1. Install PostgreSQL
Download and install from:  
https://www.postgresql.org/download/

---

## 2. Create Database
Using **pgAdmin**:

1. Open pgAdmin  
2. Right-click **Databases → Create → Database**  
3. Set name: `hospital_management`  
4. Right-click the database → **Query Tool**  
5. Run the SQL file located at:  
   `database/schema_postgresql.sql`  
   (Creates tables + inserts sample data)

---

## 3. Download JDBC Driver
Download PostgreSQL JDBC Driver:  
https://jdbc.postgresql.org/download.html

Save it in your project folder:  
`/lib/postgresql-42.6.0.jar`

---

## 4. Configure Database Connection
Open:

`src/model/DatabaseConnection.java`

Update with your PostgreSQL credentials:

```
private static final String URL = "jdbc:postgresql://localhost:5432/hospital_management";
private static final String USERNAME = "postgres";
private static final String PASSWORD = "your_password";
```

---

## 5. Run the Application

Double-click the script:

```
build-and-run.sh
```

This script will automatically:
- Compile all Java files  
- Run the project  
- Launch the **Hospital Management System GUI**

If double-clicking doesn't work, run manually using terminal:

```
sh build-and-run.sh
```

---
