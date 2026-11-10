package mx.bazaldua.guaruramovil.free.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.db.dao.ListaNegraDAO;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.ContactUtils;
import mx.bazaldua.guaruramovil.free.util.Util;
import mx.bazaldua.guaruramovil.free.view.helper.ListaNegraHelper;
import mx.bazaldua.guaruramovil.free.vo.ContactVO;
import mx.bazaldua.guaruramovil.free.vo.ListaNegraVO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class ListaNegraActivity extends Activity {
	private ListaNegraVO listaNegraVO;
	private String telefonoSeleccionado;
	private Long idSeleccionado;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_negra);
        inicializa();
        consultarListaNegra();
    }
    
    /**
     * Inicializa componentes necesarios 
     */
    private void inicializa() {
    	final ExpandableListView elvElementos = (ExpandableListView) findViewById(R.id.elvElementos);
    	TextView textView1 = (TextView) findViewById(R.id.textView1);
    	Animation animation = AnimationUtils.loadAnimation(this, R.anim.tweenanimation);
    	textView1.startAnimation(animation);
    	
    	elvElementos.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				String[] elementos = {getString(R.string.operacion_eliminar)};
				TextView lblObjeto = null;
				
				setIdSeleccionado( null );
				setTelefonoSeleccionado( null );
				
				if ( ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP ) {
					lblObjeto = (TextView) view.findViewById(R.id.lblId);
					setIdSeleccionado( Long.valueOf( lblObjeto.getText().toString() ) );
					lblObjeto = (TextView) view.findViewById(R.id.lblNombre);
					setTelefonoSeleccionado( ( lblObjeto.getText().toString() ) );
				} else {
					lblObjeto =(TextView) view.findViewById(android.R.id.text1);
					setTelefonoSeleccionado( lblObjeto.getText().toString() );
				}
				Util.vibrar(ListaNegraActivity.this, 10);
				Util.optionsDialog( ListaNegraActivity.this, R.string.dialog_title_operation, elementos, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int position) {							
						eliminarNumero();
					}
				} );
				return true;
			}
		} );
	}

	/**
     * Consulta los numeros frecuentes que se han agregado a la aplicacion. 
     */
    @SuppressWarnings("unchecked")
    private void consultarListaNegra() {
    	ListaNegraDAO dao = new ListaNegraDAO(this);
    	List<ListaNegraVO> lstElementos = dao.getListListasNegras();
    	ExpandableListView elvElementos = (ExpandableListView) findViewById(R.id.elvElementos);

    	if ( lstElementos != null ) {
    		Map<Integer, ?> mapElementos = ListaNegraHelper.getMapConsulta(lstElementos, this);
    		
			ExpandableListAdapter mAdapter = new SimpleExpandableListAdapter(
                    this,
                    (List<Map<String, String>>) mapElementos.get(0),
                    R.layout.elemento_expandable_group,
                    new String[] { Constants.EXTRA_PARAM_NOMBRE, Constants.EXTRA_PARAM_ID },
                    new int[] { R.id.lblNombre, R.id.lblId },
                    (List<List<Map<String, String>>>) mapElementos.get(1),
                    android.R.layout.simple_expandable_list_item_2,
                    new String[] { Constants.EXTRA_PARAM_TELEFONO, Constants.EXTRA_PARAM_TIPO_TELEFONO },
                    new int[] { android.R.id.text1, android.R.id.text2 }
                    );
            elvElementos.setAdapter( mAdapter );
    	} else {
			ExpandableListAdapter mAdapter = new SimpleExpandableListAdapter(
                    this,
                    new ArrayList<Map<String, String>>(),
                    R.layout.elemento_expandable_group,
                    new String[] { Constants.EXTRA_PARAM_NOMBRE, Constants.EXTRA_PARAM_ID },
                    new int[] { android.R.id.text1 },
                    new ArrayList<List<Map<String, String>>>(),
                    android.R.layout.simple_expandable_list_item_2,
                    new String[] { Constants.EXTRA_PARAM_TELEFONO, Constants.EXTRA_PARAM_TIPO_TELEFONO },
                    new int[] { android.R.id.text1, android.R.id.text2 }
                    );
    		elvElementos.setAdapter(mAdapter);

    	}
    	dao.closeConnection();
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.lista_negra, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch ( item.getItemId() ) {
			case R.id.menu_agregar_agenda:
				seleccionaNumeroAgenda();
				break;
				
			case R.id.menu_agregar_sim:
				seleccionaNumeroSIM();
				break;
				
			case R.id.menu_agregar_historial:
				seleccionaNumeroHistorial();
				break;
	
			case R.id.menu_eliminar_todos:
				eliminaTodos();
				break;
				
			case R.id.menu_agregar_numero:
				seleccionaNumero();
				break;
		}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    /**
     * Selecciona un número del historial de llamadas
     */
    private void seleccionaNumeroHistorial() {
		listaNegraVO = null;
		telefonoSeleccionado = null;
		Intent intent = new Intent( this, TotalLlamadasActivity.class );
		intent.putExtra(Constants.EXTRA_PARAM_PICK, true);
		startActivityForResult(intent, Constants.ACTION_PICK_NUMBER_HISTORY);
	}

	/**
     * Agrega un numero mediante captura
     */
    private void seleccionaNumero() {
		final View view       = LayoutInflater.from(this).inflate(R.layout.agregar_numero, null);
		final View viewHeader = LayoutInflater.from(this).inflate(R.layout.header_dialog, null);
		Util.setDataViewHeader(viewHeader, R.string.telefono, R.drawable.ic_menu_agregar_numero, this);
		new AlertDialog.Builder(this)
		.setCustomTitle(viewHeader)
        .setView(view)
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	agregarNumero( ListaNegraHelper.getListaNegraVOFromNumber( view, getContentResolver() ) );
            }
        })
        .setNegativeButton(R.string.alert_dialog_cancel, null )
        .create().show();
	}

	/**
     * Despliega el intent para obtener los contactos guardados en SIM 
     */
    private void seleccionaNumeroSIM() {
		listaNegraVO = null;
		telefonoSeleccionado = null;
		Intent intent = new Intent( this, ContactSIMActivity.class );
		startActivityForResult(intent, Constants.ACTION_PICK_NUMBER_SIM);
	}

    /**
     * Elimina todos los numeros frecuentes almacenados en el sistema
     */
    private void eliminaTodos() {
    	OnClickListener resultTrue = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				eliminarNumeros();
			}
		};
		Util.confirm(this, R.string.dialog_title_confirm, R.string.dialog_msg_eliminar_todos_lista_negra, resultTrue, null);
	}

	/**
     * Selecciona el numero frecuente de la lista de contactos
     */
    private void seleccionaNumeroAgenda() {
		listaNegraVO = null;
		telefonoSeleccionado = null;
		Intent intent = new Intent( Intent.ACTION_PICK, Contacts.CONTENT_URI );
		startActivityForResult(intent, Constants.ACTION_PICK_NUMBER);
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ( resultCode == Activity.RESULT_OK ) {
			switch ( requestCode ) {
				case Constants.ACTION_PICK_NUMBER:
					ContactVO vo = ContactUtils.getContactFromPick( data, this );
					listaNegraVO = ListaNegraHelper.getListaNegraVOFromContactVO(vo, Constants.CONTACTO_AGENDA);
					
					if ( listaNegraVO != null ) {
						listaNegraVO.setTipoTelefono(null);
						agregarNumero( listaNegraVO );				
					}
					break;
					
				case Constants.ACTION_PICK_NUMBER_HISTORY:
					listaNegraVO = ListaNegraHelper.getListaNegraVOFromNumber( data, getContentResolver() );
					if ( listaNegraVO != null ) {
						agregarNumero(listaNegraVO);
					}
					break;
	
				case Constants.ACTION_PICK_NUMBER_SIM:
					vo = ContactUtils.getContactFromPickSIM( data );
					listaNegraVO = ListaNegraHelper.getListaNegraVOFromContactVO(vo, Constants.CONTACTO_SIM);
					
					if ( listaNegraVO != null ) {
						listaNegraVO.setTipoTelefono(null);
						agregarNumero( listaNegraVO );
					}
					break;
			}
		}
    }
    
    /**
     * Agrega el contacto seleccionado como frecuente
     * @param vo
     * @param view	Objeto contenedor de la vista
     */
	public void agregarNumero(ListaNegraVO vo) {
    	ListaNegraDAO dao = new ListaNegraDAO(getApplicationContext());
    	
    	//VERSION FREE
    	if ( dao.exist( vo.getTelefono() ) ) dao.update( vo );
    	else if ( dao.count() < 5 ) Log.d(Constants.LOG_APP, "Resultado["+dao.insert( vo )+"]");	
    	else Util.confirm(this, R.string.app_name, R.string.notif_msg_no_disp_maximo_5_registros, null, null);	
    	
    	dao.closeConnection();
    	consultarListaNegra();
	}
	
	/**
	 * Elimina los numeros frecuentes que se encuentren en la aplicacion
	 */
	private void eliminarNumeros() {
		ListaNegraDAO dao = new ListaNegraDAO(this);
		Util.mensajeToast(this, getString(R.string.eliminando_todos));
		dao.deleteAll();
		dao.closeConnection();
		consultarListaNegra();
	}
    
    /**
     * Elimina el numero frecuente seleccionado
     */
    private void eliminarNumero() {    	
    	ListaNegraDAO dao = new ListaNegraDAO(this);
    	
    	if ( getIdSeleccionado() != null ) {
    		Util.mensajeToast(this, getString(R.string.eliminando_elemento, getTelefonoSeleccionado()));
    		Log.d(Constants.LOG_APP, "Resultado ID["+dao.deleteId(  getIdSeleccionado() )+"]");
    	} else {
    		Util.mensajeToast(this, getString(R.string.eliminando_elemento, getTelefonoSeleccionado()));
    		Log.d(Constants.LOG_APP, "Resultado TEL["+dao.delete(  getTelefonoSeleccionado() )+"]");	
    	}
    	
    	dao.closeConnection();
    	consultarListaNegra();
    }

	/**
	 * @return the telefonoSeleccionado
	 */
	public String getTelefonoSeleccionado() {
		return telefonoSeleccionado;
	}

	/**
	 * @param telefonoSeleccionado the telefonoSeleccionado to set
	 */
	public void setTelefonoSeleccionado(String telefonoSeleccionado) {
		this.telefonoSeleccionado = telefonoSeleccionado;
	}

	/**
	 * @return the frecuenteVO
	 */
	public ListaNegraVO getListaNegraVO() {
		return listaNegraVO;
	}

	/**
	 * @param listaNegraVO the ListaNegraVO to set
	 */
	public void setFrecuenteVO(ListaNegraVO listaNegraVO) {
		this.listaNegraVO = listaNegraVO;
	}

	/**
	 * @return the idSeleccionado
	 */
	public Long getIdSeleccionado() {
		return idSeleccionado;
	}

	/**
	 * @param idSeleccionado the idSeleccionado to set
	 */
	public void setIdSeleccionado(Long idSeleccionado) {
		this.idSeleccionado = idSeleccionado;
	}
}