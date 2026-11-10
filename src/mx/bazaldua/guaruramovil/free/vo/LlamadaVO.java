package mx.bazaldua.guaruramovil.free.vo;

import java.sql.Date;
import java.util.Comparator;

public class LlamadaVO extends ContactVO implements Comparator<LlamadaVO> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8429613152306280824L;
	private Date fecha;
	private Integer duracion;
	private Integer tipo;
	
	public LlamadaVO() {
		super();
	}
	
	public LlamadaVO(Long id, String nombre, String telefono, Date fecha, Integer duracion, Integer tipo) {
		setId( id );
		setNombre(nombre);
		setTelefono(telefono);
		this.fecha    = fecha;
		this.duracion = duracion;
		this.tipo     = tipo;
	}
	
	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}
	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	/**
	 * @return the duracion
	 */
	public Integer getDuracion() {
		return duracion == null ? 0 : duracion;
	}
	/**
	 * @param duracion the duracion to set
	 */
	public void setDuracion(Integer duracion) {
		this.duracion = duracion;
	}
	/**
	 * @return the tipo
	 */
	public Integer getTipo() {
		return tipo;
	}
	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	@Override
	public int compare(LlamadaVO arg0, LlamadaVO arg1) {
		return arg1.getFecha().compareTo( arg0.getFecha() );
	}
}
