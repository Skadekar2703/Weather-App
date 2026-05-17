---
name: SkyCast
colors:
  surface: '#fcf9f8'
  surface-dim: '#dcd9d9'
  surface-bright: '#fcf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f2'
  surface-container: '#f0edec'
  surface-container-high: '#ebe7e7'
  surface-container-highest: '#e5e2e1'
  on-surface: '#1c1b1b'
  on-surface-variant: '#3e4850'
  inverse-surface: '#313030'
  inverse-on-surface: '#f3f0ef'
  outline: '#6e7881'
  outline-variant: '#bdc8d1'
  surface-tint: '#00658d'
  primary: '#00658d'
  on-primary: '#ffffff'
  primary-container: '#00aeef'
  on-primary-container: '#003e58'
  inverse-primary: '#82cfff'
  secondary: '#5d5f5f'
  on-secondary: '#ffffff'
  secondary-container: '#dfe0e0'
  on-secondary-container: '#616363'
  tertiary: '#8d4f00'
  on-tertiary: '#ffffff'
  tertiary-container: '#ea8c21'
  on-tertiary-container: '#572f00'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c6e7ff'
  primary-fixed-dim: '#82cfff'
  on-primary-fixed: '#001e2d'
  on-primary-fixed-variant: '#004c6b'
  secondary-fixed: '#e2e2e2'
  secondary-fixed-dim: '#c6c6c7'
  on-secondary-fixed: '#1a1c1c'
  on-secondary-fixed-variant: '#454747'
  tertiary-fixed: '#ffdcc0'
  tertiary-fixed-dim: '#ffb876'
  on-tertiary-fixed: '#2d1600'
  on-tertiary-fixed-variant: '#6b3b00'
  background: '#fcf9f8'
  on-background: '#1c1b1b'
  surface-variant: '#e5e2e1'
typography:
  display-xl:
    fontFamily: Inter
    fontSize: 84px
    fontWeight: '700'
    lineHeight: 92px
    letterSpacing: -0.04em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  title-md:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '500'
    lineHeight: 24px
  body-md:
    fontFamily: robotoFlex
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  margin-mobile: 24px
  margin-tablet: 48px
  gutter: 16px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 32px
---

## Brand & Style

This design system is built upon a **Minimalist-Glassmorphic** hybrid aesthetic, specifically tailored for the high-performance demands of Android 14. The brand personality is dependable, ethereal, and hyper-focused on data clarity. It seeks to evoke a sense of calm and situational awareness through environmental immersion.

The visual narrative is driven by dynamic conditions. Rather than a static interface, the UI acts as a lens into the sky, utilizing full-bleed gradients and translucent "glass" surfaces to organize information without disconnecting the user from the atmospheric context. 

**Key Principles:**
- **Atmospheric Immersion:** Full-screen background transitions reflect real-time weather.
- **Visual Breathability:** Generous negative space and high-contrast typography ensure immediate legibility at a glance.
- **Glassmorphic Precision:** Surfaces use subtle blurs and thin rim-lights to define depth rather than heavy drop shadows.

## Colors

The palette is anchored by **Deep Sky Blue (#00AEEF)**, representing the brand's core identity. However, this design system utilizes a **dynamic color logic** where the UI adjusts based on the current weather state. 

- **Primary:** Used for active states, key action buttons, and brand touchpoints.
- **Glass Surfaces:** Semi-transparent white or dark overlays with a 20px - 40px backdrop blur to maintain legibility over complex gradients.
- **Typography Contrast:** Text must strictly adhere to WCAG AA standards. On light/sunny backgrounds, use high-contrast black or deep navy; on dark/night backgrounds, use pure white.
- **Semantic Colors:** Warning (Amber #FFC107) for weather alerts and Danger (Coral #FF5252) for severe conditions.

## Typography

This design system leverages a dual-font strategy. **Inter** provides a modern, geometric structure for headlines and UI labels, while **Roboto Flex** (optimized for Android) handles body content and data-heavy weather metrics for maximum readability across various screen densities.

**Visual Hierarchy Rules:**
- **The "Hero" Temperature:** Utilize the `display-xl` weight for the current temperature to create a singular focal point.
- **Metric Labels:** Use `label-sm` in all-caps for secondary data like "HUMIDITY" or "WIND SPEED" to differentiate them from primary readings.
- **Variable Weights:** In Roboto Flex, use slightly heavier weights (500-600) for numeric data to ensure it stands out against dynamic backgrounds.

## Layout & Spacing

The layout follows a **Fluid Grid** model optimized for the verticality of modern Android devices. It adheres to an 8dp baseline grid to ensure alignment across the Material Design ecosystem.

- **Margins:** A generous 24px side margin is maintained on mobile to prevent content from feeling cramped against the hardware edges.
- **The Content Stack:** Information is organized vertically. Current weather occupies the top 40% of the viewport, with detailed metrics and forecasts appearing in card clusters below.
- **Adaptability:** On tablets and foldable devices, the layout reflows from a single column to a multi-pane structure, where the current weather resides on the left and the 7-day forecast expands on the right.

## Elevation & Depth

In alignment with Android 14’s evolution of Material Design, this design system minimizes heavy drop shadows in favor of **Tonal and Glassmorphic depth**.

- **Surface Tiers:**
    - **Tier 0 (Background):** The dynamic animated gradient.
    - **Tier 1 (Cards):** Glassmorphic surfaces with a `Blur: 30px` and a `1px white border (15% opacity)` to simulate the edge of a glass pane.
    - **Tier 2 (Modals/Pop-overs):** Increased opacity and a subtle ambient shadow (Blur: 12px, Spread: 0, Opacity: 10%) to indicate temporary interaction layers.
- **Interaction:** Upon touch, glass surfaces should subtly increase in opacity (5-10%) to provide tactile feedback without changing elevation.

## Shapes

The shape language is defined by large, friendly radii that mirror the rounded corners of modern Android hardware. 

- **Primary Container:** All weather cards and background containers must use a **24dp corner radius**.
- **Small Elements:** Chips, search bars, and input fields use a **12dp radius** to maintain a consistent but distinct hierarchy from the larger card containers.
- **Iconography:** Icons should be linear, 2px stroke width, with slightly rounded terminal ends to match the overall softness of the UI.

## Components

All components are designed to feel integrated into the background rather than sitting "on top" of it.

- **Glass Cards:** The primary container. Must include a backdrop-filter and a light stroke on the top and left edges to simulate light hitting the glass.
- **Weather Chips:** Small, semi-transparent pills used for "Feels Like" or "UV Index" highlights.
- **Linear Icons:** 24x24dp icons with a consistent 2px stroke. Weather icons (Sun, Clouds, Rain) should be custom-weighted to match the Inter typeface.
- **Action Buttons:** Use Material 3 "Filled" or "Tonal" styles. The primary button uses the Deep Sky Blue brand color with white text.
- **Segmented Controls:** Used for switching between "Hourly" and "Daily" views. These should be low-profile, using a simple highlight indicator rather than a heavy container change.
- **Horizontal Scroll Lists:** For 24-hour forecasts, use a borderless list layout where items are separated by generous 16dp spacing, allowing the background gradient to flow through.