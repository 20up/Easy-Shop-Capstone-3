package org.yearup.models;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart
{
    private int userId;
    private Map<Integer, ShoppingCartItem> items = new HashMap<>();

    public ShoppingCart()
    {
        // no-arg constructor
    }

    public ShoppingCart(int userId, List<ShoppingCartItem> itemList)
    {
        this.userId = userId;
        this.items = new HashMap<>();
        for (ShoppingCartItem item : itemList)
        {
            this.items.put(item.getProductId(), item);
        }
    }

    public int getUserId()
    {
        return userId;
    }

    public Map<Integer, ShoppingCartItem> getItems()
    {
        return items;
    }

    public void setItems(Map<Integer, ShoppingCartItem> items)
    {
        this.items = items;
    }

    public boolean contains(int productId)
    {
        return items.containsKey(productId);
    }

    public void add(ShoppingCartItem item)
    {
        items.put(item.getProductId(), item);
    }

    public ShoppingCartItem get(int productId)
    {
        return items.get(productId);
    }

    public BigDecimal getTotal()
    {
        BigDecimal total = items.values()
                .stream()
                .map(i -> i.getLineTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total;
    }
}
