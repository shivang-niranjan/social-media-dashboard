# Social Media Dashboard with Authentication

## Overview
This project is a simple Social Media Dashboard with user authentication built using HTML, CSS, JavaScript for the frontend, and Java for the backend.

## Project Structure
- `web/` - Contains frontend files (HTML, CSS, JavaScript)
- `src/` - Contains Java backend source code

## Features
- Login page with username and password authentication
- Dashboard page accessible after successful login
- Basic styling and client-side interactivity

## Prerequisites
- Java Development Kit (JDK) 11 or higher

## How to Run
1. Compile the Java backend:
   ```
   javac -d out src/com/dashboard/*.java
   ```
   Or if your shell does not support this glob pattern, compile all Java files in src:
   ```
   javac -d out src/**/*.java

   javac -d out src/SocialMediaApiClient.java src/SocialMediaDashboard.java src/SocialMediaDashboardHandlers.java
   ```

2. Run the Java backend:
   ```
   java -cp out com.dashboard.SocialMediaDashboard
   ```

3. Open your browser and navigate to:
   ```
   http://localhost:8000/
   ```

4. Login with the following credentials:
   - Username: `admin`
   - Password: `password`

## Notes
- This is a simple demonstration with hardcoded credentials.
- For production use, consider using a proper web framework and secure authentication methods.
