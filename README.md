# Expense Tracker - Auto-Capture from Notifications

A native Android expense tracker that automatically captures payment notifications and asks you to categorize them. No manual entry needed!

## Features

🎯 **Auto-Capture**: Intercepts payment notifications from UPI apps (PhonePe, GPay, Paytm, etc.)
💰 **Smart Parsing**: Extracts amount, UPI ID, and timestamp automatically
📊 **Analytics**: Pie charts and bar graphs showing spending by category
📱 **Offline First**: All data stored locally in SQLite via Room DB
🎨 **Material Design**: Clean, modern UI with bottom navigation

## How It Works

1. **Grant Notification Access** - App needs permission to read payment notifications
2. **Make a Payment** - Use any UPI app to make a payment
3. **Quick Categorize** - A popup appears asking you to select category (Metro, Food, Groceries, etc.)
4. **Track & Analyze** - View all transactions and analytics in the app

## Categories

- Metro
- Food  
- Groceries
- Shopping
- Entertainment
- Bills
- Health
- Other

## Tech Stack

- **Language**: Java
- **Database**: Room (SQLite)
- **Charts**: MPAndroidChart
- **UI**: Material Design Components
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Setup

1. Clone the repo
2. Open in Android Studio
3. Sync Gradle files
4. Run on device/emulator
5. Grant notification listener permission when prompted

## Permissions

- `POST_NOTIFICATIONS` - To show category selection popup
- `BIND_NOTIFICATION_LISTENER_SERVICE` - To intercept payment notifications

## Project Structure

```
app/src/main/java/com/expensetracker/
├── MainActivity.java                    # Main activity with bottom nav
├── NotificationListener.java            # Service that intercepts notifications
├── CategorySelectionActivity.java       # Popup for category selection
├── TransactionsFragment.java            # List of all transactions
├── AnalyticsFragment.java              # Charts and stats
├── SettingsFragment.java               # App settings
└── database/
    ├── AppDatabase.java                # Room database
    ├── Transaction.java                # Transaction entity
    └── TransactionDao.java             # Database operations
```

## Future Enhancements

- [ ] CSV export
- [ ] Budget alerts
- [ ] Merchant recognition (auto-categorize based on UPI ID)
- [ ] Dark mode
- [ ] Custom categories
- [ ] Recurring expense detection
- [ ] Monthly comparison charts
- [ ] Cloud backup

## Note

This app requires notification listener permission which is a sensitive permission. The app only processes payment-related notifications and doesn't store or transmit any data externally. All data stays on your device.

## License

MIT
