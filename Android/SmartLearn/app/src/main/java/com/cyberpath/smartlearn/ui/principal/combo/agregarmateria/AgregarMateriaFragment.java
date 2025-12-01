package com.cyberpath.smartlearn.ui.principal.combo.agregarmateria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.contenido.Materia;
import com.cyberpath.smartlearn.data.model.relaciones.UsuarioMateria;
import com.cyberpath.smartlearn.data.model.usuario.Usuario;
import com.cyberpath.smartlearn.util.constants.UsuarioCst;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarMateriaFragment extends Fragment {

    private final List<Materia> listaMateriasDisponibles = new ArrayList<>();
    private final List<Materia> listaMateriasUsuario = new ArrayList<>();
    private final List<Materia> listaAllMaterias = new ArrayList<>();
    private final Usuario usuarioActual = UsuarioCst.USUARIO_ACTUAL;
    private ViewPager2 viewPagerMaterias;
    private AdaptadorAgregarMaterias adapterMaterias;
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

        crearCarrusel(view);
        cargarMateriasUsuario(usuarioActual.getId());
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
        List<Materia> materiasDisponiblesParaAgregar = listaAllMaterias.stream()
                .filter(materiaTotal -> listaMateriasUsuario.stream()
                        .noneMatch(materiaUsuario -> materiaUsuario.getId().equals(materiaTotal.getId())))
                .collect(Collectors.toList());

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                listaMateriasDisponibles.clear();
                listaMateriasDisponibles.addAll(materiasDisponiblesParaAgregar);

                adapterMaterias.actualizarListaDuplicada();
                adapterMaterias.notifyDataSetChanged();

                if (materiasDisponiblesParaAgregar.size() > 0) {
                    viewPagerMaterias.setCurrentItem(1, false);
                }
            });
        }
    }

    private void mostrarDialogoInscribir(Materia materia) {
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
        if (usuarioActual.getId() == null) {
            Toast.makeText(getContext(), "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioMateria inscripcion = new UsuarioMateria();
        inscripcion.setIdMateria(idMateria);
        inscripcion.setIdUsuario(usuarioActual.getId());

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
                    adapterMaterias.actualizarLista(new ArrayList<>(listaMateriasDisponibles));
                    viewPagerMaterias.setCurrentItem(1, false);

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


    public void crearCarrusel(View view){
        viewPagerMaterias = view.findViewById(R.id.viewPagerMaterias);
        adapterMaterias = new AdaptadorAgregarMaterias(listaMateriasDisponibles, this::mostrarDialogoInscribir);
        viewPagerMaterias.setAdapter(adapterMaterias);

        viewPagerMaterias.setCurrentItem(1, false);

        viewPagerMaterias.setOffscreenPageLimit(3);

        viewPagerMaterias.setClipToPadding(false);
        viewPagerMaterias.setClipChildren(false);
        viewPagerMaterias.setPadding(80, 0, 80, 0);

        viewPagerMaterias.setPageTransformer(new ViewPager2.PageTransformer() {
            private static final float MIN_SCALE = 0.85f;
            private static final float MIN_ALPHA = 0.5f;

            @Override
            public void transformPage(@NonNull View page, float position) {
                if (position < -1) {
                    page.setAlpha(0f);
                } else if (position <= 1) {
                    float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position) * 0.15f);
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);

                    page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
                } else {
                    page.setAlpha(0f);
                }
            }
        });

        viewPagerMaterias.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private boolean isResetting = false;

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_IDLE && !isResetting) {
                    int position = viewPagerMaterias.getCurrentItem();
                    int realSize = listaMateriasDisponibles.size();

                    if (realSize <= 1) return;

                    if (position == 0) {
                        isResetting = true;
                        viewPagerMaterias.setCurrentItem(realSize, false);
                        viewPagerMaterias.post(() -> {
                            viewPagerMaterias.requestTransform();
                            isResetting = false;
                        });
                    } else if (position == realSize + 1) {
                        isResetting = true;
                        viewPagerMaterias.setCurrentItem(1, false);
                        viewPagerMaterias.post(() -> {
                            viewPagerMaterias.requestTransform();
                            isResetting = false;
                        });
                    }
                }
            }
        });
    }


}