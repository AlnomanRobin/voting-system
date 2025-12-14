# 🎯 API Documentation - Secure Voting System

## Base URL
```
http://localhost:8080/api
```

## Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## 🔐 Authentication Endpoints

### 1. Login
**POST** `/auth/login`

Login to the system and receive a JWT token.

**Request Body:**
```json
{
  "username": "voter001",
  "password": "voter123"
}
```

**Response (Success):**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "VOTER",
  "username": "voter001",
  "message": "Login successful"
}
```

**Response (Failed):**
```json
{
  "success": false,
  "message": "Invalid credentials"
}
```

---

### 2. Logout
**POST** `/auth/logout`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": "true",
  "message": "Logged out successfully"
}
```

---

### 3. Register Voter
**POST** `/auth/register`

Register a new voter account.

**Request Body:**
```json
{
  "voterId": "V002",
  "nationalId": "N87654321",
  "fullName": "Jane Smith",
  "username": "voter002",
  "password": "SecurePass123!",
  "dateOfBirth": "1995-05-20",
  "gender": "Female",
  "address": "456 Oak Avenue"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Registration successful. Awaiting verification."
}
```

---

## 🗳️ Voter Endpoints

All voter endpoints require `ROLE_VOTER` authentication.

### 1. Get Voter Dashboard
**GET** `/voter/dashboard`

Get voter information and voting status.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "voterName": "John Doe Voter",
  "voterId": "V001",
  "verified": true,
  "status": "VERIFIED",
  "activeElections": [
    {
      "id": 1,
      "name": "Presidential Election 2024",
      "description": "National Presidential Election",
      "startTime": "2024-12-15T08:00:00",
      "endTime": "2024-12-15T20:00:00",
      "status": "ACTIVE"
    }
  ],
  "votingStatus": [
    {
      "electionId": 1,
      "electionName": "Presidential Election 2024",
      "hasVoted": false
    }
  ]
}
```

---

### 2. Get Active Elections
**GET** `/voter/elections/active`

Retrieve all currently active elections.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Presidential Election 2024",
    "description": "National Presidential Election",
    "type": "PRESIDENTIAL",
    "startTime": "2024-12-15T08:00:00",
    "endTime": "2024-12-15T20:00:00",
    "status": "ACTIVE"
  }
]
```

---

### 3. Get Election Candidates
**GET** `/voter/elections/{electionId}/candidates`

Get all candidates for a specific election.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Alice Johnson",
    "partyName": "Progressive Party",
    "partySymbol": "Rising Sun",
    "biography": "Experienced politician...",
    "manifesto": "Focus on education and healthcare...",
    "active": true
  },
  {
    "id": 2,
    "name": "Bob Smith",
    "partyName": "Conservative Alliance",
    "partySymbol": "Mountain Peak",
    "biography": "Business leader...",
    "active": true
  }
]
```

---

### 4. Cast Vote
**POST** `/voter/vote`

Cast a vote for a candidate in an election.

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "electionId": 1,
  "candidateId": 1
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Your vote has been recorded securely",
  "votedAt": "2024-12-15T10:30:45"
}
```

**Response (Already Voted):**
```json
{
  "success": false,
  "message": "You have already voted in this election"
}
```

---

### 5. Check Vote Status
**GET** `/voter/vote/status/{electionId}`

Check if you have already voted in a specific election.

**Headers:**
```
Authorization: Bearer <token>
```

**Response:**
```json
{
  "hasVoted": true,
  "electionId": 1
}
```

---

## 🛡️ Admin Endpoints

All admin endpoints require `ROLE_ADMIN` authentication.

### 1. Get Admin Dashboard
**GET** `/admin/dashboard`

Get system-wide statistics.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "totalVoters": 1250,
  "verifiedVoters": 1100,
  "pendingVoters": 150,
  "totalElections": 5,
  "activeElections": 2,
  "totalVotes": 3456,
  "suspiciousActivities": 3
}
```

---

### 2. Get All Elections
**GET** `/admin/elections`

Retrieve all elections in the system.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Presidential Election 2024",
    "type": "PRESIDENTIAL",
    "status": "ACTIVE",
    "startTime": "2024-12-15T08:00:00",
    "endTime": "2024-12-15T20:00:00",
    "resultsPublished": false
  }
]
```

---

### 3. Create Election
**POST** `/admin/elections`

Create a new election.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Request Body:**
```json
{
  "name": "Local Council Election 2024",
  "description": "Election for local council members",
  "type": "LOCAL",
  "startTime": "2024-12-20T08:00:00",
  "endTime": "2024-12-20T18:00:00"
}
```

