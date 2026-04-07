package com.expensetracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";
    
    // Patterns for different banks
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(?:INR|Rs\\.?|₹)\\s*(\\d+(?:,\\d+)*(?:\\.\\d{2})?)");
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("A/c\\s*[Xx]*\\d{4,}");
    private static final Pattern UPI_REF_PATTERN = Pattern.compile("UPI/[A-Z]+/[A-Za-z0-9]+");
    
    // Keywords that indicate a debit transaction
    private static final String[] DEBIT_KEYWORDS = {
        "debited", "debit", "paid", "spent", "withdrawn", "purchase", "sent"
    };
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = sms.getOriginatingAddress();
                        String messageBody = sms.getMessageBody();
                        
                        Log.d(TAG, "SMS from: " + sender);
                        Log.d(TAG, "Message: " + messageBody);
                        
                        // Check if it's a bank transaction SMS
                        if (isBankTransaction(sender, messageBody)) {
                            PaymentData payment = parsePaymentData(messageBody, sender);
                            
                            if (payment != null && payment.amount > 0) {
                                showCategorySelection(context, payment);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean isBankTransaction(String sender, String message) {
        // Check if sender looks like a bank (usually starts with letters, not +91)
        if (sender == null || sender.startsWith("+")) {
            return false;
        }
        
        String msgLower = message.toLowerCase();
        
        // Must have amount
        if (!AMOUNT_PATTERN.matcher(message).find()) {
            return false;
        }
        
        // Must be a debit transaction
        boolean isDebit = false;
        for (String keyword : DEBIT_KEYWORDS) {
            if (msgLower.contains(keyword)) {
                isDebit = true;
                break;
            }
        }
        
        if (!isDebit) {
            return false;
        }
        
        // Common bank identifiers
        return msgLower.contains("a/c") || 
               msgLower.contains("account") ||
               msgLower.contains("upi") ||
               msgLower.contains("bank");
    }
    
    private PaymentData parsePaymentData(String message, String sender) {
        PaymentData data = new PaymentData();
        data.timestamp = System.currentTimeMillis();
        data.source = sender;
        data.rawText = message;
        
        // Extract amount
        Matcher amountMatcher = AMOUNT_PATTERN.matcher(message);
        if (amountMatcher.find()) {
            String amountStr = amountMatcher.group(1).replace(",", "");
            try {
                data.amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing amount: " + amountStr, e);
            }
        }
        
        // Extract account number (last 4 digits usually shown)
        Matcher accountMatcher = ACCOUNT_PATTERN.matcher(message);
        if (accountMatcher.find()) {
            data.accountNumber = accountMatcher.group(0);
        }
        
        // Extract UPI reference if available
        Matcher upiMatcher = UPI_REF_PATTERN.matcher(message);
        if (upiMatcher.find()) {
            data.upiReference = upiMatcher.group(0);
        }
        
        // Try to extract merchant/beneficiary name
        // Pattern: "to/towards [NAME]" or "at [NAME]"
        Pattern merchantPattern = Pattern.compile("(?:to|towards|at)\\s+([A-Za-z0-9\\s]+?)(?:\\s+on|\\.|$)", Pattern.CASE_INSENSITIVE);
        Matcher merchantMatcher = merchantPattern.matcher(message);
        if (merchantMatcher.find()) {
            data.merchant = merchantMatcher.group(1).trim();
        }
        
        return data;
    }
    
    private void showCategorySelection(Context context, PaymentData payment) {
        Intent intent = new Intent(context, CategorySelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("amount", payment.amount);
        intent.putExtra("merchant", payment.merchant);
        intent.putExtra("accountNumber", payment.accountNumber);
        intent.putExtra("upiReference", payment.upiReference);
        intent.putExtra("timestamp", payment.timestamp);
        intent.putExtra("source", payment.source);
        intent.putExtra("rawText", payment.rawText);
        context.startActivity(intent);
    }
    
    // Data class for parsed payment info
    private static class PaymentData {
        double amount;
        String merchant;
        String accountNumber;
        String upiReference;
        long timestamp;
        String source;
        String rawText;
    }
}
