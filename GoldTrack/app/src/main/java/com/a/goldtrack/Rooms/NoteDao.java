package com.a.goldtrack.Rooms;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    @Query("Select * from note_table order by priority desc")
    LiveData<List<Note>> getAllNotes();
}
