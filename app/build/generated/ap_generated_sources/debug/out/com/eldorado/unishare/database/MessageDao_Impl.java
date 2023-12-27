package com.eldorado.unishare.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.eldorado.unishare.model.Message;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class MessageDao_Impl implements MessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Message> __insertionAdapterOfMessage;

  public MessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessage = new EntityInsertionAdapter<Message>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `messages` (`id`,`text`,`senderId`,`receiverId`,`lastMsgId`,`nextMsgId`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Message entity) {
        statement.bindLong(1, entity.id);
        if (entity.text == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.text);
        }
        if (entity.senderId == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.senderId);
        }
        if (entity.receiverId == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.receiverId);
        }
        if (entity.lastMsgId == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.lastMsgId);
        }
        if (entity.nextMsgId == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.nextMsgId);
        }
      }
    };
  }

  @Override
  public void addMessageToDatabase(final Message message) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfMessage.insert(message);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<Message>> getMessages(final String senderId, final String receiverId) {
    final String _sql = "SELECT * FROM messages WHERE (senderId = ? AND receiverId = ?) OR (senderId = ? AND receiverId = ?) ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    if (senderId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, senderId);
    }
    _argIndex = 2;
    if (receiverId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, receiverId);
    }
    _argIndex = 3;
    if (receiverId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, receiverId);
    }
    _argIndex = 4;
    if (senderId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, senderId);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"messages"}, false, new Callable<List<Message>>() {
      @Override
      @Nullable
      public List<Message> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderId");
          final int _cursorIndexOfReceiverId = CursorUtil.getColumnIndexOrThrow(_cursor, "receiverId");
          final int _cursorIndexOfLastMsgId = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMsgId");
          final int _cursorIndexOfNextMsgId = CursorUtil.getColumnIndexOrThrow(_cursor, "nextMsgId");
          final List<Message> _result = new ArrayList<Message>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Message _item;
            _item = new Message();
            _item.id = _cursor.getLong(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfText)) {
              _item.text = null;
            } else {
              _item.text = _cursor.getString(_cursorIndexOfText);
            }
            if (_cursor.isNull(_cursorIndexOfSenderId)) {
              _item.senderId = null;
            } else {
              _item.senderId = _cursor.getString(_cursorIndexOfSenderId);
            }
            if (_cursor.isNull(_cursorIndexOfReceiverId)) {
              _item.receiverId = null;
            } else {
              _item.receiverId = _cursor.getString(_cursorIndexOfReceiverId);
            }
            if (_cursor.isNull(_cursorIndexOfLastMsgId)) {
              _item.lastMsgId = null;
            } else {
              _item.lastMsgId = _cursor.getString(_cursorIndexOfLastMsgId);
            }
            if (_cursor.isNull(_cursorIndexOfNextMsgId)) {
              _item.nextMsgId = null;
            } else {
              _item.nextMsgId = _cursor.getString(_cursorIndexOfNextMsgId);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
