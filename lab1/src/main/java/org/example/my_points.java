package org.example;

import java.io.*;

public class my_points implements Serializable {
    public int x,y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public my_points(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
