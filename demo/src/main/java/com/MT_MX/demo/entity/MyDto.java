package com.MT_MX.demo.entity;

public class MyDto {
    private String name;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyDto(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    private int amount;

    // constructor, getters, setters
}