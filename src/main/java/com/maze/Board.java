package com.maze;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class Board {

    public static final String R = "RIGHT";
    public static final String L = "LEFT";
    public static final String D = "DOWN";
    public static final String U = "UP";
    Square[][] board;
    int size;
    List<Directional> directionals;
    Square start;
    String startDirection;
    int numOfRegions = 0;
    Square end;


    public Board(int size){
        this.size = size;
        board = new Square[size][size];
        directionals = new ArrayList<>();
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                board[i][j] = new Square();
                Square s = board[i][j];
                if (i == 0){
                    s.availbleDirections.remove(U);
                }
                if (i == size-1){
                    s.availbleDirections.remove(D);
                }
                if (j == 0){
                    s.availbleDirections.remove(L);
                }
                 if (j == size-1){
                     s.availbleDirections.remove(R);
                 }
            }
        }
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                Square s = board[i][j];
                if (i < size-1){
                    s.bottom = board[i+1][j];
                }
                 if (i>0){
                     s.top = board[i-1][j];
                 }
                if (j > 0){
                    s.left = board[i][j-1];
                }
                if (j < size-1){
                    s.right = board[i][j+1];
                }
            }
        }

        populateDirectionals();
    }

    public Maze getMaze(){
        Maze m = new Maze();
        m.setSize(size);
        int index = 0;
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++) {
                Square s = board[i][j];
                m.addToLayout(s.directional.id);
                if (s.value == 'S'){
                    m.setStart(index);
                } else if (s.value == 'E'){
                    m.setEnd(index);
                }
                index ++;
            }
        }
        return m;
    }

    public void populateFinishAmongOtherStuff(){
        start.visted = true;
        start.pacesWalked = 1;
        if (start.availbleDirections.contains(U) && start.top != null){
            traverse(start.top, start, U,1);
        }
        if (start.availbleDirections.contains(D) && start.bottom != null){
            traverse(start.bottom, start, D,1);
        }
        if (start.availbleDirections.contains(L) && start.left != null){
            traverse(start.left, start, L, 1);
        }
        if (start.availbleDirections.contains(R) && start.right != null){
            traverse(start.right, start, R, 1);
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (end == null) {
                    end = board[i][j];
                } else if (end.pacesWalked < board[i][j].pacesWalked) {
                    end = board[i][j];
                }
            }
        }
        end.value = 'E';
    }

    private void traverse(Square s, Square prev, String direction, int i) {
        if (s == null){
            return;
        }
        if (s.visted){
            createDeadEnd(s, calculateOpposite(direction));
           createDeadEnd(prev, direction);
            return;
        }
        i++;
        s.visted = true;
        s.pacesWalked = i;
        boolean touched = false;
        List<String> eventuallyIWillGetThisRight = new ArrayList<>();
        eventuallyIWillGetThisRight.addAll(s.directional.availableDirections);
        eventuallyIWillGetThisRight.remove(calculateOpposite(direction));
        if (eventuallyIWillGetThisRight.contains(U)){
            traverse(s.top, s, U,i);
            touched = true;
        }
        if (eventuallyIWillGetThisRight.contains(D)){
            traverse(s.bottom, s, D,i);
            touched = true;

        }
        if (eventuallyIWillGetThisRight.contains(L)){
            traverse(s.left, s, L, i);
            touched = true;

        }
        if (eventuallyIWillGetThisRight.contains(R)){
            traverse(s.right, s, R, i);
            touched = true;
        }
    }

    private void createDeadEnd(Square s, String direction) {
        List<String> mustHaves = new ArrayList<>();
        mustHaves.addAll(s.directional.availableDirections);
        mustHaves.remove(direction);
        List<Directional> options =directionals.stream().filter(d -> d.availableDirections.containsAll(mustHaves)).collect(Collectors.toList());
        List<String> mustntHaves = new ArrayList<>();
        mustntHaves.addAll(s.directional.unavailableDirections);
        mustntHaves.add(direction);
        options =options.stream().filter(d -> d.unavailableDirections.containsAll(mustntHaves)).collect(Collectors.toList());
        if(options.size()>1){
            System.out.println("BOOOOOO!");
        }else{
            if (s.value != 'S') {
                s.value = options.get(0).value;
            }
            s.directional = options.get(0);
        }
    }


    public void consolidate(){
        while( numOfRegions > 1) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Square s = board[i][j];
                    if (s.bottom != null && s.bottom.identifier != s.identifier) {
                        mergeSquares(s.bottom, U, s.identifier);
                        mergeSquares(s, D, s.identifier);
                    }
                    if (s.top != null && s.top.identifier != s.identifier) {
                        mergeSquares(s.top, D, s.identifier);
                        mergeSquares(s, U, s.identifier);
                    }
                    if (s.right != null && s.right.identifier != s.identifier) {
                        mergeSquares(s.right, L, s.identifier);
                        mergeSquares(s, R, s.identifier);
                    }
                    if (s.left != null && s.left.identifier != s.identifier) {
                        mergeSquares(s.left, R, s.identifier);
                        mergeSquares(s, L, s.identifier);
                    }
                }
            }
        }

    }

    public void addMissingPieces(){
        int id = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j].value == '.') {
                    id++;
                    numOfRegions++;
                    Square s = board[i][j];
                    String d =createSecondStartSquare(s, i, j, id);
                    if (d.equals(D)){
                            newPath(s.bottom, U, id);
                    }
                    if (d.equals(U)){
                            newPath(s.top, D, id);
                    }
                    if (d.equals(L)){
                            newPath(s.left, R, id);
                    }
                    if (d.equals(R)){
                            newPath(s.right, L, id);
                    }
                }
            }
        }
    }

    public void mergeSquares(Square s, String direction, int id){
        List<String> mustHaves = new ArrayList<>();
        mustHaves.addAll(s.directional.availableDirections);
        mustHaves.add(direction);
        List<Directional> options =directionals.stream().filter(d -> d.availableDirections.containsAll(mustHaves)).collect(Collectors.toList());
        List<String> mustntHaves = new ArrayList<>();
        mustntHaves.addAll(s.directional.unavailableDirections);
        mustntHaves.remove(direction);
        options =options.stream().filter(d -> d.unavailableDirections.containsAll(mustntHaves)).collect(Collectors.toList());
        if(options.size()>1){
            System.out.println("BOOOOOO!");
        }else{
            if (s.value != 'S') {
                s.value = options.get(0).value;
            }
            s.directional = options.get(0);
        }
        if(id != s.identifier) {
            numOfRegions--;
            updateAll(id, s.identifier);

        }
    }
    public void newPath(Square s, String direction, int id){
        if (s.value != '.'){
            mergeSquares(s, direction, id);
            return;
        }
        Directional d = findDirectional(s, direction, size, true);
        if (s.availbleDirections.size() == 1 && s.availbleDirections.get(0).equals(direction)){
            s.value = d.value;
            s.directional = d;
            return;
        }
        s.availbleDirections.removeAll(d.unavailableDirections);
        s.availbleDirections.remove(direction);
        updateSurroundingSquares(s, d);
        s.value = d.value;
        s.directional = d;
        s.identifier = id;
        if (s.availbleDirections.contains(D)){
            newPath(s.bottom, U, id);
        }
        if (s.availbleDirections.contains(U)){
            newPath(s.top, D, id);
        }
        if (s.availbleDirections.contains(L)){
            newPath(s.left, R, id);
        }
        if (s.availbleDirections.contains(R)){
            newPath(s.right, L,id);
        }


    }

    private void updateAll(int existingId, int newId) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j].identifier == existingId) {
                    board[i][j].identifier = newId;
                }
            }
        }
    }

    public String createSecondStartSquare(Square s, int i, int j, int id){
      List<Directional> options = directionals.stream().filter(d->d.priority==1).collect(Collectors.toList());
      if (i == 0){
          options=options.stream().filter(d-> !d.availableDirections.contains(U)).collect(Collectors.toList());
      }
        if (i == size-1){
            options=options.stream().filter(d-> !d.availableDirections.contains(D)).collect(Collectors.toList());
        }
        if (j == 0){
            options=options.stream().filter(d-> !d.availableDirections.contains(L)).collect(Collectors.toList());
        }
        if (j == size-1){
            options=options.stream().filter(d-> !d.availableDirections.contains(R)).collect(Collectors.toList());
        }
        Random r = new Random();
        int choice = r.nextInt(options.size());
        Directional d = options.get(choice);
        updateSurroundingSquares(s,d);
        s.availbleDirections.removeAll(d.unavailableDirections);
        s.value = d.value;
        s.directional = d;
        s.identifier = id;
        return d.availableDirections.get(0);

    }

    public void populateMaze(){
        numOfRegions++;
        chooseDirectional(start, startDirection, true, true, 0);
    }


    public Directional findDirectional(Square s, String direction, int length, Boolean branch){
        List<Directional> options = new ArrayList<>();
        List<String> unavailable = calculateUnavailable(s.availbleDirections);
        options.addAll(directionals.stream().filter(d -> d.unavailableDirections.containsAll(unavailable)).collect(Collectors.toList()));
        s.mustHaveDirections.add(direction);
        options =options.stream().filter(d -> d.availableDirections.containsAll(s.mustHaveDirections)).collect(Collectors.toList());
        Random r = new Random();
        if (length < size/2) {
            Map<Integer, List<Directional>> m =  options.stream().collect(groupingBy(d1-> ((Integer)d1.priority)));
            if (m.get(2) != null && m.get(2).size() > 0) {
                if (branch && m.get(3) != null && m.get(3).size() > 0) {
                    Boolean b = r.nextBoolean();
                    if (b) {
                        options = m.get(3);
                    } else {
                        options = m.get(2);
                    }
                } else {
                    options = m.get(2);
                }
            } else if (m.get(3) != null && m.get(3).size() > 0) {
                options = m.get(3);
            } else {
                options = m.get(1);
            }
        }

        if (options == null){
            System.out.println("here");
        }
        int choice = r.nextInt(options.size());
        return options.get(choice);
    }

    public void updateSurroundingSquares(Square s, Directional d){
        if (d.unavailableDirections.contains(U) && s.top != null){
            s.top.availbleDirections.remove(D);
        }
        if (d.unavailableDirections.contains(D) && s.bottom != null){
            s.bottom.availbleDirections.remove(U);
        }
        if (d.unavailableDirections.contains(R) && s.right != null){
            s.right.availbleDirections.remove(L);
        }
        if (d.unavailableDirections.contains(L) && s.left != null){
            s.left.availbleDirections.remove(R);
        }
        if (d.availableDirections.contains(D)){
            s.bottom.mustHaveDirections.add(U);
        }
        if (d.availableDirections.contains(U)){
            s.top.mustHaveDirections.add(D);
        }
        if (d.availableDirections.contains(L)){
            s.left.mustHaveDirections.add(R);
        }
        if (d.availableDirections.contains(R)){
            s.right.mustHaveDirections.add(L);
        }

    }

    public void chooseDirectional(Square s, String direction, boolean branch, boolean start, int length){
        if (s.value != '.'){
           if (!start) {
               return;
           }
        }
        Directional d = findDirectional(s, direction, length, branch);
        int l  = length+1;
        if(!start) {
            s.value = d.value;
        }
        s.directional = d;
        s.identifier = 0;
        if (s.availbleDirections.size() == 1 && s.availbleDirections.get(0).equals(direction)){
            return;
        }
        s.availbleDirections.removeAll(d.unavailableDirections);
        s.availbleDirections.remove(direction);
       updateSurroundingSquares(s, d);
       Boolean branch2 = true;
       if (d.priority==3){
           branch2 = false;
       }
        if (s.availbleDirections.contains(D)){
            if (s.bottom.availbleDirections.contains(U)) {
                chooseDirectional(s.bottom, U, branch2, false, l);
            }
        }
        if (s.availbleDirections.contains(U)){
            if (s.top.availbleDirections.contains(D)) {
                chooseDirectional(s.top, D, branch2, false, l);
            }
        }
        if (s.availbleDirections.contains(L)){
            if (s.left.availbleDirections.contains(R)) {
                chooseDirectional(s.left, R, branch2, false, l);
            }
        }
        if (s.availbleDirections.contains(R)){
            if (s.right.availbleDirections.contains(L)) {
                chooseDirectional(s.right, L, branch2, false, l);
            }
        }
    }

    private List<String> calculateUnavailable(List<String> availbleDirections) {
        List<String> un= new ArrayList<>();
        un.add(U);
        un.add(D);
        un.add(R);
        un.add(L);
        un.removeAll(availbleDirections);
        return un;
    }

    public void setStart(){
        Random r = new Random();
        //pick which of the four side has the start 0=top, 1=bottom, 2=left, 3=right
        int side = r.nextInt(4);
        int location = r.nextInt(size);
        switch (side) {
            case 0:
                startDirection = D;
                start = board[0][location];
                board[0][location].value ='S';
                break;
            case 1:
                startDirection = U;
                start = board[size-1][location];
                board[size-1][location].value ='S';
                break;
            case 2:
                startDirection = R;
                start =board[location][0];
                board[location][0].value ='S';
                break;
            case 3:
                startDirection = L;
                start = board[location][size-1];
                board[location][size-1].value ='S';
                break;
        }

    }

    public void printMaze(){
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                System.out.print(board[i][j].value);
            }
            System.out.println();
        }
