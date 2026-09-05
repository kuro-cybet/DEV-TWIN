require("dotenv").config();

const express = require("express");
const cors = require("cors");

const pool = require("./config/db");
const applicantRoutes = require("./routes/applicantRoutes");
const applicationRoutes = require("./routes/applicationRoutes");
const creditCheckRoutes = require("./routes/creditCheckRoutes");
const approvalRoutes = require("./routes/approvalRoutes");
const disbursementRoutes = require("./routes/disbursementRoutes");
const dashboardRoutes = require("./routes/dashboardRoutes");
const { notFound, errorHandler } = require("./middleware/errorHandler");

const app = express();

app.use(
  cors({
    origin: process.env.CORS_ORIGIN || "http://localhost:5173"
  })
);

app.use(express.json());

app.get("/api/health", async (req, res, next) => {
  try {
    await pool.query("SELECT 1");
    res.json({
      success: true,
      message: "Credit & Loan API is running",
      database: "connected"
    });
  } catch (error) {
    next(error);
  }
});

app.use("/api/applicants", applicantRoutes);
app.use("/api/applications", applicationRoutes);
app.use("/api/credit-checks", creditCheckRoutes);
app.use("/api/approvals", approvalRoutes);
app.use("/api/disbursements", disbursementRoutes);
app.use("/api/dashboard", dashboardRoutes);

app.use(notFound);
app.use(errorHandler);

const PORT = Number(process.env.PORT || 5000);

async function start() {
  try {
    await pool.query("SELECT 1");

    app.listen(PORT, () => {
      console.log(`Credit & Loan API running on http://localhost:${PORT}`);
    });
  } catch (error) {
    console.error("Unable to connect to MySQL:", error.message);
    process.exit(1);
  }
}

start();
