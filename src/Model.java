

import java.util.*;

public class Model {

    private static final int FIELD_WIDTH = 4;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;
    private Tile[][] gameTiles;
    int score;
    int maxTile;

    public Model() {
        score = 0;
        maxTile = 0;
        resetGameTiles();
    }
    public void autoMove() {
        PriorityQueue<MoveEfficiency> priorityQueue = new PriorityQueue<>(4, Collections.reverseOrder());

        priorityQueue.add(getMoveEfficiency(() -> left()));
        priorityQueue.add(getMoveEfficiency(() -> right()));
        priorityQueue.add(getMoveEfficiency(() -> up()));
        priorityQueue.add(getMoveEfficiency(() -> down()));
        
        priorityQueue.peek().getMove().move();
    }
    private void saveState(Tile[][] tiles) {
        Tile[][] newGameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                newGameTiles[i][j] = new Tile(gameTiles[i][j].value);
            }
        }
        previousStates.push(newGameTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }
    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }
    public Tile[][] rotateGameTiles(Tile[][] gameTiles) {
        Tile[][] result = new Tile[gameTiles[0].length][gameTiles.length];
        int arrI = gameTiles.length - 1;
        int arrJ = 0;
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = gameTiles[arrI--][arrJ];
            }
            arrI = gameTiles.length - 1;
            arrJ++;
        }
        return result;

    }
    public MoveEfficiency getMoveEfficiency(Move move) {
        move.move();
        MoveEfficiency moveEfficiency;
        if (!hasBoardChanged()) {
            moveEfficiency = new MoveEfficiency(-1, 0, move);
        } else {
            moveEfficiency = new MoveEfficiency(getEmptyTiles().size(), score, move);
        }
        rollback();
        return moveEfficiency;
    }
    public boolean hasBoardChanged() {
        int sumNewGameTiles = 0;
        for (Tile[] tiles : gameTiles) {
            for (Tile tile : tiles) {
                sumNewGameTiles += tile.value;
            }
        }
        int sumOldGameTiles = 0;
        for (Tile[] tilesOld : previousStates.peek()) {
            for (Tile tileOld : tilesOld) {
                sumOldGameTiles += tileOld.value;
            }
        }
        if (sumNewGameTiles != sumOldGameTiles) {
            return true;
        } else {
            return false;
        }
    }
    void up() {
        saveState(gameTiles);
        gameTiles = rotateGameTiles(gameTiles);
        gameTiles = rotateGameTiles(gameTiles);
        gameTiles = rotateGameTiles(gameTiles);
        left();
        gameTiles = rotateGameTiles(gameTiles);
    }
    void down() {
        saveState(gameTiles);
        gameTiles = rotateGameTiles(gameTiles);
        left();
        gameTiles = rotateGameTiles(gameTiles);
        gameTiles = rotateGameTiles(gameTiles);
        gameTiles = rotateGameTiles(gameTiles);
    }
    void right() {
        saveState(gameTiles);
        gameTiles = rotateGameTiles(gameTiles);
        gameTiles = rotateGameTiles(gameTiles);
        left();
        gameTiles = rotateGameTiles(gameTiles);
        gameTiles = rotateGameTiles(gameTiles);
    }

    void left() {
        if (isSaveNeeded) {
            saveState(gameTiles);
        }
        boolean moveFlag = false;
        for (int i = 0; i < gameTiles.length; i++) {
            if (compressTiles(gameTiles[i]) || mergeTiles(gameTiles[i])) {
                moveFlag = true;
            }
        }
        if (moveFlag) {
            addTile();
        }
        isSaveNeeded = true;
    }

    public void randomMove() {
        int n = ((int) (Math.random() * 100)) % 4;
        switch (n) {
            case 0:
                left();
                break;
            case 1:
                right();
                break;
            case 2:
                up();
                break;
            case 3:
                down();
                break;
        }
    }

    private boolean mergeTiles(Tile[] tiles) {
        boolean isMadeChanges = false;
        for (int i = 0; i < tiles.length - 1; i++) {
            Tile currentTile = tiles[i];
            Tile nextTile = tiles[i + 1];

            if ((currentTile.value != 0 && nextTile.value != 0) &&
                (currentTile.value == nextTile.value)) {
                currentTile.value += nextTile.value;
                nextTile.value = 0;

                score += currentTile.value;
                if (currentTile.value > maxTile) {
                    maxTile = currentTile.value;
                }
                isMadeChanges = true;
            }
        }
        compressTiles(tiles);
        return isMadeChanges;
    }

    private boolean compressTiles(Tile[] tiles) {
        boolean isMadeChanges = false;
        for (int i = tiles.length - 1; i >= 0; i--) {
            if (tiles[i].value == 0 && i + 1 != tiles.length) {
                for (int j = i + 1; j < tiles.length; j++) {
                    if (tiles[j].value != 0) {
                        tiles[i].value = tiles[j].value;
                        tiles[j].value = 0;
                        i = j;
                        isMadeChanges = true;
                    }
                }
            }
        }
        return isMadeChanges;
    }

    private void addTile() {
        if (getEmptyTiles().isEmpty()) {
            return;
        }
        Tile tileToAdd = getEmptyTiles().get((int) (getEmptyTiles().size()* Math.random()));
        tileToAdd.value = Math.random() < 0.9 ? 2 : 4;
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> emptyTiles = new ArrayList<>();
        for (Tile[] tiles : gameTiles) {
            for (Tile tile : tiles) {
                if (tile.value == 0) {
                    emptyTiles.add(tile);
                }
            }
        }
        return emptyTiles;
    }

    void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles[i].length; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }
    public boolean canMove() {
        if (!getEmptyTiles().isEmpty()) {
            return true;
        }
        for (int i = 0; i < gameTiles.length; i++) {
            for (int j = 0; j < gameTiles.length - 1; j++) {
                if (gameTiles[i][j].value == gameTiles[i][j + 1].value) {
                    return true;
                }
            }
        }
        for (int j = 0; j < gameTiles.length; j++) {
            for (int i = 0; i < gameTiles.length - 1; i++) {
                if (gameTiles[i][j].value == gameTiles[i + 1][j].value) {
                    return true;
                }
            }
        }
        return false;
    }
    public Tile[][] getGameTiles() {
        return gameTiles;
    }
}
