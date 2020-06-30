
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import javax.annotation.Generated;

/**
 *
 * @author damien.lamy
 */
public class Metaheuristic {

	/**
	 *
	 * @param data
	 * @param log
	 * @param maxGRASP
	 * @param maxELS
	 * @param maxNeighbour
	 * @param maxTime
	 * @return
	 */
	public static Solution GRASP_ELS(Data data, Logger log, int maxGRASP, int maxELS, int maxNeighbour, int sizePopInit, int maxTime, int seed, Boolean withDM) {
		// on cree une population INITIALE de taille sizePopInit
		data.withDM=withDM;
		data.puissMax = 10;
		Solution best_solution = new Solution(data);
		Solution une_solution = new Solution(data);
		Solution un_voisin = new Solution(data);
		Solution best_voisin = new Solution(data);
		Solution solCopie = new Solution(data);
		Mining mining = new Mining(data);
		Solution solution_locale = new Solution(data);
		MersenneTwisterFast r = new MersenneTwisterFast(seed);
		long startTime = System.nanoTime();
		TreeMap<Long, Solution> listSol = new TreeMap<Long, Solution>();
		int puissanceDix = 1000;

		best_solution.setIsUsed(false);

		// --------------------------------------------------------------------------------------------------------------------------------
		// generation of sizePop start solutions
		// --------------------------------------------------------------------------------------------------------------------------------
		if (withDM) {
			for (int j = 0; j < sizePopInit; j++) {
				Solution newSol = new Solution(data);
				newSol.generer_rand_bierwirth(data, r);
				//LocalSearch.recherche_locale_Stoch_2(newSol, solution_locale, savS, data, r, 10000);//data.getNbIterLS()

				LocalSearch.recherche_locale_naive2(newSol, solution_locale, data, r, 10000);//data.getNbIterLS()
				listSol.put(newSol.getMakespan() * sizePopInit * data.puissMax + j, newSol);
				if (!best_solution.getIsUsed() || newSol.getMakespan() < best_solution.getMakespan()) {
					best_solution.Copie(newSol, data);
					best_solution.setCourbeDescente(best_solution.getTimeToBest(), best_solution.getMakespan());
					//System.out.println("value : " + best_solution.getMakespan());
				}

			}

			mining.setPatternMemoire(Mining.checkReverse(data, sizePopInit, data.getSize(), listSol, 40.0, 30.0, 2)); // avec memoire
			mining.addPatterns(data);
		}

		int i = 0;
		while (++i < maxGRASP && TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) < data.getTimeMax()) {

			une_solution.generer_rand_bierwirth(data, r);


			//LocalSearch.recherche_locale_Stoch(une_solution, solution_locale, data,  r, (int)(50)); // recherche locale sur le voisin
			LocalSearch.recherche_locale_naive_DM2(une_solution, solution_locale, data, r, (int)(20*data.getSize()), mining, true, 100); //e voisin


			if (!best_solution.getIsUsed() || une_solution.getMakespan() < best_solution.getMakespan()) {
				best_solution.Copie(une_solution, data);
				// log.log(Level.INFO, Long.toString(best_solution.getMakespan()) + " - iter: "
				// + Integer.toString(i) + "\n");

			}

			int j = 0;
			while (++j < maxELS && TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) < data.getTimeMax()) {
				best_voisin.setIsUsed(false);
				int k = 0;

				while (++k < maxNeighbour && TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS) < data.getTimeMax()) {
					// Neighbourhood.generer_voisin(une_solution, un_voisin, data, r);
					Neighbourhood.generer_MultiVoisin(un_voisin, une_solution,data, r, 3, 5);

					// LocalSearch.recherche_locale(un_voisin, solution_locale, data); // recherche
					//LocalSearch.recherche_locale_Nowi(un_voisin, solution_locale, data, mining, r, 100);
					//LocalSearch.local_search_Nowicki(data, un_voisin, solution_locale);
					// locale sur le voisin
					//LocalSearch.recherche_locale_Stoch(un_voisin, solution_locale, data, r, (50)); // recherche locale sur le voisin
					//LocalSearch.recherche_locale_naive2(un_voisin, solution_locale, data, r, (int)(50*data.getSize())); // recherche locale sur le voisin
					LocalSearch.recherche_locale_naive_DM2(un_voisin, solution_locale, data, r, (int)(20*data.getSize()), mining, true, 100); //e voisin
					//LocalSearch.recherche_locale_naive2(un_voisin, solution_locale, data, r, (int)(0.7*data.getSize()*(data.getSize()-1))); //e voisin

					//LocalSearch.recherche_locale_Stoch_DM2(un_voisin, solution_locale, data, mining, r, 10*data.getSize()); // recherche locale sur le voisin
					if (!best_voisin.getIsUsed() || un_voisin.getMakespan() < best_voisin.getMakespan()) { // si le cout est amelioré, on sauvegarde le voisin

						best_voisin.Copie(un_voisin, data);

					}
				}
				une_solution.Copie(best_voisin, data);

