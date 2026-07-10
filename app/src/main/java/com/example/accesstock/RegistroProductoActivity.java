package com.example.accesstock;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.accesstock.data.Producto;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistroProductoActivity extends AppCompatActivity {

    private EditText etNombre, etDetalles, etStock, etCodigoBarras, etProveedor;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_producto);

        db = FirebaseFirestore.getInstance();

        etNombre = findViewById(R.id.etNombre);
        etDetalles = findViewById(R.id.etDetalles);
        etStock = findViewById(R.id.etStock);
        etCodigoBarras = findViewById(R.id.etCodigoBarras);
        etProveedor = findViewById(R.id.etProveedor);
        Button btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void guardarProducto() {
        android.widget.Toast.makeText(this, "Intentando guardar...", android.widget.Toast.LENGTH_SHORT).show();
        String nombre = etNombre.getText().toString().trim();
        String detalles = etDetalles.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        String codigo = etCodigoBarras.getText().toString().trim();
        String proveedor = etProveedor.getText().toString().trim();

        if (nombre.isEmpty() || stockStr.isEmpty() || codigo.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock = Integer.parseInt(stockStr);
        Producto producto = new Producto(nombre, detalles, stock, codigo, proveedor);
        producto.setId(codigo); // Usamos el código de barras como ID

        db.collection("productos").document(codigo).set(producto)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
