package minesveiper;

import java.io.Serializable;

/**
 * Created by Elise Haram Vannes on 09.04.2018.
 */
public class Brick implements Serializable{

    private boolean bomb = false;
    private boolean alive = true;
    private int neighbors = 0;
    private boolean flagMarked = false;

    public boolean isBomb() {
        return bomb;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(int neighbors) {
        this.neighbors = neighbors;
    }

    public boolean isFlagMarked() {
        return flagMarked;
    }

    public void setFlagMarked(boolean flagMarked) {
        this.flagMarked = flagMarked;
    }
}
