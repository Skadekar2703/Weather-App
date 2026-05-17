# 🌦️ SkyCast - Android Weather App

SkyCast is a modern Android weather application built using Kotlin and XML layouts.  
The app provides real-time weather updates, hourly forecasts, 5-day forecasts, and dynamic weather-based UI experiences.

---

# ✨ Features

## Current Weather
- Real-time weather updates
- Temperature display
- Weather conditions
- Humidity
- Wind speed
- Pressure
- Visibility
- UV Index

## Location Detection
- Auto GPS location detection
- Current city weather updates

## Search Weather
- Search weather by city name
- Dynamic weather fetching

## Forecasts
- Hourly weather forecast
- 5-day weather forecast

## Dynamic UI
- Weather-based backgrounds
- Sunny theme
- Rainy theme
- Cloudy theme
- Night mode visuals

## Modern UI
- Minimal clean design
- Rounded cards
- Smooth animations
- Responsive layouts

---

# 🛠️ Tech Stack

- Kotlin
- XML Layouts
- MVVM Architecture
- Retrofit
- Coroutines
- WeatherAPI
- RecyclerView
- Glide
- Fused Location Provider

---

# 🏗️ Architecture

The project follows MVVM architecture:

```plaintext
UI → ViewModel → Repository → Retrofit API → WeatherAPI
```

---

# 📂 Project Structure

```plaintext
com.weatherapp

├── api
├── model
├── repository
├── ui
├── viewmodel
├── utils
```

---

# 🌐 API Used

Weather data is fetched using:

https://www.weatherapi.com/

---

# 📱 Screens

- Splash Screen
- Home Screen
- Search Screen
- Forecast Detail Screen

---

# 🚀 Future Improvements

- AQI support
- Weather notifications
- AI weather assistant
- Voice search
- Favorite cities
- Dark mode improvements

---

# ⚙️ Setup Instructions

## 1. Clone Repository

```bash
git clone https://github.com/Skadekar2703/Weather-App.git
```

---

## 2. Open in Android Studio

Open the project folder in Android Studio.

---

## 3. Add API Key

Get free API key from:

https://www.weatherapi.com/

Add your API key inside:

```plaintext
local.properties
```

Example:

```properties
WEATHER_API_KEY=YOUR_API_KEY
```

---

## 4. Run App

Connect Android device or emulator and run the project.

---

# 📌 Status

🚧 Currently under development

---

# 👨‍💻 Developer

Soham Kadekar

---

# ⭐ Support

If you like this project, consider giving it a star ⭐
