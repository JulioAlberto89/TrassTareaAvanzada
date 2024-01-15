package iestrassierra.jlcamunas.trasstarea.basedatos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;
@Dao
public interface TareaDAO {
    //Anotación que permite realizar una consulta de todos las Tareas de la lista
    @Query("SELECT * FROM Tarea")
    //Método que realiza la consulta anterior
    LiveData<List<Tarea>> getAll();

    //Anotación que permite realizar una consulta para las tareas con uns ids determinados
    @Query("SELECT * FROM Tarea WHERE id IN (:tareasIds)")
    //Método que realiza la consulta anterior
    List<Tarea> loadAllByIds(int[] tareasIds);

    //Anotación que permite realizar una consulta para una tarea para un título determinado
    @Query("SELECT * FROM tarea WHERE titulo LIKE :titulo LIMIT 1")
    //Método que realiza la consulta anterior
    Tarea findByTitulo(String titulo);

    //Anotación que permite realizar la inserción de una relación de tareas
    @Insert
    //Método que realiza la inserción anterior
    void insertAll(Tarea... tareas);

    //Anotación que permite realizar el borrado de una tarea
    @Delete
    //Método que realiza el borrado anterior
    void delete(Tarea tarea);
}