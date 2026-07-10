package com.example.accesstock.empleado;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.accesstock.R;
import com.example.accesstock.model.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EliminarProductoFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvVacio;
    private ProductoEliminarAdapter adapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eliminar, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewEliminar);
        tvVacio = view.findViewById(R.id.tvVacio);

        adapter = new ProductoEliminarAdapter(this::confirmarEliminar);
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

    private void confirmarEliminar(Producto producto) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar producto")
                .setMessage("¿Seguro que quieres eliminar \"" + producto.getNombre() + "\"? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarProducto(producto))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarProducto(Producto producto) {
        db.collection("productos").document(producto.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    registrarEliminacion(producto);
                    Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void registrarEliminacion(Producto producto) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> movimiento = new HashMap<>();
        movimiento.put("idProducto", producto.getId());
        movimiento.put("nombreProducto", producto.getNombre());
        movimiento.put("tipoMovimiento", "eliminacion");
        movimiento.put("cantidad", producto.getCantidad());
        movimiento.put("usuario", uid);
        movimiento.put("fecha", FieldValue.serverTimestamp());

        db.collection("historial").add(movimiento);
    }
}