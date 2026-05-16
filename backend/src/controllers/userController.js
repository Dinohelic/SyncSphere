const prisma = require('../config/prisma');

const getUsers = async (req, res, next) => {
  try {
    const users = await prisma.user.findMany({
      select: {
        id: true,
        fullName: true,
        email: true,
        role: true,
      },
      orderBy: { fullName: 'asc' },
    });

    res.status(200).json(users);
  } catch (error) {
    next(error);
  }
};

const promoteUser = async (req, res, next) => {
  try {
    if (!req.user || req.user.role !== 'ADMIN') {
      return res.status(403).json({ success: false, message: 'Access denied' });
    }

    const { id } = req.params;

    if (!id) {
      return res.status(400).json({ success: false, message: 'User id is required' });
    }

    if (req.user.userId === id) {
      return res.status(400).json({ success: false, message: 'You are already an admin' });
    }

    const user = await prisma.user.findUnique({
      where: { id },
      select: { id: true, fullName: true, email: true, role: true },
    });

    if (!user) {
      return res.status(404).json({ success: false, message: 'User not found' });
    }

    if (user.role === 'ADMIN') {
      return res.status(400).json({ success: false, message: 'User is already an admin' });
    }

    const updatedUser = await prisma.user.update({
      where: { id },
      data: { role: 'ADMIN' },
      select: { id: true, fullName: true, email: true, role: true },
    });

    console.info({
      event: 'user_promoted',
      promotedUserId: updatedUser.id,
      promotedBy: req.user.userId,
    });

    return res.status(200).json({
      success: true,
      message: 'User promoted successfully',
      data: { user: updatedUser },
    });
  } catch (error) {
    next(error);
  }
};

module.exports = {
  getUsers,
  promoteUser,
};