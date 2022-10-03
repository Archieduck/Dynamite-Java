package training.dynamite;

import com.softwire.dynamite.bot.Bot;
import com.softwire.dynamite.game.*;

import java.util.List;
import java.util.Random;

public class MyBot implements Bot {

    private int dynamiteCount;
    private int theirDynamiteCount;
    private int dynamiteChance;
    private int theirDynamiteChance;
    private List<Move> lastTen;
    private boolean sameAfterDraw;
    private Move lastDrawOutcome;
    private int movesSinceOppDynamite;

    public MyBot() {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        dynamiteCount = 98;
        theirDynamiteCount = 100;
        theirDynamiteChance = 1;
        System.out.println("Started new match");
    }

    @Override
    public Move makeMove(Gamestate gamestate) {
        dynamiteChance += 1;
        if (gamestate.getRounds().size() > 0) {  // keep count of their dynamite
            Round lastRound = (Round) gamestate.getRounds().get(gamestate.getRounds().size() - 1);
            if (lastRound.getP2() == Move.D) {
                movesSinceOppDynamite = 0;
                theirDynamiteCount -= 1;
            } else {
                movesSinceOppDynamite += 1;
            }
        }
        if (gamestate.getRounds().size() > 1) {
            Round lastRound = (Round) gamestate.getRounds().get(gamestate.getRounds().size() - 1);
            Round lastRoundAgain = (Round) gamestate.getRounds().get(gamestate.getRounds().size() - 2);
            if (lastRoundAgain.getP2() == lastRoundAgain.getP1()){ // get the move they played after the last draw
                if (lastRound.getP2() == lastDrawOutcome) {
                    sameAfterDraw = true;
                } else {
                    sameAfterDraw = false;
                }
                lastDrawOutcome = lastRound.getP2();
            }

            if (lastRound.getP2() == lastRoundAgain.getP2()){  //if they play the same move twice, beat it
                return beatTheirLastMove(lastRound.getP2());
            } else if (dynamiteCount > 1 && chanceOfDynamite(dynamiteCount, dynamiteChance, gamestate)){ //plays my dynamite randomly
                dynamiteChance = 0;
                dynamiteCount -= 1;
                return Move.D;
            } else if (lastRound.getP2().equals(lastRound.getP1())) { // if last round was a draw
                if (lastDrawOutcome != null && gamestate.getRounds().size() > 0 && sameAfterDraw) {
                    return beatTheirLastMove(lastDrawOutcome);
                }else if (lastDrawOutcome != null && !sameAfterDraw){
                    return playTheirLastMove(lastDrawOutcome);
                }
                if (dynamiteCount > 1){
                    dynamiteChance = 0;
                    dynamiteCount -= 1;
                    return Move.D;
                } else {
                    return getRandomMove();
                }

            }else {
                return beatTheirLastMove(lastRound.getP2());
            }

        } else {
            if (dynamiteCount > 1){
                dynamiteChance = 0;
                return Move.D;
            } else {
                return getRandomMove();
            }

        }


    }

    //else if (chanceOfDynamite(theirDynamiteCount, movesSinceOppDynamite, gamestate)) {
    //                    return Move.W;
    //            }

    public Move playTheirLastMove(Move theirLastMove) {
        switch (theirLastMove) {
            case R:
                return Move.R;
            case P:
                return Move.P;
            case S:
                return Move.S;
            case D:
                if (dynamiteCount > 1){
                    dynamiteChance = 0;
                    dynamiteCount -= 1;
                    return Move.D;
                } else {
                    return getRandomMove();
                }
            case W:
                return getRandomMove();
            default:
                throw new RuntimeException("Invalid last move from P111");
        }


    }

    public Move beatTheirLastMove(Move theirLastMove) {
        switch (theirLastMove) {
            case R:
                return Move.P;
            case P:
                return Move.S;
            case S:
                return Move.R;
            case D:
                return Move.W;
            case W:
                return Move.R;
            default:
                throw new RuntimeException("Invalid last move from P2");
        }


    }

    public Boolean chanceOfDynamite(int dynamiteCount, int dynamiteChance, Gamestate gamestate) {
        if (gamestate.getRounds().size() > 0){
            Round lastRound = (Round)gamestate.getRounds().get(gamestate.getRounds().size() - 1);
            Move lastMove = lastRound.getP2();
        }
        Random rand = new Random();
        int int_random = rand.nextInt(5);
        if (dynamiteCount > 1) {
            if (dynamiteChance >= (4 * int_random) || dynamiteCount > (1000 - gamestate.getRounds().size())) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public Move getRandomMove() {
        int randomNumberBetween0And3 = (int)Math.floor(Math.random() * 3.0);
        Move[] possibleMoves = new Move[]{Move.R, Move.P, Move.S};
        Move randomMove = possibleMoves[randomNumberBetween0And3];
        return randomMove;
    }

}
