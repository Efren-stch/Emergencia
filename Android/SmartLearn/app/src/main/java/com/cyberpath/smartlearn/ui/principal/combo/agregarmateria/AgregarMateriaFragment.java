package com.cyberpath.smartlearn.ui.principal.combo.agregarmateria;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.contenido.Materia;
import com.cyberpath.smartlearn.data.model.relaciones.UsuarioMateria;
import com.cyberpath.smartlearn.ui.principal.combo.principal.materias.AdaptadorMaterias;
import com.cyberpath.smartlearn.ui.principal.combo.principal.materias.MateriasFragmentDirections;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarMateriaFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listViewMaterias;
    private AdaptadorMaterias adaptadorMaterias;

    private List<Materia> listaMateriasDisponibles = new ArrayList<>();
    private List<Materia> listaMateriasUsuario = new ArrayList<>();
    private List<Materia> listaAllMaterias = new ArrayList<>();

    private boolean cargandoMateriasUsuario = false;
    private boolean cargandoAllMaterias = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agregar_materia, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listViewMaterias = view.findViewById(R.id.listViewMaterias);
        listViewMaterias.setOnItemClickListener(this);
        adaptadorMaterias = new AdaptadorMaterias(getContext(), listaMateriasDisponibles);
        listViewMaterias.setAdapter(adaptadorMaterias);

        Integer idUsuario = obtenerIdUsuario();

        cargarMateriasUsuario(idUsuario);
        cargarAllMaterias();
    }

    private void cargarMateriasUsuario(Integer idUsuario) {
        cargandoMateriasUsuario = true;
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Materia>> call = apiService.getMateriasByUsuario(idUsuario);

        call.enqueue(new Callback<List<Materia>>() {
            @Override
            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMateriasUsuario.clear();
                    listaMateriasUsuario.addAll(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al cargar tus materias", Toast.LENGTH_SHORT).show();
                }
                cargandoMateriasUsuario = false;
                actualizarListaSiCargasListas();
            }

            @Override
            public void onFailure(Call<List<Materia>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión (materias usuario)", Toast.LENGTH_SHORT).show();
                cargandoMateriasUsuario = false;
                actualizarListaSiCargasListas();
            }
        });
    }

    private void cargarAllMaterias() {
        cargandoAllMaterias = true;
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Materia>> call = apiService.getMaterias();

        call.enqueue(new Callback<List<Materia>>() {
            @Override
            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaAllMaterias.clear();
                    listaAllMaterias.addAll(response.body());
                } else {
                    Toast.makeText(getContext(), "Error al cargar catálogo de materias", Toast.LENGTH_SHORT).show();
                }
                cargandoAllMaterias = false;
                actualizarListaSiCargasListas();
            }

            @Override
            public void onFailure(Call<List<Materia>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión (catálogo)", Toast.LENGTH_SHORT).show();
                cargandoAllMaterias = false;
                actualizarListaSiCargasListas();
            }
        });
    }

    private void actualizarListaSiCargasListas() {
        if (cargandoMateriasUsuario || cargandoAllMaterias) {
            return;
        }
        // Corregido: Filtrar listaAllMaterias en lugar de listaMateriasDisponibles (que está vacía)
        List<Materia> materiasDisponiblesParaAgregar = listaAllMaterias.stream()
                .filter(materiaTotal -> listaMateriasUsuario.stream()
                        .noneMatch(materiaUsuario -> materiaUsuario.getId().equals(materiaTotal.getId())))
                .collect(Collectors.toList());

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                listaMateriasDisponibles.clear();
                listaMateriasDisponibles.addAll(materiasDisponiblesParaAgregar);
                adaptadorMaterias.notifyDataSetChanged();

                if (materiasDisponiblesParaAgregar.isEmpty()) {
                    // Agregado: Informar al usuario si no hay materias disponibles
                    Toast.makeText(getContext(), "No hay materias disponibles para inscribir", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Integer obtenerIdUsuario() {
        return 1; // PENDIENTE: Implementar obtención dinámica (e.g., desde SharedPreferences o bundle)
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mostrarDialogoInscribir(position);
    }

    private void mostrarDialogoInscribir(int position) {
        Materia materia = listaMateriasDisponibles.get(position); // Corregido: Acceder directamente a la lista correcta
        View vista = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_aceptar_cancelar, null);

        TextView tvMensaje = vista.findViewById(R.id.tvMensaje);
        tvMensaje.setText("¿Deseas inscribirte en: " + materia.getNombre() + "?");

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(vista)
                .setCancelable(true)
                .show();

        vista.findViewById(R.id.btnAceptar).setOnClickListener(v -> {
            inscribirMateria(materia);
            dialog.dismiss();
        });

        vista.findViewById(R.id.btnCancelar).setOnClickListener(v -> dialog.dismiss());
    }

    private void inscribirMateria(Materia materia) {
        Integer idMateria = materia.getId();
        Integer idUsuario = obtenerIdUsuario();
        if (idUsuario == null) {
            Toast.makeText(getContext(), "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioMateria inscripcion = new UsuarioMateria();
        inscripcion.setIdMateria(idMateria);
        inscripcion.setIdUsuario(idUsuario);

        Toast.makeText(getContext(), "Inscribiendo...", Toast.LENGTH_SHORT).show();

        ApiService api = RetrofitClient.getApiService();
        Call<UsuarioMateria> call = api.save(inscripcion);

        call.enqueue(new Callback<UsuarioMateria>() {
            @Override
            public void onResponse(Call<UsuarioMateria> call, Response<UsuarioMateria> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "¡Te has inscrito correctamente a " + materia.getNombre() + "!",
                            Toast.LENGTH_LONG).show();

                    listaMateriasDisponibles.remove(materia);
                    adaptadorMaterias.notifyDataSetChanged();

                    listaMateriasUsuario.add(materia);
                } else {
                    Toast.makeText(getContext(), "Error al inscribirte. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioMateria> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}