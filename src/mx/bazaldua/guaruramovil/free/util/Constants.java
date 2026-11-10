/**
 * 
 */
package mx.bazaldua.guaruramovil.free.util;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public interface Constants {
	String LOG_APP 					= "EstadisticasLog";
	int DIALOG_FILTRO_FECHAS 		= 1;
	int DIALOG_AGREGAR_FRECUENTE	= 2;
	int DIALOG_REMARCAR				= 3;
	
	String EXTRA_BUNDLE_LLAMADA			= "Llamada";
	String EXTRA_PARAM_TELEFONO			= "Telefono";
	String EXTRA_PARAM_HORA_INICIO		= "Inicio";
	String EXTRA_PARAM_HORA_TERMINO		= "Termina";
	String EXTRA_PARAM_REMARCAR			= "Remarcar";
	String EXTRA_PARAM_NOMBRE			= "Nombre";
	String EXTRA_PARAM_TIPO_TELEFONO	= "TipoTelefono";
	String EXTRA_PARAM_ID				= "Id";
	String EXTRA_PARAM_PICK				= "Pick";
	String EXTRA_PARAM_RECHAZADA		= "LlamadaRechazada";
	String EXTRA_PARAM_TIPO_LLAMADA		= "TipoLlamada";
	String EXTRA_PARAM_LICENCE			= "Guarura";
	
	int MENU_AGREGAR_NUMERO_AGENDA		= 1;
	int MENU_ELIMINAR_NUMEROS			= 2;
	int MENU_TIEMPO_REDONDEADO			= 3;
	int MENU_NO_FRECUENTES				= 4;
	int MENU_AGREGAR_NUMERO_SIM			= 5;
	int MENU_AGREGAR_NUMERO				= 6;
	int MENU_AGREGAR_NUMERO_HISTORIAL	= 7;
	
	int ACTION_PICK_NUMBER			= 1;
	int ACTION_PICK_NUMBER_SIM		= 2;
	int ACTION_PICK_NUMBER_HISTORY	= 3;
	
	int OPERACION_LLAMAR			= 0;
	int OPERACION_MODIFICAR			= 1;
	int OPERACION_ELIMINAR			= 2;
	
	String CONF_DEFAULT_TIEMPO_ALERTA	= "15"; 
	String CONF_DEFAULT_TIEMPO_REMARCA	= "10";
	String CONF_DEFAULT_DIA_CORTE 		= "1";
	boolean CONF_DEFAULT_REMARCAR		= false;
	boolean CONF_DEFAULT_REDONDEAR		= false;
	boolean CONF_DEFAULT_FRECUENTES		= true;
	boolean CONF_DEFAULT_VIBRAR_FRECUENTE	= true;
	boolean CONF_DEFAULT_OPCION_REMARCAR	= true;
	boolean CONF_DEFAULT_CONTROLAR_ENTRANTE	= false;
	boolean CONF_DEFAULT_REGISTRAR_TODO		= false;
	
	int PROCESO_FINALIZADO			= 1;
	int PROCESO_FINALIZADO_ERROR	= 2;
	
	int  CONTACTO_AGENDA			= 0;
	int  CONTACTO_SIM				= 1;
	int  CONTACTO_DECONOCIDO		= -2;
	
	int ALERT_CONTROL_CALL			= 1;
	int ALERT_CONTROL_DISMISS		= 2;
	int ALERT_CONTROL_ALARM			= 3;
	
	int TIPO_LLAMADA_ENTRANTE		= 1;
	int TIPO_LLAMADA_SALIENTE		= 2;
}
