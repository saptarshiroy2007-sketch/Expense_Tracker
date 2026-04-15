# Expense Tracker

An Android expense tracking application built in native Java that automatically detects and logs transactions by reading SMS notifications from banks and payment apps — no manual entry required.

## What does it do?

Every time you make a payment, your bank sends you an SMS. Expense Tracker intercepts those messages, parses the transaction details (amount, merchant, type), and logs them automatically into a local database — categorized and ready to review. The goal is zero-friction expense tracking that runs quietly in the background.

## Features

- **Automatic SMS-based transaction detection** — listens for bank/UPI SMS notifications and parses them in real time
- **Auto-categorization** — classifies transactions into predefined categories (Food, Transport, Shopping, etc.)
- **Local SQLite database** — all data stored on-device, no cloud required
- **Transaction history view** — browse logged expenses with details
- **Default category system** — pre-loaded categories on first launch

## Tech Stack

- **Language:** Java (Native Android)
- **Database:** SQLite (via Android Room / SQLiteOpenHelper)
- **SMS Parsing:** BroadcastReceiver (`SmsReceiver.java`)
- **Platform:** Android

## Getting Started

### Prerequisites

- Android Studio
- Android device or emulator (API level 21+)
- Grant SMS read permissions when prompted

### Setup

1. Clone the repository
   ```
   git clone https://github.com/saptarshiroy2007-sketch/Expense-Tracker.git
   ```
2. Open in Android Studio
3. Build and run on your device or emulator
4. Grant SMS permissions on first launch

## Permissions Required

```xml
<uses-permission android:name="android.permission.RECEIVE_SMS"/>
<uses-permission android:name="android.permission.READ_SMS"/>
```

## Known Limitations

> This project is functional but still under active development. The following issues are known:

- **Background tracking stops when app is closed** — the SMS BroadcastReceiver is not yet registered as a persistent background service. The app must be running (foreground or background) to detect new transactions. A foreground service fix is planned.
- **Consecutive payment detection issues** — rapid back-to-back transactions from the same sender can occasionally be missed or merged due to SMS deduplication logic.
- **Merchant/ID recognition** — the SMS parser does not reliably extract merchant names and transaction IDs across all bank and UPI message formats, as these vary significantly between providers.
- **Classification accuracy** — auto-categorization may misclassify some transactions depending on the SMS format; manual override is recommended for edge cases.

## Roadmap

- [ ] Register `SmsReceiver` as a persistent `ForegroundService` so tracking continues when the app is closed
- [ ] Improve SMS regex patterns to handle more bank/UPI formats
- [ ] Fix consecutive transaction deduplication logic
- [ ] Improve merchant name and transaction ID extraction
- [ ] Add manual transaction entry as a fallback
- [ ] Monthly/weekly spending summary and charts
- [ ] Export to CSV

## Author

**Saptarshi Roy** — [GitHub](https://github.com/saptarshiroy2007-sketch)
