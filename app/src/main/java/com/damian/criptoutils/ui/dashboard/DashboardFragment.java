package com.damian.criptoutils.ui.dashboard;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damian.criptoutils.R;
import com.damian.criptoutils.criptorecyclerapi.Criptomoneda;
import com.damian.criptoutils.criptorecyclerapi.ListaCriptoAdapter;
import com.damian.criptoutils.criptorecyclerapi.LlamadaAPIListaCripto;
import com.damian.criptoutils.criptorecyclerapi.RetrofitLlamadaAPIListaCripto;
import com.damian.criptoutils.databinding.FragmentDashboardBinding;
import com.damian.criptoutils.utilities.SQLiteManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DashboardFragment extends Fragment implements ListaCriptoAdapter.OnItemClickListener {

    private FragmentDashboardBinding binding;

    // Mis variable para el Recycler
    private List<Criptomoneda> criptomonedaList;
    private RecyclerView recyclerView;
    private ListaCriptoAdapter listaCriptoAdapter;

    // Variables para SQLite
    private SQLiteManager SQLiteBD;

    // Mis variables para el Webview detalle de criptos
    private WebView webView;
    private String UrlDetalleCripto = "about:blank";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ////////////////////////////////////////////////////////////////////////////////////////////
        // MI CODIGO OnCreate
        //

        // Asignamos el webview
        webView = binding.webviewDetalleCripto;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Inyectar CSS personalizado
                String customCSS = "#__next .ButtonSwitcher.trading-view-button,#__next .coin-stats section:first-child,#__next .coin-stats>div>section:nth-child(2)>div>div:not(:first-child),#cdp-global-nav-wrapper,#onetrust-consent-sdk,[data-module-name=Coin-stats]>div>div>div:nth-child(4),body>div:nth-child(8)>div:nth-child(6)>div,div.btn-group,div.content_body>a,div[cmc-ui-portal] div[class*=sc-ce],div[data-role=global-header],footer,li[data-index=tab-5],li[data-index=tab-6]{display:none!important}[data-hydration-on-demand=true] *{pointer-events:none!important}[data-module-name=Coin-stats],[data-module-name=Coin-stats]>div>div>div:nth-child(4),div[class*=Modal_dialog-body-wrapper]{display:unset!important}[data-module-name=Coin-stats]>div[class]:nth-child(2){display:revert!important}[data-module-name]{display:none}#__next .coin-stats>div>section:nth-child(2)>div>button{visibility:hidden!important;pointer-events:none!important}";
                String javascript = "javascript:(function() {" +
                        "var style = document.createElement('style');" +
                        "style.innerHTML = '" + customCSS + "';" +
                        "document.head.appendChild(style);" +
                        "})()";

                webView.loadUrl(javascript);
            }
        });

        // Cargar la URL específica
        webView.loadUrl(UrlDetalleCripto);

        //
        // FIN DE MI CODIGO OnCreate
        ////////////////////////////////////////////////////////////////////////////////////////////

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.recyclerCriptosLista);
        cargarListaCriptosRecyclerAPI();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llm);
        listaCriptoAdapter = new ListaCriptoAdapter(criptomonedaList, getActivity());
        // Añadimos el listener del click
        listaCriptoAdapter.setOnItemClickListener(this); // Set the item click listener
        recyclerView.setAdapter(listaCriptoAdapter);


        // Mostrar icono de Cargando
        LinearLayout layoutCargando = binding.layoutCargando;
        layoutCargando.setVisibility(View.VISIBLE);
        // Ocultar icono de NoInternet
        LinearLayout layoutNoInternet = binding.layoutNoInternet;
        layoutNoInternet.setVisibility(View.GONE);


    }

    public void cargarListaCriptosRecyclerAPI() {

        // Creamos una referencia al DashboardFragment
        DashboardFragment fragment = this;

        Log.e("RecyclerViewCargarListaCriptos", "Llamando a API para cargar lista de criptos");

        Call<List<Criptomoneda>> call = RetrofitLlamadaAPIListaCripto.getClient().create(LlamadaAPIListaCripto.class).getCriptos();

        call.enqueue(new Callback<List<Criptomoneda>>() {
            @Override
            public void onResponse(Call<List<Criptomoneda>> call, retrofit2.Response<List<Criptomoneda>> response) {

                if (response.isSuccessful()) {
                    Log.i("RecyclerViewCargarListaCriptos", "Respuesta recibida de API: " + response.body());

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
                    listaCriptoAdapter = new ListaCriptoAdapter(criptomonedaList, getActivity());
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(llm);
                    // Añadimos el listener del click
                    listaCriptoAdapter.setOnItemClickListener(fragment); // Set the item click listener

                    // Cargamos adapter
                    recyclerView.setAdapter(listaCriptoAdapter);

                    // Guardamos info de las monedas en SQLite
                    for (Criptomoneda cripto : criptomonedaList) {
                        // Iniciamos la BD
                        SQLiteBD = new SQLiteManager(getContext());
                        SQLiteBD.open();
                        // Si no existe registro de Precio, creamos uno nuevo
                        if (SQLiteBD.selectPrecioBDD(cripto.getNombre()) == " ") {
                            Log.i("SQLite", "Retrofit/RecyclerView: Creando registro SQLite para: " + cripto.getNombre() + " en BBDD");
                            SQLiteBD.insert(cripto.getNombre(), String.valueOf(cripto.getPrecio()), String.valueOf(cripto.getMarketCap()), "Descripcion", cripto.getIcono());
                        } else {
                            SQLiteBD.actualizarCriptomoneda(cripto.getNombre(), String.valueOf(cripto.getPrecio()), String.valueOf(cripto.getMarketCap()), "Descripcion", cripto.getIcono());
                            Log.i("SQLite", "Retrofit/RecyclerView: Ya existe registro en SQLite BDD para: " + cripto.getNombre());
                        }
                    }


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

    // Metodo para crear nueva pantalla desde el recyclerview
    @Override
    public void onItemClick(String id, int position) {

//      TODO: OPCIONAL: Dar animacion de clic a elemento del recycler (como :hover de CSS o algo asi)
        // Animamos el elemento para reflejar la pulsación
//        ObjectAnimator animacion = ObjectAnimator.ofFloat(recyclerView.findViewHolderForAdapterPosition(position), "alpha", 1f, 1.1f, 1f);
//        animacion.setDuration(500); // Duración de la animación en milisegundos
//        animacion.start();

        // Codigo que ocurre al clicar sobre un elemento del RecyclerView
        // Loggeamos posicion clicada
        Log.e("RecyclerViewCargarListaCriptos", "Tocado recylerItem con ID: " + id + ", numero: " + position);
        // Cargamos URL para el detalle
        corregirURLsDetalleCripto(id);
//        UrlDetalleCripto = "https://coinmarketcap.com/es/currencies/" + id + "/";
        webView.loadUrl(UrlDetalleCripto);
        // Mostrar Layout detalle
        LinearLayout layoutWebviewDetalleCripto = binding.layoutWebviewDetalleCripto;
        layoutWebviewDetalleCripto.setVisibility(View.VISIBLE);
        // Ocultar icono de NoInternet
        LinearLayout layoutRecyclerMercado = binding.layoutRecyclerMercado;
        layoutRecyclerMercado.setVisibility(View.GONE);
    }

    // Mis metodos para el Webview detalle de criptos


    private void corregirURLsDetalleCripto(String id) {
        if (id.equals("binancecoin")) {
            UrlDetalleCripto = "https://coinmarketcap.com/es/currencies/" + "bnb" + "/";
        } else if (id.equals("staked-ether")) {
            UrlDetalleCripto = "https://coinmarketcap.com/es/currencies/" + "steth" + "/";
        } else if (id.equals("the-open-network")) {
            UrlDetalleCripto = "https://coinmarketcap.com/es/currencies/" + "toncoin" + "/";
        } else if (id.equals("wrapped-steth")) {
            UrlDetalleCripto = "https://coinmarketcap.com/es/currencies/" + "lido-finance-wsteth" + "/";
        } else if (id.equals("leo-token")) {
            UrlDetalleCripto = "https://coinmarketcap.com/es/currencies/" + "unus-sed-leo" + "/";
        }
        else {
            UrlDetalleCripto = "https://coinmarketcap.com/es/currencies/" + id + "/";
        }
    }
}