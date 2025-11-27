package com.cyberpath.smartlearn.ui.principal.combo.principal.materias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.model.contenido.Materia;

import java.util.List;

public class AdaptadorMaterias extends BaseAdapter {
    Context contexto;
    List<Materia> listaMaterias;
    LayoutInflater inflater;

    public AdaptadorMaterias(Context contexto, List<Materia> listaMaterias) {
        this.contexto = contexto;
        this.listaMaterias = listaMaterias;
        this.inflater = LayoutInflater.from(contexto);
    }

    public void actualizarLista(List<Materia> nuevasMaterias) {
        this.listaMaterias = nuevasMaterias;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listaMaterias != null ? listaMaterias.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return listaMaterias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_element, null);
        }
        TextView textView = convertView.findViewById(R.id.textview);
        ImageView imagenMateria = convertView.findViewById(R.id.imageicon);

        Materia materia = listaMaterias.get(position);
        textView.setText(materia.getNombre());
        imagenMateria.setImageResource(R.drawable.ic_launcher_foreground);

        return convertView;
    }
}