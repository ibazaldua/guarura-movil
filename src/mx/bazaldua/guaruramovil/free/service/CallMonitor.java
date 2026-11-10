/**
 * 
 */
package mx.bazaldua.guaruramovil.free.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.activity.RemarcarDialogActivity;
import mx.bazaldua.guaruramovil.free.preference.Configuracion;
import mx.bazaldua.guaruramovil.free.util.AudioUtil;
import mx.bazaldua.guaruramovil.free.util.CallUtils;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.Util;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.util.Log;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class CallMonitor extends Service {
	private Bundle bundle;
	private int tipoLlamada;
	private Calendar initCall;
	private Calendar alarmCall;
	private Calendar endCall;
	private boolean remarcar;
	private boolean primero;
	private boolean remarmarMostrado;
	private AudioUtil audioUtil;
	private NotificationManager notificationManager;
	private static final int DELAY = 250;
	SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy hh:mm:ss" );
	SimpleDateFormat dateFormatMMSS = new SimpleDateFormat( "mm:ss" );
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {			
			ejecutarTarea();
		};
	};
	
	/**
	 * Establece la notificacion con el tiempo restante de la llamada
	 */
	private void setNotification( String time, int soundAlarm ) {
		notificationManager    = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
		notificationManager.notify( Constants.ALERT_CONTROL_CALL, Util.getNotification( this, R.drawable.ic_stat_frecuentes, getString(R.string.notif_control_call), 
																					System.currentTimeMillis(), getString(R.string.notif_time, time), 
																					getString(R.string.notif_control_call), new Intent(Intent.ACTION_VIEW, CallLog.Calls.CONTENT_URI), 
																					Notification.FLAG_NO_CLEAR, PendingIntent.FLAG_UPDATE_CURRENT, time==null?Notification.DEFAULT_VIBRATE : 0, soundAlarm ) );
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

		setNotification( null, 0 );
		
		if ( pref.getBoolean(Configuracion.VIBRAR_FRECUENTE, Constants.CONF_DEFAULT_FRECUENTES) ) Util.vibrar(this, 1000);
		
		bundle    = intent.getBundleExtra(Constants.EXTRA_BUNDLE_LLAMADA);
		initCall  = Calendar.getInstance();
		endCall   = Calendar.getInstance();
		alarmCall = Calendar.getInstance();
		remarcar  = bundle.getBoolean(Constants.EXTRA_PARAM_REMARCAR);

		tipoLlamada = bundle.getInt(Constants.EXTRA_PARAM_TIPO_LLAMADA);
		initCall.setTimeInMillis( bundle.getLong(Constants.EXTRA_PARAM_HORA_INICIO) );
		endCall.setTimeInMillis( bundle.getLong(Constants.EXTRA_PARAM_HORA_TERMINO) );
		alarmCall.setTimeInMillis( bundle.getLong(Constants.EXTRA_PARAM_HORA_TERMINO) - ( Integer.valueOf(pref.getString( Configuracion.TIEMPO_ALERTA_FIN_LLAMADA, Constants.CONF_DEFAULT_TIEMPO_ALERTA)) * 1000 ) );
		primero = true;
		remarmarMostrado = false;
		Log.d(Constants.LOG_APP, new StringBuilder("remarcar[").append(remarcar).append("] Inicio[").append(dateFormat.format(initCall.getTime()))
								.append("] Termina[").append(dateFormat.format(endCall.getTime()))
								.append("] alarm[").append(dateFormat.format(alarmCall.getTime())).append("]").toString());
		
		// Iniciamos el servicio
		iniciarServicio();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	public void onDestroy() {
		super.onDestroy();

		// Detenemos el serviciop
		this.finalizarServicio();

		Log.d(Constants.LOG_APP, "Servicio detenido");
	}

	/**
	 * 
	 */
	public void iniciarServicio() {
		try {
			Log.d(Constants.LOG_APP, "Iniciando servicio...");

			Thread thread = new Thread() {
				public void run() {
					handler.sendEmptyMessage(0);
				};				
			};
			Thread.yield();
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.run();
			Log.d(Constants.LOG_APP, "Temporizador iniciado");
		} catch(Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		}
	}

	/**
	 * Finalizando servicio
	 */
	public void finalizarServicio() {
		try {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			Log.d(Constants.LOG_APP, "Finalizando servicio...");
			// Detenemos el timer
			notificationManager.cancel( Constants.ALERT_CONTROL_CALL );
			
			if ( audioUtil != null && audioUtil.isPlaying() ) audioUtil.stop();
			else if ( audioUtil != null ) audioUtil.restoreVolume();
			
			if ( handler != null ) handler.removeMessages(0);
			
			//Se verifica si la llamada se cancelo en el intervalo de finalizar llamada y alarma de llamada para mandar la
			//opcion de volver a marcar al numero frecuente
			if ( tipoLlamada == Constants.TIPO_LLAMADA_SALIENTE &&
				Calendar.getInstance().after( alarmCall ) &&
				remarcar &&
				pref.getBoolean(Configuracion.OPCION_REMARCAR, Constants.CONF_DEFAULT_OPCION_REMARCAR) ) showOptionRedial();

			handler   = null;
			initCall  = null;
			alarmCall = null;
			endCall   = null;
			pref      = null;
			Log.d(Constants.LOG_APP, "Temporizador detenido");
		} catch(Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		}
	}

	/**
	 * Realiza la revision del tiempo para saber cuando cortar la llamada
	 */
	private synchronized void ejecutarTarea() {
		//Establece la notificacion con el tiempo
		setNotification(dateFormatMMSS.format( endCall.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() ), 0);
		
		//Se manda notificacion de que se acabara la llamada
		if ( primero && Calendar.getInstance().after( alarmCall )  ) {
			Log.d(Constants.LOG_APP, "SONANDO ALARMA [".concat( dateFormat.format( Calendar.getInstance().getTime() ) ).concat( "]" ) );
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( this );
			Util.vibrar(this, pref.getBoolean( Configuracion.ALARMA_VIBRAR, true) ? 25 : 0);
			primero = false;
			
			audioUtil = new AudioUtil(this, R.raw.beep1, 0.1f, 0.1f);
			audioUtil.setLoop(pref.getBoolean( Configuracion.ALARMA_REPETITIVA, true));
			audioUtil.start(); 
			
//			setNotification(dateFormatMMSS.format( endCall.getTimeInMillis() - Calendar.getInstance().getTimeInMillis() ), R.raw.beep1 );
			handler.sendEmptyMessageDelayed(0, DELAY);
		} else if ( Calendar.getInstance().after( endCall ) ) {
			Log.d(Constants.LOG_APP, "Terminando Llamada [".concat( dateFormat.format( Calendar.getInstance().getTime() ) ).concat( "]" ) );
			try {
				CallUtils.endCall(getApplicationContext());
				finalizarServicio();
				//Se verifica si se debe de remarcar
				if ( tipoLlamada == Constants.TIPO_LLAMADA_SALIENTE && remarcar ) {
					showOptionRedial();
				}
				bundle   = null;
			} catch (Exception e) {
				Log.e(Constants.LOG_APP, "ERROR AL FINALIZAR LA LLAMADA", e);
			} catch (Throwable e) {
				Log.e(Constants.LOG_APP, "ERROR AL FINALIZAR SERVICIO", e);
			}
		} else {
			handler.sendEmptyMessageDelayed(0, DELAY);
		}
	}

	/**
	 * Llamada a la opcion de volver a marcar al numero frecuente
	 */
	private void showOptionRedial() {
		if ( !remarmarMostrado ) {
			remarmarMostrado = true;
			Intent intent = new Intent(this, RemarcarDialogActivity.class);
			intent.putExtra(Constants.EXTRA_BUNDLE_LLAMADA, bundle);
			intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT );
			startActivity(intent);
			intent = null;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
