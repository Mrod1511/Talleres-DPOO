package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

public class RestauranteTest
{
    // Archivos reales de la aplicación
    private static final File INGREDIENTES = new File( "./data/ingredientes.txt" );

    private static final File MENU = new File( "./data/menu.txt" );

    private static final File COMBOS = new File( "./data/combos.txt" );

    // Archivos pequeños construidos para las pruebas
    private static final String CARPETA_PRUEBAS = "./data/pruebas/";

    private static final File INGREDIENTES_PEQUENO = new File( CARPETA_PRUEBAS + "ingredientes_pequeno.txt" );

    private static final File MENU_PEQUENO = new File( CARPETA_PRUEBAS + "menu_pequeno.txt" );

    private static final File COMBOS_PEQUENO = new File( CARPETA_PRUEBAS + "combos_pequeno.txt" );

    private static final File INGREDIENTES_REPETIDOS = new File( CARPETA_PRUEBAS + "ingredientes_repetidos.txt" );

    private static final File MENU_REPETIDO = new File( CARPETA_PRUEBAS + "menu_repetido.txt" );

    private static final File COMBOS_REPETIDOS = new File( CARPETA_PRUEBAS + "combos_repetidos.txt" );

    private static final File COMBOS_FALTANTE = new File( CARPETA_PRUEBAS + "combos_faltante.txt" );

    private static final File INGREDIENTES_MALFORMADO = new File( CARPETA_PRUEBAS + "ingredientes_malformado.txt" );

    private static final File MENU_MALFORMADO = new File( CARPETA_PRUEBAS + "menu_malformado.txt" );

    private static final File COMBOS_MALFORMADO = new File( CARPETA_PRUEBAS + "combos_malformado.txt" );

    private static final File NO_EXISTE = new File( CARPETA_PRUEBAS + "no_existe.txt" );

    // Carpeta y prefijo que usa Restaurante para guardar las facturas
    private static final String CARPETA_FACTURAS = "./facturas/";

    private static final String PREFIJO_FACTURAS = "factura_";

    private Restaurante restaurante;

    private ArrayList<File> facturasCreadas;

    @BeforeEach
    void setUp( ) throws Exception
    {
        restaurante = new Restaurante( );
        facturasCreadas = new ArrayList<File>( );
    }

    @AfterEach
    void tearDown( ) throws Exception
    {
        for( File factura : facturasCreadas )
        {
            factura.delete( );
        }
    }

    /**
     * Cierra el pedido en curso y registra el archivo de la factura para borrarlo al terminar la prueba.
     */
    private File cerrarPedidoEnCurso( ) throws Exception
    {
        int id = restaurante.getPedidoEnCurso( ).getIdPedido( );
        File factura = new File( CARPETA_FACTURAS + PREFIJO_FACTURAS + id + ".txt" );
        facturasCreadas.add( factura );
        restaurante.cerrarYGuardarPedido( );
        return factura;
    }

    private ProductoMenu buscarProducto( String nombre )
    {
        ProductoMenu encontrado = null;
        for( ProductoMenu p : restaurante.getMenuBase( ) )
        {
            if( p.getNombre( ).equals( nombre ) )
            {
                encontrado = p;
            }
        }
        return encontrado;
    }

    private Combo buscarCombo( String nombre )
    {
        Combo encontrado = null;
        for( Combo c : restaurante.getMenuCombos( ) )
        {
            if( c.getNombre( ).equals( nombre ) )
            {
                encontrado = c;
            }
        }
        return encontrado;
    }

    private Ingrediente buscarIngrediente( String nombre )
    {
        Ingrediente encontrado = null;
        for( Ingrediente i : restaurante.getIngredientes( ) )
        {
            if( i.getNombre( ).equals( nombre ) )
            {
                encontrado = i;
            }
        }
        return encontrado;
    }

    // ---------------------------------------------------------------
    // Constructor y consultas
    // ---------------------------------------------------------------

