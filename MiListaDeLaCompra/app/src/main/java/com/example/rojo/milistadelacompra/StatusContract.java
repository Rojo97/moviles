/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.net.Uri;
import android.provider.BaseColumns;

public class StatusContract {
    public static final String REMOTEURL = "jdbc:mysql://virtual.lab.inf.uva.es:20064/listaCompra";
    public static final String REMOTEUSER = "root";
    public static final String REMOTEPASS = "";

    public static final String QUERYPARTICIPACION = "SELECT * FROM Participacion P WHERE P.nickUsuario = '";
    //public static final String QUERYPARTICIPACION = "SELECT * FROM Participacion P WHERE P.nickUsuario = '";


    public static final String DB_NAME = "MyLists.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "status";
    public static final String TABLEPARTICIPACION = "Participacion";
    public static final String TABLELISTACOMPRA = "ListaCompra";
    public static final String TABLEELEMENTO = "Elemento";
    public static final String DEFAULT_SORT = ColumnParticipacion.LISTA + " DESC";

    // Constantes del content provider
    // content://com.example.rojo.milistadelacompra.StatusProvider/status
    public static final String AUTHORITY = "com.example.rojo.milistadelacompra.StatusProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLEPARTICIPACION);
    public static final int STATUS_ITEM = 1;
    public static final int STATUS_DIR = 2;

    public class ColumnParticipacion {
        public static final String ID = BaseColumns._ID;
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
    }

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String USER = "user";
        public static final String MESSAGE = "message";
        public static final String CREATED_AT = "created_at";
    }
}