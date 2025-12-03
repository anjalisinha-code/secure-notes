🛡️ **Secure Notes — Spring Boot 3 (Java 17)**
A minimal, security-focused REST API for encrypted notes.
Implements a clean layered architecture, AES-GCM encryption, static token authentication, and an H2 in-memory database.

🚀 **Features:**
* CRUD endpoints for secure notes
* AES-GCM encryption (content never stored in plaintext)
* Static token authentication (Bearer static-token-123)
* H2 in-memory DB (no external setup)
* Layered architecture (Controller → Service → Repository)
* DTO vs Entity separation
* Input validation on all write operations
* Swagger UI for interactive API testing
* API Test cases

🏛️ **Architecture Overview**
Controller → Service → Repository
Controller – Request handling, DTO mapping, validation
Service – Business logic, AES-GCM encryption/decryption, token verification
Repository – Persistence layer (Spring Data JPA + H2)

🔐 **Security Measures**
1. Authentication
Token-based authentication
Expected header:
  Authorization: Bearer static-token-123
  Replace with JWT/OAuth2 in production

2. AES-GCM Content Encryption
AES-256-GCM with hex-encoded key
Plaintext never stored

3. Input Validation
Rejects empty/invalid titles
Rejects missing content
Validation occurs at controller layer

4. Secrets via Environment Variables
ENCRYPTION_SECRET
SECURITY_TOKEN
No secrets stored in code or configs.

🧰 Tech Stack
* Java 17
* Spring Boot 3
* Spring Web / Validation
* Spring Data JPA (H2)
* springdoc-openapi (Swagger)
* JUnit + MockMvc + Mockito

▶️ How to Run
Clone Repo → Set Env Vars → Build → Run
1. clone the repo: https://github.com/anjalisinha-code/secure-notes.git
2. Set environment variables
   export ENCRYPTTION_SECRET="00112233445566778899aabbccddeeff"
   export SECURITY_TOKEN="Bearer static-token-123"
3. Build the project
   mvn clean install
4. Start the application
   ./mvnw spring-boot:run

Application URL:
http://localhost:8080

Swagger UI
http://localhost:8080/swagger-ui.html

Click Authorize → enter:
static-token-123

📡 **API Endpoints**
_Method	 Path	           Description_
POST	    /notes	        Create note
GET	    /notes/{id}	  Get note by ID
GET	    /notes	        List all notes
PUT	    /notes/{id}	  Update note
DELETE	 /notes/{id}	  Delete note

**📸 API Screenshot Gallery**
A quick visual overview of the Secure Notes API in action.

* docs/screenshots/swagger-overview.png
* docs/screenshots/auth-dialog.png
* docs/screenshots/create-note.png
* docs/screenshots/get-note.png
* docs/screenshots/list-notes.png
* docs/screenshots/update-note.png
* docs/screenshots/delete-note.png

💻 **Quick cURL Examples**
TOKEN="Bearer static-token-123"
BASE="http://localhost:8080"

Create:
curl -X POST "$BASE/notes" \
-H "Authorization: $TOKEN" \
-H "Content-Type: application/json" \
-d '{"title":"Demo Secure Note","content":"AES-GCM encrypted demo"}'

Get by ID:
curl "$BASE/notes/1" -H "Authorization: $TOKEN"

List:
curl "$BASE/notes" -H "Authorization: $TOKEN"

Update:
curl -X PUT "$BASE/notes/1" \
-H "Authorization: $TOKEN" \
-H "Content-Type: application/json" \
-d '{"title":"Updated Title","content":"Updated secret"}'

Delete:
curl -X DELETE "$BASE/notes/1" -H "Authorization: $TOKEN"


📌 Future Improvements
* Replace static token with JWT
* Move secrets to  key vault 
* Add pagination to avoid decrypting large datasets
* Add more integration/unit tests (error paths)
* Add CI formatting (spotless:check)