    @Test
    void testRestauranteNuevoEstaVacio( )
    {
        assertNotNull( restaurante.getPedidos( ), "La lista de pedidos no debe ser null." );
        assertNotNull( restaurante.getIngredientes( ), "La lista de ingredientes no debe ser null." );
        assertNotNull( restaurante.getMenuBase( ), "El menú base no debe ser null." );
        assertNotNull( restaurante.getMenuCombos( ), "El menú de combos no debe ser null." );

        assertTrue( restaurante.getPedidos( ).isEmpty( ) );
        assertTrue( restaurante.getIngredientes( ).isEmpty( ) );
        assertTrue( restaurante.getMenuBase( ).isEmpty( ) );
        assertTrue( restaurante.getMenuCombos( ).isEmpty( ) );
        assertNull( restaurante.getPedidoEnCurso( ), "Un restaurante nuevo no tiene pedido en curso." );
    }

    // ---------------------------------------------------------------
    // iniciarPedido
    // ---------------------------------------------------------------

    @Test
    void testIniciarPedido( ) throws Exception
    {
        restaurante.iniciarPedido( "Matias", "Calle 123" );

        Pedido enCurso = restaurante.getPedidoEnCurso( );
        assertNotNull( enCurso, "Debe haber un pedido en curso." );
        assertEquals( "Matias", enCurso.getNombreCliente( ) );
        assertTrue( restaurante.getPedidos( ).isEmpty( ), "El pedido en curso aún no es un pedido cerrado." );
    }

    @Test
    void testIniciarPedidoCuandoYaHayUnoLanzaExcepcion( ) throws Exception
    {
        restaurante.iniciarPedido( "Matias", "Calle 123" );
        Pedido primero = restaurante.getPedidoEnCurso( );

        YaHayUnPedidoEnCursoException excepcion = assertThrows( YaHayUnPedidoEnCursoException.class, ( ) -> restaurante.iniciarPedido( "Ana", "Carrera 7" ) );

        assertTrue( excepcion.getMessage( ).contains( "Matias" ), "El mensaje debe incluir el cliente del pedido en curso." );
        assertTrue( excepcion.getMessage( ).contains( "Ana" ), "El mensaje debe incluir el nuevo cliente." );
        assertSame( primero, restaurante.getPedidoEnCurso( ), "El pedido en curso no debe cambiar." );
    }

    // ---------------------------------------------------------------
    // cerrarYGuardarPedido
    // ---------------------------------------------------------------

    @Test
    void testCerrarSinPedidoEnCursoLanzaExcepcion( )
    {
        assertThrows( NoHayPedidoEnCursoException.class, ( ) -> restaurante.cerrarYGuardarPedido( ) );
        assertTrue( restaurante.getPedidos( ).isEmpty( ) );
    }

    @Test
    void testCerrarYGuardarPedido( ) throws Exception
    {
        restaurante.iniciarPedido( "Matias", "Calle 123" );
        Pedido pedido = restaurante.getPedidoEnCurso( );
        pedido.agregarProducto( new ProductoMenu( "corral", 14000 ) );
        pedido.agregarProducto( new ProductoMenu( "papas medianas", 5500 ) );
        String textoEsperado = pedido.generarTextoFactura( );

        File factura = cerrarPedidoEnCurso( );

        assertNull( restaurante.getPedidoEnCurso( ), "Al cerrar, ya no debe haber pedido en curso." );
        assertEquals( 1, restaurante.getPedidos( ).size( ), "El pedido cerrado debe quedar en el histórico." );
        assertSame( pedido, restaurante.getPedidos( ).get( 0 ), "El pedido guardado debe ser el que se cerró." );

        assertTrue( factura.exists( ), "Debe existir el archivo " + factura.getPath( ) );
        String contenido = new String( Files.readAllBytes( factura.toPath( ) ), Charset.defaultCharset( ) );
        assertEquals( textoEsperado, contenido, "El archivo debe contener la factura del pedido." );
    }

