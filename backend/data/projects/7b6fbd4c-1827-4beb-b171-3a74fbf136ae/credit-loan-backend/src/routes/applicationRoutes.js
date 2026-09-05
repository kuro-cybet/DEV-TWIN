const express = require("express");
const pool = require("../config/db");
const asyncHandler = require("../utils/asyncHandler");
const { createApplication, getApplication } = require("../services/applicationService");
const {
  applicationTypes,
  statusValues,
  requireFields,
  isPositiveNumber
} = require("../utils/validation");

const router = express.Router();

router.post("/", asyncHandler(async (req, res) => {
  const missing = requireFields(req.body, [
    "applicantId",
    "applicationType"
  ]);

  if (missing.length) {
    return res.status(400).json({
      success: false,
      message: `Missing fields: ${missing.join(", ")}`
    });
  }

  if (!applicationTypes.includes(req.body.applicationType)) {
    return res.status(400).json({
      success: false,
      message: `applicationType must be one of: ${applicationTypes.join(", ")}`
    });
  }

  if (
    req.body.requestedAmount !== undefined &&
    !isPositiveNumber(req.body.requestedAmount)
  ) {
    return res.status(400).json({
      success: false,
      message: "requestedAmount must be a positive number."
    });
  }

  const applicationId = await createApplication(req.body);
  const application = await getApplication(applicationId);

  res.status(201).json({
    success: true,
    data: application
  });
}));

router.get("/", asyncHandler(async (req, res) => {
  const { status, type, applicantId } = req.query;

  let sql = `
    SELECT
      a.ApplicationID,
      a.ApplicantID,
      ap.FullName,
      a.ApplicationDate,
      a.ApplicationType,
      a.RequestedAmount,
      a.Purpose,
      a.Status
    FROM Application a
    JOIN Applicant ap ON ap.ApplicantID = a.ApplicantID
    WHERE 1 = 1
  `;

  const params = [];

  if (status) {
    if (!statusValues.includes(status)) {
      return res.status(400).json({
        success: false,
        message: "Invalid status."
      });
    }

    sql += " AND a.Status = ?";
    params.push(status);
  }

  if (type) {
    if (!applicationTypes.includes(type)) {
      return res.status(400).json({
        success: false,
        message: "Invalid application type."
      });
    }

    sql += " AND a.ApplicationType = ?";
    params.push(type);
  }

  if (applicantId) {
    sql += " AND a.ApplicantID = ?";
    params.push(applicantId);
  }

  sql += " ORDER BY a.ApplicationID DESC";

  const [rows] = await pool.execute(sql, params);

  res.json({ success: true, data: rows });
}));

router.get("/:id", asyncHandler(async (req, res) => {
  const application = await getApplication(req.params.id);

  if (!application) {
    return res.status(404).json({
      success: false,
      message: "Application not found."
    });
  }

  const [creditChecks] = await pool.execute(
    "SELECT * FROM CreditCheck WHERE ApplicationID = ?",
    [req.params.id]
  );

  const [approvalStages] = await pool.execute(
    "SELECT * FROM ApprovalStage WHERE ApplicationID = ? ORDER BY ApprovalStageID",
    [req.params.id]
  );

  const [disbursement] = await pool.execute(
    "SELECT * FROM DisbursedAccount WHERE ApplicationID = ?",
    [req.params.id]
  );

  res.json({
    success: true,
    data: {
      application,
      creditCheck: creditChecks[0] || null,
      approvalStages,
      disbursement: disbursement[0] || null
    }
  });
}));

router.put("/:id/status", asyncHandler(async (req, res) => {
  const { status } = req.body;

  if (!statusValues.includes(status)) {
    return res.status(400).json({
      success: false,
      message: "Invalid status."
    });
  }

  const connection = await pool.getConnection();

  try {
    await connection.beginTransaction();

    const [rows] = await connection.execute(
      `SELECT ApplicationID, Status
       FROM Application
       WHERE ApplicationID = ?
       FOR UPDATE`,
      [req.params.id]
    );

    if (!rows.length) {
      await connection.rollback();
      return res.status(404).json({
        success: false,
        message: "Application not found."
      });
    }

    const oldStatus = rows[0].Status;

    await connection.execute(
      "UPDATE Application SET Status = ? WHERE ApplicationID = ?",
      [status, req.params.id]
    );

    await connection.execute(
      `INSERT INTO TransactionLog
       (ApplicationID, TransactionType, ActionType, OldValue, NewValue)
       VALUES (?, 'APPLICATION', 'STATUS_UPDATE', ?, ?)`,
      [req.params.id, oldStatus, status]
    );

    await connection.commit();

    res.json({
      success: true,
      message: "Application status updated.",
      data: {
        applicationId: Number(req.params.id),
        oldStatus,
        newStatus: status
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
