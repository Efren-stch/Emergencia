package com.cyberpath.smartlearn.data.model.ejercicio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ejercicio {
    private Integer id;
    private String nombre;
    private boolean hecho;

    private Integer idSubtema;
}
