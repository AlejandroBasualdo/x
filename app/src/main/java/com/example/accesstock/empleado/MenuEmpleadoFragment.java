package com.example.accesstock.empleado;

import android.os.Bundle;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.accesstock.R;
import com.google.firebase.auth.FirebaseAuth;

public class MenuEmpleadoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_empleado, container, false);

        Button btnRegistrar = view.findViewById(R.id.btnRegistrarProducto);
        Button btnHistorial = view.findViewById(R.id.btnHistorial);
        Button btnEscaneo = view.findViewById(R.id.btnEscaneo);
        Button btnStock = view.findViewById(R.id.btnStock);
        Button btnEliminar = view.findViewById(R.id.btnEliminar);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        btnRegistrar.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_menuEmpleadoFragment_to_registrarProductoFragment)
        );
        btnStock.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_menuEmpleadoFragment_to_stockFragment)
        );
        btnHistorial.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_menuEmpleadoFragment_to_historialFragment)
        );
        btnEliminar.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_menuEmpleadoFragment_to_eliminarProductoFragment)
        );
        btnEscaneo.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_menuEmpleadoFragment_to_escaneoFragment)
        );

        // Los demás botones (Historial, Escaneo, Stock, Eliminar) los conectamos
        // cuando creemos cada una de esas pantallas.

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
        });

        return view;
    }
}