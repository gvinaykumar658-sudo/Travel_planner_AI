  # TravelMate — Vehicle Rental Management System

Full-stack project: **Spring Boot** (backend REST API + JWT auth) and **React (Vite)** (frontend).
Three roles: **Customer**, **Vendor**, **Admin**.

```
travelmate/
├── backend/     Spring Boot API (Java, Maven)
└── frontend/    React app (Vite)
```

---

## 1. Prerequisites (install once)

- **Java 17+** — check with `java -version`
- **Maven** — check with `mvn -version` (or use the included `mvnw` wrapper if you add one; IntelliJ/VS Code's Java extension pack also runs Maven for you)
- **Node.js 18+** and npm — check with `node -v`
- **VS Code** with these extensions: *Extension Pack for Java*, *Spring Boot Extension Pack*, *ES7+ React snippets* (optional but helpful)

No database install needed — the backend uses an in-memory **H2** database by default, so it runs with zero setup. (Instructions to switch to MySQL are in `backend/src/main/resources/application.properties`.)

---

## 2. Run it locally (2 terminals)

### Backend (Terminal 1)
```bash
cd backend
mvn spring-boot:run
```
Runs on **http://localhost:8080**. On first run it prints demo login credentials in the console:
```
Admin:    admin@travelmate.com / admin123
Vendor:   vendor@travelmate.com / vendor123
Customer: customer@travelmate.com / customer123
```

### Frontend (Terminal 2)
```bash
cd frontend
npm install
npm run dev
```
Runs on **http://localhost:5173** — open that in your browser.

That's it — log in with one of the demo accounts above, or register your own.

---

## 3. Opening in VS Code

1. Open the whole `travelmate` folder in VS Code (`File → Open Folder`).
2. Use two integrated terminals (`` Ctrl+` ``) — one for `backend`, one for `frontend` — and run the commands above in each.
3. The Java extension pack will auto-detect the Maven project in `backend/`.

---

## 4. Pushing to GitHub

```bash
cd travelmate
git init
git add .
git commit -m "Initial commit: TravelMate vehicle rental management system"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

---

## 5. Deploying (free tiers)

**Backend → Render**
1. Push this repo to GitHub (above).
2. On [render.com](https://render.com) → New → Web Service → connect your repo, set root directory to `backend`.
3. Build command: `mvn clean package -DskipTests`
4. Start command: `java -jar target/travelmate-backend-1.0.0.jar`
5. Once deployed, copy the live URL (e.g. `https://travelmate-backend.onrender.com`).

**Frontend → Vercel or Netlify**
1. On [vercel.com](https://vercel.com) → New Project → import your repo, set root directory to `frontend`.
2. Add an environment variable: `VITE_API_URL` = `https://<your-render-url>/api`
3. Deploy — Vercel auto-detects Vite.

Also add your deployed frontend URL to the CORS list in `backend/src/main/java/com/travelmate/config/SecurityConfig.java` if you lock it down further (currently allows all origins for simplicity).

---

## 6. Feature summary (for your report)

- JWT-based authentication with role-based access (Spring Security)
- Three roles: Customer, Vendor, Admin — each with their own dashboard
- Vehicle search/filter, booking with **overlapping-date prevention**
- Vendor: list vehicles (pending admin approval), view bookings, earnings estimate
- Admin: approve/reject vehicle listings, view all vehicles & bookings
- Clean, custom-designed responsive UI (no default component-library look)
- REST API tested via Postman-style routes under `/api/**`

## 7. API quick reference

| Method | Endpoint | Who | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | anyone | Create account |
| POST | `/api/auth/login` | anyone | Get JWT token |
| GET | `/api/vehicles/public` | anyone | Browse approved vehicles |
| POST | `/api/vehicles` | vendor | List a new vehicle |
| GET | `/api/vehicles/mine` | vendor | Vendor's own vehicles |
| GET | `/api/vehicles/pending` | admin | Vehicles awaiting approval |
| PUT | `/api/vehicles/{id}/approve` | admin | Approve a vehicle |
| POST | `/api/bookings` | customer | Book a vehicle |
| GET | `/api/bookings/mine` | customer | My trips |
| GET | `/api/bookings/vendor` | vendor | Bookings on my vehicles |
| PUT | `/api/bookings/{id}/cancel` | owner/admin | Cancel a booking |
