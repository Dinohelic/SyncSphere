const express = require('express');
const {
  getAnnouncements,
  createAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
} = require('../controllers/announcementController');
const { verifyToken } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(verifyToken);

router.route('/').get(getAnnouncements).post(createAnnouncement);
router.route('/:id').put(updateAnnouncement).delete(deleteAnnouncement);

module.exports = router;

