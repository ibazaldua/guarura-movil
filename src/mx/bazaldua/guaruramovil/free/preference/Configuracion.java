/**
 * 
 */
package mx.bazaldua.guaruramovil.free.preference;

import mx.bazaldua.guaruramovil.free.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class Configuracion extends PreferenceActivity {
	public static final String REMARCAR						= "chkDefRemarcar";
	public static final String TIEMPO_ALERTA_FIN_LLAMADA	= "txtDefTiempoAlarma";
	public static final String TIEMPO_REMARCACION			= "txtDefTiempoRemarcar";
	public static final String DIA_CORTE					= "txtDefDiaCorte";
	public static final String MINUTOS						= "txtDefTiempoMinutos";
	public static final String SEGUNDOS 					= "txtDefTiempoSegundos";
	public static final String ALARMA_REPETITIVA 			= "chkDefAlarmaRepetitiva";
	public static final String TIEMPO_REDONDEADO 			= "chkDefTiempoRedondeado";
	public static final String MOSTRAR_FRECUENTES			= "chkDefMostrarFrecuentes";
	public static final String ALARMA_VIBRAR				= "chkAlarmaVibrar";
	public static final String VIBRAR_FRECUENTE				= "chkDefVibrarDetFrecuente";
	public static final String OPCION_REMARCAR				= "chkDefOpcionRemarcar";
	public static final String CONTROLAR_ENTRANTE			= "chkDefControlarEntrante";
	public static final String REGISTRAR_TODO				= "chkDefRegistrarTodo";
	public static final String MINUTOS_RENTA				= "txtDefMinutosPlan";
	public static final String PORCENTAJE_ALERTA			= "txtPorcentajeAlerta";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.configuracion);
	}
	
	
	
}
