package com.cyberpath.smartlearn.ui.principal.combo.agregarmateria;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.model.contenido.Materia;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorAgregarMaterias extends RecyclerView.Adapter<AdaptadorAgregarMaterias.MateriaViewHolder> {

    private List<Materia> listaMateriasOriginal;
    private List<Materia> listaMateriasDuplicada;
    private OnMateriaClickListener listener;

    public interface OnMateriaClickListener {
        void onMateriaClick(Materia materia);
    }

    public AdaptadorAgregarMaterias(List<Materia> listaMaterias, OnMateriaClickListener listener) {
        this.listaMateriasOriginal = listaMaterias;
        this.listener = listener;
        actualizarListaDuplicada();
    }

    public void actualizarListaDuplicada() {
        listaMateriasDuplicada = new ArrayList<>();
        if (listaMateriasOriginal == null || listaMateriasOriginal.isEmpty()) return;

        int size = listaMateriasOriginal.size();
        if (size == 1) {
            listaMateriasDuplicada.addAll(listaMateriasOriginal);
            return;
        }

        listaMateriasDuplicada.add(listaMateriasOriginal.get(size - 1));
        listaMateriasDuplicada.addAll(listaMateriasOriginal);
        listaMateriasDuplicada.add(listaMateriasOriginal.get(0));
    }


    public void actualizarLista(List<Materia> nuevasMaterias) {
        this.listaMateriasOriginal = nuevasMaterias;
        actualizarListaDuplicada();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_element, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        int realPosition = (position - 1 + listaMateriasOriginal.size()) % listaMateriasOriginal.size();
        Materia materia = listaMateriasOriginal.get(realPosition);

        holder.tvNombre.setText(materia.getNombre());
        holder.tvDescripcion.setText(materia.getDescripcion() != null ? materia.getDescripcion() : "Descripción no disponible");
        holder.imageMateria.setImageResource(R.drawable.ic_launcher_foreground);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMateriaClick(materia);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaMateriasDuplicada != null ? listaMateriasDuplicada.size() : 0;
    }

    public static class MateriaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMateria;
        TextView tvNombre, tvDescripcion;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMateria = itemView.findViewById(R.id.imageMateria);
            tvNombre = itemView.findViewById(R.id.tvNombreMateria);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionMateria);
        }
    }
}