const express = require("express");
const pool = require("../config/db");
const asyncHandler = require("../utils/asyncHandler");
const {
  stageValues,
  decisionValues
} = require("../utils/validation");

const router = express.Router();

const nextStatus = {
  CREDIT_CHECK: "CREDIT_CHECK",
  UNDERWRITING: "FINAL_APPROVAL",
  FINAL_APPROVAL: "FINAL_APPROVAL",
  DISBURSEMENT: "DISBURSED"
};

router.post("/", asyncHandler(async (req, res) => {
  const {
    applicationId,
    stageName,
    officerId
  } = req.body;

  if (!applicationId || !stageValues.includes(stageName) || !officerId) {
    return res.status(400).json({
      success: false,
      message: "applicationId, valid stageName and officerId are required."
    });
  }

  const [result] = await pool.execute(
    `INSERT INTO ApprovalStage
     (ApplicationID, StageName, OfficerID, Decision)
     VALUES (?, ?, ?, 'PENDING')
     ON DUPLICATE KEY UPDATE
       OfficerID = VALUES(OfficerID)`,
    [applicationId, stageName, officerId]
  );

  const [rows] = await pool.execute(
    `SELECT * FROM ApprovalStage
     WHERE ApplicationID = ? AND StageName = ?`,
    [applicationId, stageName]
  );

  res.status(201).json({
    success: true,
    data: rows[0],
    affectedRows: result.affectedRows
  });
}));

router.get("/:applicationId", asyncHandler(async (req, res) => {
  const [rows] = await pool.execute(
    `SELECT *
     FROM ApprovalStage
     WHERE ApplicationID = ?
     ORDER BY ApprovalStageID`,
    [req.params.applicationId]
  );

  res.json({ success: true, data: rows });
}));

/*
  Concurrent approval protection:

  SELECT ... FOR UPDATE locks the application row until COMMIT/ROLLBACK.
  This is the database-level locking mechanism used here to demonstrate
  strict two-phase locking for conflicting approval operations.
*/
router.post("/:applicationId/decision", asyncHandler(async (req, res) => {
  const applicationId = Number(req.params.applicationId);
  const {
    stageName,
    officerId,
    decision,
    remarks
  } = req.body;

  if (
    !stageValues.includes(stageName) ||
    !decisionValues.includes(decision) ||
    !officerId
  ) {
    return res.status(400).json({
      success: false,
      message: "Valid stageName, officerId and decision are required."
    });
  }

  if (decision === "PENDING") {
    return res.status(400).json({
      success: false,
      message: "Decision must be APPROVED or REJECTED."
    });
  }

  const connection = await pool.getConnection();

  try {
    await connection.beginTransaction();

    // Acquire exclusive row lock. Held until COMMIT/ROLLBACK.
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

    const [stageRows] = await connection.execute(
      `SELECT *
       FROM ApprovalStage
       WHERE ApplicationID = ? AND StageName = ?
       FOR UPDATE`,
      [applicationId, stageName]
    );

    if (!stageRows.length) {
      await connection.rollback();
      return res.status(404).json({
        success: false,
        message: "Approval stage has not been assigned."
      });
    }

    const currentStage = stageRows[0];

    if (currentStage.IsCompleted) {
      await connection.rollback();
      return res.status(409).json({
        success: false,
        message: "This approval stage is already completed."
      });
    }

    const oldDecision = currentStage.Decision;

    await connection.execute(
      `UPDATE ApprovalStage
       SET Decision = ?,
           OfficerID = ?,
           DecisionDate = CURRENT_TIMESTAMP,
           Remarks = ?,
           IsCompleted = TRUE
       WHERE ApprovalStageID = ?`,
      [decision, officerId, remarks || null, currentStage.ApprovalStageID]
    );

    let newStatus;

    if (decision === "REJECTED") {
      newStatus = "REJECTED";
    } else if (stageName === "CREDIT_CHECK") {
      newStatus = "UNDERWRITING";
    } else if (stageName === "UNDERWRITING") {
      newStatus = "FINAL_APPROVAL";
    } else if (stageName === "FINAL_APPROVAL") {
      newStatus = "FINAL_APPROVAL";
    } else {
      newStatus = "DISBURSED";
    }

    await connection.execute(
      "UPDATE Application SET Status = ? WHERE ApplicationID = ?",
      [newStatus, applicationId]
    );

    await connection.execute(
      `INSERT INTO TransactionLog
       (ApplicationID, TransactionType, ActionType, OldValue, NewValue)
       VALUES (?, 'APPROVAL', ?, ?, ?)`,
      [
        applicationId,
        stageName,
        oldDecision,
        `${decision} by officer ${officerId}`
      ]
    );

    await connection.commit();

    res.json({
      success: true,
      message: "Approval decision recorded.",
      data: {
        applicationId,
        stageName,
        decision,
        newApplicationStatus: newStatus
      }
    });
  } catch (error) {
    await connection.rollback();
    throw error;
  } finally {
    connection.release();
  }
}));

module.exports = router;
