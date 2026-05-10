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

module.exports = {
  getUsers,
};