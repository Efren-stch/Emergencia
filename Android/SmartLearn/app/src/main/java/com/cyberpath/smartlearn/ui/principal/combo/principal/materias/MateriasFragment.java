package com.cyberpath.smartlearn.ui.principal.combo.principal.materias;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.contenido.Materia;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MateriasFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listViewMaterias;
    private AdaptadorMaterias adaptadorMaterias;
    private List<Materia> listaMaterias = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_materias, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewMaterias = view.findViewById(R.id.listViewMaterias);
        adaptadorMaterias = new AdaptadorMaterias(getContext(), listaMaterias);
        listViewMaterias.setAdapter(adaptadorMaterias);
        listViewMaterias.setOnItemClickListener(this);

        Integer idUsuario = obtenerIdUsuario();
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
                    requireActivity().runOnUiThread(() -> {
                        listaMaterias.clear();
                        listaMaterias.addAll(materias);
                        adaptadorMaterias.notifyDataSetChanged();

                        if (materias.isEmpty()) {
                            Toast.makeText(requireContext(), "No hay temas para esta materia", Toast.LENGTH_SHORT).show();
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

    private Integer obtenerIdUsuario() { //falta completarlo
        return 1;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Materia materia = listaMaterias.get(position);
        var action = MateriasFragmentDirections
                .actionMateriasFragmentToTemasFragment(materia);

        NavHostFragment.findNavController(this).navigate(action);
    }
}