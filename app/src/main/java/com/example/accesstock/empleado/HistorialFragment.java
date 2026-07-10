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
import com.example.accesstock.model.Movimiento;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

public class HistorialFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvVacio;
    private HistorialAdapter adapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerViewHistorial);
        tvVacio = view.findViewById(R.id.tvVacio);

        adapter = new HistorialAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        cargarHistorial();

        return view;
    }

    private void cargarHistorial() {
        if (!com.example.accesstock.ConexionUtil.hayInternet(getContext())) {
            Toast.makeText(getContext(), "No hay conexión a internet", Toast.LENGTH_LONG).show();
        }

        db.collection("historial")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error al cargar historial: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (querySnapshot == null) return;

                    List<Movimiento> movimientos = new ArrayList<>();
                    for (var doc : querySnapshot.getDocuments()) {
                        Movimiento m = doc.toObject(Movimiento.class);
                        if (m != null) movimientos.add(m);
                    }

                    adapter.actualizarLista(movimientos);
                    tvVacio.setVisibility(movimientos.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerView.setVisibility(movimientos.isEmpty() ? View.GONE : View.VISIBLE);
                });
    }
}