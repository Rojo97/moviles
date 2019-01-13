/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.net.Uri;
import android.provider.BaseColumns;

public class CarroCompraContract {
    public static final String REMOTEURL = "jdbc:mysql://virtual.lab.inf.uva.es:20064/listaCompra";
    public static final String REMOTEUSER = "root";
    public static final String REMOTEPASS = "";


    public static final String DB_NAME = "MyLists.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "status";
    public static final String TABLEPARTICIPACION = "Participacion";
    public static final String TABLELISTACOMPRA = "ListaCompra";
    public static final String TABLEELEMENTO = "Elemento";
    public static final String DEFAULT_SORT_LISTA = ColumnListaCompra.ID + " DESC";
    public static final String DEFAULT_SORT_PARTICIPACION = ColumnParticipacion.LISTA + " DESC";
    public static final String DEFAULT_SORT_ELEMENTO = ColumnElemento.ID + " DESC";

    // Constantes del content provider
    // content://com.example.rojo.milistadelacompra.CarroCompraProvider/status
    public static final String AUTHORITY = "com.example.rojo.milistadelacompra.CarroCompraProvider";
    public static final Uri CONTENT_URI_LISTA = Uri.parse("content://" + AUTHORITY + "/" + TABLELISTACOMPRA);
    public static final Uri CONTENT_URI_PARTICIPACION = Uri.parse("content://" + AUTHORITY + "/" + TABLEPARTICIPACION);
    public static final Uri CONTENT_URI_ELEMENTO = Uri.parse("content://" + AUTHORITY + "/" + TABLEELEMENTO);
    public static final int STATUS_ITEM_LISTA = 1;
    public static final int STATUS_DIR_LISTA = 2;
    public static final int STATUS_ITEM_PARTICIPACION_LISTA= 3;
    public static final int STATUS_DIR_PARTICIPACION = 4;
    public static final int STATUS_DIR_PARTICIPACION_LISTA= 5;
    public static final int STATUS_ITEM_ELEMENTO_LISTA = 6;
    public static final int STATUS_DIR_ELEMENTO = 7;
    public static final int STATUS_DIR_ELEMENTO_LISTA = 8;

    public class ColumnParticipacion {
        public static final String USER = "nickUsuario";
        public static final String LISTA = "nombreLista";
    }

    public class ColumnListaCompra {
        public static final String ID = "nombre";
        public static final String USER = "nickUsuario";
        public static final String STATUS = "estado";
    }

    public class ColumnElemento {
        public static final String ID = "nombre";
        public static final String QUANTITY = "cantidad";
        public static final String PRICE = "precioUnidad";
        public static final String IDLISTA = "nombreLista";
        public static final String STATUS = "estado";
        public static final String REMOVED = "eliminado";
    }

}