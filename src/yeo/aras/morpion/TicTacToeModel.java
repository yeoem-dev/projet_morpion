package yeo.aras.morpion;

import javafx.beans.binding.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TicTacToeModel {

    //Taille du plateau de jeu (pour être extensible).
    private final static int BOARD_WIDTH = 3;
    private final static int BOARD_HEIGHT = 3;

    // Nombre de piéces alignés pour gagner (idem).
    private final static int WINNING_COUNT = 3;

    // Joueur courant
    private final ObjectProperty<Owner> turn = new SimpleObjectProperty<>(Owner.FIRST);

    // Vainqueur du jeu, NONE si pas de vainqueur
    private final ObjectProperty<Owner> winner = new SimpleObjectProperty<>(Owner.NONE);

    // Plateau de jeu
    private final ObjectProperty<Owner>[][] board;

    // Positions gagnantes
    private final BooleanProperty[][] winningBoard;

    // Constructeur privé
    private TicTacToeModel() {
        board = new ObjectProperty[BOARD_WIDTH][BOARD_HEIGHT];
        winningBoard = new BooleanProperty[BOARD_WIDTH][BOARD_HEIGHT];
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
                winningBoard[i][j] = new SimpleBooleanProperty(false);
            }
        }
    }

    public static int getBoardWidth() {
        return BOARD_WIDTH;
    }

    public static int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    /**
     * @return la seule instance possible du jeu
     */
    public static TicTacToeModel getInstance() {
        return TicTacToeModelHolder.INSTANCE;
    }

    // Classe interne selon le pattern singleton
    private static class TicTacToeModelHolder {
        private static final TicTacToeModel INSTANCE = new TicTacToeModel();
    }

    public void restart() {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                board[i][j].set(Owner.NONE);
                winningBoard[i][j].set(false);
            }
        }
        turn.set(Owner.FIRST);
        winner.set(Owner.NONE);
    }

    public final ObjectProperty<Owner> turnProperty() {
        return turn;
    }

    public final ObjectProperty<Owner> getSquare(int row, int column) {
            return board[row][column];
    }

    public final BooleanProperty getWinningSquare(int row, int column) {
        return winningBoard[row][column];
    }

    /**
     * Cette fonction ne doit donner le bon résultat que si le jeu est
     * terminé. L'affichage peut être caché avant la fin du jeu.
     *
     * @return résultat du jeu sous forme de texte
     */
    public final StringExpression getEndOfMessage() {
        StringBinding winnerMessage = Bindings.createStringBinding(() -> {
            Owner winner = this.winner.get();
            if (winner == Owner.FIRST) {
                return "Le joueur 1 a gagné.";
            } else if (winner == Owner.SECOND) {
                return "Le joueur 2 a gagné.";
            } else {
                return "Match nul !";

            }
        }, winner);
        return Bindings.when(gameOver()).then(winnerMessage).otherwise("");
    }

    public void setWinner(Owner winner) {
        this.winner.set(winner);
    }

    public boolean validSquare(int row, int column) {
        return row >= 0 && row <= BOARD_WIDTH && column >= 0 && column <= BOARD_HEIGHT;
    }

    public void nextPlayer() {
        turn.set(turn.get().opposite());
    }

    //Jouer dans la case (row, column) quand c'est possible
    public void play(int row, int column) {
        if (legalMove(row, column).get()) {
            board[row][column].set(turn.get());
            nextPlayer();
        }
    }

    /**
     * @return true s'il est possible de jouer dans la case
     * c'est-à-dire la case est libre et le jeu n'est pas terminé
     */
    public BooleanBinding legalMove(int row, int column) {
        return Bindings.createBooleanBinding(()-> validSquare(row, column)
                && !gameOver().get()
                && board[row][column].get() == Owner.NONE, turn, winner);
    }

    public NumberExpression getScore() {
        IntegerBinding countX = new IntegerBinding() {
            @Override
            protected int computeValue() {
                int count = 0;
                for (int i = 0; i < BOARD_WIDTH; i++) {
                    for (int j = 0; j < BOARD_HEIGHT; j++) {
                        if (board[i][j].get() == Owner.FIRST) {
                            count++;
                        }
                    }
                }
                return count;
            }
        };

        IntegerBinding countO = new IntegerBinding() {
            @Override
            protected int computeValue() {
                int count = 0;
                for (int i = 0; i < BOARD_WIDTH; i++) {
                    for (int j = 0; j < BOARD_HEIGHT; j++) {
                        if (board[i][j].get() == Owner.SECOND) {
                            count++;
                            System.out.println(count);
                        }
                    }
                }
                return count;
            }
        };

        return Bindings.add(countX, countO);
    }


    /**
     * @return true si le jeu est terminé
     * (soit un joueur a gagné, soit il n'y a plus de cases à jouer)
     */
    public BooleanBinding gameOver() {
        // Scénario: la table est pleine
        BooleanBinding fullBoard = Bindings.createBooleanBinding(()->{
            for (int i = 0; i < BOARD_WIDTH; i++) {
                for (int j = 0; j < BOARD_HEIGHT; j++) {
                    if (board[i][j].get() == Owner.NONE) {
                        return false;
                    }
                }
            }
            // sinon true :)
            return true;
        });

        // Scénario: Il y'a un gagnant
        BooleanBinding winCondition = Bindings.createBooleanBinding(()->{
            // parcourir les lignes
            for (int i = 0; i < BOARD_WIDTH; i++) {
                if (board[i][0].get() != Owner.NONE &&
                        board[i][0].get() == board[i][1].get() &&
                        board[i][1].get() == board[i][2].get()) {
                    setWinner(board[i][0].get());
                    for (int j = 0; j < BOARD_HEIGHT; j++) {
                        winningBoard[i][j].set(true);
                    }
                    return true;
                }
            }

            // parcourir les colonnes
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                if (board[0][j].get() != Owner.NONE &&
                        board[0][j].get() == board[1][j].get() &&
                        board[1][j].get() == board[2][j].get()) {

                    setWinner(board[0][j].get());
                    for (int i = 0; i < BOARD_WIDTH; i++) {
                        winningBoard[i][j].set(true);
                    }
                    return true;
                }
            }

            // diagonale principale
            if (board[0][0].get() != Owner.NONE &&
                    board[0][0].get() == board[1][1].get() &&
                    board[1][1].get() == board[2][2].get()) {
                for (int k = 0; k < BOARD_WIDTH; k++) {
                    winningBoard[k][k].set(true);
                }
                setWinner(board[0][0].get());
                return true;
            }

            // diagonale secondaire
            if (board[0][2].get() != Owner.NONE &&
                    board[0][2].get() == board[1][1].get() &&
                    board[1][1].get() == board[2][0].get()) {
                for (int k = 0; k < BOARD_WIDTH; k++) {
                    winningBoard[k][2 - k].set(true);
                }
                setWinner(board[0][2].get());
                return true;
            }
            return false;
        }, board[0][0], board[0][1], board[0][2], board[1][0], board[1][1], board[1][2], board[2][0], board[2][1], board[2][2]);

        return fullBoard.or(winCondition);

    }

}
