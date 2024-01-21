package iestrassierra.jlcamunas.trasstarea.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import android.annotation.SuppressLint;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaAdapter;
import iestrassierra.jlcamunas.trasstarea.basedatos.TareaDataBase;
import iestrassierra.jlcamunas.trasstarea.modelo.Tarea;

public class ListadoTareasActivity extends AppCompatActivity {
    private RecyclerView rv;
    private TextView listadoVacio;
    private MenuItem menuItemPrior;
    private ArrayList<Tarea> tareas = new ArrayList<>();
    private TareaAdapter adaptador;
    private boolean boolPrior = false;
    private Tarea tareaSeleccionada;
    private SharedPreferences sharedPreferences;

    // Variable para almacenar el tamaño de la fuente anterior
    private String previoFontSize;
    private boolean temaPrevio;
    private TareaDataBase tareaDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_tareas);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //Binding del TextView
        listadoVacio = findViewById(R.id.listado_vacio);

        //Binding del RecyclerView
        rv = findViewById(R.id.listado_recycler);

        //RESTAURACIÓN DEL ESTADO GLOBAL DE LA ACTIVIDAD
        if (savedInstanceState != null) {
            //Recuperamos la lista de Tareas y el booleano prioritarias
            tareas = savedInstanceState.getParcelableArrayList("listaTareas");
            boolPrior = savedInstanceState.getBoolean("boolPrior");
        } else {
            //Inicializamos la lista de tareas y el booleano prioritarias
            inicializarListaTareas();
            boolPrior = false;
        }

        //Creamos el adaptador y lo vinculamos con el RecycleView
        adaptador = new TareaAdapter(this,  tareas, boolPrior);
        rv.setAdapter(adaptador);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        //Registramos el rv para el menú contextual
        registerForContextMenu(rv);

        //Comprobamos si el listado está vacío
        comprobarListadoVacio();

        // Inicializa previousFontSize con el tamaño de la fuente actual
        previoFontSize = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("tamanyo_letra", "@string/media");

        //Controlar el tema
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //previousFontSize = sharedPreferences.getString("signature", "@string/media");
        temaPrevio = sharedPreferences.getBoolean("tema", false);

        //Inicializo la base de datos
        tareaDataBase = TareaDataBase.getInstance(this.getApplicationContext());
    }

    //Activamos las opciones de preferencias
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Comprueba si el tamaño de la fuente ha cambiado
        String actualFontSize = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("tamanyo_letra", "@string/media");
        boolean temaActual = sharedPreferences.getBoolean("tema", false);

        if (!actualFontSize.equals(previoFontSize) || temaActual != temaPrevio) {
            recreate();

            previoFontSize = actualFontSize;
            temaPrevio = temaActual;
        }
    }

    //Método que ordena las tareas según distintos criterios.
    //Compruebo primero si está activada la opción ascendente o descendente.
    private void ordenarTareas() {
        // Obtiene el valor actual del SwitchPreference
        boolean ordenAscendente = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("orden", false);

        String sortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("ordenacion", "@array/criterio_ordenacion_valores");

        switch (sortOrder) {
            case "a":
                Collections.sort(tareas, new Comparator<Tarea>() {
                    @Override
                    public int compare(Tarea o1, Tarea o2) {
                        //return o1.getTitulo().compareTo(o2.getTitulo());
                        int comparacion = o1.getTitulo().compareTo(o2.getTitulo());
                        return ordenAscendente ? comparacion : -comparacion;
                    }
                });
                break;
            case "b":
                Collections.sort(tareas, new Comparator<Tarea>() {
                    @Override
                    public int compare(Tarea o1, Tarea o2) {
                        //return o1.getFechaCreacion().compareTo(o2.getFechaCreacion());
                        int comparacion = o1.getFechaCreacion().compareTo(o2.getFechaCreacion());
                        return ordenAscendente ? comparacion : -comparacion;
                    }
                });
                break;

            case "c":
                Collections.sort(tareas, new Comparator<Tarea>() {
                    @Override
                    public int compare(Tarea o1, Tarea o2) {
                        //return Integer.compare(o1.quedanDias(), o2.quedanDias());
                        int comparacion = Integer.compare(o1.quedanDias(), o2.quedanDias());
                        return ordenAscendente ? comparacion : -comparacion;

                    }
                });
                break;
            case "d":
                Collections.sort(tareas, new Comparator<Tarea>() {
                    @Override
                    public int compare(Tarea o1, Tarea o2) {
                        int comparacion = Integer.compare(o1.getProgreso(), o2.getProgreso());
                        return ordenAscendente ? comparacion : -comparacion;
                    }
                });
        }
    }

    //SALVADO DEL ESTADO GLOBAL DE LA ACTIVIDAD
    //Salva la lista de tareas y el valor booleano de prioritarias para el caso en que la actividad
    // sea destruida por ejemplo al cambiar la orientación del dispositivo
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("listaTareas", tareas);
        outState.putBoolean("prioritarias", boolPrior);
    }


    ////////////////////////////////////// OPCIONES DEL MENÚ ///////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menuItemPrior = menu.findItem(R.id.item_priority);
        //
        menu.setGroupVisible(R.id.item_preferences,true);
        //Colocamos el icono adecuado
        iconoPrioritarias();
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        //OPCION CREAR TAREA
        if (id == R.id.item_add) {
            //Llamada al launcher con contrato y respuesta definidos
            lanzadorCrearTarea.launch(null);
        }

        //OPCION MOSTRAR PRIORITARIAS / TODAS
        else if (id == R.id.item_priority) {

            //Conmutamos el valor booleando
            boolPrior = !boolPrior;
            //Colocamos el icono adecuado
            iconoPrioritarias();
            adaptador.setBoolPrior(boolPrior);
            adaptador.notifyDataSetChanged();

            //Comprobamos que hay algún elemento que mostrar
            comprobarListadoVacio();
        }

        //OPCIÓN ACERCA DE...
        else if (id == R.id.item_about) {

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.trasstarea);
            imageView.requestLayout();

            //Creamos un AlertDialog como cuadro de diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.about_title);
            builder.setView(imageView);
            builder.setMessage(R.string.about_msg);
            // Botón "Aceptar"
            builder.setPositiveButton(R.string.about_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // Cerrar el cuadro de diálogo.
                }
            });
            // Mostrar el cuadro de diálogo
            builder.create().show();
        } else if (id == R.id.item_preferences) {
            Intent intent = new Intent(this, SettingsActivity.class);
            lanzadorPreferencias.launch(intent);
            return true;
        }
        else if (id == R.id.item_estadisticas) {
            Intent intent = new Intent(this, EstadisticasActivity.class);
            startActivity(intent);
            return true;
        }

        //OPCIÓN SALIR
        else if (id == R.id.item_exit) {
            Toast.makeText(this,R.string.msg_salida, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

    //////////////////////////////// OPCIONES DEL MENÚ CONTEXTUAL  /////////////////////////////////
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item){
        
        //Leemos la tarea seleccionada en el evento de mostrar el menú contextual
        tareaSeleccionada = adaptador.getTareaSeleccionada();

        int itemId = item.getItemId();

        //OPCION DESCRIPCIÓN
        if (itemId == R.id.item_descripcion) {
            // Mostrar un cuadro de diálogo con la descripción de la tarea
            AlertDialog.Builder builder = new AlertDialog.Builder(ListadoTareasActivity.this);
            builder.setTitle(R.string.dialog_description);
            builder.setMessage(tareaSeleccionada.getDescripcion());
            builder.setPositiveButton(R.string.dialog_close, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return true;
        }

        //OPCION EDITAR
        else if (itemId == R.id.item_editar) {
            lanzadorActividadEditar.launch(tareaSeleccionada);
            return true;
        }

        //OPCION BORRAR
        else if (itemId == R.id.item_borrar) {

            if(tareaSeleccionada != null){
                // Mostrar un cuadro de diálogo para confirmar el borrado
                AlertDialog.Builder builder = new AlertDialog.Builder(ListadoTareasActivity.this);
                builder.setTitle(R.string.dialog_confirmacion_titulo);
                builder.setMessage(getString(R.string.dialog_msg) + " \"" + tareaSeleccionada.getTitulo() + "\"?");
                builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int position;

                        //Borramos la tarea seleccionada de la colección tareas
                        position = tareas.indexOf(tareaSeleccionada);
                        tareas.remove(tareaSeleccionada);
                        adaptador.notifyItemRemoved(position);

                        //Borro la tarea de la base de datos.
                        Executor executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> tareaDataBase.tareaDAO().delete(tareaSeleccionada));

                        //Comprobamos si el listado ha quedado vacío
                        comprobarListadoVacio();

                        //Notificamos que la tarea ha sido borrada al usuario
                        Toast.makeText(ListadoTareasActivity.this, R.string.dialog_erased, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(R.string.dialog_no, null);
                builder.show();
            }else{
                // Si no se encuentra el elemento, mostrar un mensaje de error
                Toast.makeText(ListadoTareasActivity.this, R.string.dialog_not_found, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    ////////////////////////// COMUNICACIONES CON ACTIVIDADES SECUNDARIAS //////////////////////////

     //Contrato personalizado para el lanzador hacia la actividad CrearTareaActivity
    ActivityResultContract<Intent, Tarea> contratoCrear = new ActivityResultContract<Intent, Tarea>() {
        //En primer lugar se define el Intent de ida
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Intent intent) {
            return new Intent(context, CrearTareaActivity.class);
        }
        //Ahora se define el método de parseo de la respuesta. En este caso se recibe un objeto Tarea
        @Override
        public Tarea parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                try {
                    return (Tarea) Objects.requireNonNull(intent.getExtras()).get("NuevaTarea");
                } catch (NullPointerException e) {
                    Log.e("Error en intent devuelto", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),R.string.action_canceled, Toast.LENGTH_SHORT).show();
            }
            return null; // Devuelve null si no se pudo obtener una Tarea válida.
        }
    };

    //Registramos el lanzador hacia la actividad CrearTareaActivity con el contrato personalizado y respuesta con implementación anónima
    private final ActivityResultLauncher<Intent> lanzadorCrearTarea = registerForActivityResult(contratoCrear, new ActivityResultCallback<Tarea>() {
        @Override
        public void onActivityResult(Tarea nuevaTarea) {
            if (nuevaTarea != null) {
                tareas.add(nuevaTarea);
                // Ordenar la lista después de agregar la nueva tarea
                adaptador.notifyItemInserted(tareas.size() - 1); // Agregar el elemento nuevo al adaptador.
                Toast.makeText(ListadoTareasActivity.this.getApplicationContext(), R.string.tarea_add, Toast.LENGTH_SHORT).show();
                comprobarListadoVacio();
            }
        }
    });

    //Aquí llamo al método de ordenar las tareas previamente realizado.
    private final ActivityResultLauncher<Intent> lanzadorPreferencias = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            recreate();
            ordenarTareas();
            adaptador.notifyDataSetChanged();
        }
    });

    //Contrato para el lanzador hacia la actividad EditarTareaActivity
    ActivityResultContract<Tarea, Tarea> contratoEditar = new ActivityResultContract<Tarea, Tarea>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Tarea tarea) {
            Intent intent = new Intent(context, EditarTareaActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("TareaEditable", tarea);
            intent.putExtras(bundle);
            return intent;
        }

        @Override
        public Tarea parseResult(int i, @Nullable Intent intent) {
            if (i == Activity.RESULT_OK && intent != null) {
                try {
                    return (Tarea) Objects.requireNonNull(intent.getExtras()).get("TareaEditada");
                } catch (NullPointerException e) {
                    Log.e("Error en intent devuelto", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }else if(i == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),R.string.action_canceled, Toast.LENGTH_SHORT).show();
            }
            return null; // Devuelve null si no se pudo obtener una Tarea válida.
        }
    };

    //Respuesta para el lanzador hacia la actividad EditarTareaActivity
    ActivityResultCallback<Tarea> resultadoEditar = new ActivityResultCallback<Tarea>() {
        @Override
        public void onActivityResult(Tarea tareaEditada) {
            if (tareaEditada != null) {
                //Seteamos el id de la tarea recibida para que coincida con el de la tarea editada
                tareaEditada.setId(tareaSeleccionada.getId());

                // Actualizamos la tarea en la base de datos en un hilo separado
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        TareaDataBase db = TareaDataBase.getInstance(getApplicationContext());
                        db.tareaDAO().update(tareaEditada);
                    }
                });

                //Procedemos a la sustitución de la tarea editada por la seleccionada.
                int posicionEnColeccion = tareas.indexOf(tareaSeleccionada);
                tareas.remove(tareaSeleccionada);
                tareas.add(posicionEnColeccion, tareaEditada);

                //Notificamos al adaptador y comprobamos si el listado ha quedado vacío
                adaptador.notifyItemChanged(posicionEnColeccion);
                comprobarListadoVacio();

                //Comunicamos que la tarea ha sido editada al usuario
                Toast.makeText(getApplicationContext(), R.string.tarea_edit, Toast.LENGTH_SHORT).show();
            }
        }
    };

    //Registramos el lanzador hacia la actividad EditarTareaActivity con el contrato y respuesta personalizados
    ActivityResultLauncher<Tarea> lanzadorActividadEditar = registerForActivityResult(contratoEditar, resultadoEditar);

    //////////////////////////////////////// OTROS MÉTODOS /////////////////////////////////////////

    //Método para cambiar el icono de acción para mostrar todas las tareas o solo prioritarias
    private void iconoPrioritarias(){
        if(boolPrior)
            //Ponemos en la barra de herramientas el icono PRIORITARIAS
            menuItemPrior.setIcon(R.drawable.baseline_star_outline_24);
        else
            //Ponemos en la barra de herramientas el icono NO PRIORITARIAS
            menuItemPrior.setIcon(R.drawable.baseline_star_outline_24_crossed);
    }

    //Método que comprueba si el listado de tareas está vacío.
    //Cuando está vacío oculta el RecyclerView y muestra el TextView con el texto correspondiente.
    private void comprobarListadoVacio(){

        ViewTreeObserver vto = rv.getViewTreeObserver();

        //Observamos cuando el RecyclerView esté completamente terminado
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Contamos la altura total del RecyclerView
                int alturaRV = 0;
                for (int i = 0; i < adaptador.getItemCount(); i++) {
                    View itemView = rv.getChildAt(i);
                    if (itemView != null)
                        alturaRV += itemView.getHeight();
                }

                if (alturaRV == 0) {
                    listadoVacio.setText(boolPrior?R.string.listado_tv_no_prioritarias:R.string.listado_tv_vacio);
                    listadoVacio.setVisibility(View.VISIBLE);
                } else {
                    listadoVacio.setVisibility(View.GONE);
                }

                // Remueve el oyente para evitar llamadas innecesarias
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


    }

    // Método de inicialización de la colección. Tareas de ejemplo.
    private void inicializarListaTareas(){

        //Cambio la funcionalidad de éste método para mostrar las tareas creadas.
        // Inicializa la base de datos
        TareaDataBase tareaDataBase = TareaDataBase.getInstance(getApplicationContext());

        // Recupera las tareas de la base de datos
        tareaDataBase.tareaDAO().getAll().observe(this, tareasDB -> {
            if (tareasDB != null) {
                // Actualiza la lista de tareas en el hilo principal
                tareas.clear();
                tareas.addAll(tareasDB);
                adaptador.notifyDataSetChanged();

                // Comprueba si el listado ha quedado vacío
                comprobarListadoVacio();
            }
        });

        tareas.add(new Tarea("Hacer el cuestionario inicial", "10/09/2023", "17/09/2023", 100, true, ""));
        tareas.add(new Tarea("Hacer la tarea UT01", "18/09/2023", "03/10/2023", 100, true, ""));
        tareas.add(new Tarea("Hacer cuestionarios UT01", "18/09/2023", "01/10/2023", 100, false, ""));
        tareas.add(new Tarea("Hacer la tarea UT02", "02/10/2023", "23/11/2023", 100, true, ""));
        tareas.add(new Tarea("Hacer cuestionarios UT02", "02/10/2023", "22/11/2023", 50, false, ""));
        tareas.add(new Tarea("Hacer la tarea UT03", "24/11/2023", "08/01/2024", 50, true, ""));
        tareas.add(new Tarea("Hacer cuestionarios UT03", "24/11/2023", "07/01/2024", 25, false, ""));
        tareas.add(new Tarea("Hacer la tarea UT04", "08/01/2024", "12/02/2024", 0, true, ""));
        tareas.add(new Tarea("Hacer cuestionarios UT04", "08/01/2024", "12/02/2024", 0, false, ""));
        tareas.add(new Tarea("Hacer la tarea UT05", "13/02/2024", "07/04/2024", 0, true, ""));
        tareas.add(new Tarea("Hacer cuestionarios UT05", "13/02/2024", "07/04/2024", 0, false, ""));
        tareas.add(new Tarea("Hacer la tarea UT06", "08/04/2024", "20/05/2024", 0, true, ""));
        tareas.add(new Tarea("Hacer cuestionarios UT06", "08/04/2024", "20/05/2024", 0, false, ""));

        //En cada tarea de ejemplo incluímos una descripción larga generada con Lorem Ipsum
        tareas.forEach(tarea -> tarea.setDescripcion(
                    "In volutpat fringilla finibus. Proin fermentum, nulla sit amet congue tincidunt, libero ante facilisis nisl, non tempor enim ligula ut ipsum. Cras a turpis blandit, molestie elit vulputate, porttitor massa. Nulla facilisi. In quis vehicula velit. Cras eget dui dui. Sed sit amet placerat orci. Aliquam nec orci sit amet lectus bibendum faucibus bibendum quis dolor. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed non neque et dui ultrices sodales. Cras vitae velit in dolor luctus aliquet. Sed vel elit et nunc dignissim varius vel ac leo.\n\n" +
                    "Maecenas porta feugiat auctor. Aliquam nec justo nec arcu venenatis vehicula.\n\nNunc metus libero, pellentesque a feugiat nec, fringilla a arcu. Donec nisi lorem, ullamcorper id augue vel, ultricies bibendum nulla. Sed tincidunt vitae erat in pellentesque. Sed accumsan est non ligula interdum, nec molestie magna sagittis. Praesent nibh neque, feugiat sagittis tempor hendrerit, suscipit vitae elit. Sed tempus, turpis at porta lobortis, dui arcu posuere leo, a auctor sapien est sit amet sapien. Nunc et purus ac sem pharetra aliquet. Cras magna magna, condimentum id nulla in, ultrices ultricies lacus. Nam finibus magna et augue egestas, sit amet malesuada felis porttitor. In sed tortor elementum, tempus eros nec, iaculis diam.\n\n" +
                    "Etiam id feugiat nisl, a ultricies augue.\nDonec dui metus, congue in ligula eu, pulvinar eleifend nisi. Vestibulum porttitor ut mauris eget maximus. Nunc sit amet ex faucibus, maximus magna at, placerat libero. Curabitur sodales ut ante tempor auctor. Vestibulum quis ornare tortor. Proin convallis felis vel tempus condimentum. Duis vestibulum porttitor hendrerit. Integer eget lacus finibus, maximus lacus a, finibus nibh. Proin eget placerat libero. Ut eget nisi nec orci convallis mattis in vel purus. Cras dapibus, ante id varius lobortis, lorem elit sagittis ex, mollis egestas urna arcu nec quam. Aenean lacinia, dui at dictum lacinia, leo nulla tristique odio, in laoreet massa enim at arcu.\n\n" +
                    "Pellentesque commodo nisi ut pellentesque condimentum.\n\nSed ultricies efficitur ipsum eget scelerisque. Nunc ultrices et quam at sagittis. Quisque ut tortor tempor, finibus elit quis, commodo sapien. Phasellus a purus enim. Integer semper pretium tempus. Morbi congue metus dictum molestie luctus. Sed sit amet euismod lectus, sit amet ultricies magna.\n\n" +
                    "Cras imperdiet, ligula quis semper ultrices, lectus dolor bibendum nisl, vitae gravida orci purus nec nisi.\n\nNulla pharetra tempus risus at convallis. Vivamus in orci maximus, faucibus nisl at, dignissim eros. Duis viverra leo ut lorem dapibus elementum. Pellentesque congue ut arcu nec vulputate. Sed pretium libero nibh, sit amet volutpat turpis maximus in. Suspendisse nec quam sit amet diam dictum auctor. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae;\n\n" +
                    "Nam eget ante eget mauris pulvinar tempus id in lacus.\n\nPellentesque ut accumsan arcu. Etiam ultrices metus nec leo lobortis, id vehicula tortor eleifend. In mattis nisl eget mauris ultrices lacinia at quis lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Praesent enim tellus, dignissim sed velit eu, lobortis bibendum lacus. Duis ultricies faucibus tellus, nec tempus elit blandit et. Cras tristique nunc velit, a efficitur ante imperdiet sit amet. Nulla sit amet interdum purus, in mollis sem. Nulla pharetra id sem et dignissim.\n\n" +
                    "Morbi a lobortis nibh, sagittis venenatis elit. Pellentesque lacus nulla, sollicitudin ac posuere non, iaculis nec mauris.\n\nEtiam cursus tincidunt congue. Duis vitae posuere odio. Maecenas id magna mauris. Cras a nulla tempor, ultricies nisi non, vestibulum orci. Maecenas at risus non ipsum luctus ultrices eu ac elit. Donec sed urna a leo aliquam viverra id sed magna. Duis fermentum imperdiet massa, vel iaculis nulla dictum ac. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nunc placerat sapien magna, nec tristique odio euismod eu. Nunc non pretium nibh. Donec nec nulla sed dui bibendum placerat. Morbi venenatis ex vitae ex maximus dignissim sit amet sed risus. Etiam viverra ipsum purus, ut consectetur risus faucibus nec.\n\n" +
                    "Mauris pellentesque dictum dolor ac imperdiet. Etiam a iaculis justo, sit amet dignissim lectus.\n\nMauris iaculis mauris maximus, imperdiet ipsum sit amet, sodales urna. Ut non tempor sapien, eget ullamcorper odio. Pellentesque facilisis neque id nulla congue maximus. Donec aliquet mauris in turpis maximus, ut fringilla tellus iaculis. In euismod volutpat nulla, non hendrerit magna ullamcorper in. Pellentesque eu vestibulum leo, in dapibus orci. Praesent elementum convallis arcu quis iaculis. Aliquam ornare tortor in arcu dignissim, at efficitur turpis dignissim. Vestibulum eleifend felis nec nulla fringilla, vel bibendum lacus ultricies. Cras ultricies tincidunt lacus, quis interdum mi. Pellentesque eu venenatis ex. Morbi dapibus pretium justo vel vehicula.\n\n" +
                    "Nunc tristique odio eget pellentesque viverra.\n\nPellentesque sed turpis auctor, suscipit purus eget, varius lacus. Vivamus at aliquam lectus. Maecenas egestas mollis augue id finibus. Donec rutrum sem ut eros lacinia hendrerit. Vestibulum egestas et turpis vitae posuere. Pellentesque rhoncus orci ut tellus sodales, in condimentum nulla sagittis. Nulla sem ligula, maximus vitae ex vel, semper sodales sem. Sed non libero ac ipsum auctor consectetur id non urna. Pellentesque neque erat, maximus molestie tellus non, vulputate sagittis urna. Integer lectus purus, dignissim et accumsan sit amet, luctus sed tellus.\n\n" +
                    "Sed tempus eu orci et lacinia. Donec posuere a velit ut vehicula.\n\nNam a mollis purus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Quisque eu interdum nisl. Ut eu est et est tristique luctus a eu eros. Morbi ac ullamcorper diam. In libero turpis, pharetra sed lacinia vitae, euismod sed neque. Sed tincidunt ultrices lectus a auctor.\n\n" +
                    "Nam eleifend, nisl in semper accumsan, massa sapien convallis sapien, sed mattis magna eros eget enim.\n\nNullam commodo hendrerit felis, sed fermentum velit varius eget. Curabitur interdum porta tempus. Donec diam ligula, sodales at lectus vitae, porttitor vehicula velit. Pellentesque convallis nibh elit, aliquet placerat sem rhoncus id. Nullam dapibus maximus nisi vel suscipit. Vivamus sagittis mi vel risus efficitur tincidunt.\n\n" +
                    "Vestibulum tincidunt maximus turpis, eget fermentum lectus iaculis non. Proin vulputate metus sed metus laoreet ultricies. Maecenas pulvinar lectus quis pretium rhoncus. Suspendisse eget dolor vel nisi aliquet condimentum id a erat. Donec rutrum sem."
            )
        );
        //Guardar las tareas de prueba
        // Inicializa la base de datos
        /*
        TareaDataBase tareaDataBase = TareaDataBase.getInstance(getApplicationContext());

        // Inserta las tareas en la base de datos
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            for (Tarea tarea : tareas) {
                tareaDataBase.productoDAO().insertAll(tarea);
            }
        });
         */
    }

}