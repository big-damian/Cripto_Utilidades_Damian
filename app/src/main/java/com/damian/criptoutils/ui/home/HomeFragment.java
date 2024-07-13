package com.damian.criptoutils.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.damian.criptoutils.R;
import com.damian.criptoutils.databinding.FragmentHomeBinding;
import com.damian.criptoutils.utilities.SQLiteManager;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    // MIS VARIABLES
    private SQLiteManager SQLiteBD;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ////////////////////////////////////////////////////////////////////////////////////////////
        // MI CODIGO
        //


        // Iniciamos la BD
        SQLiteBD = new SQLiteManager(getContext());
        SQLiteBD.open();

        // TODO: Que se ponga en pantalla el precio de BTC desde la BD
        // Ponemos el precio de Bitcoin en pantalla
        Log.e("SQLite", "Cargando precio desde la BDD y poniendolo en pantalla");
        // Si no existe registro de Precio, creamos uno nuevo
        if (SQLiteBD.selectPrecioBDD("Bitcoin") == " ") {
            Log.e("SQLite", "Creando registro para Bitcoin en BBDD");
            SQLiteBD.insert("Bitcoin", "Precio desconocido", "MarketCap desconocido", getString(R.string.descripcion_bitcoin));
        } else {
            Log.e("SQLite", "Ya existe registro en BBDD para Bitcoin");
            binding.textoPrecioBitcoin.setText("BTC: " + SQLiteBD.selectPrecioBDD("Bitcoin") + " €");
        }

        //
        // FIN DE MI CODIGO
        ////////////////////////////////////////////////////////////////////////////////////////////

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}