    @Test
    void testCerrarPedidoVacio( ) throws Exception
    {
        restaurante.iniciarPedido( "Matias", "Calle 123" );
        File factura = cerrarPedidoEnCurso( );

        assertTrue( factura.exists( ) );
        assertEquals( 1, restaurante.getPedidos( ).size( ) );
    }

    @Test
    void testCerrarDosVecesLanzaExcepcion( ) throws Exception
    {
        restaurante.iniciarPedido( "Matias", "Calle 123" );
        cerrarPedidoEnCurso( );

        assertThrows( NoHayPedidoEnCursoException.class, ( ) -> restaurante.cerrarYGuardarPedido( ), "No se puede cerrar dos veces el mismo pedido." );
        assertEquals( 1, restaurante.getPedidos( ).size( ) );
    }

    @Test
    void testIniciarPedidoDespuesDeCerrarOtro( ) throws Exception
    {
        restaurante.iniciarPedido( "Matias", "Calle 123" );
        cerrarPedidoEnCurso( );

        assertDoesNotThrow( ( ) -> restaurante.iniciarPedido( "Ana", "Carrera 7" ), "Después de cerrar un pedido se puede iniciar otro." );
        assertEquals( "Ana", restaurante.getPedidoEnCurso( ).getNombreCliente( ) );
    }

    @Test
    void testVariosPedidosCerradosSeConsultanPorId( ) throws Exception
    {
        restaurante.iniciarPedido( "Matias", "Calle 123" );
        int idPrimero = restaurante.getPedidoEnCurso( ).getIdPedido( );
        File facturaPrimero = cerrarPedidoEnCurso( );

        restaurante.iniciarPedido( "Ana", "Carrera 7" );
        int idSegundo = restaurante.getPedidoEnCurso( ).getIdPedido( );
        File facturaSegundo = cerrarPedidoEnCurso( );

        assertNotSame( facturaPrimero, facturaSegundo );
        assertTrue( facturaPrimero.exists( ) && facturaSegundo.exists( ), "Cada pedido debe tener su propio archivo." );
        assertEquals( 2, restaurante.getPedidos( ).size( ) );
        assertEquals( idPrimero + 1, idSegundo );

        ArrayList<Pedido> pedidos = restaurante.getPedidos( );
        assertEquals( idPrimero, pedidos.get( 0 ).getIdPedido( ) );
        assertEquals( "Matias", pedidos.get( 0 ).getNombreCliente( ) );
        assertEquals( idSegundo, pedidos.get( 1 ).getIdPedido( ) );
        assertEquals( "Ana", pedidos.get( 1 ).getNombreCliente( ) );
    }

    // ---------------------------------------------------------------
    // cargarInformacionRestaurante: archivos reales
    // ---------------------------------------------------------------

    @Test
    void testCargarInformacionArchivosReales( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU, COMBOS );

        assertEquals( 15, restaurante.getIngredientes( ).size( ), "El archivo de ingredientes tiene 15 ingredientes." );
        assertEquals( 22, restaurante.getMenuBase( ).size( ), "El archivo de menú tiene 22 productos." );
        assertEquals( 4, restaurante.getMenuCombos( ).size( ), "El archivo de combos tiene 4 combos." );

        Ingrediente lechuga = restaurante.getIngredientes( ).get( 0 );
        assertEquals( "lechuga", lechuga.getNombre( ) );
        assertEquals( 1000, lechuga.getCostoAdicional( ) );
        assertEquals( 4500, buscarIngrediente( "frijol refrito" ).getCostoAdicional( ) );

