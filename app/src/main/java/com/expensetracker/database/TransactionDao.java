package com.expensetracker.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    long insert(Transaction transaction);
    
    @Update
    void update(Transaction transaction);
    
    @Delete
    void delete(Transaction transaction);
    
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    List<Transaction> getAllTransactions();
    
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY timestamp DESC")
    List<Transaction> getTransactionsByCategory(String category);
    
    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    List<Transaction> getTransactionsByDateRange(long startTime, long endTime);
    
    @Query("SELECT category, SUM(amount) as total FROM transactions GROUP BY category")
    List<CategoryTotal> getCategoryTotals();
    
    @Query("SELECT SUM(amount) FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime")
    double getTotalSpending(long startTime, long endTime);
    
    @Query("DELETE FROM transactions")
    void deleteAll();
    
    // Helper class for category totals
    class CategoryTotal {
        public String category;
        public double total;
    }
}
