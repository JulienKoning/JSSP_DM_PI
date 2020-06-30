import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import ilog.concert.*;
import ilog.cplex.*;

public class ProgLin {
	
	IloCplex cplex;
	
	public ProgLin(Data data) {
		
	}
	
	
	public void build_JSP_Mining(Data data, Mining mining, int timeLim, boolean isMining) {
		// TODO Auto-generated method stub
		try {
			cplex = new IloCplex();
			
			//déclaration binaire
			IloNumVar[][] x = new IloNumVar[data.getSize()][];
		    for (int i = 0; i < data.getSize(); i++){
		       x[i] = cplex.boolVarArray(data.getSize());
		       for (int j = 0; j < data.getSize(); j++){
		    	   int i1 = i+1; int j1 = j+1;
		    	   x[i][j].setName("x_"+i1+"_"+j1);
		       }
		    }
		    //déclaration date de début
			IloNumVar[] st = new IloNumVar[data.getSize()];
			for (int i = 0; i < data.getSize(); i++){
				st[i]=cplex.numVar(0, data.bigH);
		    	   int i1 = i+1; 
		    	st[i].setName("s_"+i1);
		    }
			 //déclaration date de début
			IloIntVar cmax = cplex.intVar(0, data.bigH);
			cmax.setName("cmax");
						
			
			//fixer mkpn
			for (int op = 0; op < data.getSize(); ++op) {
		    	   int op1 = op+1;
				cplex.addLe( cplex.diff(st[op],cmax),  -data.getDurationForOp(op)).setName("c0_"+op1);
			}
			
			//respect gamme
			for (int op1 = 0; op1 < data.getSize(); ++op1) {
				for (int op2 = op1+1; op2 < data.getSize(); ++op2) {
					if (data.getJobForOp(op1) == data.getJobForOp(op2)) {
						 int opp1 = op1+1; int opp2 = op2+1;
						cplex.addLe( cplex.diff(st[op1],st[op2]),  -data.getDurationForOp(op1)).setName("c1_"+opp1+"_"+opp2);
					}
				}
			}
	        
			//respect disjonctions
			for (int op1 = 0; op1 < data.getSize(); ++op1) {
				for (int op2 = op1+1; op2 < data.getSize(); ++op2) {
					if (op1 != op2 && data.getMachineForOp(op1) == data.getMachineForOp(op2)) {
						
							int opp1 = op1+1; int opp2 = op2+1;
							cplex.addEq( cplex.sum(x[op1][op2],x[op2][op1]),  1).setName("c2_"+opp1+"_"+opp2);
						
						
					}
				}
			}
			
			//dates disjonctions
			for (int op1 = 0; op1 < data.getSize(); ++op1) {
				for (int op2 = 0; op2 < data.getSize(); ++op2) {
					if (op1 != op2 && data.getMachineForOp(op1) == data.getMachineForOp(op2)) {
						
						
							int opp1 = op1+1; int opp2 = op2+1;
							cplex.addLe( cplex.sum(cplex.diff(st[op1], st[op2]),cplex.prod(data.bigH, x[op1][op2])), data.bigH-data.getDurationForOp(op1)).setName("c3_"+opp1+"_"+opp2);
						
					}
				}
			}
			
			if (isMining) {
				String toOut = "";
				// respect pattern
				for (int pattern=0; pattern < mining.nbAddedArcs; ++pattern) {
					int opp1 = mining.addedArcs[pattern][0]+1; int opp2 = mining.addedArcs[pattern][1]+1;
					cplex.addEq( x[mining.addedArcs[pattern][0]][mining.addedArcs[pattern][1]],1).setName("c4_"+opp1+"_"+opp2);
					toOut+= "x_"+opp1+"_"+opp2+"\n";
					//cplex.addLe( cplex.diff(st[opp1-1],st[opp2-1]),  -data.getDurationForOp(opp1-1)).setName("c42_"+(opp1)+"_"+(opp2));
				}
				try  {
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./INSTANCES_CPLEX/patterns/patterns_"+data.getpName()+"_"+mining.getNbSigma()+".pat", false)));
				    out.println(toOut);
				    out.close();
				} catch (IOException ioe) {}
			
			}
			
					
			cplex.addMinimize(cmax);
	       
			cplex.exportModel("./INSTANCES_CPLEX/"+data.getpName()+"_"+(int)mining.getNbSigma()+".lp"); //+"_"+(int)mining.getNbSigma()
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void exec_JSP_Mining(Data data, Mining mining, int timeLim, boolean isMining) {
		try {
			//cplex.setParam(IloCplex.DoubleParam.TiLim, timeLim);
			if ( cplex.solve() ) {
		        System.out.println("Solution status: " + cplex.getStatus());
		        System.out.println("CPU: " + cplex.getCplexTime());
		        System.out.println("cmax: " + cplex.getObjValue());
		        System.out.println();
			}
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void build_paramFile(Data data, double sig) {
		
		try {	
							
			String line = "";

			if (sig>-1) {
				line += "read "+ data.getpName() + "_"+sig+".lp \n";
			} else  {
				line += "read "+ data.getpName() + ".lp \n";
			}
			
			line += "set threads 1\n";
			line += "set timelimit 7200 \n";
			line += "mipopt\n";
			line += "display solution variables \n-\n";
			
			PrintWriter dataForCPLEX;
			if (sig>-1) {
				dataForCPLEX = new PrintWriter(new BufferedWriter(new FileWriter(data.getPathName()+"/INSTANCES_CPLEX/paramCPLEX_"+sig+".txt", true)));
			} else {
				dataForCPLEX = new PrintWriter(new BufferedWriter(new FileWriter(data.getPathName()+"/INSTANCES_CPLEX/paramCPLEX.txt", true)));
			}
			dataForCPLEX.print(line);
			dataForCPLEX.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	   
	}
}

