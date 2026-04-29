# 📱 Numerical Analysis Solver (CS-252)

> A powerful, step-by-step **Numerical Analysis Android Solver** built with modern Android technologies and engineered for accuracy, performance, and usability.

---

## 🚀 Overview

**Numerical Analysis Solver** is a native Android application designed to solve complex numerical problems with **high precision** and **clear step-by-step explanations**.

Developed as a final project for the **Numerical Analysis (CS-252)** course, the app focuses on delivering:

* Accurate computations
* Transparent iterative processes
* A seamless and intuitive user experience

🎯 The goal: bridge the gap between **theory and practical computation** in numerical methods.

---

## 🎥 Demo

🔗 [Watch Demo on LinkedIn](https://www.linkedin.com/posts/anasayman13_androiddevelopment-kotlin-jetpackcompose-ugcPost-7455307202329968640-sT_q?utm_source=social_share_send&utm_medium=member_desktop_web&rcm=ACoAAEnBkXIBKTJ6DykIoZwkgWXGnxE_KUe1yIg)

---

## 🧮 Mathematical Modules

### 📌 1. Root Finding (Chapter 1)

* Bisection Method
* False Position Method
* Newton’s Method
* Simple Fixed Point
* Secant Method

✨ **Features:**

* Detailed iteration tables
* Convergence tracking
* High numerical precision

---

### 📌 2. Linear Algebraic Equations (Chapter 2)

* Gauss Elimination
* LU Decomposition
* Cramer's Rule
* Gauss-Jordan

⚙️ **Engineered with:**

* Partial Pivoting
* Zero-coefficient handling
* Division-by-zero prevention

---

### 📌 3. Optimization (Chapter 3)

* Golden Section Search

📈 Supports:

* Maximum & Minimum finding
* Iterative convergence display

---

## ⚙️ Core Engineering Features

### 🧠 Smart Math Parser

* Custom preprocessing using **Regex + exp4j**
* Handles implicit multiplication automatically

```text
5x → 5 * x
2(x+1) → 2 * (x+1)
```

---

### ⌨️ Custom Scientific Keypad

* Fully custom-built input system
* Eliminates dependency on OS keyboard
* Optimized for fast mathematical entry

---

### 🗂️ Calculation History & State Restoration

* Built using **Room Database**
* Complex data (Matrices & Vectors) handled via **Gson serialization**

✨ Features:

* Save all previous computations
* One-click **Re-run**
* Full state restoration

---

## 🏗️ Tech Stack & Architecture

### 💻 Language

* Kotlin

### 🎨 UI

* Jetpack Compose (Material 3)

### 🧱 Architecture

* Clean Architecture
* MVVM

### ⚡ Concurrency

* Kotlin Coroutines
* Flow

### 💾 Local Storage

* Room Database

### 🔄 Serialization

* Gson

---

## 📂 Project Structure

```
NumericalAnalysisSolver/
│
├── app/
│   ├── presentation/
│   │   ├── screens/
│   │   ├── components/
│   │   └── viewmodels/
│   │
│   ├── domain/
│   │   ├── models/
│   │   ├── usecases/
│   │   └── repository/
│   │
│   ├── data/
│   │   ├── local/
│   │   ├── parser/
│   │   └── repository_impl/
│   │
│   ├── utils/
│   └── MainActivity.kt
│
└── build.gradle
```

---

## 📸 Screenshots

### 🏠 Home Screen

<p align="center">
  <img src="https://github.com/user-attachments/assets/6938697a-14e6-4b2f-b3ad-931b2eb28ba4" width="45%" style="margin:10px;" />
  <img src="https://github.com/user-attachments/assets/fdd7b5d0-96ec-4224-8001-376becdc85e6" width="45%" style="margin:10px;" />
</p>

---

### 🧮 Solver Interface

<p align="center">
  <img src="https://github.com/user-attachments/assets/eeea2a18-e7ed-4e94-9dba-5496047e1ee1" width="60%" />
</p>

---

### 📊 Iteration Table

<p align="center">
  <img src="https://github.com/user-attachments/assets/22f39db0-96b3-402a-9adf-b0b0e2604377" width="80%" />
</p>

---

### 🗂️ History Screen

<p align="center">
  <img src="https://github.com/user-attachments/assets/275d0fb3-268f-4522-ad86-6c31d35b6fbf" width="45%" style="margin:10px;" />
  <img src="https://github.com/user-attachments/assets/3e20cf70-d258-4c72-a69b-cb73882e1dff" width="45%" style="margin:10px;" />
</p>

---

## 👨‍💻 Team & Credits

### 🧑‍💻 Developed By

* **Anas Ayman El-Gebaili** (Native Android Developer)
* Adham Sayed Kamel
* Ahmed Mohamed Hamid

---

### 🎓 Academic Supervision

* **Dr. Rania Ahmed**

---

## 🌟 Key Highlights

* 🔥 Production-level Android architecture
* 📐 Accurate numerical computation engine
* ⚡ Optimized performance with Coroutines
* 🧩 Modular & scalable codebase
* 🎯 Designed for real academic & practical use

---

## 📌 Future Improvements

* Graph plotting for functions 📈
* Export results as PDF 📄
* Dark/Light theme customization 🌗
* More numerical methods (Interpolation, Integration, ODEs)

---

## 📄 License

This project is developed for academic purposes.
Feel free to use or extend it with proper attribution.

---

## 💬 Final Note

This project reflects a strong combination of:

* Mathematical problem-solving
* Software engineering best practices
* Modern Android development

If you're interested in numerical computing or Android engineering, this project is a solid reference point.

---
