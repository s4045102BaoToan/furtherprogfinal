package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.DBUtils.JPAUtil;
import com.example.model.*;
import jakarta.persistence.*;

public class DeliveryManController {
    private EntityManagerFactory emf;

    public DeliveryManController(){
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    public void validateDeliveryMan(DeliveryMan deliveryMan){
        if(deliveryMan.getName() == "" || deliveryMan.getName().isBlank()){
            throw new IllegalArgumentException("Delivery guy name cant be blank");
        }
        if(deliveryMan.getPhoneNumber() == null){
            throw new IllegalArgumentException("Delivery guy phone number cant be blank");
        }
    }

    public List<DeliveryMan> getAllDeliveryMan(){
        EntityManager em = emf.createEntityManager();
        List<DeliveryMan> foundPeople = new ArrayList<>(em.createQuery("SELECT people FROM DeliveryMan people", DeliveryMan.class).getResultList());
        em.close();
        return foundPeople;
    }

    public void addDeliveryMan(DeliveryMan deliveryMan){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            validateDeliveryMan(deliveryMan);
            em.persist(deliveryMan);
            em.getTransaction().commit();
        } catch (Exception e) {
           em.getTransaction().rollback();
           throw e;
        } finally {
            em.close();
        }
    }

    public void updateDeliveryMan(DeliveryMan deliveryMan){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            DeliveryMan foundDeliveryMan = em.find(DeliveryMan.class, deliveryMan.getId());
            foundDeliveryMan.setName(deliveryMan.getName());
            foundDeliveryMan.setPhoneNumber(deliveryMan.getPhoneNumber());
            validateDeliveryMan(foundDeliveryMan);
            em.getTransaction().commit();
        } catch (Exception e) {
           em.getTransaction().rollback();
           throw e;
        } finally {
            em.close();
        }
    }

    public void deleteDeliveryMan(Integer id){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            DeliveryMan foundDeliveryMan = em.find(DeliveryMan.class, id);
            em.remove(foundDeliveryMan);
            em.getTransaction().commit();
        } catch (Exception e) {
           em.getTransaction().rollback();
           throw e;
        } finally {
            em.close();
        }
    }
}
