/*
 * Represents one audio snippet.
 */
public class Snippet {
    int start, end, loopDelay;
    public Snippet(int start, int end, int loopDelay) {
        this.start = start;
        this.end = end;
        this.loopDelay = loopDelay;
    }
}
