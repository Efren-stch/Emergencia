package com.cyberpath.smartlearn.ui.acceso;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;  // O usa androidx.preference
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.usuario.Usuario;
import com.cyberpath.smartlearn.logic.acceso.validarAcceso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private Button btnLogin, btnRegistro;
    private EditText etUsuario, etContrasena;
    private validarAcceso validarAcceso;
    private SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validarAcceso = new validarAcceso(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());  // Agregado: inicializar prefs
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogin = view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        btnRegistro = view.findViewById(R.id.btn_registro);
        btnRegistro.setOnClickListener(this);

        etUsuario = view.findViewById(R.id.et_nombre_usuario);
        etContrasena = view.findViewById(R.id.et_contrasena);

        boolean usuarioRegistrado = prefs.getBoolean("usuarioRegistrado", false);
        if (!usuarioRegistrado) {
            Navigation.findNavController(view).navigate(R.id.signUpFragment);
            return;
        }
        view.post(() -> validarAcceso.verificarPermisoBiometria());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_registro) {
            Navigation.findNavController(v).navigate(R.id.signUpFragment);
        } else if (v.getId() == R.id.btn_login) {
            String usuario = etUsuario.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();
            validarAcceso.validarLoginManual(usuario, contrasena);
        }
    }

    public void realizarLoginManual(String nombreCuenta, String contrasena) {
        ApiService api = RetrofitClient.getApiService();
        Usuario loginRequest = new Usuario();
        loginRequest.setNombreCuenta(nombreCuenta);
        loginRequest.setContrasena(contrasena);

        api.login(loginRequest).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Login exitoso", Toast.LENGTH_SHORT).show();
                    validarAcceso.navegacionMainActivity();  // Cambiado: método correcto
                } else if (response.code() == 401) {
                    Toast.makeText(getContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}