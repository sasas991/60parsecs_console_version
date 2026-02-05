package com.parsecs;

public class mainitems {
    private String name;
    private itemcategory category;

    public mainitems(String name, itemcategory category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public itemcategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
}