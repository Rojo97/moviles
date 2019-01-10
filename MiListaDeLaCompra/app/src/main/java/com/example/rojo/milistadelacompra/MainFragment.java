/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;

public class MainFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = MainFragment.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {StatusContract.Column.USER, StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT};
    private static final int LOADER_ID = 42;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int botones = 5;
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        LinearLayout layout = view.findViewById(R.id.my_lists_buttons);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < botones; i++){
            Button boton = new Button(this.getActivity());
            boton.setLayoutParams(layoutParams);
            boton.setText("Aqui va la lista " + i);
            boton.setId(i);
            boton.setOnClickListener(this);
            layout.addView(boton);

        }
        return view;

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        Log.d(TAG,"OnClicked " + i);
        Intent intentLista = new Intent(view.getContext(), ListaActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("PULSADO", i);
        intentLista.putExtras(bundle);
        startActivity(intentLista);
    }
}