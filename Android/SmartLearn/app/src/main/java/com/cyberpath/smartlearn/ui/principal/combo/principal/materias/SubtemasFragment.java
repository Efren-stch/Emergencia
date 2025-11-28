package com.cyberpath.smartlearn.ui.principal.combo.principal.materias;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.contenido.Subtema;
import com.cyberpath.smartlearn.data.model.contenido.Tema;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubtemasFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listViewSubtemas;
    private AdaptadorSubtemas adaptadorSubtemas;
    private List<Subtema> listaSubtemas = new ArrayList<>();

    private TextView textoTema;

    private Tema tema;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subtemas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textoTema = view.findViewById(R.id.tvNombreTema);

        listViewSubtemas = view.findViewById(R.id.listViewSubtemas);
        adaptadorSubtemas = new AdaptadorSubtemas(requireContext(), listaSubtemas);
        listViewSubtemas.setAdapter(adaptadorSubtemas);
        listViewSubtemas.setOnItemClickListener(this);

        tema = SubtemasFragmentArgs.fromBundle(getArguments()).getTema();
        int temaId = tema.getId();

        textoTema.setText(tema.getNombre());

        if (temaId == -1) {
            Toast.makeText(requireContext(), "Error: tema no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        cargarSubtemas(temaId);
    }

    private void cargarSubtemas(int subtemaId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Subtema>> call = apiService.getSubtemasByTema(subtemaId);

        call.enqueue(new Callback<List<Subtema>>() {
            @Override
            public void onResponse(Call<List<Subtema>> call, Response<List<Subtema>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Subtema> subsubtemas = response.body();
                    requireActivity().runOnUiThread(() -> {
                        listaSubtemas.clear();
                        listaSubtemas.addAll(subsubtemas);
                        adaptadorSubtemas.notifyDataSetChanged();

                        if (subsubtemas.isEmpty()) {
                            Toast.makeText(requireContext(), "No hay subsubtemas disponibles", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mostrarError("Error al cargar subsubtemas");
                }
            }

            @Override
            public void onFailure(Call<List<Subtema>> call, Throwable t) {
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void mostrarError(String mensaje) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Subtema subsubtema = listaSubtemas.get(position);

        Toast.makeText(requireContext(),
                "Seleccionaste: " + subsubtema.getNombre(), Toast.LENGTH_LONG).show();

    }
}