package iestrassierra.jlcamunas.trasstarea.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import iestrassierra.jlcamunas.trasstarea.R;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switchMode;
    boolean modoOscuro;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Mostramos el botón 'home'
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Mostamos título
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //Establecemos el contenido del título
        getSupportActionBar().setTitle("Preferencias de usuario");

        //Cargamos el fragmento de preferencias
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            //////////////////////////Cambio de tema claro/oscuro/////////////////////////////////////
            // Obtén el estado del SwitchPreference
            boolean isNightMode = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("tema", false);

            // Cambia el tema en tiempo de ejecución
            AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

            // Encuentra el SwitchPreference y establece un listener para cambios
            SwitchPreference themeSwitch = findPreference("tema");
            if (themeSwitch != null) {
                themeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                    // Cuando el usuario cambia el SwitchPreference, actualiza el tema
                    boolean isNightMode1 = (Boolean) newValue;
                    AppCompatDelegate.setDefaultNightMode(isNightMode1 ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                    return true;
                });
            }
            //////////////////////////Cambio del tamaño de la letra////////////////////////////////////
            // Encuentra la ListPreference y establece un listener para cambios
            ListPreference fontSizePreference = findPreference("tamanyo_letra");
            if (fontSizePreference != null) {
                fontSizePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    // Cuando el usuario cambia la ListPreference, actualiza el tamaño de la fuente
                    String fontSize = (String) newValue;
                    float fontScale;
                    switch (fontSize) {
                        case "a":
                            fontScale = 0.8f;
                            break;
                        case "b":
                            fontScale = 1f;
                            break;
                        case "c":
                            fontScale = 1.2f;
                            break;
                        default:
                            fontScale = getResources().getConfiguration().fontScale;
                            break;
                    }
                    Configuration configuration = getResources().getConfiguration();
                    configuration.fontScale = fontScale;
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    getResources().updateConfiguration(configuration, metrics);

                    // Reinicia la actividad para que los cambios surtan efecto en el momento
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);

                    return true;
                });
            }
            /////////////////////////////////////////////////////////////////////
            ///////////////////////////Ordenar tareas////////////////////////////
            String sortOrderPreference = PreferenceManager
                    .getDefaultSharedPreferences(getContext())
                    .getString("ordenacion", "@array/criterio_ordenacion_valores");

            ListPreference sortOrderListPreference = findPreference("ordenacion");
            if (sortOrderListPreference != null) {
                sortOrderListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    // Cuando el usuario cambia la ListPreference, envía un Intent a la actividad principal
                    String sortOrder = (String) newValue;
                    //Intent intent = new Intent(getContext(), ListadoTareasActivity.class);
                    //intent.putExtra("sortOrder", sortOrder);
                    //startActivity(intent);
                    return true;
                });
            }
            //////////////////////////////////////////////////////////////////////////
        }
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        }
    }
}