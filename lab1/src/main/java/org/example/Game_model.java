package org.example;

import java.io.Serializable;
import java.util.List;

public class Game_model implements Serializable {
    public int status;

    public List<Character> firsPlayerTables;
    public List<Character> secondPlayerTables;

    public char CF_ship ='H';
    public char CF_water ='~';
    public char CF_dmg ='X';
    public char CF_miss ='O';


    public int getStatus() {
        return status;
    }

    public List<Character> getFirsPlayerTables() {
        return firsPlayerTables;
    }

    public List<Character> getSecondPlayerTables() {
        return secondPlayerTables;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setFirsPlayerTables(List<Character> firsPlayerTables) {
        this.firsPlayerTables = firsPlayerTables;
    }

    public void setSecondPlayerTables(List<Character> secondPlayerTables) {
        this.secondPlayerTables = secondPlayerTables;
    }

    public Game_model(int status, List<Character> firsPlayerTables, List<Character> secondPlayerTables) {
        this.status = status;
        this.firsPlayerTables = firsPlayerTables;
        this.secondPlayerTables = secondPlayerTables;
    }
}
