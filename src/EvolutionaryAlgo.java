
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
        Solution solution2 = new Solution(data);
        Solution solution_temp1 = new Solution(data);
        Solution solution_temp2 = new Solution(data);
        MersenneTwisterFast r = new MersenneTwisterFast(seed);
        //long startTime = System.nanoTime();
        TreeMap<Long, Solution> listSol = new TreeMap<Long, Solution>();
        ArrayList<Solution> listSol2 = new ArrayList<>();
        ArrayList<Solution> listSol3 = new ArrayList<>();
        //int puissanceDix = 1000;

        System.out.println("WithDM : " + withDM);
        System.out.println("SolOpti : " + solOptiConnue);
        boolean optiTrouve = false;

        int RLiter = (int) (Math.log(2.0) * data.getSize() * (data.getSize() - 1));
        for (int j = 0; j < sizePop; j++) {
            Solution newSol = new Solution(data);
            newSol.generer_rand_bierwirth(data, r);
            //LocalSearch.recherche_locale_Stoch_2(newSol, solution_locale, savS, data, r, 10000);//data.getNbIterLS()

            LocalSearch.recherche_locale_naive2(newSol, solution_locale, data, r, RLiter
					);//data.getNbIterLS()
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

        System.out.println(bestSol.getMakespan() + "; Gen : 0; Gen Stable : 0\n");
        int cpt = 1;
        int cptGenStable = 0;
        int[] tab = new int[data.getNbjob()];
        int[] tab2 = new int[data.getNbjob()];
        outerloop:
        while (cpt < nbGen && cptGenStable < nbGenStable && !optiTrouve) {
            for (int i = 0; i < sizePop; i++) {//génération de nouveaux individus
                double w = r.nextDouble();
                double x = r.nextDouble();
                double y = r.nextDouble();
                double z = r.nextDouble();
                int rangParentFav = sizePop/10;
                int a = r.nextInt(rangParentFav);
                une_solution.Copie(listSol2.get(a), data);
                int b = r.nextInt(sizePop);
                while (b == a) {
                    b = r.nextInt(sizePop);
                }
                solution2.Copie(listSol2.get(b), data) ;
                if (w < pc) {//crossover appliqué avec une proba pc

                    int e = r.nextInt(data.getSize());

                    for (int k = 0; k <= e; k++) {
                        tab[une_solution.getVecteur(k)]++;
                        tab2[solution2.getVecteur(k)]++;
                    }

                    solution_temp1.Copie(une_solution, data);
                    solution_temp2.Copie(solution2, data);
                    //int pos = e;
                    int pos1 = 0;
                    int pos2 = 0;
                    int p1 = e + 1;
                    int p2 = e + 1;
                     while (p1 < data.getSize() &&  pos1<data.getSize() ) {
                        /*pos = ((pos + 1) - ((pos + 1) / data.getSize()) * data.getSize());
                        while  (tab[solution2.getVecteur(pos)]== data.getNbmac()) {
                            pos = ((pos + 1) - ((pos + 1) / data.getSize()) * data.getSize());
                        }*/
                        if (tab[solution_temp2.getVecteur(pos1)]< data.getNbmac()) {
                            une_solution.setVecteur(p1, solution_temp2.getVecteur(pos1));
                            tab[solution_temp2.getVecteur(pos1)]++;
                            p1++;
                            pos1++;
                        }
                        else pos1++;

                    }
                    while (p2 < data.getSize() &&  pos2<data.getSize() ) {

                        if (tab2[solution_temp1.getVecteur(pos2)]< data.getNbmac()) {
                            solution2.setVecteur(p2, solution_temp1.getVecteur(pos2));
                            tab2[solution_temp1.getVecteur(pos2)]++;
                            p2++;
                            pos2++;
                        }
                        else pos2++;

                    }
                    //une_solution.checkVecteur(data);
                    //solution2.checkVecteur(data);
                    une_solution.evaluate(data, e);
                    solution2.evaluate(data, e);
                    for (int j=0; j < data.getNbjob(); ++j) {
                        tab[j] = 0;
                        tab2[j] = 0;
                    }
                }
                if (x < pm) { //mutation appliquée avec une proba pm
                    int c = r.nextInt(data.getSize());
                    int d = r.nextInt(data.getSize());
                    Neighbourhood.echanger(une_solution, c, d);
                    une_solution.evaluate(data, (c<d?c:d));
                }
                if (y < pm) {
                    int e = r.nextInt(data.getSize());
                    int f = r.nextInt(data.getSize());
                    Neighbourhood.echanger(solution2, e, f);
                    solution2.evaluate(data, (e<f?e:f));
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
							RLiter, mining, withDM, 100);
                    LocalSearch.recherche_locale_naive_DM2(solution2, solution_locale, data, r,
							RLiter, mining, withDM, 100);
                }
                Solution solCopie = new Solution(data);
                solCopie.Copie(une_solution, data);
                Solution solCopie2 = new Solution(data);
                solCopie2.Copie(solution2, data);
                listSol3.add(solCopie);
                listSol3.add(solCopie2);
                if (solCopie.getMakespan() ==solOptiConnue) {
                    bestSol.Copie(solCopie, data);
                    cpt++;
                    break outerloop;
                }
                if (solCopie2.getMakespan() ==solOptiConnue) {
                    bestSol.Copie(solCopie2, data);
                    cpt++;
                    break outerloop;
                }

            }
            Collections.sort(listSol3);
            //ArrayList<Solution> best300Sol = new ArrayList<Solution>(listSol3.subList(0, sizePop));
            listSol2.clear();
            listSol2.addAll(listSol3.subList(0, sizePop));
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
