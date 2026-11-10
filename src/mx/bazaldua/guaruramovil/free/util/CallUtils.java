/**
 * 
 */
package mx.bazaldua.guaruramovil.free.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import mx.bazaldua.guaruramovil.free.db.dao.ConfiguracionFrecuentesDAO;
import mx.bazaldua.guaruramovil.free.vo.HistorialVO;
import mx.bazaldua.guaruramovil.free.vo.LlamadaVO;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class CallUtils {
	
	/**
	 * Regresa el objeto ITelephony que se encuentra oculto.
	 * @param context	Contexto de la aplicacion
	 * @return	Objeto ITelephony
	 * 
	 * @throws ClassNotFoundException	Objeto contenedor de excepciones.
	 * @throws SecurityException	Objeto contenedor de excepciones.
	 * @throws NoSuchMethodException	Objeto contenedor de excepciones.
	 * @throws IllegalArgumentException	Objeto contenedor de excepciones.
	 * @throws IllegalAccessException	Objeto contenedor de excepciones.
	 * @throws InvocationTargetException	Objeto contenedor de excepciones.
	 */
	public static Object getITelephony( Context context ) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		Class<?> classTM = Class.forName(tm.getClass().getName());
	    Method method = classTM.getDeclaredMethod("getITelephony");
	    method.setAccessible(true);
		return method.invoke(tm);
	}
	
	/**
	 * Finaliza una llamada en curso
	 * 
	 * @param context	Contexto de la aplicacion
	 * @throws SecurityException	Objeto contenedor de excepciones.
	 * @throws IllegalArgumentException	Objeto contenedor de excepciones.
	 * @throws NoSuchMethodException	Objeto contenedor de excepciones.
	 * @throws ClassNotFoundException	Objeto contenedor de excepciones.
	 * @throws IllegalAccessException	Objeto contenedor de excepciones.
	 * @throws InvocationTargetException	Objeto contenedor de excepciones.
	 */
	public static void endCall( Context context ) throws SecurityException, IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		execMethod("endCall", context, new Object[0] );
	}
	
	/**
	 * Contesta una llamada en curso
	 * 
	 * @param context	Contexto de la aplicacion
	 * @param number	Numero que se marcara
	 * @throws SecurityException	Objeto contenedor de excepciones.
	 * @throws IllegalArgumentException	Objeto contenedor de excepciones.
	 * @throws NoSuchMethodException	Objeto contenedor de excepciones.
	 * @throws ClassNotFoundException	Objeto contenedor de excepciones.
	 * @throws IllegalAccessException	Objeto contenedor de excepciones.
	 * @throws InvocationTargetException	Objeto contenedor de excepciones.
	 */
	public static void call( Context context, String number ) throws SecurityException, IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse( new StringBuilder("tel:").append( number ).toString() ));
        context.startActivity(callIntent);
//		execMethod("call", context, number);
		
	}
	/**
	 * Contesta una llamada en curso
	 * 
	 * @param context	Contexto de la aplicacion
	 * @throws SecurityException	Objeto contenedor de excepciones.
	 * @throws IllegalArgumentException	Objeto contenedor de excepciones.
	 * @throws NoSuchMethodException	Objeto contenedor de excepciones.
	 * @throws ClassNotFoundException	Objeto contenedor de excepciones.
	 * @throws IllegalAccessException	Objeto contenedor de excepciones.
	 * @throws InvocationTargetException	Objeto contenedor de excepciones.
	 */
	public static void answerRingingCall(Context context ) throws SecurityException, IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		execMethod("answerRingingCall", context, new Object[0]);
	}
	
	/**
	 * 
	 * @param method	Nombre del metodo a obtener del objeto <code>iTelephony</code>
	 * @param iTelephony	Objeto <code>iTelephony</code>
	 * @return	Objeto <code>Method</code>
	 * @throws SecurityException	Objeto contenedor de excepciones.
	 * @throws IllegalArgumentException	Objeto contenedor de excepciones.
	 * @throws ClassNotFoundException	Objeto contenedor de excepciones.
	 * @throws NoSuchMethodException	Objeto contenedor de excepciones.
	 * @throws IllegalAccessException	Objeto contenedor de excepciones.
	 * @throws InvocationTargetException	Objeto contenedor de excepciones.
	 */
	public static void execMethod( String method, Context context, Object... param ) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Object iTelephony = getITelephony(context);
		iTelephony.getClass().getMethod( method ).invoke(iTelephony, param);
	}	
	
	/**
	 * Regresa las llamadas redondeadas parsa identificar el tiempo exacto que esten cobrando los operadores
	 * @param lstResultado	Lista original con el resultado de las llamadas
	 * @return				Lista con la duracion de llamada redondeada
	 */
	public static synchronized SparseArray<Object> getLlamadas(List<?> lstResultado, boolean redondear, boolean frecuentes, Context context) {
		SparseArray<Object> mapResult = new SparseArray<Object>();
		List<Object> lstRes            = null;
		int mod                        = 0;
		Long tiempoTotal               = 0L;
		LlamadaVO llamadaVO            = null;
		HistorialVO historialVO        = null;
		int duracion                  = 0;
		String telefono                = null;
		ConfiguracionFrecuentesDAO dao = new ConfiguracionFrecuentesDAO(context);
		try {
			if ( redondear || !frecuentes ) {
				lstRes  = new ArrayList<Object>();
				for (Object vo : lstResultado) {
					if ( vo instanceof LlamadaVO ) llamadaVO = (LlamadaVO) ((LlamadaVO) vo).clone();
					else historialVO = (HistorialVO) ((HistorialVO) vo).clone();
	
					duracion = llamadaVO != null ? llamadaVO.getDuracion() : historialVO.getDuration().intValue();
					telefono = llamadaVO != null ? llamadaVO.getTelefono() : historialVO.getNumber();
					
					//Se vewrifica si se va a realizar el redondeo
					if ( redondear ) {
						mod = duracion % 60;
						if ( mod != 0 ) {
							if ( llamadaVO != null ) llamadaVO.setDuracion( duracion + (60 - mod) );
							else historialVO.setDuration( Long.valueOf( String.valueOf( duracion + (60 - mod) ) ) );
						}
					}
					
					//Se verifica si no se van a agregar los numeros frecuentes a la lista a desplegar
					if ( (frecuentes && redondear)  || 
						( !frecuentes && !dao.exist(telefono.length() > 10 ? 
													telefono.substring(telefono.trim().length() - 10) : 
														telefono) ) ) {
						
						lstRes.add( llamadaVO == null ? historialVO : llamadaVO  );
						tiempoTotal += llamadaVO != null ? llamadaVO.getDuracion() : historialVO.getDuration();
					}
				}
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		} finally {
			dao.closeConnection();
			dao = null;
		}
		
		mapResult.put(1, lstRes);
		mapResult.put(2, tiempoTotal);
		return mapResult;
	}
	
	/**
	 * Devuelve un numero telefonico sin espacios, guiones o puntos
	 * 
	 *  @param phone Numero telefonico a formatear
	 * @return	Numero telefonico sin espacios, guiones o puntos
	 */
	public static CharSequence getPhoneNumber( CharSequence phone ) {
		return phone == null ?  "" : phone.toString().replaceAll(" ", "").replaceAll("-", "").replaceAll("\\.", "");
	}
}