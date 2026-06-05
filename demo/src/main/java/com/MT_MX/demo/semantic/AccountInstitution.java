package com.MT_MX.demo.semantic;

import java.util.List;

public class AccountInstitution {
    private String account;
    private String name;
    private List<String> address;

    public AccountInstitution(String account, String name, List<String> address) {
        this.account = account;
        this.name = name;
        this.address = address;
    }

    public String getAccount() { return account; }
    public String getName() { return name; }
    public List<String> getAddress() { return address; }
}
