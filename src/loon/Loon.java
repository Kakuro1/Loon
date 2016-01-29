package loon;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Loon
{
    static int R,C,A,L,V,B,T,rs,cs;
    static boolean Lgrid[][];
    static List<int[]> Lcells;
    static int winds[][][][];
    static List<int[]> delVvec;
    static Random randomGenerator;
    
    public static void main( String[] args ) throws Exception
    {
        randomGenerator = new Random();
        BufferedReader in = new BufferedReader(new FileReader("loon_r70_c300_a8_radius7_saturation_250.in"));
        String s;
        s=in.readLine();
        R=Integer.parseInt( s.split(" ")[0] );
        C=Integer.parseInt( s.split(" ")[1] );
        A=Integer.parseInt( s.split(" ")[2] );
        s=in.readLine();
        L=Integer.parseInt( s.split(" ")[0] );
        V=Integer.parseInt( s.split(" ")[1] );
        B=Integer.parseInt( s.split(" ")[2] );
        T=Integer.parseInt( s.split(" ")[3] );
        s=in.readLine();
        rs=Integer.parseInt( s.split(" ")[0] );
        cs=Integer.parseInt( s.split(" ")[1] );
        
        Lgrid=new boolean[R][C];
        Lcells=new ArrayList<int[]>();
        //TODO evtl unnecessary
        for(int row=0;row<R;++row)
            for(int col=0;col<C;++col)
                Lgrid[row][col]=false;
        
        for(int i=0;i<L;++i){
            s=in.readLine();
            int row=Integer.parseInt( s.split(" ")[0] );
            int col=Integer.parseInt( s.split(" ")[1] );
            Lgrid[row][col]=true;
            Lcells.add( new int[]{row,col} );
            
        }
        
        winds=new int[R][C][A][2];
        for(int alt=0;alt<A;++alt){
            for(int row=0;row<R;++row){
                s=in.readLine();
                String sa[]=s.split( " " );
                for(int col=0;col<C;++col){
                    winds[row][col][alt][0]=Integer.parseInt( sa[2*col] );
                    winds[row][col][alt][1]=Integer.parseInt( sa[2*col+1] );
                }
            }
        }
        in.close();
        
        delVvec=new ArrayList<int[]>();
        for(int delR=-V;delR<=V;++delR){
            for(int delC=-V;delC<=V;++delC){
                if(delR*delR+delC*delC<=V*V)
                    delVvec.add( new int[]{delR,delC} );
            }
        }
        
        doAlgo();
    }
    
    static void doAlgo() throws Exception{
        LoonState currentMoves=new LoonState();
        for(int i=0;i<1000;i++){
            currentMoves.evaluate();
            if(currentMoves.points/(double)T/L>0.5)
                break;
            currentMoves.optimize();
            /*
            int alt=-1;
            for(int t=0;t<T;++t){
                if(alt<=0){
                    int rand=randomGenerator.nextInt(2);
                    currentMoves.Bmoves[currentMoves.weakestBid][t]=rand;
                    alt+=rand;
                } else if(alt==A-1) {
                    int rand=randomGenerator.nextInt(2)-1;
                    currentMoves.Bmoves[currentMoves.weakestBid][t]=rand;
                    alt+=rand;
                } else {
                    int rand=randomGenerator.nextInt(3)-1;
                    currentMoves.Bmoves[currentMoves.weakestBid][t]=rand;
                    alt+=rand;
                }
            }
            */
        }
        currentMoves.evaluate();
        currentMoves.shortDump();
    }
}
