package com.cyberpath.smartlearn.data.api;

import com.cyberpath.smartlearn.data.model.contenido.Materia;
import com.cyberpath.smartlearn.data.model.contenido.Subtema;
import com.cyberpath.smartlearn.data.model.contenido.Tema;
import com.cyberpath.smartlearn.data.model.relaciones.UsuarioMateria;
import com.cyberpath.smartlearn.data.model.usuario.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // =========== /smartlearn/api/usuario ======================
    // GET
    @GET("/smartlearn/api/usuario")
    Call<List<Usuario>> getUsuarios();
    @GET("/smartlearn/api/usuario/{id}/materias")
    Call<List<Materia>> getMateriasByUsuario(@Path("id") Integer idUsuario);
    @GET("/smartlearn/api/materia/{id}/temas")
    Call<List<Tema>> getTemasByMateria(@Path("id") Integer idMateria);
    @GET("/smartlearn/api/tema/{id}/subtemas")
    Call<List<Subtema>> getSubtemasByTema(@Path("id") Integer idTema);
    // POST
    @POST("/smartlearn/api/usuario")
    Call<Usuario> save(@Body Usuario usuario);
    @POST("/smartlearn/api/usuario/login")
    Call<Usuario> login(@Body Usuario loginRequest);

    // =========== /smartlearn/api/materia ======================
    // GET
    @GET("/smartlearn/api/materia")
    Call<List<Materia>> getMaterias();

    // =========== /smartlearn/api/usuario-materia ======================
    @POST("/smartlearn/api/usuario-materia")
    Call<UsuarioMateria> save(@Body UsuarioMateria usuarioMateria);
}