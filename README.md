# Numerical Analysis Solver (CS-252)

Android Native (Kotlin + Jetpack Compose) application that implements numerical analysis methods from:
- **Chapter 1: Finding Roots of Polynomials**
- **Chapter 2: Linear Algebraic Equations**

Built with **MVVM** architecture and a clean, student-friendly GUI.

---

## ✨ Features

### Chapter 1 — Root Finding
- Bisection Method
- False Position (Regula Falsi)
- Newton’s Method
- Fixed Point Iteration
- Secant Method

### Chapter 2 — Linear Systems
- Gauss Elimination
- LU Decomposition
- Cramer’s Rule
- Gauss-Jordan

### Output & Learning Support
- Shows final result (root / solution vector)
- Displays iterations table (step-by-step)
- Input validation and error handling (division by zero, invalid interval, non-convergence, etc.)

### Bonus (Optional)
- Function plotting (f(x)) and highlighting root
- Export results (screenshot / share)

---

## 🧱 Tech Stack
- **Kotlin**
- **Jetpack Compose**
- **MVVM Architecture**
- (Optional) MPAndroidChart for plotting

---

## 🧩 Project Structure (MVVM)


com.numerical.analysis.solver
│
├─ navigation/ # Routes + NavGraph
├─ ui/
│ ├─ screens/ # Compose screens per feature
│ ├─ components/ # Shared reusable UI components
│ ├─ state/ # UiState + events
│ └─ theme/ # Color.kt / Theme.kt / Type.kt
│
├─ domain/ # UseCases + repository interfaces
├─ data/ # repository implementations
└─ core/ # Numerical algorithms (pure Kotlin)


---

## 🚀 Getting Started

### Requirements
- Android Studio (latest stable recommended)
- JDK (Android Studio Embedded JDK is fine)
- Android SDK installed

### Run the App
1. Open the project in Android Studio
2. Sync Gradle
3. Run on emulator or physical device

---

## 🧪 How to Use (User Guide)
1. Choose **Chapter 1** or **Chapter 2**
2. Select a numerical method
3. Enter function/matrix inputs + tolerance + max iterations
4. Press **Solve**
5. View:
   - Final answer
   - Iterations/steps table

---

## 📸 Screenshots
> Add screenshots after completing each method.

- Home Screen  
  ![Home](docs/screenshots/home.png)

- Root Finding Example  
  ![Root Finding](docs/screenshots/rootfinding.png)

- Linear Systems Example  
  ![Linear Systems](docs/screenshots/linearsystems.png)

---

## 📝 Documentation / Report Notes
Each method must include:
- **Page A:** Code snippet
- **Page B:** GUI output screenshot

The final report format follows the **Project Guide**.

---

## 👥 Team Information
- Study Group (Section): **[G-Number]**
- Team Members (3):
  - Anas Ayman El-Gebaili 
  - Adham Sayed Kamel
  - Ahmed Mohamed Hamid 
 

Supervised by: **Dr. Rania Ahmed**

---

## ✅ License
For educational use (CS-252 project).
