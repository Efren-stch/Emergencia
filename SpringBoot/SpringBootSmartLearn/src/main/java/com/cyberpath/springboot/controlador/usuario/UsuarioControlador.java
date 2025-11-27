package com.cyberpath.springboot.controlador.usuario;

import com.cyberpath.springboot.dto.contenido.MateriaDto;
import com.cyberpath.springboot.dto.usuario.UsuarioDto;
import com.cyberpath.springboot.modelo.contenido.Materia;
import com.cyberpath.springboot.modelo.usuario.Configuracion;
import com.cyberpath.springboot.modelo.usuario.Rol;
import com.cyberpath.springboot.modelo.usuario.UltimaConexion;
import com.cyberpath.springboot.modelo.usuario.Usuario;
import com.cyberpath.springboot.servicio.servicio.usuario.UsuarioServicio;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/smartlearn/api")
@RestController
@AllArgsConstructor
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;

    // ============== METODOS PRINCIPALES ===================
    @GetMapping("/usuario")
    public ResponseEntity<List<UsuarioDto>> lista() {
        List<Usuario> usuarios = usuarioServicio.getAll();
        if (usuarios == null || usuarios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<UsuarioDto> dtos = usuarios.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioDto> getById(@PathVariable Integer id) {
        Usuario usuario = usuarioServicio.getById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDto(usuario));
    }

    @PostMapping("/usuario")
    public ResponseEntity<UsuarioDto> save(@RequestBody UsuarioDto usuarioDto) {
        Usuario usuario = mapDtoToEntity(usuarioDto);

        if (usuarioDto.getIdConfiguracion() != null) {
            Configuracion configuracion = Configuracion.builder()
                    .id(usuarioDto.getIdConfiguracion())
                    .build();
            configuracion.setUsuario(usuario);
            usuario.setConfiguracion(configuracion);
        }

        if (usuarioDto.getIdUltimaConexion() != null) {
            UltimaConexion ultimaConexion = UltimaConexion.builder()
                    .id(usuarioDto.getIdUltimaConexion())
                    .ultimaConexion(LocalDateTime.now())  // Valor por defecto si no se proporciona
                    .build();
            ultimaConexion.setUsuario(usuario);
            usuario.setUltimaConexion(ultimaConexion);
        }

        Usuario guardado = usuarioServicio.save(usuario);
        return ResponseEntity.ok(convertToDto(guardado));
    }

    @PutMapping("usuario/{id}")
    public ResponseEntity<UsuarioDto> update(@PathVariable Integer id, @RequestBody UsuarioDto usuarioDto) {
        Usuario datosActualizacion = mapDtoToEntity(usuarioDto);

        if (usuarioDto.getIdConfiguracion() != null) {
            Configuracion configuracion = Configuracion.builder()
                    .id(usuarioDto.getIdConfiguracion())
                    .build();
            configuracion.setUsuario(Usuario.builder().id(id).build());
            datosActualizacion.setConfiguracion(configuracion);
        }

        if (usuarioDto.getIdUltimaConexion() != null) {
            UltimaConexion ultimaConexion = UltimaConexion.builder()
                    .id(usuarioDto.getIdUltimaConexion())
                    .ultimaConexion(LocalDateTime.now())
                    .build();
            ultimaConexion.setUsuario(Usuario.builder().id(id).build());
            datosActualizacion.setUltimaConexion(ultimaConexion);
        }

        Usuario actualizado = usuarioServicio.update(id, datosActualizacion);
        return ResponseEntity.ok(convertToDto(actualizado));
    }

    @DeleteMapping("usuario/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioServicio.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("usuario/{id}/materias")
    public ResponseEntity<List<MateriaDto>> getMateriasByUsuario(@PathVariable Integer id) {
        Usuario usuario = usuarioServicio.getById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        List<MateriaDto> materias = usuario.getUsuariosMaterias()
                .stream()
                .map(um -> {
                    Materia m = um.getMateria();
                    return MateriaDto.builder()
                            .id(m.getId())
                            .nombre(m.getNombre())
                            .descripcion(m.getDescripcion())
                            .build();
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(materias);
    }

    // ====================== METODOS AUXILIARES ========================
    @PostMapping("usuario/login")
    public ResponseEntity<UsuarioDto> login(@RequestBody UsuarioDto loginRequest) {
        if (loginRequest.getNombreCuenta() == null || loginRequest.getContrasena() == null) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuario = usuarioServicio.findByNombreCuenta(loginRequest.getNombreCuenta());
        if (usuario == null || !usuario.getContrasena().equals(loginRequest.getContrasena())) {
            return ResponseEntity.status(401).build();
        }

        UsuarioDto response = convertToDto(usuario);
        response.setContrasena(null);
        return ResponseEntity.ok(response);
    }

    // ====================== MÉTODOS DE CONVERSIÓN ======================
    private UsuarioDto convertToDto(Usuario usuario) {
        return UsuarioDto.builder()
                .id(usuario.getId())
                .nombreCuenta(usuario.getNombreCuenta())
                .correo(usuario.getCorreo())
                .contrasena(usuario.getContrasena())
                .idRol(usuario.getRol() != null ? usuario.getRol().getId() : null)
                .idConfiguracion(usuario.getConfiguracion() != null ? usuario.getConfiguracion().getId() : null)
                .idUltimaConexion(usuario.getUltimaConexion() != null ? usuario.getUltimaConexion().getId() : null)
                .build();
    }

    // ====================== MAPEO DTO → ENTIDAD ======================
    private Usuario mapDtoToEntity(UsuarioDto dto) {
        return Usuario.builder()
                .nombreCuenta(dto.getNombreCuenta())
                .correo(dto.getCorreo())
                .contrasena(dto.getContrasena())
                .rol(dto.getIdRol() != null ? Rol.builder().id(dto.getIdRol()).build() : null)
                .build();
    }
}