package com.example.accesstock.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.accesstock.R;
import com.example.accesstock.model.Producto;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportesFragment extends Fragment {

    private TextView tvTotalProductos, tvTotalStock, tvStockBajo, tvSinCategorias, tvSinActividad;
    private LinearLayout layoutCategorias, layoutEmpleados;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    private final Map<String, String> nombresUsuarios = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        db = FirebaseFirestore.getInstance();

        tvTotalProductos = view.findViewById(R.id.tvTotalProductos);
        tvTotalStock = view.findViewById(R.id.tvTotalStock);
        tvStockBajo = view.findViewById(R.id.tvStockBajo);
        layoutCategorias = view.findViewById(R.id.layoutCategorias);
        layoutEmpleados = view.findViewById(R.id.layoutEmpleados);
        tvSinCategorias = view.findViewById(R.id.tvSinCategorias);
        tvSinActividad = view.findViewById(R.id.tvSinActividad);
        progressBar = view.findViewById(R.id.progressBar);

        cargarDatos();

        return view;
    }

    private void cargarDatos() {
        progressBar.setVisibility(View.VISIBLE);

        // 1. Cargamos usuarios primero, para poder mostrar nombres en vez de UIDs
        db.collection("usuarios").get().addOnSuccessListener(usuariosSnap -> {
            for (var doc : usuariosSnap.getDocuments()) {
                String nombre = doc.getString("nombre");
                nombresUsuarios.put(doc.getId(), nombre != null ? nombre : "Usuario");
            }
            cargarProductos();
        }).addOnFailureListener(e -> cargarProductos());
    }

    private void cargarProductos() {
        db.collection("productos").get().addOnSuccessListener(productosSnap -> {
            int totalProductos = 0;
            long totalStock = 0;
            int stockBajo = 0;
            Map<String, Integer> porCategoria = new LinkedHashMap<>();

            for (var doc : productosSnap.getDocuments()) {
                Producto p = doc.toObject(Producto.class);
                if (p == null) continue;

                totalProductos++;
                totalStock += p.getCantidad();
                if (p.getCantidad() <= p.getStockMinimo()) stockBajo++;

                String categoria = (p.getCategoria() == null || p.getCategoria().isEmpty())
                        ? "Sin categoría" : p.getCategoria();
                porCategoria.put(categoria, porCategoria.getOrDefault(categoria, 0) + 1);
            }

            tvTotalProductos.setText(String.valueOf(totalProductos));
            tvTotalStock.setText(String.valueOf(totalStock));
            tvStockBajo.setText(String.valueOf(stockBajo));

            pintarCategorias(porCategoria);
            cargarHistorial();
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
        });
    }

    private void pintarCategorias(Map<String, Integer> porCategoria) {
        layoutCategorias.removeAllViews();

        if (porCategoria.isEmpty()) {
            tvSinCategorias.setVisibility(View.VISIBLE);
            return;
        }
        tvSinCategorias.setVisibility(View.GONE);

        int maxCount = 1;
        for (int v : porCategoria.values()) maxCount = Math.max(maxCount, v);

        for (Map.Entry<String, Integer> entry : porCategoria.entrySet()) {
            layoutCategorias.addView(crearFilaBarra(entry.getKey(), entry.getValue(), maxCount, 0xFF1A3C8C));
        }
    }

    private void cargarHistorial() {
        db.collection("historial").get().addOnSuccessListener(historialSnap -> {
            Map<String, Integer> porUsuario = new LinkedHashMap<>();

            for (var doc : historialSnap.getDocuments()) {
                String uid = doc.getString("usuario");
                if (uid == null) continue;
                porUsuario.put(uid, porUsuario.getOrDefault(uid, 0) + 1);
            }

            pintarEmpleados(porUsuario);
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> progressBar.setVisibility(View.GONE));
    }

    private void pintarEmpleados(Map<String, Integer> porUsuario) {
        layoutEmpleados.removeAllViews();

        if (porUsuario.isEmpty()) {
            tvSinActividad.setVisibility(View.VISIBLE);
            return;
        }
        tvSinActividad.setVisibility(View.GONE);

        int maxCount = 1;
        for (int v : porUsuario.values()) maxCount = Math.max(maxCount, v);

        for (Map.Entry<String, Integer> entry : porUsuario.entrySet()) {
            String nombre = nombresUsuarios.getOrDefault(entry.getKey(), "Usuario desconocido");
            layoutEmpleados.addView(crearFilaBarra(nombre, entry.getValue(), maxCount, 0xFF4CAF50));
        }
    }

    /**
     * Crea una fila con: nombre a la izquierda, barra de color proporcional, y número al final.
     * Es una "gráfica de barras" simple hecha con Views, sin necesidad de librerías externas.
     */
    private LinearLayout crearFilaBarra(String etiqueta, int valor, int maxValor, int color) {
        float density = getResources().getDisplayMetrics().density;

        LinearLayout fila = new LinearLayout(getContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams filaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        filaParams.bottomMargin = (int) (10 * density);
        fila.setLayoutParams(filaParams);

        TextView tvEtiqueta = new TextView(getContext());
        tvEtiqueta.setText(etiqueta);
        tvEtiqueta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvEtiqueta.setTextColor(Color.parseColor("#333333"));
        LinearLayout.LayoutParams etiquetaParams = new LinearLayout.LayoutParams(
                (int) (110 * density), LinearLayout.LayoutParams.WRAP_CONTENT);
        tvEtiqueta.setLayoutParams(etiquetaParams);
        fila.addView(tvEtiqueta);

        View barra = new View(getContext());
        int maxAnchoPx = (int) (180 * density);
        int anchoPx = Math.max((int) (((float) valor / maxValor) * maxAnchoPx), (int) (8 * density));
        LinearLayout.LayoutParams barraParams = new LinearLayout.LayoutParams(
                anchoPx, (int) (18 * density));
        barraParams.rightMargin = (int) (8 * density);
        barra.setLayoutParams(barraParams);
        barra.setBackgroundColor(color);
        fila.addView(barra);

        TextView tvValor = new TextView(getContext());
        tvValor.setText(String.valueOf(valor));
        tvValor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvValor.setTextColor(Color.parseColor("#333333"));
        fila.addView(tvValor);

        return fila;
    }
}