package minesveiper;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.Serializable;

/**
 * Created by Elise Haram Vannes on 06.04.2018.
 */
public class Game implements Serializable {

    private Image blockImage;
    private Image bombImage;
    private Image smiley;
    private Image sourSmiley;
    private Image shockedSmiley;
    private Image flagImage;
    private int rows = 17;
    private int columns = 30;
    private Brick[][] board;
    private boolean died = false;
    private int bombs = 99;
    private boolean isPaused = false;
    private int[][] borderBoard;    // 0 = ingenting, 1 = skal fjernes, 2 = har blitt undersøkt
    private boolean beginner = false; // 8x8
    private boolean intermediate = false; // 16x16
    private boolean expert = true; // 17x30
    private int blockSize = 20;
    private Label howManyBombs;
    private GraphicsContext gc;
    private boolean won = false;
    private boolean hasStarted = false;

    public Game(GraphicsContext gc, Label howManyBombs){
        this.blockImage = FileHandling.initImage("C:\\Users\\Bruker\\IdeaProjects\\Minesveiper\\src\\minesveiper\\img\\block2.png");
        this.bombImage = FileHandling.initImage("C:\\Users\\Bruker\\IdeaProjects\\Minesveiper\\src\\minesveiper\\img\\bomb2.png");
        this.smiley = FileHandling.initImage("C:\\Users\\Bruker\\IdeaProjects\\Minesveiper\\src\\minesveiper\\img\\smileyButton.png");
        this.sourSmiley = FileHandling.initImage("C:\\Users\\Bruker\\IdeaProjects\\Minesveiper\\src\\minesveiper\\img\\sourButton.png");
        this.shockedSmiley = FileHandling.initImage("C:\\Users\\Bruker\\IdeaProjects\\Minesveiper\\src\\minesveiper\\img\\shockedButton.png");
        this.flagImage = FileHandling.initImage("C:\\Users\\Bruker\\IdeaProjects\\Minesveiper\\src\\minesveiper\\img\\flag.png");
        this.howManyBombs = howManyBombs;
        this.gc = gc;

        initBoard();
    }

    /**
     * When a mouseclick is registered on the canvas, this method occurs.
     * It will remove the brick from the board, and check if it contained
     * a bomb or not. If a bomb is present, the game is over.
     * @param e mouseclick-event on the canvas
     */
    void mousePressed(MouseEvent e){
        int x = (int)e.getY()/blockSize;
        int y = (int)e.getX()/blockSize;
        if(board[x][y].isAlive() && !board[x][y].isFlagMarked()) {
            board[x][y].setAlive(false);
            if (board[x][y].isBomb()) {
                if(!hasStarted){
                    moveBomb(x,y);
                    resetNeighbors();
                    countNeighbors();
                    hasStarted = true;
                    //
                    if(board[x][y].getNeighbors() == 0) {
                        borderBoard = new int[board.length][board[0].length];
                        removeEmptyNeighbors(x, y);
                        showBorder();
                    }
                } else {
                    died = true;
                }
            } else if(board[x][y].getNeighbors() == 0){
                borderBoard = new int[board.length][board[0].length];
                removeEmptyNeighbors(x, y);
                showBorder();
            }
        }
        if(!hasStarted) hasStarted = true;
    }

    /**
     * Marks a brick with a flag, or removes the flag if present.
     * Also counts how many bombs have been flagged, and updates the view.
     * @param e mouseclick-event on the canvas
     */
    void mouseRightClicked(MouseEvent e){
        int x = (int)e.getY()/blockSize;
        int y = (int)e.getX()/blockSize;
        if(board[x][y].isAlive()){
            if(board[x][y].isFlagMarked()){
                bombs++;
            } else {
                bombs--;
            }
            board[x][y].setFlagMarked(!board[x][y].isFlagMarked());
            howManyBombs.setText(String.valueOf(bombs));
        }
    }

    /**
     * A recursive method that removes neighboring bricks of the
     * input brick that have no neighboring bombs.
     * @param x x-coordinate of the original brick
     * @param y y-coordinate of the original brick
     * @param i will add to the x-coordinate of the neighbor
     * @param j will add to the y-coordinate of the neighbor
     */
    private void removeOne(int x, int y, int i, int j){
        if(board[x+i][y+j].isAlive()){
            if(!board[x+i][y+j].isFlagMarked()) {
                board[x + i][y + j].setAlive(false);
                removeEmptyNeighbors(x + i, y + j);
            }
        }
    }

    /**
     * Removes the neighbors that have no neighboring bombs
     * after one brick has been removed.
     * @param x x-coordinate of the original brick
     * @param y y-coordinate of the original brick
     */
    private void removeEmptyNeighbors(int x, int y){
        borderBoard[x][y] = 2;
        for(int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {

                if(isInBounds(x+i,y+j)) {
                    if(!board[x+i][y+j].isBomb() && board[x+i][y+j].getNeighbors() == 0){

                        removeOne(x+i,y+j,0,0);
                    }
                }
            }
        }
    }

