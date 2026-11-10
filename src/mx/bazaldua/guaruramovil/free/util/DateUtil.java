package mx.bazaldua.guaruramovil.free.util;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import mx.bazaldua.guaruramovil.free.preference.Configuracion;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;

public class DateUtil {

	/**
	 * 
	 * @param time
	 * @param formato <code>true</code>: Se obtendra un formado de HH:MM:SS
	 * 				<code>false</code> Se obtendran los minutos realizados
	 * @return
	 */
	public static String getTimeString( long time, boolean formato ) {
	    Calendar cal            = Calendar.getInstance();
	    DecimalFormat format    = new DecimalFormat( "00" );
	    cal.setTimeInMillis( time * 1000 );
		return formato ? format.format( Math.floor( time / 3600 ) ) + ":" + 
						format.format(cal.get( Calendar.MINUTE ) ) + ":" +
						format.format( cal.get( Calendar.SECOND ) ) : 
				String.valueOf( time / 60 )
					;
	}

	/**
	 * 
	 * @param datePicker
	 * @return
	 */
	public static Date getDateFromDTP ( DatePicker datePicker, boolean inicio ) {
		Calendar cal = getCalendar(inicio);
		cal.set( Calendar.DAY_OF_MONTH , datePicker.getDayOfMonth() );
		cal.set( Calendar.MONTH , datePicker.getMonth() );
		cal.set( Calendar.YEAR , datePicker.getYear() );
		return new Date( cal.getTimeInMillis() );
	}
	
	/**
	 * Devuelve un objeto calendar con la fecha actual, ya sea iniio de día o fin de día
	 * @param inicio	<code>true</code>: Fecha Actual con Inicio de día
	 * 					<code>false</code>: Fecha Actual con fin de día
	 * @return	Fecha actual, ya sea iniio de día o fin de día
	 */
	public static Calendar getCalendar (boolean inicio) {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.HOUR_OF_DAY , inicio ? 0 : 23 );
		cal.set( Calendar.MINUTE , inicio ? 0 : 59 );
		cal.set( Calendar.SECOND , inicio ? 0 : 59 );
		return cal;
	}

	/**
	 * 
	 * @return
	 */
	public static Date getDay1Date() {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.DAY_OF_MONTH , 1 );
		cal.set( Calendar.HOUR_OF_DAY , 0 );
		cal.set( Calendar.MINUTE , 0 );
		cal.set( Calendar.SECOND , 0 );
		return new Date( cal.getTimeInMillis() ) ;
	}

	/**
	 * Establece la fecha del mes actual derivada del día de corte configurado
	 * @param context	Contexto de la aplicacion
	 * @return			<code>Date</code> con la fecha de acuerdo al dia de corte elegido
	 */
	public static Date getCutOffDay ( Context context ) {
		Calendar cal           = getCalendar(true);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		
		cal.set(Calendar.DATE, Integer.valueOf( pref.getString( Configuracion.DIA_CORTE , Constants.CONF_DEFAULT_DIA_CORTE) ));
		
		if ( cal.compareTo( Calendar.getInstance() ) == 1 ) cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
		
		pref = null;
		return new Date( cal.getTimeInMillis() );
	}
	
	/**
	 * Formatea a la formato establecido en el dispositivo 
	 * @param date	Fecha a formatear
	 * @return		Fecha formateada
	 */
	public static String formatDate( Date date ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis( date.getTime() );
		return formatDate(calendar);
	}
	
	/**
	 * Formatea a la formato establecido en el dispositivo 
	 * @param date	Fecha a formatear tipo <code>DatePicker</code>
	 * @param inicio	<code>true</code>: Establece la hora, minutos y segundos a 0
	 * @param 			<code>false</code>:	Establece la hora, minutos y segundos a su valor final
	 * @return		Fecha formateada
	 * 
	 */
	public static String formatDate( DatePicker date, boolean inicio ) {
		return formatDate( getDateFromDTP(date, inicio) );
	}
	
	/**
	 * Formatea a la formato establecido en el dispositivo 
	 * @param calendar	Fecha a formatear
	 * @return		Fecha formateada
	 */
	public static String formatDate( Calendar calendar ) {
		return DateFormat.format("dd/MM/yyyy", calendar).toString();
	}
	
	/**
	 * Devuelve una fecha a partir de un texto
	 * @param dateText	Fecha a transformar
	 * @return			Fecha en objeto <code>Calendar</code>
	 */
	public static Calendar getCalendarFromText ( String dateText ) {
		Calendar calendar = Calendar.getInstance();
		StringTokenizer token = new StringTokenizer(dateText, "/");
		
		if ( dateText.length() == 10 && token.countTokens() == 3) {
			try {
				calendar.set(Calendar.DAY_OF_MONTH,  Integer.valueOf( token.nextElement().toString() ) );
				calendar.set(Calendar.MONTH,  Integer.valueOf( token.nextElement().toString() ) - 1 );
				calendar.set(Calendar.YEAR,  Integer.valueOf( token.nextElement().toString() ) );
			}catch (Exception e) {
				calendar = Calendar.getInstance();
				Log.e(Constants.LOG_APP, new StringBuilder("Error al convertir fecha [").append(dateText).append("]").toString(), e);
			}
		}
		return calendar;
	}
}
