import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DrawingPanel extends JPanel{
	
	private int width;
	private int height;
	private int boardSize;
	private int sizeX;
	private int sizeY;

	private BufferedImage knight;
	
	private int cnt = 0;
	private int[][] chessboard;
	private boolean[][] jumpedOn;
	private int[][] jumps;
	private int currentPos = 0;

	// beinhaltet die möglichen Spruenge
	private final int[][] KNIGHT_JUMPS = {
		{+1,-2},
		{+2,-1},
		{+2,+1},
		{+1,+2},
		{-1,+2},
		{-2,+1},
		{-2,-1},
		{-1,-2}
	};

	private boolean backtracking = false;

	//Attribute fuer Animation
	private Timer tour;
	private ActionListener animation = new ActionListener(){
		public void actionPerformed(ActionEvent e){
			if(currentPos >= sizeX*sizeY -1){
				Timer t = (Timer) e.getSource();
				t.stop();//Animation stoppt
			}
			currentPos++;
			repaint();
		}
	};

	/* Knostruktor */
	public DrawingPanel(int width, int height, int sizeX, int sizeY){
		super();
		this.width = width;
		this.height = height;
		this.boardSize = sizeX;
		this.sizeX = sizeX;
		this.sizeY = sizeY;

		chessboard = new int[sizeX][sizeY];
		jumps = new int[sizeX*sizeY][2];
		jumpedOn = new boolean[sizeX][sizeY];

		tour = new Timer(500, animation);

		loadImage("/img/black.png");
	}
		
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		drawChessboard(g);
		if(backtracking){
			drawBacktracking(g);
		}
		drawMoves(g);
	}
	
	// Methode zum Zeichnen eines Schachbretts
	public void drawChessboard(Graphics g){
		
		//sorgt dafuer, dass bei fenster resize keine weissen stellen zu sehen sind
		g.setColor(new Color(118, 150, 86));
		g.fillRect(0,0,getWidth(),getHeight());

		for (int i = 0; i < sizeX; i++){
		//zeichnet checkerboard muster
			for (int j = 0; j < sizeY; j++){
				if((i+j)%2==1){
					g.setColor(new Color(118, 150, 86));
				}else {
					g.setColor(new Color(238, 238, 210));
				}
				g.fillRect(width/sizeX*i,height/sizeY*j,width/sizeX,height/sizeY);
			}
		}
	}

	// Methode zum zeichnen der Wege, die ausprobiert werden
	public void drawBacktracking(Graphics g){
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));
		g2.setColor(Color.DARK_GRAY);
		for(int i = 0; i<cnt-1; i++){
			if(jumps[i][0] != -1 && jumps[i+1][0] != -1){
				int fromX = width/sizeX*jumps[i][0] + width/(sizeX*2);
				int fromY = height/sizeY*jumps[i][1] + width/(sizeX*2);
				int toX = width/sizeX*jumps[i+1][0] + width/(sizeX*2);
				int toY = height/sizeY*jumps[i+1][1] + width/(sizeX*2);
				g2.drawLine(fromX, fromY, toX, toY);
			}
		}
	}
	
	// Methode zum Zeichnen der Zuege
	public void drawMoves(Graphics g){
		for (int i = 0; i < sizeX; i++){
			for (int j = 0; j < sizeY; j++){
				if(currentPos == chessboard[i][j] && currentPos!=0){
					g.drawImage(knight, width/sizeX*i,height/sizeY*j,knight.getWidth() / (22 * sizeX / 8) * width/800,knight.getHeight() / (22 * sizeX/8 ) * width/800, null);
					jumpedOn[i][j]=true;
				}
				if((i+j)%2 == 0){
					g.setColor(new Color(118,150,86));
				}else{
					g.setColor(new Color(238,238,210));
				}
				if(chessboard[i][j]!=0 && (jumpedOn[i][j] || backtracking)){
					g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 16*width/800));
					g.drawString(String.valueOf(chessboard[i][j]), width/sizeX*i, height/sizeY*j + 15*width/800);
				}
			}
		}
	}
	
	// Rekursive Methode, die alle moeglichen Wege des Springers ausprobiert
	public boolean jump(int x, int y){
		
		cnt++;

		int nextX;
		int nextY; 
		
		/* Anker */
		if (sizeX * sizeY == cnt){ 
			chessboard[x][y] = cnt;
				
			jumps[cnt-1][0] = x;
			jumps[cnt-1][1] = y;

			return true;
		}
		
		for(int[] knightJump : KNIGHT_JUMPS){
			nextX = x + knightJump[0];
			nextY = y + knightJump[1];
			if (isFree(nextX, nextY)){
				chessboard[x][y] = cnt;
				
				jumps[cnt-1][0] = x;
				jumps[cnt-1][1] = y;

				if (jump(nextX, nextY)){
					return true;
				}else{
					chessboard[nextX][nextY] = 0;
					
					jumps[cnt][0] = -1;
					jumps[cnt][1] = -1;

					cnt--;
				}
			}
		}
		if(backtracking){
			paintImmediately(0,0,getWidth(),getHeight());
		}
		return false; // bei falschem Weg
	}

	// Gibt true aus, wenn Feld frei ist und sich auf Schachbrett befindet
	public boolean isFree(int x, int y){
		return (x >= 0 && x < chessboard.length && y >= 0 && y < chessboard[0].length &&chessboard[x][y] == 0);
	}
	
	// Startet Animation mit Springer
	public void startTour(){
		tour.start();
	}
	
	public void loadImage(String st){
		try{
			knight = ImageIO.read(getClass().getResourceAsStream(st));
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void reset(){
		cnt = 0;
		currentPos = 0;
		chessboard = new int[sizeX][sizeY];
		jumps = new int[sizeX*sizeY][2];
		jumpedOn = new boolean[sizeX][sizeY];
	}
		
	/* Getter und Setter */

	public void setWidth(int width){
		this.width = width;
	}

	public void setHeight(int height){
		this.height = height;
	}

	public void setBoardSize(int boardSize){
		this.boardSize = boardSize;
		chessboard = new int[boardSize][boardSize];
		jumps = new int[boardSize*boardSize][2];
	}

	public void setSizeX(int sizeX){
		this.sizeX = sizeX;
		chessboard = new int[sizeX][sizeY];
		jumps = new int[sizeX*sizeY][2];
	}
	
	public void setSizeY(int sizeY){
		this.sizeY = sizeY;
		chessboard = new int[sizeX][sizeY];
		jumps = new int[sizeX*sizeY][2];
	}


	public void setBacktracking(boolean backtracking){
		this.backtracking = backtracking;
	}
}
