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
    role: source.role ?? source.user_role ?? source.roleType ?? undefined,
  };
};

const registerUser = async (req, res, next) => {
  try {
    const validatedData = registerSchema.parse(normalizeAuthBody(req.body));

    const existingUser = await prisma.user.findUnique({
      where: { email: validatedData.email },
    });

    if (existingUser) {
      return res.status(409).json({ success: false, message: 'Email is already in use' });
    }

    const hashedPassword = await bcrypt.hash(validatedData.password, 10);

    const user = await prisma.user.create({
      data: {
        fullName: validatedData.fullName,
        email: validatedData.email,
        password: hashedPassword,
        role: 'MEMBER',
      },
    });

    res.status(201).json({
      success: true,
      message: 'User registered successfully',
      data: {
        user: {
          id: user.id,
          fullName: user.fullName,
          email: user.email,
          role: user.role,
          createdAt: user.createdAt,
        },
      },
    });
    console.info({ event: 'user_registered', userId: user.id, email: user.email, role: user.role });
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
      return res.status(401).json({ success: false, message: 'Invalid credentials' });
    }

    const isPasswordValid = await bcrypt.compare(validatedData.password, user.password);

    if (!isPasswordValid) {
      return res.status(401).json({ success: false, message: 'Invalid credentials' });
    }

    const token = jwt.sign(
      { userId: user.id, role: user.role },
      process.env.JWT_SECRET || 'syncsphere_secret',
      { expiresIn: '7d' }
    );

    res.status(200).json({
      success: true,
      message: 'Login successful',
      data: {
        token,
        user: {
          id: user.id,
          fullName: user.fullName,
          email: user.email,
          role: user.role,
        },
      },
    });
    console.info({ event: 'user_login', userId: user.id, email: user.email });
  } catch (error) {
    next(error);
  }
};

module.exports = {
  registerUser,
  loginUser,
};

