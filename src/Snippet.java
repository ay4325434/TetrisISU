/*
 * Represents one audio snippet.
 */
public class Snippet {
    private int start, end, loopDelay;
    public Snippet(int start, int end, int loopDelay) {
        this.start = start;
        this.end = end;
        this.loopDelay = loopDelay;
    }
    public int getStart() {
        return start;
    }
    public int getEnd() {
        return end;
    }
    public int getLoopDelay() {
        return loopDelay;
    }
}
