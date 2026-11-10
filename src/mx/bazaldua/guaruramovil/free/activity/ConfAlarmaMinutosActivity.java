/**
 * 
 */
package mx.bazaldua.guaruramovil.free.activity;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.preference.Configuracion;
import mx.bazaldua.guaruramovil.free.util.Util;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author Iván Bazaldúa Ramírez
 *
 */
public class ConfAlarmaMinutosActivity extends Activity {
    private SharedPreferences pref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conf_alerta_minutos);
		SeekBar skbPorcentaje      = (SeekBar) findViewById(R.id.skbPorcentaje);
		TextView txtDefMinutosPlan = (TextView) findViewById(R.id.txtDefMinutosPlan);
		pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		
		//Se establecen los valores
		txtDefMinutosPlan.setText( pref.getString(Configuracion.MINUTOS_RENTA, "0") );
		skbPorcentaje.setProgress( pref.getInt(Configuracion.PORCENTAJE_ALERTA, 0) );
		setProcentaje( skbPorcentaje.getProgress() );
		
		skbPorcentaje.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setProcentaje( progress );
			}

		} );

	}
	
	@Override
	protected void onDestroy() {
        super.onDestroy();
        TextView txtDefMinutosPlan = (TextView) findViewById(R.id.txtDefMinutosPlan);
        SeekBar skbPorcentaje      = (SeekBar) findViewById(R.id.skbPorcentaje);
        SharedPreferences.Editor editor = pref.edit();
        
        if ( !Util.isNullOrEmpty( txtDefMinutosPlan.getText() ) ) {
        	editor.putString(Configuracion.MINUTOS_RENTA, txtDefMinutosPlan.getText().toString());
        }
        editor.putInt( Configuracion.PORCENTAJE_ALERTA, skbPorcentaje.getProgress() );
        editor.commit();
        
	}
	
	/**
	 * 
	 * @param progress
	 */
	private void setProcentaje(int progress) {
		TextView lblAlerta = (TextView) findViewById(R.id.lblAlerta);
		lblAlerta.setText( getString(R.string.porcentajeAlerta).concat( " ".concat(String.valueOf(progress)).concat("%") ) );
		lblAlerta = null;
		
	}
}
