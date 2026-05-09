const { ZodError } = require('zod');

const errorHandler = (err, req, res, next) => {
  console.error(err);

  if (err instanceof ZodError) {
    return res.status(400).json({
      message: 'Validation failed',
      errors: err.errors.map((e) => ({ field: e.path.join('.'), message: e.message })),
    });
  }

  res.status(500).json({ message: 'Internal server error' });
};

module.exports = errorHandler;

