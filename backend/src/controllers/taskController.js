const prisma = require('../config/prisma');
const { taskSchema } = require('../validators/taskValidation');

const taskInclude = {
  assignedMembers: {
    select: {
      id: true,
      fullName: true,
      email: true,
      role: true,
    },
  },
};

const getTasks = async (req, res, next) => {
  try {
    const { user } = req;
    let tasks;

    if (user.role === 'ADMIN') {
      tasks = await prisma.task.findMany({
        include: taskInclude,
      });
    } else {
      tasks = await prisma.task.findMany({
        where: { assignedMembers: { some: { id: user.userId } } },
        include: taskInclude,
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

    const { assignedToIds, ...taskData } = validatedData;

    const task = await prisma.task.create({
      data: {
        ...taskData,
        assignedMembers: {
          connect: assignedToIds.map((id) => ({ id })),
        },
      },
      include: taskInclude,
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
      include: taskInclude,
    });

    if (!task) {
      return res.status(404).json({
        message: 'Task not found',
      });
    }

    if (user.role === 'MEMBER' && !task.assignedMembers?.some((member) => member.id === user.userId)) {
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

    const { assignedToIds, ...taskData } = validatedData;
    const updateData = { ...taskData };

    if (user.role === 'ADMIN' && Array.isArray(assignedToIds)) {
      updateData.assignedMembers = {
        set: [],
        connect: assignedToIds.map((memberId) => ({ id: memberId })),
      };
    }

    const updatedTask = await prisma.task.update({
      where: { id },
      data: updateData,
      include: taskInclude,
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