package org.example;

import java.util.ArrayList;
import java.lang.Math;

public class Gameboard {
    int[][] gb = new int[6][7];

    /**
     * marks which player is currently playing.
     * 1 for player 1, 2 for player 2
     */
    private int turnMarker = 1;

    private int chainLeft = 1;
    private int chainRight = 1;

    public int getTurnMarker(){
        return turnMarker;
    }

    /**
     * Move method: places a piece in the array at the coordinates of [y][x] and then checks if resulting play was
     * a winning move.
     * @param x
     * @return -1 if no win condition is found, otherwise returns the number of the winning player.
     */
    public int move(int x){
        int y = 0;
        //placing the piece in the array
        {
            for (int i = gb.length - 1; i >= 0; i--) {
                if (gb[i][x] == 0) {
                    gb[i][x] = turnMarker;
                    y = i;
                    break;
                }
            }

        }

        if (turnMarker == 2){
            turnMarker = 1;
        } else {
            turnMarker = 2;
        }

        return winCheck(y, x);
    }


    public int winCheck(int y, int x){
        if (checkDiagonalUpper(y, x)){
            return turnMarker;
        };
        if (checkDiagonalLower(y, x)){
            return turnMarker;
        };
        if (checkHorizontal(y, x)){
            return turnMarker;
        };
        if (checkVertical(y, x)){
            return turnMarker;
        };

        return -1;
    }

    /**
     * checks the two upper diagonal win conditions, y and x are the starting point for the win checking.
     * It should be the coordinates of the last piece that was played.
     * @param y is the rows of the array
     * @param x is the rows of the array
     * @return true if there are 4 in a row, or false if not
     */
    public boolean checkDiagonalUpper(int y, int x){
        for (int i = y+1; i <= y + 3; i++){
            for (int j = x+1; j <= x + 3; j++){

                //checks array index up and to the right
                if (isValidMove(i,j) && gb[i][j] == turnMarker){
                    chainRight++;
                }
                //checks array index up and to the left
                if (isValidMove(i, -j) && gb[i][-j] == turnMarker){
                    chainLeft++;
                }

                //checks if win condition is met
                if (chainLeft >= 4 || chainRight >= 4){
                    return true;
                }

            }
        }
        chainRight = 1;
        chainLeft = 1;
        return false;
    }
    /**
     * checks the two lower diagonal win conditions, y and x are the starting point for the win checking.
     * It should be the coordinates of the last piece that was played.
     * @param y is the rows of the array
     * @param x is the cols of the array
     * @return true if there are 4 in a row, or false if not
     */
    public boolean checkDiagonalLower(int y, int x){
        for (int i = y+1; i <= y + 3; i++){
            for (int j = x+1; j <= x + 3; j++){

                //checks array index down and to the right
                if (isValidMove(-i, j) && gb[-i][j] == turnMarker){
                    chainRight++;
                }
                //checks array index up and to the left
                if (isValidMove(-i, -j) && gb[-i][-j] == turnMarker){
                    chainLeft++;
                }

                //checks if win condition is met
                if (chainLeft >= 4 || chainRight >= 4){
                    return true;
                }

            }
        }
        chainRight = 1;
        chainLeft = 1;
        return false;
    }

    public boolean checkHorizontal(int y, int x){
        for (int i = x+1; i <= x + 3; i++){

            //checks array index to the right
            if (isValidMove(y, i) && gb[y][i] == turnMarker){
                chainRight++;
            }

            //checks array index to the left
            if (isValidMove(y , -i) && gb[y][-i] == turnMarker){
                chainLeft++;
            }
        }

        //checks if win condition is met
        if (chainLeft >= 4 || chainRight >= 4){
            return true;
        }

        chainRight = 1;
        chainLeft = 1;
        return false;
    }

    public boolean checkVertical(int y, int x){
        for (int i = y - 1; i <= y - 3; i++){

            //checks array index below
            if (isValidMove(i, x) && gb[i][x] == turnMarker){
                chainLeft++;
            }
        }

        //checks if win condition is met
        if (chainLeft >= 4){
            return true;
        }

        chainLeft = 1;
        return false;
    }

    public boolean isValidMove(int y, int x){
        if (y < gb.length && x < gb[0].length){
            if (y >= 0 && x >= 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        String returnString = "";
        for (int i = 0; i < gb[0].length; i++){
            returnString += "   " + i;
        }
        returnString += "\n";
        for (int i = 0; i < gb.length; i++){
            returnString += i + "  ";
            for (int j = 0; j < gb[i].length; j++){
                returnString += gb[i][j] + "   ";
            }
            returnString += "\n";
        }

        return returnString;
    }
}
