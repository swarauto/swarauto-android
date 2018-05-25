[![Build Status](https://travis-ci.org/swarauto/swarauto-android.svg?branch=master)](https://travis-ci.org/swarauto/swarauto-android)

**SWarAuto** is a smart tool that supports auto farming for game Summoners War: Sky Arena.
  - Auto farm Cairos Dungeon, Faimon, TOA, Rift beast.
  - Auto refill energy. Retry on network problems.
  - Custom auto sell rune drop by rarity.
  - Smart recognize game screen to give directives, prevent detection.

**swarauto-android** is build to run solely on Android device.

---

# Requirements
  - Minimum Android version: 4.0.3 (Ice Cream Sandwich)
  - Your Android must be **ROOTED**

# Download
[Download APK here](https://github.com/swarauto/swarauto-android/releases)

# How to use

## 1. Make device config with swarauto-desktop
  - Follow [swarauto-desktop guide](https://github.com/swarauto/swarauto-desktop) to create config for your Android device

## 2. Install APK
  - Download APK and install to Android

## 3. Copy device config (profile) from Desktop to Android
  - On your Android phone, create a folder `swarauto` on your sdcard. It should be `/sdcard/swarauto`
  - On your PC, navigate to the SWarAuto folder. Copy folder `profiles` to your Android phone. It should be `/sdcard/swarauto/profiles/`

## 4. Run SWarAuto app for the 1st time
  - Now run SWarAuto app on Android.
  - Allow these permission when asked:
    - Root access
    - Access storage (your sdcard)
    - Show app window on top (Apps that can appear on top)
  - Make sure `System info` has no error.

## 5. Enjoy autoing

# Dependencies
  - SWarAuto is inspired by [sw-bot](https://github.com/justindannguyen/sw-bot)
  - Use OpenCV for game screen recognition
  - Use base lib [swarauto-base](https://github.com/swarauto/swarauto-base)

# Disclaimer
**This is personal fun project, please use it as your own risks. We are not responsible for any banned account**
