## 🗄️ Database Setup (PostgreSQL)

### Start the Database Services:  
Navigate to the directory containing the docker-compose.yaml file.
Run the following command to start the PostgreSQL and pgAdmin services:
```bash
docker-compose up -d
```
Verify that the PostgreSQL service is running on port 5432 and pgAdmin is accessible at http://localhost:5050.

---

## 🚀 Backend Setup (Spring Boot)

### Build the Application:  
Navigate to the root directory of your project (where the pom.xml file is located).
Run the following Maven command to build the project:
```bash
mvn clean install
```

### Run the Application:  
Start the Spring Boot application using the following command:
```bash
mvn spring-boot:run
```
The application should now be running on http://localhost:8080 (default port).

---

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
ip_addr=
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
git clone https://github.com/kzi-nastava/iss-project-event-planner-siit-2024-team-10.git
cd your-backend
./mvnw spring-boot:run
```
This will start the backend server on:  
`http://localhost:8080` (or whatever IP and port you've set)
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
