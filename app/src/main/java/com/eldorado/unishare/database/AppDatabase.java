package com.eldorado.unishare.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.eldorado.unishare.model.Device;
import com.eldorado.unishare.model.Message;

@Database(entities = {Device.class, Message.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeviceDao deviceDao();
    public abstract MessageDao messageDao();
}
