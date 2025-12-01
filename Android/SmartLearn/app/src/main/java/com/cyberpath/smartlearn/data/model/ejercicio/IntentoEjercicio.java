package com.cyberpath.smartlearn.data.model.ejercicio;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntentoEjercicio {
    private Integer id;
    private double puntaje;
    private LocalDateTime fecha;

    private Integer idUsuario;
    private Integer idEjercicio;
}
