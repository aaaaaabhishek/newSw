package com.MT_MX.demo.semantic;

import java.util.List;

public class OrderingInstitution {

    private String account;
    private String name;
    private List<String> address;

    public OrderingInstitution() {
    }

    public OrderingInstitution(String account, String name, List<String> address) {
        this.account = account;
        this.name = name;
        this.address = address;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }
}