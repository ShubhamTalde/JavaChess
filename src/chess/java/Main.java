package chess.java;
import java.awt.Color;
import javax.swing.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame f=new JFrame();//creating instance of JFrame    
		        
		Board b = new Board(600,600,new Color(226, 227, 177),new Color(84, 84, 65));
		
		f.add(b);
		f.setSize(800,800);//400 width and 500 height  
		f.setLayout(null);//using no layout managers  
		f.setVisible(true);//making the frame visible 
	
	}

}
