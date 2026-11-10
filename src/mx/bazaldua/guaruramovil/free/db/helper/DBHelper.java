/**
 * 
 */
package mx.bazaldua.guaruramovil.free.db.helper;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.bazaldua.guaruramovil.free.db.dao.ConfiguracionFrecuentesDAO;
import mx.bazaldua.guaruramovil.free.db.dao.HistoryDAO;
import mx.bazaldua.guaruramovil.free.db.dao.ListaNegraDAO;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.vo.FrecuenteVO;
import mx.bazaldua.guaruramovil.free.vo.HistorialVO;
import mx.bazaldua.guaruramovil.free.vo.ListaNegraVO;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class DBHelper extends SQLiteOpenHelper {
	private static Context context 							= null;
	
	public static final String TABLE_FRECUENTES		= "ConfiguracionFrecuentes";
	public static final String TABLE_LISTA_NEGRA	= "ListaNegra";
	public static final String TABLE_HISTORIAL		= "HistorialLlamadas";
	
	private static final String CREATE_FRECUENTES	= "CREATE TABLE ".concat(TABLE_FRECUENTES).concat(" (")
														.concat( "telefono TEXT," )
														.concat( "minutos INTEGER," )
														.concat( "segundos INTEGER," )
														.concat( "remarcar BOOLEAN" )	
														.concat(")");
	private static final String DELETE_FRECUENTES 	= "DROP TABLE IF EXISTS ".concat( TABLE_FRECUENTES );
	
	private static final String CREATE_LISTA_NEGRA	= "CREATE TABLE ".concat(TABLE_LISTA_NEGRA).concat(" (")
													.concat( "id LONG," )
													.concat( "telefono TEXT," )
													.concat( "tipoContacto INTEGER" )	
													.concat(")");
	private static final String DELETE_LISTA_NEGRA	= "DROP TABLE IF EXISTS ".concat( TABLE_LISTA_NEGRA );
	
	private static final String CREATE_HISTORIAL	= "CREATE TABLE ".concat(TABLE_HISTORIAL).concat(" (")
													.concat( "_id LONG," )
													.concat( "number TEXT," )
													.concat( "type INTEGER," )
													.concat( "date LONG," )
													.concat( "duration LONG," )
													.concat( "name TEXT" )
													.concat(")");
	private static final String DELETE_HISTORIAL	= "DROP TABLE IF EXISTS ".concat( TABLE_HISTORIAL );
	
	private static Map<String, List<?>> mapBackup = new HashMap<String, List<?>>();
	
	
	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		DBHelper.context = context;
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL( CREATE_FRECUENTES );
		db.execSQL( CREATE_LISTA_NEGRA );
		db.execSQL( CREATE_HISTORIAL );
		copyLogCalls(db);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int version, int arg2) {
		backupDatabase(db);
		db.execSQL(DELETE_FRECUENTES);
		db.execSQL(DELETE_LISTA_NEGRA);
		db.execSQL(DELETE_HISTORIAL);
		onCreate(db);
		restoreDataBase(db);
	}
	
	/**
	 * 
	 * @param db
	 */
	private void copyLogCalls(SQLiteDatabase db) {
		HistoryDAO dao = new HistoryDAO(db, context);
		List<HistorialVO> lstHistory = dao.getHistoryPhone();
		
		if ( lstHistory != null && lstHistory.size() > 0 ) dao.insert(lstHistory);
	}

	/**
	 * Respalda la BD para despues volver a generar los registros
	 */
	private void backupDatabase(SQLiteDatabase db){
		ConfiguracionFrecuentesDAO daoFrecuentes = null;
		ListaNegraDAO daoListaNegra              = null;
		List<FrecuenteVO> lstFrecuentes          = null;
		List<ListaNegraVO> lstListaNegra         = null;
		int total                                = 0;
		try {
			Log.d(Constants.LOG_APP, "Respaldando DB...");
			daoFrecuentes = new ConfiguracionFrecuentesDAO(db, context);
			daoListaNegra = new ListaNegraDAO(db, context);
			lstFrecuentes = daoFrecuentes.getListFrecuentes();
			lstListaNegra = daoListaNegra.getListListasNegras();
			
			if ( lstFrecuentes != null && lstFrecuentes.size() > 0 ) {
				total += lstFrecuentes.size();
				mapBackup.put(TABLE_FRECUENTES, lstFrecuentes);
			}
			if ( lstListaNegra != null && lstListaNegra.size() > 0 ) {
				total += lstListaNegra.size();
				mapBackup.put(TABLE_LISTA_NEGRA, lstListaNegra);
			}
			Log.d(Constants.LOG_APP, "Respaldando DB Finalizado ["+total+"].");
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		} finally {
			daoFrecuentes = null;
			daoListaNegra = null;
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void restoreDataBase(SQLiteDatabase db){
		ConfiguracionFrecuentesDAO daoFrecuentes = null;
		ListaNegraDAO daoListaNegra              = null;
		List<FrecuenteVO> lstFrecuentes          = null;
		List<ListaNegraVO> lstListaNegra         = null;
		int total                                = 0;
		try {
			Log.d(Constants.LOG_APP, "Restaurando DB...");
			lstFrecuentes = (List<FrecuenteVO>) mapBackup.get(TABLE_FRECUENTES);
			lstListaNegra = (List<ListaNegraVO>) mapBackup.get(TABLE_LISTA_NEGRA);
			
			if ( lstFrecuentes != null && lstFrecuentes.size() > 0 ) {
				daoFrecuentes = new ConfiguracionFrecuentesDAO(db, context);
				for (FrecuenteVO vo : lstFrecuentes) {
					vo.setNombre(null);
					vo.setId(null);
					vo.setFoto(null);
					vo.setLookupKey(null);
					vo.setDescTipoTelPersonalizado(null);
					daoFrecuentes.insert(vo);
					total ++;
				}
			}
			
			if ( lstListaNegra != null && lstListaNegra.size() > 0 ) {
				daoListaNegra = new ListaNegraDAO(db, context);
				for (ListaNegraVO vo : lstListaNegra) {
					vo.setNombre(null);
					vo.setTipoTelefono(null);
					vo.setFoto(null);
					vo.setLookupKey(null);
					vo.setDescTipoTelPersonalizado(null);
					daoListaNegra.insert(vo);
					total ++;
				}
			}
			Log.d(Constants.LOG_APP, "DB Restauranda ["+total+"].");
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		} finally {
			lstFrecuentes = null;
			lstListaNegra = null;
			daoFrecuentes = null;
			daoListaNegra = null;
			mapBackup     = null;
		}
	}
}
