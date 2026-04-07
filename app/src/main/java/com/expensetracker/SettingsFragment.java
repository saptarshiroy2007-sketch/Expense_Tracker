package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.expensetracker.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsFragment extends Fragment {
    
    private Button manageCategoriesButton;
    private Button clearDataButton;
    private Button exportDataButton;
    
    private AppDatabase db;
    private ExecutorService executorService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        db = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        
        manageCategoriesButton = view.findViewById(R.id.manageCategoriesButton);
        clearDataButton = view.findViewById(R.id.clearDataButton);
        exportDataButton = view.findViewById(R.id.exportDataButton);
        
        manageCategoriesButton.setOnClickListener(v -> openManageCategories());
        clearDataButton.setOnClickListener(v -> confirmClearData());
        exportDataButton.setOnClickListener(v -> exportData());
        
        return view;
    }
    
    private void openManageCategories() {
        Intent intent = new Intent(requireContext(), ManageCategoriesActivity.class);
        startActivity(intent);
    }
    
    private void confirmClearData() {
        new AlertDialog.Builder(requireContext())
            .setTitle("Clear All Data?")
            .setMessage("This will delete all your transaction history. This action cannot be undone.")
            .setPositiveButton("Clear", (dialog, which) -> clearAllData())
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void clearAllData() {
        executorService.execute(() -> {
            db.transactionDao().deleteAll();
            
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "All data cleared", Toast.LENGTH_SHORT).show();
            });
        });
    }
    
    private void exportData() {
        Toast.makeText(requireContext(), "Export feature coming soon!", Toast.LENGTH_SHORT).show();
        // TODO: Implement CSV export
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
