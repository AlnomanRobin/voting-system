# 🗳️ Secure Online Voting System

A professional, enterprise-grade online voting system built with **Java Spring Boot** backend and **JavaFX** frontend. Designed with security-first UI/UX principles inspired by government-level systems.

## 🚀 Features

### Security Features
- ✅ BCrypt password hashing
- ✅ JWT-based authentication
- ✅ Session timeout protection
- ✅ Rate limiting on login attempts
- ✅ CAPTCHA verification
- ✅ Audit logging for all actions
- ✅ One vote per voter enforcement
- ✅ Tamper-proof vote recording

### User Features (Voter Panel)
- 📋 Secure voter authentication via National ID/Voter ID
- 👤 Personal dashboard with voting status
- 🗳️ Browse elections and candidates
- ✔️ Vote with confirmation dialog
- 🔒 Vote immutability (cannot change after submission)
- 📊 View results after election closes

### Admin Features
- 📊 Comprehensive admin dashboard
- 👥 Voter management (CRUD operations)
- 🏛️ Election management
- 🎭 Candidate management
- 📈 Real-time voting statistics
- 🔐 Security logs and audit trails
- 📤 Export results to CSV
- 📊 Graphical result display (Bar/Pie charts)

## 🎨 UI/UX Design

### Color Palette (Government-Grade)
- **Primary Navy**: #0A1F44 (Trust & Authority)
- **Royal Blue**: #1F4FD8 (Action Buttons)
- **White**: #FFFFFF (Clean Background)
- **Light Gray**: #F4F6F9 (Cards & Panels)
- **Success Green**: #2ECC71
- **Error Red**: #E74C3C

### Design Principles
- ✨ Clean, minimal, trustworthy interface
- ♿ WCAG accessibility compliant
- 📱 Responsive layouts
- 🎯 Clear visual hierarchy
- ⚡ Fast and responsive
- 🚫 Zero clutter, professional appearance

## 🛠️ Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** (Authentication & Authorization)
- **Spring Data JPA** (Database ORM)
- **MySQL** (Database)
- **JWT** (Token-based authentication)
- **BCrypt** (Password hashing)

### Frontend
- **JavaFX 21** (Modern desktop UI)
- **JFreeChart** (Data visualization)
- **CSS** (Custom styling)

### Build Tools
- **Maven** (Dependency management)

## 📦 Project Structure

```
voting-system/
├── src/main/java/com/votingsystem/
│   ├── config/              # Security & App configuration
│   ├── controller/          # REST API controllers
│   ├── model/              # Entity models
│   ├── repository/         # Data access layer
│   ├── service/            # Business logic
│   ├── security/           # JWT & Security utilities
│   ├── ui/                 # JavaFX UI components
│   │   ├── login/          # Login screen
│   │   ├── voter/          # Voter dashboard
│   │   ├── admin/          # Admin panel
│   │   └── components/     # Reusable UI components
│   └── VotingSystemApplication.java
├── src/main/resources/
│   ├── application.properties
│   ├── fxml/               # JavaFX FXML files
│   ├── css/                # Stylesheets
│   └── images/             # UI assets
└── pom.xml
```

## 🔧 Setup Instructions

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- JavaFX SDK

### Database Setup
1. Install MySQL
2. Create database:
```sql
CREATE DATABASE voting_system;
```
3. Update credentials in `application.properties`

### Running the Application

1. **Clone the repository**
```bash
git clone <repository-url>
cd voting-system
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run Spring Boot backend**
```bash
mvn spring-boot:run
```

4. **Run JavaFX frontend**
```bash
mvn javafx:run
```

## 🔐 Default Credentials

### Admin Account
- **Username**: admin
- **Password**: admin123

### Test Voter Account
- **Voter ID**: V001
- **Password**: voter123

⚠️ **Change these credentials in production!**

## 📊 Database Schema

### Tables
- `users` - Base user authentication
- `voters` - Voter-specific information
- `admins` - Administrator accounts
- `elections` - Election details
- `candidates` - Candidate information
- `votes` - Vote records (encrypted)
- `audit_logs` - Security and action logs

## 🎯 Key Functionalities

### Vote Flow
1. Voter logs in with credentials
2. System validates voter eligibility
3. Display active elections
4. Show candidates with details
5. Voter selects candidate
6. Confirmation dialog appears
7. Vote is securely recorded
8. Success message displayed
9. Vote button disabled (one vote only)

### Security Measures
- Password strength validation
- Failed login attempt tracking
- Session expiration (30 minutes)
- JWT token refresh mechanism
- Database encryption for sensitive data
- Audit trail for all actions
- IP address logging
- Timestamp verification

## 📝 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `POST /api/auth/refresh` - Refresh JWT token

### Voter APIs
- `GET /api/voter/dashboard` - Get voter dashboard
- `GET /api/elections/active` - List active elections
- `POST /api/vote` - Cast a vote
- `GET /api/vote/status` - Check voting status

### Admin APIs
- `GET /api/admin/dashboard` - Admin dashboard stats
- `POST /api/admin/election` - Create election
- `PUT /api/admin/election/{id}` - Update election
- `DELETE /api/admin/election/{id}` - Delete election
- `GET /api/admin/results/{electionId}` - Get results
- `GET /api/admin/logs` - Security audit logs

## ♿ Accessibility Features

- High contrast text for readability
- Keyboard navigation support
- Screen reader compatible labels
- Large clickable buttons (min 44x44px)
- Focus indicators
- ARIA labels for all interactive elements
- Tooltip support for all icons

## 🏆 Professional Highlights

This project demonstrates:
- ✅ Enterprise-level architecture
- ✅ Security-first development approach
- ✅ Clean code principles
- ✅ Modern UI/UX design
- ✅ Scalable system design
- ✅ Professional documentation
- ✅ Government-grade security standards

**Perfect for CV/Portfolio** - Showcases full-stack Java development skills with emphasis on security and user experience.

## 📄 License

This project is developed for educational and portfolio purposes.

## 👨‍💻 Developer

Built with ❤️ using Java, Spring Boot, and JavaFX

---

**Note**: This is a demonstration project. For production deployment, additional security measures, load balancing, and infrastructure considerations are required.
