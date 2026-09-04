package uniandes.dpoo.aerolinea.modelo.cliente;

import java.util.ArrayList;
import java.util.List;

import uniandes.dpoo.aerolinea.modelo.Vuelo;
import uniandes.dpoo.aerolinea.tiquetes.Tiquete;

public abstract class Cliente {
	private List<Tiquete> tiquetesSinUsar;

	private List<Tiquete> tiquetesUsados;

	public Cliente() {
		this.tiquetesSinUsar = new ArrayList<Tiquete>();
		this.tiquetesUsados = new ArrayList<Tiquete>();
	}

	public abstract String getIdentificador();

	public abstract String getTipoCliente();

	public void agregarTiquete(Tiquete tiquete) {
		this.tiquetesSinUsar.add(tiquete);
	}

	public int calcularValorTotalTiquetes() {
		int total = 0;

		for (Tiquete tiquete : this.tiquetesSinUsar) {
			total += tiquete.getTarifa();
		}

		for (Tiquete tiquete : this.tiquetesUsados) {
			total += tiquete.getTarifa();
		}

		return total;
	}

	public void usarTiquetes(Vuelo vuelo) {
		List<Tiquete> tiquetesDelVuelo = new ArrayList<Tiquete>();

		for (Tiquete tiquete : this.tiquetesSinUsar) {
			if (tiquete.getVuelo().equals(vuelo)) {
				tiquetesDelVuelo.add(tiquete);
			}
		}

		for (Tiquete tiquete : tiquetesDelVuelo) {
			tiquete.marcarComoUsado();
			this.tiquetesSinUsar.remove(tiquete);
			this.tiquetesUsados.add(tiquete);
		}
	}

}