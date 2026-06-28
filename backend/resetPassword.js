const bcrypt = require("bcrypt");
const prisma = require("./src/config/prisma");

async function updateUser() {
  try {
  
    const email = "Your Email";
    const newPassword = "New Password";
    const newRole = "MEMBER"; // Use "ADMIN" or "MEMBER"
    

    const hashedPassword = await bcrypt.hash(newPassword, 10);

    const updatedUser = await prisma.user.update({
      where: { email },
      data: {
        password: hashedPassword, 
        role: newRole,
      },
    });

    console.log("✅ User updated successfully!");
    console.log("--------------------------------");
    console.log(`Name : ${updatedUser.fullName}`);
    console.log(`Email: ${updatedUser.email}`);
    console.log(`Role : ${updatedUser.role}`);
    console.log(`Password: ${newPassword}`);
  } catch (error) {
    console.error("❌ Error updating user:");
    console.error(error.message);
  } finally {
    await prisma.$disconnect();
  }
}

updateUser();