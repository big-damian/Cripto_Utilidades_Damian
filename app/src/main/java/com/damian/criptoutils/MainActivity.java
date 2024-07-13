package com.damian.criptoutils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
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
import androidx.recyclerview.widget.RecyclerView;

import com.damian.criptoutils.criptorecyclerapi.Criptomoneda;
import com.damian.criptoutils.criptorecyclerapi.ListaCriptoAdapter;
import com.damian.criptoutils.criptorecyclerapi.LlamadaAPIListaCripto;
import com.damian.criptoutils.criptorecyclerapi.RetrofitLlamadaAPIListaCripto;
import com.damian.criptoutils.databinding.ActivityMainBinding;
import com.damian.criptoutils.utilities.CalculadoraFecha;
import com.google.android.material.bottomnavigation.BottomNavigationView;


// MIS IMPORTS
import android.widget.EditText;
import android.widget.LinearLayout;
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
    int segundosDelay = 15; // Segungos tras los cuales actualizar

    // TODO Borrar esto si no sirve
    private List<Criptomoneda> criptomonedaList;
    private RecyclerView recyclerView;
    private ListaCriptoAdapter listaCriptoAdapter;
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


                Log.e("LlamadaAPI", "Actualizando precio cada " + segundosDelay + " segundos");
//                llamarAPIActualizarPrecioMoneda("Bitcoin", R.id.texto_precioBitcoin, true, false);


                // Creamos Snackbar para indicar que se va a actualizar el precio
                Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Actualizando precios en línea", Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                snackbar.show();


                llamarAPIPrecioMonedaYBD("Bitcoin", true, false);


                handler.postDelayed(this, delay * segundosDelay); //Repetir cada 30 segundos (delay * 30)
            }
        }, delay);


        // Establecer dais que quedan hasta entrega
        TextView text_diasQuedan = (TextView) findViewById(R.id.text_diasQuedan);
        CalculadoraFecha CalculadoraFecha = new CalculadoraFecha();
        text_diasQuedan.setText(CalculadoraFecha.DevolverTiempoTexto());


        // Añadir RecyclerView y cargar lista de precios
        // TODO Borrar esto si no sirve
//        recyclerView = findViewById(R.id.recyclerCriptosLista);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        cargarListaCriptosRecyclerAPI();


        //
        // FIN DE MI CODIGO
        ////////////////////////////////////////////////////////////////////////////////////////////

    }


    public void botonActualizarPrecioBitcoinHome(View view) {

        Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Actualizando precio de Bitcoin en línea...", Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        snackbar.show();

        llamarAPIPrecioMonedaYBD("Ethereum", true, false);

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

//                  Log.e("LlamadaAPI", "Guardando " + response.getJSONObject(moneda.toLowerCase()).getString("eur") + " en la Base de Datos");
                    Log.e("SQLite", "Guardando " + respuestaAPIParseada + " en la BD SQLite de Criptos");

                    // .TODO:  ESO DE NOMBRE HABRIA QUE CAMBIARLO POR 'Bitcoin'
                    SQLiteBD.actualizarPrecio("Nombre", respuestaAPIParseada);

                    Log.e("LlamadaAPI", "Guardado: '" + SQLiteBD.selectPrecioBDD("Bitcoin") + "' en la Base de Datos");

                    // Creo que este try-catch no es necesario
                    try {
                        TextView texto_precioBitcoin = (TextView) findViewById(R.id.texto_precioBitcoin);
                        texto_precioBitcoin.setText("BTC: " + SQLiteBD.selectPrecioBDD("Bitcoin") + " €");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    if (toast == true) {
                        //Toast.makeText(getApplicationContext(), "El precio de Bitcoin es: " + respuestaAPIParseada + " EUR", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Precio actualizado, precio de " + moneda + ": " + respuestaAPIParseada + " EUR", Toast.LENGTH_SHORT).show();
                    }
                    if (snackbar == true) {
                        // LinearLayout layout_Snackbar;
                        // layout_Snackbar = (LinearLayout) findViewById(R.id.layout_Snackbar);
                        Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Precio actualizado, precio de " + moneda + ": " + respuestaAPIParseada + " EUR", Snackbar.LENGTH_SHORT);
                        View snackBarView = snackbar.getView();
                        /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                        snackbar.show();
                    }

                } catch (JSONException e) {
                    Log.e("LlamadaAPI", "Error al parsear u obtener JSON");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), "Se produjo un error al conectar con la API, comprueba tu internet", Toast.LENGTH_SHORT).show();

                Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Se produjo un error al conectar con la API, comprueba tu internet", Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                snackbar.show();

                Log.e("LlamadaAPI", "Se produjo un error al conectar con la API, posiblemente no haya conexión a Internet");
            }
        });
        queue.add(jsonObjectRequest);
    }












//            Statement statement;
//            statement = connection.createStatement();
//            ResultSet resultSet;
//            resultSet = statement.executeQuery(
//                    "select * from usuarios");
//            int code;
//            String title;
//            while (resultSet.next()) {
//                code = resultSet.getInt("code");
//                title = resultSet.getString("title").trim();
//                Log.e("MySQL", "Code : " + code + " Title : " + title);
//            }
//            resultSet.close();
//            statement.close();
//            connection.close();







    MySQLManager MySQLManager;
    Connection cone;
    ResultSet resultados;
    String resultadoQuery, str;

    // Sacado de este video que es GOD: https://www.youtube.com/watch?v=mbHvLSwhdLA
