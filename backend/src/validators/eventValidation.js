const { z } = require('zod');

const eventSchema = z.object({
  title: z.string().min(1, 'Title is required'),
  venue: z.string().min(1, 'Venue is required'),
  description: z.string().optional(),
  eventDate: z.string().datetime('Invalid date format'),
});

module.exports = {
  eventSchema,
};

