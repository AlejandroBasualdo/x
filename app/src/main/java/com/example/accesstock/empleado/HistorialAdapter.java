package com.example.accesstock.empleado;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesstock.R;
import com.example.accesstock.model.Movimiento;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder> {

    private List<Movimiento> lista = new ArrayList<>();
    private final SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public void actualizarLista(List<Movimiento> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial, parent, false);
        return new HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Movimiento m = lista.get(position);

        holder.tvNombreProducto.setText(m.getNombreProducto());
        holder.tvCantidadMovimiento.setText("Cantidad: " + m.getCantidad());

        String tipo = m.getTipoMovimiento() != null ? m.getTipoMovimiento().toUpperCase() : "";
        holder.tvTipoMovimiento.setText(tipo);

        if ("ENTRADA".equals(tipo)) {
            holder.tvTipoMovimiento.setTextColor(0xFF4CAF50); // verde
        } else if ("SALIDA".equals(tipo)) {
            holder.tvTipoMovimiento.setTextColor(0xFFFFA726); // naranja
        } else {
            holder.tvTipoMovimiento.setTextColor(0xFFE53935); // rojo (eliminación)
        }

        if (m.getFecha() != null) {
            holder.tvFecha.setText(formato.format(m.getFecha()));
        } else {
            holder.tvFecha.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class HistorialViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreProducto, tvTipoMovimiento, tvCantidadMovimiento, tvFecha;

        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProducto = itemView.findViewById(R.id.tvNombreProducto);
            tvTipoMovimiento = itemView.findViewById(R.id.tvTipoMovimiento);
            tvCantidadMovimiento = itemView.findViewById(R.id.tvCantidadMovimiento);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}