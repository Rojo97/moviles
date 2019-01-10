/*Victor Rojo Alvarez
* Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListaFragment extends Fragment{

    private static final String TAG = ListaFragment.class.getSimpleName();
    private TextView nombreTextview;
    private String listaNombre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_lista, container, false);
        nombreTextview = view.findViewById(R.id.nombre_lista);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            listaNombre = bundle.getString("LISTA_NOMBRE");
            nombreTextview.setText(listaNombre);

        }
    }
}
