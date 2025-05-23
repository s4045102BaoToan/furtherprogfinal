package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.DBUtils.JPAUtil;
import com.example.model.*;

import jakarta.persistence.*;

public class OrderController {
    private EntityManagerFactory emf;

    public OrderController(){
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    public void validateOrder(Order order){
        if(order.getTotal() == null){
            throw new IllegalArgumentException("Total of order is not calculated");
        }
        if(order.getDateOfCreation() == null){
            throw new IllegalArgumentException("No date created");
        }
        if(order.getItems().size() <= 0){
            throw new IllegalArgumentException("No items has been added to order");
        }
        if(order.getDeliveryMan() == null){
            throw new IllegalArgumentException("No delivery man was assigned.");
        }
        if(order.getCustomer() == null){
            throw new IllegalArgumentException("No customer was assigned.");
        }
    }

    public List<Order> getAllOrders(){
        EntityManager em = emf.createEntityManager();
        List<Order> foundOrders = new ArrayList<>();
        try{
            em.getTransaction().begin();
            foundOrders = em.createQuery("SELECT order FROM Order order", Order.class).getResultList();
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
        return foundOrders;
    }

    public void addOrder(Order order){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateOrder(Order order){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Order foundOrder = em.find(Order.class, order.getId());
            foundOrder.setDateOfCreation(order.getDateOfCreation());
            foundOrder.setCustomer(order.getCustomer());
            foundOrder.setDeliveryMan(order.getDeliveryMan());
            foundOrder.setItems(order.getItems());
            foundOrder.setTotal(order.getTotal());
            validateOrder(foundOrder);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteOrder(Integer id){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Order foundOrder = em.find(Order.class, id);
            em.remove(foundOrder);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void addItemsToOrder(Order order, List<Item> items){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            List<Item> foundItems = items.stream()
                .map(item -> em.find(Item.class, item.getId()))
                .collect(Collectors.toList());
            Order foundOrder = em.find(Order.class, order.getId());
            foundOrder.setItems(foundItems);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
