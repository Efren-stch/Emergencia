package com.cyberpath.smartlearn.util.constants;

import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.usuario.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioCst {
    public static Usuario USUARIO_ACTUAL;

    public static void asignarConstantesUsuario(Integer id) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Usuario>> call = apiService.getUsuarios();
        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Usuario> usuarios = response.body();
                    for (Usuario u : usuarios) {
                        if (u.getId().equals(id)) {
                            String nombre = u.getNombreCuenta();
                            String correo = u.getCorreo();
                            String contrasena = u.getContrasena();
                            Integer rol = u.getIdRol();
                            Integer configuracion = u.getIdConfiguracion();
                            Integer ultimaConexion = u.getIdUltimaConexion();

                            USUARIO_ACTUAL = new Usuario(
                                    id, nombre, correo, contrasena, rol, configuracion, ultimaConexion
                            );
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                System.out.println("Error al cargar usuario");
            }
        });
    }
}
