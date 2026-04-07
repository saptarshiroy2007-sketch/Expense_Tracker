package com.expensetracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expensetracker.database.AppDatabase;
import com.expensetracker.database.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ManageCategoriesActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private Button addCategoryButton;
    
    private AppDatabase db;
    private ExecutorService executorService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);
        
        getSupportActionBar().setTitle("Manage Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        db = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        recyclerView = findViewById(R.id.categoriesRecyclerView);
        addCategoryButton = findViewById(R.id.addCategoryButton);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        
        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());
        
        loadCategories();
    }
    
    private void loadCategories() {
        executorService.execute(() -> {
            List<Category> categories = db.categoryDao().getAllCategories();
            
            runOnUiThread(() -> {
                adapter.updateCategories(categories);
            });
        });
    }
    
    private void showAddCategoryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        EditText nameInput = dialogView.findViewById(R.id.categoryNameInput);
        
        // Color picker chips
        View[] colorChips = {
            dialogView.findViewById(R.id.color1),
            dialogView.findViewById(R.id.color2),
            dialogView.findViewById(R.id.color3),
            dialogView.findViewById(R.id.color4),
            dialogView.findViewById(R.id.color5),
            dialogView.findViewById(R.id.color6)
        };
        
        final String[] selectedColor = {"#2196F3"};  // Default blue
        
        String[] colors = {"#2196F3", "#4CAF50", "#FF9800", "#E91E63", "#9C27B0", "#00BCD4"};
        for (int i = 0; i < colorChips.length; i++) {
            final int index = i;
            colorChips[i].setOnClickListener(v -> {
                selectedColor[0] = colors[index];
                // Highlight selected
                for (View chip : colorChips) {
                    chip.setAlpha(0.5f);
                }
                v.setAlpha(1.0f);
            });
        }
        
        new AlertDialog.Builder(this)
            .setTitle("Add Category")
            .setView(dialogView)
            .setPositiveButton("Add", (dialog, which) -> {
                String name = nameInput.getText().toString().trim();
                
                if (name.isEmpty()) {
                    Toast.makeText(this, "Category name required", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                executorService.execute(() -> {
                    // Check if category already exists
                    Category existing = db.categoryDao().getCategoryByName(name);
                    if (existing != null) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                    
                    // Get next sort order
                    int nextOrder = db.categoryDao().getAllCategories().size();
                    
                    Category category = new Category(name, selectedColor[0], false, nextOrder);
                    db.categoryDao().insert(category);
                    
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show();
                        loadCategories();
                    });
                });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    // RecyclerView Adapter
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private List<Category> categories;
        
        CategoryAdapter(List<Category> categories) {
            this.categories = categories;
        }
        
        void updateCategories(List<Category> newCategories) {
            this.categories = newCategories;
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Category category = categories.get(position);
            
            holder.nameText.setText(category.name);
            holder.colorIndicator.setBackgroundColor(Color.parseColor(category.color));
            
            if (category.isDefault) {
                holder.typeText.setText("Default");
                holder.deleteButton.setVisibility(View.GONE);
            } else {
                holder.typeText.setText("Custom");
                holder.deleteButton.setVisibility(View.VISIBLE);
                
                holder.deleteButton.setOnClickListener(v -> {
                    showDeleteConfirmation(category, position);
                });
            }
            
            holder.editButton.setOnClickListener(v -> {
                showEditDialog(category);
            });
        }
        
        @Override
        public int getItemCount() {
            return categories.size();
        }
        
        private void showEditDialog(Category category) {
            EditText input = new EditText(ManageCategoriesActivity.this);
            input.setText(category.name);
            
            new AlertDialog.Builder(ManageCategoriesActivity.this)
                .setTitle("Rename Category")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    
                    if (newName.isEmpty()) {
                        Toast.makeText(ManageCategoriesActivity.this, 
                            "Name required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    executorService.execute(() -> {
                        String oldName = category.name;
                        category.name = newName;
                        db.categoryDao().update(category);
                        
                        // Update all transactions with old category name
                        List<com.expensetracker.database.Transaction> transactions = 
                            db.transactionDao().getTransactionsByCategory(oldName);
                        
                        for (com.expensetracker.database.Transaction t : transactions) {
                            t.category = newName;
                            db.transactionDao().update(t);
                        }
                        
                        runOnUiThread(() -> {
                            Toast.makeText(ManageCategoriesActivity.this, 
                                "Category renamed!", Toast.LENGTH_SHORT).show();
                            loadCategories();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
        }
        
        private void showDeleteConfirmation(Category category, int position) {
            executorService.execute(() -> {
                int count = db.categoryDao().getTransactionCount(category.name);
                
                runOnUiThread(() -> {
                    if (count > 0) {
                        new AlertDialog.Builder(ManageCategoriesActivity.this)
                            .setTitle("Cannot Delete")
                            .setMessage("This category has " + count + " transaction(s). " +
                                      "Delete or recategorize them first.")
                            .setPositiveButton("OK", null)
                            .show();
                    } else {
                        new AlertDialog.Builder(ManageCategoriesActivity.this)
                            .setTitle("Delete Category?")
                            .setMessage("Delete \"" + category.name + "\"?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                executorService.execute(() -> {
                                    db.categoryDao().delete(category);
                                    
                                    runOnUiThread(() -> {
                                        Toast.makeText(ManageCategoriesActivity.this, 
                                            "Category deleted", Toast.LENGTH_SHORT).show();
                                        loadCategories();
                                    });
                                });
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    }
                });
            });
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            View colorIndicator;
            TextView nameText;
            TextView typeText;
            Button editButton;
            Button deleteButton;
            
            ViewHolder(View itemView) {
                super(itemView);
                colorIndicator = itemView.findViewById(R.id.colorIndicator);
                nameText = itemView.findViewById(R.id.categoryName);
                typeText = itemView.findViewById(R.id.categoryType);
                editButton = itemView.findViewById(R.id.editCategoryButton);
                deleteButton = itemView.findViewById(R.id.deleteCategoryButton);
            }
        }
    }
}
