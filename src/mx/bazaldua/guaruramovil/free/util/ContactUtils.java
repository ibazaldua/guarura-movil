package mx.bazaldua.guaruramovil.free.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mx.bazaldua.guaruramovil.free.R;
import mx.bazaldua.guaruramovil.free.activity.CortarLlamadasActivity;
import mx.bazaldua.guaruramovil.free.activity.ListaNegraActivity;
import mx.bazaldua.guaruramovil.free.adapter.RowTelefonoAdapter;
import mx.bazaldua.guaruramovil.free.provider.ContactSIM;
import mx.bazaldua.guaruramovil.free.view.helper.FrecuenteHelper;
import mx.bazaldua.guaruramovil.free.view.helper.ListaNegraHelper;
import mx.bazaldua.guaruramovil.free.vo.ContactVO;
import mx.bazaldua.guaruramovil.free.vo.ListaNegraVO;
import mx.bazaldua.guaruramovil.free.vo.PhoneVO;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;

/**
 * 
 * @author Iván Bazaldúa Ramírez
 *
 */
public class ContactUtils {

	/**
	 * Devuelve el contacto de acuerdo al numero telefonico enviado.
	 * @param telefono	Numero de telefono a buscar
	 * @return
	 */
	public static ContactVO getContactByPhone( String telefono, ContentResolver contentResolver ) {
		//Se obtiene el ID del contacto --------------------------------------------
		ContactVO contactVO = null;
		Long id             = -1L;
		Uri contactosUri    = Uri.withAppendedPath( Phone.CONTENT_FILTER_URI, Uri.encode( telefono ) );
		Cursor cursor       = contentResolver.query(contactosUri, new String[]{Phone.CONTACT_ID, Phone.DISPLAY_NAME, Phone.TYPE, Phone.LABEL, Phone.LOOKUP_KEY}, null, null, null);
		if ( cursor.moveToNext() ) {
			id = cursor.getLong( cursor.getColumnIndex( Phone.CONTACT_ID ) );
							
			if ( id > -1 ) {
				
				contactVO          = new ContactVO();			
				//Se busca la imagen del contacto
				contactosUri       = ContentUris.withAppendedId(Contacts.CONTENT_URI,  id );
				InputStream stream = Contacts.openContactPhotoInputStream(contentResolver, contactosUri);
		        
		        contactVO.setId( id );
		        contactVO.setLookupKey(cursor.getString( cursor.getColumnIndex( Phone.LOOKUP_KEY ) ) );
		        contactVO.setNombre( cursor.getString( cursor.getColumnIndex( Phone.DISPLAY_NAME ) ) );
	        	contactVO.setFoto( stream != null ? BitmapFactory.decodeStream( stream, null, null ) : null );
	        	contactVO.setTipoTelefono( cursor.getInt( cursor.getColumnIndex( Phone.TYPE ) ) );
	        	contactVO.setTelefono( telefono );
	        	contactVO.setDescTipoTelPersonalizado( cursor.getString( cursor.getColumnIndex( Phone.LABEL ) ) );
			}
		}

		cursor.close();
		cursor = null;
		return contactVO;
	}
	
	
	/**
	 * Devuelve la foto del contacto mediante el ID
	 * @param id	Identidicador del contacto
	 * @param cr	{@link ContentResolver}
	 * @return		{@link Bitmap} de la foto
	 */
	public static Bitmap getPhotoById(long id, ContentResolver cr) {
		Bitmap result = null;
		
		if ( id != -1 ) {
			Uri contactosUri   = ContentUris.withAppendedId(Contacts.CONTENT_URI,  id );
			InputStream stream = Contacts.openContactPhotoInputStream(cr, contactosUri);
			result = stream != null ? BitmapFactory.decodeStream( stream, null, null ) : null;
		}
		
		return result;
	}
	
	
	/**
	 * Devuelve el id del contacto
	 * @param telefono	Numero de telefono a buscar
	 * @return
	 */
	public static long getContactIdByPhone( String telefono, ContentResolver contentResolver ) {
		//Se obtiene el ID del contacto --------------------------------------------
		Long id             = -1L;
		Uri contactosUri    = Uri.withAppendedPath( Phone.CONTENT_FILTER_URI, Uri.encode( telefono ) );
		Cursor cursor       = contentResolver.query(contactosUri, new String[]{Phone.CONTACT_ID}, null, null, null);
		if ( cursor.moveToNext() )  id = cursor.getLong( cursor.getColumnIndex( Phone.CONTACT_ID ) );
		cursor.close();
		return id;
	}
	
