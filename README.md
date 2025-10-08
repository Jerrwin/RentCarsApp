# RentCars Android App 🚗

[![Java](https://img.shields.io/badge/Language-Java-orange)](https://www.java.com/)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-blue)](https://firebase.google.com/)
[![Android Studio](https://img.shields.io/badge/IDE-Android%20Studio-green)](https://developer.android.com/studio)

A fully functional **car rental Android application** built with **Java** and **Firebase**.  
Users can browse cars, book them, view bookings, and submit feedback.  
Admins can manage cars, bookings, and feedback efficiently.

---

## Features

### User
- Sign up & login securely
- Browse available cars
- Book a car
- View and manage bookings
- Submit feedback

### Admin
- Admin login
- Add, edit, or remove cars
- View all bookings
- View user feedback

---

## Tech Stack

- **Language:** Java  
- **Database & Backend:** Firebase Realtime Database  
- **IDE:** Android Studio  
- **Minimum SDK:** 26 (Android 8.0 Oreo)

---

## Installation

1. Clone the repository:

```bash
git clone https://github.com/Jerrwin/RentCarsApp.git
```
2. Open the project in Android Studio.
3. Add your Firebase google-services.json file inside the app/ folder.
4. Sync Gradle and build the project.
5. Run the app on an emulator or real device.

---

## Project Structure

    com.example.rentcars
    ├── activities      # Activity Java files
    ├── adapters        # RecyclerView adapters
    ├── models          # Data classes for Firebase
    ├── helpers         # Helper classes
    ├── invoices        # Invoice generation files
    └── res             # Layouts, drawables, fonts, values, navigation


---

## Contributing

Feel free to fork the repo, submit issues, or create pull requests.  
Maintain the project structure and coding style.
