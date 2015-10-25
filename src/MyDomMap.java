import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.ntu.way2fungames.*;



public class MyDomMap extends Land implements WeatherMap{
	Provence Provences[];
	int[][] ProvenceMapImageData;
	private int[][] cpts;
	private int[][] ProvenceMapDebugData;
	int numProv;
	Image iBorders;
	Image iWeather;
	
	private int curProv;
	private int curPoint;
	private float stbx;
	private float stby;
	private float[] pcent;
	private float mag1;
	private float mag2;
	private float mag3;
	private float len1;
	private float len2;
	private float adjby;

	private float[] newvert;
	private int[] inewvert;
	private float olde;
	private float newe;
	private float slope;
	private int istbx;
	private int istby;
	private float cpx;
	private float cpy;
	private int icpx;
	private int icpy;
	
	private float stiff=.1f;	
	private int maxgrowthturn=250;
	private int ptsperprov=(int) (360*.50f);
	private int minDist= 50;
	
	public MyDomMap(int nSize, int nNumProv) {
		super(nSize);
		System.out.println("super init done");
		
		iBorders = (Image) new BufferedImage(nSize, nSize, BufferedImage.TYPE_INT_ARGB); 
		iWeather=  (Image) new BufferedImage(nSize, nSize, BufferedImage.TYPE_INT_ARGB);
		
		numProv = nNumProv;
		ProvenceMapImageData = new int[size][size];
		generateProvences(nNumProv);
		createWeather();
		// TODO Auto-generated constructor stub
	}
	public Provence[] generateProvences(int nNumProv){
		BufferedImage bordersBi = (BufferedImage) iBorders;
		Graphics2D g2d = bordersBi.createGraphics();

		Provences = new Provence[nNumProv];
		//float minDist = (float) (size/(nNumProv/2));
		minDist= (int) ((50f/1024f)*512f);
		System.out.println("min provence center distance: "+String.valueOf(minDist));
		
		for (int pidx=0;pidx<nNumProv;pidx++){
			float nearistDistance=0;
			Provences[pidx] = new Provence(ptsperprov);
			
			if (pidx >0){
				while (getNearistProvCenterDistance(pidx,Provences) < (float) minDist){//choose random locations until one is far enough away from all other procences.
					Provences[pidx].x = (int) (Math.random()* size);
					Provences[pidx].y = (int) (Math.random()* size);
					//nearistDistance = getNearistProvCenterDistance(pidx,Provences);
				}
			}else{
				Provences[pidx].x = (int) (Math.random()* size);
				Provences[pidx].y = (int) (Math.random()* size);
			}
			
			//ProvenceMapImageData[Provences[pidx].x][Provences[pidx].y]= 255;	
		}
		
		
		expandProvenceBorders();
		for (int pidx=0;pidx<nNumProv;pidx++){
			g2d.setColor(Color.WHITE);
			g2d.drawRect(Provences[pidx].x, Provences[pidx].y, 0, 0);
		}
		g2d.dispose();

		System.out.println("addProvencesToMapImage");
		addProvencesToMapImage();
		return Provences;
	}
	/*
	 *base
	 *smooth
	 *elevation
	 *ocean
	 *boarders
	 */
	private void expandProvenceBorders() {
		
		System.out.println("placeProvCenters");
		
		placeProvCenters();
		
		System.out.println("move points");
		
		for (int expturn=0;expturn<maxgrowthturn;expturn++){//move pts
			for (curProv=0;curProv<numProv;curProv++){
				
				for (curPoint=0;curPoint<ptsperprov;curPoint=curPoint+2){
					movepoint();
				}
				
				for (curPoint=1;curPoint<ptsperprov;curPoint=curPoint+2){
					movepoint();
				}
			}
			
		}
		System.out.println("fillHoles");
		fillHoles();
		
		for (curProv=0;curProv<numProv;curProv++){
			Provences[curProv].recalcCenter();
		}
	}
	private void fillHoles() {
		int[][] lProvData =new int[size][size];
		for (int x=0;x<size;x++){for (int y=0;y<size;y++){
			lProvData[x][y] =  ProvenceMapImageData[x][y];	
		}}
		
		boolean foundhole = true;
		int expturn = 0;
		while (foundhole == true){

			
			foundhole = false;
			for (int x=0;x<size;x++){for (int y=0;y<size;y++){
				
				if (lProvData[x][y]==0){
					ProvenceMapImageData[x][y]=getmostsignificantneighbor(x,y,lProvData);
					foundhole = true;					
				}
				
			}}
			for (int x=0;x<size;x++){for (int y=0;y<size;y++){
				lProvData[x][y] =  ProvenceMapImageData[x][y];	
			}}

		}

	}
	private int getmostsignificantneighbor(int x, int y, int[][] lProvData) {
		int[][] neighbors = new int[5][2];
		
		//int curNeighbor = 0;
		boolean found = false;
		
			for (int xx=x-2;xx<=x+2;xx++){for (int yy=y-2;yy<=y+2;yy++){
				if ((xx>0)&(xx<size)){if ((yy>0)&(yy<size)){
				if (lProvData[xx][yy]!=0){
					
					found = false;
					for (int i=0;i<5;i++){//search for record
						if (neighbors[i][0]==lProvData[xx][yy]){
							neighbors[i][1]++;
							found = true;
						}
					}
					if (found == false){//no record found
						for (int i=0;i<5;i++){
							if (neighbors[i][0]==0){
								neighbors[i][0]=lProvData[xx][yy];
								neighbors[i][1]++;
								i = 5;
							}
						}
					}
					//neighbors[curNeighbor][0]=lProvData[x][y];
				}
				}}
			}}
		
		if( (neighbors[0][1]>neighbors[1][1])&(neighbors[0][1]>neighbors[2][1])&(neighbors[0][1]>neighbors[3][1])&(neighbors[0][1]>neighbors[4][1]) ){
			return neighbors[0][0];}
		else if( (neighbors[1][1]>neighbors[0][1])&(neighbors[1][1]>neighbors[2][1])&(neighbors[1][1]>neighbors[3][1])&(neighbors[1][1]>neighbors[4][1]) ){
			return neighbors[1][0];}
		else if( (neighbors[2][1]>neighbors[0][1])&(neighbors[2][1]>neighbors[1][1])&(neighbors[2][1]>neighbors[3][1])&(neighbors[2][1]>neighbors[4][1]) ){
			return neighbors[2][0];}
		else if( (neighbors[3][1]>neighbors[0][1])&(neighbors[3][1]>neighbors[1][1])&(neighbors[3][1]>neighbors[2][1])&(neighbors[3][1]>neighbors[4][1]) ){
			return neighbors[3][0];}
		else if( (neighbors[4][1]>neighbors[0][1])&(neighbors[4][1]>neighbors[1][1])&(neighbors[4][1]>neighbors[2][1])&(neighbors[4][1]>neighbors[3][1]) ){
			return neighbors[4][0];}
		else if(neighbors[0][0]==0){
			return 0;}
		
		else{
			return neighbors[0][0];
			}
	
		
		
	}
	
