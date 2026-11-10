/**
 * 
 */
package mx.bazaldua.guaruramovil.free.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mx.bazaldua.guaruramovil.free.db.helper.DBHelper;
import mx.bazaldua.guaruramovil.free.util.CallUtils;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.DBUtil;
import mx.bazaldua.guaruramovil.free.vo.HistorialVO;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CallLog.Calls;
import android.util.Log;

/**
 * @author bazalduai
 *
 */
public class HistoryDAO extends DAO {
	private final String[] projection = new String[] {
			Calls.TYPE,
			Calls.NUMBER,
			Calls.DATE,
			Calls.DURATION,
			Calls.CACHED_NAME,
			Calls._ID};
	
	/**
	 * 
	 * @param context
	 */
	public HistoryDAO(Context context) {
		super(context);
	}
	
	/**
	 * 
	 * @param db
	 * @param context
	 */
	public HistoryDAO(SQLiteDatabase db, Context context) {
		super(db, context);
	}
	
	/**
	 * Consulta las llamadas registradaas en base a un criterio de consulta
	 * @param condicion
	 * @param valores
	 * @return
	 */
	public List<HistorialVO> getHistoryPhone( String condicion, String[] valores ) {
		List<HistorialVO> lstResult = null;
		HistorialVO vo              = null;
		try {
			StringBuilder builder = new StringBuilder("Condicion["+condicion+"] Valores[");
			if ( valores != null ) for (String valor : valores) builder.append( valor ).append("|");
			else builder.append("NULL");
			builder.append("]");
			Log.d(Constants.LOG_APP, builder.toString());
			
			Cursor cur = context.getContentResolver().query(Calls.CONTENT_URI,
					projection, //Columnas a devolver
					condicion,       //Condición de la query
					valores,       //Argumentos variables de la query
					Calls.DATE.concat( " DESC" ) );      //Orden de los resultados
			
			Log.d(Constants.LOG_APP, "Total Consultado Historial["+cur.getCount()+"]");
			if ( cur.getCount() == 0 ) {
				cur.close();
			} else {
				lstResult = new ArrayList<HistorialVO>();
				cur.moveToFirst();
				do {
					vo = new HistorialVO();
					vo.setType( cur.getInt ( cur.getColumnIndex(Calls.TYPE) ) );
					vo.setNumber( CallUtils.getPhoneNumber( cur.getString( cur.getColumnIndex(Calls.NUMBER) ) ).toString() );
					vo.setDate( cur.getLong( cur.getColumnIndex(Calls.DATE) ) );
					vo.setDuration( cur.getLong( cur.getColumnIndex(Calls.DURATION) ) );
					vo.setName( cur.getString( cur.getColumnIndex(Calls.CACHED_NAME) ) );
					vo.set_id( cur.getLong( cur.getColumnIndex(Calls._ID) ) );
					
					lstResult.add(vo);

				} while (cur.moveToNext());
				cur.close();
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		}
		return lstResult;
	}
	
	/**
	 * Regresa todas las llamadas registradas
	 * @return
	 */
	public List<HistorialVO> getHistoryPhone() {
		return getHistoryPhone(null, null);
	}
	
	/**
	 * Obtiene la ultima llamada registrada en el Log de llamadas del telefono
	 * s@return
	 */
	public HistorialVO getLastCallPhone(long date) {
		HistorialVO resultVO = null;
		String[] valores = {String.valueOf(date)};
		List<HistorialVO> lstHistorial = getHistoryPhone(Calls.DATE + " > ? ", valores);
		Log.d(Constants.LOG_APP, "Consulta Last CALL ["+(lstHistorial == null?0: lstHistorial.size())+"]");
		if ( lstHistorial != null && lstHistorial.size() > 0 ) resultVO = lstHistorial.get(0);
		lstHistorial = null;
		return resultVO;
	}
	
	
	/**
	 * Inserta un registro en la tabla de Historial
	 * @param vo
	 * @return
	 */
	public long insert ( List<HistorialVO> lstElementos ) {
		long result = 0;
		Log.d(Constants.LOG_APP, "Total a respaldar ["+lstElementos.size()+"]");
		for (HistorialVO historialVO : lstElementos) {
			result = insert(historialVO);
		}
		
		return result;
	}
	
	/**
	 * Inserta un registro en la tabla de Historial
	 * @param vo
	 * @return
	 */
	public long insert ( HistorialVO vo ) {
		long result;
		
		ContentValues contentValues = DBUtil.getContentValues( vo );
		
		result = getDbConnection().insert(DBHelper.TABLE_HISTORIAL, null, contentValues);
		return result;
	}
	
	/**
	 * Consulta a la tabla de la Aplicacion regresando todos los elementos
	 * @param condicion
	 * @param valores
	 * @return
	 */
	public List<HistorialVO> getHistoryLocal( String condicion, String[] valores ) {
		List<HistorialVO> lstElementos = null;
		HistorialVO vo                 = null;
		Cursor cur                     = null;

		StringBuilder builder = new StringBuilder("Condicion["+condicion+"] Valores[");
		if ( valores != null ) for (String valor : valores) builder.append( valor ).append("|");
		else builder.append("NULL");
		builder.append("]");
		Log.d(Constants.LOG_APP, builder.toString());
		
		try {
			cur = getDbConnection().query(DBHelper.TABLE_HISTORIAL, null, condicion, valores, null, null, Calls._ID + " DESC");
			Log.d(Constants.LOG_APP, "Total Consultado Historial Propio["+cur.getCount()+"]");
			if ( cur.getCount() > 0 ) {
				
				lstElementos = new ArrayList<HistorialVO>();
				while ( cur.moveToNext() ) {
					vo = new HistorialVO();
					
					vo.setType( cur.getInt ( cur.getColumnIndex(Calls.TYPE) ) );
					vo.setNumber( CallUtils.getPhoneNumber( cur.getString( cur.getColumnIndex(Calls.NUMBER) ) ).toString() );
					vo.setDate( cur.getLong( cur.getColumnIndex(Calls.DATE) ) );
					vo.setDuration( cur.getLong( cur.getColumnIndex(Calls.DURATION) ) );
					vo.setName( cur.getString( cur.getColumnIndex(Calls.CACHED_NAME) ) );
					vo.set_id( cur.getLong( cur.getColumnIndex(Calls._ID) ) );
					
					lstElementos.add( vo );
				}
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		} finally {
			if ( cur != null ) cur.close();
			cur = null;
		}
		return lstElementos;
	}
	
	/**
	 * Consulta a la tabla de la Aplicacion regresando todos los elementos 
	 * @return
	 */
	public List<HistorialVO> gettHistoryLocal() {
		return getHistoryLocal(null, null);
	}
	
	/**
	 * Actualiza los registros faltantes en la tabla de Historial propia para que vaya a la par del historia del telefono
	 * 
	 * @param fechaInicial	Fecha inicial para realizar la consulta
	 * @param fechaFinal	Fecha final para realizar la consulta
	 * @return				<code>List<HistorialVO></code> con los registros faltantes que se han insertado en la BD
	 */
	public List<HistorialVO> actualizaFaltantes( Date fechaInicial, Date fechaFinal ) {
		String[] valor                 = new String[2];
		List<HistorialVO> lstFaltantes = null;
		List<HistorialVO> lstHistorial = null;
		
		try {
		
			if ( fechaInicial != null ) {
				valor[0] = String.valueOf( fechaInicial.getTime() );
				valor[1] = String.valueOf( fechaFinal.getTime() );
			} else {
				valor[0] = "0";
				valor[1] = String.valueOf(fechaFinal.getTime());
			}
	
			lstFaltantes = getHistoryPhone(Calls.DATE + " BETWEEN ? AND ? ", valor);
			
			if ( lstFaltantes != null && lstFaltantes.size() > 0 ) {
				lstHistorial = new ArrayList<HistorialVO>();
				for (int i = lstFaltantes.size() - 1; i >= 0 ; i--) {
					if ( !lstFaltantes.get(i).getDate().equals( fechaInicial.getTime() ) ) {
						Log.d(Constants.LOG_APP, "Insertando Faltante");
						
						StringBuilder builder = new StringBuilder();
						builder.append( "Nombre [" ).append( lstFaltantes.get(i).getName() ).append("] ");
						builder.append( "Fecha [" ).append( lstFaltantes.get(i).getDate() ).append("] ");
						builder.append( "Duracion [" ).append( lstFaltantes.get(i).getDuration() ).append("] ");						
						Log.d(Constants.LOG_APP, "Registro ["+builder.toString()+"]");
						
						lstHistorial.add(0, lstFaltantes.get(i));
						//Se guardan en la BD los registros faltantes
						insert( lstFaltantes.get(i) );
					}
				}
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		}
		return lstHistorial;
	}
	
	/**
	 * Verifica si existe el registro de llamada
	 * @param vo	Objeto con los datos a buscar dentro del Historial de llamadas propio
	 * @return		<code>true</code>: SI existe el reigstro
	 * 				<code>false</code>: NO existe el registro.
	 */
	public boolean exist( HistorialVO vo ) {
		boolean result = false;
		Cursor cur     = null;
		try {
			String condicion = Calls.NUMBER + "=? AND " + Calls.DATE + "=? AND " + Calls.DURATION + "=?";
			String[] valores = { vo.getNumber(), vo.getDate().toString(), vo.getDuration().toString() };
			cur = getDbConnection().query(DBHelper.TABLE_HISTORIAL, null, condicion, valores, null, null, Calls._ID + " DESC");
			result = cur.getCount() > 0;
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		} finally {
			if ( cur != null ) cur.close();
		}
		
		return result;
		
	}
}
