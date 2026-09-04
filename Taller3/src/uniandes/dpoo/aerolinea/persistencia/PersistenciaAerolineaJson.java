package uniandes.dpoo.aerolinea.persistencia;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import uniandes.dpoo.aerolinea.exceptions.AeropuertoDuplicadoException;
import uniandes.dpoo.aerolinea.exceptions.InformacionInconsistenteException;
import uniandes.dpoo.aerolinea.modelo.Aerolinea;
import uniandes.dpoo.aerolinea.modelo.Aeropuerto;
import uniandes.dpoo.aerolinea.modelo.Avion;
import uniandes.dpoo.aerolinea.modelo.Ruta;
import uniandes.dpoo.aerolinea.modelo.Vuelo;

public class PersistenciaAerolineaJson implements IPersistenciaAerolinea
{
    private static final String NOMBRE = "nombre";
    private static final String CODIGO = "codigo";
    private static final String NOMBRE_CIUDAD = "nombreCiudad";
    private static final String LATITUD = "latitud";
    private static final String LONGITUD = "longitud";

    private static final String CODIGO_RUTA = "codigoRuta";
    private static final String ORIGEN = "origen";
    private static final String DESTINO = "destino";
    private static final String HORA_SALIDA = "horaSalida";
    private static final String HORA_LLEGADA = "horaLlegada";

    private static final String CAPACIDAD = "capacidad";

    private static final String FECHA = "fecha";
    private static final String AVION = "avion";

    @Override
    public void cargarAerolinea( String archivo, Aerolinea aerolinea ) throws IOException, InformacionInconsistenteException
    {
        String jsonCompleto = new String( Files.readAllBytes( new File( archivo ).toPath( ) ) );
        JSONObject raiz = new JSONObject( jsonCompleto );

        Map<String, Aeropuerto> aeropuertos = cargarAeropuertos( raiz.getJSONArray( "aeropuertos" ) );
        cargarRutas( aerolinea, raiz.getJSONArray( "rutas" ), aeropuertos );
        cargarAviones( aerolinea, raiz.getJSONArray( "aviones" ) );
        cargarVuelos( aerolinea, raiz.getJSONArray( "vuelos" ) );
    }

    @Override
    public void salvarAerolinea( String archivo, Aerolinea aerolinea ) throws IOException
    {
        JSONObject jobject = new JSONObject( );

        salvarAeropuertos( aerolinea, jobject );
        salvarRutas( aerolinea, jobject );
        salvarAviones( aerolinea, jobject );
        salvarVuelos( aerolinea, jobject );

        PrintWriter pw = new PrintWriter( archivo );
        jobject.write( pw, 2, 0 );
        pw.close( );
    }

    private Map<String, Aeropuerto> cargarAeropuertos( JSONArray jAeropuertos ) throws InformacionInconsistenteException
    {
        Map<String, Aeropuerto> aeropuertos = new LinkedHashMap<String, Aeropuerto>( );

        int numAeropuertos = jAeropuertos.length( );
        for( int i = 0; i < numAeropuertos; i++ )
        {
            JSONObject jAeropuerto = jAeropuertos.getJSONObject( i );

            String nombre = jAeropuerto.getString( NOMBRE );
            String codigo = jAeropuerto.getString( CODIGO );
            String nombreCiudad = jAeropuerto.getString( NOMBRE_CIUDAD );
            double latitud = jAeropuerto.getDouble( LATITUD );
            double longitud = jAeropuerto.getDouble( LONGITUD );

            try
            {
                Aeropuerto nuevoAeropuerto = new Aeropuerto( nombre, codigo, nombreCiudad, latitud, longitud );
                aeropuertos.put( codigo, nuevoAeropuerto );
            }
            catch( AeropuertoDuplicadoException e )
            {
                throw new InformacionInconsistenteException( e.getMessage( ) );
            }
        }

        return aeropuertos;
    }

    private void cargarRutas( Aerolinea aerolinea, JSONArray jRutas, Map<String, Aeropuerto> aeropuertos ) throws InformacionInconsistenteException
    {
        int numRutas = jRutas.length( );
        for( int i = 0; i < numRutas; i++ )
        {
            JSONObject jRuta = jRutas.getJSONObject( i );

            String codigoRuta = jRuta.getString( CODIGO_RUTA );

            String codigoOrigen = jRuta.getString( ORIGEN );
            Aeropuerto origen = aeropuertos.get( codigoOrigen );
            if( origen == null )
                throw new InformacionInconsistenteException( "No existe un aeropuerto con el código '" + codigoOrigen + "', requerido como origen de la ruta '" + codigoRuta + "'" );

            String codigoDestino = jRuta.getString( DESTINO );
            Aeropuerto destino = aeropuertos.get( codigoDestino );
            if( destino == null )
                throw new InformacionInconsistenteException( "No existe un aeropuerto con el código '" + codigoDestino + "', requerido como destino de la ruta '" + codigoRuta + "'" );

            String horaSalida = jRuta.getString( HORA_SALIDA );
            String horaLlegada = jRuta.getString( HORA_LLEGADA );

            Ruta nuevaRuta = new Ruta( origen, destino, horaSalida, horaLlegada, codigoRuta );
            aerolinea.agregarRuta( nuevaRuta );
        }
    }

