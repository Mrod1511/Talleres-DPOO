package uniandes.dpoo.aerolinea.modelo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uniandes.dpoo.aerolinea.exceptions.VueloSobrevendidoException;
import uniandes.dpoo.aerolinea.modelo.cliente.Cliente;
import uniandes.dpoo.aerolinea.modelo.tarifas.CalculadoraTarifas;
import uniandes.dpoo.aerolinea.tiquetes.GeneradorTiquetes;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

public class Vuelo {
	private Ruta ruta;

	private String fecha;

	private Avion avion;

	private Map<String, Tiquete> tiquetes;

	public Vuelo(Ruta ruta, String fecha, Avion avion) {
		this.ruta = ruta;
		this.fecha = fecha;
		this.avion = avion;
		this.tiquetes = new HashMap<String, Tiquete>();
	}

	public Ruta getRuta() {
		return this.ruta;
	}

	public String getFecha() {
		return this.fecha;
	}

	public Avion getAvion() {
		return this.avion;
	}

	public Collection<Tiquete> getTiquetes() {
		return this.tiquetes.values();
	}

	public void agregarTiquete(Tiquete tiquete) {
		this.tiquetes.put(tiquete.getCodigo(), tiquete);
	}

	public int venderTiquetes(Cliente cliente, CalculadoraTarifas calculadora, int cantidad)
			throws VueloSobrevendidoException {
		if (this.tiquetes.size() + cantidad > this.avion.getCapacidad()) {
			throw new VueloSobrevendidoException(this);
		}

		int valorTotal = 0;

		for (int i = 0; i < cantidad; i++) {
			int tarifa = calculadora.calcularTarifa(this, cliente);
			// El propio constructor de Tiquete se encarga de registrar el nuevo tiquete
			// tanto en el cliente como en este vuelo
			GeneradorTiquetes.generarTiquete(this, cliente, tarifa);
			valorTotal += tarifa;
		}

		return valorTotal;
	}

	@Override
	public boolean equals(Object obj) {
		boolean sonIguales = false;

		if (obj instanceof Vuelo) {
			Vuelo otroVuelo = (Vuelo) obj;
			sonIguales = this.ruta.getCodigoRuta().equals(otroVuelo.getRuta().getCodigoRuta())
					&& this.fecha.equals(otroVuelo.getFecha());
		}

		return sonIguales;
	}

}