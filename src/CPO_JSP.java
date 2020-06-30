// ---------------------------------------------------------------*- Java -*-
// File: ./examples/src/java/SchedJobShop.java
// --------------------------------------------------------------------------
// Licensed Materials - Property of IBM
//
// 5724-Y48 5724-Y49 5724-Y54 5724-Y55 5725-A06 5725-A29
// Copyright IBM Corporation 1990, 2017. All Rights Reserved.
//
// Note to U.S. Government Users Restricted Rights:
// Use, duplication or disclosure restricted by GSA ADP Schedule
// Contract with IBM Corp.
// --------------------------------------------------------------------------

/* ------------------------------------------------------------

Problem Description
-------------------

In the classical Job-Shop Scheduling problem a finite set of jobs is
processed on a finite set of machines. Each job is characterized by a
fixed order of operations, each of which is to be processed on a
specific machine for a specified duration.  Each machine can process
at most one operation at a time and once an operation initiates
processing on a given machine it must complete processing
uninterrupted.  The objective of the problem is to find a schedule
that minimizes the makespan of the schedule.

------------------------------------------------------------ */

import ilog.concert.*;
import ilog.cp.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//solveur ppc
public class CPO_JSP {
	
    static class IntervalVarList extends ArrayList<IloIntervalVar> {
        public IloIntervalVar[] toArray() {
            return (IloIntervalVar[]) this.toArray(new IloIntervalVar[this.size()]);
        }
    }

    static IloIntExpr[] arrayFromList(List<IloIntExpr> list) {
        return (IloIntExpr[]) list.toArray(new IloIntExpr[list.size()]);
    }

    public CPO_JSP(Data data, Mining mining) {

    	IloCP cp = new IloCP();
        try {
           
            List<IloIntExpr> ends = new ArrayList<IloIntExpr>();
            IntervalVarList[] machines = new IntervalVarList[data.getNbmac()];
            for (int j = 0; j < data.getNbmac(); j++)
                machines[j] = new IntervalVarList();

           
            IloIntervalVar[] tabOp = new IloIntervalVar[data.getSize()];
            
            for (int i = 0; i < data.getSize(); i++) {
            	
            	tabOp[i]=cp.intervalVar(data.getDurationForOp(i));

                machines[data.getMachineForOp(i)].add(tabOp[i]);
                if (data.getPrecConj(i) >= 0) {
                    cp.add(cp.endBeforeStart(tabOp[i-1], tabOp[i]));
                }
                
                ends.add(cp.endOf(tabOp[i]));
            }

            for (int j = 0; j < data.getNbmac(); j++)
                cp.add(cp.noOverlap(machines[j].toArray()));
            
    						  						
			for (int pattern=0; pattern < mining.nbAddedArcs; ++pattern) {
				int opp1 = mining.addedArcs[pattern][0]; int opp2 = mining.addedArcs[pattern][1];
				cp.add(cp.endBeforeStart(tabOp[opp1], tabOp[opp2]));
				//cplex.addLe( cplex.diff(st[opp1-1],st[opp2-1]),  -data.getDurationForOp(opp1-1)).setName("c42_"+(opp1)+"_"+(opp2));
			}
  
            IloObjective objective = cp.minimize(cp.max(arrayFromList(ends)));
            cp.add(objective);
            cp.setOut(null);
            cp.setParameter(IloCP.DoubleParam.TimeLimit, 600);
            
            long startTime = System.nanoTime();
            System.out.println("Instance \t: " + data.getpName());
            if (cp.solve()) {
                System.out.println("Makespan : " + cp.getObjValue() +"; gap : "+cp.getObjGap()+"; cpu(s) : " + TimeUnit.SECONDS.convert(System.nanoTime()- startTime, TimeUnit.NANOSECONDS));
                PrintWriter out;
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter("./results/patterns/patterns_"+data.getpName()+"_"+mining.getNbSigma()+".pat", false)));
				    out.println(data.toOut);
				    out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } else {
                System.out.println("No solution found.");
            }
        } catch (IloException e) {
            System.err.println("Error: " + e);
        }
    }
}
