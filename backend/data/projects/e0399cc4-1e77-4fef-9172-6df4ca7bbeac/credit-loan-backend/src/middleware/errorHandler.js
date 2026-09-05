function notFound(req, res) {
  res.status(404).json({
    success: false,
    message: `Route not found: ${req.method} ${req.originalUrl}`
  });
}

function errorHandler(err, req, res, next) {
  console.error(err);

  if (err.code === "ER_DUP_ENTRY") {
    return res.status(409).json({
      success: false,
      message: "Duplicate value violates a unique constraint.",
      detail: err.sqlMessage
    });
  }

  if (err.code === "ER_NO_REFERENCED_ROW_2") {
    return res.status(400).json({
      success: false,
      message: "Referenced record does not exist."
    });
  }

  res.status(500).json({
    success: false,
    message: "Internal server error.",
    detail: process.env.NODE_ENV === "development" ? err.message : undefined
  });
}

module.exports = { notFound, errorHandler };
