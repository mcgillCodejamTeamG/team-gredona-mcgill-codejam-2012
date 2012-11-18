

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import javax.swing.JFrame;
import javax.swing.JPanel;

public class Controller implements KeyListener{
	
	/*
	public static void main(String args[]){
	
		JPanel screen = new JPanel(); 
		screen.addKeyListener(new TestClient()); 
		
		final int width = 480;
		final int height = 320; 
		
		screen.setLayout(new BorderLayout()); 
		screen.setSize(width, height); 
				
		JFrame window = new JFrame("Code Jam 2012");
	    window.setContentPane(screen);
	
	    window.setSize(screen.getSize()); 
	    window.setLocation(200,25);
	    window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    window.setMinimumSize(new Dimension(width/2, height/2)); 
	    window.setResizable(true); 
	    window.setVisible(true);
	    
	   /**
	    * Screen will always be width by height no matter the dimensions of the window on a specific system.
	    
	    int wDiff = window.getWidth()-screen.getWidth(); //difference in widths
	    int hDiff = window.getHeight()-screen.getHeight(); // difference in heights
	    window.setSize(window.getWidth()+wDiff, window.getHeight()+hDiff); 
	    screen.setSize(width, height);  
	    screen.requestFocus(); 
	}*/ 

	private Client client;
	private GraphData graphData; 
	private TableData tableData; 
	
	public Controller(){
		client = new Client(); 
		client.connect(); 
		
		
		
	}

	
	public void keyReleased(KeyEvent arg0) {
		client.send("H"); 
	}

	
	public void keyTyped(KeyEvent arg0) {}
	public void keyPressed(KeyEvent arg0) {}	

}
