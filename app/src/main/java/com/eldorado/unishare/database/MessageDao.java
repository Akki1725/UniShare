package com.eldorado.unishare.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.eldorado.unishare.model.Message;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM messages WHERE (senderId = :senderId AND receiverId = :receiverId) OR (senderId = :receiverId AND receiverId = :senderId) ORDER BY id ASC")
    LiveData<List<Message>> getMessages(String senderId, String receiverId);

    @Insert
    void addMessageToDatabase(Message message);
}
