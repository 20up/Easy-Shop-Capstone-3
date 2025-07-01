package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);

    void addToCart(int userId, int productId);

    void updateCartItem(int userId, int productId, int quantity);

    void clearCart(int userId);
}
