package mx.bazaldua.guaruramovil.free.activity;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.provider.ContactSIM;
import mx.bazaldua.guaruramovil.free.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ContactSIMActivity extends Activity {
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactos_sim);
        inicializaComponentes();
    }

    /**
     * Inicializa los componentes de la Actividad
     */
	private void inicializaComponentes() {
		ListView lsvElementos        = (ListView) findViewById( R.id.lsvElementos );
		String[] projection          = { ContactSIM._ID, ContactSIM.NAME, ContactSIM.NUMBER };
		int[] controls               = { R.id.lblId, R.id.lblNombre, R.id.lblNumero};
		Cursor cursor                = null;
		SimpleCursorAdapter adapter  = null;
		
		lsvElementos.setOnItemClickListener(  new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapView, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra(Constants.EXTRA_PARAM_ID, Long.valueOf( ((TextView) view.findViewById(R.id.lblId)).getText().toString() ) );
				intent.putExtra(Constants.EXTRA_PARAM_TELEFONO, ((TextView) view.findViewById(R.id.lblNumero)).getText() );
				intent.putExtra(Constants.EXTRA_PARAM_NOMBRE,  ((TextView) view.findViewById(R.id.lblNombre)).getText() );
				
				ContactSIMActivity.this.setResult(Activity.RESULT_OK, intent);
				ContactSIMActivity.this.finish();
			}
		});
		
		cursor  = getContentResolver().query(ContactSIM.CONTENT_URI, projection, null, null, null);
		
		adapter = new SimpleCursorAdapter(this,R.layout.elemento_contacto_sim,cursor, projection, controls);
		if ( cursor == null ) {
			cursor  = getContentResolver().query(ContactSIM.CONTENT_URI_15, projection, null, null, null);
			adapter = new SimpleCursorAdapter(this,R.layout.elemento_contacto_sim,cursor, projection, controls);
		}
		lsvElementos.setAdapter( adapter);
	}
}