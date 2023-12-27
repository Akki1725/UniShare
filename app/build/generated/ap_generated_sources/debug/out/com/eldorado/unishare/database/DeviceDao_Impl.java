package com.eldorado.unishare.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.eldorado.unishare.model.Device;
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
public final class DeviceDao_Impl implements DeviceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Device> __insertionAdapterOfDevice;

  private final EntityDeletionOrUpdateAdapter<Device> __updateAdapterOfDevice;

  public DeviceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDevice = new EntityInsertionAdapter<Device>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `devices` (`mac`,`blueId`,`name`,`deviceName`,`profileImage`,`firstName`,`lastName`,`lastMsg`,`address`,`city`,`zip`,`status`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Device entity) {
        if (entity.getMac() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getMac());
        }
        if (entity.getBlueId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getBlueId());
        }
        if (entity.getName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getName());
        }
        if (entity.getDeviceName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDeviceName());
        }
        if (entity.getProfileImage() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getProfileImage());
        }
        if (entity.getFirstName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFirstName());
        }
        if (entity.getLastName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getLastName());
        }
        if (entity.getLastMsg() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLastMsg());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAddress());
        }
        if (entity.getCity() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCity());
        }
        if (entity.getZip() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getZip());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getStatus());
        }
      }
    };
    this.__updateAdapterOfDevice = new EntityDeletionOrUpdateAdapter<Device>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `devices` SET `mac` = ?,`blueId` = ?,`name` = ?,`deviceName` = ?,`profileImage` = ?,`firstName` = ?,`lastName` = ?,`lastMsg` = ?,`address` = ?,`city` = ?,`zip` = ?,`status` = ? WHERE `mac` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Device entity) {
        if (entity.getMac() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getMac());
        }
        if (entity.getBlueId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getBlueId());
        }
        if (entity.getName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getName());
        }
        if (entity.getDeviceName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDeviceName());
        }
        if (entity.getProfileImage() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getProfileImage());
        }
        if (entity.getFirstName() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFirstName());
        }
        if (entity.getLastName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getLastName());
        }
        if (entity.getLastMsg() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLastMsg());
        }
        if (entity.getAddress() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getAddress());
        }
        if (entity.getCity() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getCity());
        }
        if (entity.getZip() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getZip());
        }
        if (entity.getStatus() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getStatus());
        }
        if (entity.getMac() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getMac());
        }
      }
    };
  }

  @Override
  public void addDevice(final Device device) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDevice.insert(device);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateDatabase(final Device device) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfDevice.handle(device);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<Device>> getAllDevices() {
    final String _sql = "SELECT * FROM devices";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"devices"}, false, new Callable<List<Device>>() {
      @Override
      @Nullable
      public List<Device> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMac = CursorUtil.getColumnIndexOrThrow(_cursor, "mac");
          final int _cursorIndexOfBlueId = CursorUtil.getColumnIndexOrThrow(_cursor, "blueId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceName");
          final int _cursorIndexOfProfileImage = CursorUtil.getColumnIndexOrThrow(_cursor, "profileImage");
          final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
          final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
          final int _cursorIndexOfLastMsg = CursorUtil.getColumnIndexOrThrow(_cursor, "lastMsg");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final int _cursorIndexOfZip = CursorUtil.getColumnIndexOrThrow(_cursor, "zip");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<Device> _result = new ArrayList<Device>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Device _item;
            final String _tmpMac;
            if (_cursor.isNull(_cursorIndexOfMac)) {
              _tmpMac = null;
            } else {
              _tmpMac = _cursor.getString(_cursorIndexOfMac);
            }
            _item = new Device(_tmpMac);
            final String _tmpBlueId;
            if (_cursor.isNull(_cursorIndexOfBlueId)) {
              _tmpBlueId = null;
            } else {
              _tmpBlueId = _cursor.getString(_cursorIndexOfBlueId);
            }
            _item.setBlueId(_tmpBlueId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            _item.setName(_tmpName);
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            _item.setDeviceName(_tmpDeviceName);
            final String _tmpProfileImage;
            if (_cursor.isNull(_cursorIndexOfProfileImage)) {
              _tmpProfileImage = null;
            } else {
              _tmpProfileImage = _cursor.getString(_cursorIndexOfProfileImage);
            }
            _item.setProfileImage(_tmpProfileImage);
            final String _tmpFirstName;
            if (_cursor.isNull(_cursorIndexOfFirstName)) {
              _tmpFirstName = null;
            } else {
              _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
            }
            _item.setFirstName(_tmpFirstName);
            final String _tmpLastName;
            if (_cursor.isNull(_cursorIndexOfLastName)) {
              _tmpLastName = null;
            } else {
              _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
            }
            _item.setLastName(_tmpLastName);
            final String _tmpLastMsg;
            if (_cursor.isNull(_cursorIndexOfLastMsg)) {
              _tmpLastMsg = null;
            } else {
              _tmpLastMsg = _cursor.getString(_cursorIndexOfLastMsg);
            }
            _item.setLastMsg(_tmpLastMsg);
            final String _tmpAddress;
            if (_cursor.isNull(_cursorIndexOfAddress)) {
              _tmpAddress = null;
            } else {
              _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            }
            _item.setAddress(_tmpAddress);
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            _item.setCity(_tmpCity);
            final String _tmpZip;
            if (_cursor.isNull(_cursorIndexOfZip)) {
              _tmpZip = null;
            } else {
              _tmpZip = _cursor.getString(_cursorIndexOfZip);
            }
            _item.setZip(_tmpZip);
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            _item.setStatus(_tmpStatus);
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
