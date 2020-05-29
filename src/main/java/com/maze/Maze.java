package com.maze;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

public class Maze {

    private int size;
    private int start;
    private int end;
    private List<Integer> layout;

    public Maze(){
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<Integer> getLayout() {
        return layout;
    }

    public void setLayout(List<Integer> layout) {
        this.layout = layout;
    }

    public void addToLayout(int i){
        if (layout == null){
            layout = new ArrayList<>();
        }
        layout.add(i);
    }
}
