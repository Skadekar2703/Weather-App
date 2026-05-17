# SkyCast — Design System & UX Breakdown

## 1. Design System
- **Color Palette:** 
    - Primary: Deep Sky Blue (#00AEEF)
    - Backgrounds: Dynamic gradients (Sunny: #FFB347 -> #FFCC33, Cloudy: #E4E5E6 -> #00416A, Rainy: #203A43 -> #2C5364, Night: #0F2027 -> #203A43).
    - Surface: White/Dark Gray with 10% opacity (Glassmorphism).
- **Typography:** Android Standard (Inter or Roboto).
    - Hero Temp: 96sp, Semi-bold.
    - Headings: 20sp, Medium.
    - Body: 14sp, Regular.
- **Spacing:** 8dp grid system. Standard margins: 16dp or 24dp.
- **Border Radius:** 24dp for cards (Modern Android look).
- **Icon Style:** Linear, 2dp stroke width, consistent 24dp bounding box.

## 2. App Style Direction
- **Mood:** Airy, modern, and reliable.
- **Visual Style:** Minimalist card-based UI with subtle glassmorphism and high-contrast typography.
- **Accessibility:** Minimum touch targets of 48dp, AA color contrast ratios for all text over dynamic backgrounds.

## 3. Screen List (V1)
- **Splash Screen:** Brand logo and initial GPS check.
- **Home Screen:** Current conditions, hourly/daily forecast, and detail widgets.
- **Search Screen:** Full-screen overlay with active keyboard focus.
- **Permission Request:** Clear value-prop for GPS access.
- **Error/Empty States:** Integrated into the Home flow (No internet, location denied).

## 4. Interaction Logic
- **Navigation:** Single activity. Search icon in top-right expands to Search Screen.
- **Gestures:** Horizontal scroll for hourly forecast, vertical scroll for full dashboard, pull-to-refresh for data updates.
- **Transitions:** Shared element transitions for weather icons from list to detail.

## 5. Figma Organization
- **Pages:** ❖ Components, 🎨 Tokens, 📱 Prototypes, 📝 Specs.
- **Auto-layout:** Used for all card stacks and lists to ensure responsive scaling for various Android screen sizes.