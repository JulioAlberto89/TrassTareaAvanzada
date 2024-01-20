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

    //Consultas personalizadas
    // Nueva consulta para obtener el número de tareas con la mitad o más del progreso completado
    @Query("SELECT COUNT(*) FROM Tarea WHERE progreso >= 50")
    LiveData<Integer> getCountTareasConMitadOmasProgreso();

    // Puedes también tener una versión que no devuelva LiveData si no es necesario para tu caso
    @Query("SELECT COUNT(*) FROM Tarea WHERE progreso >= 50")
    int getCountTareasConMitadOmasProgresoSinLiveData();

    // Obtener el número total de tareas
    @Query("SELECT COUNT(*) FROM Tarea")
    LiveData<Integer> getCountTotalTareas();

    // Obtener el número de tareas prioritarias
    @Query("SELECT COUNT(*) FROM Tarea WHERE prioritaria = 1")
    LiveData<Integer> getCountTareasPrioritarias();

    // Obtener el número de tareas terminadas
    @Query("SELECT COUNT(*) FROM Tarea WHERE progreso = 100")
    LiveData<Integer> getCountTareasTerminadas();

    // También puedes tener una versión que no devuelva LiveData si no es necesario para tu caso
    @Query("SELECT COUNT(*) FROM Tarea WHERE progreso = 100")
    int getCountTareasTerminadasSinLiveData();

    //Número de tareas con documentos adjuntos
    @Query("SELECT COUNT(*) FROM Tarea WHERE URL_doc IS NOT NULL AND URL_doc != ''")
    LiveData<Integer> getCountTareasConDocumentos();

    //Porcentaje de terminadas
    @Query("SELECT (COUNT(*) * 100 / (SELECT COUNT(*) FROM Tarea)) FROM Tarea WHERE progreso = 100")
    LiveData<Integer> getCountTareasPorcentajeTerminadas();
}