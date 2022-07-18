import com.epam.tamentoring.bo.*;
import com.epam.tamentoring.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ShoppingCartUnitTests {
    @Mock
    private DiscountUtility discountUtility;

    @InjectMocks
    private OrderService orderService;

    public static final Product NOTEBOOK = new Product(1, "notebook", 1000, 2);
    public static final Product SMARTPHONE = new Product(2, "smartphone", 400, 1);
    public static final Product TV = new Product(3, "tv", 1200, 3);
    public static final Product EBOOK = new Product(4, "e-book", 250, 1);

    @Test
    public void addProductsToCartTest() {
        ShoppingCart shoppingCart = new ShoppingCart(new ArrayList(Arrays.asList(NOTEBOOK, SMARTPHONE)));
        UserAccount user = new UserAccount("John", "Smith", "1990/10/10", shoppingCart);
        user.getShoppingCart().addProductToCart(TV);
        assertEquals(Arrays.asList(NOTEBOOK, SMARTPHONE, TV), user.getShoppingCart().getProducts());
    }

    @Test
    public void addAlreadyExistingProductInCartTest() {
        ShoppingCart shoppingCart = new ShoppingCart(Arrays.asList(SMARTPHONE, EBOOK));
        UserAccount user = new UserAccount("John", "Smith", "1990/10/10", shoppingCart);
        user.getShoppingCart().addProductToCart(SMARTPHONE);
        assertEquals(2, user.getShoppingCart().getProductById(2).getQuantity());
    }

    @Test
    public void removeProductsFromCartTest() {
        ShoppingCart shoppingCart = new ShoppingCart(new ArrayList(Arrays.asList(TV, SMARTPHONE)));
        UserAccount user = new UserAccount("John", "Smith", "1990/10/10", shoppingCart);
        user.getShoppingCart().removeProductFromCart(SMARTPHONE);
        assertEquals(Arrays.asList(TV), user.getShoppingCart().getProducts());
    }

    @Test
    public void getTotalPriceOfCartTest() {
        ShoppingCart shoppingCart = new ShoppingCart(Arrays.asList(NOTEBOOK, TV));
        UserAccount user = new UserAccount("John", "Smith", "1990/10/10", shoppingCart);
        assertEquals(5600, user.getShoppingCart().getCartTotalPrice());
    }

    @Test
    public void tryToRemoveNotExistentProductFromCartTest() {
        ShoppingCart shoppingCart = new ShoppingCart(Arrays.asList(EBOOK));
        UserAccount user = new UserAccount("John", "Smith", "1990/10/10", shoppingCart);
        assertThrows(ProductNotFoundException.class, () -> user.getShoppingCart().removeProductFromCart(SMARTPHONE));
    }

    @Test
    public void tryToGetNotExistingProductFromCartTest() {
        ShoppingCart shoppingCart = new ShoppingCart(Arrays.asList(TV));
        UserAccount user = new UserAccount("John", "Smith", "1990/10/10", shoppingCart);
        assertThrows(ProductNotFoundException.class, () -> user.getShoppingCart().getProductById(1));
    }

    @Test
    public void getDiscountTest() {
        MockitoAnnotations.initMocks(this);
        ShoppingCart shoppingCart = new ShoppingCart(new ArrayList(Arrays.asList(NOTEBOOK, SMARTPHONE)));
        UserAccount user = new UserAccount("John", "Smith", "1990/10/10", shoppingCart);
        Mockito.when(discountUtility.calculateDiscount(user)).thenReturn(3.0);
        assertEquals(shoppingCart.getCartTotalPrice() - 3.0, orderService.getOrderPrice(user));
        Mockito.verify(discountUtility, Mockito.times(1)).calculateDiscount(user);
        Mockito.verifyNoMoreInteractions(discountUtility);
    }
}
