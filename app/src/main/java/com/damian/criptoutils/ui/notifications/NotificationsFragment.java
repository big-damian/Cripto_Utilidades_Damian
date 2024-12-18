package com.damian.criptoutils.ui.notifications;

import static com.damian.criptoutils.ui.account.AccountFragment.loggeado;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.damian.criptoutils.R;
import com.damian.criptoutils.databinding.FragmentNotificationsBinding;
import com.damian.criptoutils.miscriptorecyclersqlite.ListaMisCriptoAdapter;
import com.damian.criptoutils.miscriptorecyclersqlite.MisCriptomonedas;
import com.damian.criptoutils.utilities.MySQLManager;
import com.damian.criptoutils.utilities.SQLiteManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    // Mis variables para el Spinner
    private Spinner spinnerListaCriptos;
    private ArrayList<String> listaCriptoSpinner = new ArrayList<>();
    private ArrayAdapter<String> adapterSpinnerListaCriptos;
    private EditText editTextCantidadCriptos;
    RequestQueue requestQueue;

    // Variables para SQLite
    private SQLiteManager SQLiteBD;

    // Variable para cargar/guardar criptos en cuenta del usuario
    public static String loggeadoEmail;

    // Mis variables para el recycler
    private RecyclerView recyclerView;
    private ListaMisCriptoAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ////////////////////////////////////////////////////////////////////////////////////////////
        // MI CODIGO OnCreate
        //

        // Iniciamos la BD
        SQLiteBD = new SQLiteManager(getActivity());
        SQLiteBD.open();

        // Codigo para el recycler
        recyclerView = binding.recyclerMisCriptosLista;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Obtener los datos de la base de datos
        List<MisCriptomonedas> cryptoList = SQLiteBD.selectTodasMiscriptos();

        // Configurar el adaptador
        adapter = new ListaMisCriptoAdapter(getContext(), cryptoList);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            binding.layoutNoMisCriptosGuardadas.setVisibility(View.VISIBLE);
        } else {
            binding.layoutNoMisCriptosGuardadas.setVisibility(View.GONE);
        }


        //
        // FIN DE MI CODIGO OnCreate
        ////////////////////////////////////////////////////////////////////////////////////////////

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.botonAnadir).setOnClickListener(v -> showDialog(view));

        // Boton guardar mis criptos en la nube
        binding.botonGuardarMisCriptoNube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggeado) {
                    guardarMisCriptoNube();
                } else {
                    Snackbar snackbar = Snackbar.make(getView(), "Inicia sesión en tu cuenta para poder guardar tus criptomonedas", Snackbar.LENGTH_LONG);
                    /* Aplicar margen inferior de 125dp */ snackbar.getView().setTranslationY(-125 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    snackbar.show();
                }
            }
        });
        // Boton cargar mis criptos de la nube
        binding.botonCargarMisCriptoNube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggeado) {
                    cargarMisCriptoNube();
                } else {
                    Snackbar snackbar = Snackbar.make(getView(), "Inicia sesión en tu cuenta para poder cargar tus criptomonedas", Snackbar.LENGTH_LONG);
                    /* Aplicar margen inferior de 125dp */ snackbar.getView().setTranslationY(-125 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    snackbar.show();
                }
            }
        });
    }

    // Hecho con este video: https://www.youtube.com/watch?v=L1wVdGRnGEg (y la ayuda de chatGPT para que deje asignar el adapter al spinner)
    // Ayuda para dialogs en este video: https://www.youtube.com/watch?v=WSOmYN8y0_k
    private void showDialog(View fragmentView) {

        requestQueue = Volley.newRequestQueue(getActivity());

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_yourcripto);

        editTextCantidadCriptos = dialog.findViewById(R.id.editTextCantidadCriptos);

        spinnerListaCriptos = dialog.findViewById(R.id.spinnerListaCriptos);
        Button botonDialogoAgregarTusCriptos = dialog.findViewById(R.id.botonDialogoAgregarTusCriptos);
        Button botonCancelarDialogo = dialog.findViewById(R.id.botonCancelarDialogo);
        EditText editTextNumberDecimal = dialog.findViewById(R.id.editTextCantidadCriptos);

        // Establecer boton no clickable hasta que no se haya cargado la info
        botonDialogoAgregarTusCriptos.setClickable(true);

        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=eur&x_cg_demo_api_key=CG-SzhFJQ3Nbdqhxh8DMr7x7zDm";

        Log.e("SpinnerCargarListaCriptos", "Llamando a API para cargar lista de criptos");
        Log.e("SpinnerCargarListaCriptos", "URL: " + url);

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    // Activar boton aceptar y quitar texto no hay internet
                    botonDialogoAgregarTusCriptos.setActivated(true);
                    botonDialogoAgregarTusCriptos.setTextColor(Color.WHITE);
                    botonDialogoAgregarTusCriptos.setClickable(true);
                    listaCriptoSpinner.clear();

                    Log.e("SpinnerCargarListaCriptos", "Parseando: " + response);
                    for (int i=0; i<response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String nombreCriptomoneda = jsonObject.optString("name") + " (" + jsonObject.optString("symbol").toUpperCase() + ")";
                        listaCriptoSpinner.add(nombreCriptomoneda);
                        adapterSpinnerListaCriptos = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_spinner_item, listaCriptoSpinner);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaCriptoSpinner);
                        spinnerListaCriptos.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Log.e("SpinnerCargarListaCriptos", "Error parseando respuesta de Volley");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SpinnerCargarListaCriptos", "Error de Volley: " + error.toString());

                // Añadir texto no hay internet en el spinner
                listaCriptoSpinner.clear();
                listaCriptoSpinner.add("No hay conexión a internet");
                adapterSpinnerListaCriptos = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, listaCriptoSpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listaCriptoSpinner);
                spinnerListaCriptos.setAdapter(adapter);
                // Desactivar boton guardar monedas
                botonDialogoAgregarTusCriptos.setActivated(false);
