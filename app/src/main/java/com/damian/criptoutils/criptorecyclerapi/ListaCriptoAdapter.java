package com.damian.criptoutils.criptorecyclerapi;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.damian.criptoutils.R;

import java.util.List;

public class ListaCriptoAdapter extends RecyclerView.Adapter<ListaCriptoAdapter.ViewHolder> {

    private List<Criptomoneda> cripto;
    private Context context;

    public ListaCriptoAdapter(List<Criptomoneda> cripto, Context context) {
        this.cripto = cripto;
        this.context = context;
    }

    @NonNull
    @Override
    public ListaCriptoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext())
        View view = LayoutInflater.from(context)
                .inflate(R.layout.criptos_elementos_recycler,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaCriptoAdapter.ViewHolder holder, int position) {
        holder.nombreListaCripto.setText(cripto.get(position).getName());
        holder.precioListaCripto.setText(String.valueOf(cripto.get(position).getPrecio()) + " €");
        holder.variacionPrecioListaCripto.setText(String.valueOf(cripto.get(position).getVariacionPrecio2Decis()) + " %");
        if (cripto.get(position).getVariacionPrecio() > 0) {
            holder.variacionPrecioListaCripto.setTextColor(Color.GREEN);
        } else {
            holder.variacionPrecioListaCripto.setTextColor(Color.RED);
        }
        Glide.with(context).load(cripto.get(position).getImage()).into(holder.fotoListaCripto);
    }

    @Override
    public int getItemCount() {
//        return cripto.size();
        return cripto == null ? 0 : cripto.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView fotoListaCripto;
        private TextView nombreListaCripto;
        private TextView precioListaCripto;
        private TextView variacionPrecioListaCripto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoListaCripto = itemView.findViewById(R.id.fotoListaCripto);
            nombreListaCripto = itemView.findViewById(R.id.nombreListaCripto);
            precioListaCripto = itemView.findViewById(R.id.precioListaCripto);
            variacionPrecioListaCripto = itemView.findViewById(R.id.variacionPrecioListaCripto);
        }
    }
}
