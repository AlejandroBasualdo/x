package com.example.accesstock.empleado;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.accesstock.R;
import com.example.accesstock.model.Producto;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EscaneoFragment extends Fragment {

    private PreviewView previewView;
    private ProgressBar progressBar;
    private ExecutorService cameraExecutor;
    private BarcodeScanner scanner;
    private FirebaseFirestore db;
    private boolean codigoDetectado = false; // evita procesar el mismo código muchas veces

    private final ActivityResultLauncher<String> permisoLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    iniciarCamara();
                } else {
                    Toast.makeText(getContext(), "Se necesita permiso de cámara para escanear", Toast.LENGTH_LONG).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_escaneo, container, false);

        previewView = view.findViewById(R.id.previewView);
        progressBar = view.findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        cameraExecutor = Executors.newSingleThreadExecutor();
        scanner = BarcodeScanning.getClient();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara();
        } else {
            permisoLauncher.launch(Manifest.permission.CAMERA);
        }

        return view;
    }

    private void iniciarCamara() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analizarImagen);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Toast.makeText(getContext(), "Error al iniciar cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @androidx.camera.core.ExperimentalGetImage
    private void analizarImagen(ImageProxy imageProxy) {
        if (codigoDetectado || imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (!barcodes.isEmpty() && !codigoDetectado) {
                        String codigo = barcodes.get(0).getRawValue();
                        if (codigo != null) {
                            codigoDetectado = true;
                            requireActivity().runOnUiThread(() -> procesarCodigo(codigo));
                        }
                    }
                })
                .addOnFailureListener(e -> {})
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void procesarCodigo(String codigo) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("productos")
                .whereEqualTo("codigoBarras", codigo)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    progressBar.setVisibility(View.GONE);

                    if (!querySnapshot.isEmpty()) {
                        // Ya existe: sumamos 1 al stock automáticamente
                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        sumarStock(doc.getId(), doc.getString("nombre"));
                    } else {
                        // No existe: mandamos a Registrar Producto con el código precargado
                        Bundle args = new Bundle();
                        args.putString("codigoBarras", codigo);
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_escaneoFragment_to_registrarProductoFragment, args);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    codigoDetectado = false;
                    Toast.makeText(getContext(), "Error al buscar producto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sumarStock(String idProducto, String nombreProducto) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("productos").document(idProducto)
                .update("cantidad", FieldValue.increment(1))
                .addOnSuccessListener(unused -> {
                    Map<String, Object> movimiento = new HashMap<>();
                    movimiento.put("idProducto", idProducto);
                    movimiento.put("nombreProducto", nombreProducto);
                    movimiento.put("tipoMovimiento", "entrada");
                    movimiento.put("cantidad", 1);
                    movimiento.put("usuario", uid);
                    movimiento.put("fecha", FieldValue.serverTimestamp());
                    db.collection("historial").add(movimiento);

                    Toast.makeText(getContext(), "+1 stock: " + nombreProducto, Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al actualizar stock: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
    }
}