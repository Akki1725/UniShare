package com.eldorado.unishare.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.eldorado.unishare.model.Device;

@Database(entities = {Device.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeviceDao deviceDao();
}
