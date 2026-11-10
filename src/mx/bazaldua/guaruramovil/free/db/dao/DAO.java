/**
 * 
 */
package mx.bazaldua.guaruramovil.free.db.dao;

import mx.bazaldua.guaruramovil.free.db.helper.DBHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class DAO {
	private SQLiteDatabase dbConnection;
	private final String NAME_DB = "DBEstadisticas";
	protected Context context;
	private final int versionDB = 2;
	
	/**
	 * 
	 * @param context
	 */
	public DAO( Context context ) {
		DBHelper helper = new DBHelper(context, NAME_DB, null, versionDB);
		dbConnection = helper.getWritableDatabase();
		this.context = context; 
	}
	
	public DAO( SQLiteDatabase db, Context context ) {
		new DBHelper(context, NAME_DB, null, versionDB);
		dbConnection = db;
		this.context = context; 
	}
	
	/**
	 * 
	 * @return
	 */
	public SQLiteDatabase getDbConnection () {
		return dbConnection;
	}
	
	/**
	 * 
	 */
	public void closeConnection() {
		if ( dbConnection != null ) dbConnection.close();
	}
	
}
