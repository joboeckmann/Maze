package com.maze;

public class Main {


    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);
        Board board = new Board(size);
        board.setStart();
        board.populateMaze();
        //System.out.println("populated");
        //System.out.println(board.numOfRegions);
        //board.printMaze();
        board.addMissingPieces();
        //System.out.println("filled");
        //System.out.println(board.numOfRegions);
        //board.printMaze();
        board.consolidate();
        //System.out.println("consol");
        //System.out.println(board.numOfRegions);
        board.populateFinishAmongOtherStuff();
        board.printMaze();


    }


}
