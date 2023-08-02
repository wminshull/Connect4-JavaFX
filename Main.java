package main;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for executing UI of Connect4 (view-controller)
 * @author owen and weston
 *
 */
public class Main extends Application{
	/** image for player one's icon and pieces */
    private final Image playerOneImage = new Image(this.getClass().getResource("/main/resources/weirdtechnocaco.png").toString());
    /** image for player two's icon and pieces */
    private final Image playerTwoImage = new Image(this.getClass().getResource("/main/resources/weirdtechnoimp.png").toString());
    /** icon for the app window */
    private final Image windowIcon = new Image(getClass().getResource("/main/resources/doomguyprofile.png").toString());
    /** animated background video for game */
    private final Media doomguyBackground = new Media(getClass().getResource("/main/resources/doombackground.mp4").toExternalForm());
    /** the music to play in the background */
    private final Media backgroundMusic = new Media(getClass().getResource("/main/resources/backgroundmusic.mp3").toString());
    /** the object to play the background music */
    private MediaView audio;

    /** the model of the gameboard */
    private Gameboard gameboard;
    /** boolean indicating if the opponent is a human or ai (true for ai) */
    private boolean aifoe;
    /** the artificial intelligence player */
    
    /** root stackpane */
    private StackPane root;
    	/** borderpane containing the majority of the app's UI */
        private BorderPane gamePane;
        	/** an Hbox containing the player icon and status label */
            private HBox playerBox;
            	/** the image containing the player icon for each turn */
                private ImageView playerImage;
                /** label containing info on whose turn it is, who won, etc. */
                private Label statusLabel;

    /** gridpane for ? */
    private GridPane gridPane;
            /** pane that arranges all of the gamepiece images */
            private Pane discPane;
            /** Hbox to contain relevant app buttons */
            private HBox buttons;

                /** button for starting a new game */
                private Button newGame;

                /** button for closing the app/game */
                private Button exit;

                /** Toggle Button for the AI player selection */
                private ToggleButton aiToggle;
        /** pane for the back layer of the root pane that is solely containing the background video */
        private Pane backgroundPane;
        	/** the mediaview that will play the background video */
            private MediaView backgroundView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
    	gameboard = new Gameboard();
    	aifoe = true;
        Font doomFont = Font.loadFont(this.getClass().getResourceAsStream("/main/resources/AmazDooMRight.ttf"), 60);
        root = new StackPane();
        gamePane = new BorderPane();
        gamePane.setId("borderPane");
        createUI();
        root = new StackPane();
        root.setMinHeight(640);
        root.setMinWidth(640);
        root.setId("rootStackPane");
        root.getChildren().addAll(backgroundPane, gamePane);
        gamePane.setTop(playerBox);
        gamePane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        gamePane.setCenter(gridPane);
        gamePane.setBottom(buttons);
        createBoard();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/main/resources/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.getIcons().add(windowIcon);
        stage.setTitle("Connect Four");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Creates the gameboard
     */
    public void createBoard(){
        Shape boardRectangle = createBoardRectangle();
        gridPane.add(boardRectangle, 0, 1);
        List<Rectangle> rectangles = createColumns();
        for(Rectangle rectangle: rectangles){
            gridPane.add(rectangle, 0, 1);
        }

    }
    
    /**
     * Creates a rectangle with holes cut out for the pieces.
     * @return the generated shape representing the board
     */
    private Shape createBoardRectangle() {
        Shape boardRectangle = new Rectangle(640, 560);
        for(int row = 0; row < 6; row++){
            for(int col = 0; col < 7; col++){
                Circle piece = new Circle();
                piece.setRadius(40);
                piece.setCenterX(40);
                piece.setCenterY(40);
                piece.setSmooth(true);

                piece.setTranslateX(col * 85 + 20);
                piece.setTranslateY(row * 85 + 20);

                boardRectangle = Shape.subtract(boardRectangle, piece);
            }
        }
        boardRectangle.setFill(Color.web("290202", 0.6));

        return boardRectangle;
    }
    
    /**
     * Generates large transparent columns that detect clicks in a particular
     * column of the gameboard and call the proper methods
     * @return a list containing the generated column pieces
     */
    private List<Rectangle> createColumns(){
        List<Rectangle> rectangles = new ArrayList<>();

        for (int col = 0; col < 7; col++){
            Rectangle gameSlot = new Rectangle(80, 7*80);
            gameSlot.setFill(Color.TRANSPARENT);
            gameSlot.setTranslateX((col*85)+20);

            final int column = col;
            gameSlot.setOnMouseClicked(event -> {
                if(!aifoe) {
                    insertPiece(column);
                }else if(aifoe && gameboard.getTurn()==1) {
                    insertPiece(column);
                }
            });

            rectangles.add(gameSlot);
        }

        return rectangles;
    }

