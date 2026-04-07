package com.expensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.expensetracker.database.AppDatabase;
import com.expensetracker.database.Transaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionsFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private TextView totalAmountText;
    private TextView emptyStateText;
    
    private AppDatabase db;
    private ExecutorService executorService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);
        
        db = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        
        recyclerView = view.findViewById(R.id.transactionsRecyclerView);
        totalAmountText = view.findViewById(R.id.totalAmountText);
        emptyStateText = view.findViewById(R.id.emptyStateText);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        
        loadTransactions();
        
        return view;
    }
    
    private void loadTransactions() {
        executorService.execute(() -> {
            List<Transaction> transactions = db.transactionDao().getAllTransactions();
            
            requireActivity().runOnUiThread(() -> {
                if (transactions.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyStateText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyStateText.setVisibility(View.GONE);
                    adapter.updateTransactions(transactions);
                    
                    // Calculate total
                    double total = 0;
                    for (Transaction t : transactions) {
                        total += t.amount;
                    }
                    DecimalFormat df = new DecimalFormat("#,##0.00");
                    totalAmountText.setText("Total: ₹" + df.format(total));
                }
            });
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadTransactions(); // Refresh when fragment becomes visible
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    // RecyclerView Adapter
    private class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
        private List<Transaction> transactions;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        private final DecimalFormat amountFormat = new DecimalFormat("#,##0.00");
        
        TransactionAdapter(List<Transaction> transactions) {
            this.transactions = transactions;
        }
        
        void updateTransactions(List<Transaction> newTransactions) {
            this.transactions = newTransactions;
            notifyDataSetChanged();
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Transaction transaction = transactions.get(position);
            
            holder.amountText.setText("₹" + amountFormat.format(transaction.amount));
            holder.categoryText.setText(transaction.category);
            holder.dateText.setText(dateFormat.format(new Date(transaction.timestamp)));
            
            if (transaction.merchant != null && !transaction.merchant.isEmpty()) {
                holder.upiText.setText(transaction.merchant);
                holder.upiText.setVisibility(View.VISIBLE);
            } else if (transaction.accountNumber != null) {
                holder.upiText.setText(transaction.accountNumber);
                holder.upiText.setVisibility(View.VISIBLE);
            } else {
                holder.upiText.setVisibility(View.GONE);
            }
            
            // Click to edit category
            holder.itemView.setOnClickListener(v -> {
                showEditDialog(holder.itemView.getContext(), transaction, position);
            });
        }
        
        @Override
        public int getItemCount() {
            return transactions.size();
        }
        
        private void showEditDialog(android.content.Context context, Transaction transaction, int position) {
            executorService.execute(() -> {
                List<com.expensetracker.database.Category> cats = db.categoryDao().getAllCategories();
                String[] categories = new String[cats.size()];
                for (int i = 0; i < cats.size(); i++) {
                    categories[i] = cats.get(i).name;
                }
                
                requireActivity().runOnUiThread(() -> {
                    new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Edit Category")
                        .setItems(categories, (dialog, which) -> {
                            String newCategory = categories[which];
                            
                            // Update transaction
                            transaction.category = newCategory;
                            
                            // Save to database
                            executorService.execute(() -> {
                                db.transactionDao().update(transaction);
                                
                                requireActivity().runOnUiThread(() -> {
                                    notifyItemChanged(position);
                                    android.widget.Toast.makeText(context, 
                                        "Updated to " + newCategory, 
                                        android.widget.Toast.LENGTH_SHORT).show();
                                });
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                });
            });
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView amountText;
            TextView categoryText;
            TextView dateText;
            TextView upiText;
            
            ViewHolder(View itemView) {
                super(itemView);
                amountText = itemView.findViewById(R.id.transactionAmount);
                categoryText = itemView.findViewById(R.id.transactionCategory);
                dateText = itemView.findViewById(R.id.transactionDate);
                upiText = itemView.findViewById(R.id.transactionUpi);
            }
        }
    }
}
