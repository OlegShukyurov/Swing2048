

public class MoveEfficiency implements Comparable<MoveEfficiency> {

    private int numberOfEmptyTiles;
    private int score;
    private Move move;

    public MoveEfficiency(int numberOfEmptyTiles, int score, Move move) {
        this.numberOfEmptyTiles = numberOfEmptyTiles;
        this.score = score;
        this.move = move;
    }

    public Move getMove() {
        return move;
    }

    @Override
    public int compareTo(MoveEfficiency o) {
        if (numberOfEmptyTiles == o.numberOfEmptyTiles) {
            if (score == o.score) {
                return 0;
            }
        }
        if (numberOfEmptyTiles > o.numberOfEmptyTiles || score > o.score) {
            return 1;
        } else {
            return -1;
        }
    }
}
