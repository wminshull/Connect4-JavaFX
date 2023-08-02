package main;

import java.util.*;

/**
 * Class containing the logic and code for the AIPlayer
 * @author owen and weston
 *
 */
public class AIPlayer {
	/** the model for the game of connect4 the is AI playing */
	private Gameboard board;

	/**	Move object that is set to the best move from the Minimax algorithm */
	private Move bestMove;

	/** How many layers deep the Minimax algorithm will run*/
	final int DEPTH = 8;
	
	public AIPlayer(Gameboard game) {
		this.board = game;
	}

	//min-max algorithm

	/**
	 * Uses the minimax algorithm to determine the best move
	 * @return A move object, uses the bestMove attribute.
	 */
	public Move getBestMove(){
		ArrayList<Move> moves = new ArrayList<Move>();
		maxPlayer(DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);
		board.setTurn(2);
		moves.clear();
		return bestMove;
	}

	/**
	 * The Max player for the minimax algorithm
	 * @param depth how deep you want the AI to generate moves.
	 * @param alpha alpha for alpha-beta pruning
	 * @param beta beta for alpha-beta pruning
	 * @return the score for the max player.
	 */
	private int maxPlayer(int depth, int alpha, int beta){
		ArrayList<Move> maxMoves = board.makeTestMoves();
		int max = alpha;

		if (depth == 0 || maxMoves.size() == 0){
			return boardHeuristicValue(board.getBoard());
		}

		for(Move m : maxMoves){
			board.testMove(m, 1);
			int value = minPlayer(depth - 1, max, beta);
			board.undoMove(m);

			if (value > max) {
				max = value;
				if (max >= beta) {
					break;
				}
				if (depth == DEPTH) {
					bestMove = m;
				}
			}
		}
		return max;
	}

	/**
	 * The min player for the minimax algorithm
	 * @param depth how deep you want the AI to generate moves.
	 * @param alpha alpha for alpha-beta pruning
	 * @param beta beta for alpha-beta pruning
	 * @return the score for the min player.
	 */
	private int minPlayer(int depth, int alpha, int beta){
		ArrayList<Move> minMoves = board.makeTestMoves();
		int min = beta;

		if (depth == 0 || minMoves.size() == 0){
			return boardHeuristicValue(board.getBoard());
		}

		for(Move m : minMoves){
			board.testMove(m, 2);
			int value = maxPlayer(depth - 1, alpha, min);
			board.undoMove(m);

			if (value < min) {
				min = value;
				if (min <= alpha) {
					break;
				}
				if (depth == DEPTH) {
					bestMove = m;
				}
			}
		}
		return min;
	}

	/**
	 * An overloaded method that generates a heuristic value given a 2D gameboard
	 * @param board the array to generate a heuristic value for
	 * @return the heuristic value for the board that was passed in
	 */
	public int boardHeuristicValue(int[][] board){
		int value = 0;
		for (int row = 0; row < 6; row++){
			for (int col = 0; col < 7; col++){
				value += boardHeuristicValue(board, row, col);
			}
		}
		return value;
	}

	/**
	 * Generates a heuristic value for a 2D gameboard and a row and column index.
	 * It looks at longest chains of similar pieces up, down, left, right, and diagonally.
	 * @param board the 2D array for generating heuristic values
	 * @param row the row index that you want to start at
	 * @param col the column index that you want to start at
	 * @return the value for the chains at the given row, col index.
	 */
	public int boardHeuristicValue(int board[][], int row, int col){
		int value = 0;
		if (col <= 3){	//checking board values to the right of the piece
			value += chainHeuristicValue(new int[] {col, board[row][col], board[row][col+1], board[row][col+2], board[row][col+3]});
		}
		if (col >= 3){ //checking board values to the left of the piece and generating a score from it
			value += chainHeuristicValue(new int[] {board[row][col], board[row][col-1], board[row][col-2], board[row][col-3]});
		}
		if (row >= 3){ //checking board values below the piece and generating a score from it
			value += chainHeuristicValue(new int[] {board[row][col], board[row-1][col], board[row-2][col], board[row-3][col]});
		}
		if (row <= 2){ //generating a score from values above the piece
			value += chainHeuristicValue(new int[] {board[row][col], board[row+1][col], board[row+2][col], board[row+3][col]});
		}
		if (col <= 3 && row <= 2){	//generating a score from values above and to the right of the piece
			value += chainHeuristicValue(new int[] {board[row][col], board[row +1][col+1], board[row+2][col+2], board[row+3][col+3]});
		}
		if (col <= 3 && row >= 3){	//generating a score from values below and to the right of the piece
			value += chainHeuristicValue(new int[] {board[row][col], board[row-1][col+1], board[row-2][col+2], board[row-3][col+3]});
		}
		if (col >= 3 && row <= 2){	//generating a score from values above and to the left of the piece
			value += chainHeuristicValue(new int[] {board[row][col], board[row+1][col-1], board[row+2][col-2], board[row+3][col-3]});
		}
		if (col >= 3 && row >=3){
			value += chainHeuristicValue(new int[] {board[row][col], board[row-1][col-1], board[row-2][col-2], board[row-3][col-3]});
		}
		return value;
	}

	/**
	 * Takes in the 4-element arrays from boardHeuristicValue and determines the value based
	 * on how many pieces in a row there are and whether or not the row is blocked by an
	 * opponents piece.
	 * @param chain the array that will be scored
	 * @return a value that is negative if an opponents chain exists in the array or positive
	 * if the AI's chain exists.
	 */
	public int chainHeuristicValue(int[] chain){
		int player1Chain = 0;
		int player2Chain = 0;
		for(int i = 0; i < chain.length; i++){
			if (chain[i] == 1){
				player1Chain++;
			}
			if (chain[i] == 2){
				player2Chain++;
			}
		}
		if (player1Chain == 0){
			if (player2Chain == 4) return -1000;
			if (player2Chain == 3) return -50;
			if (player2Chain == 2) return -10;
			if (player2Chain == 1) return -1;
		}
		if (player2Chain == 0){
			if (player1Chain == 4) return 1000;
			if (player1Chain == 3) return 50;
			if (player1Chain == 2) return 10;
			if (player1Chain == 1) return 1;
		}
		return 0;
	}
}