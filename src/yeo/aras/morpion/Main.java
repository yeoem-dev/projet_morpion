package yeo.aras.morpion;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    private TicTacToeModel model;
    private Label occupiedByXLabel;
    private Label occupiedByOLabel;
    private Label freeCellsLabel;

    @Override
    public void start(Stage primaryStage) {
        model = TicTacToeModel.getInstance();

        BorderPane root = new BorderPane();
        GridPane gridPane = new GridPane();
        Button restartButton = new Button("Restart");
        occupiedByXLabel = new Label();
        occupiedByOLabel = new Label();
        freeCellsLabel = new Label();

        root.setCenter(gridPane);
        root.setBottom(restartButton);
        root.setLeft(occupiedByXLabel);
        root.setRight(occupiedByOLabel);
        root.setTop(freeCellsLabel);

        // Créer la grille
        for (int i = 0; i < TicTacToeModel.getBoardWidth(); i++) {
            for (int j = 0; j < TicTacToeModel.getBoardHeight(); j++) {
                TicTacToeSquare square = new TicTacToeSquare(i, j);
                gridPane.add(square, j, i); // Notez l'inversion des indices pour la disposition correcte
            }
        }

        // Actions du bouton Restart
        restartButton.setOnAction(event -> {
            model.restart();
            updateLabels();
        });

        Label statusLabel = new Label();
        statusLabel.textProperty().bind(model.getEndOfMessage());

        // Mise à jour des labels

        updateLabels();

        // Styling
        occupiedByXLabel.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        occupiedByOLabel.setStyle("-fx-background-color: cyan; -fx-text-fill: black;");
        freeCellsLabel.setStyle("-fx-background-color: lightgray;");

        // Alignement des labels
        gridPane.add(statusLabel, 0, TicTacToeModel.getBoardHeight(), TicTacToeModel.getBoardWidth(), 1);

        occupiedByXLabel.setAlignment(Pos.CENTER);
        occupiedByOLabel.setAlignment(Pos.CENTER);
        freeCellsLabel.setAlignment(Pos.CENTER);

        // Créer la scène
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.show();
    }

    public void updateLabels() {
        int occupiedByX = model.getScore().getValue().intValue();
        int occupiedByO = model.getScore().getValue().intValue() - occupiedByX; // Le nombre de cases occupées par O est le complément du nombre de cases occupées par X
        int freeCells = 9 - model.getScore().getValue().intValue();

        System.out.println(model.getScore().getValue().intValue());

        occupiedByXLabel.setText("Occupied by X: " + occupiedByX);
        occupiedByOLabel.setText("Occupied by O: " + occupiedByO);
        freeCellsLabel.setText("Free cells: " + freeCells);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
