package com.cyberpath.smartlearn.ui.principal.combo.principal.materias;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.contenido.Materia;
import com.cyberpath.smartlearn.data.model.contenido.Subtema;
import com.cyberpath.smartlearn.data.model.contenido.Tema;
import com.cyberpath.smartlearn.data.model.usuario.UltimaConexion;
import com.cyberpath.smartlearn.preferences.PreferencesManager;
import com.cyberpath.smartlearn.util.constants.UsuarioCst;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class SubtemasFragment extends Fragment {


    private ViewPager2 viewPagerSubtemas;
    private AdaptadorSubtemas adapterSubtemas;
    private final List<Subtema> listaSubtemas = new ArrayList<>();
    private TextView textoTema;
    private Tema tema;

    private int ultimaPosicionReal = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subtemas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textoTema = view.findViewById(R.id.tvNombreTema);
        crearCarrusel(view);

        tema = SubtemasFragmentArgs.fromBundle(getArguments()).getTema();
        int temaId = tema.getId();
        textoTema.setText(tema.getNombre());

        cargarSubtemas(temaId);
    }

    private void cargarSubtemas(int temaId) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<Subtema>> call = apiService.getSubtemasByTema(temaId);

        call.enqueue(new Callback<List<Subtema>>() {
            @Override
            public void onResponse(Call<List<Subtema>> call, Response<List<Subtema>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Subtema> subtemas = response.body();
                    requireActivity().runOnUiThread(() -> {
                        listaSubtemas.clear();
                        listaSubtemas.addAll(subtemas);
                        adapterSubtemas.actualizarLista(new ArrayList<>(listaSubtemas));
                        adapterSubtemas.notifyDataSetChanged();

                        if (subtemas.size() > 0) {
                            viewPagerSubtemas.setCurrentItem(1, false);
                        }

                        if (subtemas.isEmpty()) {
                            Toast.makeText(requireContext(), "No hay subtemas disponibles", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mostrarError("Error al cargar subtemas");
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

    private void onSubtemaClick(Subtema subtema) {
        guardarUltimaConexion(subtema);
        Toast.makeText(requireContext(),
                "Seleccionaste: " + subtema.getNombre(), Toast.LENGTH_LONG).show();
    }

    public void crearCarrusel(View view) {
        viewPagerSubtemas = view.findViewById(R.id.viewPagerSubtemas);
        adapterSubtemas = new AdaptadorSubtemas(listaSubtemas, this::onSubtemaClick);
        viewPagerSubtemas.setAdapter(adapterSubtemas);

        viewPagerSubtemas.setCurrentItem(1, false);
        viewPagerSubtemas.setOffscreenPageLimit(3);
        viewPagerSubtemas.setClipToPadding(false);
        viewPagerSubtemas.setClipChildren(false);
        viewPagerSubtemas.setPadding(80, 0, 80, 0);

        viewPagerSubtemas.setPageTransformer((page, position) -> {
            float scaleFactor = Math.max(0.85f, 1 - Math.abs(position) * 0.15f);
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setAlpha(0.5f + (scaleFactor - 0.85f) / (1 - 0.85f) * 0.5f);
        });

        viewPagerSubtemas.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private boolean isResetting = false;

            @Override
            public void onPageSelected(int position) {
                int realSize = listaSubtemas.size();
                if (realSize > 0) {
                    ultimaPosicionReal = (position - 1 + realSize) % realSize;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_IDLE && !isResetting) {
                    int position = viewPagerSubtemas.getCurrentItem();
                    int realSize = listaSubtemas.size();

                    if (realSize <= 1) return;

                    if (position == 0) {
                        isResetting = true;
                        viewPagerSubtemas.setCurrentItem(realSize, false);
                        viewPagerSubtemas.post(() -> {
                            viewPagerSubtemas.requestTransform();
                            isResetting = false;
                        });
                    } else if (position == realSize + 1) {
                        isResetting = true;
                        viewPagerSubtemas.setCurrentItem(1, false);
                        viewPagerSubtemas.post(() -> {
                            viewPagerSubtemas.requestTransform();
                            isResetting = false;
                        });
                    }
                }
            }
        });
    }

    public void guardarUltimaConexion(Subtema subtema) {
        UltimaConexion ultimaConexion = new UltimaConexion();

        ApiService apiService = RetrofitClient.getApiService();
        Calendar tiempo = Calendar.getInstance();
        SimpleDateFormat formatoTiempo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fecha = formatoTiempo.format(tiempo.getTime());

        Integer idUsuario = UsuarioCst.USUARIO_ACTUAL.getId();

        ultimaConexion.setUltimaConexion(fecha);
        ultimaConexion.setIdSubtema(subtema.getId());

        Call<UltimaConexion> call = apiService.update(idUsuario, ultimaConexion);
        call.enqueue(new Callback<UltimaConexion>() {
            @Override
            public void onResponse(Call<UltimaConexion> call, Response<UltimaConexion> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UltimaConexion ultimaConexion = response.body();
                    PreferencesManager.setIdSubtemaUltimaConexion(requireContext(), ultimaConexion.getIdSubtema());

                }
            }

            @Override
            public void onFailure(Call<UltimaConexion> call, Throwable t) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}