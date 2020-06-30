

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

public class Mining {

	protected boolean[][] pattern;
	protected static int puissanceMax;
	protected boolean isUsed;
	protected static double nbSigma;
	protected static double[][] patternValue;
	protected int nbAddedArcs;
	protected int addedArcs[][];
	protected double[][] listPatternQuality ;
	protected int sizeList;
	public int avoidedPatterns;
	
	public double getNbSigma() {
		return nbSigma;
	}
	
	public boolean getIsUsed() {return isUsed;}
	
	public Mining() {
		
	}
	public Mining(boolean[][] pattern) {
		this.pattern = pattern;
	}

	public Mining(Data data) {
		puissanceMax = data.puissMax;
		this.pattern = new boolean[data.getSize() + 2][data.getSize() + 2];
		nbAddedArcs=0;
		addedArcs = new int[data.getSize()*data.getSize()][2];
		patternValue = new double[data.getSize()+1][data.getSize()+1];
		isUsed=data.withDM;
	}
	
	public void clearMining(Data data) {
		for (int i = 0; i < data.getSize() + 2; i++) {
			for (int j = 0; j < data.getSize() + 2; j++) {
				pattern[i][j] = false;
					
			}
		}
	}

	public int getNbPattern() {
		int nb = 0;
		if (pattern != null) {

			for (int i = 0; i < pattern.length; i++) {
				for (int j = 0; j < pattern[i].length; j++) {
					if (pattern[i][j]) {
						nb++;
					}
				}
			}
		}
		return nb;
	}


	public void setPatternMemoire(boolean[][] patternN) {// do not override older pattern except if the opposite is find
		for (int i = 0; i < patternN.length; i++) {
			for (int j = 0; j < patternN[i].length; j++) {
				if (!this.pattern[i][j]) {
					this.pattern[i][j] = patternN[i][j];
				}
				if (this.pattern[i][j]) {
					pattern[j][i] = false;
				}
			}
		}
	}

	public void setPatternNoMemoire(boolean[][] patternN) {// do not override older pattern except if the opposite is find
		for (int i = 0; i < patternN.length; i++) {
			for (int j = 0; j < patternN[i].length; j++) {
				
				this.pattern[i][j] = patternN[i][j];
				if (this.pattern[i][j]) {
					pattern[j][i] = false;
				}
			}
		}
	}


