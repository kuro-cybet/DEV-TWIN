# Credit Card & Loan Application Processing System — Backend

## Stack
- Node.js
- Express
- MySQL
- mysql2
- dotenv
- CORS

## Modules covered
1. Applicant and application management
2. Credit check and underwriting
3. Multi-level approval workflow
4. Disbursement and recovery logging
5. Concurrent approval protection using database transactions + `SELECT ... FOR UPDATE`

## Setup

### 1. Create the database
Run:

```bash
mysql -u root -p < sql/schema.sql
```

### 2. Configure environment
Copy `.env.example` to `.env` and set the MySQL password.

### 3. Install packages

```bash
npm install
```

### 4. Start

```bash
npm run dev
```

API:
`http://localhost:5000`

Health check:
`GET /api/health`

## Main endpoints

### Applicants
- `POST /api/applicants`
- `GET /api/applicants`
- `GET /api/applicants/:id`
- `PUT /api/applicants/:id`
- `DELETE /api/applicants/:id`

### Applications
- `POST /api/applications`
- `GET /api/applications`
- `GET /api/applications/:id`
- `PUT /api/applications/:id/status`

### Credit checks
- `POST /api/credit-checks`
- `GET /api/credit-checks/:applicationId`

### Approval
- `POST /api/approvals`
- `GET /api/approvals/:applicationId`
- `POST /api/approvals/:applicationId/decision`

### Disbursement
- `POST /api/disbursements`
- `GET /api/disbursements/:applicationId`
- `GET /api/disbursements/:applicationId/history`

## Transaction/concurrency design

Approval decisions run inside a MySQL transaction and lock the application row with:

```sql
SELECT * FROM Application
WHERE ApplicationID = ?
FOR UPDATE;
```

The lock is held until COMMIT/ROLLBACK, preventing conflicting concurrent approval updates on the same application.

Disbursement is also performed as one transaction. InnoDB provides durable redo-based crash recovery for committed changes.
