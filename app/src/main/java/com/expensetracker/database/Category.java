package com.expensetracker.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String color;  // Hex color code
    public boolean isDefault;  // Can't delete default categories
    public int sortOrder;  // For custom ordering
    
    public Category(String name, String color, boolean isDefault, int sortOrder) {
        this.name = name;
        this.color = color;
        this.isDefault = isDefault;
        this.sortOrder = sortOrder;
    }
}
