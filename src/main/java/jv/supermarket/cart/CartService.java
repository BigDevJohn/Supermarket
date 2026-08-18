package jv.supermarket.cart;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jv.supermarket.product.ProductService;
import jv.supermarket.shared.customexception.ResourceNotFoundException;

@Service
public class CartService {

    private final CartRepository cartRepo;

    private final CartItemRepository itemRepo;

    private final ProductService productService;

    CartService(CartRepository cartRepo, CartItemRepository itemRepo, ProductService productService) {
        this.cartRepo = cartRepo;
        this.itemRepo = itemRepo;
        this.productService = productService;
    }

    public Cart saveCart(Cart cart) {
        return cartRepo.save(cart);
    }

    public Cart getCartById(Long cartId) {
        return cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));
    }

    public CartDTO getCart(Long cartId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found with id: " + cartId + ". The user must be a Client to have a cart"));
        return convertToDTO(cart);
    }

    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));
        itemRepo.deleteAllByCartId(cartId);
        cart.clearCart();
    }

    public boolean existsById(Long cartId) {
        return cartRepo.existsById(cartId);
    }

    public CartItemDTO convertItemToDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();

        dto.setProduct(productService.convertToDTO(item.getProduct()));
        dto.setSubTotal(item.getSubTotal());
        dto.setQuantity(item.getQuantity());

        return dto;
    }

    public CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setTotalPrice(cart.getTotalPrice());
        dto.setItems(cart.getItems().stream()
                .map(item -> convertItemToDTO(item))
                .collect(Collectors.toSet()));
        return dto;
    }
}
