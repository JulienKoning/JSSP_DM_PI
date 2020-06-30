/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OUT_RES {

	public String name_Instance[];										// nom de l'instance
	public int lower_bound[];											// nom de l'instance
	public double tt_Instance_X_Replication[][] ;			// temps total d'execution pour la replication
	public double ttb_Instance_X_Replication[][] ;		// temps d'execution pour arriver à la meilleure solution pour la replication
	public long s_Instance_X_Replication[][] ;			// solution found
	public int nb_Opt[];												// solution found
	public double deviance_Instance_X_Replication[][] ;	// deviance
	
	public double avg_TTB_Instance[];								// temps d'execution moyen pour arriver à la meilleure solution pour la replication
	public double avg_TT_Instance[];								// temps d'execution moyen pour arriver à la meilleure solution pour la replication
	public double avg_S_Instance[];									// temps d'execution moyen pour arriver à la meilleure solution pour la replication
	public double avg_DEV_Instance[];	
	public String descenteVal_X_replication[][];
	public String descenteTime_X_replication[][];
	
	public OUT_RES() {
		int max_instances = 100;
		int nbreplic = 11;
		name_Instance = new String[max_instances];
		lower_bound = new int[max_instances];
		tt_Instance_X_Replication = new double[max_instances][nbreplic];
		ttb_Instance_X_Replication = new double[max_instances][nbreplic];
		s_Instance_X_Replication = new long[max_instances][nbreplic];
		nb_Opt = new int[max_instances];
		deviance_Instance_X_Replication = new double[max_instances][nbreplic];
		descenteVal_X_replication = new String[max_instances][nbreplic];
		descenteTime_X_replication = new String[max_instances][nbreplic];
		avg_TTB_Instance= new double[max_instances];							
		avg_TT_Instance= new double[max_instances];									
		avg_S_Instance= new double[max_instances];										
		avg_DEV_Instance= new double[max_instances];	
		
		for (int i = 0; i < max_instances; ++i) {
			avg_S_Instance[i] = 0;
			avg_TTB_Instance[i] = 0;
			avg_TT_Instance[i] = 0;
			avg_DEV_Instance[i] = 0;
			nb_Opt[i] = 0;
			lower_bound[i] = 1;
		}
	}
	
	public void stock_Res(Data data, Solution sol, int instance, int replic) {
		
		tt_Instance_X_Replication[instance][replic] = sol.getTotalTime();
		ttb_Instance_X_Replication[instance][replic]= sol.getTimeToBest();
		
		s_Instance_X_Replication[instance][replic]=sol.getMakespan();
		deviance_Instance_X_Replication[instance][replic]= (((float) sol.getMakespan()-data.bks)/data.bks)*100;

		avg_S_Instance[instance]+=sol.getMakespan();		
		avg_TTB_Instance[instance]+=ttb_Instance_X_Replication[instance][replic];
		avg_TT_Instance[instance]+=tt_Instance_X_Replication[instance][replic];
		avg_DEV_Instance[instance]+=deviance_Instance_X_Replication[instance][replic];
		if (sol.getMakespan() == data.bks) ++nb_Opt[instance]; 
		descenteTime_X_replication[instance][replic] = "";
		descenteVal_X_replication[instance][replic] = "";
		TreeMap<Long, Long> descente = sol.getCourbeDescente();
		while (descente.size()>0) {
			java.util.Map.Entry<Long, Long> e = descente.pollFirstEntry();
			descenteVal_X_replication[instance][replic] += e.getValue()+";";
			descenteTime_X_replication[instance][replic] += e.getKey()+";";
		}
	}
	
	public void out_Results_CSV(Data data, int end) {
		double avg_S = 0, avg_TTB = 0, avg_TT = 0, avg_DEV = 0, avg_LB = 0, avg_BestValue = 0, avg_TTBValue = 0, avg_DEVBestValue = 0;
		long bestValue; double bestTime;
		String toOut_light = "";
		String toOut = "";
		String s1, s2, s3, s4, s5, s6, s7, s8;
		
		toOut_light += "INSTANCE ;  LB ; S ; TT_S ; TTB_S ; Dev_S ; BFS ; TTB_BFS ; Dev_BFS ; Nb_OPT/" + data.MAX_REPLICATIONS + "\n";
		for (int inst = 0; inst <= end; ++inst) {
			bestValue = data.INFINITE_C;
			bestTime = data.INFINITE_C;
			avg_LB += lower_bound[inst];
			//avg_S_Instance[inst] = avg_S_Instance[inst] / data.MAX_REPLICATIONS;
			//avg_TT_Instance[inst] = avg_TT_Instance[inst] / data.MAX_REPLICATIONS;
			//avg_TTB_Instance[inst] = avg_TTB_Instance[inst] / data.MAX_REPLICATIONS;
			//avg_DEV_Instance[inst] = (((float)avg_S_Instance[inst] - lower_bound[inst]) / lower_bound[inst]) * 100;

			toOut += name_Instance[inst] + " ; " + " LB = " + lower_bound[inst] + "\n";
			toOut += "Replication ;  S ; TT_S ; TTB_S ; Dev_S  \n";
			for (int i = 0; i<data.MAX_REPLICATIONS; ++i) {
				s1 = Double.toString(tt_Instance_X_Replication[inst][i]);		s1 = s1.replace(".", ",");
				s2 = Double.toString(ttb_Instance_X_Replication[inst][i]);		s2 = s2.replace(".", ",");
				s3 = Double.toString(deviance_Instance_X_Replication[inst][i]); s3 = s3.replace(".", ",");

				toOut += i + 1 + " ; " + s_Instance_X_Replication[inst][i] + " ; " + s1 + " ; " + s2 + " ; " + s3 + "\n";
				toOut += ";;"+descenteTime_X_replication[inst][i] +"\n";
				toOut += ";;"+descenteVal_X_replication[inst][i] +"\n";
				if (s_Instance_X_Replication[inst][i] <= bestValue) {
					bestValue = s_Instance_X_Replication[inst][i];
					if (bestTime > ttb_Instance_X_Replication[inst][i]) bestTime = ttb_Instance_X_Replication[inst][i];
				}
			}

			avg_S += avg_S_Instance[inst]/ data.MAX_REPLICATIONS;
			avg_TT += avg_TT_Instance[inst]/ data.MAX_REPLICATIONS;
			avg_TTB += avg_TTB_Instance[inst]/ data.MAX_REPLICATIONS;
			avg_DEV += avg_DEV_Instance[inst]/ data.MAX_REPLICATIONS;
			avg_BestValue += bestValue;
			avg_TTBValue += bestTime;

			s1 = Double.toString(avg_S_Instance[inst]/ data.MAX_REPLICATIONS);		s1 = s1.replace(".", ",");
			s2 = Double.toString(avg_TT_Instance[inst]/ data.MAX_REPLICATIONS);		s2 = s2.replace(".", ",");
			s3 = Double.toString(avg_TTB_Instance[inst]/ data.MAX_REPLICATIONS);		s3 = s3.replace(".", ",");
			s4 = Double.toString(avg_DEV_Instance[inst]/ data.MAX_REPLICATIONS);		s4 = s4.replace(".", ",");
			s5 = Long.toString(bestValue);
			s6 = Double.toString(bestTime);						s6 = s6.replace(".", ",");
			s7 = Float.toString(((((float)bestValue - lower_bound[inst]) / lower_bound[inst]) * 100));	s7 = s7.replace(".", ",");

			avg_DEVBestValue += (((float)bestValue - lower_bound[inst]) / lower_bound[inst]) * 100;
			toOut += "Average : \n";
			toOut += " - " + " ; " + s1 + " ; " + s2 + " ; " + s3 + " ; " + s4 + " ; " + "\n\n\n";

			toOut_light += name_Instance[inst] + " ; " + lower_bound[inst] + " ; " + s1 + " ; " + s2 + " ; " + s3 + " ; " + s4 + " ; " + s5 + " ; " + s6 + " ; " + s7 + " ; " + nb_Opt[inst] + "\n";

		}

		s1 = Double.toString(avg_S / data.MAX_INSTANCES);			s1 = s1.replace(".", ",");
		s2 = Double.toString(avg_TT / data.MAX_INSTANCES);			s2 = s2.replace(".", ",");
		s3 = Double.toString(avg_TTB / data.MAX_INSTANCES);			s3 = s3.replace(".", ",");
		s4 = Double.toString(avg_DEV / data.MAX_INSTANCES);			s4 = s4.replace(".", ",");
		s5 = Double.toString(avg_BestValue / data.MAX_INSTANCES);	s5 = s5.replace(".", ",");
		s6 = Double.toString(avg_TTBValue / data.MAX_INSTANCES);		s6 = s6.replace(".", ",");
		s7 = Double.toString(avg_DEVBestValue / data.MAX_INSTANCES);	s7 = s7.replace(".", ",");
		s8 = Double.toString(avg_LB / data.MAX_INSTANCES);			s8 = s8.replace(".", ",");

		toOut_light += "Average : \n";
		toOut_light += " - " + " ; " + s8 + " ; " + s1 + " ; " + s2 + " ; " + s3 + " ; " + s4 + " ; " + s5 + " ; " + s6 + " ; " + s7 + " ; " + "\n";

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("results.csv", true)));
		    out.println(toOut);
		    out.close();
		    
		    PrintWriter outlight = new PrintWriter(new BufferedWriter(new FileWriter("results_light.csv", true)));
		    outlight.println(toOut_light);
		    outlight.close();
		} catch (IOException ioe) {};
	}
	
}
