package com.damian.criptoutils.criptorecyclerapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitLlamadaAPIListaCripto {

    private static Retrofit retrofit;
    public static Retrofit getClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.coingecko.com/api/v3/coins/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
