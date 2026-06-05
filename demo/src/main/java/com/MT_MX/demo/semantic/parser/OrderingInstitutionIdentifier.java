package com.MT_MX.demo.semantic.parser;

public class OrderingInstitutionIdentifier {
    public String getBic() {
        return Bic;
    }

    public void setBic(String bic) {
        Bic = bic;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public OrderingInstitutionIdentifier(String account, String location, String bic) {
        this.account = account;
        this.location = location;
        Bic = bic;
    }

    String account;
    String Bic;
    String location;
}
