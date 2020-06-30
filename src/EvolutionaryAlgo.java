
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.min;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * @author damien.lamy
 */
public class EvolutionaryAlgo {

    public void GeneticAlgorithm() {

    }

    /**
     * @param data
     * @param log
     * @param withDM       : true if Data Mining selected
     * @param typeElection : choose between elitist election and weird one (Matthieu
     *                     Gondran)
     * @param pc           : crossover probability
     * @param pm           : mutation probability
     * @param pls          : local search probability
     * @param sizePop      : size of population
     * @param nbGen        : number of iterations in memetic algorithm
     * @param els1         : parameter if ELS based local search
     * @param els2         : parameter if ELS based local search
     * @param els3         : parameter if ELS based local search
     * @param seed         : seed for the MersenneTwister
     * @param nbGenStable  : maximum of stable generation
	 * @param solOptiConnue
     * @return
     */
    public static Solution MemeticAlgo(Data data, Logger log, Boolean withDM, Boolean typeElection, double pc,
									   double pm, double pls, int sizePop, int nbGen, int els1, int els2, int els3,
									   int seed, int nbGenStable, long solOptiConnue) {
        // ------------------------------------------
        // dispatch parameters
        // ------------------------------------------

        data.withDM = withDM;
        data.puissMax = 10;
        Solution bestSol = new Solution(data);
        Solution une_solution = new Solution(data);
        Mining mining = new Mining(data);
        Solution solution_locale = new Solution(data);
        MersenneTwisterFast r = new MersenneTwisterFast(seed);
        long startTime = System.nanoTime();
        TreeMap<Long, Solution> listSol = new TreeMap<Long, Solution>();
        ArrayList<Solution> listSol2 = new ArrayList<>();
        ArrayList<Solution> listSol3 = new ArrayList<>();
        int puissanceDix = 1000;

        System.out.println("WithDM : " + withDM);
        boolean optiTrouve = false;
        for (int j = 0; j < sizePop; j++) {
            Solution newSol = new Solution(data);
            newSol.generer_rand_bierwirth(data, r);
            //LocalSearch.recherche_locale_Stoch_2(newSol, solution_locale, savS, data, r, 10000);//data.getNbIterLS()

            LocalSearch.recherche_locale_naive2(newSol, solution_locale, data, r,
					(int) (Math.log(2.0) * data.getSize() * (data.getSize() - 1)));//data.getNbIterLS()
            listSol.put(newSol.getMakespan() * 300 * data.puissMax + j, newSol);
            listSol2.add(newSol);
            if (!bestSol.getIsUsed() || newSol.getMakespan() < bestSol.getMakespan()) {
                bestSol.Copie(newSol, data);
                bestSol.setCourbeDescente(bestSol.getTimeToBest(), bestSol.getMakespan());
                //System.out.println("value : " + bestSol.getMakespan());
                if (bestSol.getMakespan() == solOptiConnue) {
                           	optiTrouve = true;
                }
            }

        }
        if (withDM) {
            mining.setPatternMemoire(Mining.checkReverse(data, 300, data.getSize(), listSol, 40.0, 30.0, 2)); // avec
			// memoire
            mining.addPatterns(data);
            System.out.println("Nb Patterns : " + mining.getNbPattern());
        }

        System.out.println("Valeur initale bestSol : " + bestSol.getMakespan());
        int cpt = 0;
        int cptGenStable = 0;
        outerloop:
        while (cpt < nbGen && cptGenStable < nbGenStable && !optiTrouve) {
            for (int i = 0; i < 2 * sizePop; i++) {//génération de 600 nouveaux individus
                int a = i - ((i / sizePop) * sizePop);
                double x = r.nextDouble();
                double y = r.nextDouble();
                double z = r.nextDouble();
                une_solution.Copie(listSol2.get(a), data);
                if (x < pc) {//crossover appliqué avec une proba pc
                    int d = r.nextInt(sizePop);
                    while (d == a) {
                        d = r.nextInt(sizePop);
                    }
                    Solution solution2 = listSol2.get(d);
                    int e = r.nextInt(data.getSize());
                    int[] tab = new int[data.getNbjob()];

                    for (int k = 0; k <= e; k++) {
                        tab[une_solution.getVecteur(k)]++;
                    }
                    int pos = e;
                    for (int k = e + 1; k < data.getSize(); k++) {
                        pos = ((pos + 1) - ((pos + 1) / data.getSize()) * data.getSize());
                        while (tab[solution2.getVecteur(pos)] == data.getNbmac()) {
                            pos = ((pos + 1) - ((pos + 1) / data.getSize()) * data.getSize());
                        }

                        une_solution.setVecteur(k, solution2.getVecteur(pos));
                        tab[solution2.getVecteur(pos)]++;
                    }
                    une_solution.checkVecteur(data);
                    une_solution.evaluate(data, e);
                }
                if (y < pm) { //mutation appliquée avec une proba pm
                    int b = r.nextInt(data.getSize());
                    int c = r.nextInt(data.getSize());
                    Neighbourhood.echanger(une_solution, b, c);
                    une_solution.evaluate(data, min(b, c));
                }
                if (z < pls) {
					/*if (withDM) {
						LocalSearch.recherche_locale_Stoch_DM2(une_solution, solution_locale, data, mining, r, (int)
						Math.log(2.0)*data.getSize()*(data.getSize()-1));
					} else {
						LocalSearch.recherche_locale_Stoch(une_solution, solution_locale, data, r, (int)Math.log(2.0)
						*data.getSize()*(data.getSize()-1));
					}*/
                    LocalSearch.recherche_locale_naive_DM2(une_solution, solution_locale, data, r,
							(int) (Math.log(2.0) * data.getSize() * (data.getSize() - 1)), mining, withDM, 100);
                }
                Solution solCopie = new Solution(data);
                solCopie.Copie(une_solution, data);
                listSol3.add(solCopie);
                if (solCopie.getMakespan() ==solOptiConnue) {
                    bestSol.Copie(solCopie, data);
                    cpt++;
                    break outerloop;
                }

            }
            Collections.sort(listSol3);
            ArrayList<Solution> best300Sol = new ArrayList<Solution>(listSol3.subList(0, sizePop));
            listSol2.clear();
            listSol2.addAll(best300Sol);
            listSol3.clear();
            if (listSol2.get(0).getMakespan() < bestSol.getMakespan()) {
                bestSol.Copie(listSol2.get(0), data);
                System.out.println(bestSol.getMakespan() + "; Gen : " + cpt + "; Gen Stable : " + cptGenStable + "\n");
                cptGenStable = 0;
            } else cptGenStable++;

            cpt++;
        }
        bestSol.setTotalTime(System.nanoTime());
        //bestSol.writeGANTT_SVG(data);
        System.out.println(bestSol.getMakespan() + "_" + bestSol.getTotalTime() + "; Gen : " + cpt + "; Gen Stable : " + cptGenStable + "\n");
        return bestSol;
    }
}
