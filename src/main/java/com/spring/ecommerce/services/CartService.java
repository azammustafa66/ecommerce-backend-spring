package com.spring.ecommerce.services;

import com.spring.ecommerce.dto.AddToCartRequest;
import com.spring.ecommerce.dto.CartItemDTO;
import com.spring.ecommerce.dto.CartResponseDTO;
import com.spring.ecommerce.models.Cart;
import com.spring.ecommerce.models.CartItems;
import com.spring.ecommerce.models.Product;
import com.spring.ecommerce.models.User;
import com.spring.ecommerce.repositories.CartItemRepository;
import com.spring.ecommerce.repositories.CartRepository;
import com.spring.ecommerce.repositories.ProductRepository;
import com.spring.ecommerce.utils.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    @Transactional
    public CartResponseDTO addToCart(UUID productId, Integer quantity) {
        User currentUser = authUtil.currentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (product.getProductStock() == null || product.getProductStock() < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product stock less than quantity");
        }

        Cart cart = cartRepository.findByUser_UserId(currentUser.getUserId()).orElseGet(() -> {
            Cart c = new Cart();
            c.setUser(currentUser);
            return cartRepository.save(c);
        });

        double unitPrice = product.getSpecialPrice() != null && product.getSpecialPrice() > 0
                ? product.getSpecialPrice()
                : product.getPrice();

        CartItems item = cartItemRepository
                .findByCart_CartIdAndProduct_Id(cart.getCartId(), productId)
                .orElseGet(() -> {
                    CartItems ci = new CartItems();
                    ci.setCart(cart);
                    ci.setProduct(product);
                    ci.setProductPrice(unitPrice);
                    ci.setQuantity(0);
                    cart.getCartItems().add(ci);
                    return ci;
                });

        long newQty = item.getQuantity() + quantity;
        if (product.getProductStock() < newQty) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock for combined quantity");
        }
        item.setQuantity(newQty);
        cartItemRepository.save(item);

        recomputeTotal(cart);
        cartRepository.save(cart);

        return toDto(cart);
    }

    public List<CartResponseDTO> getAllCart() {
        return cartRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public CartResponseDTO getCurrentUserCart() {
        User user = authUtil.currentUser();
        Cart cart = cartRepository.findByUser_UserId(user.getUserId()).orElseGet(() -> {
            Cart c = new Cart();
            c.setUser(user);
            return cartRepository.save(c);
        });
        return toDto(cart);
    }

    @Transactional
    public CartResponseDTO updateCart(AddToCartRequest request) {
        User currentUser = authUtil.currentUser();

        Cart cart = cartRepository.findByUser_UserId(currentUser.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        CartItems item = cartItemRepository
                .findByCart_CartIdAndProduct_Id(cart.getCartId(), request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not in cart"));

        Product product = item.getProduct();
        if (product.getProductStock() == null || product.getProductStock() < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough stock");
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        recomputeTotal(cart);
        cartRepository.save(cart);

        return toDto(cart);
    }

    @Transactional
    public CartResponseDTO deleteItem(UUID productId) {
        User currentUser = authUtil.currentUser();

        Cart cart = cartRepository.findByUser_UserId(currentUser.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        CartItems item = cartItemRepository
                .findByCart_CartIdAndProduct_Id(cart.getCartId(), productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not in cart"));

        cart.getCartItems().remove(item);

        recomputeTotal(cart);
        cartRepository.save(cart);

        return toDto(cart);
    }

    @Transactional
    public CartResponseDTO clearCart() {
        User currentUser = authUtil.currentUser();

        Cart cart = cartRepository.findByUser_UserId(currentUser.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        cart.getCartItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        return toDto(cart);
    }

    @Transactional
    public void onProductPriceChanged(UUID productId, double newUnitPrice) {
        cartItemRepository.findByProduct_Id(productId).forEach(item -> {
            item.setProductPrice(newUnitPrice);
            cartItemRepository.save(item);
            Cart cart = item.getCart();
            recomputeTotal(cart);
            cartRepository.save(cart);
        });
    }

    @Transactional
    public void onProductStockChanged(UUID productId, int newStock) {
        cartItemRepository.findByProduct_Id(productId).forEach(item -> {
            Cart cart = item.getCart();
            if (newStock <= 0) {
                cart.getCartItems().remove(item);
            } else if (item.getQuantity() > newStock) {
                item.setQuantity(newStock);
                cartItemRepository.save(item);
            } else {
                return;
            }
            recomputeTotal(cart);
            cartRepository.save(cart);
        });
    }

    @Transactional
    public void onProductDeleted(UUID productId) {
        cartItemRepository.findByProduct_Id(productId).forEach(item -> {
            Cart cart = item.getCart();
            cart.getCartItems().remove(item);
            recomputeTotal(cart);
            cartRepository.save(cart);
        });
    }

    private void recomputeTotal(Cart cart) {
        double total = cart.getCartItems().stream()
                .mapToDouble(ci -> ci.getProductPrice() * ci.getQuantity() - ci.getDiscount())
                .sum();
        cart.setTotalPrice(total);
    }

    private CartResponseDTO toDto(Cart cart) {
        CartResponseDTO dto = modelMapper.map(cart, CartResponseDTO.class);
        List<CartItemDTO> items = cart.getCartItems().stream().map(ci -> {
            CartItemDTO itemDto = modelMapper.map(ci, CartItemDTO.class);
            itemDto.setLineTotal(ci.getProductPrice() * ci.getQuantity() - ci.getDiscount());
            return itemDto;
        }).toList();
        dto.setItems(items);
        return dto;
    }
}