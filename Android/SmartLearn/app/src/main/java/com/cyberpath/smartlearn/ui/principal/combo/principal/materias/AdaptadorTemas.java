package com.cyberpath.smartlearn.ui.principal.combo.principal.materias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cyberpath.smartlearn.R;
import com.cyberpath.smartlearn.data.model.contenido.Tema;

import java.util.List;

public class AdaptadorTemas extends BaseAdapter {
    Context contexto;
    List<Tema> listaTemas;
    LayoutInflater inflater;

    public AdaptadorTemas(Context contexto, List<Tema> listaTemas) {
        this.contexto = contexto;
        this.listaTemas = listaTemas;
        this.inflater = LayoutInflater.from(contexto);
    }

    public void actualizarLista(List<Tema> nuevosTemas) {
        this.listaTemas = nuevosTemas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listaTemas != null ? listaTemas.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return listaTemas.get(position);
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
        ImageView imagenTema = convertView.findViewById(R.id.imageicon);

        Tema tema = listaTemas.get(position);
        textView.setText(tema.getNombre());
        imagenTema.setImageResource(R.drawable.ic_launcher_foreground);

        return convertView;
    }
}