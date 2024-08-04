package chess.java;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

import chess.java.Piece.Type;

public class Board extends JPanel implements MouseListener{
	private int width,height,cellWidth,cellHeight;
	
	private Image sprite;
	private Color primary,secondary;
	
	List<int[]> validMoves = new ArrayList<>();
	List<int[]> specialMoves = new ArrayList<>();
	List<Piece> pieces = new ArrayList<>();
	List<Piece> movedPieces = new ArrayList<>();
	Piece whiteKing, blackKing;
	Piece active, longMovedPawn;
	boolean isBlackTurn = false;
	
	public Board(int width , int height, Color primary, Color secondary){
		this.width = width;
		this.height = height;
		
		this.primary = primary;
		this.secondary = secondary;
		
		cellWidth = width/8;
		cellHeight = height/8;
		
		pieces = new ArrayList<Piece>();

		Piece.Type[] pTypes =  {
				Type.ROOK,Type.KNIGHT,Type.BISHOP,Type.QUEEN,Type.KING,Type.BISHOP,Type.KNIGHT,Type.ROOK
				
		};
		for(int i = 0 ; i < 8; i++) {
			pieces.add(new Piece(pTypes[i],false,i,7));
			pieces.add(new Piece(Piece.Type.PAWN,false,i,6));
			
			pieces.add(new Piece(Piece.Type.PAWN,true,i,1));
			pieces.add(new Piece(pTypes[i],true,i,0));
			
			if(pTypes[i]==Type.KING) {
				blackKing = pieces.get(i*4+3);
				whiteKing = pieces.get(i*4);
			}
		}
		
		sprite = new ImageIcon("res/pieces.png").getImage();
		setSize(width,height);
		
		this.addMouseListener(this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int px = e.getX()/cellWidth;
		int py = e.getY()/cellHeight;
		boardClick(px,py);
		revalidate();
		repaint();
		System.out.println(px+":"+py);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawGrid(Graphics g) {
		boolean last = false;
		for(int i =0;i < 8; i++) {
			for(int j =0;j < 8; j++) {
				last=!last;
				g.setColor( last ? primary: secondary );
				g.fillRect(i*cellWidth, j*cellHeight, cellWidth, cellHeight);
			}
			last = !last;
		}
	}
	
	private void drawPieces(Graphics g) {
//		System.out.println(pieces.toString());
		for(Piece p : pieces){
			int dx = p.px*cellWidth;
			int dy = p.py*cellHeight;
			g.drawImage(sprite,dx,dy,dx+cellWidth,dy+cellHeight, p.x1 , p.y1, p.x2,p.y2,p.observer);
		}
		if(active!=null) {
			g.setColor( new Color(5,5,200,60) );
			g.fillRect(active.px* cellWidth,active.py* cellHeight, cellWidth, cellHeight);
		}
	}
	
	private void drawMoves(Graphics g){
		for(int[] p : validMoves){
			g.setColor( new Color(100,100,250,60) );
			g.fillRect(p[0]* cellWidth,p[1]* cellHeight, cellWidth, cellHeight);
		}
		
		for(int[] p : specialMoves){
			g.setColor( new Color(100,100,250,60) );
			g.fillRect(p[0]* cellWidth,p[1]* cellHeight, cellWidth, cellHeight);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		drawGrid(g);
		drawPieces(g);
		if(active!=null){
			drawMoves(g);
		}
	}
	
	public void boardClick(int px,int py) {
		if(active!=null) {
			for(int[] p : validMoves){
				if(p[0]==px && p[1]==py) {
					pieces.remove(ChessUtils.findPiece(px, py, pieces));
					longMovedPawn = (active.t == Type.PAWN && Math.abs(active.py-py)==2)? active : null;
					active.px = px;
					active.py = py;
					if(active.t == Type.KING || active.t == Type.ROOK) {
						if(movedPieces.indexOf(active)==-1)
							movedPieces.add(active);
					}
					isBlackTurn = !isBlackTurn;
					break;
				}
			}
			
			for(int[] p : specialMoves) {
				if(p[0]==px && p[1]==py) {
					if(active.t==Type.PAWN) {
						pieces.remove(longMovedPawn);
					}else {
						movedPieces.add(active);
						Piece r = ChessUtils.findPiece(p[2], py, pieces);
						r.px = px+p[3];
						active.px = px;
					}
					active.px = px;
					active.py = py;
					longMovedPawn = null;
					isBlackTurn = !isBlackTurn;
					break;
				}
			}

			active = null;
			return;
		}
		
		for(Piece p : pieces) {
			if(px==p.px && py == p.py) {
				if(p.isBlack!=isBlackTurn)
					return;
				List<int[]> moves = ChessUtils.getValidMoves(p, pieces);
				
				validMoves.clear();
				specialMoves.clear();;
				int kx , ky;
				if(p.isBlack) {
					kx = blackKing.px;
					ky = blackKing.py;
				}else {
					kx = whiteKing.px;
					ky = whiteKing.py;
				}
				for(int[] m : moves) {
					p.px = m[0];
					p.py = m[1];
					if(p.t==Type.KING) {
						if(!ChessUtils.isCheck(p.px,p.py,p.isBlack,pieces))
							validMoves.add(m);
					}
					else {
						if(!ChessUtils.isCheck(kx,ky,p.isBlack,pieces))
							validMoves.add(m);
					}
				}
				p.px = px;
				p.py = py;
				
				//possible enpawn moves
				if(p.t==Type.PAWN && longMovedPawn!=null &&longMovedPawn.py == p.py && longMovedPawn.isBlack != p.isBlack ) {
					int dx = longMovedPawn.px -p.px;
					int dy = p.isBlack ? 1:-1;
					if(dx==1||dx==-1) {
						longMovedPawn.py-=dy;
						p.px += dx;
						p.py += dy;
						if(!ChessUtils.isCheck(kx,ky,p.isBlack,pieces))
							specialMoves.add( new int[]{p.px,p.py});

						longMovedPawn.py+=dy;
						p.px -= dx;
						p.py -= dy;
					}
					
				}
				
				if(p.t==Type.KING && !ChessUtils.isCheck(px, py, p.isBlack, pieces) && movedPieces.indexOf(p)==-1){
					int nx = p.px;
					while(nx>0) {
						nx--;
						Piece r = ChessUtils.findPiece(nx, py, pieces);
						if(r!=null) {
							if(r.t==Type.ROOK && movedPieces.indexOf(r)==-1){
								int rx = r.px;
								p.px--;
								if(ChessUtils.isCheck(p.px, p.py, p.isBlack, pieces))
									break;
								r.px = p.px;
								p.px--;
								if(!ChessUtils.isCheck(p.px, p.py, p.isBlack, pieces))
									specialMoves.add(new int[]{p.px,p.py,rx,1});
								r.px = rx;
							}
							break;	
						}
					}
					
					nx = px;
					while(nx<7) {
						nx++;
						Piece r = ChessUtils.findPiece(nx, py, pieces);
						if(r!=null) {
							if(r.t==Type.ROOK&& movedPieces.indexOf(r)==-1){
								int rx = r.px;
								p.px=px+1;
								if(ChessUtils.isCheck(p.px, p.py, p.isBlack, pieces))
									break;
								r.px = p.px;
								p.px++;
								if(!ChessUtils.isCheck(p.px, p.py, p.isBlack, pieces))
									specialMoves.add(new int[]{p.px,p.py,rx,-1});
								r.px = rx;
							}
							break;	
						}
					}
					
					p.px = px;
				}
				
				if(validMoves.size()>0)
					active = p;
				return;
			}
		}
	}
	

}
