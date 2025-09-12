# spring-next-app

# Spring Boot + Next.js App

This project has a **Spring Boot backend** and a **Next.js frontend**.

## 📂 Project Structure

spring-next-app/
├── backend/ # Spring Boot app
├── frontend/ # Next.js app


## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/spring-next-app.git
cd spring-next-app

cd backend
mvn spring-boot:run

Backend will be running at:
👉 http://localhost:8080

-------------------------

cd frontend
npm install
npm run dev

Frontend will be running at:
👉 http://localhost:3000

From the project root:
docker compose up --build

Stop containers:
docker compose down