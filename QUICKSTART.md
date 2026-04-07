# Quick Start Guide 🚀

## What You Got

A fully functional native Android expense tracker that:
- Auto-captures payment notifications from UPI apps
- Asks you to categorize each expense 
- Stores everything locally (no cloud, no tracking)
- Shows analytics with charts
- Works completely offline

## File Structure

```
ExpenseTracker/
├── README.md                    # Project overview
├── SETUP_GUIDE.md              # Detailed setup instructions
├── ARCHITECTURE.md             # Technical deep dive
├── app/
│   ├── build.gradle            # Dependencies
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/expensetracker/
│       │   ├── MainActivity.java
│       │   ├── NotificationListener.java    # 🔥 Core magic
│       │   ├── CategorySelectionActivity.java
│       │   ├── TransactionsFragment.java
│       │   ├── AnalyticsFragment.java
│       │   ├── SettingsFragment.java
│       │   └── database/
│       └── res/                # Layouts, colors, themes
├── build.gradle                # Project config
└── settings.gradle
```

## Get It Running (5 mins)

### 1. Install Android Studio
Download: https://developer.android.com/studio

### 2. Open Project
- File > Open
- Select the `ExpenseTracker` folder
- Wait for Gradle sync

### 3. Run It
- Connect phone via USB (enable USB debugging)
- OR create an emulator (Tools > Device Manager)
- Click the green Run button ▶️

### 4. Grant Permission
- App will ask for Notification Listener access
- Toggle ON for "Expense Tracker"
- Go back to the app

### 5. Test It
- Make a payment with any UPI app
- Popup appears → Select category → Save
- Check Transactions tab 📊

## Key Files to Customize

### Add Payment Apps
`NotificationListener.java` → `isPaymentApp()` method
```java
return packageName.contains("yourapp");
```

### Add Categories
`CategorySelectionActivity.java` → `CATEGORIES` array
```java
private static final String[] CATEGORIES = {
    "Metro", "Food", "Groceries", "Your Category"
};
```

### Change Colors
`app/src/main/res/values/colors.xml`
```xml
<color name="green_primary">#4CAF50</color>
```

## How It Works

```
UPI Payment
    ↓
Android Notification
    ↓
NotificationListener catches it
    ↓
Parses amount + UPI ID with regex
    ↓
Shows category popup
    ↓
You tap a category
    ↓
Saves to SQLite
    ↓
Updates UI + Charts
```

## Tech Stack

- **Java** - Native Android
- **Room** - SQLite database
- **MPAndroidChart** - Charts
- **Material Components** - UI

## Common Issues

**"Notification not detected"**
→ Check Settings > Apps > Expense Tracker > Notification Access is ON

**"App crashes on launch"**
→ Check Logcat (View > Tool Windows > Logcat)
→ Clean & Rebuild (Build > Clean Project)

**"Charts not showing"**
→ Make sure you have transactions first
→ Check Analytics tab after adding expenses

## Build for Release

```bash
# In Android Studio terminal
./gradlew assembleRelease

# Output:
# app/build/outputs/apk/release/app-release.apk
```

## What's Next?

Possible upgrades:
- Cloud backup (Firebase)
- Recurring expense detection
- Budget alerts
- Merchant auto-categorization
- Dark mode
- Export to CSV
- Widgets

## Support

- Read `SETUP_GUIDE.md` for detailed instructions
- Read `ARCHITECTURE.md` to understand the code
- Check GitHub Issues for common problems
- Stack Overflow for Android questions

## License

MIT - do whatever you want with it

---

Built with ☕ and native Android
No BS, just pure functionality 🔥
