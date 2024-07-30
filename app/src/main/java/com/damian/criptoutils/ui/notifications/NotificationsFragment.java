package com.damian.criptoutils.ui.notifications;

import android.app.Dialog;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.damian.criptoutils.R;
import com.damian.criptoutils.databinding.FragmentNotificationsBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Supongamos que abres el diálogo al hacer clic en un botón
        view.findViewById(R.id.botonAnadir).setOnClickListener(v -> showDialog(view));
    }

    // Hecho con este video: https://www.youtube.com/watch?v=L1wVdGRnGEg (y la ayuda de chatGPT para que deje asignar el adapter al spinner)
    // Ayuda para dialogs en este video: https://www.youtube.com/watch?v=WSOmYN8y0_k
    private void showDialog(View fragmentView) {

        requestQueue = Volley.newRequestQueue(getActivity());

        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_yourcripto);

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
                botonDialogoAgregarTusCriptos.setBackgroundColor(Color.RED);
                botonDialogoAgregarTusCriptos.setClickable(false);
            }
        });
        requestQueue.add(getRequest);

        // Configurar el botón Aceptar
        botonDialogoAgregarTusCriptos.setOnClickListener(v -> {
            String seleccionSpinner = (String) spinnerListaCriptos.getSelectedItem();
            if (!editTextNumberDecimal.getText().toString().isEmpty() && Double.parseDouble(editTextNumberDecimal.getText().toString()) > 0.0) {
                Snackbar snackbar = Snackbar.make(fragmentView, "Agregada criptomoneda " + seleccionSpinner + " = " + editTextNumberDecimal.getText() + " unidades", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                /* Aplicar margen inferior de 50dp */ snackBarView.setTranslationY(-50 * ((float) getActivity().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
                snackbar.show();

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
}