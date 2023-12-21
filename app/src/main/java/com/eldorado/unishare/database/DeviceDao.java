package com.eldorado.unishare.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.eldorado.unishare.model.Device;

import java.util.List;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM devices")
    LiveData<List<Device>> getAllDevices();

    @Insert
    void addDevice(Device device);
}
