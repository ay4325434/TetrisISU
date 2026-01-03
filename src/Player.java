/*
Represents a player that has finished the game. Used to score name and score.
 */
public class Player implements Comparable<Player>{
    private String name;
    private int score;
    public Player(String name, int score){
        if(score < 0) score = 0;
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore(){
        return score;
    }

    @Override
    public int compareTo(Player o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString(){
        return name + " " + score;
    }
}
