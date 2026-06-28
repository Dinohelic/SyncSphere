<div align="center">

# 🔄 SyncSphere

**A modern collaborative workspace — built for teams that move fast.**

SyncSphere is a full-stack team productivity platform featuring a native Android app and a cloud-deployed REST API. It enables organizations to manage tasks, schedule events, broadcast announcements, and control access — all from a single, intuitive interface.

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Node.js](https://img.shields.io/badge/Backend-Node.js-339933?style=flat-square&logo=node.js&logoColor=white)](https://nodejs.org)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://postgresql.org)
[![License: ISC](https://img.shields.io/badge/License-ISC-blue?style=flat-square)](LICENSE)

</div>

---

## 📸 Overview

SyncSphere bridges the gap between scattered tools by unifying task tracking, event scheduling, announcements, and team administration into a cohesive Android experience — backed by a production-grade Node.js API deployed on Render.

---

## 📲 Download & Try It

<div align="center">

### [⬇️ Download APK](https://drive.google.com/file/d/1o-TZ5As9d9j4aBsn1vDCPSQJBRWN3YMo/view?usp=sharing)

Scan the QR code to download and install the app on your Android device:

<img src="syncsphere_qr.png" alt="Scan to download SyncSphere APK" width="180"/>

> ⚠️ You may need to enable **"Install from unknown sources"** in your Android settings.

</div>

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **JWT Authentication** | Secure login & registration with token-based sessions |
| 🛡️ **Role-Based Access Control** | Separate flows and permissions for `ADMIN` and `MEMBER` |
| ✅ **Task Management** | Create, assign, and track tasks with priority (`Low` / `Medium` / `High`) and status (`To Do` / `In Progress` / `Completed`) |
| 📅 **Event Scheduling** | Browse and manage upcoming organizational events and venues |
| 📢 **Announcements** | Pinned and recent announcements with real-time visibility |
| 🧑‍💼 **Admin Dashboard** | Promote members, monitor project progress, manage the org |
| 👤 **Profile Management** | View personal info, organization role, and account details |

---

## 🛠️ Tech Stack

### 📱 Android App

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| DI | Hilt |
| Networking | Retrofit + OkHttp |
| Async | Coroutines + Flow |
| Image Loading | Coil |

### 🖥️ Backend

| Layer | Technology |
|---|---|
| Runtime | Node.js |
| Framework | Express.js |
| ORM | Prisma |
| Database | PostgreSQL |
| Validation | Zod |
| Auth | JWT + Bcrypt |
| Deployment | Render |

---

## 📂 Project Structure

```
SyncSphere/
├── app/                          # Android Application
│   ├── src/main/java/
│   │   ├── data/                 # Repositories, API models, DTOs
│   │   ├── di/                   # Hilt modules
│   │   ├── ui/                   # Composable screens & ViewModels
│   │   └── utils/                # Helpers & extensions
│   └── build.gradle.kts
│
├── backend/                      # Node.js REST API
│   ├── src/
│   │   ├── routes/               # API endpoint definitions
│   │   ├── controllers/          # Request handlers
│   │   └── validators/           # Zod schemas
│   ├── prisma/
│   │   ├── schema.prisma         # Database schema
│   │   └── migrations/           # Migration history
│   └── package.json
│
└── README.md
```

---

## ⚙️ Getting Started

### Prerequisites

- Node.js `v18+`
- PostgreSQL database (local or hosted)
- Android Studio (Hedgehog or newer)
- Android SDK 26+

---

### 🖥️ Backend Setup

```bash
# 1. Navigate to backend directory
cd backend

# 2. Install dependencies
npm install

# 3. Set up environment variables
cp .env.example .env
```

Edit `.env` with your values:

```env
DATABASE_URL="postgresql://USER:PASSWORD@HOST:PORT/DATABASE"
JWT_SECRET="your-super-secret-key"
PORT=3000
```

```bash
# 4. Run database migrations
npx prisma migrate dev

# 5. (Optional) Seed the database
npx prisma db seed

# 6. Start the development server
npm run dev
```

> The API will be live at `http://localhost:3000`

---

### 📱 Android App Setup

1. Open the project root in **Android Studio**
2. Let Gradle sync complete
3. In `app/src/main/java/.../network/RetrofitInstance.kt`, update:
   ```kotlin
   private const val BASE_URL = "http://<YOUR_BACKEND_IP>:3000/"
   ```
4. Connect a device or start an emulator
5. Hit **Run ▶️**

> For a cloud-deployed backend, replace the IP with your Render service URL.

---

## 🔌 API Overview

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Authenticate and receive JWT |
| `GET` | `/api/tasks` | Fetch all tasks for the org |
| `POST` | `/api/tasks` | Create a new task |
| `PATCH` | `/api/tasks/:id` | Update task status or priority |
| `GET` | `/api/events` | List all upcoming events |
| `GET` | `/api/announcements` | Fetch announcements |
| `GET` | `/api/admin/members` | Admin: list all members |
| `PATCH` | `/api/admin/promote/:id` | Admin: promote a member |

> All protected routes require `Authorization: Bearer <token>` header.

---

## 🌐 Deployment

The backend is deployed on **[Render](https://render.com)**. To deploy your own instance:

1. Push the `backend/` directory to a GitHub repo
2. Create a new **Web Service** on Render, pointing to that repo
3. Add the environment variables (`DATABASE_URL`, `JWT_SECRET`) in Render's dashboard
4. Render will auto-deploy on every push to `main`

---

## 🛣️ Roadmap

- [ ] Push notifications via FCM
- [ ] Real-time updates using WebSockets
- [ ] File attachments on tasks
- [ ] Dark mode support
- [ ] iOS companion app

---

## 🤝 Contributing

Contributions are welcome! To get started:

```bash
# Fork the repo, then:
git checkout -b feature/your-feature-name
git commit -m "feat: add your feature"
git push origin feature/your-feature-name
# Open a Pull Request
```

Please follow conventional commits and ensure Prisma schema changes include a migration.

---

## 📄 License

This project is licensed under the **ISC License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  Built with ❤️ by <a href="https://github.com/dinohelic">Deepak Yadav</a>
</div>