	//1,3,5
	//0,2,4,
	//0,1,2,3,4,5
	private void movepoint() {
		
		
		if(Provences[curProv].BorderPts[curPoint][4]!=1){
			mpBase();
			mpElevation();
			mpSmooth();
			mpOcean();
			mpMountains();
			mpBoarders();
			
			Provences[curProv].BorderPts[curPoint][0]=stbx;
			Provences[curProv].BorderPts[curPoint][1]=stby;
		}
	}
	
	private void mpMountains() {
			try{
			olde = elevationMap[Provences[curProv].x][Provences[curProv].y];
			newe = elevationMap[istbx][istby];
			if ((( hillsToMountains > olde)&(hillsToMountains<newe))|((hillsToMountains< olde)&(hillsToMountains>newe))){//cross ocean level
				stbx = cpx;//boarderPts[curProv][curPoint][0]+(boarderPts[curProv][curPoint][2]*0.001000f); 
				stby = cpy;//boarderPts[curProv][curPoint][1]+(boarderPts[curProv][curPoint][3]*0.001000f);
				Provences[curProv].BorderPts[curPoint][4]=1;
			}else{//other
			}
			}catch (ArrayIndexOutOfBoundsException e){}
		}
		
	
	private void mpBase() {
		cpx  = Provences[curProv].BorderPts[curPoint][0];
		icpx = Math.round(cpx);		
		cpy  = Provences[curProv].BorderPts[curPoint][1];
		icpy = Math.round(cpy);
			
		stbx = cpx +(Provences[curProv].BorderPts[curPoint][2]); 
		stby = cpy +(Provences[curProv].BorderPts[curPoint][3]);
		
		istbx = Math.round(stbx);
		istby = Math.round(stby);
	}
	private void mpSmooth() {
		pcent = new float[]{Provences[curProv].x,Provences[curProv].y};
		
		if (curPoint==0){
			mag1 = getMag(Provences[curProv].BorderPts[ptsperprov-1],pcent);
		}else{
			mag1 = getMag(Provences[curProv].BorderPts[curPoint-1],pcent);
		}
		
		if (curPoint==ptsperprov-1){
			mag3 = getMag(Provences[curProv].BorderPts[0],pcent);
		}else{
			mag3 = getMag(Provences[curProv].BorderPts[curPoint+1],pcent);
		}

		mag2 = getMag(new float[]{stbx,stby},pcent);

		
		len1 = Math.abs(mag2-mag1);
		len2 = Math.abs(mag2-mag3);
		
		if ((len1>5)|(len2>5)){
			len2=len1;
		}
		adjby = (0-(mag2-mag1));
		adjby = (adjby + (0-(mag2-mag3)))/2;
		adjby = adjby*stiff;
				
		newvert= adjVert1ToMag(new float[]{stbx,stby},pcent,mag2+adjby);
		
		stbx = newvert[0];
		stby = newvert[1];
		
		istbx = Math.round(newvert[0]);
		istby = Math.round(newvert[1]);
		
//		if (((ProvenceMapImageData[inewvert[0]][inewvert[1]]==0)|(ProvenceMapImageData[inewvert[0]][inewvert[1]]==curProv+1))){
//				
//		}else{
//			boarderPts[curProv][curPoint][4] = 1;
//		}

	}
	private void mpElevation() {
		try{
		olde = elevationMap[icpx][icpy];
		newe = elevationMap[istbx][istby];
		slope = newe-olde;
		
			if (slope<0){
				slope = -slope;
				stbx = stbx + (Provences[curProv].BorderPts[curPoint][2]*slope); 
				stby = stby + (Provences[curProv].BorderPts[curPoint][3]*slope);
			}else{
				slope = 1-slope;
				stbx = cpx + (Provences[curProv].BorderPts[curPoint][2]*slope*.15f); 
				stby = cpy+ (Provences[curProv].BorderPts[curPoint][3]*slope*.15f);
			}
		
		}catch (ArrayIndexOutOfBoundsException e){}
	}	
	private void mpOcean() {
		try{
		olde = elevationMap[Provences[curProv].x][Provences[curProv].y];
		newe = elevationMap[istbx][istby];
		if (((oceanLevel> olde)&(oceanLevel<newe))|((oceanLevel< olde)&(oceanLevel>newe))){//cross ocean level
			stbx = cpx;//boarderPts[curProv][curPoint][0]+(boarderPts[curProv][curPoint][2]*0.001000f); 
			stby = cpy;//boarderPts[curProv][curPoint][1]+(boarderPts[curProv][curPoint][3]*0.001000f);
			Provences[curProv].BorderPts[curPoint][4]=1;
		}else{//other
		}
		}catch (ArrayIndexOutOfBoundsException e){}
	}
	private void mpBoarders() {
		try{
		if (ProvenceMapImageData[istbx][istby]==0){//pt mapped to nothing
			ProvenceMapImageData[istbx][istby] = curProv+1;
		}else if(ProvenceMapImageData[istbx][istby]==(curProv+1)){// pt mapped to self already
			
		}else{// else pt mapped to another provence
			stbx = cpx;
			stby = cpy;
			Provences[curProv].BorderPts[curPoint][4]=1;
			Provences[curProv].addConnection(ProvenceMapImageData[istbx][istby] );
			Provences[ProvenceMapImageData[istbx][istby]-1].addConnection(curProv+1);
		}
		}catch (ArrayIndexOutOfBoundsException e){
			stbx = cpx;
			stby = cpy;
			
		}
	}

