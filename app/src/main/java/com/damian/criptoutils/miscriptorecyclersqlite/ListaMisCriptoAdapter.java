package com.damian.criptoutils.miscriptorecyclersqlite;

import android.app.Dialog;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.damian.criptoutils.R;
import com.damian.criptoutils.utilities.SQLiteManager;

import com.bumptech.glide.Glide;
import java.util.List;

public class ListaMisCriptoAdapter extends RecyclerView.Adapter<ListaMisCriptoAdapter.ItemViewHolder> {

    private List<MisCriptomonedas> cryptoList;
    private Context context;

    public ListaMisCriptoAdapter(Context context, List<MisCriptomonedas> cryptoList) {
        this.context = context;
        this.cryptoList = cryptoList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.miscriptos_elementos_recycler, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        MisCriptomonedas item = cryptoList.get(position);
        holder.nombreTextView.setText(item.getName() + " (" + item.getSimbolo() + ")");
        holder.cantidadTextView.setText(item.getCantidad() + " " + item.getSimbolo());

        // Calculo de valor de Mis Criptos
        // Abrir base de datos y llamar a metodo eliminar moneda por simbolo
        SQLiteManager dbManager = new SQLiteManager(context);
        dbManager.open();
        // Multiplicamos la cantidad por el precio sacado de la BDD
        try {
            double calculoValor = Double.parseDouble( item.getCantidad() ) * Double.parseDouble( dbManager.selectPrecioBDD(item.getName()) ); // Calculo de valor con multiplicacion sacada de SQLite
            String calculoValor2Decimales = String.format("%.2f", calculoValor);
            holder.valorTextView.setText(calculoValor2Decimales + " €");
        } catch (NumberFormatException e) {
            Log.e("CalculoValorMisCriptos", "No se pudo parsear el valor del precio o de la cantidad");
            holder.valorTextView.setText("? €");
        }
        //

        // Use Glide para cargar los iconos
        Glide.with(context)
                .load(dbManager.selectIconoURL(item.getName())) // Assuming you have a method to get the image URL
                .placeholder(R.drawable.icono_cargando) // Placeholder image
                .into(holder.fotoListaMisCripto);

        holder.iconoBorrarMisCriptos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(item.getSimbolo());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    // Método para actualizar la lista de datos

    public void actualizarRecycler(List<MisCriptomonedas> newCryptoList) {
        this.cryptoList = newCryptoList;
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    private void showDialog(String simbolo) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_delete_miscripto);

        //TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
        TextView botonDialogoEliminarTusCriptos = dialog.findViewById(R.id.botonDialogoEliminarTusCriptos);
        Button botonCancelarDialogo = dialog.findViewById(R.id.botonCancelarDialogo);

        botonCancelarDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        botonDialogoEliminarTusCriptos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Dialogo eliminar criptos", Toast.LENGTH_SHORT).show();

                // Abrir base de datos y llamar a metodo eliminar moneda por simbolo
                SQLiteManager dbManager = new SQLiteManager(context);
                dbManager.open();
                dbManager.deleteMisCriptomoneda(simbolo);
                dialog.dismiss();
                // Update the RecyclerView
                List<MisCriptomonedas> updatedList = dbManager.selectTodasMiscriptos();
                actualizarRecycler(updatedList);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView iconoBorrarMisCriptos;
        TextView nombreTextView;
        TextView cantidadTextView;
        TextView valorTextView;
        ImageView fotoListaMisCripto;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoListaMisCripto = itemView.findViewById(R.id.fotoListaMisCripto);
            nombreTextView = itemView.findViewById(R.id.nombreListaMisCripto);
            cantidadTextView = itemView.findViewById(R.id.cantidadListaMisCripto);
            valorTextView = itemView.findViewById(R.id.valorListaMisCripto);
            iconoBorrarMisCriptos = itemView.findViewById(R.id.iconoBorrarMisCriptos);

            // Filtro de color para poner el icono Borrar en blanco y negro
//            ColorMatrix colorMatrix = new ColorMatrix();
//            colorMatrix.setSaturation(0);
//            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
//            iconoBorrarMisCriptos.setColorFilter(filter);
        }
    }
}