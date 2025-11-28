package com.cyberpath.smartlearn.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREFS_NAME = "smartlearn_prefs";
    private static final String KEY_USUARIO_REGISTRADO = "usuario_registrado";
    private static final String KEY_MODO_AUDIO = "modo_audio";
    private static final String KEY_ID_USUARIO = "id_usuario"; // opcional

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void setUsuarioRegistrado(Context context, boolean registrado) {
        getPrefs(context).edit().putBoolean(KEY_USUARIO_REGISTRADO, registrado).apply();
    }

    public static boolean isUsuarioRegistrado(Context context) {
        return getPrefs(context).getBoolean(KEY_USUARIO_REGISTRADO, false);
    }

    public static void setModoAudio(Context context, boolean activado) {
        getPrefs(context).edit().putBoolean(KEY_MODO_AUDIO, activado).apply();
    }

    public static boolean isModoAudioActivado(Context context) {
        return getPrefs(context).getBoolean(KEY_MODO_AUDIO, false);
    }

    public static void setIdUsuario(Context context, int id) {
        getPrefs(context).edit().putInt(KEY_ID_USUARIO, id).apply();
    }

    public static int getIdUsuario(Context context) {
        return getPrefs(context).getInt(KEY_ID_USUARIO, -1);
    }
}
