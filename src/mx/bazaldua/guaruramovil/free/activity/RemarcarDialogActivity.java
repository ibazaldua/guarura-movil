package mx.bazaldua.guaruramovil.free.activity;

import java.util.Timer;
import java.util.TimerTask;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.preference.Configuracion;
import mx.bazaldua.guaruramovil.free.util.AudioUtil;
import mx.bazaldua.guaruramovil.free.util.CallUtils;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.Util;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class RemarcarDialogActivity extends Activity {

	private ProgressDialog progress;
	private Handler progressHandler;
	private String telefono;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Bundle bundle   = getIntent().getBundleExtra(Constants.EXTRA_BUNDLE_LLAMADA);
        setContentView(R.layout.translucent_background);
        telefono = bundle.getString(Constants.EXTRA_PARAM_TELEFONO);
        
		progressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (progress.getProgress() >= progress.getMax()) {
                	finalizarRemarcar();
                } else {
                    progress.incrementProgressBy(1);
                    progressHandler.sendEmptyMessageDelayed(0, 1000);
                }
            }
        };
		
		showDialog(Constants.DIALOG_REMARCAR);
		if ( progress.getMax() > 0 && progress.getMax() < 100 ) {
			progress.setProgress( 0 );
			progressHandler.sendEmptyMessage( 0 );			
		} else {
			finalizarRemarcar();
		}
	}
	
	/**
	 * Se finaliza el dialogo remarcando el numero indicado 
	 */
	private void finalizarRemarcar() {
        progress.dismiss();
        progressHandler.removeMessages(0);
        progress = null;
        progressHandler = null;
        try {
        	final AudioUtil audioUtil = new AudioUtil(this, R.raw.beep30, 0.1f, 0.1f);
        	audioUtil.start();
			CallUtils.call(this, telefono);
			final Timer timer = new Timer ();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					audioUtil.stop();
					timer.cancel();
				}
			}, 1500);

		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		}
        Util.mensajeToast(this, getString(R.string.remarcando_numero, telefono ) );
		finish();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if ( id == Constants.DIALOG_REMARCAR ) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			progress = new ProgressDialog(this);
			progress.setIcon(android.R.drawable.ic_menu_call);
			progress.setTitle( getString(R.string.remarcar_numero, telefono) );
			progress.setMax( Integer.valueOf( pref.getString(Configuracion.TIEMPO_REMARCACION, Constants.CONF_DEFAULT_TIEMPO_REMARCA) ) );
			progress.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
			progress.setButton( Dialog.BUTTON_POSITIVE, getString(R.string.remarcar) , new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					try {
						finalizarRemarcar();
					} catch (Exception e) {
						Log.e(Constants.LOG_APP, e.getMessage(), e);
					}
				}
			});
			
			progress.setButton( Dialog.BUTTON_NEGATIVE, getString(R.string.dialog_button_cancel) , new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					cancelar();
				}
			});
			
			return progress;
		} else {
			return super.onCreateDialog(id);	
		}
		
	}
	
	/**
	 * Cancelar el remarcado.
	 */
	private void cancelar() {
		progress.dismiss();
		progressHandler.removeMessages(0);
		progress = null;
		progressHandler = null;
		finish();
	}
	
}
