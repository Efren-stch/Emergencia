package com.cyberpath.smartlearn.ui.principal.combo.principal.materias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.model.contenido.Subtema;
import com.cyberpath.smartlearn.data.model.contenido.Subtema;

import java.util.List;

public class AdaptadorSubtemas extends BaseAdapter {
    Context contexto;
    List<Subtema> listaSubtemas;
    LayoutInflater inflater;

    public AdaptadorSubtemas(Context contexto, List<Subtema> listaSubtemas) {
        this.contexto = contexto;
        this.listaSubtemas = listaSubtemas;
        this.inflater = LayoutInflater.from(contexto);
    }

    public void actualizarLista(List<Subtema> nuevosSubtemas) {
        this.listaSubtemas = nuevosSubtemas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listaSubtemas != null ? listaSubtemas.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return listaSubtemas.get(position);
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
        ImageView imagenSubtema = convertView.findViewById(R.id.imageicon);

        Subtema tema = listaSubtemas.get(position);
        textView.setText(tema.getNombre());
        imagenSubtema.setImageResource(R.drawable.ic_launcher_foreground);

        return convertView;
    }
}