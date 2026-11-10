package mx.bazaldua.guaruramovil.free;  
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mx.bazaldua.guaruramovil.free.activity.TotalLlamadasActivity;
import mx.bazaldua.guaruramovil.free.db.dao.ConfiguracionFrecuentesDAO;
import mx.bazaldua.guaruramovil.free.db.dao.HistoryDAO;
import mx.bazaldua.guaruramovil.free.db.dao.ListaNegraDAO;
import mx.bazaldua.guaruramovil.free.preference.Configuracion;
import mx.bazaldua.guaruramovil.free.service.CallMonitor;
import mx.bazaldua.guaruramovil.free.util.CallUtils;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.ContactUtils;
import mx.bazaldua.guaruramovil.free.util.DateUtil;
import mx.bazaldua.guaruramovil.free.util.Util;
import mx.bazaldua.guaruramovil.free.vo.ContactVO;
import mx.bazaldua.guaruramovil.free.vo.HistorialVO;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
public class IncomingCallReceiver extends BroadcastReceiver {
	
	private static String phoneNumber = null;
	private SharedPreferences pref    = null;
	
	@Override 
	public void onReceive(Context context, Intent intent) {
		pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		Log.d(Constants.LOG_APP, "Licence["+pref.getBoolean(Constants.EXTRA_PARAM_LICENCE, false)+"] Context["+context.getApplicationContext()+"]");
		if (  pref.getBoolean(Constants.EXTRA_PARAM_LICENCE, false) ) { 
			Log.d(Constants.LOG_APP, new StringBuilder("ACTION [").append(intent.getAction()).append("]********").toString());
			
			Bundle bundle = intent.getExtras();
			//Se identifica el numero al se realizara la llamada
			if ( intent.getAction().equals( Intent.ACTION_NEW_OUTGOING_CALL ) ) {
				onOutgoingCall(context, bundle);
			//Se verifica el estado de la conexion
			} else if ( intent.getAction().equals( TelephonyManager.ACTION_PHONE_STATE_CHANGED ) ) {
				onPhoneStateChange(context, bundle);
			}
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param bundle
	 */
	private void onPhoneStateChange(Context context, Bundle bundle) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
		String state                            = bundle.getString(TelephonyManager.EXTRA_STATE);
		Log.d(Constants.LOG_APP, "State["+state+"] bundle["+bundle+"]");
		Intent service = new Intent(context, CallMonitor.class);
 
		//Cuando pasa el estado de la llamada a En Llamada
		if ( TelephonyManager.EXTRA_STATE_OFFHOOK.equalsIgnoreCase( state ) && phoneNumber != null ) {
			if ( phoneNumber.trim().length() > 10 ) {
				phoneNumber = phoneNumber.substring( phoneNumber.trim().length() - 10 );
			}
			Bundle extra = getBundleContact(Constants.TIPO_LLAMADA_SALIENTE, phoneNumber, context);
			if ( extra != null ) {
				service.putExtra(Constants.EXTRA_BUNDLE_LLAMADA, extra);
				//Se inicia el monitoreo del tiempo
				context.startService( service );
			}
			phoneNumber = null;

		//Cuando pasa el estado de la llamada a Espera
		} else if ( TelephonyManager.EXTRA_STATE_IDLE.equalsIgnoreCase( state ) ) {
			//Se detiene el servicio en caso de que se este ejecutando
			context.stopService(service);
			
			notificationManager.cancel( Constants.ALERT_CONTROL_ALARM );
			//Se registra en la tabla historial la llamada
			if (pref.getBoolean(Configuracion.REGISTRAR_TODO, Constants.CONF_DEFAULT_REGISTRAR_TODO) ) registraLlamada(context);
			
		//Cuando cambia el estado de la llamada a Sonando
		} else if ( TelephonyManager.EXTRA_STATE_RINGING.equalsIgnoreCase( state ) ) {
			try {
				String incoming        = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
				ListaNegraDAO dao      = new ListaNegraDAO(context);
				if ( dao.exist(incoming) ) {
					ContactVO vo         = ContactUtils.getContactByPhone(incoming, context.getContentResolver());
					String param         = vo == null ? incoming : vo.getNombre();
					Intent intentForward = new Intent(context, TotalLlamadasActivity.class);
					intentForward.putExtra(Constants.EXTRA_PARAM_RECHAZADA, true);
					intentForward.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
					Log.e(Constants.LOG_APP, "Cancelando LLAMADA [".concat(incoming).concat("]..."));
					CallUtils.endCall(context);
					Util.vibrar(context, 1000);
					notificationManager.notify( incoming, Constants.ALERT_CONTROL_DISMISS, Util.getNotification( context, R.drawable.ic_stat_proteccion, context.getString(R.string.notif_control_dismiss, param), 
							System.currentTimeMillis(), context.getString(R.string.notif_title_llamada_recahzada), 
							param, intentForward, 
							Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT, PendingIntent.FLAG_CANCEL_CURRENT, Notification.DEFAULT_ALL, 0 ) );
					notificationManager = null;
					vo    = null;
					param = null;
					Log.e(Constants.LOG_APP, "LLAMADA Cancelada");						
				} else if ( pref.getBoolean(Configuracion.CONTROLAR_ENTRANTE, Constants.CONF_DEFAULT_CONTROLAR_ENTRANTE) ) {
					Bundle extra = getBundleContact(Constants.TIPO_LLAMADA_ENTRANTE, incoming, context);
					if ( extra != null ) {
						service.putExtra(Constants.EXTRA_BUNDLE_LLAMADA, extra);
						//Se inicia el monitoreo del tiempo
						context.startService( service );
					}
					
				}
				dao.closeConnection();
				dao = null;

			} catch (Exception e) {
				Log.e(Constants.LOG_APP, e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 * @param context
	 * @param bundle
	 */
	private void onOutgoingCall(Context context, Bundle bundle) {
		phoneNumber = bundle.getString( Intent.EXTRA_PHONE_NUMBER );
		verificarTiempo(context);
	}

	/**
	 * Verifica el tiempo total de llamadas de acuerdo al día de corte especificado
	 * @param context	Contexto de la aplicacion.
	 */
	private void verificarTiempo(Context context) {
		SharedPreferences pref                  = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		NotificationManager notificationManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
		HistoryDAO dao                          = new HistoryDAO(context);
		List<HistorialVO> lstLlamadas           = null;
		String condicion                        = Calls.DATE.concat( " BETWEEN ? AND ? AND ".concat( Calls.TYPE ).concat( " = ?" ) );
		String[] valores                        = { String.valueOf(DateUtil.getCutOffDay(context).getTime()), String.valueOf(DateUtil.getCalendar(false).getTimeInMillis()), String.valueOf( Constants.TIPO_LLAMADA_SALIENTE ) };
		long suma                               = 0;
		String mensaje                          = null;
		int pocentajeAlerta                     = pref.getInt(Configuracion.PORCENTAJE_ALERTA, 0);
		int minutosMax                          = Integer.valueOf(pref.getString(Configuracion.MINUTOS_RENTA, "0"));
		boolean redondear                       = pref.getBoolean(Configuracion.TIEMPO_REDONDEADO, Constants.CONF_DEFAULT_REDONDEAR);
		boolean frecuentes                      = pref.getBoolean(Configuracion.MOSTRAR_FRECUENTES, Constants.CONF_DEFAULT_FRECUENTES);
		int minutosAlerta                       = pocentajeAlerta == 0 ? 0 : Integer.valueOf( Double.valueOf(minutosMax * (pocentajeAlerta/100d)).intValue() );
		SparseArray<Object> mapElementos        = null;
		boolean registrarTodo                   = pref.getBoolean(Configuracion.REGISTRAR_TODO, Constants.CONF_DEFAULT_REGISTRAR_TODO);
		
		if ( minutosMax > 0 && pocentajeAlerta > 0 ) {
			if ( registrarTodo ) lstLlamadas = dao.getHistoryLocal( condicion, valores);
			else lstLlamadas = dao.getHistoryPhone( condicion, valores);
			
			if ( lstLlamadas != null ) {
				//Se verifica si no hacen falta registros en la tabla de Historial Local
				if ( registrarTodo ) {
					List<HistorialVO> lstFaltantes = dao.actualizaFaltantes(new Date( lstLlamadas.get(0).getDate() != null ? lstLlamadas.get(0).getDate() : 0), new Date());
					if ( lstFaltantes != null ) lstLlamadas.addAll(0, lstFaltantes);
					lstFaltantes = null;
				}
				
				mapElementos = CallUtils.getLlamadas(lstLlamadas, redondear, frecuentes, context);
				
				if ( mapElementos.get(1) != null ) {
					suma = (Long) mapElementos.get(2);
				} else {
					for (HistorialVO historialVO : lstLlamadas) {
						suma += historialVO.getDuration();
					}
				}
				if ( suma > 0 ) {
					suma /= 60;
					
					Log.d(Constants.LOG_APP, "Duracion llamadas["+suma+"]");
					
					if ( suma > minutosMax ) {
						mensaje =  context.getString(R.string.notif_title_tiempo_excedido, String.valueOf( suma - minutosMax ) );
					} else if ( suma >= minutosAlerta  ) {
						mensaje = context.getString(R.string.notif_title_alerta_tiempo, String.valueOf( suma ), String.valueOf(minutosMax - suma) );
					}
				}
			}
	
			if ( mensaje != null ) {
				Intent intentForward = new Intent(context, TotalLlamadasActivity.class);
				
				notificationManager.notify( Constants.ALERT_CONTROL_ALARM, 
						Util.getNotification( context, suma > minutosMax ? R.drawable.ic_stat_danger : R.drawable.ic_stat_warning, 
											mensaje, 
											System.currentTimeMillis(), 
											context.getString(R.string.notif_title_alerta), 
											mensaje, 
											intentForward, 
											Notification.FLAG_NO_CLEAR, PendingIntent.FLAG_UPDATE_CURRENT, 
											Notification.DEFAULT_ALL, 
											0 ) );
			}
		}
		pref = null;
		notificationManager = null;
		dao.closeConnection();
		dao = null;
		
	}

	/**
	 * Registra la llamada en la BD
	 * @param context
	 */
	private void registraLlamada(Context context) {
		HistoryDAO dao = new HistoryDAO(context);
		HistorialVO vo = dao.getLastCallPhone(0);
		
		if ( vo != null ) {
			if (  !dao.exist( vo ) ) dao.insert( vo );
		}
		dao.closeConnection();
	}

	/**
	 * Obtiene los parametros que se le deben de establecer al servicio cuando se encuentra un numero frecuente
	 * @param tipoLlamada	Tipo de llamada que se va a controlar
	 * @param phoneNumber	Numero telefonico a establecer
	 * @param context		Contexto de la aplicacion
	 * @return				Bundle con los parametros necesarios para el servicio
	 */
	private Bundle getBundleContact( int tipoLlamada, String phoneNumber, Context context ) {
		Bundle extra                   = null;
		ConfiguracionFrecuentesDAO dao = new ConfiguracionFrecuentesDAO(context);
		Cursor cur                     = dao.getNumeroFrecuente(phoneNumber);
		
		if ( cur.getCount() == 1 ) {
			cur.moveToFirst();
			Calendar cal = Calendar.getInstance();
			int remarcar = cur.getInt( cur.getColumnIndex("remarcar") );
			extra        = new Bundle();
			Log.d(Constants.LOG_APP, "Extableciendo parametros para tipo llamada["+tipoLlamada+"]");
			extra.putInt(Constants.EXTRA_PARAM_TIPO_LLAMADA, tipoLlamada);
			extra.putString(Constants.EXTRA_PARAM_TELEFONO, phoneNumber);
			extra.putLong(Constants.EXTRA_PARAM_HORA_INICIO, cal.getTimeInMillis());
			cal.add(Calendar.MINUTE, cur.getInt( cur.getColumnIndex("minutos") ));
			cal.add(Calendar.SECOND, cur.getInt( cur.getColumnIndex("segundos") ));
			extra.putLong(Constants.EXTRA_PARAM_HORA_TERMINO, cal.getTimeInMillis());
			extra.putBoolean(Constants.EXTRA_PARAM_REMARCAR, Boolean.valueOf( remarcar == 0 ? false : true ));
		}
		cur.close();
		cur = null;
		dao.closeConnection();
		return extra;
	}
} 