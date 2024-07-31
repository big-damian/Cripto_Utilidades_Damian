package com.damian.criptoutils.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

public class ApiHandlerJSON {

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private Context contextoActivity;
    private final String urlPredet = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=eur";
    // Variable para guardar respuestaAPI
    public String respuestaAPI;

    public String llamadaGET(String url, Context contextoActivity) {

        // Iniciando cola de request (RequestQueue initialized)
        mRequestQueue = Volley.newRequestQueue(contextoActivity);

        // Iniciando request tipo String (String Request initialized)
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String respuesta) {

                Log.i("llamadaAPI", "Respuesta API: " + respuesta.toString());

                respuestaAPI = respuesta.toString();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("llamadaAPI", "Error al llamar API:" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);

        return respuestaAPI;
    }

    public String llamadaGETtoast(String url, Context contextoActivity) {

        // Iniciando cola de request (RequestQueue initialized)
        mRequestQueue = Volley.newRequestQueue(contextoActivity);

        // Iniciando request tipo String (String Request initialized)
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String respuesta) {

                Log.i("llamadaAPI", "Respuesta API (+Toast): " + respuesta.toString());

                respuestaAPI = respuesta.toString();

                Toast.makeText(contextoActivity, respuestaAPI, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("llamadaAPI", "Error al llamar API (+Toast):" + error.toString());

                Toast.makeText(contextoActivity, "Error al conectar con la API", Toast.LENGTH_SHORT).show();
            }
        });

        mRequestQueue.add(mStringRequest);

        return respuestaAPI;
    }

    public String llamadaPrecioBitcoinToast(Context contextoActivity) {

        String url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=eur";

        // Iniciando cola de request (RequestQueue initialized)
        mRequestQueue = Volley.newRequestQueue(contextoActivity);

        // Iniciando request tipo JSON (JSON Request initialized)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject respuesta) {

                Log.i("llamadaAPI", "Respuesta API (+Toast): " + respuesta.toString());

                try {

                    String precioBitcoin = respuesta.getString("eur");

                    respuestaAPI = precioBitcoin.toString();

                    Log.i("llamadaAPI", "Respuesta API parseada (Eur): " + precioBitcoin.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(contextoActivity, "El precio actualizado de Bitcoin es: " + respuestaAPI, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("llamadaAPI", "Error al llamar API (+Toast):" + error.toString());

                Toast.makeText(contextoActivity, "Error al conectar con la API", Toast.LENGTH_SHORT).show();
            }
        });

        mRequestQueue.add(mStringRequest);

        return respuestaAPI;
    }

    public String llamadaPrecioMonedaActualizar(String moneda, Context contextoActivity) {

        String url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=" + moneda;

        // Iniciando cola de request (RequestQueue initialized)
        mRequestQueue = Volley.newRequestQueue(contextoActivity);

        // Iniciando request tipo JSON (JSON Request initialized)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject respuesta) {

                Log.i("llamadaAPI", "Respuesta API (+Toast): " + respuesta.toString());

                try {

                    String precioBitcoin = respuesta.getString("eur");

                    respuestaAPI = precioBitcoin.toString();

//                    //TODO: Esta mierda hay que elimnarla, en general limpiar toda esta clase
//                    MainActivity.actualizarRecibidoAPI(respuestaAPI);

                    Log.i("llamadaAPI", "Respuesta API parseada (Eur): " + precioBitcoin.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("llamadaAPI", "Error al llamar API (+Toast):" + error.toString());

                Toast.makeText(contextoActivity, "Error al conectar con la API", Toast.LENGTH_SHORT).show();
            }
        });

        mRequestQueue.add(mStringRequest);

        return respuestaAPI;
    }

    public String llamadaPrecioMonedaActualizarToast(String moneda, Context contextoActivity) {

        String url = "https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=" + moneda;

        // Iniciando cola de request (RequestQueue initialized)
        mRequestQueue = Volley.newRequestQueue(contextoActivity);

        // Iniciando request tipo JSON (JSON Request initialized)
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject respuesta) {

                Log.i("llamadaAPI", "Respuesta API (+Toast): " + respuesta.toString());

                try {

                    String precioBitcoin = respuesta.getString("eur");

                    respuestaAPI = precioBitcoin.toString();

//                    //TODO: Esta mierda hay que elimnarla, en general limpiar toda esta clase
//                    MainActivity.actualizarRecibidoAPI(respuestaAPI);

                    Toast.makeText(contextoActivity, "El precio actualizado de " + moneda.toUpperCase() + " es: " + respuestaAPI, Toast.LENGTH_SHORT).show();

                    Log.i("llamadaAPI", "Respuesta API parseada (Eur): " + precioBitcoin.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("llamadaAPI", "Error al llamar API (+Toast):" + error.toString());

                Toast.makeText(contextoActivity, "Error al conectar con la API", Toast.LENGTH_SHORT).show();
            }
        });

        mRequestQueue.add(mStringRequest);

        return respuestaAPI;
    }

    public ApiHandlerJSON() {

    }
}

//        Como llamar desde el MainActivity
//        ApiHandler.llamadaGET("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=eur", this);
//        ApiHandler.llamadaGETtoast("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=eur", this);