**Response:**
```json
{
  "id": 2,
  "name": "Local Council Election 2024",
  "type": "LOCAL",
  "status": "SCHEDULED",
  "startTime": "2024-12-20T08:00:00",
  "endTime": "2024-12-20T18:00:00"
}
```

---

### 4. Update Election
**PUT** `/admin/elections/{id}`

Update an existing election.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Request Body:**
```json
{
  "name": "Updated Election Name",
  "description": "Updated description",
  "startTime": "2024-12-21T08:00:00",
  "endTime": "2024-12-21T18:00:00"
}
```

---

### 5. Delete Election
**DELETE** `/admin/elections/{id}`

Delete an election (cannot delete if votes exist).

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "message": "Election deleted successfully"
}
```

---

### 6. Add Candidate
**POST** `/admin/candidates`

Add a candidate to an election.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Request Body:**
```json
{
  "name": "David Brown",
  "partyName": "Independent",
  "partySymbol": "Star",
  "biography": "Independent candidate with focus on local issues",
  "manifesto": "Community first approach",
  "election": {
    "id": 1
  },
  "displayOrder": 4
}
```

---

### 7. Get All Voters
**GET** `/admin/voters`

Get list of all registered voters.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
[
  {
    "id": 1,
    "voterId": "V001",
    "nationalId": "N12345678",
    "fullName": "John Doe Voter",
    "verified": true,
    "status": "VERIFIED",
    "eligible": true
  }
]
```

---

### 8. Verify Voter
**POST** `/admin/voters/{id}/verify`

Verify a pending voter account.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "message": "Voter verified successfully"
}
```

---

### 9. Suspend Voter
**POST** `/admin/voters/{id}/suspend`

Suspend a voter account.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "message": "Voter suspended successfully"
}
```

---

### 10. Get Election Results
**GET** `/admin/results/{electionId}`

Get vote results for a specific election.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "election": {
    "id": 1,
    "name": "Presidential Election 2024"
  },
  "totalVotes": 950,
  "candidates": [
    {
      "id": 1,
      "name": "Alice Johnson",
      "party": "Progressive Party",
      "votes": 450
    },
    {
      "id": 2,
      "name": "Bob Smith",
      "party": "Conservative Alliance",
      "votes": 350
    },
    {
      "id": 3,
      "name": "Carol Williams",
      "party": "People's Coalition",
      "votes": 150
    }
  ]
}
```

---

### 11. Publish Results
**POST** `/admin/elections/{id}/publish-results`

Publish the results of a completed election.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "message": "Results published successfully"
}
```

---

### 12. Get Audit Logs
**GET** `/admin/logs?page=0&size=20`

Get security audit logs with pagination.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Items per page (default: 20)

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "action": "Successful login",
      "actionType": "LOGIN",
      "severity": "INFO",
      "ipAddress": "127.0.0.1",
      "timestamp": "2024-12-15T10:30:00",
      "success": true
    }
  ],
  "totalElements": 100,
  "totalPages": 5,
  "number": 0,
  "size": 20
}
```

---

### 13. Get Critical Logs
**GET** `/admin/logs/critical`

Get only critical and error-level logs.

**Headers:**
```
Authorization: Bearer <admin-token>
```

**Response:**
```json
[
  {
    "id": 45,
    "action": "Account locked due to failed attempts",
    "actionType": "ACCOUNT_LOCKED",
    "severity": "CRITICAL",
    "ipAddress": "192.168.1.100",
    "timestamp": "2024-12-15T09:15:00"
  }
]
```

---

## 📊 Error Responses

### 401 Unauthorized
```json
{
  "timestamp": "2024-12-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid token or expired session"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-12-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied"
}
```

### 400 Bad Request
```json
{
  "success": false,
  "message": "Invalid request data"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-12-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## 🔒 Security Notes

1. **JWT Token Expiration:** Tokens expire after 24 hours
2. **Rate Limiting:** Login attempts limited to 5 per account
3. **Account Locking:** Automatic after 5 failed attempts
4. **Session Timeout:** 30 minutes of inactivity
5. **HTTPS Required:** Production deployment must use HTTPS
6. **CORS:** Configure allowed origins in production

---

## 📝 Testing with cURL

### Login Example:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"voter001","password":"voter123"}'
```

### Cast Vote Example:
```bash
curl -X POST http://localhost:8080/api/voter/vote \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{"electionId":1,"candidateId":1}'
```

### Get Dashboard Example:
```bash
curl -X GET http://localhost:8080/api/voter/dashboard \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🧪 Testing with Postman

1. Import the collection from this API documentation
2. Set up environment variable for `token`
3. Login to get token automatically
4. Use token in subsequent requests

**Base URL Variable:** `{{baseUrl}}` = `http://localhost:8080/api`
