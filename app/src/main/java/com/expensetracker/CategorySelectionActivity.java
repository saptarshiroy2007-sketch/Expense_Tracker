package com.expensetracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expensetracker.database.AppDatabase;
import com.expensetracker.database.Transaction;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategorySelectionActivity extends AppCompatActivity {
    
    private TextView amountText;
    private TextView upiText;
    private ChipGroup categoryChipGroup;
    private Button saveButton;
    private Button skipButton;
    
    private double amount;
    private String upiId;
    private long timestamp;
    private String source;
    private String rawText;
    private String selectedCategory;
    
    private ExecutorService executorService;
    private AppDatabase db;
    
    // Categories loaded from database
    private List<com.expensetracker.database.Category> categories;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);
        
        // Initialize database
        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Get payment data from intent
        amount = getIntent().getDoubleExtra("amount", 0);
        upiId = getIntent().getStringExtra("merchant");
        String accountNumber = getIntent().getStringExtra("accountNumber");
        String upiReference = getIntent().getStringExtra("upiReference");
        timestamp = getIntent().getLongExtra("timestamp", System.currentTimeMillis());
        source = getIntent().getStringExtra("source");
        rawText = getIntent().getStringExtra("rawText");
        
        // Store these for saving later
        final String finalAccountNumber = accountNumber;
        final String finalUpiReference = upiReference;
        
        // Initialize views
        amountText = findViewById(R.id.amountText);
        upiText = findViewById(R.id.upiText);
        categoryChipGroup = findViewById(R.id.categoryChipGroup);
        saveButton = findViewById(R.id.saveButton);
        skipButton = findViewById(R.id.skipButton);
        
        // Display payment info
        DecimalFormat df = new DecimalFormat("#,##0.00");
        amountText.setText("₹" + df.format(amount));
        upiText.setText(upiId != null ? upiId : "Unknown");
        
        // Setup category chips
        setupCategoryChips();
        
        // Save button click
        saveButton.setOnClickListener(v -> saveTransaction());
        
        // Skip button - auto-categorize as Miscellaneous
        skipButton.setOnClickListener(v -> {
            selectedCategory = "Miscellaneous";
            saveTransaction();
        });
    }
    
    private void setupCategoryChips() {
        // Load categories from database
        executorService.execute(() -> {
            categories = db.categoryDao().getAllCategories();
            
            runOnUiThread(() -> {
                for (com.expensetracker.database.Category category : categories) {
                    Chip chip = new Chip(this);
                    chip.setText(category.name);
                    chip.setCheckable(true);
                    chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(
                        Color.parseColor(category.color)
                    ));
                    chip.setTextColor(Color.WHITE);
                    
                    chip.setOnClickListener(v -> {
                        selectedCategory = category.name;
                        // Uncheck other chips
                        for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
                            Chip c = (Chip) categoryChipGroup.getChildAt(i);
                            if (c != chip) {
                                c.setChecked(false);
                            }
                        }
                    });
                    categoryChipGroup.addView(chip);
                }
            });
        });
    }
    
    private void saveTransaction() {
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String merchant = getIntent().getStringExtra("merchant");
        String accountNumber = getIntent().getStringExtra("accountNumber");
        String upiReference = getIntent().getStringExtra("upiReference");
        
        Transaction transaction = new Transaction(
            amount,
            merchant,
            accountNumber,
            upiReference,
            timestamp,
            selectedCategory,
            source,
            rawText
        );
        
        executorService.execute(() -> {
            db.transactionDao().insert(transaction);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
