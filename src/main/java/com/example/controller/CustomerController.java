package com.example.controller;

import java.util.*;

import com.example.DBUtils.JPAUtil;
import com.example.model.*;

import jakarta.persistence.*;

public class CustomerController {
    private EntityManagerFactory emf;

    public CustomerController(){
        this.emf = JPAUtil.getEntityManagerFactory();
    }

    public void validateCustomer(Customer customer){
        if(customer.getName() == "" || customer.getName().isBlank()){
            throw new IllegalArgumentException("Customer name cant be empty");
        }
        // if(customer.getAddress() == "" || customer.getAddress().isBlank()){
        //     throw new IllegalArgumentException("Customer name cant be empty");
        // }
        // if(customer.getPhoneNumber() == "" || customer.getName().isBlank()){
        //     throw new IllegalArgumentException("Customer name cant be empty");
        // }
    }

    public List<Customer> getAllCustomers(){
        EntityManager em = emf.createEntityManager();
        List<Customer> foundCustomers = new ArrayList<>(em.createQuery("SELECT customer FROM Customer customer", Customer.class).getResultList());
        em.close();
        return foundCustomers;
    }
    
    //ADD
    public void addCustomer(Customer customer){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            validateCustomer(customer);
            em.persist(customer);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    //UPDATE
    public void updateCustomer(Customer customer){
        EntityManager em = emf.createEntityManager();
        try{
            Customer foundCustomer = em.find(Customer.class, customer.getId());
            em.getTransaction().begin();
            foundCustomer.setAddress(customer.getAddress());
            foundCustomer.setName(customer.getName());
            foundCustomer.setAddress(customer.getAddress());
            foundCustomer.setPhoneNumber(customer.getPhoneNumber());
            validateCustomer(foundCustomer);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    //DELTE
    public void deleteCustomer(Integer id){
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Customer foundCustomer = em.find(Customer.class, id);
            em.remove(foundCustomer);
            em.getTransaction().commit();
        } catch (Exception e){
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
