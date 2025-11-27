package com.cyberpath.smartlearn.ui.principal.combo.accesibilidad;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.ui.principal.combo.principal.materias.MateriasFragment;

public class AccesibilidadFragment extends Fragment {
    Button btn_regresar_a_inicio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accesibilidad, container, false);
        btn_regresar_a_inicio = view.findViewById(R.id.btn_regresar_inicio);
        btn_regresar_a_inicio.setOnClickListener(v -> {
            MateriasFragment inicioFragment = new MateriasFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, inicioFragment)
                    .commit();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}