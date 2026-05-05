# AML Backend - Anti-Money Laundering Solution

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![JWT](https://img.shields.io/badge/JWT-Authentication-black?style=for-the-badge&logo=json-web-tokens)

A robust, multi-tenant Anti-Money Laundering (AML) backend system designed for financial institutions to detect, investigate, and report suspicious activities.

## 🚀 Key Features

*   **Multi-Tenant Architecture**: Schema-based multi-tenancy allowing isolated data environments for different banking entities.
*   **Dynamic Rule Engine**: Custom-built rule engine with templates to identify suspicious transaction patterns (e.g., large cash deposits, rapid movement of funds).
*   **Data Ingestion**: High-performance CSV parser for importing large volumes of transaction and customer data.
*   **Case & Investigation Management**: Comprehensive workflow for assigning cases to Compliance Officers, tracking investigations, and updating case statuses.
*   **Automated Reporting**: Generation of detailed PDF reports for SAR (Suspicious Activity Report) and STR (Suspicious Transaction Report) filings.
*   **Secure Authentication**: JWT-based security with custom authentication providers and token blacklisting for secure logouts.
*   **Notification System**: Real-time in-app notifications and email alerts for critical events like case assignments or rule violations.

## 🛠️ Tech Stack

*   **Framework**: Spring Boot (Spring Web, Spring Security, Spring Data JPA)
*   **Language**: Java 17
*   **Database**: PostgreSQL
*   **Migration**: Flyway DB
*   **Security**: JSON Web Token (jjwt)
*   **Utilities**: Lombok, Dotenv, OpenPDF (PDF generation), Spring Mail

## 📂 Project Structure

```text
AML/
├── .mvn/                # Maven wrapper configuration
├── logs/                # Application logs (aml-app.log)
├── uploads/             # Temporary storage for uploaded CSV files
├── src/
│   ├── main/
│   │   ├── java/com/tss/aml/
│   │   │   ├── authentication/   # Custom Auth Token & Security Provider
│   │   │   ├── config/           # Security, Multi-tenancy, & Hibernate config
│   │   │   ├── context/          # Tenant Context Holder (ThreadLocal)
│   │   │   ├── controller/       # REST API Endpoints (Auth, Rules, Cases, etc.)
│   │   │   ├── dto/              # Data Transfer Objects (Request/Response)
│   │   │   ├── entity/           # JPA Entities (Base, Tenant-specific, Master)
│   │   │   ├── enums/            # Domain Enums (Roles, Status, RuleTypes)
│   │   │   ├── exception/        # Custom Exceptions & Global Error Handler
│   │   │   ├── filter/           # Security Filters (JWT & Login Authentication)
│   │   │   ├── mapper/           # DTO to Entity Mappers
│   │   │   ├── master/           # Master Database Entities & Repositories
│   │   │   ├── model/            # Generic Domain Models
│   │   │   ├── reports/          # Report-specific Models & Data Classes
│   │   │   ├── rule_engine/      # AML Rule Logic & Dynamic Templates (RL_001...)
│   │   │   ├── service/          # Core Business Logic & External Services
│   │   │   └── tenant/           # Tenant Database Entities & Repositories
│   │   └── resources/
│   │       ├── db/migration/     # Flyway SQL Scripts (Master & Tenant schemas)
│   │       ├── templates/        # Email & PDF Templates
│   │       └── application.properties
│   └── test/                # Unit and Integration Test Suites
├── .env                 # Environment variables for database & secrets
├── mvnw                 # Maven Wrapper script
└── pom.xml              # Project dependencies and build configuration
```

## 🏁 Getting Started

### Prerequisites

*   JDK 17 or higher
*   Maven 3.x
*   PostgreSQL 14+

### Environment Configuration

Create a `.env` file in the root directory (already provided in the workspace) with the following variables:

```env
DB_URL=jdbc:postgresql://localhost:5432/aml
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Installation & Running

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd AML
    ```

2.  **Install dependencies**:
    ```bash
    mvn clean install
    ```

3.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

The server will start on `http://localhost:8080`.

## 🛡️ Security & Roles

The system implements strict **Role-Based Access Control (RBAC)**:
*   **SYSTEM_ADMIN**: Manages global bank tenants, creates rule templates, and monitors system health.
*   **BANK_ADMIN**: Manages bank-specific compliance officers, uploads data, configures rules, and views high-level reports.
*   **COMPLIANCE_OFFICER**: Conducts day-to-day investigations, reviews alerts, and files SAR/STR reports.

## 📊 API Reference

### 🔐 Authentication
| Endpoint | Method | Role | Description |
| :--- | :--- | :--- | :--- |
| `/login` | `POST` | Public | Authenticate user and receive JWT. |
| `/api/v1/auth/logout` | `POST` | All | Invalidate current session and blacklist token. |
| `/api/v1/auth/verify` | `POST` | Public | Validate the integrity of a JWT. |
| `/api/v1/auth/register/tenant` | `POST` | `SYSTEM_ADMIN` | Onboard a new bank entity. |
| `/api/v1/auth/register/co` | `POST` | `BANK_ADMIN` | Register a new Compliance Officer. |

### 📁 Data & File Management
| Endpoint | Method | Role | Description |
| :--- | :--- | :--- | :--- |
| `/api/v1/data/tenants` | `GET` | `SYSTEM_ADMIN` | Fetch all registered banks. |
| `/api/v1/data/rule-templates` | `GET` | `SYSTEM_ADMIN` | Fetch available AML rule templates. |
| `/api/files/customers` | `POST` | `BANK_ADMIN` | Bulk upload customer CSV data. |
| `/api/files/transactions` | `POST` | `BANK_ADMIN` | Bulk upload transaction CSV data. |

### ⚖️ Investigation Workflow
| Endpoint | Method | Role | Description |
| :--- | :--- | :--- | :--- |
| `/api/v1/investigation/cases` | `GET` | `BA`, `CO` | Fetch all investigation cases. |
| `/api/v1/investigation/assignments` | `POST` | `BANK_ADMIN` | Assign alerts/cases to officers. |
| `/api/v1/investigation/mark-false-positive/{id}`| `PUT` | `CO` | Dismiss an alert as a false positive. |
| `/api/v1/investigation/cases/{id}/escalate`| `PUT` | `CO` | Escalate a suspicious case to admin. |
| `/api/v1/investigation/cases/{id}/close` | `PUT` | `CO` | Close a case after investigation. |
| `/api/v1/investigation/cases/{id}/file-sar` | `PUT` | `CO` | Formally file a Suspicious Activity Report. |

### 🔍 Rules & Alerts
| Endpoint | Method | Role | Description |
| :--- | :--- | :--- | :--- |
| `/api/v1/rules` | `POST` | `SYSTEM_ADMIN`| Configure a new detection rule. |
| `/api/v1/rules/alerts` | `GET` | `BANK_ADMIN` | Fetch recent system alerts. |
| `/api/v1/rules/alerts/customer/{num}` | `GET` | `BA`, `CO` | Fetch alerts for a specific customer. |

### 📈 Reports & Notifications
| Endpoint | Method | Role | Description |
| :--- | :--- | :--- | :--- |
| `/api/v1/notifications` | `GET` | `BA`, `CO` | Fetch user-specific notifications. |
| `/api/v1/reports/alert/pdf` | `GET` | All | Download summary of an alert in PDF. |
| `/api/v1/reports/cases/{id}/pdf` | `GET` | All | Generate full case investigation report. |
| `/api/v1/reports/co-performance` | `GET` | `BANK_ADMIN` | View officer productivity metrics. |

## ⚡ Database Optimization

To maintain high performance as data scales, the following indexes are recommended:

| Index Name | Table | Column(s) | Primary Benefit |
| :--- | :--- | :--- | :--- |
| **`idx_bt_jti`** | `blacklisted_tokens` | `jti` | Ensures zero-latency security checks on every API call. |
| **`idx_txn_eval`** | `transactions` | `evaluated` | Prevents the Rule Engine from stalling as transaction volume grows. |
| **`idx_txn_cust_time`**| `transactions` | `customer_id, txn_time`| Optimizes complex pattern-matching and velocity rules. |
| **`idx_cust_num`** | `customers` | `customer_number` | Accelerates bulk CSV data ingestion and customer lookups. |
| **`idx_br_active`** | `broken_rules` | `active, created_at` | Keeps the Alert Dashboard and PDF reports loading instantly. |
| **`idx_case_status`** | `cases` | `case_status` | Provides a smooth experience for officers managing 100s of cases. |

---
*Developed as part of the Swabhav Project Capstone.*

