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

public class CarroCompraProvider extends ContentProvider {

    private static final String TAG = CarroCompraProvider.class.getSimpleName();
    private DbHelper dbHelper;
    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(CarroCompraContract.AUTHORITY, CarroCompraContract.TABLELISTACOMPRA, CarroCompraContract.STATUS_DIR_LISTA);
        sURIMatcher.addURI(CarroCompraContract.AUTHORITY, CarroCompraContract.TABLELISTACOMPRA + "/*", CarroCompraContract.STATUS_ITEM_LISTA);
        sURIMatcher.addURI(CarroCompraContract.AUTHORITY, CarroCompraContract.TABLEPARTICIPACION, CarroCompraContract.STATUS_DIR_PARTICIPACION);

        sURIMatcher.addURI(CarroCompraContract.AUTHORITY, CarroCompraContract.TABLELISTACOMPRA + "/*/Participantes", CarroCompraContract.STATUS_DIR_PARTICIPACION_LISTA);
        //En la ruta de la Uri el primer * se refiere al nombre de la ListaCompra y el segundo * al nombre del Usuario
        sURIMatcher.addURI(CarroCompraContract.AUTHORITY, CarroCompraContract.TABLELISTACOMPRA + "/*/Participantes/*", CarroCompraContract.STATUS_ITEM_PARTICIPACION_LISTA);
        sURIMatcher.addURI(CarroCompraContract.AUTHORITY, CarroCompraContract.TABLEELEMENTO, CarroCompraContract.STATUS_DIR_ELEMENTO);
        sURIMatcher.addURI(CarroCompraContract.AUTHORITY, CarroCompraContract.TABLELISTACOMPRA + "/*/Elementos", CarroCompraContract.STATUS_DIR_ELEMENTO_LISTA);
        sURIMatcher.addURI(CarroCompraContract.AUTHORITY, CarroCompraContract.TABLELISTACOMPRA + "/*/Elementos/*", CarroCompraContract.STATUS_ITEM_ELEMENTO);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        Log.d(TAG, "onCreated");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
            String where;
            String table;
            String orderBy = null;
            String id;
            String nombreLista;
            String nombreElemento;
            switch (sURIMatcher.match(uri)) {
                case CarroCompraContract.STATUS_DIR_LISTA:
                    where = selection;
                    table = CarroCompraContract.TABLELISTACOMPRA;
                    orderBy = (TextUtils.isEmpty(sortOrder)) ? CarroCompraContract.DEFAULT_SORT_LISTA : sortOrder;
                    break;
                case CarroCompraContract.STATUS_ITEM_LISTA:
                    id = uri.getLastPathSegment();
                    where = CarroCompraContract.ColumnListaCompra.ID
                            + "= '"
                            + id
                            + "' "
                            + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                    table = CarroCompraContract.TABLELISTACOMPRA;
                    break;
                case CarroCompraContract.STATUS_DIR_PARTICIPACION:
                    where = selection;
                    table = CarroCompraContract.TABLEPARTICIPACION;
                    orderBy = (TextUtils.isEmpty(sortOrder)) ? CarroCompraContract.DEFAULT_SORT_PARTICIPACION : sortOrder;
                    break;
                case CarroCompraContract.STATUS_ITEM_PARTICIPACION_LISTA:
                    String nombreUser = uri.getLastPathSegment();
                    nombreLista = uri.getPathSegments().get(1);

                    where = CarroCompraContract.ColumnParticipacion.LISTA
                            + "= '"
                            + nombreLista
                            + "' and "
                            + CarroCompraContract.ColumnParticipacion.USER
                            + "= '"
                            + nombreUser
                            + "' "
                            + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                    table = CarroCompraContract.TABLEPARTICIPACION;
                    break;
                case CarroCompraContract.STATUS_DIR_ELEMENTO:
                    where = selection;
                    table = CarroCompraContract.TABLEELEMENTO;
                    orderBy = (TextUtils.isEmpty(sortOrder)) ? CarroCompraContract.DEFAULT_SORT_ELEMENTO : sortOrder;
                    break;
                case CarroCompraContract.STATUS_DIR_ELEMENTO_LISTA:
                    nombreLista = uri.getPathSegments().get(1);

                    where = CarroCompraContract.ColumnElemento.IDLISTA
                            + "= '"
                            + nombreLista
                            + "' "
                            + (TextUtils.isEmpty(selection) ? "" : " and ( " + selection + " )");
                    table = CarroCompraContract.TABLEELEMENTO;
                    break;

                default:
                    throw new IllegalArgumentException("uri incorrecta: " + uri);
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(table, projection, where, selectionArgs, null, null,
                    orderBy);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            Log.d(TAG, "registros recuperados de la Db : " + cursor.getCount());
            return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case CarroCompraContract.STATUS_DIR_LISTA:
                Log.d(TAG, "gotType: vnd.android.listacompra.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.listacompra.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            case CarroCompraContract.STATUS_ITEM_LISTA:
                Log.d(TAG, "gotType: vnd.android.listacompra.item/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return
                        "vnd.android.listacompra.item/vnd.com.example.rojo.milistadelacompra.provider.status";
            case CarroCompraContract.STATUS_DIR_PARTICIPACION:
                Log.d(TAG, "gotType: vnd.android.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            case CarroCompraContract.STATUS_ITEM_PARTICIPACION_LISTA:
                Log.d(TAG, "gotType: vnd.android.listacompra.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.listacompra.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            case CarroCompraContract.STATUS_DIR_ELEMENTO:
                Log.d(TAG, "gotType: vnd.android.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.participacion.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            case CarroCompraContract.STATUS_DIR_ELEMENTO_LISTA:
                Log.d(TAG, "gotType: vnd.android.listacompra.elemento.dir/vnd.com.example.rojo.milistadelacompra.provider.carrocompra");
                return "vnd.android.listacompra.elemento.dir/vnd.com.example.rojo.milistadelacompra.provider.status";
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri ret = null;
        String table;

        switch (sURIMatcher.match(uri)){
            case CarroCompraContract.STATUS_DIR_LISTA:
                table = CarroCompraContract.TABLELISTACOMPRA;
                break;
            case CarroCompraContract.STATUS_DIR_PARTICIPACION_LISTA:
                table = CarroCompraContract.TABLEPARTICIPACION;
                break;
            case CarroCompraContract.STATUS_DIR_ELEMENTO_LISTA:
                table = CarroCompraContract.TABLEELEMENTO;
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        Log.d(TAG, " Db LLEGAAAA "+ rowId);
        // ¿Se insertó correctamente?
        if (rowId != -1) {
            // Notificar que los datos para la URI han cambiado
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ret;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        String where;
        switch (sURIMatcher.match(uri)) {
            case CarroCompraContract.STATUS_DIR_LISTA:
                where = s;
                break;
            case CarroCompraContract.STATUS_ITEM_LISTA:
                long id = ContentUris.parseId(uri);
                where = CarroCompraContract.ColumnListaCompra.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(s) ? "" : " and ( " + s + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.delete(CarroCompraContract.TABLE, where, strings);
        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "registros borrados: " + ret);
        return ret;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        String where;
        switch (sURIMatcher.match(uri)) {
            case CarroCompraContract.STATUS_DIR_LISTA:
                where = s;
                break;
            case CarroCompraContract.STATUS_ITEM_LISTA:
                long id = ContentUris.parseId(uri);
                where = CarroCompraContract.ColumnListaCompra.ID
                        + "="
                        + id
                        + (TextUtils.isEmpty(s) ? "" : " and ( " + s + " )");
                break;
            default:
                throw new IllegalArgumentException("uri incorrecta: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int ret = db.update(CarroCompraContract.TABLE, contentValues, where, strings);
        if (ret > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(TAG, "registros actualizados: " + ret);
        return ret;
    }
}
