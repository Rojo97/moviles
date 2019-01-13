/*Victor Rojo Alvarez
 * Ismael Perez Martin*/
package com.example.rojo.milistadelacompra;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author ismpere
 * @author vicrojo
 * Implementación del ContentProvider para el acceso a los recursos de la base de datos local
 * de listas de la compra, participantes de las listas y elementos de las listas
 */
public class ListaCompraProvider extends ContentProvider {

    private static final String TAG = ListaCompraProvider.class.getSimpleName();
    private DbHelper dbHelper;
    private static final UriMatcher sURIMatcher;

    /**
     * Se añaden las Uri estáticas para el acceso a los recursos de la BD
     */
    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(ListaCompraContract.AUTHORITY, ListaCompraContract.TABLELISTACOMPRA, ListaCompraContract.STATUS_DIR_LISTA);
        sURIMatcher.addURI(ListaCompraContract.AUTHORITY, ListaCompraContract.TABLELISTACOMPRA + "/*", ListaCompraContract.STATUS_ITEM_LISTA);
        sURIMatcher.addURI(ListaCompraContract.AUTHORITY, ListaCompraContract.TABLEPARTICIPACION, ListaCompraContract.STATUS_DIR_PARTICIPACION);

        sURIMatcher.addURI(ListaCompraContract.AUTHORITY, ListaCompraContract.TABLELISTACOMPRA + "/*/Participantes", ListaCompraContract.STATUS_DIR_PARTICIPACION_LISTA);
        //En la ruta de la Uri el primer * se refiere al nombre de la ListaCompra y el segundo * al nombre del Usuario
        sURIMatcher.addURI(ListaCompraContract.AUTHORITY, ListaCompraContract.TABLELISTACOMPRA + "/*/Participantes/*", ListaCompraContract.STATUS_ITEM_PARTICIPACION_LISTA);
        sURIMatcher.addURI(ListaCompraContract.AUTHORITY, ListaCompraContract.TABLEELEMENTO, ListaCompraContract.STATUS_DIR_ELEMENTO);
        sURIMatcher.addURI(ListaCompraContract.AUTHORITY, ListaCompraContract.TABLELISTACOMPRA + "/*/Elementos", ListaCompraContract.STATUS_DIR_ELEMENTO_LISTA);
        sURIMatcher.addURI(ListaCompraContract.AUTHORITY, ListaCompraContract.TABLELISTACOMPRA + "/*/Elementos/*", ListaCompraContract.STATUS_ITEM_ELEMENTO_LISTA);
    }

    /**
     * Imlementación del método onCreate del ContentProvider
     * @return
     */
    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        Log.d(TAG, "onCreated");
        return true;
    }

    /**
     * Implementación del método para consultas select a la base de datos local mediante una uri de acceso al recurso
     * @param uri al recurso
     * @param projection permisos
     * @param selection clausula where de la ocnsulta
     * @param selectionArgs parametros de la clausula where
     * @param sortOrder ordenacion de la consulta
     * @return Cursor a los resultados de la consulta
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String where;
        String table;
        String orderBy = null;
        String id;
        String nombreLista;
        String nombreElemento;

        //Se hace un match a la uri para elegir que consulta realizar a la base de datos local
        switch (sURIMatcher.match(uri)) {
            case ListaCompraContract.STATUS_DIR_LISTA:
                where = selection;
                table = ListaCompraContract.TABLELISTACOMPRA;
                orderBy = (TextUtils.isEmpty(sortOrder)) ? ListaCompraContract.DEFAULT_SORT_LISTA : sortOrder;
                break;
            case ListaCompraContract.STATUS_ITEM_LISTA:
                id = uri.getLastPathSegment();
                where = ListaCompraContract.ColumnListaCompra.ID
                        + "= '"
                        + id
                        + "' "
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                table = ListaCompraContract.TABLELISTACOMPRA;
                break;
            case ListaCompraContract.STATUS_DIR_PARTICIPACION:
                where = selection;
                table = ListaCompraContract.TABLEPARTICIPACION;
                orderBy = (TextUtils.isEmpty(sortOrder)) ? ListaCompraContract.DEFAULT_SORT_PARTICIPACION : sortOrder;
                break;
            case ListaCompraContract.STATUS_ITEM_PARTICIPACION_LISTA:
                String nombreUser = uri.getLastPathSegment();
                nombreLista = uri.getPathSegments().get(1);

                where = ListaCompraContract.ColumnParticipacion.LISTA
                        + "= '"
                        + nombreLista
                        + "' and "
                        + ListaCompraContract.ColumnParticipacion.USER
                        + "= '"
                        + nombreUser
                        + "' "
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                table = ListaCompraContract.TABLEPARTICIPACION;
                break;
            case ListaCompraContract.STATUS_DIR_ELEMENTO:
                where = selection;
                table = ListaCompraContract.TABLEELEMENTO;
                orderBy = (TextUtils.isEmpty(sortOrder)) ? ListaCompraContract.DEFAULT_SORT_ELEMENTO : sortOrder;
                break;
            case ListaCompraContract.STATUS_DIR_ELEMENTO_LISTA:
                nombreLista = uri.getPathSegments().get(1);

                where = ListaCompraContract.ColumnElemento.IDLISTA
                        + "= '"
                        + nombreLista
                        + "' "
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                table = ListaCompraContract.TABLEELEMENTO;
                orderBy = (TextUtils.isEmpty(sortOrder)) ? ListaCompraContract.DEFAULT_SORT_ELEMENTO : sortOrder;
                break;

            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }

        //Se ejecuta la consulta elegida en el switch obteniendo una instancia leíble de la base de datos local
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(table, projection, where, selectionArgs, null, null,
                orderBy);

        //Se notifica a los observadores del cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(TAG, "registros recuperados de la Db : " + cursor.getCount());
        db.close();
        return cursor;
    }

    /**
     * Devuelve el tipo de la uri asociada
     * @param uri a consultar
     * @return tipo de la uri
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case ListaCompraContract.STATUS_DIR_LISTA:
                Log.d(TAG, "gotType: vnd.android.listacompra.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.listacompra.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            case ListaCompraContract.STATUS_ITEM_LISTA:
                Log.d(TAG, "gotType: vnd.android.listacompra.item/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return
                        "vnd.android.listacompra.item/vnd.com.example.rojo.milistadelacompra.provider.status";
            case ListaCompraContract.STATUS_DIR_PARTICIPACION:
                Log.d(TAG, "gotType: vnd.android.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            case ListaCompraContract.STATUS_ITEM_PARTICIPACION_LISTA:
                Log.d(TAG, "gotType: vnd.android.listacompra.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.listacompra.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            case ListaCompraContract.STATUS_DIR_ELEMENTO:
                Log.d(TAG, "gotType: vnd.android.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            case ListaCompraContract.STATUS_DIR_ELEMENTO_LISTA:
                Log.d(TAG, "gotType: vnd.android.listacompra.elemento.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.listacompra.elemento.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
    }

    /**
     * Implementación de las consultas de tipo insert a la base de datos local
     * @param uri al recurso a insertar
     * @param contentValues valores del registro a insertar en la base de datos
     * @return uri actualizada con el nuevo elemento insertado
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri ret = null;
        String table;

        //Se hace un match para elegir que tipo de insert realizar y donde
        switch (sURIMatcher.match(uri)) {
            case ListaCompraContract.STATUS_DIR_LISTA:
                table = ListaCompraContract.TABLELISTACOMPRA;
                break;
            case ListaCompraContract.STATUS_DIR_PARTICIPACION_LISTA:
                table = ListaCompraContract.TABLEPARTICIPACION;
                break;
            case ListaCompraContract.STATUS_DIR_ELEMENTO_LISTA:
                table = ListaCompraContract.TABLEELEMENTO;
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }

        //Se obtiene una instancia escribible de la base de datos y se inserta la fila en el recurso indicado
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        // ¿Se insertó correctamente?
        if (rowId != -1) {
            // Notificar que los datos para la URI han cambiado
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        return ret;
    }

    /**
     * Implementación de las consultas de tipo delete a la base de datos local
     * @param uri al recurso a eliminar
     * @param selection clausula where de la consulta
     * @param selectionArgs parametros de la clausula where
     * @return numero de filas de la base de datos local eliminadas
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String where;

        //Se realiza un march para saber que tipo de delete hacer y en que tabla
        switch (sURIMatcher.match(uri)) {
            case ListaCompraContract.STATUS_DIR_LISTA:
                where = selection;
                break;
            case ListaCompraContract.STATUS_ITEM_LISTA:
                long id = ContentUris.parseId(uri);
                where = ListaCompraContract.ColumnListaCompra.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }

        //Se obtiene una instancia escribible de la base de datos y se ejecuta la consulta delete
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.delete(ListaCompraContract.TABLE, where, selectionArgs);

        //Se comprueba que se ha eliminado alguna fila y se notifica
        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "registros borrados: " + ret);
        db.close();
        return ret;
    }

    /**
     * Implementación de las consultas de tipo update a la base de datos local
     * @param uri al recurso a actualizar
     * @param contentValues valores a modificar de la fila
     * @param selection clausula where de la consulta
     * @param selectionArgs argumentos de la clausula where
     * @return
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        String where;
        String table;
        String id;
        String nombreLista;

        //Se realiza un match para saber que update realizar y en que tabla se debe hacer
        switch (sURIMatcher.match(uri)) {
            case ListaCompraContract.STATUS_DIR_LISTA:
                where = selection;
                table = ListaCompraContract.TABLELISTACOMPRA;
                break;
            case ListaCompraContract.STATUS_ITEM_LISTA:
                id = uri.getLastPathSegment();
                where = ListaCompraContract.ColumnListaCompra.ID
                        + "= '"
                        + id
                        + "' "
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                table = ListaCompraContract.TABLELISTACOMPRA;
                break;
            case ListaCompraContract.STATUS_ITEM_ELEMENTO_LISTA:
                nombreLista = uri.getPathSegments().get(1);

                where = ListaCompraContract.ColumnElemento.IDLISTA
                        + "= '"
                        + nombreLista
                        + "' "
                        + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                table = ListaCompraContract.TABLEELEMENTO;
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }

        //Se obtiene una instancia escribible de la base de datos local y se realiza el update
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.update(table, contentValues, where, selectionArgs);
        //Si se ha modificado alguna columna de la base de datos se notifica
        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "registros actualizados: " + ret);
        db.close();
        return ret;
    }
}
