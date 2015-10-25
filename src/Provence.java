public class Provence {
	int x,y;	
	int Number;
	float Resources;
	float Supplies;
	float Population;
	int Connections[]= new int[100];
	float[][] BorderPts= new float[360][5];
	private int curConnection = 0;
	
	public Provence(int bpmax){
		BorderPts= new float[bpmax][5];
	}
	public void addConnection(int addNum) {
		boolean f=false;
		for (int ii = 0;ii<100;ii++){
			if (Connections[ii]==addNum){f = true;}
		}
		if (f==false)		{
			Connections[curConnection]= addNum;
			curConnection=curConnection+1;
		}
	}
	
	public void recalcCenter(){
		float xt=0,yt=0;
		for(int pt = 0;pt<BorderPts.length;pt=pt+1){
			xt=xt+BorderPts[pt][0];
			yt=yt+BorderPts[pt][1];
		}
		int xn = (int) (xt/BorderPts.length);
		int yn = (int) (yt/BorderPts.length);
		float d = (float) Math.hypot(xn-x, yn-y);
		System.out.println("distance=" +String.valueOf(d));
		x= xn;
		y= yn;
		
	}
}
