package com.cyberpath.smartlearn.ui.acceso;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.usuario.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SignUpFragment";

    // UI
    private Button btnRegresar, btnRegistro;
    private EditText etNombre, etContrasena, etCorreo;
    private RadioButton radioActiva, radioInactiva, radioAlumno, radioDocente;
    private RadioGroup grupoAccesibilidad, grupoTipoUsuario;
    private ProgressBar loading;

    private SharedPreferences prefs;

    private static final String PREF_MODO_AUDIO = "modo_audio"; // clave que coincides con tu tabla configuracion

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = requireActivity().getSharedPreferences("configuracion_app", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnRegresar = view.findViewById(R.id.btn_regresar);
        btnRegistro = view.findViewById(R.id.btn_registro);
        etNombre = view.findViewById(R.id.et_nombre);
        etContrasena = view.findViewById(R.id.et_contraseña);
        etCorreo = view.findViewById(R.id.et_correo);
        radioActiva = view.findViewById(R.id.radio_activa);
        radioInactiva = view.findViewById(R.id.radio_inactiva);
        radioAlumno = view.findViewById(R.id.radio_alumno);
        radioDocente = view.findViewById(R.id.radio_docente);
        grupoAccesibilidad = view.findViewById(R.id.grupo_accesibilidad);
        grupoTipoUsuario = view.findViewById(R.id.grupo_tipo_usuario);
        loading = view.findViewById(R.id.loading);

        btnRegresar.setOnClickListener(this);
        btnRegistro.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_regresar) {
            Navigation.findNavController(v).navigate(R.id.loginFragment);
        } else if (v.getId() == R.id.btn_registro) {
            registrarUsuario();
        }
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String contrasena = etContrasena.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();

        if (nombre.isEmpty() || contrasena.isEmpty() || correo.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean modoAudio;
        if (radioActiva.isChecked()) {
            modoAudio = true;
        } else if (radioInactiva.isChecked()) {
            modoAudio = false;
        } else {
            Toast.makeText(requireContext(), "Selecciona el modo de accesibilidad", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.edit().putBoolean(PREF_MODO_AUDIO, modoAudio).apply();

        Integer idRol;
        if (radioAlumno.isChecked()) {
            idRol = 1;
        } else if (radioDocente.isChecked()) {
            idRol = 2;
        } else {
            Toast.makeText(requireContext(), "Selecciona tipo de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombreCuenta(nombre);
        nuevoUsuario.setContrasena(contrasena);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setIdRol(idRol);

        Log.d(TAG, "Registrando usuario: " + nombre + ", correo: " + correo + ", rol: " + idRol);
        loading.setVisibility(View.VISIBLE);
        btnRegistro.setEnabled(false);

        ApiService api = RetrofitClient.getApiService();
        api.save(nuevoUsuario).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                loading.setVisibility(View.GONE);
                btnRegistro.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();

                    prefs.edit().putBoolean("usuarioRegistrado", true).apply();

                    Navigation.findNavController(requireView()).navigate(R.id.loginFragment);
                } else {
                    String error = "Error " + response.code() + ": " + response.message();
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, error);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                loading.setVisibility(View.GONE);
                btnRegistro.setEnabled(true);
                String error = "Error de red: " + t.getMessage();
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                Log.e(TAG, error, t);
            }
        });
    }
}