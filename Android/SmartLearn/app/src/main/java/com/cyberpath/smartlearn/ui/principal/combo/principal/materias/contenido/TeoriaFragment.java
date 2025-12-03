package com.cyberpath.smartlearn.ui.principal.combo.principal.materias.contenido;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.contenido.Subtema;
import com.cyberpath.smartlearn.data.model.contenido.Tema;
import com.cyberpath.smartlearn.data.model.contenido.Teoria;
import com.cyberpath.smartlearn.ui.principal.combo.principal.materias.SubtemasFragmentArgs;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeoriaFragment extends Fragment {
    TextView tvTituloSubtema, tvContenidoTeoria;
    Subtema subtema;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teoria, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subtema = TeoriaFragmentArgs.fromBundle(getArguments()).getSubtema();
        tvTituloSubtema = view.findViewById(R.id.tv_titulo_subtema);
        tvContenidoTeoria = view.findViewById(R.id.tv_contenido_teoria);

        tvTituloSubtema.setText(subtema.getNombre());
        asignarTeoria();
    }

    private void asignarTeoria() {
        ApiService apiService = RetrofitClient.getApiService();
        Call<Teoria> call = apiService.getTeoriaById(subtema.getId());
        call.enqueue(new Callback<Teoria>() {
            @Override
            public void onResponse(Call<Teoria> call, Response<Teoria> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Teoria teoria = response.body();
                    tvContenidoTeoria.setText(teoria.getContenido());
                } else {
                    Toast.makeText(requireContext(), "Error al cargar la teoría", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Teoria> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}