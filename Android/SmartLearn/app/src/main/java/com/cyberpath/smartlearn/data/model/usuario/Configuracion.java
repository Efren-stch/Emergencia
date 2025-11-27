package com.cyberpath.smartlearn.data.model.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Configuracion {
    private Integer id;
    private boolean modoAudio;
    private boolean notificacionesActivadas;
    private Configuracion.TamanoFuente tamanoFuente = TamanoFuente.medio;
    private boolean modoOffline;
    private boolean cuentaCreada;

    private Integer idUsuario;

    public enum TamanoFuente {
        pequeno, medio, grande
    }
}
