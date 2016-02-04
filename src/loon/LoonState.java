package loon;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class LoonState
{
    int Bmoves[][];
    int Bstrength[];
    int Bpos[][];
    int ticksLived;
    int points;   
    
    
    LoonState(){
        Bmoves=new int[Loon.B][Loon.T];
        Bstrength=new int[Loon.B];
        Bpos=new int[Loon.B][3];
        for(int b=0;b<Loon.B;++b){
            for(int t=0;t<Loon.T;++t)
                Bmoves[b][t]=0;
            Bstrength[b]=0;
            Bpos[b][0]=Loon.rs;
            Bpos[b][1]=Loon.cs;
            Bpos[b][2]=-2;  //-2 not lifted, -1 chrased, 0 lowest alt level
        }
        ticksLived=0;
        points=0;
        
        //test moves
        /*
        for(int b=0;b<Loon.B;++b)
            Bmoves[b][7*b]=1;
            */
        //end test initialization
    }

    void set(LoonState copy){
        ticksLived=copy.ticksLived;
        for(int b=0;b<Loon.B;++b)
            for(int t=0;t<Loon.T;++t)
                Bmoves[b][t]=copy.Bmoves[b][t];
        for(int b=0;b<Loon.B;++b){
            Bstrength[b]=copy.Bstrength[b];
            Bpos[b][0]=copy.Bpos[b][0];
            Bpos[b][1]=copy.Bpos[b][1];
            Bpos[b][2]=copy.Bpos[b][2];
        }
        points=copy.points;
    }
    
    void setOnlyBmoves(LoonState copy){
        for(int b=0;b<Loon.B;++b)
            for(int t=0;t<Loon.T;++t)
                Bmoves[b][t]=copy.Bmoves[b][t];
    }

    void dump() throws FileNotFoundException, UnsupportedEncodingException{
        PrintWriter writer = new PrintWriter("result.out", "UTF-8");
        for(int t=0;t<Loon.T;++t){
            String s=""+Bmoves[0][t];
            for(int b=1;b<Loon.B;++b)
                s+=" "+Bmoves[b][t];
            writer.println( s );
        }
        writer.close();
    }
    
    void shortDump(){
        System.out.println( (points/(double)Loon.T/Loon.L)+" "+getWeakestB() );
    }
    
    int getWeakestB(){
        int weakest=Bstrength[0];
        int weakestB=0;
        for(int b=1;b<Loon.B;++b){
            if(weakest>Bstrength[b]){
                weakest=Bstrength[b];
                weakestB=b;
            }
        }
        return weakestB;
    }
    
    void tick() throws Exception{
      //do ballon movement
        for(int b=0;b<Loon.B;++b){
            if(Bpos[b][0]==-1)
                continue;
            if(Bpos[b][2]==-2 && Bmoves[b][ticksLived]==1) //do liftof over crash state away
                Bpos[b][2]+=1;
            Bpos[b][2]+=Bmoves[b][ticksLived];
            if(Bpos[b][2]<-2 || Bpos[b][2]==-1 || Bpos[b][2]>=Loon.A){
                points=-1;
                throw new Exception("reached not allowed altitude!");
            }
            if(Bpos[b][2]==-2)
                continue;
            int row=Bpos[b][0];
            int col=Bpos[b][1];
            int alt=Bpos[b][2];
            int windR=Loon.winds[row][col][alt][0];
            int windC=Loon.winds[row][col][alt][1];
            Bpos[b][0]+=windR;
            Bpos[b][1]+=windC;
            if(Bpos[b][0]<0 || Bpos[b][0]>=Loon.R){
                Bpos[b][0]=-1;
                Bpos[b][1]=0;
            }
            Bpos[b][1]=(Bpos[b][1]+Loon.C)%Loon.C;
        }
        evalBpositions();
        ++ticksLived;
    }
    
    void evaluate() throws Exception{
        points=0;
        ticksLived=0;
        for(int b=0;b<Loon.B;++b){
            Bpos[b][0]=Loon.rs;
            Bpos[b][1]=Loon.cs;
            Bpos[b][2]=-2;
            Bstrength[b]=0;
        }
        while(ticksLived<Loon.T){
            tick();
        }
        shortDump();
    }
    
    void optimize() throws FileNotFoundException, UnsupportedEncodingException{
        int weakestB=getWeakestB();
        LoonState[] test=new LoonState[27];
        for(int i=0;i<27;++i){
            test[i]=new LoonState();
            test[i].setOnlyBmoves( this );
        }
        for(int i=0;i<27;++i){
            test[i].Bmoves[weakestB][0]=i%3-1;
            test[i].Bmoves[weakestB][1]=(i/3)%3-1;
            test[i].Bmoves[weakestB][2]=(i/9)%3-1;
            try{
                test[i].tick();
                test[i].tick();
                test[i].tick();
            }
            catch ( Exception e ){
                //e.printStackTrace();
                continue;
            }
        }
        for(int t=3;t<Loon.T;++t){
            LoonState bestTest[]=new LoonState[9];
            for(int i=0;i<9;++i)
                bestTest[i]=new LoonState();
            int weakestBestId=0;
            for(int i=0;i<27;++i){
                if(bestTest[weakestBestId].points<test[i].points){
                    bestTest[weakestBestId].set( test[i] );
                    weakestBestId=0;
                    for(int k=1;k<9;++k){
                        if(bestTest[k].points<bestTest[weakestBestId].points){
                            weakestBestId=k;
                        } else if(bestTest[k].points==bestTest[weakestBestId].points){
                            if(Loon.randomGenerator.nextBoolean())
                                weakestBestId=k;
                        }
                    }
                } else if(bestTest[weakestBestId].points==test[i].points){
                    if(Loon.randomGenerator.nextBoolean())
                        bestTest[weakestBestId].set( test[i] );
                    weakestBestId=0;
                    for(int k=1;k<9;++k){
                        if(bestTest[k].points<bestTest[weakestBestId].points){
                            weakestBestId=k;
                        } else if(bestTest[k].points==bestTest[weakestBestId].points){
                            if(Loon.randomGenerator.nextBoolean())
                                weakestBestId=k;
                        }
                    }
                }
            }
            bestTest[0].shortDump();
            for(int i=0;i<27;++i){
                test[i].set( bestTest[i/3] );
                test[i].Bmoves[weakestB][t]=(i%3)-1;
                try{
                    test[i].tick();
                }
                catch ( Exception e ){
                    //e.printStackTrace();
                    continue;
                }
            }
        }
        for(int i=0;i<27;++i)
            if(test[i].points>this.points)
                this.set( test[i] );
        dump();
    }
    
    void evalBpositions(){
    	/*
        int Bgrid[][]=new int[Loon.R][Loon.C];
        for(int r=0;r<Loon.R;++r){
            for(int c=0;c<Loon.C;++c)
                Bgrid[r][c]=0;
        }
        for(int b=0;b<Loon.B;++b){
            int r=Bpos[b][0];
            int c=Bpos[b][1];
            if(r==-1 || Bpos[b][2]==-2)
                continue;
            if(Bgrid[r][c]==0){
                Bgrid[r][c]=-(b+1);
            } else if(Bgrid[r][c]<0){
                Bgrid[r][c]=2;
            } else{
                Bgrid[r][c]+=1;
            }
        }
        */
        // check for connected Lcells
    	int ballons[][] = new int[Loon.R][Loon.C];
    	//HashMap<int[], Integer> LcellToBallon = new HashMap<int[], Integer>();
        for(int b=0;b<Loon.B;++b){
            int row=Bpos[b][0];
            int col=Bpos[b][1];
            //boolean connected=false;
            for(int checkV=0;checkV<Loon.delVvec.size();++checkV){
                int delRow=Loon.delVvec.get( checkV )[0];
                int delCol=Loon.delVvec.get( checkV )[1];
                int curRow=row+delRow;
                int curCol=col+delCol;
                if(curRow<0 || curRow>=Loon.R)
                    continue;
                curCol=(curCol+Loon.C)%Loon.C;
                if(Loon.Lgrid[curRow][curCol]==true){
                    //Bstrength[-Bgrid[curRow][curCol]-1]+=1;
                	if(ballons[curRow][curCol]!=0) {
                	//int cur[] = {curRow, curCol};
                	//if(LcellToBallon.get(cur)!=null) {
                	//try {
                		Bstrength[ballons[curRow][curCol]]-=1;
                		//Bstrength[LcellToBallon.get(cur)]-=1;
                		Loon.Lgrid[curRow][curCol]=false;
                	}
                	//catch (Exception e) {
                	else {
                		ballons[curRow][curCol] = b;
                		//LcellToBallon.put(cur , b);
                		Bstrength[b]+=1;
                		++points;
                	}
                }
            }
            /*
            if(connected)
                ++points;*/
        }
        //reset Lgrid
        for(int l=0;l<Loon.Lcells.size();++l) {
        	Loon.Lgrid[Loon.Lcells.get( l )[0]][Loon.Lcells.get( l )[1]] = true;
        }
    }
}
