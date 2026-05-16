const { z, ZodError } = require('zod');

const trimAndLower = (val) => (typeof val === 'string' ? val.trim().toLowerCase() : val);
const trim = (val) => (typeof val === 'string' ? val.trim() : val);
const toUpper = (val) => (typeof val === 'string' ? val.trim().toUpperCase() : val);

const registerSchema = z.object({
  fullName: z.preprocess(trim, z.string().min(2, 'Full name must be at least 2 characters')),
  email: z.preprocess(trimAndLower, z.string().email('Invalid email address')),
  password: z.preprocess(trim, z.string().min(6, 'Password must be at least 6 characters')),
  // role: accepts any casing, normalizes to UPPERCASE, and validates against the enum
  role: z.preprocess(toUpper, z.enum(['ADMIN', 'MEMBER']).optional()),
});

const loginSchema = z.object({
  email: z.preprocess(trimAndLower, z.string().email('Invalid email address')),
  password: z.preprocess(trim, z.string().min(1, 'Password is required')),
});

module.exports = {
  registerSchema,
  loginSchema,
};

