package com.damian.criptoutils.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.module.AppGlideModule;

import com.damian.criptoutils.R;
import com.damian.criptoutils.databinding.FragmentHomeBinding;
import com.damian.criptoutils.utilities.CalculadoraFecha;
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

        // Establecer dias que quedan hasta entrega
        TextView text_diasQuedan = binding.textDiasQuedan;
        CalculadoraFecha CalculadoraFecha = new CalculadoraFecha();
        text_diasQuedan.setText(CalculadoraFecha.DevolverTiempoTexto());


        // Iniciamos la BD
        SQLiteBD = new SQLiteManager(getContext());
        SQLiteBD.open();

        // Ponemos el precio de Bitcoin en pantalla
        Log.e("SQLite", "Cargando precio desde la BDD y poniendolo en pantalla");
        // Si no existe registro de Precio, creamos uno nuevo
        if (SQLiteBD.selectPrecioBDD("Bitcoin") == " ") {
            Log.e("SQLite", "Creando registro para Bitcoin en BBDD");
            SQLiteBD.insert("Bitcoin", "Precio desconocido", "MarketCap desconocido1", getString(R.string.descripcion_bitcoin));
        } else {
            Log.e("SQLite", "Ya existe registro en BBDD para Bitcoin");
            binding.textoPrecioBitcoin.setText("BTC: " + SQLiteBD.selectPrecioBDD("Bitcoin") + " €");
        }

        // Gif en la pantalla home
        ImageView imageView = binding.gifMercadosCripto;
        Transformation<Bitmap> circleCrop = new CircleCrop();
        Glide.with(this)
                .load(R.drawable.gif_webp_mercados)
                .optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(circleCrop))
                .into(imageView);

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