//                botonDialogoAgregarTusCriptos.setEnabled(false);
                botonDialogoAgregarTusCriptos.setBackgroundColor(Color.RED);
                botonDialogoAgregarTusCriptos.setClickable(false);
            }
        });
        requestQueue.add(getRequest);

        // Configurar el botón Aceptar
        botonDialogoAgregarTusCriptos.setOnClickListener(v -> {
            String seleccionSpinner = (String) spinnerListaCriptos.getSelectedItem();
            if (!editTextNumberDecimal.getText().toString().isEmpty() && Double.parseDouble(editTextNumberDecimal.getText().toString()) > 0.0) {

                // Antes de guardar en SQLite obtenemos nombre y simbolo del spinner
                // Obtener nombre de la cripto antes del parentesis
                // ---
                // Definir el patrón regex para capturar el contenido antes del último paréntesis
                String pattern = "^(.*)\\([^)]*\\)$";
                // Compilar el patrón
                java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
                java.util.regex.Matcher matcher = compiledPattern.matcher(spinnerListaCriptos.getSelectedItem().toString()); // Pasamos el string del spinner
                // Verificar si coincide y extraer el contenido
                String nombreObtenidoTextoSpinner = "";
                if (matcher.find()) {
                    nombreObtenidoTextoSpinner = matcher.group(1).trim(); // El primer grupo captura el contenido antes de los paréntesis
                } else {
                    Log.e("SQLiteMisCriptos", "No se encontraron paréntesis al final de la cadena (Mientras se buscaba el nombre).");
                }

                // Obtener simbolo de la cripto antes del parentesis
                // ---
                // Definir el patrón regex para capturar contenido entre paréntesis al final
                pattern = "\\(([^)]+)\\)$";
                // Compilar el patrón
                compiledPattern = java.util.regex.Pattern.compile(pattern);
                matcher = compiledPattern.matcher(spinnerListaCriptos.getSelectedItem().toString()); // Pasamos el string del spinner
                // Verificar si coincide y extraer el contenido
                String simboloObtenidoTextoSpinner = "";
                if (matcher.find()) {
                    simboloObtenidoTextoSpinner = matcher.group(1); // El primer grupo captura el contenido dentro de los paréntesis
                } else {
                    Log.e("SQLiteMisCriptos", "No se encontraron paréntesis al final de la cadena (Mientras se buscaba el simbolo).");
                }

                // Guardando moneda en BDD con los resultados anteriores y el editText
                // Comprobamos que no se haya guardado ya esa cripto y en caso negativo, guardamos
                if (SQLiteBD.selectMisCriptomonedasBDD(simboloObtenidoTextoSpinner) == " ") {

                    // Guardamos Mis_Criptomonedas en SQLite
                    SQLiteBD.guardarMiCriptomoneda(nombreObtenidoTextoSpinner, simboloObtenidoTextoSpinner, editTextCantidadCriptos.getText().toString());
                    // Loggeamos que se ha guardado la criptomoneda
                    Log.e("SQLiteMisCriptos", "Guardado en SQLite: Nombre cripto: <" + nombreObtenidoTextoSpinner + ">, simbolo: <" + simboloObtenidoTextoSpinner + ">, cantidad: <" + editTextCantidadCriptos.getText().toString() + ">");

                    // Mostrar Snackbar criptomoneda guardada
                    Snackbar snackbar = Snackbar.make(fragmentView, "Agregada criptomoneda " + seleccionSpinner + " = " + editTextNumberDecimal.getText() + " unidades", Snackbar.LENGTH_LONG);
                    /* Aplicar margen inferior de 125dp */ snackbar.getView().setTranslationY(-125 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    snackbar.show();

                } else { // Si la criptomoneda se ha guardado anteriormente:

                    // Mostrar Snackbar criptomoneda ya existe
                    Snackbar snackbar = Snackbar.make(fragmentView, "No se guardó la criptomoneda " + seleccionSpinner + ". Ésta ya se ha guardado previamente. Elimina la cripto existente para guardarla de nuevo", Snackbar.LENGTH_LONG);
                    /* Aplicar margen inferior de 125dp */ snackbar.getView().setTranslationY(-125 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    snackbar.show();

                    Log.e("SQLiteMisCriptos", "Ya existe registro en SQLite Mis_Criptomonedas para cripto: <" + nombreObtenidoTextoSpinner + ">, simbolo: <" + simboloObtenidoTextoSpinner + ">");
                }

                // Actualizamos el recylcerview de MisCriptos por si el usuario ha añadido una nueva cripto
                // Obtener los datos actualizados de la base de datos
                List<MisCriptomonedas> listaMisCriptoActualizada = SQLiteBD.selectTodasMiscriptos();

                // Actualizar los datos en el adaptador
                adapter.actualizarRecycler(listaMisCriptoActualizada);
                if (adapter.getItemCount() == 0) {
                    binding.layoutNoMisCriptosGuardadas.setVisibility(View.VISIBLE);
                } else {
                    binding.layoutNoMisCriptosGuardadas.setVisibility(View.GONE);
                }

                dialog.dismiss();

            } else if (editTextNumberDecimal.getText().toString().isEmpty() || Double.parseDouble(editTextNumberDecimal.getText().toString()) <= 0.0) {
                Toast.makeText(getActivity(), "Selecciona una criptomoneda e introduce la cantidad de criptomonedas que tienes para agregarlas (mayor a cero)", Toast.LENGTH_SHORT).show();
        }
        });

        botonCancelarDialogo.setOnClickListener(v2 -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Guardar mis criptos en la nube
    private void guardarMisCriptoNube() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            // Primero eliminamos todos los registros de la cuenta
            try {
                Connection cone = MySQLManager.ConexionMySQL();
                PreparedStatement preparedStatement = cone.prepareStatement("DELETE FROM criptomonedas_usuarios WHERE email_usuario = ?");
                preparedStatement.setString(1, loggeadoEmail);
                int rowsAffected = preparedStatement.executeUpdate();
                cone.close();
                Log.e("eliminarTodasMisCriptosNube", "Eliminadas " + rowsAffected + " registros de la cuenta: " + loggeadoEmail);
            } catch (Exception e) {
                Log.e("eliminarTodasMisCriptosNube", "No se pudieron eliminar los registros de la cuenta: " + loggeadoEmail + ": " + Log.getStackTraceString(e));
            }
            // Creamos cursor para SQLite y bucle para los multiples registros
            Cursor cursor = SQLiteBD.rawQuery("SELECT * FROM Mis_Criptomonedas", null);
            if (cursor.moveToFirst()) {
                        do {
                            // Seleccionamos los registros de SQLite
                            @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                            @SuppressLint("Range") String simbolo = cursor.getString(cursor.getColumnIndex("Simbolo"));
                            @SuppressLint("Range") String cantidad = cursor.getString(cursor.getColumnIndex("Cantidad"));
                            Log.e("guardarMisCriptoNube", "Recuperadas de SQLite mis criptos para MySQL");

                            // Mandamos los registros a MySQL
                            try {
                                Connection cone = MySQLManager.ConexionMySQL();
                                PreparedStatement preparedStatement = cone.prepareStatement("INSERT INTO criptomonedas_usuarios (email_usuario, nombre_cripto, simbolo_cripto, cantidad_cripto) VALUES (?, ?, ?, ?)");
                                preparedStatement.setString(1, loggeadoEmail);
                                preparedStatement.setString(2, nombre);
                                preparedStatement.setString(3, simbolo);
                                preparedStatement.setString(4, cantidad);
                                preparedStatement.executeUpdate();
                                preparedStatement.close();
                                cone.close();
                                Log.e("guardarMisCriptoNube", "Guardadas mis criptos a MySQL");

                                // Mostramos snackbar de exito
                                Snackbar snackbar = Snackbar.make(getView(), "Tu criptomoneda " + nombre + " se ha guardado en la nube exitosamente!", Snackbar.LENGTH_LONG);
                                /* Aplicar margen inferior de 125dp */ snackbar.getView().setTranslationY(-125 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                                snackbar.show();

                            } catch (Exception e) {
                                Log.e("guardarMisCriptoNube", "Error enviando mis criptos a MySQL: " + Log.getStackTraceString(e));
                            }

                        } while (cursor.moveToNext());
                    }
            cursor.close();
        });

    }

    private void cargarMisCriptoNube() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            // Primero cogemos los registros de la cuenta en MySQL
            try {
                Connection cone = MySQLManager.ConexionMySQL();
                PreparedStatement preparedStatement = cone.prepareStatement("SELECT nombre_cripto, simbolo_cripto, cantidad_cripto FROM criptomonedas_usuarios WHERE email_usuario = ?");
                preparedStatement.setString(1, loggeadoEmail);
                ResultSet resultSet = preparedStatement.executeQuery();
                Log.e("cargarMisCriptoNube", "Registros obtenidos de MySQL con éxito");

                // Insertamos los registros en SQLite
                try {
                    // Eliminamos todos los registros de SQLite
                    SQLiteBD.deleteTodasMisCriptomonedas();
                    while (resultSet.next()) {
                        String nombre = resultSet.getString("nombre_cripto");
                        String simbolo = resultSet.getString("simbolo_cripto");
                        String cantidad = resultSet.getString("cantidad_cripto");

                        SQLiteBD.guardarMiCriptomoneda(nombre, simbolo, cantidad);
                    }
                    Log.e("cargarMisCriptoNube", "Registros guardados con éxito en SQLite");

                    // Mostramos snackbar de exito
                    Snackbar snackbar = Snackbar.make(getView(), "Tus criptomonedas se han cargado desde la nube correctamente!", Snackbar.LENGTH_LONG);
                    /* Aplicar margen inferior de 125dp */ snackbar.getView().setTranslationY(-125 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    snackbar.show();

                } catch (Exception e) {
                    Log.e("cargarMisCriptoNube", "Error guardando los registros en SQLite: " + Log.getStackTraceString(e));
                }
                resultSet.close();
                preparedStatement.close();
                cone.close();
            } catch (Exception e) {
                Log.e("cargarMisCriptoNube", "cargarMisCriptoNube error: " + Log.getStackTraceString(e));
            }
        });
        try {
            Thread.sleep(1000);
            // Actualizamos el recyclerView para que muestre los cambios
            List<MisCriptomonedas> listaMisCriptoActualizada = SQLiteBD.selectTodasMiscriptos();
            adapter.actualizarRecycler(listaMisCriptoActualizada);
            if (adapter.getItemCount() == 0) {
                binding.layoutNoMisCriptosGuardadas.setVisibility(View.VISIBLE);
            } else {
                binding.layoutNoMisCriptosGuardadas.setVisibility(View.GONE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}