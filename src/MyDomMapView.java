import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;


public class MyDomMapView extends JComponent {
     	java.io.File fn;
     	public Image iTerrain;
     	public Image iBorders;
     	public Image iExtra;
     	
     	
     	public MyDomMap MyMap;
     	BufferedImage bi;
		private Provence selectedProvence;
		private float sx;
		private float sy;
		public boolean showBorders;
		public boolean showExtra;
		public boolean showTerrian=true;
     	
     	
     	
     	public MyDomMapView(){
     		MouseListener pressed= new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent e) {
						// TODO Auto-generated method stub
						pressed(e);	
					}

	
					@Override
					public void mouseEntered(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseExited(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mousePressed(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseReleased(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

				
 			};
 			
 			addMouseListener(pressed);
     	}
     	private void pressed(MouseEvent e){
     		
     		if (MyMap != null){
     			selectedProvence = MyMap.getProvAtXY((int) (e.getX()/sx), (int) (e.getY()/sy), MyMap.Provences);
     			}
     		
     		this.repaint();
   		}
     	
     	     	
     	public void paint(Graphics g) {
             if(MyMap!=null){ 
     	
            
     		 sx = ((float)getWidth ()/(float)MyMap.size);
        	 sy = ((float)getHeight()/(float)MyMap.size);
             }

        	 Graphics2D g2d = (Graphics2D)g;

             int lw = getWidth();
             int lh = getHeight();

             g2d.setBackground(Color.gray );
             g2d.clearRect(0, 0, lw, lh);

             if (showTerrian){ g2d.drawImage(iTerrain, 0, 0,getWidth(),getHeight(), this);}
             if (showBorders){ g2d.drawImage(iBorders, 0, 0,getWidth(),getHeight(), this);}
             if (showExtra)  { g2d.drawImage(iExtra  , 0, 0,getWidth(),getHeight(), this);}
             
             if (selectedProvence != null){
            
            	 g2d.setColor(Color.WHITE);
            	 g2d.fillOval(((int)(selectedProvence.x*sx)-5),(int)((selectedProvence.y*sy)-5),10,10);
            	 
             if (selectedProvence.Connections!= null){
             	for(int pt = 0;pt<100 ;pt=pt+1){
             		if (selectedProvence.Connections[pt]!=0){
             			Provence conProvence = MyMap.Provences[selectedProvence.Connections[pt]-1];
             			g2d.drawLine((int)(selectedProvence.x*sx), (int)(selectedProvence.y*sy), (int)(conProvence.x*sx),  (int)(conProvence.y*sy));
             			
             		}

             	}            		 
            	 }
             }
             
             g2d.dispose();
          
         }
        
}