    /**
     * If the first brick that gets clicked contains a bomb,
     * the bomb will be moved somewhere else so that the game
     * can continue.
     * @param i the x coordinate of the original bomb
     * @param j the y coordinate of the original bomb
     */
    private void moveBomb(int i, int j){
        board[i][j].setBomb(false);
        boolean hasBeenPlaced = false;

        while(!hasBeenPlaced){
            int x = (int)Math.floor(Math.random()*rows);
            int y = (int)Math.floor(Math.random()*columns);
            if(!board[x][y].isBomb()){
                board[x][y].setBomb(true);
                hasBeenPlaced = true;
            }
        }
    }

    /**
     * Initiates a new game.
     */
    void newGame(){
        initBoard();
        died = false;
        hasStarted = false;
        howManyBombs.setText(String.valueOf(bombs));
    }

    /**
     * Initiates the game board. Decides number of bombs, and size of array
     * depending on the size of the canvas.
     * Bombs are placed, and the neighboring bombs are counted.
     */
    private void initBoard(){
        if(gc.getCanvas().getHeight() < 170){
            bombs = 10;
            rows = 8;
            columns = 8;
        } else if(gc.getCanvas().getWidth() < 400){
            bombs = 40;
            rows = 16;
            columns = 16;
        }
        this.howManyBombs.setText(String.valueOf(bombs));

        board = new Brick[rows][columns];
        // create bricks
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                board[i][j] = new Brick();
            }
        }
        // place bombs
        for(int i = 0; i < bombs; i++){
            int x = (int)Math.floor(Math.random()*rows);
            int y = (int)Math.floor(Math.random()*columns);
            if(!board[x][y].isBomb()) board[x][y].setBomb(true);
            else i--;
        }
        countNeighbors();
    }

    /**
     * Calls method to count neighboring bombs for each brick.
     */
    private void countNeighbors(){
        // count neighbors
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                Brick brick = board[i][j];
                countNeighbors(i,j, brick);
            }
        }
    }

    /**
     * Resets the count of neighbors to 0.
     */
    private void resetNeighbors(){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                Brick brick = board[i][j];
                brick.setNeighbors(0);
            }
        }
    }

    /**
     * Counts the neighboring boms for a brick.
     * @param x x-coordinate of the brick
     * @param y y-coordinate of the brick
     * @param brick the Brick object
     */
    private void countNeighbors(int x, int y, Brick brick){
        for(int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if(i == 0 && j == 0) continue;

                if(isInBounds(x+i,y+j)) {
                    if (board[x + i][y + j].isBomb()) brick.setNeighbors(brick.getNeighbors() + 1);
                }
            }
        }
    }

    /**
     * Checks if the x and y coordinates are within bounds of an array.
     * @param x x-coordinate in array
     * @param y y-coordinate in array
     * @return true if it is in bounds
     */
    private boolean isInBounds(int x, int y){
        if(x < 0 || x > rows-1 || y < 0 || y > columns-1) return false;
        return true;
    }

    /**
     * After a field of bricks with no neighboring bombs have been shown,
     * the nearest bricks that have neighboring bombs will be revealed.
     */
    private void showBorder(){
        for(int i = 0; i < borderBoard.length; i++){
            for(int j = 0; j < borderBoard[0].length; j++){

                if(borderBoard[i][j] == 2){
                    for(int k = -1; k < 2; k++) {
                        for (int m = -1; m < 2; m++) {

                            //if(k == 0 && m == 0) continue;

                            if(isInBounds(i+k,j+m)) {
                                if(board[i+k][j+m].getNeighbors() > 0 && !board[i+k][j+m].isFlagMarked()){
                                    borderBoard[i+k][j+m] = 1;
                                }
                            }
                        }
                    }
                }
            }
        }

        for(int i = 0; i < borderBoard.length; i++){
            for(int j = 0; j < borderBoard[0].length; j++){
                if(borderBoard[i][j] == 1){
                    board[i][j].setAlive(false);
                }
            }
        }
    }

    /**
     * Checks to see if the game has been won.
     * @return true if the game is won
     */
    boolean isGameWon(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[0].length; j++){
                if(board[i][j].isAlive() && !board[i][j].isBomb()){
                    return false;
                }
            }
        }
        won = true;
        return true;
    }

    Brick[][] getBoard(){
        return board;
    }

    Image getBlockImage(){
        return blockImage;
    }

    Image getBombImage(){ return bombImage; }

    Image getSmiley() {
        return smiley;
    }

    public void setSmiley(Image smiley) {
        this.smiley = smiley;
    }

    Image getSourSmiley() {
        return sourSmiley;
    }

    public void setSourSmiley(Image sourSmiley) {
        this.sourSmiley = sourSmiley;
    }

    public Image getShockedSmiley() {
        return shockedSmiley;
    }

    boolean died(){
        return died;
    }

    public void setDied(boolean died){
        this.died = died;
    }

    boolean isPaused() {
        return isPaused;
    }

    void setPaused(boolean paused) {
        isPaused = paused;
    }

    Image getFlagImage() {
        return flagImage;
    }

    public void setFlagImage(Image flagImage) {
        this.flagImage = flagImage;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public boolean isWon() {
        return won;
    }

    public void setGameWon(boolean gameWon){
        this.won = gameWon;
    }

}
