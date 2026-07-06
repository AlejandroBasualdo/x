package com.example.accesstock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        Button btnHistorial = findViewById(R.id.btnHistorial);
        Button btnEscaneo = findViewById(R.id.btnEscaneo);
        Button btnStock = findViewById(R.id.btnStock);
        Button btnEliminar = findViewById(R.id.btnEliminar);
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnRegistrar.setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Abriendo Registro...", android.widget.Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, RegistroProductoActivity.class));
        });
        btnHistorial.setOnClickListener(v -> startActivity(new Intent(this, HistorialActivity.class)));
        btnEscaneo.setOnClickListener(v -> startActivity(new Intent(this, EscaneoActivity.class)));
        btnStock.setOnClickListener(v -> startActivity(new Intent(this, StockActivity.class)));
        btnEliminar.setOnClickListener(v -> startActivity(new Intent(this, EliminarProductoActivity.class)));
        
        btnCerrarSesion.setOnClickListener(v -> {
            finish();
        });
    }
}
