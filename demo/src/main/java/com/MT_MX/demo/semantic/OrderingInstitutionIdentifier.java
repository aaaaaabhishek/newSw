package com.MT_MX.demo.semantic;

public class OrderingInstitutionIdentifier {
    private  String account;
    private   String Bic;
    private  String location;
    public OrderingInstitutionIdentifier(String account, String location, String bic) {
        this.account = account;
        this.location = location;
        Bic = bic;
    }

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


}
