const { ZodError } = require('zod');
const { Prisma } = require('@prisma/client');

const formatZodMessage = (zodErr) => {
  try {
    const messages = zodErr.errors.map((e) => e.message).filter(Boolean);
    return messages.length ? messages.join('. ') : 'Invalid input';
  } catch (e) {
    return 'Invalid input';
  }
};

const errorHandler = (err, req, res, next) => {
  // Structured logging (avoid sensitive data)
  console.error({
    message: err.message,
    name: err.name,
    code: err.code,
    route: `${req.method} ${req.originalUrl}`,
    stack: process.env.NODE_ENV === 'production' ? undefined : err.stack,
  });

  // Zod validation errors
  if (err instanceof ZodError) {
    const message = formatZodMessage(err);
    return res.status(400).json({
      success: false,
      message,
    });
  }

  // Prisma known errors (e.g., unique constraint)
  if (err instanceof Prisma.PrismaClientKnownRequestError) {
    // Unique constraint violation
    if (err.code === 'P2002') {
      const target = (err.meta && err.meta.target) || [];
      return res.status(409).json({
        success: false,
        message: `Resource already exists (${target.join(', ')})`,
      });
    }

    return res.status(400).json({ success: false, message: 'Database error' });
  }

  // Prisma validation/runtime errors
  if (err instanceof Prisma.PrismaClientValidationError || err instanceof Prisma.PrismaClientUnknownRequestError) {
    return res.status(400).json({ success: false, message: 'Database request error' });
  }

  // JWT errors
  if (err.name === 'TokenExpiredError') {
    return res.status(401).json({ success: false, message: 'Session expired. Please log in again.' });
  }
  if (err.name === 'JsonWebTokenError') {
    return res.status(401).json({ success: false, message: 'Invalid authentication token' });
  }

  // Fallback for other errors
  const status = err.status || 500;
  const message = err.message || 'Server Error';

  return res.status(status).json({ success: false, message });
};

module.exports = errorHandler;