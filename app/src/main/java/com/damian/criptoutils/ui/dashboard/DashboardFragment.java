package com.damian.criptoutils.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damian.criptoutils.MainActivity;
import com.damian.criptoutils.R;
import com.damian.criptoutils.criptorecyclerapi.Criptomoneda;
import com.damian.criptoutils.criptorecyclerapi.ListaCriptoAdapter;
import com.damian.criptoutils.criptorecyclerapi.LlamadaAPIListaCripto;
import com.damian.criptoutils.criptorecyclerapi.RetrofitLlamadaAPIListaCripto;
import com.damian.criptoutils.databinding.FragmentDashboardBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    // Mi variable para el Recycler
    private List<Criptomoneda> criptomonedaList;
    private RecyclerView recyclerView;
    private ListaCriptoAdapter listaCriptoAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ///

//        Log.e("RecyclerViewCiprot", "String.valueOf(listaCriptoAdapter.getItemCount())");
//        Log.e("RecyclerViewCiprot", String.valueOf(listaCriptoAdapter.getItemCount()));
//        recyclerView = binding.recyclerCriptosLista;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        listaCriptoAdapter = new ListaCriptoAdapter(criptomonedaList,getActivity());
//        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setAdapter(listaCriptoAdapter);
//        cargarListaCriptosRecyclerAPI();

        ///

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Crear y cargar el RecyclerView
//        recyclerView = binding.recyclerCriptosLista;
//        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(llm);
//        recyclerView.setAdapter(listaCriptoAdapter);
////        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        cargarListaCriptosRecyclerAPI();




//        recyclerView = binding.recyclerCriptosLista;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        cargarListaCriptosRecyclerAPI();

//        CODIGO PREVIO AL ARREGLO INDIO https://www.youtube.com/watch?v=HGrFPWUCFNg
//        Log.e("RecyclerViewCripto", "String.valueOf(listaCriptoAdapter.getItemCount())");
//        Log.e("RecyclerViewCripto", String.valueOf(listaCriptoAdapter.getItemCount()));
//        recyclerView = binding.recyclerCriptosLista;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        listaCriptoAdapter = new ListaCriptoAdapter(criptomonedaList,getActivity());
//        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        recyclerView.setAdapter(listaCriptoAdapter);
//        cargarListaCriptosRecyclerAPI();

//        Log.e("RecyclerViewCripto", "String.valueOf(listaCriptoAdapter.getItemCount())");
//        Log.e("RecyclerViewCripto", String.valueOf(listaCriptoAdapter.getItemCount()));

        recyclerView = view.findViewById(R.id.recyclerCriptosLista);
        cargarListaCriptosRecyclerAPI();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        listaCriptoAdapter = new ListaCriptoAdapter(criptomonedaList,getActivity());
        recyclerView.setAdapter(listaCriptoAdapter);


        // Mostrar icono de Cargando
        LinearLayout layoutCargando = binding.layoutCargando;
        layoutCargando.setVisibility(View.VISIBLE);
        // Ocultar icono de NoInternet
        LinearLayout layoutNoInternet = binding.layoutNoInternet;
        layoutNoInternet.setVisibility(View.GONE);



    }

    public void cargarListaCriptosRecyclerAPI() {

        Log.e("RecyclerViewCargarListaCriptos", "Llamando a API para cargar lista de criptos");

        Call<List<Criptomoneda>> call = RetrofitLlamadaAPIListaCripto.getClient().create(LlamadaAPIListaCripto.class).getCriptos();

        call.enqueue(new Callback<List<Criptomoneda>>() {
            @Override
            public void onResponse(Call<List<Criptomoneda>> call, retrofit2.Response<List<Criptomoneda>> response) {

                if (response.isSuccessful()) {
                    Log.e("RecyclerViewCargarListaCriptos", "Respuesta recibida de API: " + response.body());

                    // Ocultar icono de Cargando
                    if (binding != null) {
                        LinearLayout layoutCargando = binding.layoutCargando;
                        layoutCargando.setVisibility(View.GONE);
                    }
                    // Ocultar icono de No internet
                    if (binding != null) {
                        LinearLayout layoutNoInternet = binding.layoutNoInternet;
                        layoutNoInternet.setVisibility(View.GONE);
                    }

                    criptomonedaList = response.body();
                    listaCriptoAdapter = new ListaCriptoAdapter(criptomonedaList,getActivity());
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(listaCriptoAdapter);


                }
                else {
                    Log.e("RecyclerViewCargarListaCriptos", "Error de conexión o de obtención de respuesta de la API metodo (onResponse > else)");
                    Log.e("RecyclerViewCargarListaCriptos", "Respuesta recibida de API: " + response.body());

                    LinearLayout layoutNoInternet = binding.layoutNoInternet;
                    layoutNoInternet.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Criptomoneda>> call, Throwable t) {
                Log.e("RecyclerViewCargarListaCriptos", "Error de conexión o de obtención de respuesta de la API metodo (OnFailure)");
                Log.e("RecyclerViewCargarListaCriptos", t.toString());

                // Ocultar icono de Cargando
                LinearLayout layoutCargando = binding.layoutCargando;
                layoutCargando.setVisibility(View.GONE);
                // Mostrar icono de No internet
                LinearLayout layoutNoInternet = binding.layoutNoInternet;
                layoutNoInternet.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}