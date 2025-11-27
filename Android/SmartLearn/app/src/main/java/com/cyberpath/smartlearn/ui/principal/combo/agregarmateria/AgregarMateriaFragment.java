package com.cyberpath.smartlearn.ui.principal.combo.agregarmateria;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.contenido.Materia;
import com.cyberpath.smartlearn.ui.principal.combo.principal.materias.AdaptadorMaterias;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarMateriaFragment extends Fragment {

    private ListView listViewMaterias;
    private AdaptadorMaterias adaptadorMaterias;
    private List<Materia> listaMaterias = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar_materia, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa el ListView
        listViewMaterias = view.findViewById(R.id.listViewMaterias);
        adaptadorMaterias = new AdaptadorMaterias(getContext(), listaMaterias);
        listViewMaterias.setAdapter(adaptadorMaterias);

        // Obtén el idUsuario (ej. de SharedPreferences, bundle o login)
        Integer idUsuario = obtenerIdUsuario();  // Implementa este método

        // Llama a la API para cargar materias
        cargarMaterias(idUsuario);
    }

    private void cargarMaterias(Integer idUsuario) {
        if (idUsuario == null) {
            Toast.makeText(getContext(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Materia>> call = apiService.getMateriasByUsuario(idUsuario);
        call.enqueue(new Callback<List<Materia>>() {
            @Override
            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Materia> materias = response.body();
                    // Actualiza la UI en el hilo principal
                    getActivity().runOnUiThread(() -> {
                        adaptadorMaterias.actualizarLista(materias);
                        if (materias.isEmpty()) {
                            Toast.makeText(getContext(), "No hay materias disponibles", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error al cargar materias", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onFailure(Call<List<Materia>> call, Throwable t) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private Integer obtenerIdUsuario() {
        // Ejemplo: de SharedPreferences
        // SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        // return prefs.getInt("idUsuario", -1);  // -1 si no existe
        return 1;  // Placeholder: reemplaza con el ID real
    }
}