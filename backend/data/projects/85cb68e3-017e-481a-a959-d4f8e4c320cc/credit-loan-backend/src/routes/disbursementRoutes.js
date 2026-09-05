const express = require("express");
const crypto = require("crypto");
const pool = require("../config/db");
const asyncHandler = require("../utils/asyncHandler");

const router = express.Router();

function generateAccountNo() {
  return "LN" + Date.now().toString().slice(-10) + crypto.randomInt(100, 999);
}

router.post("/", asyncHandler(async (req, res) => {
  const {
    applicationId,
    accountType,
    disbursedAmount,
    bankName,
    ifscCode,
    simulateCrash
  } = req.body;

  if (!applicationId || !accountType || disbursedAmount === undefined) {
    return res.status(400).json({
      success: false,
      message: "applicationId, accountType and disbursedAmount are required."
    });
  }

  const amount = Number(disbursedAmount);

  if (!Number.isFinite(amount) || amount <= 0) {
    return res.status(400).json({
      success: false,
      message: "disbursedAmount must be greater than zero."
    });
  }

  const connection = await pool.getConnection();

  try {
    await connection.beginTransaction();

    // Lock application before irreversible disbursement operation.
    const [applications] = await connection.execute(
      `SELECT ApplicationID, Status, ApplicationType, RequestedAmount
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

    const application = applications[0];

    if (application.Status !== "FINAL_APPROVAL") {
      await connection.rollback();
      return res.status(409).json({
        success: false,
        message: "Application must be in FINAL_APPROVAL before disbursement."
      });
    }

    const [finalApproval] = await connection.execute(
      `SELECT *
       FROM ApprovalStage
       WHERE ApplicationID = ?
         AND StageName = 'FINAL_APPROVAL'
         AND Decision = 'APPROVED'
         AND IsCompleted = TRUE
       FOR UPDATE`,
      [applicationId]
    );

    if (!finalApproval.length) {
      await connection.rollback();
      return res.status(409).json({
        success: false,
        message: "Final approval is required before disbursement."
      });
    }

    const [existing] = await connection.execute(
      "SELECT * FROM DisbursedAccount WHERE ApplicationID = ? FOR UPDATE",
      [applicationId]
    );

    if (existing.length) {
      await connection.rollback();
      return res.status(409).json({
        success: false,
        message: "Application has already been disbursed.",
        data: existing[0]
      });
    }

    const accountNo = generateAccountNo();

    /*
      Immediate-update style:
      The disbursement row and status are updated inside the transaction.
      InnoDB writes changes to its log before committing them and can redo
      committed changes during crash recovery.
    */
    await connection.execute(
      `INSERT INTO DisbursedAccount
       (ApplicationID, AccountNo, AccountType, DisbursedAmount, BankName, IFSCCode)
       VALUES (?, ?, ?, ?, ?, ?)`,
      [
        applicationId,
        accountNo,
        accountType,
        amount,
        bankName || null,
        ifscCode || null
      ]
    );

    await connection.execute(
      `UPDATE Application
       SET Status = 'DISBURSED'
       WHERE ApplicationID = ?`,
      [applicationId]
    );

    await connection.execute(
      `INSERT INTO ApprovalStage
       (ApplicationID, StageName, OfficerID, Decision, DecisionDate, Remarks, IsCompleted)
       VALUES (?, 'DISBURSEMENT', NULL, 'APPROVED', CURRENT_TIMESTAMP, 'Disbursement completed', TRUE)
       ON DUPLICATE KEY UPDATE
         Decision = 'APPROVED',
         DecisionDate = CURRENT_TIMESTAMP,
         Remarks = 'Disbursement completed',
         IsCompleted = TRUE`,
      [applicationId]
    );

    await connection.execute(
      `INSERT INTO TransactionLog
       (ApplicationID, TransactionType, ActionType, NewValue)
       VALUES (?, 'DISBURSEMENT', 'COMMIT_READY', ?)`,
      [applicationId, `Account=${accountNo}, Amount=${amount}`]
    );

    /*
      For classroom crash-recovery demonstration only:
      Roll back before commit to simulate an interrupted transaction.
      Real committed crash recovery is handled by InnoDB's redo logging.
    */
    if (simulateCrash === true) {
      throw new Error("SIMULATED_CRASH_BEFORE_COMMIT");
    }

    await connection.commit();

    const [rows] = await pool.execute(
      "SELECT * FROM DisbursedAccount WHERE ApplicationID = ?",
      [applicationId]
    );

    res.status(201).json({
      success: true,
      message: "Disbursement completed successfully.",
      data: rows[0]
    });
  } catch (error) {
    await connection.rollback();

    if (error.message === "SIMULATED_CRASH_BEFORE_COMMIT") {
      return res.status(503).json({
        success: false,
        message: "Simulated crash occurred before COMMIT. Transaction was rolled back.",
        recovery: "InnoDB transaction recovery protects committed data; uncommitted changes are rolled back."
      });
    }

    throw error;
  } finally {
    connection.release();
  }
}));

router.get("/:applicationId", asyncHandler(async (req, res) => {
  const [rows] = await pool.execute(
    "SELECT * FROM DisbursedAccount WHERE ApplicationID = ?",
    [req.params.applicationId]
  );

  if (!rows.length) {
    return res.status(404).json({
      success: false,
      message: "No disbursed account found."
    });
  }

  res.json({ success: true, data: rows[0] });
}));

router.get("/:applicationId/history", asyncHandler(async (req, res) => {
  const [rows] = await pool.execute(
    `SELECT *
     FROM TransactionLog
     WHERE ApplicationID = ?
     ORDER BY LogID ASC`,
    [req.params.applicationId]
  );

  res.json({ success: true, data: rows });
}));

module.exports = router;
