package com.a.goldtrack.Rooms;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    public void insertNote(Note note) {
        new InsertNoteAsync(noteDao).execute(note);
    }

    public void updateNote(Note note) {
        new UpdateNoteAsync(noteDao).execute(note);
    }

    public void deleteNote(Note note) {
        new DeleteNoteAsync(noteDao).execute(note);
    }

    public void deleteAllNote() {
        new DeleteAllNoteAsync(noteDao).execute();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    private static class InsertNoteAsync extends AsyncTask<Note, Void, Void> {

        private NoteDao noteDao;

        public InsertNoteAsync(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insertNote(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsync extends AsyncTask<Note, Void, Void> {

        private NoteDao noteDao;

        public UpdateNoteAsync(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.updateNote(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsync extends AsyncTask<Note, Void, Void> {

        private NoteDao noteDao;

        public DeleteNoteAsync(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.deleteNote(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNoteAsync extends AsyncTask<Void, Void, Void> {

        private NoteDao noteDao;

        public DeleteAllNoteAsync(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... notes) {
            noteDao.deleteAllNotes();
            return null;
        }
    }

//    private static class GetAllNoteAsync extends AsyncTask<Void, Void, LiveData<List<Note>>> {
//
//        private NoteDao noteDao;
//
//        public GetAllNoteAsync(NoteDao noteDao) {
//            this.noteDao = noteDao;
//        }
//
//        @Override
//        protected LiveData<List<Note>> doInBackground(Void... voids) {
//            return noteDao.getAllNotes();
//        }
//    }
}