    /**
     * This method inserts pieces onto the visual gameboard based on the location
     * they've been placed at in the model
     * @param col the column number of the move
     */
    private void insertPiece (int col){
        int row = 0;
        Circle piece = new Circle();
        if (gameboard.getTurn() == 1) {
            //if the turn is 1, it will call the player move method using the column
            //input parameter
            piece.setFill(new ImagePattern(playerOneImage));
            gameboard.move(col);
        } else {
            //if the turn is two it will call the AI move method and set the column
            //parameter to the lastMove column attribute.
            if (aifoe){
                piece.setFill(new ImagePattern(playerTwoImage));
                gameboard.moveAI(2);
                col = gameboard.getLastMove().getCol();
            }else{
                piece.setFill(new ImagePattern(playerTwoImage));
                gameboard.move(col);
            }
        }
        row = gameboard.getLastMove().getRow();
        piece.setRadius(40);
        piece.setCenterX(40);
        piece.setCenterY(40);

        discPane.getChildren().add(piece);
        piece.setTranslateX(col*(85) + 20);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), piece);
        translateTransition.setToY((5 - row) * 85 + 20);
        gridPane.setMouseTransparent(true);
        translateTransition.setOnFinished(event -> {
            if(gameboard.winnerFound()) {
                gameOver();
            }else{
                gridPane.setMouseTransparent(false);
                if(gameboard.getTurn() == 1){
                    playerImage.setImage(playerOneImage);
                    statusLabel.setText("Player 1's Turn");
                }else{
                    playerImage.setImage(playerTwoImage);
                    statusLabel.setText("Player 2's Turn");
                }
                if (gameboard.getTurn() == 2 && aifoe) {
                    //if turn 2 it doesn't matter what number gets passed in here because
                    //the ai get move will just replace it with whatever column the
                    //best move is in.
                    insertPiece(0);
                }
            }
        });
        translateTransition.play();
    }

    /**
     * The gameover method, called when the game has ended, displays the winner
     * and then freezes the gameboard.
     */
    private void gameOver() {
    	int winner = gameboard.getTurn();
        if(winner == 1 || winner == 2) {
        	statusLabel.setText("Player " + gameboard.getTurn() + " wins!");
        }else if(winner == 3) {
        	statusLabel.setText("Tie game!");
        }
        gridPane.setMouseTransparent(true);
        newGame.setDefaultButton(true);
    }

    /**
     * Resets the view and signals the model to reset itself as well for a new game.
     */
    public void resetGame(){
        playerImage.setImage(playerOneImage);
        gridPane.setMouseTransparent(false);
        discPane.getChildren().clear();
        gameboard.clear();
        statusLabel.setText("Player 1's turn");
    }

    /**
     * Method closing the app and shutting down the game
     */
    private void exitGame(){
        Platform.exit();
        System.exit(0);
    }

    /**
     * Method creating all pieces of the UI that are not explicitly created elsewhere.
     */
    public void createUI(){
        MediaPlayer audioPlayer = new MediaPlayer(backgroundMusic);
        audioPlayer.setAutoPlay(true);
        audioPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        audioPlayer.play();
        audio = new MediaView();
        audio.setMediaPlayer(audioPlayer);
        audioPlayer.play();

        backgroundPane = new Pane();
        backgroundPane.setId("backgroundPane");
        MediaPlayer player = new MediaPlayer(doomguyBackground);
        player.setAutoPlay(true);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setMute(true);
        backgroundView = new MediaView(player);
        backgroundView.fitHeightProperty().bind(backgroundPane.heightProperty());
        backgroundView.setPreserveRatio(true);
        backgroundView.setX(-350);

        backgroundPane.getChildren().add(backgroundView);

        playerImage = new ImageView();
        playerImage.setImage(playerOneImage);

        statusLabel = new Label("Player 1's turn");
        statusLabel.setAlignment(Pos.CENTER_LEFT);
        Effect glow = new Glow(4.0);
        statusLabel.setEffect(glow);
        statusLabel.setPrefHeight(35);
        statusLabel.setMinWidth(350);
        statusLabel.setTextFill(Color.ORANGE);
        statusLabel.setStyle("-fx-font-size: 60;");
        statusLabel.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        playerBox = new HBox();
        playerBox.setAlignment(Pos.CENTER_LEFT);
        playerBox.setMargin(playerImage, new Insets(15));
        playerBox.setMinWidth(root.getWidth());
        playerBox.getChildren().addAll(playerImage, statusLabel);

        gridPane = new GridPane();
        gridPane.setPrefHeight(400);
        gridPane.setPrefWidth(600);
        ColumnConstraints gridColumnContraints = new ColumnConstraints();
        RowConstraints gridRowConstraints = new RowConstraints();
        gridColumnContraints.setMaxWidth(298);
        gridColumnContraints.setMinWidth(225);
        gridColumnContraints.prefWidthProperty().add(225);
        gridRowConstraints.setMinHeight(10);
        gridRowConstraints.setPrefHeight(30);

        gridPane.getColumnConstraints().add(gridColumnContraints);
        gridPane.getRowConstraints().add(gridRowConstraints);

        discPane = new Pane();
        discPane.setId("discPane");
        discPane.setPrefHeight(400);
        discPane.setPrefWidth(450);
        gridPane.add(discPane, 0, 1, 2, 1);

        buttons = new HBox();
        buttons.setMaxWidth(640);
        buttons.setAlignment(Pos.CENTER);
        buttons.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        buttons.setMaxHeight(30);
        newGame = new Button("New Game");
        newGame.setDefaultButton(false);
        newGame.setEffect(glow);
        newGame.setMinWidth(4*(buttons.getMaxWidth()/8));
        newGame.setMaxHeight(30);
        newGame.setId("bottomButton");
        newGame.setOnMouseClicked(event ->{
            resetGame();
        });
        exit = new Button("Exit");
        exit.setCancelButton(true);
        exit.setEffect(glow);
        exit.setMinWidth(2*buttons.getMaxWidth()/8);
        exit.setMaxHeight(30);
        exit.setId("bottomButton");
        exit.setOnMouseClicked(event ->{
            exitGame();
        });
        aiToggle = new ToggleButton();
        aiToggle.setText("AI");
        aiToggle.setSelected(true);
        aiToggle.setOnAction(event -> {
            if (aiToggle.isSelected()){
                aifoe = true;
            } else {
                aifoe = false;
            }
        });
        aiToggle.setAlignment(Pos.CENTER);
        aiToggle.setEffect(glow);
        aiToggle.setMinWidth(2*(buttons.getMaxWidth()/8));
        buttons.getChildren().addAll(aiToggle, newGame, exit);

    }
}