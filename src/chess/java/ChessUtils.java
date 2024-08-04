package chess.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import chess.java.Piece.Type;

public class ChessUtils {
	private static String notations = "QKRNBPqkrnbp";
	public static class PieceComparator implements Comparator<Piece>{		
		@Override
		public int compare(Piece o1, Piece o2) {
			if(o1.py==o2.py){
				return o1.px < o2.px ? -1:1;
			}
			else return o1.py < o2.py ? -1:1;
			
		}
	}
	

	
	public static String getPositionId(List<Piece> pieces){
		Piece[] p = (Piece[])pieces.toArray();
		Arrays.sort(p,new PieceComparator());
		StringBuffer sb = new StringBuffer();
		for(int i = 0 ; i < p.length ; i ++) {
			sb.append(p[i].px);
			sb.append(p[i].py);
			sb.append(p[i].t.value);
		}
		return sb.toString();
	}
	

	public static String getPositionFEN(List<Piece> pieces){
		Piece[] p = (Piece[])pieces.toArray();
		Arrays.sort(p,new PieceComparator());
		StringBuffer sb = new StringBuffer();
		int lpx = 0,lpy = 0;
		for(int i = 0 ; i < p.length ; i ++) {
			Piece e = p[i];
			while(lpy != e.py) {
				if(lpx < 7)
					sb.append(8-lpx);
				lpx = 0;
				lpy++;
			}
			if(lpx!=e.px){
				sb.append(e.px-lpx);
				lpx = e.px;
			}
			sb.append(notations.charAt(e.t.value + (e.isBlack ? 6:0) ));
			lpx++;
		}
		return sb.toString();
	}
	
	public static boolean isCheck(int px,int py,boolean isBlack,List<Piece> pieces) {
		int nx,ny;
		Piece p;
		
		//checks by rook or queen
		for(int i = -1; i <2;i+=2) {
			nx = px+i;
			while(isValidPosition(nx,py)) {
				p = findPiece(nx,py,isBlack,pieces);
				if(p!=null) {
					if(isBlack!=p.isBlack && (p.t==Type.ROOK || p.t==Type.QUEEN)) {
						return true;
					}
					break;
				}
				nx+=i;
			}
			

			ny = py+i;
			while(isValidPosition(px,ny)) {
				p = findPiece(px,ny,isBlack,pieces);
				if(p!=null && !(p.t== Type.KING && p.isBlack==isBlack)) {
					if(isBlack!=p.isBlack && (p.t==Type.ROOK || p.t==Type.QUEEN)) {
						return true;
					}
					break;
				}
				ny+=i;
			}
		}
		
		//checks by bishop and queen
		for(int x = -1; x <2;x+=2) {
			for(int y = -1; y <2;y+=2) {
				nx = px+x;
				ny = py+y;
				
				while(isValidPosition(nx,ny)) {
					p = findPiece(nx,ny,isBlack,pieces);
					if(p!=null) {
						if(isBlack!=p.isBlack && (p.t==Type.BISHOP || p.t==Type.QUEEN)) {
							return true;
						}
						break;
					}
					nx+=x;
					ny+=y;
				}
			}
		}
		
		//checks by knight
		for(int x = -1; x <2;x+=2) {
			for(int y = -1; y <2;y+=2) {
				nx = px + x;
				ny = py + y*2;
				if(isValidPosition(nx,ny)) {
					p = findPiece(nx,ny,isBlack,pieces);
					if(p!=null && p.t == Type.KNIGHT && p.isBlack!=isBlack)
						return true;
				}
				
				nx = px + x*2;
				ny = py + y;				
				if(isValidPosition(nx,ny)) {
					p = findPiece(nx,ny,isBlack,pieces);
					if(p!=null && p.t == Type.KNIGHT && p.isBlack!=isBlack)
						return true;
				}
			}
		}
		
		//pawn checks
		ny = py +( isBlack? 1:-1);
		for(int i = -1; i <2;i+=2) {
			nx = px+i;
			if(isValidPosition(nx,ny)) {
				p = findPiece(nx,ny,isBlack,pieces);
				if(p!=null && p.t == Type.PAWN && p.isBlack!=isBlack) {
					return true;
				}
			}
		}
		
		
		return false;
	}
	
	public static boolean isValidPosition(int px,int py){
		return px >=0 && px <=7&&py >=0 && py <=7;
	}

	public static Piece findPiece(int px,int py, List<Piece> list){
		for(Piece  p : list) {
			if(p.px==px && p.py == py)
				return p;
		}
		return null;
	}

