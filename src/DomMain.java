import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.text.html.ImageView;

import com.ntu.way2fungames.*;

public class DomMain { 
	
    private static JFrame frame_main;
	private static MyDomMapView iv;
	MyDomMap MyLand;
	private String testFileString = "C:\\Users\\nupch_000\\AppData\\Roaming\\Dominions4\\maps\\what";
	
	public void pullThePlug() {
		frame_main.dispose();
	}
	
	public static void main(String[] args) {		
		final DomMain selfobject = new DomMain();
			
		
		ActionListener go_pressed= new ActionListener() {
				
		@Override public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand()=="go"){System.out.println("make new fractal");selfobject.makeNewMap();}
				if (e.getActionCommand()=="rain"){selfobject.rain();}					
				if (e.getActionCommand()=="exit"){selfobject.pullThePlug();}					
				}};;;
				
		ItemListener myItemListener=new ItemListener() {
					
					@Override
					public void itemStateChanged(ItemEvent arg0) {
						Checkbox cb=(Checkbox) arg0.getSource();
						String o="";
						
						if (cb.getName()=="terrian"){							
							if(cb.getState()==true) o=o+"1"; 
							if(cb.getState()==false) o=o+"0";
						}else{o=o+"x";}
						
						if (cb.getName()=="borders"){							
							if(cb.getState()==true) o=o+"1"; 
							if(cb.getState()==false) o=o+"0";
						}else{o=o+"x";}						
						
						if (cb.getName()=="extra"){							
							if(cb.getState()==true) o=o+"1"; 
							if(cb.getState()==false) o=o+"0";
						}else{o=o+"x";}
						
						selfobject.layers(o);
						
					}
				};
		
		
		frame_main = new JFrame();
		frame_main.setUndecorated(true);
		frame_main.setExtendedState(JFrame.MAXIMIZED_BOTH);		
		frame_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame_main.setVisible(true);
		
		frame_main.setLayout(null);    	
	    //frame_main.setSize(fsx, fsy);            
	    frame_main.setVisible(true);
		
		int fsx  = (int) frame_main.getContentPane().getSize().getWidth();
		int fsy  = (int) frame_main.getContentPane().getSize().getHeight();
		int ivsy = (int) (fsy);
		int ivsx = (int) (fsx);
		if (ivsx>ivsy){ivsx = ivsy;}
		
		Button bExit = new Button();
		bExit.setLocation(ivsx+80, 1);
		bExit.setSize(35, 35);
		bExit.setActionCommand("exit");
		bExit.setLabel("Exit");
		bExit.addActionListener(go_pressed);
		
		Button b_go = new Button();
	    b_go.setLocation(ivsx, 1);
	    b_go.setSize(35, 35);
	    b_go.setActionCommand("go");
	    b_go.setLabel("GO");
	    b_go.addActionListener(go_pressed);
	    
		Button b_rain = new Button();
		b_rain.setLocation(ivsx+40, 1);
		b_rain.setSize(35, 35);
		b_rain.setActionCommand("rain");
	    b_rain.setLabel("rain");
	    b_rain.addActionListener(go_pressed);

//		Button b_repaint = new Button();
//		b_repaint.setLocation(ivsx+120, 1);
//		b_repaint.setSize(35, 35);
//		b_repaint.setActionCommand("layers");
//		b_repaint.setLabel("Refresh");
//		b_repaint.addActionListener(go_pressed);

	    Checkbox cb_terrian = new Checkbox("borders");
	    cb_terrian.setName("terrian");
	    cb_terrian.setLocation(ivsx+120, 1);
	    cb_terrian.setSize(35, 35);	    
	    cb_terrian.setLabel("Terrian");
	    cb_terrian.addItemListener(myItemListener);
	    
	    Checkbox cb_borders = new Checkbox("borders");
	    cb_borders.setName("borders");
	    cb_borders.setLocation(ivsx+160, 1);
	    cb_borders.setSize(35, 35);	    
	    cb_borders.setLabel("Borders");
	    cb_borders.addItemListener(myItemListener);
	    
	    Checkbox cb_extra = new Checkbox("borders");
	    cb_extra.setName("extra");
	    cb_extra.setLocation(ivsx+200, 1);
	    cb_extra.setSize(35, 35);	    
	    cb_extra.setLabel("Extra");
	    cb_extra.addItemListener(myItemListener);

	    
	    
	    iv = new MyDomMapView();
		iv.setLocation(0,0);
		iv.setSize(ivsx, ivsy);
		iv.setBackground(Color.black);
		iv.setVisible(true);
	    
	    frame_main.getContentPane().add(b_go);
	    frame_main.getContentPane().add(b_rain);
	    frame_main.getContentPane().add(bExit);
	    frame_main.getContentPane().add(iv);
	    frame_main.getContentPane().add(cb_borders);
	    frame_main.getContentPane().add(cb_extra);
	    frame_main.getContentPane().add(cb_terrian);
	    //frame_main.getContentPane().add(b_repaint);
	    
    }

	protected void layers(String layers) {
		String ss1 = layers.substring(0,1) ;
		String ss2 = layers.substring(1,2) ;
		String ss3 = layers.substring(2,3) ;
		
		if (ss1.contains("0") ){iv.showTerrian=false;}
		if (ss1.contains("1") ){iv.showTerrian=true; }
		if (ss2.contains("0") ){iv.showBorders=false;}
		if (ss2.contains("1") ){iv.showBorders=true; }
		if (ss3.contains("0") ){iv.showExtra  =false;}
		if (ss3.contains("1") ){iv.showExtra  =true; }


		
		iv.repaint();
		
	}

	final void rain() {
		for (int rep =0;rep<100;rep=rep+1){
		MyLand.morphRain(1);
		iv.iTerrain = MyLand.img;
		iv.iBorders= MyLand.iBorders;
		
		iv.repaint();
		}
	}

	 void makeNewMap() {
		
		MyLand = new MyDomMap(1024,100);
		iv.MyMap= MyLand;
		//MyLand.img = null;
		iv.iTerrain = MyLand.img;
		iv.iBorders= MyLand.iBorders;
		iv.iExtra= MyLand.iWeather;
		iv.repaint();
		try {
			MyLand.Save(testFileString);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
