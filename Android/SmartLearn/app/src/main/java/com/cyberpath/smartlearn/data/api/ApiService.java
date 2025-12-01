package com.cyberpath.smartlearn.data.api;

import com.cyberpath.smartlearn.data.model.contenido.Materia;
import com.cyberpath.smartlearn.data.model.contenido.Subtema;
import com.cyberpath.smartlearn.data.model.contenido.Tema;
import com.cyberpath.smartlearn.data.model.relaciones.UsuarioMateria;
import com.cyberpath.smartlearn.data.model.usuario.UltimaConexion;
import com.cyberpath.smartlearn.data.model.usuario.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // =========== /smartlearn/api/usuario ======================
    @GET("/smartlearn/api/usuario")
    Call<List<Usuario>> getUsuarios();

    @GET("/smartlearn/api/usuario/{id}/materias")
    Call<List<Materia>> getMateriasByUsuario(@Path("id") Integer idUsuario);

    @GET("/smartlearn/api/materia/{id}/temas")
    Call<List<Tema>> getTemasByMateria(@Path("id") Integer idMateria);

    @GET("/smartlearn/api/tema/{id}/subtemas")
    Call<List<Subtema>> getSubtemasByTema(@Path("id") Integer idTema);

    @POST("/smartlearn/api/usuario")
    Call<Usuario> save(@Body Usuario usuario);

    @POST("/smartlearn/api/usuario/login")
    Call<Usuario> login(@Body Usuario loginRequest);

    @PUT("/smartlearn/api/usuario/{id}")
    Call<Usuario> update(@Path("id") int id, @Body Usuario usuario);

    // =========== /smartlearn/api/materia ======================
    @GET("/smartlearn/api/materia")
    Call<List<Materia>> getMaterias();

    // =========== /smartlearn/api/subtema ======================
    @GET("/smartlearn/api/subtema/{id}")
    Call<Subtema> getSubtemaById(@Path("id") Integer idSubtema);

    // =========== /smartlearn/api/usuario-materia ======================
    @POST("/smartlearn/api/usuario-materia")
    Call<UsuarioMateria> save(@Body UsuarioMateria usuarioMateria);

    // =========== /smartlearn/api/ultima-conexion ======================
    @POST("/smartlearn/api/ultima-conexion")
    Call<UltimaConexion> save(@Body UltimaConexion ultimaConexion);

    @PUT("/smartlearn/api/ultima-conexion/{id}")
    Call<UltimaConexion> update(@Path("id") int id, @Body UltimaConexion ultimaConexion);

}