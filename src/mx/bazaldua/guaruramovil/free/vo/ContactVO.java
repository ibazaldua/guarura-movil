package mx.bazaldua.guaruramovil.free.vo;

import java.io.Serializable;

import mx.bazaldua.guaruramovil.free.util.Constants;
import android.graphics.Bitmap;
import android.util.Log;

public class ContactVO implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5429002115321866768L;
	private Long id;
	private String lookupKey;
	private Bitmap foto;
	private String nombre;
	private String telefono;
	private Integer tipoTelefono;
	private String descTipoTelPersonalizado;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the foto
	 */
	public Bitmap getFoto() {
		return foto;
	}
	/**
	 * @param foto the foto to set
	 */
	public void setFoto(Bitmap foto) {
		this.foto = foto;
	}
	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}
	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	/**
	 * @return the telefono
	 */
	public String getTelefono() {
		return telefono;
	}
	/**
	 * @param telefono the telefono to set
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	/**
	 * @return the tipoTelefono
	 */
	public Integer getTipoTelefono() {
		return tipoTelefono;
	}
	/**
	 * @param tipoTelefono the tipoTelefono to set
	 */
	public void setTipoTelefono(Integer tipoTelefono) {
		this.tipoTelefono = tipoTelefono;
	}
	/**
	 * @return the descTelefonoPersonalizado
	 */
	public String getDescTipoTelPersonalizado() {
		return descTipoTelPersonalizado;
	}
	/**
	 * @param descTelefonoPersonalizado the descTelefonoPersonalizado to set
	 */
	public void setDescTipoTelPersonalizado(String descTelefonoPersonalizado) {
		this.descTipoTelPersonalizado = descTelefonoPersonalizado;
	}
	/**
	 * @return the lookupKey
	 */
	public String getLookupKey() {
		return lookupKey;
	}
	/**
	 * @param lookupKey the lookupKey to set
	 */
	public void setLookupKey(String lookupKey) {
		this.lookupKey = lookupKey;
	}
	
	@Override
	public Object clone() {
		Object object = null;
		try {
			object = super.clone();
		} catch (Exception e) {
			Log.e(Constants.LOG_APP, e.getMessage(), e);
		}
		return object;
	}
}