//        for (int i = 0; i < size; i++){
//            for (int j = 0; j < size; j++){
//                System.out.print(board[i][j].visted);
//            }
//            System.out.println();
//        }
//        for (int i = 0; i < size; i++){
//            for (int j = 0; j < size; j++){
//                System.out.print(board[i][j].pacesWalked+"  ");
//            }
//            System.out.println();
//        }
    }

    private void populateDirectionals() {
        Directional d1 = new Directional();
        d1.value = '\u256C';
        d1.availableDirections.add(U);
        d1.availableDirections.add(D);
        d1.availableDirections.add(L);
        d1.availableDirections.add(R);
        d1.priority = 3;
        d1.id =1;
        directionals.add(d1);
        Directional d2 = new Directional();
        d2.value = '\u2560';
        d2.availableDirections.add(U);
        d2.availableDirections.add(D);
        d2.availableDirections.add(R);
        d2.unavailableDirections.add(L);
        d2.priority = 3;
        d2.id =2;
        directionals.add(d2);
        Directional d3 = new Directional();
        d3.value = '\u255A';
        d3.availableDirections.add(U);
        d3.availableDirections.add(R);
        d3.unavailableDirections.add(L);
        d3.unavailableDirections.add(D);
        d3.priority = 2;
        d3.id = 3;
        directionals.add(d3);
        Directional d4 = new Directional();
        d4.value = '\u2554';
        d4.availableDirections.add(D);
        d4.availableDirections.add(R);
        d4.unavailableDirections.add(L);
        d4.unavailableDirections.add(U);
        d4.priority = 2;
        d4.id =4;
        directionals.add(d4);
        Directional d5 = new Directional();
        d5.value = '\u2551';
        d5.availableDirections.add(U);
        d5.availableDirections.add(D);
        d5.unavailableDirections.add(L);
        d5.unavailableDirections.add(R);
        d5.priority = 2;
        d5.id =5;
        directionals.add(d5);
        Directional d6 = new Directional();
        d6.value = '\u2565';
        d6.availableDirections.add(D);
        d6.unavailableDirections.add(L);
        d6.unavailableDirections.add(R);
        d6.unavailableDirections.add(U);
        d6.priority = 1;
        d6.id =6;
        directionals.add(d6);
        Directional d7 = new Directional();
        d7.value = '\u2568';
        d7.availableDirections.add(U);
        d7.unavailableDirections.add(D);
        d7.unavailableDirections.add(R);
        d7.unavailableDirections.add(L);
        d7.priority = 1;
        d7.id = 7;
        directionals.add(d7);
        Directional d8 = new Directional();
        d8.value = '\u2563';
        d8.availableDirections.add(U);
        d8.availableDirections.add(D);
        d8.availableDirections.add(L);
        d8.unavailableDirections.add(R);
        d8.priority = 3;
        d8.id = 8;
        directionals.add(d8);
        Directional d9 = new Directional();
        d9.value = '\u255D';
        d9.availableDirections.add(U);
        d9.availableDirections.add(L);
        d9.unavailableDirections.add(D);
        d9.unavailableDirections.add(R);
        directionals.add(d9);
        d9.priority = 2;
        d9.id =9;
        Directional d10 = new Directional();
        d10.value = '\u2557';
        d10.availableDirections.add(D);
        d10.availableDirections.add(L);
        d10.unavailableDirections.add(U);
        d10.unavailableDirections.add(R);
        d10.priority = 2;
        d10.id =10;
        directionals.add(d10);
        Directional d11 = new Directional();
        d11.value = '\u2566';
        d11.availableDirections.add(D);
        d11.availableDirections.add(L);
        d11.availableDirections.add(R);
        d11.unavailableDirections.add(U);
        d11.priority = 3;
        d11.id = 11;
        directionals.add(d11);
        Directional d12 = new Directional();
        d12.value = '\u2550';
        d12.availableDirections.add(L);
        d12.availableDirections.add(R);
        d12.unavailableDirections.add(U);
        d12.unavailableDirections.add(D);
        d12.priority = 2;
        d12.id = 12;
        directionals.add(d12);
        Directional d13 = new Directional();
        d13.value = '\u2561';
        d13.availableDirections.add(L);
        d13.unavailableDirections.add(R);
        d13.unavailableDirections.add(U);
        d13.unavailableDirections.add(D);
        d13.priority = 1;
        d13.id =13;
        directionals.add(d13);
        Directional d14 = new Directional();
        d14.value = '\u255E';
        d14.availableDirections.add(R);
        d14.unavailableDirections.add(L);
        d14.unavailableDirections.add(U);
        d14.unavailableDirections.add(D);
        d14.priority =1;
        d14.id = 14;
        directionals.add(d14);
        Directional d15 = new Directional();
        d15.value = '\u2569';
        d15.availableDirections.add(R);
        d15.availableDirections.add(L);
        d15.availableDirections.add(U);
        d15.unavailableDirections.add(D);
        d15.priority =3;
        d15.id =15;
        directionals.add(d15);

    }

    public String calculateOpposite(String direction){
        String opp ="";
        if (direction.equals(L)){
            opp = R;
        }
        else if (direction.equals(R)){
            opp = L;
        }
        else if (direction.equals(U)){
            opp = D;
        }
        else if (direction.equals(D)){
            opp = U;
        }
        return opp;
    }

}
