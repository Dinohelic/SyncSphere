const { z } = require('zod');

const taskSchema = z.object({
  title: z.string().min(1, 'Title is required'),
  description: z.string().optional(),
  priority: z.enum(['LOW', 'MEDIUM', 'HIGH']).optional(),
  status: z.enum(['TODO', 'IN_PROGRESS', 'COMPLETED']).optional(),
  dueDate: z.string().datetime().optional(),
  assignedToIds: z.array(z.string().uuid('Invalid user ID')).min(1, 'Assign at least one user'),
});

module.exports = {
  taskSchema,
};

