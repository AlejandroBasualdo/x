package com.example.accesstock.auth;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private EditText etCorreo, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etCorreo = view.findViewById(R.id.etCorreo);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        progressBar = view.findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> intentarLogin());

        return view;
    }

    private void intentarLogin() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            buscarRolYNavegar(uid);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            Toast.makeText(getContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void buscarRolYNavegar(String uid) {
        db.collection("usuarios").document(uid).get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            String rol = doc.getString("rol");
                            if ("admin".equals(rol)) {
                                Navigation.findNavController(requireView())
                                        .navigate(R.id.action_loginFragment_to_menuAdminFragment);
                            } else if ("empleado".equals(rol)) {
                                Navigation.findNavController(requireView())
                                        .navigate(R.id.action_loginFragment_to_menuEmpleadoFragment);
                            } else {
                                Toast.makeText(getContext(), "Rol no reconocido", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al consultar el rol", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}