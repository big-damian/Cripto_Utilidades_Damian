package com.damian.criptoutils.utilities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CalculadoraFecha {

    private int Año = 2024;
    private int Mes = 12;
    private int Dia = 11;

    public void DevolverTiempoConsola() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Define la fecha objetivo a la que calcular la diferencia
            LocalDate fechaObjetivo = LocalDate.of(Año, Mes, Dia);

            // Obtiene la fecha actual
            LocalDate fechaActual = LocalDate.now();

            // Calcula la diferencia en días
            long diasRestantes;
            diasRestantes = ChronoUnit.DAYS.between(fechaActual, fechaObjetivo);

            if (diasRestantes > 0) {
                System.out.println("\n\n\nQuedan: \u001B[34m" + diasRestantes + " dias\u001B[0m hasta el día " + fechaObjetivo);
            } else if (diasRestantes < 0) {
                System.out.println("La fecha " + fechaObjetivo + " ya ha pasado.");
            } else {
                System.out.println("Hoy es la fecha objetivo(" + fechaObjetivo + ").");
            }

        }
    }

    public CalculadoraFecha() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            DevolverTiempoTexto();
            DevolverTiempoConsola();

        }
    }

    public String DevolverTiempoTexto() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Define la fecha objetivo a la que calcular la diferencia
            LocalDate fechaObjetivo = LocalDate.of(Año, Mes, Dia);

            // Obtiene la fecha actual
            LocalDate fechaActual = LocalDate.now();

            // Calcula la diferencia en días
            long diasRestantes;
            diasRestantes = ChronoUnit.DAYS.between(fechaActual, fechaObjetivo);

            if (diasRestantes > 0) {
                return "Quedan " + diasRestantes + " días hasta el día " + fechaObjetivo;
            } else if (diasRestantes < 0) {
                return "La fecha " + fechaObjetivo + " ya ha pasado.";
            } else {
                return "Hoy es la fecha objetivo(" + fechaObjetivo + ").";
            }

        } return "Error de versión API Android";
    }
}