	public static Piece findPiece(int px,int py,boolean pref, List<Piece> list){
		Piece  op = null;
		for(Piece  p : list) {
			if(p.px==px && p.py == py) {
				if(p.isBlack==pref)
					return p;
				op = p ;
			}
		}
		return op;
	}
	public static List<int[]> getRookMoves(int px,int py,boolean isBlack,List<Piece> pieces){
		List<int[]> l = new ArrayList<int[]>();
		int nx,ny;
		Piece p;
		
		for(int i = -1; i <2;i+=2) {
			nx = px+i;
			while(isValidPosition(nx,py)) {
				p = findPiece(nx,py,pieces);
				if(p==null || p.isBlack!=isBlack)
					l.add(new int[] {nx,py});
				if(p!=null)
					break;
				nx+=i;
			}
			

			ny = py+i;
			while(isValidPosition(px,ny)) {
				p = findPiece(px,ny,pieces);
				if(p==null || p.isBlack!=isBlack)
					l.add(new int[] {px,ny});
				if(p!=null)
					break;
				ny+=i;
			}
		}
		
		return l;
	}

	public static List<int[]> getBishopMoves(int px,int py,boolean isBlack,List<Piece> pieces){
		List<int[]> l = new ArrayList<int[]>();
		int nx,ny;
		Piece p;
		for(int x = -1; x <2;x+=2) {
			for(int y = -1; y <2;y+=2) {
				nx = px+x;
				ny = py+y;
				
				while(isValidPosition(nx,ny)) {
					p = findPiece(nx,ny,pieces);
					if(p==null || p.isBlack!=isBlack)
						l.add(new int[] {nx,ny});
					if(p!=null)
						break;
					nx+=x;
					ny+=y;
				}
			}
		}
		return l;
	}
	
	public static List<int[]> getKnightMoves(int px,int py,boolean isBlack,List<Piece> pieces){
		List<int[]> l = new ArrayList<int[]>();
		int nx,ny;
		Piece p;
		for(int x = -1; x <2;x+=2) {
			for(int y = -1; y <2;y+=2) {
				nx = px + x;
				ny = py + y*2;
				if(isValidPosition(nx,ny)) {
					p = findPiece(nx,ny,pieces);
					if(p==null || p.isBlack!=isBlack)
						l.add(new int[] {nx,ny});
				}
				
				nx = px + x*2;
				ny = py + y;				
				if(isValidPosition(nx,ny)) {
					p = findPiece(nx,ny,pieces);
					if(p==null || p.isBlack!=isBlack)
						l.add(new int[] {nx,ny});
				}
			}
		}
		return l;
	}
	
	public static List<int[]> getQueenMoves(int px,int py,boolean isBlack,List<Piece> pieces){
		List<int[]> l = new ArrayList<int[]>();
		l.addAll(getBishopMoves(px,py,isBlack,pieces));
		l.addAll(getRookMoves(px,py,isBlack,pieces));
		return l;
	}

	public static List<int[]> getPawnMoves(int px,int py,boolean isBlack,List<Piece> pieces){
		List<int[]> l = new ArrayList<int[]>();
		int ny = py+ (isBlack ?1:-1);
		if(findPiece(px,ny,pieces)==null) {
			l.add(new int[] {px,ny});
			if(py==1 && isBlack && findPiece(px,ny+1,pieces)==null)
				l.add(new int[] {px,ny+1});
			if(py==6 && (!isBlack) && findPiece(px,ny-1,pieces)==null)
				l.add(new int[] {px,ny-1});
		}
		for(int nx = px-1;nx <=px+1;nx+=2){
			Piece p = findPiece(nx,ny,pieces);
			if(p!=null &&p.isBlack!=isBlack)
				l.add(new int[] {nx,ny});
		}
		return l;
	}
	
	public static List<int[]> getKingMoves(int px,int py,boolean isBlack,List<Piece> pieces){
		List<int[]> l = new ArrayList<int[]>();
		int nx,ny;
		for(int ix = -1;ix<2;ix++) {
			for(int iy = -1;iy<2;iy++) {
				if(ix!=0 || iy!=0) {
					nx=px+ix;
					ny=py+iy;
					Piece p = findPiece(nx,ny,pieces);
					if(isValidPosition(nx,ny) && (p==null || p.isBlack!=isBlack))
						l.add(new int[]{nx,ny});
				}
			}
		}
		return l;
	}
	
	public static List<int[]> getValidMoves(Piece p,List<Piece> pieces){
		switch(p.t) {
		case BISHOP:
			return getBishopMoves(p.px,p.py,p.isBlack,pieces);
		case KING:
			return getKingMoves(p.px,p.py,p.isBlack,pieces);
		case KNIGHT:
			return getKnightMoves(p.px,p.py,p.isBlack,pieces);
		case PAWN:
			return getPawnMoves(p.px,p.py,p.isBlack,pieces);
		case QUEEN:
			return getQueenMoves(p.px,p.py,p.isBlack,pieces);
		case ROOK:
			return getRookMoves(p.px,p.py,p.isBlack,pieces);
		
		}
		return new ArrayList<int[]>();
	}
}
