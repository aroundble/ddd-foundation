package com.myshop.inventory.domain.model.aggregates;

import com.myshop.catalog.command.domain.product.ProductId;
import com.myshop.common.jpa.MoneyConverter;
import com.myshop.common.model.Money;
import com.myshop.inventory.domain.model.events.StockAddedEvent;
import com.myshop.inventory.domain.model.events.StockRemovedEvent;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory extends AbstractAggregateRoot<Inventory> {
    @EmbeddedId
    private InventoryId id;

    @Column(name = "product_id")
    private ProductId productId;

    @Column(name = "quantity")
    private int quantity;

    @Convert(converter = MoneyConverter.class)
    @Column(name = "price")
    private Money price;

    protected Inventory() {
    }

    public Inventory(InventoryId id, ProductId productId, int quantity, Money price) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public InventoryId getId() {
        return id;
    }

    public ProductId getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    public void addStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.quantity += amount;
        registerEvent(new StockAddedEvent(id, amount));
    }

    public void removeStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Not enough stock to remove");
        }
        this.quantity -= amount;
        registerEvent(new StockRemovedEvent(id, amount));
    }
}
