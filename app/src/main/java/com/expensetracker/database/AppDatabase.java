package com.expensetracker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Transaction.class, Category.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "expense_tracker_db"
            )
            .fallbackToDestructiveMigration()
            .addCallback(new RoomDatabase.Callback() {
                @Override
                public void onCreate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    // Populate default categories on first run
                    new Thread(() -> {
                        populateDefaultCategories(context);
                    }).start();
                }
            })
            .build();
        }
        return instance;
    }
    
    private static void populateDefaultCategories(Context context) {
        CategoryDao categoryDao = getInstance(context).categoryDao();
        
        // Default categories with colors
        String[][] defaults = {
            {"Metro", "#2196F3"},
            {"Food", "#FF9800"},
            {"Groceries", "#4CAF50"},
            {"Shopping", "#E91E63"},
            {"Entertainment", "#9C27B0"},
            {"Bills", "#F44336"},
            {"Health", "#00BCD4"},
            {"Miscellaneous", "#607D8B"},
            {"Other", "#9E9E9E"}
        };
        
        for (int i = 0; i < defaults.length; i++) {
            Category category = new Category(defaults[i][0], defaults[i][1], true, i);
            categoryDao.insert(category);
        }
    }
}
