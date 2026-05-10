const prisma = require('../config/prisma');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const { registerSchema, loginSchema } = require('../validators/authValidation');

const normalizeAuthBody = (body) => {
  const source = body?.data ?? body?.loginRequest ?? body?.registerRequest ?? body ?? {};

  return {
    fullName: source.fullName ?? source.full_name ?? source.fullname ?? source.name ?? '',
    email: (source.email ?? '').toString().trim(),
    password: source.password ?? '',
  };
};

const registerUser = async (req, res, next) => {
  try {
    const validatedData = registerSchema.parse(normalizeAuthBody(req.body));

    const existingUser = await prisma.user.findUnique({
      where: { email: validatedData.email },
    });

    if (existingUser) {
      return res.status(400).json({ message: 'Email is already in use' });
    }

    const hashedPassword = await bcrypt.hash(validatedData.password, 10);

    const user = await prisma.user.create({
      data: {
        fullName: validatedData.fullName,
        email: validatedData.email,
        password: hashedPassword,
      },
    });

    res.status(201).json({
      message: 'User registered successfully',
      user: {
        id: user.id,
        fullName: user.fullName,
        email: user.email,
        role: user.role,
        createdAt: user.createdAt,
      },
    });
  } catch (error) {
    next(error);
  }
};

const loginUser = async (req, res, next) => {
  try {
    const validatedData = loginSchema.parse(normalizeAuthBody(req.body));

    // use case-insensitive email lookup to avoid failing when user types different casing
    const user = await prisma.user.findFirst({
      where: { email: { equals: validatedData.email, mode: 'insensitive' } },
    });

    if (!user) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }

    const isPasswordValid = await bcrypt.compare(validatedData.password, user.password);

    if (!isPasswordValid) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }

    const token = jwt.sign(
      { userId: user.id, role: user.role },
      process.env.JWT_SECRET || 'syncsphere_secret',
      { expiresIn: '7d' }
    );

    res.status(200).json({
      message: 'Login successful',
      token,
      user: {
        id: user.id,
        fullName: user.fullName,
        email: user.email,
        role: user.role,
      },
    });
  } catch (error) {
    next(error);
  }
};

module.exports = {
  registerUser,
  loginUser,
};