				if (une_solution.getMakespan() < best_solution.getMakespan()) {
					best_solution.Copie(une_solution, data);
					// best_solution.writeGANTT(data);
					log.log(Level.INFO, Long.toString(best_solution.getMakespan())) ;
					// + Integer.toString(i) + "_" + Integer.toString(j) + "\n");
					// writeGANTT(data, best_solution);
					// j=0; //Experiment - si on a amélioré la meilleure solution on s'autorise à
					// explorer davantage l'espace des solutions
				}
			}
		}
		// writeGANTT(data, best_solution);
		best_solution.setTotalTime(TimeUnit.SECONDS.convert(System.nanoTime() - best_solution.getStartTime(), TimeUnit.NANOSECONDS));
		best_solution.writeGANTT_SVG(data);
		log.log(Level.INFO, Long.toString(best_solution.getMakespan()) +"_"+best_solution.getTotalTime()+ "\n");

		return best_solution;
	}


	public static Solution cplex_t(Data data, Logger log, int nbELS, int sizePopInit, int nbDescentes, int nbMaxVoisinEls, Boolean withDM, double nbSigma) {

		// on cree une population INITIALE de taille sizePopInit
		// Population maPop;
		// maPop = new Population();
		long startTime = System.nanoTime();
		data.puissMax = 10;
		TreeMap<Long, Solution> listSol = new TreeMap<Long, Solution>();
		Mining mining = new Mining(data);
		Solution best_solution = new Solution(data);
		Solution solution_locale = new Solution(data);
		Solution bestELS = new Solution(data);
		Solution solloc = new Solution(data);
		Solution savS = new Solution(data);
		//int puissanceDix = 1000000;

		MersenneTwisterFast r = new MersenneTwisterFast(0);

		best_solution.setIsUsed(false);

		// --------------------------------------------------------------------------------------------------------------------------------
		// generation of sizePop start solutions
		// --------------------------------------------------------------------------------------------------------------------------------

		String workingDir = System.getProperty("user.dir");
		//System.out.println("Current working directory : " + workingDir);
		sizePopInit=50*data.getSize();
		sizePopInit = 300; //1000
		System.out.println("time before LS : " + TimeUnit.SECONDS.convert(System.nanoTime()- startTime, TimeUnit.NANOSECONDS));
		System.out.println("-------------------------");
		for (int j = 0; j < sizePopInit; j++) {
			//MersenneTwisterFast rr = new MersenneTwisterFast(j);
			Solution newSol = new Solution(data);
			newSol.generer_rand_bierwirth(data, r);
			//LocalSearch.recherche_locale_Stoch(newSol, solution_locale, data, r, 10000);//data.getNbIterLS()
			//LocalSearch.recherche_locale_naive2(newSol, solution_locale, data, rr, 10000);//data.getNbIterLS()
			//LocalSearch.recherche_locale_Stoch(newSol, solution_locale, data, rr, 10000);//data.getNbIterLS()
			//LocalSearch.recherche_locale_Stoch_2(newSol, solution_locale, savS, data, r, 10000);//data.getNbIterLS()
			//LocalSearch.recherche_locale_Stoch_Insert(newSol, solution_locale, savS, data, rr, 10000);
			LocalSearch.recherche_locale_naive2(newSol, solution_locale, data, r, 1000);//data.getNbIterLS()
			//LocalSearch.recherche_locale_Stoch_Insert(newSol, solution_locale, savS, data, r, 10000);
			//LocalSearch.recherche_locale_naive(newSol, solution_locale, data, r,* 10000);//data.getNbIterLS()
			listSol.put(newSol.getMakespan() * sizePopInit * data.puissMax + j, newSol);
			if (!best_solution.getIsUsed() || newSol.getMakespan() < best_solution.getMakespan()) {
				best_solution.Copie(newSol, data);
				best_solution.setCourbeDescente(best_solution.getTimeToBest(), best_solution.getMakespan());
				//System.out.println("value : " + best_solution.getMakespan());
			}

		}


		System.out.println("time after LS : " + TimeUnit.SECONDS.convert(System.nanoTime()- startTime, TimeUnit.NANOSECONDS));
		if (withDM) { // on r�cup�re les patterns
			mining.setPatternMemoire(Mining.checkReverse(data, sizePopInit, data.getSize(), listSol, 40.0, 30.0, nbSigma)); // avec memoire
			System.out.println("time after ML : " + TimeUnit.SECONDS.convert(System.nanoTime()- startTime, TimeUnit.NANOSECONDS));
			mining.addPatterns(data);
			int tutu = mining.getNbPattern();

			CPO_JSP cpo = new CPO_JSP(data, mining);
			//ProgLin pl = new ProgLin(data);
			//pl.build_JSP_Mining(data, mining, 600, true);
			//pl.writeLP_JSP(data, mining, true);
			//pl.execLP(data);
		}
		System.out.println("Killed Patterns : " + mining.avoidedPatterns );
		System.out.println("Added Patterns : " + mining.nbAddedArcs );
		return best_solution;

	}


}
