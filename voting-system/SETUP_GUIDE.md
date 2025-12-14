# 🚀 Quick Start Guide - Secure Online Voting System

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17 or higher** ([Download](https://adoptium.net/))
- **MySQL 8.0+** ([Download](https://dev.mysql.com/downloads/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **JavaFX SDK 21** (included in Maven dependencies)

## Step 1: Database Setup

### 1.1 Install MySQL
```bash
# For Windows: Use MySQL Installer
# For Linux: 
sudo apt-get install mysql-server

# For macOS:
brew install mysql
```

### 1.2 Start MySQL Service
```bash
# Windows
net start MySQL80

# Linux
sudo systemctl start mysql

# macOS
brew services start mysql
```

### 1.3 Create Database
```bash
mysql -u root -p
```

Then run:
```sql
CREATE DATABASE voting_system;
```

Or simply run the schema file:
```bash
mysql -u root -p < src/main/resources/schema.sql
```

### 1.4 Configure Database Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/voting_system
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD_HERE
```

## Step 2: Build the Project

```bash
# Navigate to project directory
cd voting-system

# Clean and build
mvn clean install
```

## Step 3: Run the Application

### Option 1: Run with Maven
```bash
mvn spring-boot:run
```

### Option 2: Run with JavaFX Maven Plugin
```bash
mvn javafx:run
```

### Option 3: Run JAR file
```bash
# Build JAR
mvn package

# Run JAR
java -jar target/secure-voting-system-1.0.0.jar
```

## Step 4: Login to the System

### Default Credentials

#### Admin Login:
- **Username:** `admin`
- **Password:** `admin123`

#### Test Voter Login:
- **Voter ID:** `V001`
- **Password:** `voter123`

## 🎯 Features Walkthrough

### For Voters:

1. **Login** with your Voter ID and password
2. **View Dashboard** showing your verification status
3. **Browse Active Elections** and view candidates
4. **Cast Your Vote** with a confirmation dialog
5. **Secure Voting** - vote is recorded with encryption
6. **One Vote Only** - system prevents double voting

### For Administrators:

1. **Login** as admin
2. **Dashboard** - view system statistics
3. **Manage Elections** - create, update, delete elections
4. **Manage Candidates** - add candidates to elections
5. **Manage Voters** - verify or suspend voter accounts
6. **View Results** - see vote counts and percentages
7. **Security Logs** - monitor all system activities

## 📊 Testing the System

### Test Scenario 1: Voter Registration & Verification

1. Create a new voter via admin panel
2. Verify the voter
3. Voter can now login and vote

### Test Scenario 2: Voting Process

1. Login as verified voter
2. View active elections
3. Select a candidate
4. Confirm vote
5. Verify vote is recorded (dashboard shows "Already Voted")

### Test Scenario 3: Admin Operations

1. Login as admin
2. Create a new election
3. Add candidates to the election
4. Set election start/end times
5. Publish results after election ends

## 🔧 Troubleshooting

### Issue: Database Connection Failed

**Solution:**
- Verify MySQL is running: `systemctl status mysql`
- Check credentials in `application.properties`
- Ensure database `voting_system` exists

### Issue: JavaFX not loading

**Solution:**
- Verify Java 17+ is installed: `java -version`
- Ensure JavaFX dependencies are in Maven
- Try running with: `mvn clean javafx:run`

### Issue: Port 8080 already in use

**Solution:**
Edit `application.properties`:
```properties
server.port=8081
```

### Issue: Login fails with correct credentials

**Solution:**
- Check audit logs table for error details
- Verify BCrypt password hashing
- Ensure user is not locked: Check `account_locked` field

## 📁 Project Structure

```
voting-system/
├── src/main/java/com/votingsystem/
│   ├── config/              # Security & configuration
│   ├── controller/          # REST API endpoints
│   ├── model/              # Database entities
│   ├── repository/         # Data access layer
│   ├── security/           # JWT & encryption
│   ├── service/            # Business logic
│   ├── ui/                 # JavaFX UI components
│   └── VotingSystemApplication.java
├── src/main/resources/
│   ├── application.properties
│   ├── schema.sql
│   └── css/
│       └── voting-system.css
├── pom.xml
└── README.md
```

## 🔐 Security Features

✅ **Password Encryption** - BCrypt with 12 rounds  
✅ **JWT Authentication** - Secure token-based auth  
✅ **Session Management** - 30-minute timeout  
✅ **Account Locking** - After 5 failed attempts  
✅ **Vote Encryption** - SHA-256 hashing  
✅ **Audit Logging** - All actions tracked  
✅ **IP Address Logging** - Security monitoring  
✅ **One Vote Per Voter** - Database constraints  

## 📞 Support

For issues or questions:

1. Check the main [README.md](README.md)
2. Review audit logs for errors
3. Check MySQL error logs
4. Verify all prerequisites are installed

## 🎓 Learning Resources

- **Spring Boot:** https://spring.io/projects/spring-boot
- **JavaFX:** https://openjfx.io/
- **Spring Security:** https://spring.io/projects/spring-security
- **JWT:** https://jwt.io/

## 📝 Next Steps

1. ✅ Complete initial setup
2. 🔄 Test all features
3. 📊 Review security logs
4. 🎨 Customize UI if needed
5. 🚀 Deploy to production (with additional security measures)

---

**Note:** This is a demonstration/educational project. For production use, additional security measures, load balancing, and infrastructure considerations are required.

## 🏆 Project Highlights for CV

> "Developed a government-grade secure online voting system using Java Spring Boot and JavaFX, featuring:
> - Enterprise-level security with JWT authentication and BCrypt encryption
> - Professional UI/UX design following government accessibility standards
> - Comprehensive audit logging and security monitoring
> - RESTful API architecture with role-based access control
> - Responsive JavaFX interface with modern design patterns
> - Complete CRUD operations for elections, candidates, and voters
> - Real-time vote tracking and result visualization"

**Perfect for:** Java Developer, Full-Stack Developer, Security Engineer positions