	/**
	 * Devuelve el contacto de acuerdo al numero telefonico enviado.
	 * @param telefono	Numero de telefono a buscar
	 * @return
	 */
	public static ContactVO getContactByPhoneSIM( String telefono, ContentResolver content ) {
		//Se obtiene el ID del contacto --------------------------------------------
		ContactVO contactVO = null;
		String[] projection = {ContactSIM.NUMBER, ContactSIM.NAME};
		String select       = new StringBuffer( ContactSIM.NUMBER ).append( " = ? " ).toString();
		String[] selectArgs = {telefono}; 
		Long id             = -1L;
		Cursor cursor       = content.query(ContactSIM.CONTENT_URI, projection, select, selectArgs, "CAST( ".concat( ContactSIM.NUMBER).concat( ", AS INTEGER )" )); 
		
//		int inf = 1;
//		int sup = cursor.getCount();
//		int centro = 0;
//		while ( inf <= sup ) {
//		  centro = ((sup - inf) / 2) + inf // División entera: se trunca la fracción
//		  if ( cursor.move(centro) &&  cursor.getString( cursor.getColumnIndex( ContactSIM.NUMBER ) ).trim().equals( telefono ) ) {
//				contactVO = new ContactVO();			
//				
//				contactVO.setId( id );
//				contactVO.setNombre( cursor.getString( cursor.getColumnIndex( ContactSIM.NAME ) ) );
//				contactVO.setTipoTelefono( Phone.TYPE_OTHER  );
//				contactVO.setTelefono( telefono );
//				break;
//			} else { Si dato < vec[centro] entonces:
//			    sup = centro - 1
//			} else {
//			   En caso contrario:
//			    inf = centro + 1
//			}
//		}
		while ( cursor.moveToNext() ) {
			if ( cursor.getString( cursor.getColumnIndex( ContactSIM.NUMBER ) ).trim().equals( telefono ) ) {
				contactVO = new ContactVO();			
				
				contactVO.setId( id );
				contactVO.setNombre( cursor.getString( cursor.getColumnIndex( ContactSIM.NAME ) ) );
				contactVO.setTipoTelefono( Phone.TYPE_OTHER  );
				contactVO.setTelefono( telefono );
				break;
			}
		}

		cursor.close();
		cursor = null;
		return contactVO;
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static ContactVO getContactFromPick ( Intent data, Activity activity ) {
		ContactVO vo = null;
		
		final Cursor cur = activity.getContentResolver().query(data.getData(), null, null, null, null);

		if ( cur.moveToNext() ) {
			 List<PhoneVO> list =  ContactUtils.getPhonesByLookupKey(activity, cur.getString( cur.getColumnIndex(Data.LOOKUP_KEY) ));
			 
			 final long id       = cur.getLong( cur.getColumnIndex(Phone._ID) );
			 final String nombre = cur.getString( cur.getColumnIndex(Phone.DISPLAY_NAME) );
			 
			 if ( list == null || list.size() == 0 ) {
				 Util.mensajeToast(activity, activity.getString( R.string.toast_number_not_found ) );
			 } else if ( list.size() == 1 ) {
				 vo = getVOFromData( id, nombre, list.get(0), activity );
			 } else if ( list.size() > 1 ) {
				 final RowTelefonoAdapter adapter = new RowTelefonoAdapter(activity, R.layout.elemento_telefono, list);
				 AlertDialog.Builder builder           = new AlertDialog.Builder(activity);
				 final Context context                 = activity;
				 
				 builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int position) {
						ContactVO vo = getVOFromData( id, nombre, adapter.getItem(position), context );
						if ( context instanceof CortarLlamadasActivity ) {
							CortarLlamadasActivity cortarLlamadas = (CortarLlamadasActivity) context;
							cortarLlamadas.setFrecuenteVO( FrecuenteHelper.getFrecuenteVOFromContactVO(vo) );
							cortarLlamadas.createDialogFrecuentes();
						
						} else if ( context instanceof ListaNegraActivity ) {
							ListaNegraActivity listaNegra = (ListaNegraActivity) context;
							ListaNegraVO listaNegraVO = ListaNegraHelper.getListaNegraVOFromContactVO(vo, Constants.CONTACTO_AGENDA);
							listaNegraVO.setTipoTelefono(null);
							listaNegra.agregarNumero( listaNegraVO );
						}
						dialog.dismiss();
					}
				});
				 builder.setTitle(R.string.seleccionar_telefono);
				 builder.show();
			 }
		}
		cur.close();
		
		return vo;
	}
	
	/**
	 * Devuelve un objeto <code>ContactVO</code> derivado de la consulta de contacto
	 * @param cur		Cursor que contiene los datos del contacto
	 * @param phoneVO	Objeto que contiene los datos del telefono
	 * @param context	Contexto de la aplicacion
	 * @return			<code>ContactVO</code> con los datos del contacto
	 */
	private static ContactVO getVOFromData(Long id, String nombre, PhoneVO phoneVO, Context context) {
	 	ContactVO vo = new ContactVO();
		vo.setId( id );
		vo.setNombre( nombre );
		vo.setTelefono( phoneVO.getNumber() );
		vo.setTipoTelefono( phoneVO.getType() );
		vo.setDescTipoTelPersonalizado( phoneVO.getDescType() );
		Uri contactosUri       = ContentUris.withAppendedId(Contacts.CONTENT_URI,  vo.getId() );
		InputStream stream = Contacts.openContactPhotoInputStream(context.getContentResolver(), contactosUri);
		vo.setFoto( stream != null ? BitmapFactory.decodeStream( stream, null, null ) : null );
		return vo;
	}

	/**
	 * Devuelve una lista de los telefonos asociados a un contacto
	 * @param context	Contexto de la aplicacion
	 * @param lookupKey	LookupKey para localizar el contacto
	 * @return			<code>List</code> con los telefonos del contacto indicado
	 */
	private static List<PhoneVO> getPhonesByLookupKey(Context context, String lookupKey) {
		List<PhoneVO> lstPhoneVO = null;
		PhoneVO phoneVO          = null;
		StringBuilder where      = new StringBuilder();
		String[] select          = {Phone.TYPE, Phone.NUMBER, Phone.LABEL };
		String[] valuesWhere     = {lookupKey, Phone.CONTENT_ITEM_TYPE};
		where.append(Data.LOOKUP_KEY).append( " = ? " ).append(" AND ").append( Data.MIMETYPE ).append(" = ? ");
		Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, select, where.toString(), valuesWhere, null);
		
		if ( cursor.moveToNext() ) {
			lstPhoneVO = new ArrayList<PhoneVO>();
			
			do {
				phoneVO = new PhoneVO();
				phoneVO.setType( cursor.getInt( cursor.getColumnIndex(Phone.TYPE) ) );
				phoneVO.setNumber( CallUtils.getPhoneNumber( cursor.getString( cursor.getColumnIndex(Phone.NUMBER)) ).toString() );
				if ( phoneVO.getType() == Phone.TYPE_CUSTOM ) phoneVO.setDescType( cursor.getString( cursor.getColumnIndex(Phone.LABEL)) ); 

				lstPhoneVO.add( phoneVO );
			} while ( cursor.moveToNext() );
		}
		cursor.close();
		cursor = null;
		return lstPhoneVO;
	}


	/**
	 * Devuelve el tipo de telefono
	 * @param type			Identificador del tipo de telefono
	 * @param personalizado	En caso de que el tipo sea personalizado, aqui vendra el nombre que se le agrego al tipo
	 * @param context		Contexto de la aplicacion
	 * @return				Tipo de telefono configurado
	 */
	public static String getPhoneType ( int type, String personalizado, Context context ) {
		String result = "";
		switch (type) {
			case Phone.TYPE_HOME:
				result = context.getString( R.string.type_home );
				break;
			case Phone.TYPE_MOBILE:
				result = context.getString( R.string.type_mobile );
				break;
			case Phone.TYPE_WORK:
				result = context.getString( R.string.type_work );
				break;
			case Phone.TYPE_WORK_MOBILE:
				result = context.getString( R.string.type_work_mobile );
				break;
			case Phone.TYPE_WORK_PAGER:
				result = context.getString( R.string.type_work_pager );
				break;
			case Phone.TYPE_ASSISTANT:
				result = context.getString( R.string.type_assistant );
				break;
			case Phone.TYPE_FAX_HOME:
				result = context.getString( R.string.type_fax_home );
				break;
			case Phone.TYPE_FAX_WORK:
				result = context.getString( R.string.type_work );
				break;
			case Phone.TYPE_OTHER_FAX:
				result = context.getString( R.string.type_other_fax );
				break;
			case Phone.TYPE_CUSTOM:
				result = personalizado;
				break;
			case Phone.TYPE_OTHER:
				result = context.getString( R.string.type_other );
				break;
		}
		return result;
	}

	/**
	 * Obtiene los datos obtenidos de los contactos SIM
	 * @param data	Objeto contenedor de los datos
	 * @return
	 */
	public static ContactVO getContactFromPickSIM(Intent data) {
		ContactVO vo = new ContactVO();
		
		vo.setId( data.getLongExtra( Constants.EXTRA_PARAM_ID, -10 ) );
		vo.setTelefono( data.getStringExtra( Constants.EXTRA_PARAM_TELEFONO ) );
		vo.setNombre( data.getStringExtra( Constants.EXTRA_PARAM_NOMBRE ) );
		vo.setTipoTelefono( Phone.TYPE_OTHER );
		
		return vo;
	}
}
