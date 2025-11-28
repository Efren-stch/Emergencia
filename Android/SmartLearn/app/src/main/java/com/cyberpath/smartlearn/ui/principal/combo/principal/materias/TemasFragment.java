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
import com.cyberpath.smartlearn.data.model.contenido.Materia;
import com.cyberpath.smartlearn.data.model.contenido.Tema;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TemasFragment extends Fragment implements AdapterView.OnItemClickListener {
    private TextView textoMateria;

    private ListView listViewTemas;
    private AdaptadorTemas adaptadorTemas;
    private List<Tema> listaTemas = new ArrayList<>();

    private Materia materia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textoMateria = view.findViewById(R.id.tvNombreMateria);

        listViewTemas = view.findViewById(R.id.listViewTemas);
        adaptadorTemas = new AdaptadorTemas(requireContext(), listaTemas);
        listViewTemas.setAdapter(adaptadorTemas);
        listViewTemas.setOnItemClickListener(this);

        materia = TemasFragmentArgs.fromBundle(getArguments()).getMateria();
        int materiaId = materia.getId();

        textoMateria.setText(materia.getNombre());

        if (materiaId == -1) {
            Toast.makeText(requireContext(), "Error: materia no válida", Toast.LENGTH_SHORT).show();
            return;
        }


        cargarTemas(materiaId);
    }

    private void cargarTemas(int materiaId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Tema>> call = apiService.getTemasByMateria(materiaId);

        call.enqueue(new Callback<List<Tema>>() {
            @Override
            public void onResponse(Call<List<Tema>> call, Response<List<Tema>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tema> temas = response.body();
                    requireActivity().runOnUiThread(() -> {
                        listaTemas.clear();
                        listaTemas.addAll(temas);
                        adaptadorTemas.notifyDataSetChanged();

                        if (temas.isEmpty()) {
                            Toast.makeText(requireContext(), "No hay temas para esta materia", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mostrarError("Error al cargar temas");
                }
            }

            @Override
            public void onFailure(Call<List<Tema>> call, Throwable t) {
                mostrarError("Error de red: " + t.getMessage());
            }
        });
    }

    private void mostrarError(String mensaje) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Tema tema = listaTemas.get(position);

        var action = TemasFragmentDirections.actionTemasFragmentToSubtemasFragment(tema);
        NavHostFragment.findNavController(this).navigate(action);
    }
}