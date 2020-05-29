package com.maze;

import java.util.ArrayList;
import java.util.List;

public class Directional {
    char value;
    List<String> availableDirections;
    List<String> unavailableDirections;
    Integer priority;
    int id;

    public Directional(){
        availableDirections = new ArrayList<>();
        unavailableDirections = new ArrayList<>();
    }

}
