const express = require('express');
const { getUsers } = require('../controllers/userController');
const { verifyToken } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(verifyToken);
router.get('/', getUsers);

module.exports = router;