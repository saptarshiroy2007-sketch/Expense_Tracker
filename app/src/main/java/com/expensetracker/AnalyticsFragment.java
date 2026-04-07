package com.expensetracker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.expensetracker.database.AppDatabase;
import com.expensetracker.database.Transaction;
import com.expensetracker.database.TransactionDao;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalyticsFragment extends Fragment {
    
    private PieChart pieChart;
    private BarChart barChart;
    private TextView monthlyTotalText;
    private TextView avgTransactionText;
    private TextView totalTransactionsText;
    
    private AppDatabase db;
    private ExecutorService executorService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);
        
        db = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        monthlyTotalText = view.findViewById(R.id.monthlyTotalText);
        avgTransactionText = view.findViewById(R.id.avgTransactionText);
        totalTransactionsText = view.findViewById(R.id.totalTransactionsText);
        
        setupCharts();
        loadAnalytics();
        
        return view;
    }
    
    private void setupCharts() {
        // Pie chart setup
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        
        Legend pieLegend = pieChart.getLegend();
        pieLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        pieLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        pieLegend.setDrawInside(false);
        
        // Bar chart setup
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setEnabled(false);
        
        Legend barLegend = barChart.getLegend();
        barLegend.setEnabled(false);
    }
    
    private void loadAnalytics() {
        executorService.execute(() -> {
            List<Transaction> allTransactions = db.transactionDao().getAllTransactions();
            List<TransactionDao.CategoryTotal> categoryTotals = db.transactionDao().getCategoryTotals();
            
            // Calculate monthly stats
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            long monthStart = cal.getTimeInMillis();
            
            cal.add(Calendar.MONTH, 1);
            long monthEnd = cal.getTimeInMillis();
            
            double monthlyTotal = db.transactionDao().getTotalSpending(monthStart, monthEnd);
            
            requireActivity().runOnUiThread(() -> {
                DecimalFormat df = new DecimalFormat("#,##0.00");
                
                // Update stats
                monthlyTotalText.setText("₹" + df.format(monthlyTotal));
                totalTransactionsText.setText(String.valueOf(allTransactions.size()));
                
                if (!allTransactions.isEmpty()) {
                    double total = 0;
                    for (Transaction t : allTransactions) {
                        total += t.amount;
                    }
                    double avg = total / allTransactions.size();
                    avgTransactionText.setText("₹" + df.format(avg));
                } else {
                    avgTransactionText.setText("₹0.00");
                }
                
                // Update pie chart
                updatePieChart(categoryTotals);
                
                // Update bar chart
                updateBarChart(categoryTotals);
            });
        });
    }
    
    private void updatePieChart(List<TransactionDao.CategoryTotal> categoryTotals) {
        if (categoryTotals.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            return;
        }
        
        pieChart.setVisibility(View.VISIBLE);
        List<PieEntry> entries = new ArrayList<>();
        
        for (TransactionDao.CategoryTotal ct : categoryTotals) {
            entries.add(new PieEntry((float) ct.total, ct.category));
        }
        
        PieDataSet dataSet = new PieDataSet(entries, "Spending by Category");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "₹" + new DecimalFormat("#,##0").format(value);
            }
        });
        
        pieChart.setData(data);
        pieChart.invalidate();
    }
    
    private void updateBarChart(List<TransactionDao.CategoryTotal> categoryTotals) {
        if (categoryTotals.isEmpty()) {
            barChart.setVisibility(View.GONE);
            return;
        }
        
        barChart.setVisibility(View.VISIBLE);
        List<BarEntry> entries = new ArrayList<>();
        
        for (int i = 0; i < categoryTotals.size(); i++) {
            entries.add(new BarEntry(i, (float) categoryTotals.get(i).total));
        }
        
        BarDataSet dataSet = new BarDataSet(entries, "Category Spending");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        
        BarData data = new BarData(dataSet);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "₹" + new DecimalFormat("#,##0").format(value);
            }
        });
        
        barChart.setData(data);
        barChart.invalidate();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        loadAnalytics();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
