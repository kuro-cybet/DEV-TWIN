const pool = require("../config/db");

async function createApplication(data) {
  const connection = await pool.getConnection();

  try {
    await connection.beginTransaction();

    const [applicantRows] = await connection.execute(
      "SELECT ApplicantID FROM Applicant WHERE ApplicantID = ? FOR SHARE",
      [data.applicantId]
    );

    if (applicantRows.length === 0) {
      const error = new Error("Applicant not found.");
      error.status = 404;
      throw error;
    }

    const [result] = await connection.execute(
      `INSERT INTO Application
       (ApplicantID, ApplicationType, RequestedAmount, Purpose, Status)
       VALUES (?, ?, ?, ?, 'SUBMITTED')`,
      [
        data.applicantId,
        data.applicationType,
        data.requestedAmount ?? 0,
        data.purpose ?? null
      ]
    );

    const applicationId = result.insertId;

    if (data.applicationType === "CreditCard") {
      await connection.execute(
        `INSERT INTO CreditCard
         (ApplicationID, CardType, CreditLimit, AnnualIncome)
         VALUES (?, ?, ?, ?)`,
        [
          applicationId,
          data.cardType ?? null,
          data.creditLimit ?? 0,
          data.annualIncome ?? 0
        ]
      );
    }

    if (data.applicationType === "PersonalLoan") {
      await connection.execute(
        `INSERT INTO PersonalLoan
         (ApplicationID, EmploymentType, TenureMonths)
         VALUES (?, ?, ?)`,
        [
          applicationId,
          data.employmentType ?? null,
          data.tenureMonths ?? null
        ]
      );
    }

    if (data.applicationType === "HomeLoan") {
      await connection.execute(
        `INSERT INTO HomeLoan
         (ApplicationID, PropertyType, PropertyValue, LoanTenureYears)
         VALUES (?, ?, ?, ?)`,
        [
          applicationId,
          data.propertyType ?? null,
          data.propertyValue ?? 0,
          data.loanTenureYears ?? null
        ]
      );
    }

    await connection.execute(
      `INSERT INTO TransactionLog
       (ApplicationID, TransactionType, ActionType, NewValue)
       VALUES (?, 'APPLICATION', 'CREATE', 'SUBMITTED')`,
      [applicationId]
    );

    await connection.commit();

    return applicationId;
  } catch (error) {
    await connection.rollback();
    throw error;
  } finally {
    connection.release();
  }
}

async function getApplication(id) {
  const [rows] = await pool.execute(
    `SELECT
       a.*,
       ap.FullName,
       ap.MobileNo,
       ap.Email,
       ap.PAN,
       ap.AadhaarNo
     FROM Application a
     JOIN Applicant ap ON ap.ApplicantID = a.ApplicantID
     WHERE a.ApplicationID = ?`,
    [id]
  );

  return rows[0] || null;
}

module.exports = { createApplication, getApplication };
