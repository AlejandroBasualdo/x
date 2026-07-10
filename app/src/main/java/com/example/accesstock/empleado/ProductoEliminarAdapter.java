package com.example.accesstock.empleado;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesstock.R;
import com.example.accesstock.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoEliminarAdapter extends RecyclerView.Adapter<ProductoEliminarAdapter.ViewHolder> {

    public interface OnEliminarClickListener {
        void onEliminarClick(Producto producto);
    }

    private List<Producto> lista = new ArrayList<>();
    private final OnEliminarClickListener listener;

    public ProductoEliminarAdapter(OnEliminarClickListener listener) {
        this.listener = listener;
    }

    public void actualizarLista(List<Producto> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_eliminar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = lista.get(position);
        holder.tvNombre.setText(producto.getNombre());
        holder.tvCantidad.setText(producto.getCantidad() + " " + producto.getUnidad());

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onEliminarClick(producto);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad;
        ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            btnEliminar = itemView.findViewById(R.id.btnEliminarItem);
        }
    }
}