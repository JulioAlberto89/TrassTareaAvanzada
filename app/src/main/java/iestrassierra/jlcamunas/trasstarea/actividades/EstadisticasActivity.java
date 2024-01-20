package iestrassierra.jlcamunas.trasstarea.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.TextView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaViewModel;
import iestrassierra.jlcamunas.trasstarea.basedatos.TareaDataBase;

public class EstadisticasActivity extends AppCompatActivity {
    private TareaDataBase tareaDataBase;
    private TextView numTareas, prioritarias, terminadas, mitad, porcentaje, documentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        numTareas = findViewById(R.id.tvNumActDato);
        prioritarias = findViewById(R.id.tvNumPrioDato);
        terminadas = findViewById(R.id.tvNumTerminadasDato);
        mitad = findViewById(R.id.tvNumMitadDato);
        porcentaje = findViewById(R.id.tvPorcentajeTermDato);
        documentos = findViewById(R.id.tvDocumentosDato);

        //Inicializo la base de datos
        tareaDataBase = TareaDataBase.getInstance(this.getApplicationContext());

        LiveData<Integer> countTotalTareasLiveData = tareaDataBase.tareaDAO().getCountTotalTareas();
        LiveData<Integer> countTareasPrioritariasLiveData = tareaDataBase.tareaDAO().getCountTareasPrioritarias();
        LiveData<Integer> countTareasTerminadasLiveData = tareaDataBase.tareaDAO().getCountTareasTerminadas();
        LiveData<Integer> countTareasMitadLiveData = tareaDataBase.tareaDAO().getCountTareasConMitadOmasProgreso();
        LiveData<Integer> countTareasPorcentajeLiveData = tareaDataBase.tareaDAO().getCountTareasPorcentajeTerminadas();
        LiveData<Integer> countTareasDocumentosLiveData = tareaDataBase.tareaDAO().getCountTareasConDocumentos();

        countTotalTareasLiveData.observe(this, countTotalTareas -> {
            // Actualiza el TextView con el número total de tareas
            numTareas.setText(String.valueOf(countTotalTareas));
        });

        countTareasPrioritariasLiveData.observe(this, countTareasPrioritarias -> {
            // Actualiza el TextView con el número de tareas prioritarias
            prioritarias.setText(String.valueOf(countTareasPrioritarias));
        });

        countTareasTerminadasLiveData.observe(this, countTareasTerminadas -> {
            // Actualiza el TextView con el número de tareas prioritarias
            terminadas.setText(String.valueOf(countTareasTerminadas));
        });


        countTareasPorcentajeLiveData.observe(this, countTareasPorcentaje -> {
            // Actualiza el TextView con el porcentaje de tareas terminadas (como un entero)
            porcentaje.setText(String.valueOf(countTareasPorcentaje));
        });

        countTareasMitadLiveData.observe(this, countTareasMitad -> {
            // Actualiza el TextView con el número de tareas con al menos la mitad de progreso
            mitad.setText(String.valueOf(countTareasMitad));
        });

        countTareasDocumentosLiveData.observe(this, countTareasDocumentos -> {
            // Actualiza el TextView con el número de tareas con documentos adjuntos
            documentos.setText(String.valueOf(countTareasDocumentos));
        });
    }
}