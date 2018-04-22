package minesveiper;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    GridPane gridPane;

    @FXML
    Canvas canvas;

    @FXML
    ImageView imageView;

    @FXML
    Label howManyBombs;

    @FXML
    Label secondsElapsed;

    @FXML
    VBox vbox;

    @FXML
    Button beginner;

    @FXML
    Button intermediate;

    @FXML
    Button expert;

    private GraphicsContext gc;
    private Game game;
    private AnimationTimer at;
    private int seconds;
    private boolean gamePaused = false;

    /**
     * Draws the game on the canvas.
     */
    private void drawGame(){
        int x = 0;
        int y = 0;
        Brick[][] board = game.getBoard();
        gc.setFill(Color.web("#8DA9B3"));
        gc.fillRect(0,0,600,340);
        for(int i = 0; i < game.getRows(); i++){

            for(int j = 0; j < game.getColumns(); j++){
                Brick brick = board[i][j];

                if(brick.isBomb()){
                    gc.drawImage(game.getBombImage(),x,y,22,22);
                } else if(!brick.isAlive()){
                    if(brick.getNeighbors() > 0){
                        gc.setFill(Color.YELLOW);
                        if(brick.getNeighbors() == 1) gc.setFill(Color.BLUE);
                        if(brick.getNeighbors() == 2) gc.setFill(Color.GREEN);
                        if(brick.getNeighbors() == 3) gc.setFill(Color.MEDIUMVIOLETRED);
                        if(brick.getNeighbors() == 4) gc.setFill(Color.YELLOW);
                        gc.fillText(String.valueOf(brick.getNeighbors()),x+5,y+15);
                    }
                }
                if(brick.isAlive()) {
                    gc.drawImage(game.getBlockImage(), x, y, 20, 20);
                    if(brick.isFlagMarked()) gc.drawImage(game.getFlagImage(),x+1,y+1,18,18);
                }
                x+=20;
            }
            y+=20;
            x=0;
        }
    }

    /**
     * Takes in a mouse event when a mouse has clicked on the canvas.
     * @param e mouseclick-event
     */
    public void mousePressed(MouseEvent e){
        if(!game.died() && !game.isWon() && !gamePaused) {
            if (e.isSecondaryButtonDown()) {
                game.mouseRightClicked(e);
            } else {
                imageView.setImage(game.getShockedSmiley());
                game.mousePressed(e);
                if (game.died()) {
                    imageView.setImage(game.getSourSmiley());
                    at.stop();
                    int x = 0;
                    int y = 0;
                    Brick[][] board = game.getBoard();
                    for(int i = 0; i < game.getRows(); i++) {

                        for (int j = 0; j < game.getColumns(); j++) {
                            Brick brick = board[i][j];

                            if (brick.isBomb()) {
                                gc.drawImage(game.getBombImage(), x, y, 22, 22);
                            }
                            x += 20;
                        }
                        y += 20;
                        x = 0;
                    }
                }
            }
            if(!game.died())drawGame();
        }
        if(game.isGameWon()){
            at.stop();
            gc.setFill(Color.CRIMSON);
            gc.fillText("You have won",30,100);
        }
    }

    /**
     * Detecs when a mouseclick has been released, and changes the smileyface.
     */
    public void mouseReleased(){
        if(!game.died() && !game.isWon() && !gamePaused)imageView.setImage(game.getSmiley());
    }

    /**
     * Takes in a keyevent, and pauses the game if the appropriate key was pressed.
     * @param e keyevent
     */
    public void keyPressed(KeyEvent e){

        if(e.getCode() == KeyCode.ESCAPE){
            gamePaused = !gamePaused;
            if(!gamePaused) at.start();
            else at.stop();

            vbox.setVisible(gamePaused);
            beginner.setVisible(gamePaused);
            intermediate.setVisible(gamePaused);
            expert.setVisible(gamePaused);
        }
    }

    /**
     * Calls on actions to start a new game.
     */
    public void newGame(){
        if(!gamePaused) {
            game.newGame();
            drawGame();
            imageView.setImage(game.getSmiley());
            at.stop();
            seconds = 0;
            game.setGameWon(false);
            tickingClock();
        }
    }

    /**
     * Ticking clock that counts the seconds a game has been playing.
     */
    private void tickingClock(){

        secondsElapsed.setText(Integer.toString(0));
        seconds = 0;

        at = new AnimationTimer() {
            long time = 0;

            @Override
            public void handle(long now) {
                if (time != 0) {
                    if (now > time + 1_000_000_000) {
                        seconds++;
                        secondsElapsed.setText(Integer.toString(seconds));
                        time = now;
                    }
                } else {
                    time = now;
                }
            }
        };
        at.start();
    }

    /**
     * Starts a beginner game.
     * @throws IOException if fxml-file is not found
     */
    public void startBeginnerGame() throws IOException{
        Stage stage;
        Parent root;
        stage = (Stage) gridPane.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("beginnerGameboard.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        root.requestFocus();
    }

    /**
     * Starts an intermediate game.
     * @throws IOException if fxml-file is not found
     */
    public void startIntermediateGame() throws IOException{
        Stage stage;
        Parent root;
        stage = (Stage) gridPane.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("intermediateGameboard.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        root.requestFocus();
    }

    /**
     * Starts an expert game.
     * @throws IOException if fxml-file is not found
     */
    public void startExpertGame() throws IOException{
        Stage stage;
        Parent root;
        stage = (Stage) gridPane.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("gameboard.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        root.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        gc.setFont(Font.font(null, FontWeight.BOLD, 16));
        game = new Game(gc,howManyBombs);
        drawGame();
        tickingClock();
    }
}
