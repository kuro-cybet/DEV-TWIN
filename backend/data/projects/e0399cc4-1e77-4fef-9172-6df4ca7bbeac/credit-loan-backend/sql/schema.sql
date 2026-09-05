CREATE DATABASE IF NOT EXISTS credit_loan_system;
USE credit_loan_system;

CREATE TABLE IF NOT EXISTS Applicant (
    ApplicantID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    DateOfBirth DATE,
    Gender VARCHAR(20),
    MobileNo VARCHAR(15),
    Email VARCHAR(100),
    Address VARCHAR(255),
    PAN VARCHAR(20) UNIQUE,
    AadhaarNo VARCHAR(20) UNIQUE,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Application (
    ApplicationID INT AUTO_INCREMENT PRIMARY KEY,
    ApplicantID INT NOT NULL,
    ApplicationDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    ApplicationType ENUM('CreditCard','PersonalLoan','HomeLoan') NOT NULL,
    RequestedAmount DECIMAL(15,2) DEFAULT 0,
    Purpose VARCHAR(255),
    Status ENUM(
        'SUBMITTED',
        'CREDIT_CHECK',
        'UNDERWRITING',
        'FINAL_APPROVAL',
        'REJECTED',
        'DISBURSED'
    ) DEFAULT 'SUBMITTED',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_application_applicant
        FOREIGN KEY (ApplicantID) REFERENCES Applicant(ApplicantID)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS CreditCard (
    ApplicationID INT PRIMARY KEY,
    CardType VARCHAR(50),
    CreditLimit DECIMAL(15,2),
    AnnualIncome DECIMAL(15,2),

    CONSTRAINT fk_creditcard_application
        FOREIGN KEY (ApplicationID) REFERENCES Application(ApplicationID)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS PersonalLoan (
    ApplicationID INT PRIMARY KEY,
    EmploymentType VARCHAR(50),
    TenureMonths INT,

    CONSTRAINT fk_personalloan_application
        FOREIGN KEY (ApplicationID) REFERENCES Application(ApplicationID)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS HomeLoan (
    ApplicationID INT PRIMARY KEY,
    PropertyType VARCHAR(100),
    PropertyValue DECIMAL(15,2),
    LoanTenureYears INT,

    CONSTRAINT fk_homeloan_application
        FOREIGN KEY (ApplicationID) REFERENCES Application(ApplicationID)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS CreditCheck (
    CreditCheckID INT AUTO_INCREMENT PRIMARY KEY,
    ApplicationID INT NOT NULL UNIQUE,
    CheckDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    CreditScore INT,
    CreditGrade VARCHAR(10),
    Remarks VARCHAR(255),

    CONSTRAINT fk_creditcheck_application
        FOREIGN KEY (ApplicationID) REFERENCES Application(ApplicationID)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ApprovalStage (
    ApprovalStageID INT AUTO_INCREMENT PRIMARY KEY,
    ApplicationID INT NOT NULL,
    StageName ENUM(
        'CREDIT_CHECK',
        'UNDERWRITING',
        'FINAL_APPROVAL',
        'DISBURSEMENT'
    ) NOT NULL,
    OfficerID INT,
    Decision ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
    DecisionDate DATETIME NULL,
    Remarks VARCHAR(255),
    IsCompleted BOOLEAN DEFAULT FALSE,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_approval_application
        FOREIGN KEY (ApplicationID) REFERENCES Application(ApplicationID)
        ON DELETE CASCADE ON UPDATE CASCADE,

    UNIQUE KEY uq_application_stage (ApplicationID, StageName)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS DisbursedAccount (
    DisbursedAccountID INT AUTO_INCREMENT PRIMARY KEY,
    ApplicationID INT NOT NULL UNIQUE,
    AccountNo VARCHAR(30) UNIQUE NOT NULL,
    AccountType VARCHAR(50) NOT NULL,
    DisbursedAmount DECIMAL(15,2) NOT NULL,
    DisbursementDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    BankName VARCHAR(100),
    IFSCCode VARCHAR(20),

    CONSTRAINT fk_disbursed_application
        FOREIGN KEY (ApplicationID) REFERENCES Application(ApplicationID)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS TransactionLog (
    LogID BIGINT AUTO_INCREMENT PRIMARY KEY,
    ApplicationID INT NOT NULL,
    TransactionType VARCHAR(50) NOT NULL,
    ActionType VARCHAR(50) NOT NULL,
    OldValue VARCHAR(255),
    NewValue VARCHAR(255),
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactionlog_application
        FOREIGN KEY (ApplicationID) REFERENCES Application(ApplicationID)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_application_applicant ON Application(ApplicantID);
CREATE INDEX idx_application_status ON Application(Status);
CREATE INDEX idx_approval_application ON ApprovalStage(ApplicationID);
CREATE INDEX idx_log_application ON TransactionLog(ApplicationID);

INSERT INTO Applicant
(FullName, DateOfBirth, Gender, MobileNo, Email, Address, PAN, AadhaarNo)
VALUES
('Demo Applicant', '2004-05-15', 'Male', '9876543210',
 'demo@example.com', 'Chennai, Tamil Nadu', 'ABCDE1234F', '123456789012')
ON DUPLICATE KEY UPDATE FullName = VALUES(FullName);
