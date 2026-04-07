package com.expensetracker.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public double amount;
    public String merchant;        // Merchant/beneficiary name
    public String accountNumber;   // Last 4 digits usually
    public String upiReference;    // UPI transaction reference
    public long timestamp;
    public String category;
    public String source;          // SMS sender (bank identifier)
    public String rawText;         // Original SMS text
    public String notes;
    
    public Transaction(double amount, String merchant, String accountNumber, String upiReference,
                      long timestamp, String category, String source, String rawText) {
        this.amount = amount;
        this.merchant = merchant;
        this.accountNumber = accountNumber;
        this.upiReference = upiReference;
        this.timestamp = timestamp;
        this.category = category;
        this.source = source;
        this.rawText = rawText;
    }
}