    private void cargarAviones( Aerolinea aerolinea, JSONArray jAviones )
    {
        int numAviones = jAviones.length( );
        for( int i = 0; i < numAviones; i++ )
        {
            JSONObject jAvion = jAviones.getJSONObject( i );

            String nombre = jAvion.getString( NOMBRE );
            int capacidad = jAvion.getInt( CAPACIDAD );

            Avion nuevoAvion = new Avion( nombre, capacidad );
            aerolinea.agregarAvion( nuevoAvion );
        }
    }

    private void cargarVuelos( Aerolinea aerolinea, JSONArray jVuelos ) throws InformacionInconsistenteException
    {
        int numVuelos = jVuelos.length( );
        for( int i = 0; i < numVuelos; i++ )
        {
            JSONObject jVuelo = jVuelos.getJSONObject( i );

            String codigoRuta = jVuelo.getString( CODIGO_RUTA );
            String fecha = jVuelo.getString( FECHA );
            String nombreAvion = jVuelo.getString( AVION );

            try
            {
                aerolinea.programarVuelo( fecha, codigoRuta, nombreAvion );
            }
            catch( Exception e )
            {
                throw new InformacionInconsistenteException( e.getMessage( ) );
            }
        }
    }

    private void salvarAeropuertos( Aerolinea aerolinea, JSONObject jobject )
    {
        Map<String, Aeropuerto> aeropuertos = new LinkedHashMap<String, Aeropuerto>( );
        for( Ruta ruta : aerolinea.getRutas( ) )
        {
            aeropuertos.put( ruta.getOrigen( ).getCodigo( ), ruta.getOrigen( ) );
            aeropuertos.put( ruta.getDestino( ).getCodigo( ), ruta.getDestino( ) );
        }

        JSONArray jAeropuertos = new JSONArray( );
        for( Aeropuerto aeropuerto : aeropuertos.values( ) )
        {
            JSONObject jAeropuerto = new JSONObject( );
            jAeropuerto.put( NOMBRE, aeropuerto.getNombre( ) );
            jAeropuerto.put( CODIGO, aeropuerto.getCodigo( ) );
            jAeropuerto.put( NOMBRE_CIUDAD, aeropuerto.getNombreCiudad( ) );
            jAeropuerto.put( LATITUD, aeropuerto.getLatitud( ) );
            jAeropuerto.put( LONGITUD, aeropuerto.getLongitud( ) );

            jAeropuertos.put( jAeropuerto );
        }

        jobject.put( "aeropuertos", jAeropuertos );
    }

    private void salvarRutas( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jRutas = new JSONArray( );
        for( Ruta ruta : aerolinea.getRutas( ) )
        {
            JSONObject jRuta = new JSONObject( );
            jRuta.put( CODIGO_RUTA, ruta.getCodigoRuta( ) );
            jRuta.put( ORIGEN, ruta.getOrigen( ).getCodigo( ) );
            jRuta.put( DESTINO, ruta.getDestino( ).getCodigo( ) );
            jRuta.put( HORA_SALIDA, ruta.getHoraSalida( ) );
            jRuta.put( HORA_LLEGADA, ruta.getHoraLlegada( ) );

            jRutas.put( jRuta );
        }

        jobject.put( "rutas", jRutas );
    }

    private void salvarAviones( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jAviones = new JSONArray( );
        for( Avion avion : aerolinea.getAviones( ) )
        {
            JSONObject jAvion = new JSONObject( );
            jAvion.put( NOMBRE, avion.getNombre( ) );
            jAvion.put( CAPACIDAD, avion.getCapacidad( ) );

            jAviones.put( jAvion );
        }

        jobject.put( "aviones", jAviones );
    }

    private void salvarVuelos( Aerolinea aerolinea, JSONObject jobject )
    {
        JSONArray jVuelos = new JSONArray( );
        for( Vuelo vuelo : aerolinea.getVuelos( ) )
        {
            JSONObject jVuelo = new JSONObject( );
            jVuelo.put( CODIGO_RUTA, vuelo.getRuta( ).getCodigoRuta( ) );
            jVuelo.put( FECHA, vuelo.getFecha( ) );
            jVuelo.put( AVION, vuelo.getAvion( ).getNombre( ) );

            jVuelos.put( jVuelo );
        }

        jobject.put( "vuelos", jVuelos );
    }

}