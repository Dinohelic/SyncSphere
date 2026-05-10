const errorHandler = (err, req, res, next) => {
  console.error(err);

  if (err?.issues && Array.isArray(err.issues)) {
    return res.status(400).json({
      success: false,
      message: 'Validation failed',
      issues: err.issues,
    });
  }

  res.status(err.status || 500).json({
    success: false,
    message: err.message || "Server Error",
  });
};

module.exports = errorHandler;