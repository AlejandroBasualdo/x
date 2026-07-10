package com.example.accesstock.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.accesstock.R;
import com.google.firebase.auth.FirebaseAuth;
import androidx.navigation.Navigation;

public class MenuAdminFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_admin, container, false);

        Button btnReportes = view.findViewById(R.id.btnReportes);
        Button btnDetalle = view.findViewById(R.id.btnDetalle);
        Button btnEliminar = view.findViewById(R.id.btnEliminar);
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        btnReportes.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_menuAdminFragment_to_reportesFragment)
        );

        btnDetalle.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_menuAdminFragment_to_stockFragment)
        );

        btnEliminar.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_menuAdminFragment_to_eliminarProductoFragment)
        );

        btnCerrarSesion.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
        });

        return view;
    }

}