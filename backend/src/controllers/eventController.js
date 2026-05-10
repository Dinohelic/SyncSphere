const prisma = require('../config/prisma');
const { eventSchema } = require('../validators/eventValidation');

const getEvents = async (req, res, next) => {
  try {
    const events = await prisma.event.findMany({ orderBy: { eventDate: 'asc' } });
    res.status(200).json(events);
  } catch (error) {
    next(error);
  }
};

const createEvent = async (req, res, next) => {
  try {
    if (req.user.role !== 'ADMIN') {
      return res.status(403).json({ message: 'Forbidden' });
    }
    const validatedData = eventSchema.parse(req.body);
    const event = await prisma.event.create({ data: validatedData });
    res.status(201).json(event);
  } catch (error) {
    next(error);
  }
};

const updateEvent = async (req, res, next) => {
  try {
    if (req.user.role !== 'ADMIN') {
      return res.status(403).json({ message: 'Forbidden' });
    }
    const { id } = req.params;
    const validatedData = eventSchema.partial().parse(req.body);
    const updatedEvent = await prisma.event.update({
      where: { id },
      data: validatedData,
    });
    res.status(200).json(updatedEvent);
  } catch (error) {
    next(error);
  }
};

const deleteEvent = async (req, res, next) => {
  try {
    if (req.user.role !== 'ADMIN') {
      return res.status(403).json({ message: 'Forbidden' });
    }
    const { id } = req.params;
    await prisma.event.delete({ where: { id } });
    res.status(204).send();
  } catch (error) {
    next(error);
  }
};

module.exports = {
  getEvents,
  createEvent,
  updateEvent,
  deleteEvent,
};

