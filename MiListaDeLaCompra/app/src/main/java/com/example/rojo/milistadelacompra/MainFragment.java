/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainFragment extends Fragment{

    private static final String TAG = MainFragment.class.getSimpleName();
    private SimpleCursorAdapter mAdapter;
    private static final String[] FROM = {StatusContract.Column.USER, StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT};
    private static final int LOADER_ID = 42;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int botones = 5;
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        LinearLayout layout = view.findViewById(R.id.my_lists_buttons);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < botones; i++){
            Button boton = new Button(this.getActivity());
            boton.setLayoutParams(layoutParams);
            boton.setText("Soy el boton" + i);
            boton.setId(i);
            layout.addView(boton);
        }
        return view;

    }
}