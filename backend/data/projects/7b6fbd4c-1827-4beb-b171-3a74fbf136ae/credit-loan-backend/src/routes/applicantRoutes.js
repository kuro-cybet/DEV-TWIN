const express = require("express");
const pool = require("../config/db");
const asyncHandler = require("../utils/asyncHandler");
const { requireFields } = require("../utils/validation");

const router = express.Router();

router.post("/", asyncHandler(async (req, res) => {
  const missing = requireFields(req.body, ["fullName"]);

  if (missing.length) {
    return res.status(400).json({
      success: false,
      message: `Missing fields: ${missing.join(", ")}`
    });
  }

  const {
    fullName,
    dateOfBirth,
    gender,
    mobileNo,
    email,
    address,
    pan,
    aadhaarNo
  } = req.body;

  const [result] = await pool.execute(
    `INSERT INTO Applicant
     (FullName, DateOfBirth, Gender, MobileNo, Email, Address, PAN, AadhaarNo)
     VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
    [
      fullName,
      dateOfBirth || null,
      gender || null,
      mobileNo || null,
      email || null,
      address || null,
      pan || null,
      aadhaarNo || null
    ]
  );

  const [rows] = await pool.execute(
    "SELECT * FROM Applicant WHERE ApplicantID = ?",
    [result.insertId]
  );

  res.status(201).json({
    success: true,
    data: rows[0]
  });
}));

router.get("/", asyncHandler(async (req, res) => {
  const [rows] = await pool.execute(
    "SELECT * FROM Applicant ORDER BY ApplicantID DESC"
  );

  res.json({ success: true, data: rows });
}));

router.get("/:id", asyncHandler(async (req, res) => {
  const [rows] = await pool.execute(
    "SELECT * FROM Applicant WHERE ApplicantID = ?",
    [req.params.id]
  );

  if (!rows.length) {
    return res.status(404).json({
      success: false,
      message: "Applicant not found."
    });
  }

  res.json({ success: true, data: rows[0] });
}));

router.put("/:id", asyncHandler(async (req, res) => {
  const {
    fullName,
    dateOfBirth,
    gender,
    mobileNo,
    email,
    address,
    pan,
    aadhaarNo
  } = req.body;

  const [result] = await pool.execute(
    `UPDATE Applicant
     SET FullName = COALESCE(?, FullName),
         DateOfBirth = COALESCE(?, DateOfBirth),
         Gender = COALESCE(?, Gender),
         MobileNo = COALESCE(?, MobileNo),
         Email = COALESCE(?, Email),
         Address = COALESCE(?, Address),
         PAN = COALESCE(?, PAN),
         AadhaarNo = COALESCE(?, AadhaarNo)
     WHERE ApplicantID = ?`,
    [
      fullName ?? null,
      dateOfBirth ?? null,
      gender ?? null,
      mobileNo ?? null,
      email ?? null,
      address ?? null,
      pan ?? null,
      aadhaarNo ?? null,
      req.params.id
    ]
  );

  if (!result.affectedRows) {
    return res.status(404).json({
      success: false,
      message: "Applicant not found."
    });
  }

  const [rows] = await pool.execute(
    "SELECT * FROM Applicant WHERE ApplicantID = ?",
    [req.params.id]
  );

  res.json({ success: true, data: rows[0] });
}));

router.delete("/:id", asyncHandler(async (req, res) => {
  const [result] = await pool.execute(
    "DELETE FROM Applicant WHERE ApplicantID = ?",
    [req.params.id]
  );

  if (!result.affectedRows) {
    return res.status(404).json({
      success: false,
      message: "Applicant not found."
    });
  }

  res.json({
    success: true,
    message: "Applicant deleted."
  });
}));

module.exports = router;
