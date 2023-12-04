package tetris;

import java.awt.Color;
import java.awt.Graphics;

public class Shape {
	private int x = 4, y = 0;
	private int normal = 600;
	private int fast = 50;
	private int delayTimeForMovement = normal;
	private long beginTime;
	private int shadowY = y;
	private int deltaX = 0;
	private boolean collision = false;
	
	private int[][] coords;
	private Board board;
	private Color color;
	
	public Shape(int[][] coords, Board board, Color color) {
		this.coords = coords;
		this.board = board;
		this.color = color;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
		if (y>19) {
			this.y = 19;
		}
	}
	
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int[][] getCoords(){
		return coords;
	}
	
	public void reset() {
		setX(4);
		setY(0);
		collision = false;
		
	}
	public void update() {
		if(collision) {
			Board.shiftClicked = false;
			// fill color for board
			for(int row = 0; row < coords.length; row++) {
				for(int col = 0; col < coords[0].length; col++) {
					if(coords[row][col] != 0) {
						board.getBoard()[y + row][x +col] = color;
					}
				}
			}
			checkLine();
			// set current shape
			board.setCurrentShape();
			delayTimeForMovement = normal;
			return;
			
		}
		// check boundary
		boolean moveX = true;
		if((x + deltaX + coords[0].length <= Board.BOARD_WIDTH) && (x +deltaX >=0)) {
			for(int row = 0; row <coords.length;row++) {
				for(int col = 0; col < coords[row].length; col++) {
					if(coords[row][col]!=0) {
						if(board.getBoard()[y+row][x+deltaX + col] != null) {
							moveX = false;
						}
					}
				}
			}
			if(moveX) {
				x+= deltaX;
			}
			
		}
		deltaX = 0;
		
		if(System.currentTimeMillis()- beginTime > delayTimeForMovement) {
			if(!(y + 1 + coords.length > Board.BOARD_HEIGHT)) {
				for(int row = 0; row < coords.length; row++) {
					for(int col = 0; col <coords[row].length; col++) {
						if(coords[row][col] != 0) {
							if(board.getBoard()[y+1+ row][x + deltaX + col] != null) {
								collision = true;
							}
						}
					}
				}
				
				if(!collision) {
					y++;
				}
				
			}
			else {
				collision = true;
			}
			
			beginTime = System.currentTimeMillis();
		}
	}
	
	public void checkLine() {
		int bottomLine = board.getBoard().length - 1;
		for(int topLine = board.getBoard().length-1; topLine >0; topLine --) {
			int count = 0;
			for(int col = 0; col <board.getBoard()[0].length; col++) {
				if(board.getBoard()[topLine][col] != null) {
					count++;
				}
				board.getBoard()[bottomLine][col] = board.getBoard()[topLine][col];			
			}
			if(count < board.getBoard()[0].length) {
				bottomLine--;
			}
			if(count == board.getBoard()[0].length) {
				Board.point++;
			}
		}
	}
	
	public void rotateShape() {
		int[][] rotatedShape = transposeMatrix(coords);
		reverseRows(rotatedShape);
		
		//check for right side and bottom
		if((x +rotatedShape[0].length >Board.BOARD_WIDTH || (y + rotatedShape.length > 20))) {
			return;
		}
		
		for(int row = 0; row < rotatedShape.length; row++) {
			for(int col = 0; col < rotatedShape[row].length; col++) {
				if (rotatedShape[row][col] != 0) {
					if(board.getBoard()[y+row][x+col]!=null) {
						return;
					}
				}
			}
		}
		coords = rotatedShape;
	}
	
	private int[][] transposeMatrix(int[][] matrix) {
		int[][] temp = new int[matrix[0].length][matrix.length];
		for(int row= 0; row < matrix.length; row++) {
			for(int col = 0; col < matrix[0].length; col++) {
				temp[col][row] = matrix[row][col];
			}
		}
		return temp;
	}
	