//    public void ConectarMySQL() {
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        executorService.execute(() -> {
//            try {
//                cone = MySQLManager.ConexionMySQL();
//                if (cone == null) {
//                    str = "Error al conectar a MySQL";
//                } else {
//                    str = "Conectado a MySQL";
//                }
//
//            } catch(Exception e) {
//                throw new RuntimeException(e);
//            }
//
//            runOnUiThread(() -> {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
//            });
//
//        });
//    }

    public void hacerLoginMySQL() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {

                EditText formulario_email = (EditText) findViewById(R.id.formulario_email);
                String email = String.valueOf(formulario_email.getText());
                EditText formulario_contra = (EditText) findViewById(R.id.formulario_contra);
                String contrasena = String.valueOf(formulario_contra.getText());

                cone = MySQLManager.ConexionMySQL();
                if (cone == null) {
                    str = "Error al conectar a MySQL";
                    Log.e("MySQL", "Error al conectar a MySQL");
                } else {
                    str = "Conectado a MySQL";
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
                    // TODO: Arreglar/Eliminar estos Toast
                    Log.e("MySQL", "Se loggea");
//                    Toast.makeText(this, "Se loggea con exito", Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Iniciada sesión exitosamente", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    snackbar.show();
                    // TODO: Llevar a pagina de login


                    this.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            LinearLayout accountLayout = (LinearLayout) findViewById(R.id.account_layout_principal);
                            accountLayout.setVisibility(View.GONE);
                            LinearLayout accountLoggeadoLayout = (LinearLayout) findViewById(R.id.loggeado_account_layout_principal);
                            accountLoggeadoLayout.setVisibility(View.VISIBLE);
                        } });
                    AccountFragment.loggeado = true;




                } else {
                    Log.e("MySQL", "No se loggea");
//                    Toast.makeText(this, "Login incorrecto", Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "Error al iniciar sesión, compruebe los datos introducidos", Snackbar.LENGTH_SHORT);
                    View snackBarView = snackbar.getView();
                    /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                    snackbar.show();
                }

                statement.close();
                resultados.close();
                cone.close();

            } catch(Exception e) {
                Log.e("MySQL",Log.getStackTraceString(e));
            }

            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                Toast.makeText(this, resultadoQuery, Toast.LENGTH_SHORT).show();
//                TextView texto_precioBitcoin = (TextView) findViewById(R.id.texto_precioBitcoin);
//                texto_precioBitcoin.setText("BTC: " + SQLiteBD.selectPrecioBDD("Bitcoin") + " €");


            });

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
                    str = "Error al conectar a MySQL";
                    Log.e("MySQL", "Error al conectar a MySQL");
                } else {
                    str = "Conectado a MySQL";
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
                View snackBarView = snackbar.getView();
                /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                snackbar.show();
                // TODO: Llevar a pagina de login
//                AccountFragment fragment = (AccountFragment) getFragmentManager().findFragmentById(getId(););
//                fragment.metodo();



                LinearLayout accountLayout = (LinearLayout) findViewById(R.id.account_layout_principal);
                accountLayout.setVisibility(View.GONE);

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
                View snackBarView = snackbar.getView();
                /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
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

                // TODO: Arreglar putos toasts
//                Toast.makeText(getApplicationContext(), resultadoQuery, Toast.LENGTH_SHORT).show();
//                TextView texto_precioBitcoin = (TextView) findViewById(R.id.texto_precioBitcoin);
//                texto_precioBitcoin.setText("BTC: " + SQLiteBD.selectPrecioBDD("Bitcoin") + " €");


            });

        });

    }

    public void botonIniciarSesion(View view) {

        MySQLManager = new MySQLManager();
        hacerLoginMySQL();

    }

    public void botonRegistrarUsuario(View view) {

        MySQLManager = new MySQLManager();
        registrarUsuarioMySQL();

    }

    public void botonCerrarSesion(View view) {

        AccountFragment.loggeado = false;

        this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                LinearLayout accountLoggeadoLayout = (LinearLayout) findViewById(R.id.loggeado_account_layout_principal);
                accountLoggeadoLayout.setVisibility(View.GONE);
                LinearLayout accountLayout = (LinearLayout) findViewById(R.id.account_layout_principal);
                accountLayout.setVisibility(View.VISIBLE);
            } });
        AccountFragment.loggeado = true;

    }

    public void cargarListaCriptosRecyclerAPI() {

        Log.e("RecyclerViewCargarListaCriptos", "Llamando a API para cargar lista de criptos");

        Call<List<Criptomoneda>> call = RetrofitLlamadaAPIListaCripto.getClient().create(LlamadaAPIListaCripto.class).getCriptos();

        call.enqueue(new Callback<List<Criptomoneda>>() {
            @Override
            public void onResponse(Call<List<Criptomoneda>> call, retrofit2.Response<List<Criptomoneda>> response) {

                if (response.isSuccessful()) {
                    Log.e("RecyclerViewCargarListaCriptos", "Respuesta recibida de API: " + response.body());
                    criptomonedaList = response.body();
                    listaCriptoAdapter = new ListaCriptoAdapter(criptomonedaList,getApplicationContext());
                    recyclerView.setAdapter(listaCriptoAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Criptomoneda>> call, Throwable t) {
                Log.e("RecyclerViewCargarListaCriptos", "Error de conexión o de obtención de respuesta de la API");
                Snackbar snackbar = Snackbar.make(binding.layoutSnackbar, "No se pudo obtener lista de criptomonedas, error de conexión", Snackbar.LENGTH_SHORT);
                View snackBarView = snackbar.getView();
                /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getApplication().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                snackbar.show();
            }
        });
    }




}