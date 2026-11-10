package mx.bazaldua.guaruramovil.free.activity;

import java.util.List;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.adapter.RowNumFrecuenteAdapter;
import mx.bazaldua.guaruramovil.free.db.dao.ConfiguracionFrecuentesDAO;
import mx.bazaldua.guaruramovil.free.util.CallUtils;
import mx.bazaldua.guaruramovil.free.util.Constants;
import mx.bazaldua.guaruramovil.free.util.ContactUtils;
import mx.bazaldua.guaruramovil.free.util.Util;
import mx.bazaldua.guaruramovil.free.view.helper.FrecuenteHelper;
import mx.bazaldua.guaruramovil.free.vo.ContactVO;
import mx.bazaldua.guaruramovil.free.vo.FrecuenteVO;
import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CortarLlamadasActivity extends Activity {
	private FrecuenteVO frecuenteVO;
	private String telefonoSeleccionado;
	ListView lsvElementos;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cortar_llamadas);
        inicializa();
        consultarFrecuentes();
    }
    
    /**
     * Inicializa componentes necesarios 
     */
    private void inicializa() {
    	lsvElementos = (ListView) findViewById(R.id.lsvElementos);
    	
    	TextView textView1 = (TextView) findViewById(R.id.textView1);
    	Animation animation = AnimationUtils.loadAnimation(this, R.anim.tweenanimation);
    	textView1.startAnimation(animation);
    	
    	
//    	lsvElementos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//    	lsvElementos.setMultiChoiceModeListener( new ModeCallback() );
//    	lsvElementos.setOnItemSelectedListener( new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				Log.d(Constants.LOG_APP, "Position["+position+"] id["+id+"]");
//				view.setSelected(true);
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
////				view.setBackgroundColor(Color.BLACK);
//				
//			}
//		} );
    	
    	lsvElementos.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				TextView lblTelefono = (TextView) view.findViewById(R.id.lblTelefono);
				String[] elementos = {getString(R.string.operacion_llamar), getString(R.string.operacion_modificar), getString(R.string.operacion_eliminar)};
				setTelefonoSeleccionado( lblTelefono.getText().toString() );
				Util.vibrar(CortarLlamadasActivity.this, 10);
				Util.optionsDialog( CortarLlamadasActivity.this, R.string.dialog_title_operation, elementos, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
							case Constants.OPERACION_LLAMAR:
								try {
									CallUtils.call(CortarLlamadasActivity.this, getTelefonoSeleccionado());
								} catch (Exception e) {
									Log.e(Constants.LOG_APP, e.getMessage(), e);
								}
								break;
							case Constants.OPERACION_MODIFICAR:
								modificarFrecuente();
								break;
							
							case Constants.OPERACION_ELIMINAR:
								eliminarFrecuente();
								break;
						}
					}
				} );
				return false;
			}
		} );
		
	}

	/**
     * Consulta los numeros frecuentes que se han agregado a la aplicacion. 
     */
    private void consultarFrecuentes() {
    	ConfiguracionFrecuentesDAO dao = new ConfiguracionFrecuentesDAO(this);
    	List<FrecuenteVO> lstElementos = dao.getListFrecuentes();
//    	lsvElementos = (ListView) findViewById(R.id.lsvElementos);

    	if ( lstElementos != null ) {
	    	RowNumFrecuenteAdapter adapter = new RowNumFrecuenteAdapter(this, R.layout.elemento_frecuente, lstElementos);
	    	lsvElementos.setAdapter( adapter );
    	} else {
    		lsvElementos.setAdapter( null );
    	}
    	dao.closeConnection();
    	dao = null;
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.frecuentes, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch ( item.getItemId() ) {
			case R.id.menu_agregar_agenda:
				seleccionaNumeroFrecuente();
				break;
				
			case R.id.menu_agregar_sim:
				seleccionaNumeroSIM();
				break;
	
			case R.id.menu_eliminar_todos:
				eliminaTodos();
				break;
		}
    	
    	return super.onOptionsItemSelected(item);
    }

    /**
     * Despliega el intent para obtener los contactos guardados en SIM 
     */
    private void seleccionaNumeroSIM() {
		frecuenteVO = null;
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
				eliminarNumerosFrecuentes();
			}
		};
		Util.confirm(this, R.string.dialog_title_confirm, R.string.dialog_msg_eliminar_frecuentes, resultTrue, null);
	}

	/**
     * Selecciona el numero frecuente de la lista de contactos
     */
    private void seleccionaNumeroFrecuente() {
		frecuenteVO = null;
		telefonoSeleccionado = null;
		Intent intent = new Intent( Intent.ACTION_PICK, Contacts.CONTENT_URI );
		startActivityForResult(intent, Constants.ACTION_PICK_NUMBER);
	}

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ContactVO vo = null;
		if ( resultCode == Activity.RESULT_OK ) {
			switch ( requestCode ) {
				case Constants.ACTION_PICK_NUMBER:
					vo = ContactUtils.getContactFromPick( data, this );
					frecuenteVO = FrecuenteHelper.getFrecuenteVOFromContactVO(vo);
					if ( frecuenteVO != null ) createDialogFrecuentes();
					break;
	
				case Constants.ACTION_PICK_NUMBER_SIM:
					vo = ContactUtils.getContactFromPickSIM( data );
					frecuenteVO = FrecuenteHelper.getFrecuenteVOFromContactVO(vo);
					if ( frecuenteVO != null ) createDialogFrecuentes();
					break;
			}
		}
    }
    
    /**
     * Muestra ventana para confirmacion del contacto frecuente.
     */
    public void createDialogFrecuentes() {
		final View view       = LayoutInflater.from(this).inflate(R.layout.agregar_frecuente, null);
		final View viewHeader = LayoutInflater.from(this).inflate(R.layout.header_dialog, null);
		FrecuenteHelper.setDatosView ( frecuenteVO, view, this );
		
		Util.setDataViewHeader(viewHeader, R.string.agregar_frecuente, R.drawable.ic_menu_agregar_contacto, this);
		
		new AlertDialog.Builder(CortarLlamadasActivity.this)
		.setCustomTitle(viewHeader)
        .setView(view)
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	//VERSION FREE
            	TextView txtMinutos = (TextView) view.findViewById(R.id.txtMinutos);
            	TextView txtSegundos = (TextView) view.findViewById(R.id.txtSegundos);
            	if ( !Util.isNullOrEmpty(txtMinutos.getText() ) && Integer.valueOf( txtMinutos.getText().toString() ) > 3 ) {
            		Util.confirm(CortarLlamadasActivity.this, R.string.app_name, R.string.notif_msg_no_disp_mayor3_free, null, null);
            	} else if ( !Util.isNullOrEmpty(txtSegundos.getText() ) && Integer.valueOf( txtSegundos.getText().toString() ) > 0 ) {
            		Util.confirm(CortarLlamadasActivity.this, R.string.app_name, R.string.notif_msg_no_disp_segundos_free, null, null);
            	} else {
            		agregarFrecuente( view );
            	}
            }
        })
        .setNegativeButton(R.string.alert_dialog_cancel, null )
        .create().show();
    }
    
    /**
     * Agrega el contacto seleccionado como frecuente
     * @param view	Objeto contenedor de la vista
     */
	private void agregarFrecuente(View view) {
    	ConfiguracionFrecuentesDAO dao = new ConfiguracionFrecuentesDAO(getApplicationContext());
    	FrecuenteVO vo                 = FrecuenteHelper.getFrecuenteVO( view );
    	if ( dao.exist( vo.getTelefono() ) ) dao.update( vo );
    	else dao.insert( vo );	
    	
    	dao.closeConnection();
    	consultarFrecuentes();
	}
	
	/**
	 * Elimina los numeros frecuentes que se encuentren en la aplicacion
	 */
	private void eliminarNumerosFrecuentes() {
		ConfiguracionFrecuentesDAO dao = new ConfiguracionFrecuentesDAO(this);
		Util.mensajeToast(this, getString(R.string.eliminando_todos));
		dao.deleteAll();
		dao.closeConnection();
		consultarFrecuentes();
	}
	
    /**
     * Modifica lo datos del numero frecuente seleccionado
     */
    private void modificarFrecuente() {
    	ConfiguracionFrecuentesDAO dao = new ConfiguracionFrecuentesDAO(this);
    	ContactVO contactVO            = null;
    	
    	frecuenteVO = dao.queryById( getTelefonoSeleccionado() );
    	if ( frecuenteVO != null ) {
			//Se obtienen los demás datos del contacto
			contactVO = ContactUtils.getContactByPhone(frecuenteVO.getTelefono(), getContentResolver());
			
			frecuenteVO.setId( contactVO.getId() );
			frecuenteVO.setNombre( contactVO.getNombre() );
			frecuenteVO.setTipoTelefono( contactVO.getTipoTelefono() );
			frecuenteVO.setFoto( contactVO.getFoto() );
			
			createDialogFrecuentes();
    	}
    	dao.closeConnection();
    }
    
    /**
     * Elimina el numero frecuente seleccionado
     */
    private void eliminarFrecuente() {    	
    	ConfiguracionFrecuentesDAO dao = new ConfiguracionFrecuentesDAO(this);
    	Util.mensajeToast(this, getString(R.string.eliminando_elemento, getTelefonoSeleccionado()));
    	dao.delete( getTelefonoSeleccionado() );
    	dao.closeConnection();
    	consultarFrecuentes();
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
	public FrecuenteVO getFrecuenteVO() {
		return frecuenteVO;
	}

	/**
	 * @param frecuenteVO the frecuenteVO to set
	 */
	public void setFrecuenteVO(FrecuenteVO frecuenteVO) {
		this.frecuenteVO = frecuenteVO;
	}
	
//    private class ModeCallback implements ListView.MultiChoiceModeListener {
//
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.frecuentes_seleccion, menu);
//            mode.setTitle("Selecciona Elementos");
//            setSubtitle(mode);
//            return true;
//        }
//
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return true;
//        }
//
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//            case R.id.menu_eliminar:
//                Toast.makeText( CortarLlamadasActivity.this, "Eliminar " + lsvElementos.getCheckedItemCount() +
//                        " elementos", Toast.LENGTH_SHORT).show();
//                mode.finish();
//                //getListView().getCheckedItemCount()
//                break;
//            default:
//                Toast.makeText(CortarLlamadasActivity.this, "Click " + item.getTitle(),
//                        Toast.LENGTH_SHORT).show();
//                break;
//            }
//            return true;
//        }
//
//        public void onDestroyActionMode(ActionMode mode) {
//        }
//
//        public void onItemCheckedStateChanged(ActionMode mode,
//                int position, long id, boolean checked) {
//            setSubtitle(mode);
//        }
//
//        private void setSubtitle(ActionMode mode) {
//            final int checkedCount = lsvElementos.getCheckedItemCount();
//            switch (checkedCount) {
//                case 0:
//                    mode.setSubtitle(null);
//                    break;
//                case 1:
//                    mode.setSubtitle("1 seleccionado");
//                    break;
//                default:
//                	mode.setSubtitle("" + checkedCount + " seleccionados");
//                    break;
//            }
//        }
//    }
	
}