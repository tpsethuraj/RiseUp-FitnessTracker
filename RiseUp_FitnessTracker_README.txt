
RiseUp-FitnessTracker - Mobile Application

Project Overview

RiseUp-FitnessTracker is a mobile fitness application designed to help young adults monitor their daily health activities using real-time data from phone sensors and Google Fit. The app allows users to track steps, workouts, calories burned, and overall activity trends through a clean and interactive dashboard.

The application provides a personalized fitness environment where users can securely log in, view activity summaries, monitor step history, and manage hydration goals. RiseUp-FitnessTracker is developed using Kotlin, Jetpack Compose, MVVM architecture, Firebase Authentication, Google Fit integration, and Room Database to ensure a smooth, accurate, and reliable fitness tracking experience. fileciteturn20file0


Key Features

Secure login and signup using Firebase and Google Sign-In  
Personalized home screen with Hello Username  
Fitness dashboard displaying steps, calories, distance, and activity  
Automatic step tracking using Google Fit and device sensors  
Real-time recent activity updates  
Graph-based step history visualization  
Activity history screen  
Water intake tracker with custom goals  
Motivational pop-up feedback on goal completion  
Light and dark theme switching  
Settings module for user preferences  
Modern UI with smooth animations  


Technologies Used

Language: Kotlin  
User Interface: Jetpack Compose  
Architecture: MVVM (Clean Architecture refactor in Sprint 5)  
Authentication: Firebase Authentication and Google Sign-In  
Fitness Data: Google Fit API  
Sensors: Accelerometer  
Local Storage: Room Database  
IDE: Android Studio  
Version Control: Git and GitHub  
Project Management: Trello  


APIs and System Services Used

Google Fit API  
Used to track steps, fitness activity, and retrieve historical activity data.

Firebase Authentication API  
https://firebase.google.com/docs/auth  
Used for secure login and account management.

Device Sensor APIs  
Used for accelerometer-based step detection.

Room Database  
Used for offline storage of fitness and hydration data.


Application Flow

Splash Screen → Login or Signup → Home Dashboard → Activity Tracking → Step History → Water Intake Tracker → Settings → Profile  


Security Implementation

Firebase Authentication ensures secure user access  
Google Sign-In provides fast and trusted login  
Only authenticated users can view fitness data  
Sensitive credentials are not stored locally  
Local fitness records are stored securely using Room Database  


Installation and Setup

Clone the project repository  
Open the project in Android Studio  
Allow Gradle to complete the build process  
Configure Firebase and enable Email/Password and Google Sign-In  
Connect Google Fit API  
Run the application on an emulator or physical Android device  


Agile Development

The project followed a sprint-based Agile methodology across five sprints. Development progressed from authentication and UI foundation to Google Fit integration, activity tracking, data visualization, and finally clean architecture refactoring, water tracking features, and UI personalization. This supported continuous improvement and stable feature delivery. fileciteturn20file0


Future Enhancements

Workout type detection  
Cloud sync across devices  
Advanced fitness analytics  
Wearable device integration  
AI-based health recommendations  


Useful Links

GitHub: add your GitHub link  
Trello: add your Trello link  
