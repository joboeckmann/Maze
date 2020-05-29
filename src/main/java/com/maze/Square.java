
package com.maze;
import java.util.ArrayList;
import java.util.List;

public class Square {
    char value = '.';
    List<String> availbleDirections;
    List<String> mustHaveDirections;
    Square top;
    Square bottom;
    Square left;
    Square right;
    Directional directional;
    int identifier;
    boolean visted;
    int pacesWalked;

    public Square(){
    availbleDirections = new ArrayList<String>();
    mustHaveDirections = new ArrayList<>();
    availbleDirections.add("UP");
    availbleDirections.add( "DOWN");
    availbleDirections.add( "LEFT");
    availbleDirections.add("RIGHT");
    }




}
