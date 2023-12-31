# Café Android App

Welcome to the Café Android App! This app is designed to manage a café's menu, orders, and more. The application is built using Kotlin, Firebase Authentication, Firestore for data storage, and Firebase Storage for handling images.

## Features

- **User Authentication:** Firebase Authentication is used to securely authenticate users, differentiating between admins and regular users.

- **Admin Panel:**
  - View all menu items.
  - Add, update, and delete menu items.
  - View and manage orders.
  - Change order status (preparing, ready, completed).
  - View detailed order information.

- **User Panel:**
  - Access user-specific features.
  - Place orders and view order history.

## Architecture

The app follows the Model-View-Controller (MVC) architecture to organize code and separate concerns:

- **Model:** Manages data and business logic.
- **View:** Displays the UI and interacts with the user.
- **Controller:** Handles user input, updates the model, and updates the view.

## Tech Stack

- **Kotlin:** The programming language used for Android app development.
- **Firebase Authentication:** For secure user authentication.
- **Firestore:** A NoSQL database for storing menu items, orders, and other data.
- **Firebase Storage:** Used to store and retrieve images.

## Getting Started

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/mboma99/AfroAroma
