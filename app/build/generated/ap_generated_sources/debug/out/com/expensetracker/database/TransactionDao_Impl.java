package com.expensetracker.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class TransactionDao_Impl implements TransactionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Transaction> __insertionAdapterOfTransaction;

  private final EntityDeletionOrUpdateAdapter<Transaction> __deletionAdapterOfTransaction;

  private final EntityDeletionOrUpdateAdapter<Transaction> __updateAdapterOfTransaction;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public TransactionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTransaction = new EntityInsertionAdapter<Transaction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `transactions` (`id`,`amount`,`merchant`,`accountNumber`,`upiReference`,`timestamp`,`category`,`source`,`rawText`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final Transaction entity) {
        statement.bindLong(1, entity.id);
        statement.bindDouble(2, entity.amount);
        if (entity.merchant == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.merchant);
        }
        if (entity.accountNumber == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.accountNumber);
        }
        if (entity.upiReference == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.upiReference);
        }
        statement.bindLong(6, entity.timestamp);
        if (entity.category == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.category);
        }
        if (entity.source == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.source);
        }
        if (entity.rawText == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.rawText);
        }
        if (entity.notes == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.notes);
        }
      }
    };
    this.__deletionAdapterOfTransaction = new EntityDeletionOrUpdateAdapter<Transaction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `transactions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final Transaction entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfTransaction = new EntityDeletionOrUpdateAdapter<Transaction>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `transactions` SET `id` = ?,`amount` = ?,`merchant` = ?,`accountNumber` = ?,`upiReference` = ?,`timestamp` = ?,`category` = ?,`source` = ?,`rawText` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final Transaction entity) {
        statement.bindLong(1, entity.id);
        statement.bindDouble(2, entity.amount);
        if (entity.merchant == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.merchant);
        }
        if (entity.accountNumber == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.accountNumber);
        }
        if (entity.upiReference == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.upiReference);
        }
        statement.bindLong(6, entity.timestamp);
        if (entity.category == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.category);
        }
        if (entity.source == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.source);
        }
        if (entity.rawText == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.rawText);
        }
        if (entity.notes == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.notes);
        }
        statement.bindLong(11, entity.id);
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM transactions";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Transaction transaction) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfTransaction.insertAndReturnId(transaction);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Transaction transaction) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfTransaction.handle(transaction);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Transaction transaction) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfTransaction.handle(transaction);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAll() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAll.release(_stmt);
    }
  }

  @Override
  public List<Transaction> getAllTransactions() {
    final String _sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
      final int _cursorIndexOfAccountNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "accountNumber");
      final int _cursorIndexOfUpiReference = CursorUtil.getColumnIndexOrThrow(_cursor, "upiReference");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
      final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final List<Transaction> _result = new ArrayList<Transaction>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Transaction _item;
        final double _tmpAmount;
        _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
        final String _tmpMerchant;
        if (_cursor.isNull(_cursorIndexOfMerchant)) {
          _tmpMerchant = null;
        } else {
          _tmpMerchant = _cursor.getString(_cursorIndexOfMerchant);
        }
        final String _tmpAccountNumber;
        if (_cursor.isNull(_cursorIndexOfAccountNumber)) {
          _tmpAccountNumber = null;
        } else {
          _tmpAccountNumber = _cursor.getString(_cursorIndexOfAccountNumber);
        }
        final String _tmpUpiReference;
        if (_cursor.isNull(_cursorIndexOfUpiReference)) {
          _tmpUpiReference = null;
        } else {
          _tmpUpiReference = _cursor.getString(_cursorIndexOfUpiReference);
        }
        final long _tmpTimestamp;
        _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        final String _tmpCategory;
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _tmpCategory = null;
        } else {
          _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
        }
        final String _tmpSource;
        if (_cursor.isNull(_cursorIndexOfSource)) {
          _tmpSource = null;
        } else {
          _tmpSource = _cursor.getString(_cursorIndexOfSource);
        }
        final String _tmpRawText;
        if (_cursor.isNull(_cursorIndexOfRawText)) {
          _tmpRawText = null;
        } else {
          _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
        }
        _item = new Transaction(_tmpAmount,_tmpMerchant,_tmpAccountNumber,_tmpUpiReference,_tmpTimestamp,_tmpCategory,_tmpSource,_tmpRawText);
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _item.notes = null;
        } else {
          _item.notes = _cursor.getString(_cursorIndexOfNotes);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Transaction> getTransactionsByCategory(final String category) {
    final String _sql = "SELECT * FROM transactions WHERE category = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (category == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, category);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
      final int _cursorIndexOfAccountNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "accountNumber");
      final int _cursorIndexOfUpiReference = CursorUtil.getColumnIndexOrThrow(_cursor, "upiReference");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
      final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final List<Transaction> _result = new ArrayList<Transaction>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Transaction _item;
        final double _tmpAmount;
        _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
        final String _tmpMerchant;
        if (_cursor.isNull(_cursorIndexOfMerchant)) {
          _tmpMerchant = null;
        } else {
          _tmpMerchant = _cursor.getString(_cursorIndexOfMerchant);
        }
        final String _tmpAccountNumber;
        if (_cursor.isNull(_cursorIndexOfAccountNumber)) {
          _tmpAccountNumber = null;
        } else {
          _tmpAccountNumber = _cursor.getString(_cursorIndexOfAccountNumber);
        }
        final String _tmpUpiReference;
        if (_cursor.isNull(_cursorIndexOfUpiReference)) {
          _tmpUpiReference = null;
        } else {
          _tmpUpiReference = _cursor.getString(_cursorIndexOfUpiReference);
        }
        final long _tmpTimestamp;
        _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        final String _tmpCategory;
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _tmpCategory = null;
        } else {
          _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
        }
        final String _tmpSource;
        if (_cursor.isNull(_cursorIndexOfSource)) {
          _tmpSource = null;
        } else {
          _tmpSource = _cursor.getString(_cursorIndexOfSource);
        }
        final String _tmpRawText;
        if (_cursor.isNull(_cursorIndexOfRawText)) {
          _tmpRawText = null;
        } else {
          _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
        }
        _item = new Transaction(_tmpAmount,_tmpMerchant,_tmpAccountNumber,_tmpUpiReference,_tmpTimestamp,_tmpCategory,_tmpSource,_tmpRawText);
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _item.notes = null;
        } else {
          _item.notes = _cursor.getString(_cursorIndexOfNotes);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Transaction> getTransactionsByDateRange(final long startTime, final long endTime) {
    final String _sql = "SELECT * FROM transactions WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
      final int _cursorIndexOfMerchant = CursorUtil.getColumnIndexOrThrow(_cursor, "merchant");
      final int _cursorIndexOfAccountNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "accountNumber");
      final int _cursorIndexOfUpiReference = CursorUtil.getColumnIndexOrThrow(_cursor, "upiReference");
      final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
      final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
      final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
      final int _cursorIndexOfRawText = CursorUtil.getColumnIndexOrThrow(_cursor, "rawText");
      final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
      final List<Transaction> _result = new ArrayList<Transaction>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Transaction _item;
        final double _tmpAmount;
        _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
        final String _tmpMerchant;
        if (_cursor.isNull(_cursorIndexOfMerchant)) {
          _tmpMerchant = null;
        } else {
          _tmpMerchant = _cursor.getString(_cursorIndexOfMerchant);
        }
        final String _tmpAccountNumber;
        if (_cursor.isNull(_cursorIndexOfAccountNumber)) {
          _tmpAccountNumber = null;
        } else {
          _tmpAccountNumber = _cursor.getString(_cursorIndexOfAccountNumber);
        }
        final String _tmpUpiReference;
        if (_cursor.isNull(_cursorIndexOfUpiReference)) {
          _tmpUpiReference = null;
        } else {
          _tmpUpiReference = _cursor.getString(_cursorIndexOfUpiReference);
        }
        final long _tmpTimestamp;
        _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
        final String _tmpCategory;
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _tmpCategory = null;
        } else {
          _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
        }
        final String _tmpSource;
        if (_cursor.isNull(_cursorIndexOfSource)) {
          _tmpSource = null;
        } else {
          _tmpSource = _cursor.getString(_cursorIndexOfSource);
        }
        final String _tmpRawText;
        if (_cursor.isNull(_cursorIndexOfRawText)) {
          _tmpRawText = null;
        } else {
          _tmpRawText = _cursor.getString(_cursorIndexOfRawText);
        }
        _item = new Transaction(_tmpAmount,_tmpMerchant,_tmpAccountNumber,_tmpUpiReference,_tmpTimestamp,_tmpCategory,_tmpSource,_tmpRawText);
        _item.id = _cursor.getInt(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfNotes)) {
          _item.notes = null;
        } else {
          _item.notes = _cursor.getString(_cursorIndexOfNotes);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<TransactionDao.CategoryTotal> getCategoryTotals() {
    final String _sql = "SELECT category, SUM(amount) as total FROM transactions GROUP BY category";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfCategory = 0;
      final int _cursorIndexOfTotal = 1;
      final List<TransactionDao.CategoryTotal> _result = new ArrayList<TransactionDao.CategoryTotal>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final TransactionDao.CategoryTotal _item;
        _item = new TransactionDao.CategoryTotal();
        if (_cursor.isNull(_cursorIndexOfCategory)) {
          _item.category = null;
        } else {
          _item.category = _cursor.getString(_cursorIndexOfCategory);
        }
        _item.total = _cursor.getDouble(_cursorIndexOfTotal);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public double getTotalSpending(final long startTime, final long endTime) {
    final String _sql = "SELECT SUM(amount) FROM transactions WHERE timestamp >= ? AND timestamp <= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final double _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getDouble(0);
      } else {
        _result = 0.0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
