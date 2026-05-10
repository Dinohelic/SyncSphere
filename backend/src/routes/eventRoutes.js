const express = require('express');
const {
  getEvents,
  createEvent,
  updateEvent,
  deleteEvent,
} = require('../controllers/eventController');
const { verifyToken } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(verifyToken);

router.route('/').get(getEvents).post(createEvent);
router.route('/:id').put(updateEvent).delete(deleteEvent);

module.exports = router;

