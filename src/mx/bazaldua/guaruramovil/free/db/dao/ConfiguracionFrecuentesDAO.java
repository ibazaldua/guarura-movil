package mx.bazaldua.guaruramovil.free.db.dao;

import java.util.ArrayList;
import java.util.List;

import mx.bazaldua.guaruramovil.free.db.helper.DBHelper;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.ContactUtils;
import mx.bazaldua.guaruramovil.free.util.DBUtil;
import mx.bazaldua.guaruramovil.free.vo.ContactVO;
import mx.bazaldua.guaruramovil.free.vo.FrecuenteVO;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ConfiguracionFrecuentesDAO extends DAO {
	
	/**
	 * 
	 * @param context
	 */
	public ConfiguracionFrecuentesDAO(Context context) {
		super(context);
	}
	
	/**
	 * 
	 * @param context
	 */
	public ConfiguracionFrecuentesDAO(SQLiteDatabase db, Context context) {
		super(db, context);
	}
	
	/**
	 * 
	 * @param telefono
	 * @return
	 */
	public Cursor getNumeroFrecuente ( String telefono ) {
		StringBuilder sql = new StringBuilder();
		sql.append( "SELECT minutos, segundos, remarcar " );
		sql.append( "FROM " ).append(DBHelper.TABLE_FRECUENTES);
		sql.append( " WHERE telefono LIKE '%" ).append(telefono).append( "%'" );
		return getDbConnection().rawQuery( sql.toString(), null ); 
	}
	
	/**
	 * 
	 * @param contentResolver
	 * @return
	 */
	public List<FrecuenteVO> getListFrecuentes( ) {
		List<FrecuenteVO> lstFrecuentes = null;
		ContentResolver contentResolver = context.getContentResolver();
		FrecuenteVO frecuenteVO         = null;
		ContactVO contactVO             = null;
		int remarcar                    = 0;
		String telefono                 = null;
		Cursor cur                      = getDbConnection().query(DBHelper.TABLE_FRECUENTES, null, null, null, null, null, null);
		
		if ( cur.getCount() > 0 ) {
			lstFrecuentes = new ArrayList<FrecuenteVO>();
			while ( cur.moveToNext() ) {
				telefono = cur.getString( cur.getColumnIndex("telefono") );
				remarcar = cur.getInt( cur.getColumnIndex("remarcar") );
				contactVO = ContactUtils.getContactByPhone(telefono, contentResolver);

				if ( contactVO == null ) contactVO = ContactUtils.getContactByPhoneSIM(telefono, contentResolver);
				
				if ( contactVO != null ) {
					frecuenteVO = new FrecuenteVO();
					frecuenteVO.setId( contactVO.getId() );
					frecuenteVO.setNombre( contactVO.getNombre() );
					frecuenteVO.setFoto( contactVO.getFoto() );
					frecuenteVO.setTelefono( telefono );
					frecuenteVO.setMinutos( cur.getInt( cur.getColumnIndex("minutos") ) );
					frecuenteVO.setSegundos( cur.getInt( cur.getColumnIndex("segundos") ) );
					frecuenteVO.setRemarcar( remarcar == 1 ? true : false );
					
				} else {
					frecuenteVO = new FrecuenteVO();
					frecuenteVO.setId( -1L );
					frecuenteVO.setTelefono( telefono );
					frecuenteVO.setMinutos( cur.getInt( cur.getColumnIndex("minutos") ) );
					frecuenteVO.setSegundos( cur.getInt( cur.getColumnIndex("segundos") ) );
					frecuenteVO.setRemarcar( remarcar == 1 ? true : false );
				}
				
				lstFrecuentes.add( frecuenteVO );
			}
		}
		cur.close();
		cur = null;
		return lstFrecuentes;
	}
	
	/**
	 * Inserta el registro contendido en el objeto <code>FrecuenteVO</code>
	 * @param vo	Objeto <code>FrecuenteVO</code>
	 * @return		<code>true</code>: La operaci&oacute;n fue exitosa
	 * 				<code>false</code>: La operaci&oacute;n NO fue exitosa
	 */
	public long insert ( FrecuenteVO vo ) {
		long result;
		
		ContentValues contentValues = DBUtil.getContentValues( vo );
		Log.d(Constants.LOG_APP, contentValues.toString());
		
		result = getDbConnection().insert(DBHelper.TABLE_FRECUENTES, null, contentValues);
		
		return result;
	}
	
	/**
	 * Elimina todos los numeros frecuentes dados de alta en la aplicacion
	 * @return	Respuesta de la operacion.
	 */
	public int deleteAll() {
		return  getDbConnection().delete(DBHelper.TABLE_FRECUENTES, null, null);
	}
	
	/**
	 * Elimina el numero frecuente indicado
	 * @param telefono	Telefono que se borrará
	 * @return	Respuesta de la operacion.
	 */
	public int delete(String telefono) {
		String[] params = {telefono};
		return  getDbConnection().delete(DBHelper.TABLE_FRECUENTES, "telefono=?", params);
	}
	
	/**
	 * Valida si un numero telefonico existe en los numeros frecuentes
	 * @param telefono	Telefono a buscar
	 * @return	<code>true</code>: Existe el telefono buscado
	 * 			<code>false</code>: NO existe el telefono buscado
	 */
	public boolean exist( String telefono ) {
		Cursor cursor = getDbConnection().query(DBHelper.TABLE_FRECUENTES, new String[]{"telefono"}, "telefono=?", new String[]{telefono}, null, null, null);
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
	public long update ( FrecuenteVO vo ) {
		long result;
		
		ContentValues contentValues = DBUtil.getContentValues( vo );
		Log.d(Constants.LOG_APP, contentValues.toString());
		
		result = getDbConnection().update(DBHelper.TABLE_FRECUENTES, contentValues, "telefono=?", new String[]{vo.getTelefono()});
		
		return result;
	}
	
	/**
	 * 
	 * @param telefono
	 * @return
	 */
	public FrecuenteVO queryById( String telefono ) {
		FrecuenteVO vo      = null;
		int remarcar        = 0;
		
		Cursor cur = getDbConnection().query(DBHelper.TABLE_FRECUENTES, null, "telefono=?", new String[]{telefono}, null, null, null);
		if ( cur.moveToNext() ) {
			vo       = new FrecuenteVO();
			remarcar = cur.getInt( cur.getColumnIndex( "remarcar" ) );
			vo.setTelefono( cur.getString( cur.getColumnIndex( "telefono" ) ) );
			vo.setMinutos( cur.getInt( cur.getColumnIndex( "minutos" ) ) );
			vo.setSegundos( cur.getInt( cur.getColumnIndex( "segundos" ) ) );
			vo.setRemarcar( remarcar == 1 ? true : false );
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
}
