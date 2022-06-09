package com.example.bookkeeping.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.bookkeeping.dao.MyDao;
import com.example.bookkeeping.entities.MyData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MyData.class}, version = 2, exportSchema = false)
public abstract class Db extends RoomDatabase {
    public abstract MyDao myDao();

    public static ExecutorService threads =
            Executors.newFixedThreadPool(4);

    public static volatile Db db;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER  TABLE  " + DbHelper.MY_DATUM_TABLE_NAME + "   ADD   sign INTEGER ");
        }
    };

    public static Db getDb(final Context context) {
        if (db == null) {
            synchronized (Db.class) {
                db = Room.databaseBuilder(
                        context.getApplicationContext(),
                        Db.class,
                        //库名 datum
                        DbHelper.DATUM_DB_NAME
                ).addMigrations(MIGRATION_1_2).build();
            }
        }
        return db;
    }
}
