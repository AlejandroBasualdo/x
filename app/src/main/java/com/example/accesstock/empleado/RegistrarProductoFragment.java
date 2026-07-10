package com.example.accesstock.empleado;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.accesstock.R;
import com.example.accesstock.model.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrarProductoFragment extends Fragment {
    private String codigoBarrasEscaneado = null;

    private EditText etNombre, etCategoria, etCantidad, etStockMinimo, etUnidad;
    private Button btnGuardar;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_producto, container, false);

        db = FirebaseFirestore.getInstance();

        etNombre = view.findViewById(R.id.etNombre);
        etCategoria = view.findViewById(R.id.etCategoria);
        etCantidad = view.findViewById(R.id.etCantidad);
        etStockMinimo = view.findViewById(R.id.etStockMinimo);
        etUnidad = view.findViewById(R.id.etUnidad);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        progressBar = view.findViewById(R.id.progressBar);

        btnGuardar.setOnClickListener(v -> guardarProducto());

        if (getArguments() != null) {
            codigoBarrasEscaneado = getArguments().getString("codigoBarras");
        }

        return view;
    }

    private void guardarProducto() {
        if (!com.example.accesstock.ConexionUtil.hayInternet(getContext())) {
            Toast.makeText(getContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
            return;
        }
        String nombre = etNombre.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();
        String stockMinimoStr = etStockMinimo.getText().toString().trim();
        String unidad = etUnidad.getText().toString().trim();

        if (nombre.isEmpty() || categoria.isEmpty() || cantidadStr.isEmpty()
                || stockMinimoStr.isEmpty() || unidad.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        long cantidad = Long.parseLong(cantidadStr);
        long stockMinimo = Long.parseLong(stockMinimoStr);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressBar.setVisibility(View.VISIBLE);
        btnGuardar.setEnabled(false);

        Producto producto = new Producto(nombre, categoria, cantidad, stockMinimo, unidad, uid, codigoBarrasEscaneado);

        db.collection("productos")
                .add(producto)
                .addOnSuccessListener(documentReference -> {
                    // Registrar también en el historial
                    registrarHistorial(documentReference.getId(), nombre, cantidad, uid);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnGuardar.setEnabled(true);
                    Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void registrarHistorial(String idProducto, String nombreProducto, long cantidad, String uid) {
        Map<String, Object> movimiento = new HashMap<>();
        movimiento.put("idProducto", idProducto);
        movimiento.put("nombreProducto", nombreProducto);
        movimiento.put("tipoMovimiento", "entrada");
        movimiento.put("cantidad", cantidad);
        movimiento.put("usuario", uid);
        movimiento.put("fecha", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("historial")
                .add(movimiento)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnGuardar.setEnabled(true);
                    Toast.makeText(getContext(), "Producto registrado con éxito", Toast.LENGTH_SHORT).show();

                    // Limpiar campos para registrar otro producto
                    etNombre.setText("");
                    etCategoria.setText("");
                    etCantidad.setText("");
                    etStockMinimo.setText("");
                    etUnidad.setText("");
                });
    }
}