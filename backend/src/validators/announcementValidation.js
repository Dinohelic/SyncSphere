const { z } = require('zod');

const announcementSchema = z.object({
  title: z.string().min(1, 'Title is required'),
  message: z.string().min(1, 'Message is required'),
  pinned: z.boolean().optional(),
});

module.exports = {
  announcementSchema,
};

