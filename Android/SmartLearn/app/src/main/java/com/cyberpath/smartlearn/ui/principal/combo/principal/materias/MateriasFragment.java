package com.cyberpath.smartlearn.ui.principal.combo.principal.materias;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.api.ApiService;
import com.cyberpath.smartlearn.data.api.RetrofitClient;
import com.cyberpath.smartlearn.data.model.contenido.Materia;
import com.cyberpath.smartlearn.data.model.contenido.Subtema;
import com.cyberpath.smartlearn.data.model.contenido.Tema;
import com.cyberpath.smartlearn.data.model.usuario.Usuario;
import com.cyberpath.smartlearn.preferences.PreferencesManager;
import com.cyberpath.smartlearn.util.constants.UsuarioCst;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MateriasFragment extends Fragment {

    private final List<Materia> listaMaterias = new ArrayList<>();
    private final Usuario usuarioActual = UsuarioCst.USUARIO_ACTUAL;
    private ViewPager2 viewPagerMaterias;
    private AdaptadorMaterias adapterMaterias;
    private TextView tvUltimoSubtema;
    private LinearLayout btnUltimoSubtema;
    private int ultimaPosicionReal = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_materias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });
        crearCarrusel(view);
        crearUltimoSubtema(view);
        cargarMaterias(usuarioActual.getId());
    }

    public void crearUltimoSubtema(View view) {
        tvUltimoSubtema = view.findViewById(R.id.tv_ultimo_subtema);
        btnUltimoSubtema = view.findViewById(R.id.btn_ultimo_subtema);

        int idUltimoSubtema = PreferencesManager.getIdSubtemaUltimaConexion(requireContext());
        if (idUltimoSubtema == -1) {
            tvUltimoSubtema.setText("Es tu primera vez, no tienes un historial");
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<Subtema> call = apiService.getSubtemaById(idUltimoSubtema);
        call.enqueue(new Callback<Subtema>() {
            @Override
            public void onResponse(Call<Subtema> call, Response<Subtema> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Subtema subtema = response.body();
                    tvUltimoSubtema.setText(subtema.getNombre());
                    btnUltimoSubtema.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            navegarUltimoSubtema(subtema);
                        }
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        tvUltimoSubtema.setText("Error al cargar el subtema");
                    });
                }
            }

            @Override
            public void onFailure(Call<Subtema> call, Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    tvUltimoSubtema.setText("Error de conexión, intenta más tarde");
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

        });

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
                        adapterMaterias.actualizarLista(new ArrayList<>(listaMaterias));

                        if (materias.isEmpty()) {
                            Toast.makeText(requireContext(), "No tienes materias inscritas", Toast.LENGTH_LONG).show();
                            return;
                        }

                        int posicionMostrar = ultimaPosicionReal + 1;
                        viewPagerMaterias.setCurrentItem(posicionMostrar, false);

                        viewPagerMaterias.post(() -> viewPagerMaterias.requestTransform());
                    });
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

    private void onMateriaClick(Materia materia) {
        var action = MateriasFragmentDirections.actionMateriasFragmentToTemasFragment(materia);
        NavHostFragment.findNavController(this).navigate(action);
    }

    private void crearCarrusel(View view) {
        viewPagerMaterias = view.findViewById(R.id.viewPagerMaterias);
        adapterMaterias = new AdaptadorMaterias(listaMaterias, this::onMateriaClick);
        viewPagerMaterias.setAdapter(adapterMaterias);

        viewPagerMaterias.setOffscreenPageLimit(3);
        viewPagerMaterias.setClipToPadding(false);
        viewPagerMaterias.setClipChildren(false);
        viewPagerMaterias.setPadding(80, 0, 80, 0);

        viewPagerMaterias.setPageTransformer((page, position) -> {
            float scaleFactor = Math.max(0.85f, 1 - Math.abs(position) * 0.15f);
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setAlpha(0.5f + (scaleFactor - 0.85f) / (1 - 0.85f) * 0.5f);
        });

        viewPagerMaterias.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            private boolean isResetting = false;

            @Override
            public void onPageSelected(int position) {
                int realSize = listaMaterias.size();
                if (realSize > 0) {
                    ultimaPosicionReal = (position - 1 + realSize) % realSize;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_IDLE && !isResetting) {
                    int position = viewPagerMaterias.getCurrentItem();
                    int realSize = listaMaterias.size();

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

    private void navegarUltimoSubtema(Subtema subtema) {
        View vista = LayoutInflater.from(requireContext()).inflate(R.layout.dialogo_teoria_practica, null);
        TextView tvMensaje = vista.findViewById(R.id.tv_titulo_subtema);
        tvMensaje.setText(subtema.getNombre());

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(vista)
                .setCancelable(true)
                .show();

        vista.findViewById(R.id.btn_teoria).setOnClickListener(v -> {
            var action = MateriasFragmentDirections.actionMateriasFragmentToTeoriaFragment(subtema);
            NavHostFragment.findNavController(this).navigate(action);

            dialog.dismiss();
        });

        vista.findViewById(R.id.btn_practica).setOnClickListener(v -> {
            var action = MateriasFragmentDirections.actionMateriasFragmentToPracticaFragment(subtema);
            NavHostFragment.findNavController(this).navigate(action);

            dialog.dismiss();
        });

        vista.findViewById(R.id.btn_cancelar).setOnClickListener(v -> dialog.dismiss());
    }
}