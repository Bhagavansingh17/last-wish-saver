# Last Wish Saver

A secure digital-legacy platform for storing private wishes and selectively releasing them to verified beneficiaries.

## Tech stack
Java 21, Spring Boot 3.3.2, Spring Security, JWT, Spring Data JPA/Hibernate, MySQL, Maven, REST APIs.

## Core design
User -> Wish -> WishAccess <- Beneficiary
Beneficiary -> VerificationRequest -> Document metadata
User/Beneficiary actions -> AccessLog

## Implemented MVP
- JWT registration/login
- Private wishes: create/update/archive/list
- Beneficiary management
- Per-wish access control
- Verification request workflow
- Owner approve/reject decision
- Audit logs
- Centralized exception handling
- MySQL persistence
- Stateless Spring Security

## Run
1. Install Java 21, Maven, MySQL.
2. Create/use MySQL. The configured URL automatically creates `last_wish_saver`.
3. Default credentials are root/root. Change `DB_USERNAME` and `DB_PASSWORD` as environment variables if needed.
4. Run:
   `mvn spring-boot:run`
5. Base URL: http://localhost:8080

## Demo flow in Postman

### 1. Register owner
POST /api/auth/register
{
  "name": "Owner",
  "email": "owner@example.com",
  "password": "secret123"
}

Save the returned token.

### 2. Login beneficiary as a second user
POST /api/auth/register
{
  "name": "Beneficiary",
  "email": "beneficiary@example.com",
  "password": "secret123"
}

For the MVP, the beneficiary record's email is matched to this user's email when submitting verification.

### 3. Owner creates a wish
POST /api/wishes
Authorization: Bearer OWNER_TOKEN
{
  "title": "My final wishes",
  "content": "Please take care of the family documents."
}

### 4. Owner creates beneficiary
POST /api/beneficiaries
Authorization: Bearer OWNER_TOKEN
{
  "name": "Beneficiary",
  "email": "beneficiary@example.com",
  "relationship": "Sibling"
}

### 5. Grant access
POST /api/wishes/1/access
Authorization: Bearer OWNER_TOKEN
{
  "beneficiaryId": 1,
  "accessLevel": "VIEW"
}

### 6. Beneficiary submits verification request
POST /api/wishes/1/verification-requests
Authorization: Bearer BENEFICIARY_TOKEN
{
  "beneficiaryId": 1,
  "note": "I am requesting access to the wish."
}

### 7. Owner approves
POST /api/verification-requests/1/decision
Authorization: Bearer OWNER_TOKEN
{
  "approve": true,
  "decisionNote": "Verified."
}

### 8. Audit
GET /api/audit-logs/1
Authorization: Bearer OWNER_TOKEN

## Interview explanation
The project demonstrates authentication, authorization, REST communication, database relationships, transaction boundaries, controlled access, auditability and a verification workflow. The main security idea is least-privilege access: a beneficiary only gets access to a particular wish after an explicit access grant and verification decision.

## Important
This is an interview/demo MVP, not a production estate/legal platform. File storage is represented by document metadata in the schema; an S3 adapter can be added later.
