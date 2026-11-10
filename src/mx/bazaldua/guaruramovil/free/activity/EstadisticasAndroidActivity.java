package mx.bazaldua.guaruramovil.free.activity;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.preference.Configuracion;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.Util;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class EstadisticasAndroidActivity extends Activity {
	

	private Handler handler;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
	        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	        setContentView(R.layout.estadisticas);
	        asignaEventos();
        } catch (Exception e) {
        	Log.e(Constants.LOG_APP, e.getMessage(), e);
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage( e.getMessage() + "\n" + e.getLocalizedMessage() );
        	builder.show();
		}
    }

    /**
     * 
     */
	private void asignaEventos() throws Exception {
		final ImageButton cmdCortarLlamadas = (ImageButton) findViewById(R.id.cmdCortarLlamadas); 
		final ImageButton cmdTotalLlamadas  = (ImageButton) findViewById(R.id.cmdTotalLlamadas);
		final ImageButton cmdAcercade       = (ImageButton) findViewById(R.id.cmdAcercaDe);
		final ImageButton cmdConfiguracion  = (ImageButton) findViewById(R.id.cmdConfiguracion);
		final ImageButton cmdListaNegra     = (ImageButton) findViewById(R.id.cmdListaNegra);
		Display display                     = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				cmdCortarLlamadas.setVisibility(View.VISIBLE); 
				cmdTotalLlamadas.setVisibility(View.VISIBLE);
				cmdAcercade.setVisibility(View.VISIBLE);
				cmdConfiguracion.setVisibility(View.VISIBLE);
				cmdListaNegra.setVisibility(View.VISIBLE);
			}
		};
		
		cmdCortarLlamadas.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.callActivity( EstadisticasAndroidActivity.this, CortarLlamadasActivity.class );
			}
		} );
		
		cmdTotalLlamadas.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.callActivity( EstadisticasAndroidActivity.this, TotalLlamadasActivity.class );
			}
		} );
		
		cmdAcercade.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final View view = LayoutInflater.from(EstadisticasAndroidActivity.this).inflate(R.layout.body_acerca_de, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(EstadisticasAndroidActivity.this);
				View viewDialog             = LayoutInflater.from(EstadisticasAndroidActivity.this).inflate(R.layout.header_dialog, null); 
				Util.setDataViewHeader(viewDialog, R.string.acerca_de, R.drawable.acerca, EstadisticasAndroidActivity.this);
				
				builder.setCustomTitle( viewDialog );
				builder.setView(view);
				builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
				builder.show();
			}
		} );
		
		cmdConfiguracion.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.callActivity(EstadisticasAndroidActivity.this, Configuracion.class);
			}
		} );
		
		cmdListaNegra.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Util.callActivity(EstadisticasAndroidActivity.this, ListaNegraActivity.class);
			}
		} );
		
		handler.sendEmptyMessageDelayed(0, 1000);
		
		//Se verifica tamaño de la pantalla para las pantallas demasiadas chicas se cambia resolucion de los menus.
		if ( display.getHeight() < 400 ) {
			TableRow row3              = (TableRow) findViewById(R.id.tableRow3);
			TableLayout.LayoutParams lp= new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			lp.setMargins(0, -25, 0, 0);
			
			row3.setLayoutParams(lp);
		}
	}
	
	@Override
	protected void onStart() {
		setProgressBarIndeterminateVisibility( false );
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		setProgressBarIndeterminateVisibility(false);
		super.onResume();
	}
}