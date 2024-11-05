# SafeTrack

**SafeTrack** is a mobile application designed to enhance user safety by combining SOS features, geofencing, location-based alerts, and medication reminders within a single streamlined app.

---

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Technology Stack](#technology-stack)
- [Testing](#testing)
- [Challenges Faced](#challenges-faced)

---

## Features

- **SOS Alerts**: Instant SOS messaging with current or last-known location, sent to emergency contacts.
- **Geofencing**: Notifies user or contacts when moving outside predefined safe zones, even if the app is closed.
- **Location-Based Alerts**: Accesses and displays nearby emergency facilities.
- **Medication Reminders**: Custom notifications for timely medication.
- **Persistent Session Management**: Saves user preferences on Firebase Realtime Database to maintain data on logout.

## Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/SafeTrack.git
   cd SafeTrack
   ```

2. **Open in Android Studio**: Open the project in Android Studio and ensure all dependencies are installed.

3. **Set Up Firebase**: Connect the app to your Firebase project:
   - Download the `google-services.json` file from Firebase and place it in your `app/` directory.
   - Configure Firebase Authentication and Realtime Database with proper rules.

4. **Run the App**: After setup, run the app on an emulator or physical device.

## Usage

1. **Login/Signup**: Authenticate using Google Sign-In.
2. **SOS Setup**: Configure emergency contacts and SOS message preferences.
3. **Geofencing**: Set up geofences to monitor safe zones.
4. **Location-Based Alerts**: Use the "Nearby" feature to find nearby emergency services.
5. **Medication Reminders**: Schedule medication notifications to stay on track.

## Technology Stack

- **Frontend**: Java, XML
- **Backend**: Firebase Realtime Database
- **Location Services**: Google Maps API, FusedLocationProviderClient
- **Notification Management**: AlarmManager for scheduling
- **Dependency Management**: Gradle

## Testing

Testing was conducted using:
- **Manual Testing**: Tested individual components with friends and family.
- **Field Testing**: Geofence functionality verified through real-world testing.
- **Edge Cases**: SMS, SOS, and location access features tested with various permissions and scenarios.

## Challenges Faced

1. **Authentication Integration**: Issues with linking Google Sign-In and Firebase Authentication.
2. **Permissions**: Complex permission management, especially with Android’s background location access.
3. **Geofencing**: Debugged real-time geofencing and optimized location tracking for better battery efficiency.
