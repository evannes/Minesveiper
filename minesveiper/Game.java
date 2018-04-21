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

    void mousePressed(MouseEvent e){
        int x = (int)e.getY()/blockSize;
        int y = (int)e.getX()/blockSize;
        if(board[x][y].isAlive() && !board[x][y].isFlagMarked()) {
            board[x][y].setAlive(false);
            if (board[x][y].isBomb()) {
                if(!hasStarted){
                    moveBomb(x,y);
                    hasStarted = true;
                } else {
                    died = true;
                }
            } else if(board[x][y].getNeighbors() == 0){
                borderBoard = new int[board.length][board[0].length];
                removeEmptyNeighbors(x, y);
                showBorder();
            }
        }
    }

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

    private void removeOne(int x, int y, int i, int j){
        if(board[x+i][y+j].isAlive()){
            if(!board[x+i][y+j].isFlagMarked()) {
                board[x + i][y + j].setAlive(false);
                removeEmptyNeighbors(x + i, y + j);
            }
        }
    }

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

    void newGame(){
        initBoard();
        died = false;
        hasStarted = false;
        howManyBombs.setText(String.valueOf(bombs));
    }

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
        // count neighbors
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                Brick brick = board[i][j];
                countNeighbors(i,j, brick);
            }
        }
    }

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

    private boolean isInBounds(int x, int y){
        if(x < 0 || x > rows-1 || y < 0 || y > columns-1) return false;
        return true;
    }

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

    public boolean isGameWon(){
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