	private void placeProvCenters() {
		//BorderPts = new float[numProv][ptsperprov][5];
		float radsperpt = (float) ((Math.PI*2)/ptsperprov);
		float baseDist = 10f;
		for (int pidx=0;pidx<numProv;pidx++){//setup pts
			for (int pt=0;pt<ptsperprov;pt++){
				Provences[pidx].BorderPts[pt][2]= (float)  Math.cos(pt*radsperpt);
				Provences[pidx].BorderPts[pt][3]= (float) -Math.sin(pt*radsperpt);
				Provences[pidx].BorderPts[pt][0]= Provences[pidx].x+(Provences[pidx].BorderPts[pt][2]*baseDist); 
				Provences[pidx].BorderPts[pt][1]= Provences[pidx].y+(Provences[pidx].BorderPts[pt][3]*baseDist);
			}
		}
	}

	
	private float[] adjVert1ToMag(float[] fs, float[] pcent, float f) {
		float mag = getMag(fs,pcent);
		float nx = (((fs[0]-pcent[0])/mag)*f)+pcent[0];
		float ny = (((fs[1]-pcent[1])/mag)*f)+pcent[1];
		return new float[]{nx,ny};
		
	}
	private float getMag(float[] fs, float[] fs2) {
		return (float) Math.hypot(fs[0]-fs2[0], fs[1]-fs2[1]);
	}
	private float getMag(float[] fs, int x, int y) {
		return (float) Math.hypot(fs[0]-x, fs[1]-y);
	}
	
