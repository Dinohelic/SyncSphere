const prisma = require('../config/prisma');
const { taskSchema } = require('../validators/taskValidation');

const getTasks = async (req, res, next) => {
  try {
    const { user } = req;
    let tasks;

    if (user.role === 'ADMIN') {
      tasks = await prisma.task.findMany({
        include: { assignedTo: true },
      });
    } else {
      tasks = await prisma.task.findMany({
        where: { assignedToId: user.userId },
        include: { assignedTo: true },
      });
    }

    res.status(200).json(tasks);
  } catch (error) {
    next(error);
  }
};

const createTask = async (req, res, next) => {
  try {
    if (req.user.role !== 'ADMIN') {
      return res.status(403).json({
        message: 'Forbidden',
      });
    }

    const validatedData = taskSchema.parse(req.body);

    const task = await prisma.task.create({
      data: validatedData,
    });

    res.status(201).json(task);
  } catch (error) {
    next(error);
  }
};

const updateTask = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { user } = req;

    const task = await prisma.task.findUnique({
      where: { id },
    });

    if (!task) {
      return res.status(404).json({
        message: 'Task not found',
      });
    }

    if (
      user.role === 'MEMBER' &&
      task.assignedToId !== user.userId
    ) {
      return res.status(403).json({
        message: 'Forbidden',
      });
    }

    let validatedData;

    if (user.role === 'ADMIN') {
      validatedData = taskSchema.partial().parse(req.body);
    } else {
      const { status } = req.body;

      if (!status) {
        return res.status(400).json({
          message: 'Only status can be updated',
        });
      }

      validatedData = { status };
    }

    const updatedTask = await prisma.task.update({
      where: { id },
      data: validatedData,
    });

    res.status(200).json(updatedTask);
  } catch (error) {
    next(error);
  }
};

const deleteTask = async (req, res, next) => {
  try {
    if (req.user.role !== 'ADMIN') {
      return res.status(403).json({
        message: 'Forbidden',
      });
    }

    const { id } = req.params;

    await prisma.task.delete({
      where: { id },
    });

    res.status(204).send();
  } catch (error) {
    next(error);
  }
};

module.exports = {
  getTasks,
  createTask,
  updateTask,
  deleteTask,
};