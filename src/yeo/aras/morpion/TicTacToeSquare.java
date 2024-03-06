package yeo.aras.morpion;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class TicTacToeSquare extends TextField {

    private static TicTacToeModel model = TicTacToeModel.getInstance();

    private ObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>(Owner.NONE);
    private SimpleBooleanProperty winningSquareProperty = new SimpleBooleanProperty(false);

    public ObjectProperty<Owner> colorProperty() {
        return ownerProperty;
    }

    public SimpleBooleanProperty winningSquareProperty() {
        return winningSquareProperty;
    }

    public TicTacToeSquare(final int row, final int column) {
        ownerProperty.bind(model.getSquare(row, column));
        winningSquareProperty.bind(model.getWinningSquare(row, column));

        setPrefSize(200, 200);
        setEditable(false);

        ownerProperty.addListener((observable, oldValue, newValue) -> {
            setText(newValue == Owner.NONE ? "" : (newValue == Owner.FIRST ? "X" : "O"));
            setEditable(newValue == Owner.NONE);
            setFont(Font.font(Math.min(getHeight(), getWidth()) * 0.5));
        });

        winningSquareProperty.addListener((observable, oldValue, newValue) -> {
            setStyle(newValue ? "-fx-background-color: cyan;" : "");
        });

        setOnMouseClicked(event -> {

            if (model.legalMove(row, column).get()) {
                model.play(row, column);
            }
        });


        setOnMouseEntered(event -> {

            if (!model.gameOver().get() && model.legalMove(row, column).get()) {
                setStyle("-fx-background-color: green;");
            } else {
                setStyle("-fx-background-color: red;");
            }
        });

        setOnMouseExited(event -> {

            setStyle(winningSquareProperty.get() ? "-fx-background-color: cyan;" : "");
        });
    }
}