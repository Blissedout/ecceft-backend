ECCEFT: Electronic Fund Transfer with Elliptic Curve Cryptography
A backend system for secure electronic fund transfers, where every transaction is authorised with Elliptic Curve Cryptography (ECC) digital signatures. Users prove they approved a transfer by signing it with a private key that never touches the server or the database.
 
Key Security Principle
The private key is never stored in the database, and never stored on the server.
• The private key is generated and kept by the user (client side). It is used only to sign transaction data.
• The public key is the only key material the system stores. It is used to verify signatures.
• If the database were fully compromised, an attacker would get public keys only, which cannot be used to sign or authorise transfers.

Features
• User registration
• ECC key pair generation (client side)
• Digital signing of transfer requests (ECDSA)
• Server-side signature verification before any money moves
• Account balance management
• Fund transfers between accounts
• Transaction history and audit trail
• Replay-attack protection (nonce / timestamp)

Tech Stack
Layer and Technology
Language: Java 21
Framework: Spring Boot
Database: MySQL
Cryptography: Bouncy Castle, curve:secp256r1
Build tool: Maven                                               
                                                
How It Works
1. Key Generation and Enrolment
   i.The client generates an ECC key pair.
   ii.The private key stays with the user.
   iii.The public key is sent to the backend during registration and stored against the user's account.

2.Initiating a Transfer
  i.The user builds the transfer payload: sender, receiver, amount, timestamp, nonce.
  ii.The user signs the payload with their private key (ECDSA), producing a signature.
  iii.The payload and signature are sent to the backend.

3. Verification and Execution
   i.The backend looks up the sender's stored public key.
   ii.It verifies the signature against the exact payload received.
   iii.It checks the nonce and timestamp to reject replayed requests.
   iv.It checks the sender has sufficient balance.
   v.If everything passes, it debits and credits the accounts in a single database transaction and records the transfer.
   vi.If any step fails, the transfer is rejected and nothing changes.

Getting Started
Prerequisites
• JDK 21
• Maven
• MySQL 8
• Git

Installation
# Clone the repository
git clone https://github.com/Blissedout/ecceft-backend.git
cd ecceft-backend 

# Create the database
mysql -u root -p -e "CREATE DATABASE ecceft;"

# Configure environment 

# Build and run
./mvnw spring-boot:run

The server starts on http://localhost:8080 by default.

Configuration
spring.datasource.url
Database URL:jdbc:mysql://localhost:3306/ecceft
spring.datasource.username
Database user: YOUR-DB-USERNAME
spring.datasource.password
Database password: YOUR-DB-PASSWORD
ecc.curve
Elliptic curve used: secp256r1
ecc.signature-algorithm: SHA256withECDSA

API Reference
POST
/api/auth/register
Register a user and enrol their public key
POST
/api/accounts
Create an account
GET
/api/accounts/id/balance
Get account balance
POST
/api/transfers
Submit a signed transfer
GET
/api/transfers/{id}
Get a transfer's details
GET
/api/accounts/{id}/transactions
View transaction history

Request

POST /api/transfers
{
  "senderAccount": "0123456789",
  "receiverAccount": "9876543210",
  "amount": 5000.00,
  "timestamp": "2026-11-25T10:30:00Z",
  "nonce": "a1b2c3d4",
  "signature": "base64-encoded ECDSA signature>"
}

Success Response

{
  "status": "SUCCESS",
  "transferId": "TXN-0001",
  "message": "Transfer completed"
}

Failure Response (invalid signature)

{
  "status": "REJECTED",
  "message": "Signature verification failed"
}

Database Design

Main tables:

• users: identity details and the user's public key
• accounts: account number, owner, balance
• transactions: sender, receiver, amount, timestamp, nonce, signature, status

Security Considerations
Concern
How ECCEFT addresses it
Private key theft from server
Private keys are never stored server side or in the database
Database breach
Only public keys are stored, so they cannot authorise transfers
Tampering with a transfer
Any change to the payload invalidates the signature
Replay attacks
Unique nonce and timestamp checks.
Partial or inconsistent transfers
Debit and credit run in one atomic database transaction
Weak randomness
Keys and nonces come from a cryptographically secure random generator

Responsibility of the user: because the server cannot recover a private key, a lost key cannot be restored. A key rotation or recovery flow is listed in the roadmap.

Testing

./mvnw test

Suggested coverage:

• Valid signature is accepted
• Tampered payload is rejected
• Wrong key signature is rejected
• Replayed nonce is rejected
• Insufficient balance is rejected
• Transfer is atomic on failure

Project Structure

ecceft-backend/
├── src/main/java/ecceft/
│   ├── controller/    # REST endpoints
│   ├── service/       # Transfer and verification logic
│   ├── crypto/        # ECC key and signature utilities
│   ├── model/         # Entities
│   ├── repository/    # Data access
│   └── config/        # App configuration
├── src/main/resources/
│   └── application.properties
├── src/test/
└── pom.xml

Roadmap

• ☐ Key rotation and revocation
• ☐ Multi-factor confirmation for large transfers
• ☐ Rate limiting and fraud detection
• ☐ Client SDK for key generation and signing
• ☐ Docker support

Contributing

1. Fork the repository
2. Create a feature branch: git checkout -b feature/your-feature
3. Commit your changes: git commit -m "Add your feature"
4. Push the branch: git push origin feature/your-feature
5. Open a Pull Request

