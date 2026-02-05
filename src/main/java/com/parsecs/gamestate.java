package com.parsecs;
import java.util.ArrayList;
import java.util.List;

public class gamestate
{
    public int oxygen=100;
    public int food=100;
    public int ship=100;
    public int day=1;
    public List<String> crew=new ArrayList<>();
    public List<mainitems> items=new ArrayList<>();
    public boolean gameover=false;
    public userauth currentUser;

    public boolean hasItem(String itemName) {
        return items.stream().anyMatch(i -> i.getName().equals(itemName));
    }

    public void removeItem(String itemName) {
        items.removeIf(i -> i.getName().equals(itemName));
    }
}