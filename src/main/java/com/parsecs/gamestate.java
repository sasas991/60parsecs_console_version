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
    public List<String> items=new ArrayList<>();
    public boolean gameover=false;
}