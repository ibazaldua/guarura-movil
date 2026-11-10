/**
 * 
 */
package mx.bazaldua.guaruramovil.free.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.bazaldua.guaruramovil.free.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class Util {
	
	/**
	 * 
	 * @param context
	 * @param mensaje
	 */
	public static void mensajeToast ( Context context, String mensaje ) {
		Toast toast       = new Toast(context);
		Activity activity = (Activity) context;
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(R.layout.toast_layout,
					(ViewGroup) activity.findViewById(R.id.lytLayout));

		TextView txtMsg = (TextView)layout.findViewById(R.id.txtMensaje);
		txtMsg.setText( mensaje );
		toast.setDuration( Toast.LENGTH_SHORT );
		toast.setView( layout );
		toast.show();
	}
	

	/**
	 * 
	 * @param context
	 * @param clazz
	 */
	public static void callActivity ( Activity context, Class<?> clazz ) {
		Intent intent     = new Intent( context, clazz);
		context.setProgressBarIndeterminateVisibility(true);
		context.startActivity(intent);
	}
	
	/**
	 * Obtiene el nombre del metodo estandar a partir de un campo dado.
	 * 
	 * @param campo		Campo del que se requiere obtener el nombre del metodo.
	 * @param isGetter	<code>true</code>: Se va a regresar el nombre del metodo getter
	 * 					<code>false</code>: Se va a regresar el nombre del metodo false
	 * @return			Regresa el nombre del metodo correspondiente al campo java.
	 */
	public static String getNameMethod ( String campo, boolean isGetter ) {
		return new StringBuffer( isGetter ? "get" : "set" ).append( campo.substring(0, 1).toUpperCase() ).append( campo.substring(1, campo.length() ) ).toString();
	}
	
	/**
	 * Recupera todos los campos de un objeto tipo <<code>Value Object</code>. incluyendo los de sus superclases.
	 * 
	 * <br>author Iván Bazaldúa Ramírez - IBR 09/10/2009 
	 * @param clazz		Objeto tipo <code>Class</code> que contiene los elementos del objeto
	 * @return			Array de campos tipo <code>java.lang.reflectField</code>
	 */
	public static Field[] getFieldsObject ( Class<?> clazz )  {
		List<Field> listFields = new ArrayList<Field>();
		boolean primero        = true;
		
		while ( primero || !clazz.getName().equals( Object.class.getName() ) ) {
			clazz = primero ? clazz: clazz.getSuperclass();
			primero = false;
			
			if ( clazz.getName().equals( Object.class.getName() ) ) continue;
		
			listFields.addAll( Arrays.asList( clazz.getDeclaredFields() ) );
		}
		
		return listFields.toArray( new Field[0] );
	}
	
	/**
	 * Recupera todos los campos de un objeto tipo <<code>Value Object</code>. incluyendo los de sus superclases.
	 * 
	 * <br>author Iván Bazaldúa Ramírez - IBR 09/10/2009 
	 * @param object	Objeto contenedor de los campos que se requieren recuperar.
	 * @return			Array de campos tipo <code>java.lang.reflectField</code>
	 */
	public static Field[] getFieldsObject ( Object object )  {
		return getFieldsObject ( object.getClass() );
	}
	
	/**
	 * Recupera todos los metodos de un objeto tipo <<code>Value Object</code>. incluyendo los de sus superclases.
	 * 
	 * <br>author Iván Bazaldúa Ramírez - IBR 09/10/2009 
	 * @param clazz		Objeto tipo <code>Class</code> que contiene los elementos del objeto
	 * @return			Array de campos tipo <code>java.lang.reflectField</code>
	 */
	public static Method[] getMethodsObject( Class<?> clazz )  {
		List<Method> listFields = new ArrayList<Method>();
		boolean primero        = true;
		
		while ( primero || !clazz.getName().equals( Object.class.getName() ) ) {
			clazz = primero ? clazz: clazz.getSuperclass();
			primero = false;
			
			if ( clazz.getName().equals( Object.class.getName() ) ) continue;
		
			listFields.addAll( Arrays.asList( clazz.getDeclaredMethods() ) );
		}
		
		return listFields.toArray( new Method[0] );
	}
	
	/**
	 *  Regresa el objeto <code>Method</code> del objeto correspondiente
	 * @param object	Objeto que contiuene los metodos a recuperar
	 * @param method	Nombre del metodo a recuperar
	 * @return			Objeto <code>Method</code> con el que se requiere trabajar
	 */
	public static Method getMethod ( Object object, String method ) {
		Method result    = null; 
		Method[] methods = getMethodsObject( object.getClass() );
		
		for (Method method2 : methods) {
			if ( method.equals( method2.getName() ) ) {
				result = method2;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Manda una ventana de dialogo para confirmar una accion
	 * @param context	Contexto de la aplicacion.
	 * @param idTitle	Identificador del titulo
	 * @param messageId	Identificador del mensaje a mostrar
	 * @param resulTrue <code>Listener</code> que contiene las operaciones a realizar cuando se confirma en el cuadro de dialogo
	 * @param resultFalse	<code>Listener</code> que contiene las operaciones a realizar cuando NO se confirma en el cuadro de dialogo
	 * @return
	 */
	public static void confirm ( Context context, int idTitle, int messageId, OnClickListener resulTrue, OnClickListener resultFalse  ) {
		confirm(context, context.getString(idTitle), context.getString(messageId), resulTrue, resultFalse);
	}
	
	/**
	 * Manda una ventana de dialogo para confirmar una accion
	 * @param context	Contexto de la aplicacion.
	 * @param title		Titulo a mostrar
	 * @param message	Mensaje a mostrar
	 * @param resulTrue <code>Listener</code> que contiene las operaciones a realizar cuando se confirma en el cuadro de dialogo
	 * @param resultFalse	<code>Listener</code> que contiene las operaciones a realizar cuando NO se confirma en el cuadro de dialogo
	 * @return
	 */
	public static void confirm ( Context context, CharSequence title, CharSequence message, OnClickListener resulTrue, OnClickListener resultFalse  ) {
		new AlertDialog.Builder(context)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(title)
        .setMessage( message )
        .setPositiveButton(R.string.alert_dialog_ok, resulTrue )
        .setNegativeButton(R.string.alert_dialog_cancel, resultFalse)
        .create().show();
	}

	/**
	 * Crea un cuadro de dialogo con opciones de una lista enviada 
	 * @param context	Contexto de la aplicacion
	 * @param titleID		Titulo del cuadro de dialogo
	 * @param elementos	Array de elementos a mostrar
	 * @param listener	<code>Listener</code> que contiene la forma a procesar
	 */
	public static void optionsDialog ( Context context, int titleID, String[] elementos, OnClickListener listener ) {
		optionsDialog(context, context.getString(titleID), elementos, listener);
	}
	
	/**
	 * Crea un cuadro de dialogo con opciones de una lista enviada 
	 * @param context	Contexto de la aplicacion
	 * @param title		Titulo del cuadro de dialogo
	 * @param elementos	Array de elementos a mostrar
	 * @param listener	<code>Listener</code> que contiene la forma a procesar
	 */
	public static void optionsDialog ( Context context, CharSequence title, String[] elementos, OnClickListener listener ) {
		new AlertDialog.Builder(context)
//        .setIcon(android.R.drawable.ic_menu_more)
        .setTitle(title)
        .setItems(elementos, listener)
        .create().show();
	}


	/**
	 * Vibra el telefono
	 * @param context	Contexto de la aplicacion
	 * @param timeMilliseconds	Tiempo a vibrar
	 */
	public static void vibrar(Context context, long timeMilliseconds) {
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(timeMilliseconds);
        vibrator = null;
	}
	
	/**
	 * Valida si una cadena esta en null o si esta con espacios en blanco
	 * @param value	Valor a validar
	 * @return		<code>true</code>: El valor es NULL o vacio.
	 * 				<code>false</code> El valor NO es NULL ni vacio.
	 */
	public static boolean isNullOrEmpty( CharSequence value ) {
		return value == null || value.toString().trim().length() == 0;
	}


	/**
	 * Genera un objeto <code>Notification</code>
	 * 
	 * @param context	Contexto de la aplicacion
	 * @param icon		Icono a mostrar en la notificacion
	 * @param tickerText	Texto en la notificacion
	 * @param when			Hora en que se esta mostrando la notificacion
	 * @param contentTitle	Titulo de la notificacion
	 * @param contentText	Contenido de la notificacion
	 * @param intent		<code>Intent</code> a donde se direccionará
	 * @param flags			Banderas <code>Notification</code>
	 * @return				Objeto <code>Notification</code>
	 */
	public static Notification getNotification(Context context, int icon, CharSequence tickerText, long when, CharSequence contentTitle, 
												CharSequence contentText, Intent intent, int flags, int flagPending, int defaults, int sound ) {
		Notification notif          = new Notification(icon, tickerText, System.currentTimeMillis() );
		Intent intentNotif          = intent;
//		long[] vibrate              = new long[]{0, 100, 200, 300};
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentNotif, flagPending);
		notif.setLatestEventInfo(context, contentTitle, contentText, pendingIntent);
		notif.flags    = flags;
		
		if ( defaults == 0 ) {
			
			notif.vibrate = null;
		}
		if ( sound > 0 ) {
			notif.sound = RingtoneManager.getDefaultUri(sound);
		} else {
			notif.defaults = defaults;
		}
		notif.sound =  null;
		return notif;
	}

	/**
	 * Establece los datos para el {@link View} del header en los {@link Dialog}
	 * @param view		Componente {@link View}
	 * @param titleId	Identificador del titulo a mostrar
	 * @param imageId	Identificador de la imagen a mostrar
	 * @param activity	{@link Activity} que contiene funciones genericas
	 */
	public static void setDataViewHeader ( View view, int titleId, int imageId, Activity activity ) {
		TextView txtTitulo  = (TextView) view.findViewById(R.id.txtTitulo);
		ImageView imgTitulo = (ImageView) view.findViewById(R.id.imgTitulo);
		
		txtTitulo.setText( activity.getString(titleId) );
		imgTitulo.setImageResource(imageId);
	}
	
	/**
	 * 
	 * @param view
	 */
    public static void unbindDrawables(View view) {
        if (view.getBackground() != null) {
        view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
            unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
        ((ViewGroup) view).removeAllViews();
        }
    }
}
