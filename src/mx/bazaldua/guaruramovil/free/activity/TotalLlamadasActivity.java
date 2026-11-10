package mx.bazaldua.guaruramovil.free.activity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.adapter.RowLLamadaAdapter;
import mx.bazaldua.guaruramovil.free.db.dao.HistoryDAO;
import mx.bazaldua.guaruramovil.free.preference.Configuracion;
import mx.bazaldua.guaruramovil.free.util.CallUtils;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.DateUtil;
import mx.bazaldua.guaruramovil.free.util.Util;
import mx.bazaldua.guaruramovil.free.vo.HistorialVO;
import mx.bazaldua.guaruramovil.free.vo.LlamadaVO;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

/**
 * 
 * @author Iván Bazaldúa Ramírez
 *
 */
public class TotalLlamadasActivity extends TabActivity {
	private static final String PREF_STICKY_TAB = "stickyTab";
	private static final int DURACION_TOTAL 	= 5;
	long tiempoTotal               				= 0L;
	int minutosMaximo              				= 0;
	int pocentajeAlerta							= 0;
	private TabHost tabHost;
	private SparseArray<List<LlamadaVO>> mapResultados;
	private SparseArray<Long> mapDuracion;
	private Map<Long, Bitmap> mapFotos;
	private boolean redondear;
	private boolean frecuentes = true;
	private boolean todoHistorial;
	private Boolean actionPick;
	private String telefono;
	private ProgressDialog progressDialog;
	private Handler progressHandler = new Handler () {
		@Override
		public void handleMessage(Message msg) {
			if ( msg.what == Constants.PROCESO_FINALIZADO ) muestraDatosPestania();
			else mostrarSinDatos(); 
			progressDialog.dismiss();
		}
	};
	
	/**
	 * Establece variables a vacio y manda mensaje de que no se encontraron datos 
	 */
	private void mostrarSinDatos() {
		mapResultados = new SparseArray<List<LlamadaVO>>();
		mapDuracion   = new SparseArray<Long>();
		mapFotos      = new HashMap<Long, Bitmap>();
		muestraDatosPestania();
		Util.mensajeToast(this, getString(R.string.toast_data_not_found));
	}

	@Override
	protected void onStart() {
		super.onStart();
		setProgressBarIndeterminateVisibility(false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setProgressBarIndeterminateVisibility(false);
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView( R.layout.total_llamadas );
		inicializa();
	}

	/**
	 * Inicializa Activity
	 */
	private void inicializa() {
		final TextView lblFechaIni      = (TextView) findViewById(R.id.lblFechaIni);
		final TextView lblFechaFin      = (TextView) findViewById(R.id.lblFechaFin);
		final ListView lsvResultado     = (ListView) findViewById(R.id.lsvResultado);
		final TextView lblDuracionTotal = (TextView) findViewById(R.id.lblDuracionTotal);
		final Calendar calIni           = DateUtil.getCalendar(true);
		final Calendar calFin           = DateUtil.getCalendar(false);
    	TextView textView1 = (TextView) findViewById(R.id.TextView01);

		tabHost                         = getTabHost();
    	actionPick                      = getIntent().getBooleanExtra(Constants.EXTRA_PARAM_PICK, false);
		OnTouchListener onTouch         = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				TextView textView = (TextView) v;
				if ( event.getAction() == MotionEvent.ACTION_DOWN ) textView .setTextColor( Color.parseColor("#A9A9A9") );	
				else textView .setTextColor( Color.parseColor("#0E9AFF") );
				return false;
			}
		};

    	Animation animation = AnimationUtils.loadAnimation(this, R.anim.tweenanimation);
    	textView1.startAnimation(animation);
		
    	Log.d(Constants.LOG_APP, "Action PICK["+actionPick+"]");
    	
		calIni.setTimeInMillis( DateUtil.getCutOffDay(this).getTime() );
		lblFechaIni.setText( DateUtil.formatDate(calIni) );
		lblFechaFin.setText( DateUtil.formatDate(calFin) );
		
