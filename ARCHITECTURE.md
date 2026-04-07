# Architecture Documentation

## Overview

The Expense Tracker is built using native Android architecture patterns with a focus on:
- **Clean separation of concerns**
- **Offline-first approach**
- **Reactive UI updates**
- **Efficient database operations**

## Architecture Pattern: MVVM-lite

While not using full MVVM with ViewModels, the app follows these principles:
- **Model**: Room database entities and DAOs
- **View**: Activities and Fragments
- **Logic**: Direct database operations on background threads

## Core Components

### 1. Notification Listening Layer

```
NotificationListenerService
    ↓
Payment Detection
    ↓
Data Parsing (Regex)
    ↓
Category Selection Intent
```

**File**: `NotificationListener.java`

**Responsibilities:**
- Intercept system notifications
- Filter for payment apps
- Extract amount, UPI ID, timestamp
- Launch category selection UI

**Key Design Decisions:**
- Uses regex patterns for flexible parsing
- Whitelist approach for payment app detection
- Passes data via Intent extras (not persistent storage)

### 2. Database Layer

```
AppDatabase (Room)
    ↓
TransactionDao
    ↓
Transaction Entity
```

**Files**: 
- `database/AppDatabase.java`
- `database/Transaction.java`
- `database/TransactionDao.java`

**Schema:**

```sql
CREATE TABLE transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amount REAL,
    upiId TEXT,
    timestamp INTEGER,
    category TEXT,
    source TEXT,
    rawText TEXT,
    notes TEXT
);
```

**Design Decisions:**
- Room provides compile-time SQL verification
- Singleton pattern for database instance
- All queries run on background threads
- No migrations needed (destructive fallback for v1)

### 3. UI Layer

```
MainActivity (BottomNavigationView)
    ↓
    ├── TransactionsFragment (List view)
    ├── AnalyticsFragment (Charts)
    └── SettingsFragment (Config)

CategorySelectionActivity (Dialog)
```

**Navigation Flow:**

```
App Launch
    ↓
Check Notification Permission
    ↓ (if not granted)
Open Settings
    ↓
Show Transactions (default)

[Background: Notification arrives]
    ↓
Parse Payment Data
    ↓
Show Category Dialog
    ↓
Save to Database
    ↓
Update UI (when fragment visible)
```

## Data Flow

### Capture Flow

```
1. UPI Payment Made
   ↓
2. Payment App Posts Notification
   ↓
3. NotificationListenerService.onNotificationPosted()
   ↓
4. Check if payment app → Parse text
   ↓
5. Create PaymentData object
   ↓
6. Start CategorySelectionActivity
   ↓
7. User selects category
   ↓
8. Create Transaction entity
   ↓
9. Insert to database (background thread)
   ↓
10. Show success toast
```

### Display Flow

```
1. Open TransactionsFragment
   ↓
2. Query database on background thread
   ↓
3. Update RecyclerView adapter on main thread
   ↓
4. Calculate and display total
```

### Analytics Flow

```
1. Open AnalyticsFragment
   ↓
2. Query category totals from database
   ↓
3. Query monthly spending
   ↓
4. Update charts on main thread
   ↓
5. Format and display stats
```

## Threading Model

### Main Thread (UI Thread)
- UI updates
- User interactions
- Fragment lifecycle

### Background Threads (ExecutorService)
- Database queries
- Database inserts/updates
- Aggregation calculations

**Pattern:**

```java
executorService.execute(() -> {
    // Background work
    List<Transaction> data = db.transactionDao().getAllTransactions();
    
    requireActivity().runOnUiThread(() -> {
        // Update UI
        adapter.updateTransactions(data);
    });
});
```

## Key Design Patterns

### 1. Singleton Pattern
**Usage**: Database instance

```java
public static synchronized AppDatabase getInstance(Context context) {
    if (instance == null) {
        instance = Room.databaseBuilder(...).build();
    }
    return instance;
}
```

### 2. Observer Pattern
**Usage**: RecyclerView adapter updates

