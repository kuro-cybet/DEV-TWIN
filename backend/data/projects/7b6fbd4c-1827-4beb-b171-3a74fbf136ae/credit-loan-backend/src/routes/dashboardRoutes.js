const express = require("express");
const pool = require("../config/db");
const asyncHandler = require("../utils/asyncHandler");

const router = express.Router();

router.get("/summary", asyncHandler(async (req, res) => {
  const [[applicants]] = await pool.query(
    "SELECT COUNT(*) AS total FROM Applicant"
  );

  const [[applications]] = await pool.query(
    "SELECT COUNT(*) AS total FROM Application"
  );

  const [[pending]] = await pool.query(
    `SELECT COUNT(*) AS total
     FROM Application
     WHERE Status NOT IN ('REJECTED','DISBURSED')`
  );

  const [[approved]] = await pool.query(
    `SELECT COUNT(*) AS total
     FROM Application
     WHERE Status = 'FINAL_APPROVAL'`
  );

  const [[disbursed]] = await pool.query(
    `SELECT COUNT(*) AS total, COALESCE(SUM(DisbursedAmount),0) AS amount
     FROM DisbursedAccount`
  );

  const [byType] = await pool.query(
    `SELECT ApplicationType, COUNT(*) AS total
     FROM Application
     GROUP BY ApplicationType`
  );

  const [byStatus] = await pool.query(
    `SELECT Status, COUNT(*) AS total
     FROM Application
     GROUP BY Status`
  );

  res.json({
    success: true,
    data: {
      applicants: applicants.total,
      applications: applications.total,
      pending: pending.total,
      finalApproval: approved.total,
      disbursedCount: disbursed.total,
      disbursedAmount: disbursed.amount,
      byType,
      byStatus
    }
  });
}));

module.exports = router;
