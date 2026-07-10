package com.example.accesstock.empleado;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesstock.R;
import com.example.accesstock.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> listaProductos = new ArrayList<>();

    public void actualizarLista(List<Producto> nuevaLista) {
        this.listaProductos = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);

        holder.tvNombre.setText(producto.getNombre());
        holder.tvCategoria.setText(producto.getCategoria());
        holder.tvCantidad.setText(producto.getCantidad() + " " + producto.getUnidad());

        // Indicador de color según nivel de stock
        if (producto.getCantidad() <= producto.getStockMinimo()) {
            holder.indicador.setBackgroundColor(0xFFE53935); // rojo - stock bajo
        } else if (producto.getCantidad() <= producto.getStockMinimo() * 2) {
            holder.indicador.setBackgroundColor(0xFFFFA726); // naranja - stock medio
        } else {
            holder.indicador.setBackgroundColor(0xFF4CAF50); // verde - stock bien
        }
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCategoria, tvCantidad;
        View indicador;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            indicador = itemView.findViewById(R.id.indicadorStock);
        }
    }
}