		lblFechaIni.setOnTouchListener( onTouch );
		lblFechaFin.setOnTouchListener( onTouch );
		
		lblDuracionTotal.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView lblDuracion = (TextView) v;
				if ( lblDuracion.getText().toString().indexOf(":") != -1 ) lblDuracion.setText( DateUtil.getTimeString(tiempoTotal, false) );
				else lblDuracion.setText( DateUtil.getTimeString(tiempoTotal, true) );
				
				setColorMinutos( lblDuracion );
			}
		} );
		
		lblFechaIni.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DatePickerDialog.OnDateSetListener dtpListener = new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year, int month, int day) {
						lblFechaIni.setText( DateUtil.formatDate(datePicker, true) );
						consultrarHistorial(DateUtil.getDateFromDTP(datePicker, true),new Date( DateUtil.getCalendarFromText(lblFechaFin.getText().toString()).getTimeInMillis() ));
					}
				};
				Calendar calIni = DateUtil.getCalendarFromText(  lblFechaIni.getText().toString() );
				DatePickerDialog dtp = new DatePickerDialog(TotalLlamadasActivity.this, dtpListener, calIni.get(Calendar.YEAR), calIni.get(Calendar.MONTH), calIni.get(Calendar.DATE));
				dtp.show();
			}
		} );
		
		lblFechaFin.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DatePickerDialog.OnDateSetListener dtpListener = new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker datePicker, int year, int month, int day) {
						lblFechaFin.setText( DateUtil.formatDate(datePicker, false) );
						consultrarHistorial(new Date( DateUtil.getCalendarFromText(lblFechaIni.getText().toString()).getTimeInMillis() ), DateUtil.getDateFromDTP(datePicker, false));
					}
				};
				Calendar calFin = DateUtil.getCalendarFromText(  lblFechaFin.getText().toString() );
				DatePickerDialog dtp = new DatePickerDialog(TotalLlamadasActivity.this, dtpListener, calFin.get(Calendar.YEAR), calFin.get(Calendar.MONTH), calFin.get(Calendar.DATE));
				dtp.show();
			}
		} );
		
		if ( actionPick ) {
			lsvResultado.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
					TextView lblTelefono = (TextView) view.findViewById(R.id.lblTelefono);
					telefono = lblTelefono.getText().toString();
					DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							regresarTelefono();
						}
					};
					Util.confirm(TotalLlamadasActivity.this, getString(R.string.dialog_title_confirm), getString( R.string.dialog_msg_agregar_lista_negra, telefono ), listener, null);
				}
			} );
		}
		
		
		creaPestanias();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean rechazadas = getIntent().getBooleanExtra(Constants.EXTRA_PARAM_RECHAZADA, false);
		int currentTab = rechazadas ? 3 : prefs.getInt(PREF_STICKY_TAB, 0);
		
		redondear       = prefs.getBoolean(Configuracion.TIEMPO_REDONDEADO, Constants.CONF_DEFAULT_REDONDEAR);
		frecuentes      = prefs.getBoolean(Configuracion.MOSTRAR_FRECUENTES, Constants.CONF_DEFAULT_FRECUENTES);
		todoHistorial   = prefs.getBoolean(Configuracion.REGISTRAR_TODO, Constants.CONF_DEFAULT_REGISTRAR_TODO);
		minutosMaximo   = Integer.parseInt( prefs.getString(Configuracion.MINUTOS_RENTA, "0") );
		pocentajeAlerta = prefs.getInt(Configuracion.PORCENTAJE_ALERTA, 0);
		
		tabHost.setCurrentTab(currentTab);
		
		consultrarHistorial( new Date( calIni.getTimeInMillis() ), new Date( calFin.getTimeInMillis() ) );
		prefs = null;
	}
	
	/**
	 * Devuelve el número telefonico para agregarlo a la lista negra
	 */
	private void regresarTelefono() {
		Intent intent = new Intent();
		intent.putExtra(Constants.EXTRA_PARAM_TELEFONO, telefono);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	/**
	 *  
	 */
	private void creaPestanias() {
		TabSpec tab1 = tabHost.newTabSpec("Entrantes")
				.setContent(R.id.tab1)
				.setIndicator( getString(R.string.tab_1_entrantes), getResources().getDrawable(android.R.drawable.sym_call_incoming) );
		TabSpec tab2 = tabHost.newTabSpec("Salientes")
				.setContent(R.id.tab1)
				.setIndicator(getString(R.string.tab_2_salientes), getResources().getDrawable(android.R.drawable.sym_call_outgoing) );

		TabSpec tab3 = tabHost.newTabSpec("Perdidas")
				.setContent(R.id.tab1)
				.setIndicator(getString(R.string.tab_3_perdidas), getResources().getDrawable(android.R.drawable.sym_call_missed) );
		
		TabSpec tab4 = tabHost.newTabSpec("Todas")
				.setContent(R.id.tab1)
				.setIndicator(getString(R.string.tab_4_todas), getResources().getDrawable(android.R.drawable.ic_menu_recent_history) );

		TabSpec tab5 = tabHost.newTabSpec("Rechazadas")
				.setContent(R.id.tab1)
				.setIndicator(getString(R.string.tab_5_rechazadas), getResources().getDrawable(android.R.drawable.presence_busy) );

		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
		tabHost.addTab(tab3);
		tabHost.addTab(tab5);
		tabHost.addTab(tab4);
		
		tabHost.setOnTabChangedListener( new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String arg0) {
				muestraDatosPestania();
			}
		} );
	}
	
	/**
	 * Realiza la consulta del historial de llamadas.
	 * @param fechaInicial	Fecha inicial a buscar
	 * @param fechaFinal	Fecha final a buscar
	 */
	private void consultrarHistorial( final Date fechaInicial, final Date fechaFinal ) {
		StringBuilder condicion              = null;
		String[] valores                     = null;
		HistoryDAO dao                       = new HistoryDAO(this);
		
		if ( fechaInicial != null ) {
			valores   =  new String[2];
			condicion = new StringBuilder();
			condicion.append( Calls.DATE ).append( " BETWEEN ? AND ?" );
			valores[0] = String.valueOf(fechaInicial.getTime());
			valores[1] = String.valueOf(fechaFinal.getTime());
		}
		
		//Se obtiene el Historial de llamadas
		final List<HistorialVO> lstHistorial = todoHistorial ?  dao.getHistoryLocal(condicion==null?null:condicion.toString(), valores) : dao.getHistoryPhone(condicion==null?null:condicion.toString(), valores);
		
		//Se agrega los ultimos registros en el historial del telefono
		if ( todoHistorial && lstHistorial != null ) {
			
			Date fechaIniTemp              = null;
			List<HistorialVO> lstFaltantes = null;
			
			if ( lstHistorial.size() > 0 ) fechaIniTemp = new Date( lstHistorial.get(0).getDate() );

			lstFaltantes = dao.actualizaFaltantes(fechaIniTemp, fechaFinal);
			
			if ( lstFaltantes != null ) lstHistorial.addAll(0, lstFaltantes);
//			String[] valor                 = new String[2];
//			List<HistorialVO> lstFaltantes = null;
//			
//			if ( lstHistorial.size() > 0 ) {
//				valor[0] = lstHistorial.get(0).getDate().toString();
//				valor[1] = String.valueOf(fechaFinal.getTime());
//			} else {
//				valor[0] = "0";
//				valor[1] = String.valueOf(fechaFinal.getTime());
//			}
//
//			lstFaltantes = dao.getHistoryPhone(Calls.DATE + " BETWEEN ? AND ? ", valor);
//			
//			if ( lstFaltantes != null && lstFaltantes.size() > 0 )
//				for (int i = lstFaltantes.size() - 1; i >= 0 ; i--) {
//					if ( !lstFaltantes.get(i).getDate().equals( lstHistorial.get(0).getDate() ) ) {
//						Log.d(Constants.LOG_APP, "Insertando Faltante");
//						lstHistorial.add(0, lstFaltantes.get(i));
//						//Se guardan en la BD los registros faltantes
//						dao.insert( lstFaltantes.get(i) );
//					}
//				}
		}
		
		if ( lstHistorial == null || lstHistorial.size() == 0 ) {
			dao.closeConnection();
			dao = null;
			mostrarSinDatos();
			return;			
		}
		
		dao.closeConnection();
		dao = null;
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage( getString(R.string.dialog_msg_processing) );
		progressDialog.setMax( lstHistorial.size() );
		progressDialog.setIndeterminate(false);
		progressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
		progressDialog.setProgress(1);
		progressDialog.setCancelable(false);
		progressDialog.show();

		//Se crea Hilo para poder mostrar ventana de dialogo
		new Thread( new Runnable() {
			@Override
			public void run() {
				Message msg = progressHandler.obtainMessage();


				Long totalDuracion = 0L;
				mapResultados      = new SparseArray<List<LlamadaVO>>();
				mapDuracion        = new SparseArray<Long>();
				mapFotos           = new HashMap<Long, Bitmap>();

				for (HistorialVO historialVO : lstHistorial) {
					if ( mapResultados.indexOfKey( historialVO.getType() ) < 0 ) mapResultados.put( historialVO.getType(), new ArrayList<LlamadaVO>() );
					if ( mapDuracion.indexOfKey( historialVO.getType() ) < 0 ) mapDuracion.put( historialVO.getType(), 0L );
					
					mapResultados.get( historialVO.getType() ).add( new LlamadaVO(-1L, historialVO.getName(), historialVO.getNumber(), new Date(historialVO.getDate()), historialVO.getDuration().intValue(), historialVO.getType()) );
					mapDuracion.put( historialVO.getType(), mapDuracion.get(historialVO.getType()) + historialVO.getDuration() );
					totalDuracion += historialVO.getDuration();
					progressDialog.incrementProgressBy(1);							
				}
				
				mapDuracion.put( DURACION_TOTAL , totalDuracion );
				msg.what = Constants.PROCESO_FINALIZADO;
		
				progressHandler.sendMessage(msg);
			}
		} ).start();
	}
	
	/**
	 *  Muestra los datos para las pesta&ntilde;a indicada
	 */
	@SuppressWarnings("unchecked")
	private void muestraDatosPestania() {
		RowLLamadaAdapter adaptador = null;
		ListView lsvResultado          = (ListView) findViewById(R.id.lsvResultado);
		TextView lblTotalLlamadas      = (TextView) findViewById(R.id.lblTotalLlamadas);
		TextView lblDuracionTotal      = (TextView) findViewById(R.id.lblDuracionTotal);
		List<LlamadaVO> lstResultado   = null;
		int tipo                       = tabHost.getCurrentTab() + 1;

		try {
			if ( mapResultados != null ) {
				//Si la pestaña actual es todas se establece el list de todas
				lstResultado = tipo == DURACION_TOTAL ? getTodasLlamadas () : mapResultados.get( tipo );
				if ( lstResultado != null && lstResultado.size() > 0 ) {
					SparseArray<Object> mapResult =  CallUtils.getLlamadas( lstResultado, redondear, frecuentes, this );
					//Se verifica si se realizo modificacion a la lista original
					if ( mapResult.get(1) != null ) {
						lstResultado = (List<LlamadaVO>) mapResult.get(1);
						tiempoTotal  = (Long) mapResult.get(2);
					} else {
						tiempoTotal  = mapDuracion.get( tipo );
					}
					adaptador = new RowLLamadaAdapter(this, R.layout.elemento_llamada, lstResultado );
					if ( tipo == DURACION_TOTAL ) adaptador.sort( new LlamadaVO() );
					adaptador.setMapFotos(mapFotos);
					lsvResultado.setAdapter( adaptador );
					lblDuracionTotal.setText( DateUtil.getTimeString( tiempoTotal, true ) );
					lblTotalLlamadas.setText( String.valueOf( lstResultado.size() ) );
					setColorMinutos( lblDuracionTotal );
				} else {
					lsvResultado.setAdapter( null );
					lblDuracionTotal.setText( String.valueOf( 0 ) );
					lblTotalLlamadas.setText( String.valueOf( 0 ) );
				}
			}
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @return
	 */
    private List<LlamadaVO> getTodasLlamadas() {
    	List<LlamadaVO> lstTodas = null; 
    	if ( mapResultados != null ) {
    		lstTodas = new ArrayList<LlamadaVO>();
    		for ( int i = 0; i < mapResultados.size(); i++ ) lstTodas.addAll( mapResultados.get( mapResultados.keyAt(i) ) );
    	}
		return lstTodas;
	}

	@Override
    protected void onPause() {
        super.onPause();
        // Cuando se cierra la aplicación guardamos la pestaña activa
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        int currentTab = tabHost.getCurrentTab();
        editor.putInt(PREF_STICKY_TAB, currentTab);
        editor.commit();
        prefs  = null;
        editor = null;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.consultas, menu);
		
		menu.findItem(R.id.menu_tiempo_redondeado).setTitle( redondear ? R.string.tiempo_normal : R.string.tiempo_redondeado  ).setIcon(redondear ? R.drawable.ic_menu_facturar_on : R.drawable.ic_menu_facturar_off);
		menu.findItem(R.id.menu_numeros_frecuentes).setTitle( frecuentes ? R.string.no_mostrar_frecuentes : R.string.mostrar_frecuentes  ).setIcon(frecuentes ? R.drawable.ic_menu_filtro_frecuente_on : R.drawable.ic_menu_filtro_frecuente_off);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch ( item.getItemId() ) {
			case R.id.menu_tiempo_redondeado:
				redondear = !redondear;
				muestraDatosPestania();
				item.setTitle( redondear ? R.string.tiempo_normal : R.string.tiempo_redondeado  );
				item.setIcon( redondear ? R.drawable.ic_menu_facturar_on : R.drawable.ic_menu_facturar_off);
				break;
				
			case R.id.menu_numeros_frecuentes:
				frecuentes = !frecuentes;
				muestraDatosPestania();
				item.setTitle( frecuentes ? R.string.no_mostrar_frecuentes : R.string.mostrar_frecuentes  );
				item.setIcon(frecuentes ? R.drawable.ic_menu_filtro_frecuente_on : R.drawable.ic_menu_filtro_frecuente_off);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	/**
	 * 
	 * @param lblDuracion
	 */
	private void setColorMinutos(TextView lblDuracion) {
		long minutos       = tiempoTotal / 60;
		int minutosAlerta  = pocentajeAlerta == 0 ? 0 : Integer.valueOf( Double.valueOf(minutosMaximo * (pocentajeAlerta/100d)).intValue() );
		int tipo           = tabHost.getCurrentTab() + 1;
		
		Log.d(Constants.LOG_APP, "minutosMax["+minutosMaximo+"] pocentajeAlerta["+pocentajeAlerta+"] minutosAlerta["+minutosAlerta+"] minutos["+minutos+"]");
		if ( tipo == 2 ) {
			if ( minutosMaximo > 0 && minutos > minutosMaximo ) {
				lblDuracion.setTextAppearance(TotalLlamadasActivity.this, R.style.totalesRed);
			} else if ( minutosMaximo > 0 && minutosAlerta > 0 &&  minutos >= minutosAlerta  ) {
				lblDuracion.setTextAppearance(TotalLlamadasActivity.this, R.style.totalesYellow);
			} else {
				lblDuracion.setTextAppearance(TotalLlamadasActivity.this, R.style.totales);
			}
		} else {
			lblDuracion.setTextAppearance(TotalLlamadasActivity.this, R.style.totales);
		}
		
	}
}