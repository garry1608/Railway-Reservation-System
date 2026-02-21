# Railway-Reservation-System

# 🚆 Railway Reservation System (Java Desktop Application)

A full-featured **Railway Reservation System** built using **Java Swing** and **JDBC**, designed to simulate real-world train ticket booking operations.  
The application provides an intuitive GUI for users to search trains, book tickets, select seats, track trains, and manage bookings, while also offering admin functionalities.

---

## 📌 Features

### 👤 User Module
- 🔐 User Registration & Login
- 🔎 Search Trains by route/date
- 🎟️ Book Train Tickets
- 💺 Seat Selection Interface
- 📜 View Booking History
- ❌ Cancel Bookings
- 📍 Track Train Status

### 🛠️ Admin Module
- 📊 Admin Dashboard
- 🗓️ Manage Train Schedules
- 📂 View All Bookings
- ⚙️ System Management

---

## 🧰 Tech Stack

- **Language:** Java  
- **GUI:** Java Swing  
- **Database:** JDBC (SQL Database)  
- **Architecture:** Event-Driven Desktop Application  

---

## 🗂️ Project Structure
📁 src
┣ 📄 RailwayApp.java → Application entry point
┣ 📄 LoginPage.java → User authentication
┣ 📄 RegisterPage.java → New user registration
┣ 📄 MainDashboard.java → User dashboard
┣ 📄 AdminDashboard.java → Admin controls
┣ 📄 SearchTrainPage.java → Train search
┣ 📄 BookTrainPage.java → Booking workflow
┣ 📄 SeatSelectionDialog.java → Seat selection UI
┣ 📄 BookingHistoryPage.java → User booking history
┣ 📄 CancelBookingPage.java → Cancel tickets
┣ 📄 TrackTrainPage.java → Train tracking
┣ 📄 TrainSchedulePage.java → Train schedules
┣ 📄 ViewBookingsPage.java → Admin booking view
┣ 📄 DatabaseConnection.java → DB connectivity
┗ 📄 UIUtils.java → UI helper utilities
