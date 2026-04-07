package com.expensetracker.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);
    
    @Update
    void update(Category category);
    
    @Delete
    void delete(Category category);
    
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    List<Category> getAllCategories();
    
    @Query("SELECT * FROM categories WHERE isDefault = 0 ORDER BY sortOrder ASC")
    List<Category> getCustomCategories();
    
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    Category getCategoryByName(String name);
    
    @Query("SELECT COUNT(*) FROM transactions WHERE category = :categoryName")
    int getTransactionCount(String categoryName);
    
    @Query("DELETE FROM categories WHERE isDefault = 0")
    void deleteAllCustomCategories();
}
