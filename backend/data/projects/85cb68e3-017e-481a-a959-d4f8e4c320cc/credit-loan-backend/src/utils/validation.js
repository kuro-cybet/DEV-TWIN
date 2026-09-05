const applicationTypes = ["CreditCard", "PersonalLoan", "HomeLoan"];

const statusValues = [
  "SUBMITTED",
  "CREDIT_CHECK",
  "UNDERWRITING",
  "FINAL_APPROVAL",
  "REJECTED",
  "DISBURSED"
];

const stageValues = [
  "CREDIT_CHECK",
  "UNDERWRITING",
  "FINAL_APPROVAL",
  "DISBURSEMENT"
];

const decisionValues = ["PENDING", "APPROVED", "REJECTED"];

function requireFields(body, fields) {
  const missing = fields.filter(
    (field) =>
      body[field] === undefined ||
      body[field] === null ||
      body[field] === ""
  );

  return missing;
}

function isPositiveNumber(value) {
  return Number.isFinite(Number(value)) && Number(value) >= 0;
}

module.exports = {
  applicationTypes,
  statusValues,
  stageValues,
  decisionValues,
  requireFields,
  isPositiveNumber
};
