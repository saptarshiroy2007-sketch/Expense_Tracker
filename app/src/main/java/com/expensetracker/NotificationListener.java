package com.expensetracker;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "NotificationListener";
    
    // Common UPI payment notification patterns
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(?:Rs\\.?|INR|₹)\\s*(\\d+(?:,\\d+)*(?:\\.\\d{2})?)");
    private static final Pattern UPI_PATTERN = Pattern.compile("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+)");
    
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        
        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();
        
        // Check if it's from a payment app
        if (isPaymentApp(packageName)) {
            Bundle extras = notification.extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            String text = extras.getString(Notification.EXTRA_TEXT);
            String bigText = extras.getString(Notification.EXTRA_BIG_TEXT);
            
            String fullText = (title != null ? title + " " : "") + 
                            (text != null ? text + " " : "") + 
                            (bigText != null ? bigText : "");
            
            Log.d(TAG, "Payment notification detected: " + fullText);
            
            // Parse payment details
            PaymentData payment = parsePaymentData(fullText, packageName);
            
            if (payment != null && payment.amount > 0) {
                // Show category selection dialog
                showCategorySelection(payment);
            }
        }
    }
    
    private boolean isPaymentApp(String packageName) {
        // Common UPI and payment apps in India
        return packageName.contains("paytm") ||
               packageName.contains("phonepe") ||
               packageName.contains("gpay") ||
               packageName.contains("google.android.apps.nbu.paisa.user") ||
               packageName.contains("amazonpay") ||
               packageName.contains("bharatpe") ||
               packageName.contains("mobikwik") ||
               packageName.contains("freecharge") ||
               packageName.contains("upi") ||
               packageName.contains("bhim") ||
               packageName.contains("sbi") ||
               packageName.contains("icici") ||
               packageName.contains("hdfc") ||
               packageName.contains("axis") ||
               packageName.contains("kotak");
    }
    
    private PaymentData parsePaymentData(String text, String source) {
        PaymentData data = new PaymentData();
        data.timestamp = System.currentTimeMillis();
        data.source = source;
        
        // Extract amount
        Matcher amountMatcher = AMOUNT_PATTERN.matcher(text);
        if (amountMatcher.find()) {
            String amountStr = amountMatcher.group(1).replace(",", "");
            try {
                data.amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing amount: " + amountStr, e);
            }
        }
        
        // Extract UPI ID
        Matcher upiMatcher = UPI_PATTERN.matcher(text);
        if (upiMatcher.find()) {
            data.upiId = upiMatcher.group(1);
        }
        
        // Store raw text for reference
        data.rawText = text;
        
        return data;
    }
    
    private void showCategorySelection(PaymentData payment) {
        Intent intent = new Intent(this, CategorySelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("amount", payment.amount);
        intent.putExtra("upiId", payment.upiId);
        intent.putExtra("timestamp", payment.timestamp);
        intent.putExtra("source", payment.source);
        intent.putExtra("rawText", payment.rawText);
        startActivity(intent);
    }
    
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
    
    // Data class for parsed payment info
    private static class PaymentData {
        double amount;
        String upiId;
        long timestamp;
        String source;
        String rawText;
    }
}
