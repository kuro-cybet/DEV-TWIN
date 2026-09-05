const express = require("express");
const pool = require("../config/db");
const asyncHandler = require("../utils/asyncHandler");

const router = express.Router();

function grade(score) {
  if (score >= 750) return "A";
  if (score >= 700) return "B";
  if (score >= 650) return "C";
  return "D";
}

router.post("/", asyncHandler(async (req, res) => {
  const {
    applicationId,
    creditScore,
    remarks
  } = req.body;

  if (!applicationId || creditScore === undefined) {
    return res.status(400).json({
      success: false,
      message: "applicationId and creditScore are required."
    });
  }

  const score = Number(creditScore);

  if (!Number.isInteger(score) || score < 300 || score > 900) {
    return res.status(400).json({
      success: false,
      message: "creditScore must be an integer between 300 and 900."
    });
  }

  const connection = await pool.getConnection();

  try {
    await connection.beginTransaction();

    const [applications] = await connection.execute(
      `SELECT ApplicationID, Status
       FROM Application
       WHERE ApplicationID = ?
       FOR UPDATE`,
      [applicationId]
    );

    if (!applications.length) {
      await connection.rollback();
      return res.status(404).json({
        success: false,
        message: "Application not found."
      });
    }

    await connection.execute(
      `INSERT INTO CreditCheck
       (ApplicationID, CreditScore, CreditGrade, Remarks)
       VALUES (?, ?, ?, ?)
       ON DUPLICATE KEY UPDATE
         CreditScore = VALUES(CreditScore),
         CreditGrade = VALUES(CreditGrade),
         Remarks = VALUES(Remarks),
         CheckDate = CURRENT_TIMESTAMP`,
      [applicationId, score, grade(score), remarks || null]
    );

    await connection.execute(
      `UPDATE Application
       SET Status = 'UNDERWRITING'
       WHERE ApplicationID = ?`,
      [applicationId]
    );

    await connection.execute(
      `INSERT INTO ApprovalStage
       (ApplicationID, StageName, Decision, IsCompleted)
       VALUES (?, 'CREDIT_CHECK', 'APPROVED', TRUE)
       ON DUPLICATE KEY UPDATE
         Decision = 'APPROVED',
         IsCompleted = TRUE,
         DecisionDate = CURRENT_TIMESTAMP`,
      [applicationId]
    );

    await connection.execute(
      `INSERT INTO TransactionLog
       (ApplicationID, TransactionType, ActionType, NewValue)
       VALUES (?, 'CREDIT_CHECK', 'CREATE', ?)`,
      [applicationId, `Score=${score}, Grade=${grade(score)}`]
    );

    await connection.commit();

    const [rows] = await pool.execute(
      "SELECT * FROM CreditCheck WHERE ApplicationID = ?",
      [applicationId]
    );

    res.status(201).json({
      success: true,
      data: rows[0]
    });
  } catch (error) {
    await connection.rollback();
    throw error;
  } finally {
    connection.release();
  }
}));

router.get("/:applicationId", asyncHandler(async (req, res) => {
  const [rows] = await pool.execute(
    "SELECT * FROM CreditCheck WHERE ApplicationID = ?",
    [req.params.applicationId]
  );

  if (!rows.length) {
    return res.status(404).json({
      success: false,
      message: "Credit check not found."
    });
  }

  res.json({ success: true, data: rows[0] });
}));

module.exports = router;
