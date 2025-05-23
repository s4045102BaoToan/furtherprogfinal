package com.example.model;


import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "orders") // Avoids conflict with SQL keyword
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double total;
    private LocalDate dateOfCreation;

    @ManyToOne
    @JoinColumn(name = "customer_id" )
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id") // creates a foreign key in the Item table
    private List<Item> items; 

    
    @ManyToOne
    @JoinColumn(name = "deliveryman_id")
    private DeliveryMan deliveryMan;

    
    public Order() {
    }
    
    public Order(Integer id, Double total, LocalDate dateOfCreation, Customer customer, List<Item> items,
            DeliveryMan deliveryMan) {
        this.id = id;
        this.total = total;
        this.dateOfCreation = dateOfCreation;
        this.customer = customer;
        this.items = items;
        this.deliveryMan = deliveryMan;
    }
    

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
    public LocalDate getDateOfCreation() {
        return dateOfCreation;
    }
    public void setDateOfCreation(LocalDate dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }
    public DeliveryMan getDeliveryMan() {
        return deliveryMan;
    }
    public void setDeliveryMan(DeliveryMan deliveryMan) {
        this.deliveryMan = deliveryMan;
    }
    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
}
