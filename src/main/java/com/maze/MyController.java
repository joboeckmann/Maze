package com.maze;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MyController {

//    @Autowired
//    private ICityService cityService;


    @RequestMapping(value = "/maze/{size}", method = RequestMethod.GET, produces = { "application/json" }, consumes = MediaType.ALL_VALUE )
    public Maze getMaze(@PathVariable("size") int size) {
        Board board = new Board(size);
        board.setStart();
        board.populateMaze();
        board.addMissingPieces();
        board.consolidate();
        board.populateFinishAmongOtherStuff();
        return board.getMaze();
    }
}
