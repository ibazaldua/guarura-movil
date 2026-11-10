package mx.bazaldua.guaruramovil.free.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.ContentValues;
import android.util.Log;

public class DBUtil {
	
	/**
	 * Devuelve un objeto <code>ContentValues</code> necesario para la insercion de valores en la BD
	 * @param vo	Objeto contenedor de los valores a insertar
	 * @return		<code>ContentValues</code> con los valores a insertar
	 */
	public static ContentValues getContentValues ( Object vo ) {
		ContentValues contentValues = null;
		Field[] fields              = null;
		Object value                = null;
		Method method               = null;
		
		if ( vo != null ) {
			contentValues = new ContentValues();
			fields = Util.getFieldsObject(vo);
			
			for (Field field : fields) {
				try {
					method = Util.getMethod( vo, Util.getNameMethod( field.getName(), true ) );
					if ( method != null ) {
						value = method.invoke(vo, (Object[])null);
						if ( value != null ) {
							if ( field.getType().getName().equals(Boolean.class.getName()) ) {
								value = value.toString().equals("true") ? 1 : 0; 
							}
							contentValues.put( field.getName() , value.toString() );
						}
					}
				} catch (Exception e) {
					Log.w(Constants.LOG_APP, e.getMessage(), e);
				}
			}
		}
		
		return contentValues;
	}
}
