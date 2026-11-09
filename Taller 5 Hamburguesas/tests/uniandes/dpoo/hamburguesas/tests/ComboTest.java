package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest
{
    private ProductoMenu corral;

    private ProductoMenu papas;

    private ProductoMenu gaseosa;

    private ArrayList<ProductoMenu> items;

    private Combo combo;

    @BeforeEach
    void setUp( ) throws Exception
    {
        corral = new ProductoMenu( "corral", 14000 );
        papas = new ProductoMenu( "papas medianas", 5500 );
        gaseosa = new ProductoMenu( "gaseosa", 5000 );

        items = new ArrayList<ProductoMenu>( );
        items.add( corral );
        items.add( papas );
        items.add( gaseosa );

        // Precio sin descuento: 24500. Con 10% de descuento: 22050
        combo = new Combo( "combo corral", 0.10, items );
    }

    @Test
    void testGetNombre( )
    {
        assertEquals( "combo corral", combo.getNombre( ), "El nombre del combo no es el esperado." );
    }

    @Test
    void testGetPrecioConDescuento( )
    {
        assertEquals( 22050, combo.getPrecio( ), "El precio debe ser el de los productos menos el descuento." );
    }

    @Test
    void testGetPrecioMenorQueLaSumaDeLosProductos( )
    {
        int sinDescuento = corral.getPrecio( ) + papas.getPrecio( ) + gaseosa.getPrecio( );
        assertEquals( 24500, sinDescuento );
        assertEquals( true, combo.getPrecio( ) < sinDescuento, "Un combo con descuento debe costar menos que los productos por separado." );
    }

    @Test
    void testGetPrecioSinDescuento( )
    {
        Combo sinDescuento = new Combo( "combo sin descuento", 0, items );
        assertEquals( 24500, sinDescuento.getPrecio( ), "Con descuento 0 el precio debe ser la suma de los productos." );
    }

    @Test
    void testGetPrecioConDescuentoDelCincuentaPorCiento( )
    {
        Combo mitad = new Combo( "combo mitad", 0.5, items );
        assertEquals( 12250, mitad.getPrecio( ), "Con 50% de descuento el precio debe ser la mitad." );
    }

    @Test
    void testGetPrecioConDescuentoTotal( )
    {
        Combo gratis = new Combo( "combo gratis", 1, items );
        assertEquals( 0, gratis.getPrecio( ), "Con 100% de descuento el combo debe ser gratis." );
    }

    @Test
    void testGetPrecioSeTruncaAEntero( )
    {
        // 14000 + 5500 + 5000 = 24500; con 9.5% de descuento quedan 22172.5, que se trunca a 22172
        Combo especial = new Combo( "combo especial", 0.095, items );
        assertEquals( 22172, especial.getPrecio( ), "El precio del combo debe ser un entero truncado." );
    }

    @Test
    void testGetPrecioConUnSoloProducto( )
    {
        ArrayList<ProductoMenu> unico = new ArrayList<ProductoMenu>( );
        unico.add( corral );
        Combo solo = new Combo( "solo corral", 0.5, unico );
        assertEquals( 7000, solo.getPrecio( ) );
    }

    @Test
    void testGetPrecioConProductosRepetidos( )
    {
        ArrayList<ProductoMenu> dobles = new ArrayList<ProductoMenu>( );
        dobles.add( corral );
        dobles.add( corral );
        Combo doble = new Combo( "combo doble", 0.5, dobles );
        assertEquals( 14000, doble.getPrecio( ), "Cada aparición del producto debe contarse." );
    }

    @Test
    void testComboConCeroProductos( )
    {
        Combo vacio = new Combo( "combo vacio", 0.1, new ArrayList<ProductoMenu>( ) );
        assertEquals( 0, vacio.getPrecio( ) );
    }

    @Test
    void testElComboCopiaLaListaDeItems( )
    {
        items.add( new ProductoMenu( "papas grandes", 6900 ) );
        assertEquals( 22050, combo.getPrecio( ), "Cambiar la lista original no debe afectar al combo." );
    }

    @Test
    void testGenerarTextoFactura( )
    {
        String esperado = "Combo combo corral\n" + " Descuento: 0.1\n" + "            22050\n";
        assertEquals( esperado, combo.generarTextoFactura( ), "El texto de la factura no es el esperado." );
    }
}
