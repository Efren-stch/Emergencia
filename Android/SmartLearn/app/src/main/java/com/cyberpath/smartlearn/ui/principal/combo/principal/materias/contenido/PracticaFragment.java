package com.cyberpath.smartlearn.ui.principal.combo.principal.materias.contenido;

import android.os.Bundle;
import android.util.Log;
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
import com.cyberpath.smartlearn.data.model.ejercicio.Ejercicio;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PracticaFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listViewEjercicios;
    private AdaptadorPractica adaptadorEjercicios;
    private List<Ejercicio> listaEjercicios = new ArrayList<>();
    private Subtema subtema;
    private TextView tvSubtema;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practica, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            subtema = PracticaFragmentArgs.fromBundle(getArguments()).getSubtema();
        }

        tvSubtema = view.findViewById(R.id.tv_subtema);
        tvSubtema.setText(subtema.getNombre());

        listViewEjercicios = view.findViewById(R.id.listViewEjercicios);
        adaptadorEjercicios = new AdaptadorPractica(getContext(), listaEjercicios);
        listViewEjercicios.setAdapter(adaptadorEjercicios);
        listViewEjercicios.setOnItemClickListener(this);

        cargarEjercicios(subtema);
    }

    private void cargarEjercicios(Subtema subtema) {
        if (subtema == null) {
            Toast.makeText(getContext(), "Subtema no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Ejercicio>> call = apiService.getEjerciciosBySubtema(subtema.getId());
        call.enqueue(new Callback<List<Ejercicio>>() {
            @Override
            public void onResponse(Call<List<Ejercicio>> call, Response<List<Ejercicio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ejercicio> ejercicios = response.body();
                    requireActivity().runOnUiThread(() -> {
                        listaEjercicios.clear();
                        listaEjercicios.addAll(ejercicios);
                        adaptadorEjercicios.notifyDataSetChanged();

                        if (ejercicios.isEmpty()) {
                            Toast.makeText(requireContext(), "No hay ejercicios para este subtema", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error al cargar ejercicios", Toast.LENGTH_SHORT).show()

                    );
                }
            }

            @Override
            public void onFailure(Call<List<Ejercicio>> call, Throwable t) {
                getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("PRACTICA ERROR: ", "Error de conexión: " + t.getMessage());
                        }

                );
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Ejercicio ejercicio = listaEjercicios.get(position);
        // Aquí puedes navegar a un fragmento de detalle del ejercicio, por ejemplo:
        // var action = EjerciciosFragmentDirections.actionEjerciciosFragmentToDetalleEjercicioFragment(ejercicio);
        // NavHostFragment.findNavController(this).navigate(action);
        Toast.makeText(getContext(), "Ejercicio seleccionado: " + ejercicio.getNombre(), Toast.LENGTH_SHORT).show();
    }
}