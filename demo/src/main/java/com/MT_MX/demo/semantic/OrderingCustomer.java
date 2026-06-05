package com.MT_MX.demo.semantic;

import java.util.List;

public class OrderingCustomer {

    private final String account;
    private final String name;
    private final List<String> address;

    public OrderingCustomer(String account, String name, List<String> address) {
        this.account = account;
        this.name = name;
        this.address = address;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public List<String> getAddress() {
        return address;
    }
}