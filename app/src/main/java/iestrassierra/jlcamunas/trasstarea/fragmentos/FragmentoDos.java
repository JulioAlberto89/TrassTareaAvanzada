package iestrassierra.jlcamunas.trasstarea.fragmentos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.actividades.ListadoTareasActivity;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaViewModel;

public class FragmentoDos extends Fragment {

    private TareaViewModel tareaViewModel;
    private EditText etDescripcion;
    Uri selectedUri;

    //Interfaces de comunicación con la actividad para el botón Guardar y Volver
    public interface ComunicacionSegundoFragmento {
        void onBotonGuardarClicked();
        void onBotonVolverClicked();
    }

    private ComunicacionSegundoFragmento comunicadorSegundoFragmento;


    public FragmentoDos() {}

    //Sobrescribimos onAttach para establecer la comunicación entre el fragmento y la actividad
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ComunicacionSegundoFragmento) { //Si la actividad implementa la interfaz
            comunicadorSegundoFragmento = (ComunicacionSegundoFragmento) context; //La actividad se convierte en escuchadora
        } else {
            throw new ClassCastException(context + " debe implementar la interfaz de comunicación con el segundo fragmento");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        //Vinculamos el fragmento con el ViewModel
        tareaViewModel = new ViewModelProvider(requireActivity()).get(TareaViewModel.class);

        //Inflamos el fragmento
        return inflater.inflate(R.layout.fragment_segundo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Binding y set EditText Descripción
        etDescripcion = view.findViewById(R.id.et_descripcion);
        tareaViewModel.getDescripcion().observe(getViewLifecycleOwner(), s -> etDescripcion.setText(s));

        //Binding y config boton Volver
        Button btVolver = view.findViewById(R.id.bt_volver);
        btVolver.setOnClickListener(v -> {
            //Escribimos en el ViewModel
            escribirViewModel();
            //Llamamos al método onBotonVolverClicked que está implementado en la actividad
            if(comunicadorSegundoFragmento != null)
                comunicadorSegundoFragmento.onBotonVolverClicked();
        });

        //Binding y config boton Guardar
        Button btGuardar = view.findViewById(R.id.bt_guardar);
        btGuardar.setOnClickListener(v -> {
            //Escribimos en el ViewModel
            tareaViewModel.setDescripcion(etDescripcion.getText().toString());

            //Llamamos al método onBotonGuardarClicked que está implementado en la actividad.
            if(comunicadorSegundoFragmento != null)
                comunicadorSegundoFragmento.onBotonGuardarClicked();
        });

        //Botón que abre el documento y guarda su url.
        Button btDocumento = view.findViewById(R.id.bt_documentos);
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(
                new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // Guarda la Uri en la variable
                        selectedUri = uri;

                        Bundle bundle = new Bundle();
                        bundle.putString("uri", selectedUri.toString());

                        // Notifica a la actividad que tiene datos para procesar
                        getParentFragmentManager().setFragmentResult("urlDocumento", bundle);
                        Toast.makeText(getActivity(), "URL del documento: " + uri, Toast.LENGTH_SHORT).show();

                        /////////////////////////////////////////////////////////////////////////
                        //Esta parte es para guardar el documento seleccionado en un directorio
                        // Obtener la ruta del directorio de archivos privados de la aplicación
                        File directory = requireContext().getFilesDir();

                        // Crear un archivo en el directorio de la aplicación
                        File destinationFile = new File(directory, "documento_" + System.currentTimeMillis());

                        try {
                            // Copiar el contenido del archivo seleccionado al nuevo archivo de destino
                            InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
                            OutputStream outputStream = new FileOutputStream(destinationFile);

                            byte[] buffer = new byte[1024];
                            int length;

                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }

                            // Cerrar streams
                            inputStream.close();
                            outputStream.close();

                            // Notificar a la actividad que tiene datos para procesar (ruta del archivo guardado)
                            Bundle bundleDocumento = new Bundle();
                            bundle.putString("uriDocumento", destinationFile.getAbsolutePath());
                            getParentFragmentManager().setFragmentResult("uriDocumento", bundleDocumento);

                            Toast.makeText(requireContext(), "Archivo guardado en: " + destinationFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
                        }
                        /////////////////////////////////////////////////////////////////////////
                    }
                });

        btDocumento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("*/*");
            }
        });
    }

    //Método para guardar el estado del formulario en un Bundle
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("descripcion",  etDescripcion.getText().toString());
        //Sincronizamos la información salvada también en el ViewModel
        escribirViewModel();
    }

    //Método para restaurar el estado del formulario
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState!=null) {
            etDescripcion.setText(savedInstanceState.getString("descripcion"));
        }
    }

    private void escribirViewModel(){
        tareaViewModel.setDescripcion(etDescripcion.getText().toString());
    }

}