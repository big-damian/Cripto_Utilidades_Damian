package com.damian.criptoutils.criptorecyclerapi;

import com.damian.criptoutils.criptorecyclerapi.Criptomoneda;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface LlamadaAPIListaCripto {

    @GET("markets?vs_currency=eur&x_cg_demo_api_key=CG-SzhFJQ3Nbdqhxh8DMr7x7zDm")
    Call<List<Criptomoneda>> getCriptos();

}
