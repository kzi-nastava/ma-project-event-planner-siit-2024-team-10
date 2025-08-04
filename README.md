# 📱 Android Application – Setup Instructions

This project includes an Android mobile application that communicates with a Spring Boot backend.

> ⚠️ **Note:** The `local.properties` file **must NOT** be committed to version control, as it contains local machine-specific configurations.

---

## ✅ Prerequisites

Before running the application, make sure you have:

- ✅ [Android Studio](https://developer.android.com/studio) installed (Hedgehog or newer)
- ✅ Java JDK 17 or later
- ✅ Git
- ✅ Spring Boot backend cloned and runnable
- ✅ An Android device or emulator available

---

## ⚙️ 1. Setup `local.properties`

Inside the **root directory** of the Android project (where the `build.gradle` file is), create a file named `local.properties` if it doesn't already exist.

Paste the following content:

```properties
## This file must *NOT* be checked into Version Control Systems,
# as it contains information specific to your local configuration.
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
ip_addr=192.168.1.4
```

Replace `YourUsername` with your Windows username.

Set `ip_addr` to the IP address of the machine running the backend.  
If you're testing on an emulator, this can often be `10.0.2.2`.

---

## 🧠 2. What is `ip_addr` for?

The application uses the `ip_addr` property to dynamically configure API calls to the backend server.  
Make sure the IP address matches the machine where the Spring Boot backend is running and is reachable from the Android device/emulator.

---

## ☕ 3. Run the Spring Boot Backend

Clone the backend repository and start the Spring application:

```bash
git clone https://github.com/your-org/your-backend.git
cd your-backend
./mvnw spring-boot:run
```

This will start the backend server on:  
`http://192.168.1.4:8080` (or whatever IP and port you've set)

Ensure that:

- The server starts without errors.  
- The device/emulator has access to this address over the network.

---

## 📲 4. Run the Android App

Once everything is set:

1. Open the Android project in **Android Studio**.
2. Let Gradle sync all dependencies.
3. Make sure the `local.properties` file is correctly configured.
4. Connect your Android device or start an emulator.
5. Click the green **Run** button in Android Studio (or press `Shift + F10`).

The app will build and launch on the connected device.

---

## 🔁 Optional: Rebuild / Clean

If something goes wrong during the build, try cleaning the project:

```bash
./gradlew clean
./gradlew build
```

Or use **Build > Clean Project** and **Build > Rebuild Project** from Android Studio.
