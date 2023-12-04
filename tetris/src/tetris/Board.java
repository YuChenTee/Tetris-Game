package tetris;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements KeyListener {
	public static int STATE_GAME_PLAY = 0;
	public static int STATE_GAME_PAUSE = 1;
	public static int STATE_GAME_OVER = 2;
	
	private long startTime = System.currentTimeMillis();
	private long elapsedTime;
	
	private int state = STATE_GAME_PLAY;
	private static int FPS = 60;
	private static int delay = 0;
	
	public static final int BOARD_WIDTH = 10;
	public static final int BOARD_HEIGHT = 20;
	public static final int BLOCK_SIZE = 30;
	private Timer looper;
	private Color[][] board = new Color[BOARD_HEIGHT][BOARD_WIDTH];
	public static int point = 0;
	private Random random = new Random();
	
	private Shape[] shapes = new Shape[7];
	private Shape[] shapesClone = new Shape[7];
	private Shape nextShape;
	private Shape currentShape;
	private Shape holdShape = null;
	public static Boolean shiftClicked = false;
	
	int randomNum = random.nextInt(shapesClone.length);
	
	private Color[] colors = {Color.decode("#eed1c24"),Color.decode("#ff7f27"),Color.decode("#fff200"),
			Color.decode("#22b14c"),Color.decode("#00a2e8"),Color.decode("#a349a4"),Color.decode("#3f48cc")
	};
	public Board() {
		
		startTime = System.currentTimeMillis();
		shapes[0] = new Shape(new int[][] {
			{1,1,1,1}
		},this, colors[0]);
		shapes[1] = new Shape(new int[][] {
			{1,1,1},
			{0,1,0}
		},this, colors[1]);
		shapes[2] = new Shape(new int[][] {
			{1,1,1},
			{1,0,0}
		},this, colors[2]);
		shapes[3] = new Shape(new int[][] {
			{1,1,1},
			{0,0,1}
		},this, colors[3]);
		shapes[4] = new Shape(new int[][] {
			{0,1,1},
			{1,1,0}
		},this, colors[4]);
		shapes[5] = new Shape(new int[][] {
			{1,1,0},
			{0,1,1}
		},this, colors[5]);
		shapes[6] = new Shape(new int[][] {
			{1,1},
			{1,1}
		},this, colors[6]);
		
		shapesClone[0] = new Shape(new int[][] {
			{1,1,1,1}
		},this, colors[0]);
		shapesClone[1] = new Shape(new int[][] {
			{1,1,1},
			{0,1,0}
		},this, colors[1]);
		shapesClone[2] = new Shape(new int[][] {
			{1,1,1},
			{1,0,0}
		},this, colors[2]);
		shapesClone[3] = new Shape(new int[][] {
			{1,1,1},
			{0,0,1}
		},this, colors[3]);
		shapesClone[4] = new Shape(new int[][] {
			{0,1,1},
			{1,1,0}
		},this, colors[4]);
		shapesClone[5] = new Shape(new int[][] {
			{1,1,0},
			{0,1,1}
		},this, colors[5]);
		shapesClone[6] = new Shape(new int[][] {
			{1,1},
			{1,1}
		},this, colors[6]);
		currentShape = shapes[0];
		getNextShape();
		
		looper = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
				repaint();
			}
		});
		looper.start();
		
	}
	
	private void updateTime() {
		long currentTime = System.currentTimeMillis();
	    elapsedTime = currentTime - startTime;
	}
	private void update() {
		if(state == STATE_GAME_PLAY) {
			currentShape.update();
			currentShape.generateShadowShape();
			updateTime();
			
		}
	}
	
	public void getNextShape() {
		randomNum = random.nextInt(shapesClone.length);
		nextShape = shapesClone[randomNum];
	}
	
	public void holdShape() {
		if (shiftClicked == false) {
			int index = Arrays.asList(shapes).indexOf(currentShape);
			if(holdShape == null) {	
				holdShape = shapesClone[index];
				setCurrentShape();
			}
			else {
				int index2 = Arrays.asList(shapesClone).indexOf(holdShape);
				currentShape = shapes[index2];
				holdShape = shapesClone[index];
				currentShape.reset();
			}
		}
		shiftClicked = true;
	}
	
	public void setCurrentShape() {
		currentShape = shapes[randomNum];
		getNextShape();
		currentShape.reset();
		checkGameWin();
		checkOverGame();
	}
	
	private void checkOverGame() {
		int[][] coords = currentShape.getCoords();
		for(int row = 0; row < coords.length;row++) {
			for(int col = 0; col < coords[0].length;col++) {
				if(coords[row][col] != 0) {
					if(board[row + currentShape.getY()][col + currentShape.getX()] != null) {
						state = STATE_GAME_OVER;
					}
				}

			}
		}
	}
	
	private void checkGameWin() {
		if(point>=40) {
			point = 40;
			state = STATE_GAME_OVER;
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		for(int i =currentShape.getX();i<currentShape.getX()+currentShape.getCoords()[0].length;i++) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(i * BLOCK_SIZE, 0, BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
			g.fillRect(i * BLOCK_SIZE, 0, BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
		}
		currentShape.render(g);
		currentShape.drawShadowShape(g);
		
		for(int row = 0; row <board.length;row++) {
			for(int col = 0; col <board[row].length;col++) {
				
				if (board[row][col] != null) {
					g.setColor(board[row][col]);
					g.fillRect(col* BLOCK_SIZE, row * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
				}
			}
		}
		
		//draw board
		g.setColor(Color.white);
		for(int row = 0; row < BOARD_HEIGHT; row++) {
			g.drawLine(0, BLOCK_SIZE * row, BLOCK_SIZE * BOARD_WIDTH, BLOCK_SIZE * row);
		}
		
		for(int col = 0; col < BOARD_WIDTH+1; col++) {
			g.drawLine(col * BLOCK_SIZE, 0, col * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
		}
		
		g.setColor(Color.cyan);
		g.drawLine(currentShape.getX() * BLOCK_SIZE, 0, currentShape.getX() * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
		g.drawLine((currentShape.getX()+currentShape.getCoords()[0].length) * BLOCK_SIZE, 0, (currentShape.getX()+currentShape.getCoords()[0].length) * BLOCK_SIZE, BLOCK_SIZE * BOARD_HEIGHT);
				

		
		Font font = new Font("Times New Roman", Font.PLAIN, 20);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString("-----------------", 310,80);
		g.setColor(Color.red);
		g.drawString("WELCOME", 315,100);
		g.setColor(Color.blue);
		g.drawString("TO", 350,130);
		g.setColor(Color.green);
		g.drawString("TETRIS", 335,160);
		g.setColor(Color.white);
		g.drawString("-----------------", 310,180);
		font = new Font("Times New Roman", Font.PLAIN, 17);
		g.setFont(font);
		g.drawString("Lines cleared: "+point, 310,250);
		// Display the elapsed time
	    g.drawString("Time: " + (elapsedTime / 1000) + "s", 310, 280);
		font = new Font("Times New Roman", Font.PLAIN, 20);
		g.setFont(font);
		g.drawString("---------------", 310,360);
		g.drawString("Hold:",315,380);
		g.drawString("---------------", 310,465);
		if(holdShape != null) {
			holdShape.drawHoldShape(g);
		}
		g.setColor(Color.white);
		g.drawString("Next piece:",315,480);
		g.drawString("---------------", 310,570);
		
		
		nextShape.drawNextShape(g);

		
		
		if(state == STATE_GAME_OVER) {
			font = new Font("Arial", Font.PLAIN, 50);
			g.setFont(font);
			g.setColor(Color.black);
			g.fillRect(0, 0, WindowGame.WIDTH, WindowGame.HEIGHT);
			if (point<40) {
				g.setColor(Color.red);
				g.drawString("GAME OVER", 50, 200);
			}
			else {
				g.setColor(Color.green);
				g.drawString("YOU WON!", 75, 200);
				font = new Font("Arial", Font.PLAIN, 20);
				g.setFont(font);
				g.setColor(Color.red);
				g.drawString("Time taken(40lines): "+elapsedTime/1000+" Secs", 80, 300);
				
			}
			
			font = new Font("Arial", Font.PLAIN, 20);
			g.setFont(font);
			g.setColor(Color.white);
			g.drawString("Press Enter to Start Again!", 80, 400);
		
		}
		
	}
	
	public Color[][] getBoard(){
		return board;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			currentShape.speedUp();
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			currentShape.moveRight();
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			currentShape.moveLeft();
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			currentShape.rotateShape();
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			currentShape.instantLocate();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			holdShape();
		}
		
		if (state == STATE_GAME_OVER) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(state == STATE_GAME_PLAY){
				state = STATE_GAME_PAUSE;
				
			}
			else if (state == STATE_GAME_PAUSE) {
				state = STATE_GAME_PLAY;
			}
			else {
				for(int row = 0; row <board.length;row++) {
					for(int col = 0; col <board[row].length;col++) {
						board[row][col] = null;
						
					}
				}
				setCurrentShape();
				holdShape = null;
				elapsedTime = 0;
				point = 0;
				startTime = System.currentTimeMillis();
				state = STATE_GAME_PLAY;
			}
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			currentShape.speedDown();
		}
		
	}
	

}
