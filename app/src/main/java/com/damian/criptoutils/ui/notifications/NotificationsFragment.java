package com.damian.criptoutils.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.damian.criptoutils.R;
import com.damian.criptoutils.criptorecyclerapi.Criptomoneda;
import com.damian.criptoutils.criptorecyclerapi.LlamadaAPIListaCripto;
import com.damian.criptoutils.criptorecyclerapi.RetrofitLlamadaAPIListaCripto;
import com.damian.criptoutils.databinding.FragmentNotificationsBinding;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    // Mis variables para el Spinner
    private Spinner spinnerListaCriptos;
    private ArrayList<String> listaCriptoSpinner = new ArrayList<>();
    private ArrayAdapter<String> adapterSpinnerListaCriptos;
    RequestQueue requestQueue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Hecho con este videazo: https://www.youtube.com/watch?v=L1wVdGRnGEg
        requestQueue = Volley.newRequestQueue(getActivity());
        spinnerListaCriptos = view.findViewById(R.id.spinnerListaCriptos);
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=eur&x_cg_demo_api_key=CG-SzhFJQ3Nbdqhxh8DMr7x7zDm";

        Log.e("SpinnerCargarListaCriptos", "Llamando a API para cargar lista de criptos");
        Log.e("SpinnerCargarListaCriptos", "URL: " + url);

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
//                    JSONArray jsonArray = response.getJSONArray("name");
//                    JSONArray jsonArray = response.getJSONArray(null);
                    Log.e("SpinnerCargarListaCriptos", "Parseando" + response);
                    for (int i=0; i<response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String nombreCriptomoneda = jsonObject.optString("name");
                        listaCriptoSpinner.add(nombreCriptomoneda);
                        adapterSpinnerListaCriptos = new ArrayAdapter<>(getActivity(),
                                android.R.layout.simple_spinner_item, listaCriptoSpinner);
                        spinnerListaCriptos.setAdapter(adapterSpinnerListaCriptos);
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
            }
        });
        requestQueue.add(getRequest);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}