	static public boolean[][] checkReverse(Data data, int nbRep, int nbTasks, /*boolean[][] matrixBool,*/ TreeMap<Long, Solution> Sol, double a, double minfreq, double nbSig)
	{
		
		boolean[][] newMatrix = new boolean[nbTasks][nbTasks];
		double[][] matrixDouble = new double[nbTasks][nbTasks];
		
		//System.out.println( " nb sol : "+Sol.size());
		ArrayList<Long>[][] matrix = new ArrayList[nbTasks][nbTasks];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j]=new ArrayList<Long>();
			}
		}
		ArrayList<Integer> vector;
		ArrayList<Long> ts;
		long makespan = 0;
		Solution sol;
		int cpt = 0;
		// pout chaque solution dans la map
		for(Entry<Long, Solution> e : Sol.entrySet())
		{
			// on récupère le makespan
			makespan = (long)(e.getKey()/(nbRep*puissanceMax));
			// on récupère la solution?
			sol = e.getValue();
			vector = sol.solutionToArraylist(data);
			
			// pour chaque item x du vector
			for(int i = 0; i<vector.size()-1; i++)
			{
				int j=i+1;

				// pour chaque item y du vecteur 
				while(j<=vector.size()-1)
				{
					ts = matrix[vector.get(i)][vector.get(j)];
//++cpt;
					ts.add(makespan);
					j++;
				}
			}
		}





		Double[][] somm= new Double[nbTasks][nbTasks];
		for(int i = 0; i<nbTasks; i++)
		{
			for(int j = i+1; j<nbTasks; j++)
			{
				somm[i][j]=0.0;//sum1
				somm[j][i]=0.0;//sum2
				// on calcule la moyenne du makespan pour la case et son inverse
				ts = matrix[i][j];
				for (int k=0; k<ts.size();k++)
					somm[i][j]+=ts.get(k);
				if (ts.size()==0) {
					somm[i][j]=0.0;
				}else {
					somm[i][j]=somm[i][j]/((double)ts.size());
				}

				ts = matrix[j][i];
				for (int k=0; k<ts.size();k++)
					somm[j][i]+=ts.get(k);
				if (ts.size()==0) {
					somm[j][i]=0.0;
				}else {
					somm[j][i]=somm[j][i]/((double)ts.size());
				}

				somm[i][j]=somm[j][i]-somm[i][j];
				somm[j][i]=-somm[i][j];

				//System.out.println(matrix[j][i].size()+matrix[i][j].size()+ " "+i+" "+j);

				/*matrixDouble[i][j]=sum2-sum1;
				matrixDouble[j][i]=sum1-sum2;*/
			}


		}
		
		//data.setTimeMax(10);

		Double var=0.0;
		int nbPatt=0;
		for(int i = 0; i<nbTasks; i++)
		{
			for(int j = i+1; j<nbTasks; j++)
			{
				if (matrix[i][j].size()>minfreq && matrix[j][i].size()>minfreq) {
					
					var+=Math.pow(somm[i][j], 2);
					nbPatt++;
				}
			}
		}
		var=var/(nbPatt*1.0);

		double sigma=Math.sqrt(var);
		//dynamique seuil
		a=nbSig*sigma;
		nbSigma = nbSig;
		//System.out.print(" seuil: "+a+" ");

		for(int i = 0; i<nbTasks; i++)
		{
			for(int j = i+1; j<nbTasks; j++)
			{
				patternValue[i][j]=somm[i][j];
				patternValue[j][i]=somm[j][i];
				if(somm[i][j]>0)
				{
					double seuil=somm[i][j];
					double f=Math.abs(matrix[j][i].size()-matrix[i][j].size())/(double)Sol.size();
					f*=Math.PI/2;
					if(seuil <= /*Math.tan(f)+*/a || matrix[j][i].size()<minfreq || matrix[i][j].size() <minfreq )
					{
						newMatrix[i][j]=false;
						newMatrix[j][i]=false;
					}
					else{
						newMatrix[i][j]=true;
						newMatrix[j][i]=false;
					}
				}
				else
				{	
					double seuil=somm[j][i];
					double f=Math.abs(matrix[j][i].size()-matrix[i][j].size())/(double)Sol.size();
					f*=Math.PI/2;
					if(seuil <= /*Math.tan(f)*/+a|| matrix[j][i].size()<minfreq || matrix[i][j].size() <minfreq )
					{
						newMatrix[j][i]=false;
						newMatrix[i][j]=false;
					}
					else{
						newMatrix[j][i]=true;
						newMatrix[i][j]=false;
					}

				}
			}
		}

		
		
		/*for(int i=0;i<nbTasks;i++)
		{
			for(int j=0;j<nbTasks;j++)
			{
				System.out.print(Math.round(matrixDouble[i][j]*10)/10.0+"("+newMatrix[i][j]+")"+"("+matrix[i][j].size()+")"+Math.abs(matrix[j][i].size()-matrix[i][j].size())/(double)Sol.size()+"\t\t");
			}
			System.out.println();
		}*/
		return newMatrix;
	}
	
	
	public void addPatterns(Data data) {
		listPatternQuality = new double[data.getSize()*data.getSize()][3];
		sizeList=0;
		//build sorted list of all patterns + quality
		int cptPattern=0;
		for (int op1 = 0; op1 < data.getSize(); ++op1) {
			for (int op2 = 0; op2 < data.getSize(); ++op2) {
				if (op1 != op2 && data.getMachineForOp(op1) == data.getMachineForOp(op2) && pattern[op1][op2]) {
					
					insertTrie(op1, op2, patternValue[op1][op2]);
				}
			}
		}
		
		
		//add patterns in decreasing order of sorted list
		avoidedPatterns = 0;
		for (int pat = sizeList-1; pat>0; --pat) {
			int op1 = (int)listPatternQuality[pat][0];
			int op2 = (int)listPatternQuality[pat][1];
			/*if (op1==42) {
				int k = 2;
			}
			boolean tutu = checkCycle(data, op1, op2);
			if(tutu || !tutu) {*/
				//l'arc ne génère pas de cycle
				addedArcs[nbAddedArcs][0]=op1;addedArcs[nbAddedArcs][1]=op2;
				++nbAddedArcs;
			/*} else {
				++avoidedPatterns;
			}*/
		}
		
	}
	
	public void addPatternsNaive(Data data) {
		
		
		//add patterns in decreasing order of sorted list
		for (int op1 = 0; op1 < data.getSize(); ++op1) {
			for (int op2 = 0; op2 < data.getSize(); ++op2) {
				if (op1 != op2 && data.getMachineForOp(op1) == data.getMachineForOp(op2) && pattern[op1][op2]) {
					boolean tutu = checkCycle(data, op1, op2);
					if(tutu || !tutu) {
						//l'arc ne génère pas de cycle
						addedArcs[nbAddedArcs][0]=op1;addedArcs[nbAddedArcs][1]=op2;
						++nbAddedArcs;
					} else {
						int merdier = 9999;
					}
										
				}
			}
		}
		
	}
	
	public boolean checkCycle(Data data, int op1, int op2) {
		
		// get already added arcs
		int[] nbSuccDisjOp = new int [data.getSize()];
		int[]stackOp = new int [data.getSize()];
		int[][] successorsDisjOp = new int [data.getSize()][data.getSize()];
		boolean[] visitedOp = new boolean[data.getSize()+1];
		
		for (int i = 0; i < data.getSize();++i) {
			nbSuccDisjOp[i]=0;
			visitedOp[i] = false;
		}
		
		//verifier arcs transitif
		//ou verifier le point de depart
		
		
		for (int op=0; op < data.getSize();++op) {// pour toutes les opérations, ajouter les successeurs disjonctifs déjà enregistrés
			nbSuccDisjOp[op]=0;
			for (int arc=0; arc < nbAddedArcs;++arc) {// pour tout arc présent dans la solution
				if (addedArcs[arc][0]==op) { // si la source de l'arc est égal à l'opération courrante alors on ajoute la destination en tant que successeur de l'op
					successorsDisjOp[op][nbSuccDisjOp[op]] = addedArcs[arc][1]; ++nbSuccDisjOp[op];
				}
			}
		}
		//on considère qu'on a vu op1;
		visitedOp[op1] = true;int stacksize = 0;
		if (data.succConj[op1]< data.getSize())	{stackOp[stacksize] = data.succConj[op1]; ++stacksize;}
		stackOp[stacksize] = op2; ++stacksize;
		for (int succOp=0; succOp < nbSuccDisjOp[op1];++succOp) { // on ajoute dans la pile tous les successeurs de l'opération dource déjà vus
			stackOp[stacksize] = successorsDisjOp[op1][succOp]; ++stacksize;
		}
		
		int op = op2; // on commence par le sommet destination de l'arc à ajouter
		
		//ici vérifier si op est ok
		boolean isFailed = false;
		while (stacksize>0 && !isFailed) {
			op = stackOp[stacksize-1];
			--stacksize; visitedOp[op]=true;
			for (int succOp=0; succOp < nbSuccDisjOp[op];++succOp) { // on ajoute à la pile toutes les opérations disjonctives
				stackOp[stacksize] = successorsDisjOp[op][succOp]; ++stacksize;
				if (visitedOp[successorsDisjOp[op][succOp]]) {
					isFailed = true;
				}
			}
			if (data.succConj[op]< data.getSize())	{ // ajout opération conjonctive
				stackOp[stacksize] = data.succConj[op]; ++stacksize;
				if (visitedOp[data.succConj[op]]) {
					isFailed = true;
				}
				
			}
			
			
		}
		
		
		return isFailed;
	}
	
	
	public void insertTrie(int op1, int op2, double quality) {
		int s = 0;
		int e = sizeList;
		int m = (s+e)/2;
		
		while (s < e) {
			m = (s+e)/2;
			if (listPatternQuality[m][2] < quality) {
				s = m+1;
			} else {
				e = m;
			}
			
		}
		sizeList++;
		for (int i=sizeList; i >s; --i) {
			listPatternQuality[i][2]=listPatternQuality[i-1][2];
			listPatternQuality[i][1]=listPatternQuality[i-1][1];
			listPatternQuality[i][0]=listPatternQuality[i-1][0];
		}
		listPatternQuality[s][0]=op1;
		listPatternQuality[s][1]=op2;
		listPatternQuality[s][2]=quality;
		        
	}
	
	
	public void outPatterns(Data data) {

		String toOut = "";
		// respect pattern
		for (int pattern=0; pattern < nbAddedArcs; ++pattern) {
			int opp1 = addedArcs[pattern][0]+1; int opp2 = addedArcs[pattern][1]+1;
			toOut+= "x_"+opp1+"_"+opp2+"\n";
			//cplex.addLe( cplex.diff(st[opp1-1],st[opp2-1]),  -data.getDurationForOp(opp1-1)).setName("c42_"+(opp1)+"_"+(opp2));
		}
		try  {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./INSTANCES_CPLEX/patterns/patterns_"+data.getpName()+"_"+nbSigma+".pat", false)));
		    out.println(toOut);
		    out.close();
		} catch (IOException ioe) {}

	}
	
}


