package jv.supermarket.cart;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jv.supermarket.product.Product;
import jv.supermarket.product.ProductService;
import jv.supermarket.shared.customexception.OutOfStockException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;
import jv.supermarket.stock.StockService;

@Service
public class CartItemService {

    final CartItemRepository cartItemRepository;

    final ProductService productService;

    final CartService cartService;

    final StockService stockService;

    CartItemService(CartService cartService, ProductService productService, CartItemRepository cartItemRepository, StockService stockService) {
        this.cartService = cartService;
        this.productService = productService;
        this.cartItemRepository = cartItemRepository;
        this.stockService = stockService;
    }

    @Transactional
    public void addItemToCart(Long productId, int quantity, Long cartId) {
        Product product = productService.getProductById(productId);

        if (!stockService.isStockAvailable(quantity, productId)) {
            throw new OutOfStockException("The product stock is insufficient for the requested quantity");
        }
        
        if (quantity < 1) {
            throw new IllegalArgumentException("Item quantity must be greater than 0");
        }

        Cart cart = cartService.getCartById(cartId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> createNewCartItem(cart, product, quantity));

        if (cartItem.getId() == null) {
            saveNewCartItem(cart, cartItem);
        } else {
            updateItemQuantity(cartId, productId, quantity);
        }
    }

    private CartItem createNewCartItem(Cart cart, Product product, int quantity) {
        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        return newItem;
    }

    private void saveNewCartItem(Cart cart, CartItem cartItem) {
        cartItemRepository.save(cartItem);
        cart.addItem(cartItem);
        cartService.saveCart(cart);
    }

    @Transactional
    public void removeItemFromCart(Long cartId, Long productId) {
        CartItem item = getCartItem(cartId, productId);
        Cart cart = cartService.getCartById(cartId);

        cart.removeItem(item);

        cartService.saveCart(cart);
    }

    @Transactional
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        CartItem item = getCartItem(cartId, productId);

        if (quantity < 1) {
            throw new IllegalArgumentException("Item quantity must be greater than 0");
        }

        if (!stockService.isStockAvailable(quantity, productId)) {
            throw new OutOfStockException("The product stock is insufficient for the requested quantity");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    private CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCartById(cartId);

        return cart.getItems().stream()
                .filter(items -> items.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No product with id: " + productId + " found in cart with id: " + cartId));
    }

}
