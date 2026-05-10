const express = require('express');
const prisma = require('../config/prisma');
const { verifyToken } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(verifyToken);

router.get('/', async (req, res, next) => {
  try {
    const totalTasks = await prisma.task.count();

    const completedTasks = await prisma.task.count({
      where: { status: 'COMPLETED' },
    });

    const pendingTasks = totalTasks - completedTasks;

    const highPriorityTasks = await prisma.task.count({
      where: { priority: 'HIGH' },
    });

    res.status(200).json({
      totalTasks,
      completedTasks,
      pendingTasks,
      highPriorityTasks,
    });
  } catch (error) {
    next(error);
  }
});

module.exports = router;