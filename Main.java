package org.example;

import java.util.Scanner;
public class Main {
    public static void main(String[] args) {

        Gameboard game = new Gameboard();
        int flag = -1;
        int playerColumn = 0;
        Scanner in = new Scanner(System.in);
        while(flag < 0) {
            System.out.println("Player " + game.getTurnMarker() + " please enter what column you want to place your next piece in.");
            playerColumn = in.nextInt();
            flag = game.move(playerColumn);

            System.out.println(game);
        }
    }
}