```java
adapter.updateTransactions(newData);
adapter.notifyDataSetChanged();
```

### 3. Factory Pattern
**Usage**: Fragment creation in MainActivity

```java
Fragment selectedFragment = null;
if (itemId == R.id.nav_transactions) {
    selectedFragment = new TransactionsFragment();
}
```

### 4. Repository Pattern (Implicit)
**Usage**: DAO acts as repository

```java
// DAO provides abstraction over data source
transactionDao.getAllTransactions()
transactionDao.getCategoryTotals()
```

## Security Considerations

### 1. Permission Model
- Notification Listener is a dangerous permission
- Requires explicit user grant
- Can be revoked anytime

### 2. Data Privacy
- All data stored locally (no cloud)
- No network requests
- No analytics tracking
- No third-party SDKs (except charts)

### 3. Input Validation
- Amount parsing with try-catch
- Null checks on UPI ID
- Category selection required

## Performance Optimizations

### 1. Database
- Indexed timestamp for date queries (can be added)
- Batch operations not needed yet (< 1000 transactions expected)
- Query optimization via Room's compile-time checks

### 2. UI
- RecyclerView for efficient list rendering
- ViewHolder pattern (automatic with RecyclerView)
- Only load visible items

### 3. Memory
- Database instance is singleton
- ExecutorService reused per fragment
- Charts use primitive types (float) not objects

## Scalability Considerations

### Current Limitations
- No pagination (loads all transactions)
- No caching layer
- No background sync
- Single-user only

### Future Improvements
- Add pagination: `LIMIT/OFFSET` queries
- Implement LiveData for reactive updates
- Add WorkManager for scheduled tasks
- Multi-user support with user_id field

## Testing Strategy

### Unit Tests (Recommended)
```java
// Database operations
@Test
public void insertAndRetrieveTransaction() {
    Transaction t = new Transaction(...);
    dao.insert(t);
    assertEquals(1, dao.getAllTransactions().size());
}
```

### Integration Tests
```java
// Notification parsing
@Test
public void parsePaymentNotification() {
    String text = "Paid Rs.100 to user@upi";
    PaymentData data = parsePaymentData(text, "phonepe");
    assertEquals(100.0, data.amount);
}
```

### UI Tests (Espresso)
```java
@Test
public void categorySelectionFlow() {
    onView(withId(R.id.categoryChipGroup))
        .perform(click());
    onView(withId(R.id.saveButton))
        .perform(click());
}
```

## Code Organization

```
app/src/main/
├── java/com/expensetracker/
│   ├── MainActivity.java                 # Entry point + navigation
│   ├── NotificationListener.java         # Core notification logic
│   ├── CategorySelectionActivity.java    # User interaction
│   ├── TransactionsFragment.java         # List display
│   ├── AnalyticsFragment.java           # Charts display
│   ├── SettingsFragment.java            # Configuration
│   └── database/
│       ├── AppDatabase.java             # Room database
│       ├── Transaction.java             # Entity
│       └── TransactionDao.java          # Queries
├── res/
│   ├── layout/                          # XML layouts
│   ├── menu/                            # Navigation menu
│   ├── values/                          # Strings, colors, themes
│   └── drawable/                        # Icons (system defaults)
└── AndroidManifest.xml                  # App config + permissions
```

## Dependencies Graph

```
App Level:
- Material Components → UI components
- Room → Database
- MPAndroidChart → Visualizations
- RecyclerView → Lists
- AndroidX Core → Compatibility

System Level:
- NotificationListenerService → Android Framework
- ExecutorService → java.util.concurrent
```

## Future Architecture Improvements

1. **Add ViewModel Layer**
   - Survive configuration changes
   - Lifecycle-aware data handling

2. **Implement LiveData**
   - Reactive database updates
   - Automatic UI refresh

3. **Use Kotlin**
   - Null safety
   - Coroutines for threading
   - Extension functions

4. **Add Dependency Injection**
   - Hilt/Dagger for better testability
   - Easier mocking

5. **Repository Pattern**
   - Abstract data source
   - Easier to add cloud sync later
