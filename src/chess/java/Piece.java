package chess.java;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.util.Arrays;

public class Piece {
	enum Type{
		QUEEN(0),
		KING(1),
		ROOK(2),
		KNIGHT(3),
		BISHOP(4),
		PAWN(5) ;
		
		public int value;
		public String annotation;
		private Type(int value){
			this.value = value;
		}
		
		
	}
	
	boolean isBlack = false;
	Type t;
	int x1,x2,y1,y2,px,py;
	ImageObserver observer;
	
	public Piece(Type t,boolean isBlack,int px,int py) {
		this.isBlack = isBlack;
		this.px = px;
		this.py = py;
		
		observer = null;
		setType(t);
	}
	
	public String toString(){
		return  (isBlack ? "black " : "white ") + px+":"+py+ t +" ["+Arrays.toString( new int[] {x1,x2,y1,y2})+"]\n";
		
	}
	
	public void setType(Type t) {
		this.t = t;
		//360 * 120
		//6* 2
		x2 = 360/6;
		y2 = 120/2;
		
		x1 = x2*t.value;
		x2 +=x1;
		if(!isBlack){
			y1 = y2;
			y2 = 2*y2;
		}
	}
	
	
}
