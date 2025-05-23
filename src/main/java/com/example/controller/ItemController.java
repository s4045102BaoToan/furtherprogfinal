package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.DBUtils.JPAUtil;
import com.example.model.*;
import jakarta.persistence.*;

public class ItemController {
    private EntityManagerFactory emf;

    public ItemController(){
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    public void validateItem(Item item){
        if(item.getName() == "" || item.getName().isBlank()){
            throw new IllegalArgumentException("Item name cant be blank");
        }
        if(item.getPrice() == null || item.getPrice() < 0){
            throw new IllegalArgumentException("Item price cant be blank/negative");
        }
    }

    public List<Item> getAllItems(){
        EntityManager em = emf.createEntityManager();
        List<Item> foundItems = new ArrayList<>(em.createQuery("SELECT item FROM Item item", Item.class).getResultList());
        em.close();
        return foundItems;
    }

    public void addItem(Item item){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            validateItem(item);   
            em.persist(item);
            em.getTransaction().commit();
        } catch (Exception e) {
           em.getTransaction().rollback();
           throw e;
        } finally {
            em.close();
        }
    }

    public void updateItem(Item item){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Item foundItem = em.find(Item.class, item.getId());
            foundItem.setName(item.getName());
            foundItem.setPrice(item.getPrice());
            validateItem(foundItem);
            em.getTransaction().commit();
        } catch (Exception e) {
           em.getTransaction().rollback();
           throw e;
        } finally {
            em.close();
        }
    }

    public void deleteItem(Integer id){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Item foundItem = em.find(Item.class, id);
            em.remove(foundItem);
            em.getTransaction().commit();
        } catch (Exception e) {
           em.getTransaction().rollback();
           throw e;
        } finally {
            em.close();
        }
    }
}
