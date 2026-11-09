package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Producto;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoMenuTest
{
    private ProductoMenu corral;

    private ProductoMenu papas;

    @BeforeEach
    void setUp( ) throws Exception
    {
        corral = new ProductoMenu( "corral", 14000 );
        papas = new ProductoMenu( "papas medianas", 5500 );
    }

    @Test
    void testGetNombre( )
    {
        assertEquals( "corral", corral.getNombre( ), "El nombre del producto no es el esperado." );
        assertEquals( "papas medianas", papas.getNombre( ), "El nombre del producto no es el esperado." );
    }

    @Test
    void testGetPrecio( )
    {
        assertEquals( 14000, corral.getPrecio( ), "El precio del producto no es el esperado." );
        assertEquals( 5500, papas.getPrecio( ), "El precio del producto no es el esperado." );
    }

    @Test
    void testGenerarTextoFactura( )
    {
        String esperado = "corral\n" + "            14000\n";
        assertEquals( esperado, corral.generarTextoFactura( ), "El texto de la factura no es el esperado." );

        esperado = "papas medianas\n" + "            5500\n";
        assertEquals( esperado, papas.generarTextoFactura( ), "El texto de la factura no es el esperado." );
    }

    @Test
    void testSeComportaComoProducto( )
    {
        Producto producto = corral;
        assertEquals( "corral", producto.getNombre( ) );
        assertEquals( 14000, producto.getPrecio( ) );
    }
}
