package iestrassierra.jlcamunas.trasstarea.ficheros;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class EscribirFicheros {
    private final Context context;

    public EscribirFicheros(Context context) {
        this.context = context;
    }

    public void escribirExterno(String carpeta, String fichero, String texto) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            escribirSD(carpeta, fichero, texto);
        } else {
            Toast.makeText(context, "La memoria externa no está disponible", Toast.LENGTH_SHORT).show();
        }
    }

    public void leerExterno(String fichero) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            String texto = leerSD(fichero);
            // Handle the read text as needed, e.g., update UI
        } else {
            Toast.makeText(context, "La memoria externa no está disponible", Toast.LENGTH_SHORT).show();
        }
    }

    public String leerExternoUri(Uri uri) {
        String texto = "";
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    texto = stringBuilder.toString();
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        return texto;
    }

    public void escribirSD(String carpeta, String fichero, String texto) {
        File directorio = new File(context.getExternalFilesDir(null), carpeta);
        if (!directorio.exists()) {
            directorio.mkdirs(); // Crea el directorio si no existe
        }
        File file = new File(directorio, fichero);
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(file));
            osw.write(texto);
            osw.flush();
            osw.close();
            Toast.makeText(context, "Se ha guardado la nota con éxito", Toast.LENGTH_SHORT).show();
        } catch (IOException | NullPointerException e) {
            Toast.makeText(context, "Error en lectura/escritura de la nota", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    public void escribirSD(String carpeta, String nombreDocumento, String contenido) {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable && mExternalStorageWriteable) {
            File directory = new File(Environment.getExternalStorageDirectory(), carpeta);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, nombreDocumento);
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(contenido);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "No se puede escribir en la memoria externa", Toast.LENGTH_SHORT).show();
        }
    }
    */

    public String leerSD(String fichero) {
        String texto = "";
        File file = new File(context.getExternalFilesDir(null), fichero);
        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader archivo = new InputStreamReader(fIn);
            BufferedReader buff = new BufferedReader(archivo);
            String strTmp;
            StringBuilder strBuilder = new StringBuilder();
            while ((strTmp = buff.readLine()) != null) {
                strTmp += "\n";
                strBuilder.append(strTmp);
            }
            buff.close();
            archivo.close();
            texto = strBuilder.toString();
        } catch (FileNotFoundException f) {
            Toast.makeText(context, "Fichero no encontrado", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Error en lectura/escritura de la nota", Toast.LENGTH_SHORT).show();
        }
        return texto;
    }
}
