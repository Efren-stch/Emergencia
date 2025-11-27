package com.cyberpath.smartlearn.data.model.contenido;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tema {
    private Integer id;
    private String nombre;

    private Integer idMateria;
}
