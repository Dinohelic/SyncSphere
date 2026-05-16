const express = require('express');
const { getUsers, promoteUser } = require('../controllers/userController');
const { verifyToken } = require('../middleware/authMiddleware');

const router = express.Router();

router.use(verifyToken);
router.get('/', getUsers);
router.put('/:id/promote', promoteUser);

module.exports = router;