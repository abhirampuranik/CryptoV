package com.example.cryptov;

public class coinStructure {

    private String name;
    private String value;
    private String change;

    public coinStructure(String name, String value, String change) {
        this.name = name;
        this.value = value;
        this.change = change;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