	private void addProvencesToMapImage() {
		BufferedImage bordersBi = (BufferedImage) iBorders;
		Graphics2D g2d = bordersBi.createGraphics();

		//BufferedImage terrainBi = (BufferedImage) iBorders;
		//Graphics2D terrainG2d = terrainBi.createGraphics();

		
		Color c = new Color(0,0,0,32);
		g2d.setColor(c);
		
		int rng=1;
		float maxborder= (float) Math.pow(rng+1+rng, 2);
		int borderstart=0;
		float ww=1;
		float bw = size/40f;
		
		if (size ==512){
			rng = 2;
			borderstart=9;
			ww=2.5f;
			bw=125f;
		}

		if (size ==1024){
			rng = 5;
			borderstart= 50;
			ww=.125f;
			bw= 45f;
		}
		if (size ==2048){
			rng = 7;	
			borderstart= 90;
			ww=.10f;
			bw= 65f;

		}
		
		
		maxborder= (int) Math.pow(rng+1+rng, 2);
		
		
		for (int x=0;x<size;x++){for (int y=0;y<size;y++){
			int p = ProvenceMapImageData[x][y];
			int border = 0;
						
			for (int xx=x-rng;xx<=x+rng;xx++){for (int yy=y-rng;yy<=y+rng;yy++){ //scan *x* subsection to see if this pixel borders another province
				if ((xx>0)&(xx<size)){if ((yy>0)&(yy<size)){
					if (p != ProvenceMapImageData[xx][yy]){
						border++;
						Provences[ProvenceMapImageData[xx][yy]-1].addConnection(p);
						}}}}}
			
			if (border>0){
				float pb = border/(float)(maxborder);
				
				float sqpb = pb*pb;//*pb*pb;
				
				float cb = (int) (sqpb*bw);
				float cw;// = (int) -(sqpbp1)+1;
				
				
				if(border>borderstart){
					cw = 0;
				}else{
					cw= border;
					
		}
				
				cw=cw*ww;
				cw = Math.max(0, cw);
				cw= Math.min(255, cw);
				try{
					g2d.setColor(new Color(255,255,255,(int)(cw)));
				}catch (IllegalArgumentException e){
					System.out.println(cw);
					
				}
				g2d.drawRect(x, y, 0, 1);
				if(cw==0){
					g2d.setColor(new Color(0,0,0,(int)cb));
					g2d.drawRect(x, y, 1, 1);
				}	
				
				//System.out.println("b");
			}
//			if (border>7){
//				g2d.drawRect(x, y, 1, 1);
//				//System.out.println("b");
//			}

		}}
		
		
		
//		for (int x=0;x<size;x++){for (int y=0;y<size;y++){
//				if (cpts[x][y]>0){
//					pdata =064;
//					r=(int) (pdata);
//					g=(int) (pdata);
//					b=(int) (pdata);
//					int now = bi.getRGB(x, y);
//					Color cnow = new Color(now);
//					int r2= cnow.getRed();
//					int g2= cnow.getGreen();
//					int b2= cnow.getBlue();
//					r= (int) ((r*.04f)+(r2*.96f));
//					g= (int) ((g*.04f)+(g2*.96f));
//					b= (int) ((b*.04f)+(b2*.96f));
//					
//					rgb = r;rgb = rgb << 8;rgb |= g;rgb = rgb << 8;rgb |= b;					
//					try{
//						bi.setRGB(x+1, y, rgb);
//						bi.setRGB(x-1, y, rgb);
//						bi.setRGB(x, y+1, rgb);
//						bi.setRGB(x, y-1, rgb);
//						
//						bi.setRGB(x+1, y+1, rgb);
//						bi.setRGB(x-1, y-1, rgb);
//						bi.setRGB(x-1, y+1, rgb);
//						bi.setRGB(x+1, y-1, rgb);
//
//					}catch (ArrayIndexOutOfBoundsException e){}
//				}
//				
//			}}
//		for (int x=0;x<size;x++){for (int y=0;y<size;y++){
//			if (cpts[x][y]>0){
//				r=(int) (cpts[x][y]);
//				g=(int) (cpts[x][y]);
//				b=(int) (cpts[x][y]);
//				r= (r);
//				g= (g);
//				b= (b);
//				
//				rgb = r;rgb = rgb << 8;rgb |= g;rgb = rgb << 8;rgb |= b;
//				bi.setRGB(x, y, rgb);
//
//			}
//			
//		}}
//		
//		for (int x=0;x<size;x++){for (int y=0;y<size;y++){
//			if (ProvenceMapDebugData[x][y]>0){
//				r=(int) (ProvenceMapDebugData[x][y]);
//				g=(int) 0;//(ProvenceMapDebugData[x][y]);
//				b=(int) 0;//(ProvenceMapDebugData[x][y]);
//				rgb = r;rgb = rgb << 8;rgb |= g;rgb = rgb << 8;rgb |= b;
//				bi.setRGB(x, y, rgb);
//			}}
//		}
		
//		for (int p=0;p<numProv;p++){
//			
//			int ii=0,i=0;
//			int pp =0;
//			while (ii<boarderPts[0].length){
//				float x1,x2,y1,y2;
//				
//				x1 = boarderPts[p][i][0];
//				y1 = boarderPts[p][i][1];
//				
//				try{
//					ii = i+1;while ((boarderPts[p][ii][4]!=1)&(ii<boarderPts[0].length)){ii++;}
//					if (pp==0){pp=1;}else{pp=0;}
//					x2 = boarderPts[p][ii][0];
//					y2 = boarderPts[p][ii][1];
//					if (pp==0){g2d.setColor(Color.green);}
//					if (pp==1){g2d.setColor(Color.red);}
//					g2d.drawLine((int)x1,(int) y1,(int) x2,(int) y2);
//					i=ii;
//				}catch (ArrayIndexOutOfBoundsException e){}
//			}
//		}
			
//		for (int p=0;p<numProv;p++){
//			
//			int ii=0;
//			int pp =0;
//			for (int i=0;i<boarderPts[0].length;i++){
//				float x1,x2,y1,y2;
//				if (pp==0){pp=1;}else{pp=0;}
//				
//				if (i == boarderPts[0].length-1){
//					x1 = boarderPts[p][i][0];
//					y1 = boarderPts[p][i][1];
//					
//					x2 = boarderPts[p][0][0];
//					y2 = boarderPts[p][0][1];
//				
//				}else{
//					x1 = boarderPts[p][i][0];
//					y1 = boarderPts[p][i][1];
//					
//					x2 = boarderPts[p][i+1][0];
//					y2 = boarderPts[p][i+1][1];
//				}
//				if (pp==0){g2d.setColor(Color.white);}
//				if (pp==1){g2d.setColor(Color.gray);}
//				g2d.drawLine((int)x1,(int) y1,(int) x2,(int) y2);
//
//		}
//		}
	}

		
	private float[] biSectLine(float x1,float y1,float x2,float y2){
		return new float[]{((x1-x2)/2f)+x1,((y1-y2)/2f)+y1};
		
	}
	private float getNearistProvCenterDistance(int pnum,Provence[] workingSet) {
		int nearIdx=0;
		float nearDist =size;
		Provence nProvence = workingSet[pnum];
		for (int cpidx=0;cpidx<workingSet.length;cpidx++){
			if ((workingSet[cpidx]!=null)&(pnum!=cpidx)){// dont measure self
				float cdist = (float) Math.hypot(nProvence.x-workingSet[cpidx].x, nProvence.y-workingSet[cpidx].y);
				if (cdist<nearDist){
					nearDist=cdist;
					nearIdx = cpidx;
				}
			}
		}
		return nearDist;
	}

