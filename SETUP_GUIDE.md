# Setup & Deployment Guide

## Prerequisites

1. **Android Studio** (latest version - Arctic Fox or later)
   - Download from: https://developer.android.com/studio
   
2. **Java Development Kit (JDK)** 11 or higher
   - Comes bundled with Android Studio
   
3. **Android Device or Emulator**
   - Physical device running Android 7.0 (API 24) or higher
   - OR Android Emulator with API 24+

## Initial Setup

### 1. Open Project in Android Studio

```bash
# Navigate to the project directory
cd ExpenseTracker

# Open with Android Studio
# File > Open > Select the ExpenseTracker folder
```

### 2. Sync Gradle Files

- Android Studio will automatically detect the project
- Click "Sync Now" when prompted
- Wait for Gradle sync to complete (may take a few minutes on first run)

### 3. Install Dependencies

All dependencies are defined in `app/build.gradle`:
- Material Components
- Room Database
- MPAndroidChart
- RecyclerView
- AndroidX libraries

Gradle will download these automatically during sync.

## Running the App

### Option A: Physical Device

1. **Enable Developer Options on your phone:**
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings > Developer Options
   - Enable "USB Debugging"

2. **Connect your phone via USB**

3. **Run the app:**
   - Click the green "Run" button in Android Studio
   - OR press `Shift + F10`
   - Select your device from the list

### Option B: Android Emulator

1. **Create a Virtual Device:**
   - Tools > Device Manager
   - Click "Create Device"
   - Select a phone (e.g., Pixel 5)
   - Download and select a system image (API 30+ recommended)
   - Finish setup

2. **Run the app:**
   - Click the green "Run" button
   - Select your emulator from the list

## First-Time App Configuration

### 1. Grant Notification Listener Permission

When you first open the app, you'll see a prompt:

1. The app will automatically open the Notification Listener Settings
2. Find "Expense Tracker" in the list
3. Toggle it ON
4. Accept the permission warning

**Manual way:**
- Settings > Apps & Notifications > Special App Access > Notification Access
- Enable for "Expense Tracker"

### 2. Test the App

To test if notification listening works:

1. Open any UPI app (PhonePe, GPay, Paytm, etc.)
2. Make a small test payment
3. A popup should appear asking you to categorize the payment
4. Select a category and tap "Save"
5. Check the Transactions tab to see your entry

## Building APK for Distribution

### Debug APK (for testing)

```bash
# In Android Studio terminal
./gradlew assembleDebug

# APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

### Release APK (for distribution)

1. **Generate Keystore (first time only):**

```bash
keytool -genkey -v -keystore expense-tracker.keystore -alias expense_tracker -keyalg RSA -keysize 2048 -validity 10000
```

2. **Configure signing in `app/build.gradle`:**

```gradle
android {
    signingConfigs {
        release {
            storeFile file('../expense-tracker.keystore')
            storePassword 'your_store_password'
            keyAlias 'expense_tracker'
            keyPassword 'your_key_password'
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

3. **Build Release APK:**

```bash
./gradlew assembleRelease

# APK will be at:
# app/build/outputs/apk/release/app-release.apk
```

## Troubleshooting

### Issue: Gradle Sync Failed

**Solution:**
- Check your internet connection
- Tools > SDK Manager > Ensure Android SDK is installed
- File > Invalidate Caches / Restart

### Issue: Notification Not Detected

**Solution:**
- Verify notification permission is granted
- Check if your payment app is in the `isPaymentApp()` list
- Add your app package name to NotificationListener.java if missing
- Test with a different UPI app

### Issue: App Crashes on Launch

**Solution:**
- Check Logcat for error messages (View > Tool Windows > Logcat)
- Ensure minimum SDK is 24
- Clean and rebuild: Build > Clean Project > Rebuild Project

### Issue: Charts Not Displaying

**Solution:**
- Verify MPAndroidChart dependency is downloaded
- Check if you have transactions in the database
- Look for errors in Logcat

## Performance Optimization

### For Large Transaction Volumes

If you have 1000+ transactions, consider:

1. **Enable ProGuard** (already in release build)
2. **Add Pagination** to TransactionDao:

```java
@Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
List<Transaction> getTransactionsPaginated(int limit, int offset);
```

3. **Index the database** for faster queries:

```java
@Entity(tableName = "transactions", indices = {@Index(value = "timestamp")})
```

## Common Customizations

### Add New Category

Edit `CategorySelectionActivity.java`:

```java
private static final String[] CATEGORIES = {
    "Metro", "Food", "Groceries", "Shopping", 
    "Entertainment", "Bills", "Health", "Other",
    "Your New Category"  // Add here
};
```

### Add New Payment App

Edit `NotificationListener.java`:

```java
private boolean isPaymentApp(String packageName) {
    return packageName.contains("paytm") ||
           packageName.contains("yourapp");  // Add here
}
```

### Change App Colors

Edit `app/src/main/res/values/colors.xml` and `themes.xml`

## Publishing to Google Play Store

1. **Create Google Play Developer Account** ($25 one-time fee)
2. **Prepare Store Listing:**
   - App icon (512x512)
   - Screenshots (min 2)
   - Feature graphic (1024x500)
   - Description
   - Privacy policy

3. **Upload Release APK/AAB:**

```bash
# Build App Bundle (preferred by Play Store)
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab
```

4. **Submit for Review**

## Security Notes

- The app only processes payment notifications locally
- No data is transmitted to external servers
- All data stored in local SQLite database
- Notification listener permission is sensitive - handle with care
- Consider adding encryption for sensitive data in production

## Next Steps

- Add user authentication
- Implement cloud backup (Firebase)
- Add budget tracking features
- Implement merchant recognition
- Add recurring expense detection
- Create widgets for home screen

## Support

For issues or questions:
- Check GitHub Issues
- Review Android documentation
- Check Stack Overflow for common problems
