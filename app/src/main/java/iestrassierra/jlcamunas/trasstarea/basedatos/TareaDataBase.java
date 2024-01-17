package iestrassierra.jlcamunas.trasstarea.basedatos;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;

@Database(entities = {Tarea.class}, version = 1, exportSchema = false)
public abstract class TareaDataBase extends RoomDatabase {
    private static TareaDataBase INSTANCIA;

    public static TareaDataBase getInstance(Context context) {
        if (INSTANCIA == null) {
            INSTANCIA = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TareaDataBase.class,
                            "dbTarea")
                    .build();
        }
        return INSTANCIA;
    }

    public static void destroyInstance() {
        INSTANCIA = null;
    }

    public abstract TareaDAO tareaDAO();
}