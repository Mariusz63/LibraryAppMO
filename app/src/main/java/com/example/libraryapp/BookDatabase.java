package com.example.libraryapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Book.class}, version = 1, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    public abstract BookDao bookDao();

    private static volatile BookDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static BookDatabase getDatabase(final Context context) {
        if (INSTANCE == null)
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BookDatabase.class, "book_db")
                    .addCallback(sRoomDatabaseCallback)
                    .build();
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                BookDao dao = INSTANCE.bookDao();
                dao.deleteAll();

                Book book = new Book("Nic mnie nie złamie. Zapanuj nad swoim umysłem i pokonaj przeciwności losu", "David Goggins");
                dao.insert(book);
                book = new Book("Nawyki warte miliony. Jak nauczyć się zachowań przynoszących bogactwo", "Brian Tracy");
                dao.insert(book);
                book = new Book("Operacja Mir", "Remigiusz Mróz");
                dao.insert(book);
                book = new Book("Himalaya. Wyprawa na krawędź życia", "Wojciech Nerkowski");
                dao.insert(book);
                book = new Book("Kleks. Nowy początek", "Katarzyna Rygiel");
                dao.insert(book);
            });
        }
    };
}
