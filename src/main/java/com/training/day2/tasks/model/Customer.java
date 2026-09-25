package com.training.day2.tasks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer extends User{
    private String address;

    @Override
    public String toString() {
        return "Customer{" +
                "address='" + address + '\'' +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public Customer(String name, int age, String address) {
        super(name, age);
        this.address = address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Book> getWishList() {
        return wishList;
    }

    public void setWishList(List<Book> wishList) {
        this.wishList = wishList;
    }

    @ManyToMany
    List<Book> wishList = new ArrayList<>();
}
