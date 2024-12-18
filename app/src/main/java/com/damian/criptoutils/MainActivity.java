package com.damian.criptoutils;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.damian.criptoutils.criptorecyclerapi.Criptomoneda;
import com.damian.criptoutils.criptorecyclerapi.ListaCriptoAdapter;
import com.damian.criptoutils.criptorecyclerapi.LlamadaAPIListaCripto;
import com.damian.criptoutils.criptorecyclerapi.RetrofitLlamadaAPIListaCripto;
import com.damian.criptoutils.databinding.ActivityMainBinding;
import com.damian.criptoutils.ui.dashboard.DashboardFragment;
import com.damian.criptoutils.ui.notifications.NotificationsFragment;
import com.damian.criptoutils.utilities.CalculadoraFecha;
import com.google.android.material.bottomnavigation.BottomNavigationView;


// MIS IMPORTS
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import org.json.JSONException;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import com.damian.criptoutils.ui.account.AccountFragment;
import com.damian.criptoutils.utilities.MySQLManager;
import com.damian.criptoutils.utilities.SQLiteManager;

import android.os.Handler;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;


    // MIS VARIABLES
    private SQLiteManager SQLiteBD;
    int segundosDelay = 30; // Segundos tras los cuales actualizar los precios

    // FIN MIS VARIABLES


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ////////////////////////////////////////////////////////////////////////////////////////////
        // CODIGO PLANTILLA
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        // FIN CODIGO PLANTILLA
        ////////////////////////////////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////////////////////////////////////
        // MI CODIGO
        //


        // Forzamos modo día
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        // Iniciamos la BD
        SQLiteBD = new SQLiteManager(this);
        SQLiteBD.open();


        // Ponemos el precio de Bitcoin en pantalla
        Log.e("SQLite", "Cargando precio desde la BDD y poniendolo en pantalla");
        // Si no existe registro de Precio, creamos uno nuevo
        if (SQLiteBD.selectPrecioBDD("Bitcoin") == " ") {
            Log.e("SQLite", "Creando registro para Bitcoin en BBDD");
            SQLiteBD.insert("Bitcoin", "Precio desconocido", "MarketCap desconocido", getString(R.string.descripcion_bitcoin));
        } else {
            Log.e("SQLite", "Ya existe registro en BBDD para Bitcoin");
        }
        TextView texto_precioBitcoin = (TextView) findViewById(R.id.texto_precioBitcoin);
        texto_precioBitcoin.setText("BTC: " + SQLiteBD.selectPrecioBDD("Bitcoin") + " €");


        // Repetir codigo cada X segundos
        final Handler handler = new Handler();
        int delay = 1000; // 1000 millisegundos (1 segundo)
        Log.e("Home", "Bucle de actualización cada " + segundosDelay + " segundos");
        handler.postDelayed(new Runnable() {
            public void run() {

                // TO-DO: Solo mostrar estos logs si estamos en la Home
                Log.e("LlamadaAPI", "Actualizando precio cada " + segundosDelay + " segundos");

                
                // Creamos Snackbar para indicar que se va a actualizar el precio
                Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Actualizando precios en línea", Snackbar.LENGTH_SHORT);
                /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

                //Solo mostramos Snackbar si estamos en la home
                View vistaDeFragmentoHome = findViewById(R.id.text_home); // Asignar fragment a variable
                if (vistaDeFragmentoHome != null && vistaDeFragmentoHome.getVisibility() == View.VISIBLE) {
                    snackbar.show();
                }

                // Actualizamos precio de las monedas para el resto de la aplicación
                llamarARetrofitYGuardarBDD();
                // Actualizamos Bitcoin
                // llamarAPIPrecioMonedaYBD("Bitcoin", true, false);

                handler.postDelayed(this, delay * segundosDelay); //Repetir cada X segundos (delay * segundosDelay)
            }
        }, delay);


        // Establecer dias que quedan hasta entrega
        TextView text_diasQuedan = (TextView) findViewById(R.id.text_diasQuedan);
        CalculadoraFecha CalculadoraFecha = new CalculadoraFecha();
        text_diasQuedan.setText(CalculadoraFecha.DevolverTiempoTexto());


        //
        // FIN DE MI CODIGO
        ////////////////////////////////////////////////////////////////////////////////////////////

    }


    public void botonActualizarPrecioBitcoinHome(View view) {

        Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Actualizando precio de Bitcoin en línea...", Snackbar.LENGTH_SHORT);
        /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

        //Solo mostramos Snackbar si estamos en la home
        View vistaDeFragmentoHome = findViewById(R.id.text_home); // Asignar fragment a variable
        if (vistaDeFragmentoHome != null && vistaDeFragmentoHome.getVisibility() == View.VISIBLE) {
            snackbar.show();
        }

        llamarAPIPrecioMonedaYBD("Bitcoin", true, false);

        // Ponemos nuevo precio de Bitcoin en la pantalla
        if (SQLiteBD.selectPrecioBDD("Bitcoin") == " ") {
            Log.e("SQLite", "Creando registro para Bitcoin en BBDD");
            SQLiteBD.insert("Bitcoin", "Precio desconocido", "MarketCap desconocido", getString(R.string.descripcion_bitcoin));
        } else {
            Log.e("SQLite", "Ya existe registro en BBDD para Bitcoin");
        }
        TextView texto_precioBitcoin = (TextView) findViewById(R.id.texto_precioBitcoin);
        texto_precioBitcoin.setText("BTC: " + SQLiteBD.selectPrecioBDD("Bitcoin") + " €");


    }


    // Este elemento obtiene el precio de la criptomoneda indicada, lo guarda en BD y devuelve el valor
    public void llamarAPIPrecioMonedaYBD(String moneda, boolean snackbar, boolean toast) {

        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + moneda + "&vs_currencies=eur";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    Log.e("LlamadaAPI", "Llamando a API: " + url);
                    Log.e("LlamadaAPI", "Respuesta API: " + response);

                    String respuestaAPIParseada = response.getJSONObject(moneda.toLowerCase()).getString("eur");

                    Log.e("LlamadaAPI", "Respuesta API: Parseada: " + respuestaAPIParseada);

                    Log.e("SQLite", "Guardando " + respuestaAPIParseada + " en la BD SQLite de Criptos");

                    SQLiteBD.actualizarPrecioCriptomoneda(moneda, respuestaAPIParseada);


                    Log.e("LlamadaAPI", "Guardado: '" + SQLiteBD.selectPrecioBDD("Bitcoin") + "' en la Base de Datos");

                    if (toast == true) {

                        //Solo mostramos Toast si estamos en la home
                        View vistaDeFragmentoHome = findViewById(R.id.text_home); // Asignar fragment a variable
                        if (vistaDeFragmentoHome != null && vistaDeFragmentoHome.getVisibility() == View.VISIBLE) {
                            Toast.makeText(getApplicationContext(), "Precio actualizado, precio de " + moneda + ": " + respuestaAPIParseada + " EUR", Toast.LENGTH_SHORT).show();
                        }

                    }
                    if (snackbar == true) {
                        Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Precio actualizado, precio de " + moneda + ": " + respuestaAPIParseada + " EUR", Snackbar.LENGTH_SHORT);
                        /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

                        //Solo mostramos Snackbar si estamos en la home
                        View vistaDeFragmentoHome = findViewById(R.id.text_home); // Asignar fragment a variable
                        if (vistaDeFragmentoHome != null && vistaDeFragmentoHome.getVisibility() == View.VISIBLE) {
                            snackbar.show();
                        }
                    }

                } catch (JSONException e) {
                    Log.e("LlamadaAPI", "Error al parsear u obtener JSON");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Se produjo un error al conectar con la API, comprueba tu internet", Snackbar.LENGTH_SHORT);
                /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));

                //Solo mostramos Snackbar si estamos en la home
                View vistaDeFragmentoHome = findViewById(R.id.text_home); // Asignar fragment a variable
                if (vistaDeFragmentoHome != null && vistaDeFragmentoHome.getVisibility() == View.VISIBLE) {
                    snackbar.show();
                }

                Log.e("LlamadaAPI", "Se produjo un error al conectar con la API, posiblemente no haya conexión a Internet");
            }
        });
        queue.add(jsonObjectRequest);
    }

    MySQLManager MySQLManager;
    Connection cone;
    ResultSet resultados;
    String resultadoQuery, str;

    public void hacerLoginMySQL() {

        // Sacado de este video que es GOD: https://www.youtube.com/watch?v=mbHvLSwhdLA
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {

                EditText formulario_email = (EditText) findViewById(R.id.formulario_email);
                String email = String.valueOf(formulario_email.getText());
                EditText formulario_contra = (EditText) findViewById(R.id.formulario_contra);
                String contrasena = String.valueOf(formulario_contra.getText());

                cone = MySQLManager.ConexionMySQL();
                if (cone == null) {
                    // Loggear y mostrar toast de que no hay conexión a internet
                    Log.e("MySQL", "Error al conectar a MySQL");

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No se puede conectar con la Base de datos, comprueba tu internet", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    // Loggear conexión realizada a la base de datos
                    Log.e("MySQL", "Conectado a MySQL");
                }

//                String query = "SELECT * FROM usuarios WHERE email = 'damian@gmail.com'";
                String query = "SELECT * FROM usuarios WHERE email = '" + email + "' AND contraseña = '" + contrasena + "'";

                Log.e("MySQL", "Lanzada query a la BD con Email: <" + email + "> y Contraseña: <" + contrasena + ">");
                Log.e("MySQL", "Lanzada query a la BD: " + query);

                PreparedStatement statement = cone.prepareStatement(query);
                ResultSet resultados = statement.executeQuery();

                StringBuilder StringResultado = new StringBuilder();

                while (resultados.next()) {
//                    StringResultado.append(resultados.getString("username")).append("\n");
                    StringResultado.append(resultados.getString("email"));
                }

                resultadoQuery = StringResultado.toString();

                Log.e("MySQL", "Obtenido de la BD: <" + resultadoQuery + ">");

                if (String.valueOf(resultadoQuery).equals(email)) {

                    // Mostrar snackbar de exito en el login
                    Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Iniciada sesión exitosamente", Snackbar.LENGTH_SHORT);
                    /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    snackbar.show();

                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout accountLayout = (LinearLayout) findViewById(R.id.account_layout_principal);
                            accountLayout.setVisibility(View.GONE);
                            LinearLayout accountLoggeadoLayout = (LinearLayout) findViewById(R.id.loggeado_account_layout_principal);
                            accountLoggeadoLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    AccountFragment.loggeado = true;

                    // Establecemos el nombre de usuario en variable estatica para cargar en nube
                    NotificationsFragment.loggeadoEmail = email;

                    // Cargando elementos del perfil a la pantalla
                    // Nombre completo
                    StringResultado = new StringBuilder();

                    resultados.beforeFirst();
                    while (resultados.next()) {
                        StringResultado.append(resultados.getString("nombre"));
                    }

                    resultadoQuery = StringResultado.toString();

                    Log.e("MySQL", "Obtenido nombre de la BD: <" + resultadoQuery + ">, asignando...");

                    TextView loggeado_formulario_nombreCompleto = findViewById(R.id.loggeado_formulario_nombreCompleto);
                    loggeado_formulario_nombreCompleto.setText(resultadoQuery);
                    //
                    // Nombre de usuario
                    StringResultado = new StringBuilder();

                    resultados.beforeFirst();
                    while (resultados.next()) {
                        StringResultado.append(resultados.getString("username"));
                    }

                    resultadoQuery = StringResultado.toString();

                    Log.e("MySQL", "Obtenido nombre de usuario de la BD: <" + resultadoQuery + ">, asignando...");

                    TextView loggeado_formulario_nombreUsuario = findViewById(R.id.loggeado_formulario_nombreUsuario);
                    loggeado_formulario_nombreUsuario.setText(resultadoQuery);
                    //
                    // Email
                    StringResultado = new StringBuilder();

                    resultados.beforeFirst();
                    while (resultados.next()) {
                        StringResultado.append(resultados.getString("email"));
                    }

                    resultadoQuery = StringResultado.toString();

                    Log.e("MySQL", "Obtenido email de la BD: <" + resultadoQuery + ">, asignando...");

                    TextView loggeado_formulario_email = findViewById(R.id.loggeado_formulario_email);
                    loggeado_formulario_email.setText(resultadoQuery);
                    //
                    // Contraseña
                    StringResultado = new StringBuilder();

                    resultados.beforeFirst();
                    while (resultados.next()) {
                        StringResultado.append(resultados.getString("contraseña"));
                    }

                    resultadoQuery = StringResultado.toString();

                    Log.e("MySQL", "Obtenida contraseña de la BD: <" + resultadoQuery + ">, asignando...");

                    TextView loggeado_formulario_password = findViewById(R.id.loggeado_formulario_password);
                    loggeado_formulario_password.setText(resultadoQuery);
                    //

                    statement.close();
                    resultados.close();
                    cone.close();
                }

                // Si no existe esa cuenta en la BBDD
                else if (!String.valueOf(resultadoQuery).equals(email)) {

                    // Mostrar snackbar de fallo en el login
                    Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "No se pudo iniciar sesión, comprueba los datos introducidos", Snackbar.LENGTH_SHORT);
                    /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    snackbar.show();
                }

            } catch (Exception e) {
                Log.e("MySQL",Log.getStackTraceString(e));
            }
        });

    }

    public void registrarUsuarioMySQL() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {

                EditText formulario_email = (EditText) findViewById(R.id.formulario_email);
                String email = String.valueOf(formulario_email.getText());
                EditText formulario_contra = (EditText) findViewById(R.id.formulario_contra);
                String contrasena = String.valueOf(formulario_contra.getText());

                cone = MySQLManager.ConexionMySQL();
                if (cone == null) {
                    // Loggear y mostrar toast de que no hay conexión a internet
                    Log.e("MySQL", "Error al conectar a MySQL");

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No se puede conectar con la Base de datos, comprueba tu internet", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    // Loggear conexión realizada a la base de datos
                    Log.e("MySQL", "Conectado a MySQL");
                }

//                String query = "SELECT * FROM usuarios WHERE email = 'damian@gmail.com'";
                String query = "INSERT INTO `usuarios` (`email`, `contraseña`) VALUES ('" + email + "', '" + contrasena + "')";

                Log.e("MySQL", "Lanzada query a la BD con Email: <" + email + "> y Contraseña: <" + contrasena + ">");
                Log.e("MySQL", "Lanzada query a la BD: " + query);

                // Esto tiene que ser execute o executeUpdate
                PreparedStatement statement = cone.prepareStatement(query);
                int resultados = statement.executeUpdate();

                StringBuilder StringResultado = new StringBuilder();

                resultadoQuery = String.valueOf(resultados);

                Log.e("MySQL", "Obtenido de la BD: <" + resultadoQuery + "> (Lo normal es que sea 1)");

                statement.close();
                cone.close();

                Log.e("MySQL", "Creado usuario");
                Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Nueva cuenta/usuario creado con éxito", Snackbar.LENGTH_SHORT);
                /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                snackbar.show();

                // Usamos el metodo anterior para cargar los datos en pantalla del usuario recien registrado (un poco cutre pero bué)
                hacerLoginMySQL();

                // Llevar a pagina de login
                this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        LinearLayout accountLayout = (LinearLayout) findViewById(R.id.account_layout_principal);
                        accountLayout.setVisibility(View.GONE);
                        LinearLayout accountLoggeadoLayout = (LinearLayout) findViewById(R.id.loggeado_account_layout_principal);
                        accountLoggeadoLayout.setVisibility(View.VISIBLE);
                    } });
                AccountFragment.loggeado = true;

            } catch (SQLException e) {
                Log.e("MySQL", "No se pudo crear el usuario, este correo ya existe");
                Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "No se pudo crear nuevo usuario, este correo ya existe", Snackbar.LENGTH_SHORT);
                /* Aplicar margen inferior de 50dp */ snackbar.getView().setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                snackbar.show();
                Log.e("MySQL",Log.getStackTraceString(e));
            } catch (Exception e) {
                Log.e("MySQL",Log.getStackTraceString(e));
            }

            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });

        });

    }

    public void botonIniciarSesion(View view) {

        MySQLManager = new MySQLManager();
        hacerLoginMySQL();

        // Si recordar usuario está activo, guardamos de nuevo
        SQLiteManager dbManager = new SQLiteManager(this);
        dbManager.open();
        Switch switch_recordarContra = findViewById(R.id.switch_recordarContra);
        TextView formulario_email = findViewById(R.id.formulario_email);
        TextView formulario_contra = findViewById(R.id.formulario_contra);
        if (switch_recordarContra.isChecked()) {
            // Llamar a metodo guardar usuario
            dbManager.olvidarUsuarioBDD();
            dbManager.recordarUsuarioBDD(formulario_email.getText().toString(), formulario_contra.getText().toString());
            Log.e("Login", "Se guarda email y contraseña");
        } else {
            // Llamar a metodo eliminar usuarios guardados
            dbManager.olvidarUsuarioBDD();
            Log.e("Login", "Se borra email y contraseña");
        }

    }

    public void botonRegistrarUsuario(View view) {

        MySQLManager = new MySQLManager();
        registrarUsuarioMySQL();

        // Si recordar usuario está activo, guardamos de nuevo
        SQLiteManager dbManager = new SQLiteManager(this);
        dbManager.open();
        Switch switch_recordarContra = findViewById(R.id.switch_recordarContra);
        TextView formulario_email = findViewById(R.id.formulario_email);
        TextView formulario_contra = findViewById(R.id.formulario_contra);
        if (switch_recordarContra.isChecked()) {
            // Llamar a metodo guardar usuario
            dbManager.olvidarUsuarioBDD();
            dbManager.recordarUsuarioBDD(formulario_email.getText().toString(), formulario_contra.getText().toString());
            Log.e("Login", "Se guarda email y contraseña");
        } else {
            // Llamar a metodo eliminar usuarios guardados
            dbManager.olvidarUsuarioBDD();
            Log.e("Login", "Se borra email y contraseña");
        }

    }

    public void llamarARetrofitYGuardarBDD() {

        Log.e("llamarARetrofitYGuardarBDD", "Llamando a API para cargar lista de criptos");

        Call<List<Criptomoneda>> call = RetrofitLlamadaAPIListaCripto.getClient().create(LlamadaAPIListaCripto.class).getCriptos();

        call.enqueue(new Callback<List<Criptomoneda>>() {
            @Override
            public void onResponse(Call<List<Criptomoneda>> call, retrofit2.Response<List<Criptomoneda>> response) {

                if (response.isSuccessful()) {
                    Log.i("llamarARetrofitYGuardarBDD", "Respuesta recibida de API: " + response.body());

                    // Guardamos info de las monedas en SQLite
                    for (Criptomoneda cripto : response.body()) {
                        // Iniciamos la BD
                        SQLiteBD = new SQLiteManager(getApplication());
                        SQLiteBD.open();
                        // Si no existe registro de Precio, creamos uno nuevo
                        if (SQLiteBD.selectPrecioBDD(cripto.getNombre()) == " ") {
                            Log.i("SQLite", "Retrofit: Creando registro SQLite para: " + cripto.getNombre() + " en BBDD");
                            SQLiteBD.insert(cripto.getNombre(), String.valueOf(cripto.getPrecio()), String.valueOf(cripto.getMarketCap()), "Descripcion", cripto.getIcono());
                        } else {
                            SQLiteBD.actualizarCriptomoneda(cripto.getNombre(), String.valueOf(cripto.getPrecio()), String.valueOf(cripto.getMarketCap()), "Descripcion", cripto.getIcono());
                            Log.i("SQLite", "Retrofit: Ya existe registro en SQLite BDD para: " + cripto.getNombre());
                        }
                    }


                }
                else {
                    Log.e("llamarARetrofitYGuardarBDD", "Error de conexión o de obtención de respuesta de la API metodo (onResponse > else)");
                    Log.e("llamarARetrofitYGuardarBDD", "Respuesta recibida de API: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Criptomoneda>> call, Throwable t) {
                Log.e("llamarARetrofitYGuardarBDD", "Error de conexión o de obtención de respuesta de la API metodo (OnFailure)");
                Log.e("llamarARetrofitYGuardarBDD", t.toString());

            }
        });
    }
}