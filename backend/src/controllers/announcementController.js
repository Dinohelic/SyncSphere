const prisma = require('../config/prisma');
const { announcementSchema } = require('../validators/announcementValidation');

const getAnnouncements = async (req, res, next) => {
  try {
    const announcements = await prisma.announcement.findMany({
      orderBy: { createdAt: 'desc' },
      include: { createdBy: { select: { fullName: true } } },
    });
    res.status(200).json(announcements);
  } catch (error) {
    next(error);
  }
};

const createAnnouncement = async (req, res, next) => {
  try {
    if (req.user.role !== 'ADMIN') {
      return res.status(403).json({ message: 'Forbidden' });
    }
    const validatedData = announcementSchema.parse(req.body);
    const announcement = await prisma.announcement.create({
      data: { ...validatedData, createdById: req.user.userId },
    });
    res.status(201).json(announcement);
  } catch (error) {
    next(error);
  }
};

const updateAnnouncement = async (req, res, next) => {
  try {
    if (req.user.role !== 'ADMIN') {
      return res.status(403).json({ message: 'Forbidden' });
    }
    const { id } = req.params;
    const validatedData = announcementSchema.partial().parse(req.body);
    const updatedAnnouncement = await prisma.announcement.update({
      where: { id },
      data: validatedData,
    });
    res.status(200).json(updatedAnnouncement);
  } catch (error) {
    next(error);
  }
};

const deleteAnnouncement = async (req, res, next) => {
  try {
    if (req.user.role !== 'ADMIN') {
      return res.status(403).json({ message: 'Forbidden' });
    }
    const { id } = req.params;
    await prisma.announcement.delete({ where: { id } });
    res.status(204).send();
  } catch (error) {
    next(error);
  }
};

module.exports = {
  getAnnouncements,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
};

