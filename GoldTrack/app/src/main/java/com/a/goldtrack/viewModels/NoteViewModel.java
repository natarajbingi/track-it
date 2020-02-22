package com.a.goldtrack.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.a.goldtrack.Rooms.Note;
import com.a.goldtrack.Rooms.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository noteRepository;
    private LiveData<List<Note>> notes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        noteRepository = new NoteRepository(application);
        notes = noteRepository.getAllNotes();
    }

    public void insertNote(Note note) {

        noteRepository.insertNote(note);
    }

    public void updateNote(Note note) {

        noteRepository.updateNote(note);
    }

    public void deleteNote(Note note) {

        noteRepository.deleteNote(note);
    }

    public void deleteAllNote() {

        noteRepository.deleteAllNote();
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }
}