	public Provence getProvAtXY(int x,int y,Provence[] workingSet) {
		return workingSet[ ProvenceMapImageData[x][y]-1];	
	}
	
	public Provence getNearistProv(int x,int y,Provence[] workingSet) {

		int nearIdx=0;
		float nearDist =size;
		
		//Provence nProvence = workingSet[pnum];
		for (int cpidx=0;cpidx<workingSet.length;cpidx++){
			
				float cdist = (float) Math.hypot(x-workingSet[cpidx].x, y-workingSet[cpidx].y);
				if (cdist<nearDist){
					nearDist=cdist;
					nearIdx = cpidx;
				}
			
		}
		
		return workingSet[nearIdx];
	}
	
	public void Save(String oFileString) throws IOException {
		Image iOut = (Image) new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB); 
		
		File oFile= new File(oFileString+".tga");
		OutputStream oStream = new FileOutputStream(oFile);
		
		BufferedImage biOut = (BufferedImage) iOut;
		Graphics2D g2dOut = biOut.createGraphics();
		g2dOut.drawImage(img, 0, 0,biOut.getWidth(),biOut.getHeight(), null);
		g2dOut.drawImage(iBorders, 0, 0,biOut.getWidth(),biOut.getHeight(), null);
		
		g2dOut.dispose();
		
