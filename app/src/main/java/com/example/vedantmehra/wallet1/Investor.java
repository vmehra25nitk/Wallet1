package com.example.vedantmehra.wallet1;

public class Investor {

    String id, name;
    int value;

    public Investor(){

    }
    public Investor(String id, String name, int value){
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
