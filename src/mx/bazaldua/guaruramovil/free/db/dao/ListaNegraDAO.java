package mx.bazaldua.guaruramovil.free.db.dao;

import java.util.ArrayList;
import java.util.List;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.db.helper.DBHelper;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.ContactUtils;
import mx.bazaldua.guaruramovil.free.util.DBUtil;
import mx.bazaldua.guaruramovil.free.vo.ContactVO;
import mx.bazaldua.guaruramovil.free.vo.ListaNegraVO;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

public class ListaNegraDAO extends DAO {
	/**
	 * 
	 * @param context
	 */
	public ListaNegraDAO(Context context) {
		super(context);
	}
	
	/**
	 * 
	 * @param context
	 */
	public ListaNegraDAO(SQLiteDatabase db, Context context) {
		super(db, context);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public List<ListaNegraVO> getListListasNegras() {
		List<ListaNegraVO> lstFElementos = null;
		ListaNegraVO vo                  = null;
		ContactVO contactVO              = null;
		String telefono                  = null;
		Cursor cur                       = getDbConnection().query(DBHelper.TABLE_LISTA_NEGRA, null, null, null, null, null, "id");

		if ( cur.getCount() > 0 ) {
			lstFElementos = new ArrayList<ListaNegraVO>();
			while ( cur.moveToNext() ) {
				telefono = cur.getString( cur.getColumnIndex("telefono") );
				contactVO = ContactUtils.getContactByPhone(telefono, context.getContentResolver());
				
				if ( contactVO == null ) contactVO = ContactUtils.getContactByPhoneSIM(telefono, context.getContentResolver());
				
				if ( contactVO != null ) {
					vo = new ListaNegraVO();
					vo.setId( contactVO.getId() );
					vo.setNombre( contactVO.getNombre() );
					vo.setFoto( contactVO.getFoto() );
					vo.setTelefono( telefono );
					vo.setDescTipoTelPersonalizado( contactVO.getDescTipoTelPersonalizado() );
					vo.setTipoTelefono( contactVO.getTipoTelefono() );
					vo.setTipoContacto( cur.getInt( cur.getColumnIndex("tipoContacto") ) );
				} else {
					vo = new ListaNegraVO();
					vo.setTelefono( telefono );
					vo.setId( cur.getLong( cur.getColumnIndex("id") ) );
					vo.setNombre( context.getString(R.string.contacto_desconocido) );
					vo.setTipoContacto( cur.getInt( cur.getColumnIndex("tipoContacto") ) );
					vo.setTipoTelefono( Phone.TYPE_OTHER );
				}
				
				lstFElementos.add( vo );
			}
		}
		cur.close();
		cur = null;
		return lstFElementos;
	}
	
	/**
	 * Inserta el registro contendido en el objeto <code>FrecuenteVO</code>
	 * @param vo	Objeto <code>FrecuenteVO</code>
	 * @return		<code>true</code>: La operaci&oacute;n fue exitosa
	 * 				<code>false</code>: La operaci&oacute;n NO fue exitosa
	 */
	public long insert ( ListaNegraVO vo ) {
		long result;
		
		ContentValues contentValues = DBUtil.getContentValues( vo );
		Log.d(Constants.LOG_APP, contentValues.toString());
		
		result = getDbConnection().insert(DBHelper.TABLE_LISTA_NEGRA, null, contentValues);
		
		return result;
	}
	
	/**
	 * Elimina todos los numeros frecuentes dados de alta en la aplicacion
	 * @return	Respuesta de la operacion.
	 */
	public int deleteAll() {
		return  getDbConnection().delete(DBHelper.TABLE_LISTA_NEGRA, null, null);
	}
	
	/**
	 * Elimina el numero frecuente indicado
	 * @param telefono	Telefono que se borrará
	 * @return	Respuesta de la operacion.
	 */
	public int delete(String telefono) {
		String[] params = {telefono};
		Log.d(Constants.LOG_APP, "Borrar Telefono["+telefono+"]");
		return  getDbConnection().delete(DBHelper.TABLE_LISTA_NEGRA, "telefono=?", params);
	}
	
	/**
	 * Elimina el numero frecuente indicado
	 * @param telefono	Telefono que se borrará
	 * @return	Respuesta de la operacion.
	 */
	public int deleteId(Long id) {
		String[] params = {id.toString()};
		Log.d(Constants.LOG_APP, "Borrar Id["+id+"]");
		return  getDbConnection().delete(DBHelper.TABLE_LISTA_NEGRA, "id=?", params);
	}
	
	/**
	 * Valida si un numero telefonico existe en los numeros frecuentes
	 * @param telefono	Telefono a buscar
	 * @return	<code>true</code>: Existe el telefono buscado
	 * 			<code>false</code>: NO existe el telefono buscado
	 */
	public boolean exist( String telefono ) {
		Cursor cursor = getDbConnection().query(DBHelper.TABLE_LISTA_NEGRA, new String[]{"telefono"}, "telefono=?", new String[]{telefono}, null, null, null);
		boolean result = cursor.getCount() > 0 ? true : false;
		cursor.close();
		cursor = null;
		return result;
	}
	
	/**
	 * Inserta el registro contendido en el objeto <code>FrecuenteVO</code>
	 * @param vo	Objeto <code>FrecuenteVO</code>
	 * @return		<code>true</code>: La operaci&oacute;n fue exitosa
	 * 				<code>false</code>: La operaci&oacute;n NO fue exitosa
	 */
	public long update ( ListaNegraVO vo ) {
		long result;
		
		ContentValues contentValues = DBUtil.getContentValues( vo );
		Log.d(Constants.LOG_APP, contentValues.toString());
		
		result = getDbConnection().update(DBHelper.TABLE_LISTA_NEGRA, contentValues, "telefono=?", new String[]{vo.getTelefono()});
		
		return result;
	}
	
	/**
	 * 
	 * @param telefono
	 * @return
	 */
	public ListaNegraVO queryById( String telefono ) {
		ListaNegraVO vo      = null;
		Cursor cur = getDbConnection().query(DBHelper.TABLE_LISTA_NEGRA, null, "telefono=?", new String[]{telefono}, null, null, null);
		if ( cur.moveToNext() ) {
			vo       = new ListaNegraVO();
			vo.setTelefono( cur.getString( cur.getColumnIndex( "telefono" ) ) );
			vo.setId( cur.getLong( cur.getColumnIndex( "idContacto" ) ) );
			vo.setTipoContacto( cur.getInt( cur.getColumnIndex( "tipoTelefono" ) ) );
		}
		cur.close();
		cur = null;
		return vo;
	}
	
	/**
	 * Verifica si existe el id consultado
	 * @param telefono	Telefono a consultar
	 * @return			<code>true</code>: Si existe el telefono buscado
	 * 					<code>false</code>: NO existe el telefono buscado
	 */
	public boolean existId( String telefono ) {
		return queryById( telefono ) != null;
	}
	
	/**
	 * Devuelve los registros contenidos en la tabla ListaNegra 
	 * @return	Total de Registros Existentes en la tabla ListaNegra
	 */
	public int count() {
		Cursor cursor = getDbConnection().query(DBHelper.TABLE_LISTA_NEGRA, new String[]{"telefono"}, null, null, null, null, null);
		int count = cursor.getCount();
		Log.d(Constants.LOG_APP, "Total Registros["+count+"]");
		cursor.close();
		cursor = null;
		return count;
	}
}
