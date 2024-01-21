package iestrassierra.jlcamunas.trasstarea.basedatos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;

@Database(entities = {Tarea.class}, version = 1, exportSchema = false)
public abstract class TareaDataBase extends RoomDatabase {
    private static TareaDataBase INSTANCIA;
    private static String nombreBdActual;

    public static TareaDataBase getInstance(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String nombreBd = sharedPreferences.getString("nombrebd", "bd");

        /*
        if (INSTANCIA == null) {
            INSTANCIA = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TareaDataBase.class,
                            nombreBd)
                    .build();
        }
         */
        // Siempre crea una nueva instancia de TareaDataBase con el nombre de la base de datos actual
        if (INSTANCIA == null || !nombreBd.equals(nombreBdActual)) {
            INSTANCIA = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TareaDataBase.class,
                            nombreBd)
                    .build();
            nombreBdActual = nombreBd;
        }
        return INSTANCIA;
    }

    public static void destroyInstance() {
        INSTANCIA = null;
    }

    public abstract TareaDAO tareaDAO();
}