package uniandes.dpoo.aerolinea.modelo;

public class Avion {
	private String nombre;

	private int capacidad;

	public Avion(String nombre, int capacidad) {
		this.nombre = nombre;
		this.capacidad = capacidad;
	}

	public String getNombre() {
		return this.nombre;
	}

	public int getCapacidad() {
		return this.capacidad;
	}

}