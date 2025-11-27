package com.cyberpath.smartlearn.data.model.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UltimaConexion {
    private Integer id;
    private LocalDateTime ultimaConexion;
    private String dispositivo;

    private Integer idUsuario;
    private Integer idSubtema;
}
