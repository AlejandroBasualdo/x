package com.example.accesstock.empleado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesstock.R;
import com.example.accesstock.model.Producto;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

public class StockFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvVacio;
    private ProductoAdapter adapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewStock);
        tvVacio = view.findViewById(R.id.tvVacio);

        adapter = new ProductoAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        cargarProductos();

        return view;
    }

    private void cargarProductos() {
        if (!com.example.accesstock.ConexionUtil.hayInternet(getContext())) {
            Toast.makeText(getContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
        }

        db.collection("productos")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error al cargar productos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (querySnapshot == null) return;

                    List<Producto> productos = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        Producto producto = doc.toObject(Producto.class);
                        if (producto != null) {
                            producto.setId(doc.getId());
                            productos.add(producto);
                        }
                    }

                    adapter.actualizarLista(productos);
                    tvVacio.setVisibility(productos.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerView.setVisibility(productos.isEmpty() ? View.GONE : View.VISIBLE);
                });
    }
}