	private void reverseRows(int[][] matrix) {
		int middle = matrix.length/2;
		for(int row = 0; row < middle;row++) {
			int[] temp = matrix[row];
			matrix[row] = matrix[matrix.length - row - 1];
			matrix[matrix.length - row -1] = temp;
		}
	}
	
	public void render(Graphics g) {
		//draw piece
		for(int row = 0; row < coords.length; row++) {
			for(int col = 0; col < coords[0].length ; col++) {
				if(coords[row][col] != 0) {
					g.setColor(color);
					g.fillRect(col* Board.BLOCK_SIZE +x*Board.BLOCK_SIZE, row*Board.BLOCK_SIZE+ y*Board.BLOCK_SIZE, Board.BLOCK_SIZE, Board.BLOCK_SIZE);
				}
			}
		}
	}
	
	public void drawNextShape(Graphics g) {
		//draw piece
		for(int row = 0; row < coords.length; row++) {
			for(int col = 0; col < coords[0].length ; col++) {
				if(coords[row][col] != 0) {
					g.setColor(color);
					g.fillRect(320+col* (Board.BLOCK_SIZE-7), 500+ row* (Board.BLOCK_SIZE-7), (Board.BLOCK_SIZE-7), (Board.BLOCK_SIZE-7));
					g.setColor(Color.blue);
					g.drawRect(320+col* (Board.BLOCK_SIZE-7), 500+ row* (Board.BLOCK_SIZE-7), (Board.BLOCK_SIZE-7), (Board.BLOCK_SIZE-7));
				}
			}
		}
	}
	
	public void drawHoldShape(Graphics g) {
		//draw piece
		for(int row = 0; row < coords.length; row++) {
			for(int col = 0; col < coords[0].length ; col++) {
				if(coords[row][col] != 0) {
					g.setColor(color);
					g.fillRect(320+col* (Board.BLOCK_SIZE-7), 400+ row* (Board.BLOCK_SIZE-7), (Board.BLOCK_SIZE-7), (Board.BLOCK_SIZE-7));
					g.setColor(Color.blue);
					g.drawRect(320+col* (Board.BLOCK_SIZE-7), 400+ row* (Board.BLOCK_SIZE-7), (Board.BLOCK_SIZE-7), (Board.BLOCK_SIZE-7));
				}
			}
		}
	}
	
	public void speedUp() {
		delayTimeForMovement = fast;
	}
	
	public void instantLocate() {
		while(true) {
	        for (int row = 0; row < coords.length; row++) {
	            for (int col = 0; col < coords[row].length; col++) {
	                if (coords[row][col] == 1 && y+coords.length<=19) {
	                    if (board.getBoard()[y + 1+row][x + col] != null) {
	                    	collision = true;
	                    	return;
	                    }
	                } 
	                    
	            }
	        }
        	if(y+coords.length>=20) {
        		collision = true;
             	return;
             }   
	        y++;
	    }

	}
	
	public void generateShadowShape() {
		shadowY = 0;
		while(true) {
	        for (int row = 0; row < coords.length; row++) {
	            for (int col = 0; col < coords[row].length; col++) {
	                if (coords[row][col] == 1 && shadowY+coords.length<=19) {
	                    if (board.getBoard()[shadowY +row+1][x + col] != null) {
	                    	return;
	                    }
	                } 
	                    
	            }
	        }
        	if(shadowY+coords.length>=20) {
             	return;
             }   
	        shadowY++;
	    }
	}
	
	
	public void drawShadowShape(Graphics g) {
		for(int row = 0; row < coords.length; row++) {
			for(int col = 0; col < coords[0].length ; col++) {
				if(coords[row][col] != 0) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(col* Board.BLOCK_SIZE +x*Board.BLOCK_SIZE, row*Board.BLOCK_SIZE+ shadowY*Board.BLOCK_SIZE, Board.BLOCK_SIZE, Board.BLOCK_SIZE);
				}
			}
		}
	}
	public void speedDown() {
		delayTimeForMovement = normal;
	}
	
	public void moveRight() {
		deltaX = 1;
	}
	
	public void moveLeft() {
		deltaX = -1;
	}

}