        ProductoMenu corral = restaurante.getMenuBase( ).get( 0 );
        assertEquals( "corral", corral.getNombre( ) );
        assertEquals( 14000, corral.getPrecio( ) );
        assertEquals( 20900, buscarProducto( "ensalada mexicana" ).getPrecio( ) );
        assertEquals( 5000, buscarProducto( "gaseosa" ).getPrecio( ) );
    }

    @Test
    void testCargarCombosArchivosReales( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU, COMBOS );

        // corral (14000) + papas medianas (5500) + gaseosa (5000) = 24500, con 10% de descuento
        assertEquals( 22050, buscarCombo( "combo corral" ).getPrecio( ), "Precio de combo corral." );
        // corral queso (16000) + papas medianas (5500) + gaseosa (5000) = 26500, con 10% de descuento
        assertEquals( 23850, buscarCombo( "combo corral queso" ).getPrecio( ), "Precio de combo corral queso." );
        // todoterreno (25000) + papas grandes (6900) + gaseosa (5000) = 36900, con 7% de descuento
        assertEquals( 34317, buscarCombo( "combo todoterreno" ).getPrecio( ), "Precio de combo todoterreno." );
        // especial (24000) + papas medianas (5500) + gaseosa (5000) = 34500, con 9.5% de descuento = 31222.5
        assertEquals( 31222, buscarCombo( "combo especial" ).getPrecio( ), "Precio de combo especial." );
    }

    // ---------------------------------------------------------------
    // cargarInformacionRestaurante: archivos pequeños (con líneas vacías)
    // ---------------------------------------------------------------

    @Test
    void testCargarInformacionArchivosPequenos( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_PEQUENO, COMBOS_PEQUENO );

        assertEquals( 3, restaurante.getIngredientes( ).size( ), "Las líneas vacías no son ingredientes." );
        assertEquals( 3, restaurante.getMenuBase( ).size( ), "Las líneas vacías no son productos." );
        assertEquals( 2, restaurante.getMenuCombos( ).size( ), "Las líneas vacías no son combos." );

        assertEquals( "queso mozzarella", restaurante.getIngredientes( ).get( 2 ).getNombre( ) );
        assertEquals( 2500, restaurante.getIngredientes( ).get( 2 ).getCostoAdicional( ) );
        assertEquals( "papas medianas", restaurante.getMenuBase( ).get( 1 ).getNombre( ) );
        assertEquals( 5500, restaurante.getMenuBase( ).get( 1 ).getPrecio( ) );

        assertEquals( 22050, buscarCombo( "combo corral" ).getPrecio( ) );
        assertEquals( 14000, buscarCombo( "combo doble" ).getPrecio( ), "Un combo puede repetir un producto: (14000 + 14000) con 50%." );
    }

    @Test
    void testCargarInformacionCombosUsanLosProductosDelMenu( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_PEQUENO, COMBOS_PEQUENO );

        // Si el combo se armó con los productos del menú, su precio refleja los precios del menú cargado
        Combo combo = buscarCombo( "combo corral" );
        int sumaMenu = buscarProducto( "corral" ).getPrecio( ) + buscarProducto( "papas medianas" ).getPrecio( ) + buscarProducto( "gaseosa" ).getPrecio( );
        assertEquals( ( int ) ( sumaMenu * 0.9 ), combo.getPrecio( ) );
    }

    // ---------------------------------------------------------------
    // cargarInformacionRestaurante: excepciones
    // ---------------------------------------------------------------

    @Test
    void testCargarIngredienteRepetidoLanzaExcepcion( )
    {
        IngredienteRepetidoException excepcion = assertThrows( IngredienteRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_REPETIDOS, MENU_PEQUENO, COMBOS_PEQUENO ) );

        assertTrue( excepcion.getMessage( ).contains( "lechuga" ), "El mensaje debe incluir el ingrediente repetido." );
    }

    @Test
    void testCargarProductoRepetidoLanzaExcepcion( )
    {
        ProductoRepetidoException excepcion = assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_REPETIDO, COMBOS_PEQUENO ) );

        assertTrue( excepcion.getMessage( ).contains( "corral" ), "El mensaje debe incluir el producto repetido." );
    }

    @Test
    void testCargarComboRepetidoLanzaExcepcion( )
    {
        ProductoRepetidoException excepcion = assertThrows( ProductoRepetidoException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_PEQUENO, COMBOS_REPETIDOS ) );

        assertTrue( excepcion.getMessage( ).contains( "combo corral" ), "El mensaje debe incluir el combo repetido." );
    }

    @Test
    void testCargarComboConProductoFaltanteLanzaExcepcion( )
    {
        ProductoFaltanteException excepcion = assertThrows( ProductoFaltanteException.class,
                ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_PEQUENO, COMBOS_FALTANTE ) );

        assertTrue( excepcion.getMessage( ).contains( "pizza" ), "El mensaje debe incluir el producto faltante." );
    }

    @Test
    void testCargarInformacionDosVecesLanzaExcepcion( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_PEQUENO, COMBOS_PEQUENO );

        assertThrows( IngredienteRepetidoException.class, ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_PEQUENO, COMBOS_PEQUENO ),
                "Cargar dos veces el mismo archivo repite los ingredientes." );
    }

    @Test
    void testCargarIngredientesMalformadosLanzaExcepcion( )
    {
        assertThrows( NumberFormatException.class, ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_MALFORMADO, MENU_PEQUENO, COMBOS_PEQUENO ) );
    }

    @Test
    void testCargarMenuMalformadoLanzaExcepcion( )
    {
        assertThrows( NumberFormatException.class, ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_MALFORMADO, COMBOS_PEQUENO ) );
    }

    @Test
    void testCargarCombosMalformadosLanzaExcepcion( )
    {
        assertThrows( NumberFormatException.class, ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_PEQUENO, COMBOS_MALFORMADO ) );
    }

    @Test
    void testCargarArchivoDeIngredientesInexistenteLanzaExcepcion( )
    {
        assertThrows( IOException.class, ( ) -> restaurante.cargarInformacionRestaurante( NO_EXISTE, MENU_PEQUENO, COMBOS_PEQUENO ) );
    }

    @Test
    void testCargarArchivoDeMenuInexistenteLanzaExcepcion( )
    {
        assertThrows( IOException.class, ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, NO_EXISTE, COMBOS_PEQUENO ) );
    }

    @Test
    void testCargarArchivoDeCombosInexistenteLanzaExcepcion( )
    {
        assertThrows( IOException.class, ( ) -> restaurante.cargarInformacionRestaurante( INGREDIENTES_PEQUENO, MENU_PEQUENO, NO_EXISTE ) );
    }

    // ---------------------------------------------------------------
    // Prueba de integración: flujo completo
    // ---------------------------------------------------------------

    @Test
    void testFlujoCompletoDeUnPedido( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( INGREDIENTES, MENU, COMBOS );

        restaurante.iniciarPedido( "Matias", "Calle 123 # 45-67" );
        Pedido pedido = restaurante.getPedidoEnCurso( );

        ProductoMenu corral = buscarProducto( "corral" );
        Combo combo = buscarCombo( "combo corral" );
        ProductoAjustado ajustado = new ProductoAjustado( corral );
        ajustado.agregarIngrediente( buscarIngrediente( "queso mozzarella" ) );
        ajustado.eliminarIngrediente( buscarIngrediente( "tomate" ) );

        pedido.agregarProducto( corral );
        pedido.agregarProducto( combo );
        pedido.agregarProducto( ajustado );

        // neto: 14000 + 22050 + 16500 = 52550; IVA: 9984 (trunca 9984.5); total: 62534
        assertEquals( 62534, pedido.getPrecioTotalPedido( ) );

        String esperado = pedido.generarTextoFactura( );
        assertTrue( esperado.endsWith( "Precio Neto:  52550\n" + "IVA:          9984\n" + "Precio Total: 62534\n" ), "Totales de la factura incorrectos:\n" + esperado );
        assertTrue( esperado.contains( "+queso mozzarella" ) && esperado.contains( "-tomate" ), "La factura debe mostrar los ajustes." );

        File factura = cerrarPedidoEnCurso( );

        assertNull( restaurante.getPedidoEnCurso( ) );
        assertEquals( 1, restaurante.getPedidos( ).size( ) );
        assertEquals( esperado, new String( Files.readAllBytes( factura.toPath( ) ), Charset.defaultCharset( ) ) );
    }
}