		TgaWriter.saveImage(iOut, oStream, false);
		//ImageIO.write((RenderedImage) img, "tga", oFile);
		
		
		
		
	}
	
	@Override
	public void createWeather() {
		float w=255;
		float evch = 0;
		float o;
		int rng=1;
		BufferedImage biWeather = ((BufferedImage)iWeather);
		float[][] data = new float[size][size];
		
		for (int y=0;y<size;y++){
			w=.1f;
			for (int x=0;x<size-1;x++){
				evch=0;
				float lc =0;
				for (int xx=x-rng+2;xx<=x+rng+2;xx++){
				for (int yy=y-rng;yy<=y+rng;yy++){					
					if ((xx>0)&(xx<size)){if ((yy>0)&(yy<size)){
						evch=evch+elevationMap[xx][yy];
						lc=lc+1;
					}}			
				}}
				
				evch=evch/lc;
				evch = elevationMap[x][y]-evch;
				
				if (evch>0){//decline
					//w=w-evch;
					//w=w*1.25f;///0;
					w=w+.05f;
				}else{//incline
					//w=w+.025f;
					w=w/1.25f;
				}
				
				data[x][y]=w;
			
				//o = w;
				//w = Math.max(o, 0);
				//o = Math.min(1 , o);
				//if((o>0)&(o<1)){
				//biWeather.setRGB(x, y, new Color(1,0,0,o/2f).getRGB());
				//g2dWeather.setColor(new Color(1,0,0,o/2f));
				//g2dWeather.fillRect(x, y, 1, 1);
				//}
				
		}}
		
		data = Utils.normalizeArray(data);
		float ba=0;
		for (int y=0;y<size;y=y+8){
		for (int x=0;x<size;x=x+8){
			ba=0;
			for (int yy=y;yy<y+8;yy++){
			for (int xx=x;xx<x+8;xx++){
				ba=ba+data[xx][yy];
				}}
			
			ba=ba/(float)(8 * 8);
			
			for (int yy=y;yy<y+8;yy++){
			for (int xx=x;xx<x+8;xx++){
					data[xx][yy]=ba;
				}}		

			}}
		
		for (int y=0;y<size;y++){for (int x=0;x<size;x++){
			o=data[x][y];		
			biWeather.setRGB(x, y, new Color(1,0,0,o).getRGB());
		}}
		
		biWeather =null;

	}
}
	

