import java.awt.*;
public class utils {
	  public static int getScreenWidthPx(){
		  Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		  Dimension dim = toolkit.getScreenSize();
		  return +dim.width;
	  }
	  
	  public static int getScreenHeightPx(){
		  Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		  Dimension dim = toolkit.getScreenSize();
		  return + dim.height;
	  }

}
