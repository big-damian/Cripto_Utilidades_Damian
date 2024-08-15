package com.damian.criptoutils.miscriptorecyclersqlite;

public class MisCriptomonedas {
    private int id;
    private String name;
    private String simbolo;
    private String cantidad;

    public MisCriptomonedas(int id, String nombre, String simbolo, String cantidad) {
        this.id = id;
        this.name = nombre;
        this.simbolo = simbolo;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public String getCantidad() {
        return cantidad;
    }

    public String getValor() {
        return "0 €";  // TODO: Actualizar esto para que devuelva el valor de la cantidad en euros (cantidad * precio)
    }
}