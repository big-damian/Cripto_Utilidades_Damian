package com.damian.criptoutils.miscriptorecyclersqlite;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.damian.criptoutils.R;

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
        holder.valorTextView.setText(item.getValor());
    }

    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        TextView cantidadTextView;
        TextView valorTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreListaMisCripto);
            cantidadTextView = itemView.findViewById(R.id.cantidadListaMisCripto);
            valorTextView = itemView.findViewById(R.id.valorListaMisCripto);
        }
    }

    // Método para actualizar la lista de datos
    public void actualizarRecycler(List<MisCriptomonedas> newCryptoList) {
        this.cryptoList = newCryptoList;
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }
    
}
