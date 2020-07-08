/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author damien.lamy
 */
public class LocalSearch {


	// recherche locale basée sur les opérations critiques
	public static void recherche_locale_Stoch(Solution une_solution, Solution solution_locale, Data data, MersenneTwisterFast r, double itermax) {
		// variables locales
		une_solution.compute_lPath(data); // on récupère les ops critiques
		int cpt = 0;boolean isOk = false;
		while (/*!isOk &&*/ cpt++ < itermax) {
			isOk = true;
			
			long mkpn = une_solution.getMakespan(); int savI = 0; boolean isSecondOk = false;
			for (int i=0; i<une_solution.getNbOpCritique();++i) {
				//int k = r.nextInt(une_solution.getNbOpCritique()); //si on veut choisir aléatoirement
				int[] op = une_solution.getOpDisj(i);
				solution_locale.Copie(une_solution, data); // on sauvegarde la solution connue
				// on inverse les donnees dans la solution locale
				Neighbourhood.echanger(solution_locale, une_solution.getPosition(op[1]), une_solution.getPosition(op[0]));
				//solution_locale.exchange_op(data, op[0], op[1]);
				solution_locale.evaluate(data, une_solution.getPosition(op[0]));
				if (solution_locale.getMakespan() < mkpn) {
					mkpn = solution_locale.getMakespan();
					savI = i;
					une_solution.Copie(solution_locale, data);
					une_solution.compute_lPath(data);
					isOk = false;
					//i=0;
					//cpt=0;
				} else if (solution_locale.getMakespan() == mkpn && r.nextDouble()<0.5) {
					mkpn = solution_locale.getMakespan();
					savI = i;
					isSecondOk = true;
					//cpt = 0;
				}
			}

			if (!isOk) {
				int[] op = une_solution.getOpDisj(savI);
				Neighbourhood.echanger(une_solution, une_solution.getPosition(op[1]), une_solution.getPosition(op[0]));
				une_solution.evaluate(data, une_solution.getPosition(op[0]));
				une_solution.compute_lPath(data);
			} else if (isSecondOk) {
				int[] op = une_solution.getOpDisj(savI);
				Neighbourhood.echanger(une_solution, une_solution.getPosition(op[1]), une_solution.getPosition(op[0]));
				une_solution.evaluate(data, une_solution.getPosition(op[0]));
				une_solution.compute_lPath(data);
				++cpt;
				isOk = false;
			}
		}
	}

	// ajout composante mining
	public static void recherche_locale_Stoch_DM2(Solution une_solution, Solution solution_locale, Data data, Mining mining, MersenneTwisterFast r, double itermax) {
		// variables locales
		if (mining.getIsUsed() && mining.nbAddedArcs >0) {
			une_solution.compute_lPath_Mining(data, mining); //cette procédure n'ajoutera que les opérations non critiques à la liste
			// variables locales
			int cpt = 0;boolean isOk = false;
			while (!isOk && cpt < itermax) {
				isOk = true;
				//if (une_solution.getNbOpCritique() != 0) {
				long mkpn = une_solution.getMakespan(); int savI = 0; boolean isSecondOk = false;
				for (int i=0; i<une_solution.getNbOpCritique();++i) {
					//int k = r.nextInt(une_solution.getNbOpCritique());
					int[] op = une_solution.getOpDisj(i);
					solution_locale.Copie(une_solution, data); // on sauvegarde la solution connue
					// on inverse les donnÃ©es dans la solution locale
					Neighbourhood.echanger(solution_locale, une_solution.getPosition(op[1]), une_solution.getPosition(op[0]));
					//solution_locale.exchange_op(data, op[0], op[1]);
					solution_locale.evaluate(data, une_solution.getPosition(op[0]));
					if (solution_locale.getMakespan() < mkpn) {
						mkpn = solution_locale.getMakespan();
						savI = i;
						//une_solution.Copie(solution_locale, data);
						//une_solution.compute_lPath(data);
						isOk = false;
						//i=0;
						//cpt=0;
					} else if (solution_locale.getMakespan() == mkpn && r.nextDouble()<0.5) { // on bouge de manière randomisée
						mkpn = solution_locale.getMakespan();
						savI = i;
						isSecondOk = true;
						//cpt = 0;
					}
				}

				//mise à jour solution
				if (!isOk) {
					int[] op = une_solution.getOpDisj(savI);
					Neighbourhood.echanger(une_solution, une_solution.getPosition(op[1]), une_solution.getPosition(op[0]));
					une_solution.evaluate(data, une_solution.getPosition(op[0]));
					une_solution.compute_lPath_Mining(data, mining);
				} else if (isSecondOk) {
					int[] op = une_solution.getOpDisj(savI);
					Neighbourhood.echanger(une_solution, une_solution.getPosition(op[1]), une_solution.getPosition(op[0]));
					une_solution.evaluate(data, une_solution.getPosition(op[0]));
					une_solution.compute_lPath_Mining(data, mining);
					++cpt;
					isOk = false;
				}
			}
		} else {
			// si pas mining alors on utilise la RL classique
			recherche_locale_Stoch(une_solution, solution_locale, data, r, itermax);
		}
	}



	// recherche locale full random
	public static int recherche_locale_naive2(Solution une_solution,  Solution solution_locale, Data data, MersenneTwisterFast r, int nbiter) {
		// variables locales
		int cpt = 0;
		int cptTotal=0;
		// tant qu'on n'a pas tout parcouru
		while (cpt++ < nbiter) {
			cptTotal++;
			int a = r.nextInt(data.getSize());
			int b = r.nextInt(data.getSize());
			// tant qu'on n'a pas deux jobs differents boolean respectPattern=true;
			boolean respect=true;
			for (int i = a+1; i < b; i++) {
				if (une_solution.getVecteur(a) == une_solution.getVecteur(i) ||une_solution.getVecteur(i) == une_solution.getVecteur(b) ) {
					respect=false;
				}
			}
			while (une_solution.getVecteur(a) == une_solution.getVecteur(b) || respect==false) {
				a = r.nextInt(data.getSize());
				b = r.nextInt(data.getSize());		// on regenere un entier
				respect=true;
				for (int i = a+1; i < b; i++) {
					if (une_solution.getVecteur(a) == une_solution.getVecteur(i) ||une_solution.getVecteur(i) == une_solution.getVecteur(b) ) {
						respect=false;
					}
				}
			}
			solution_locale.Copie(une_solution, data); // on sauvegarde la solution connue
			// on inverse les donnÃ©es dans la solution locale
			Neighbourhood.echanger(solution_locale, a, b);
			// exchange_op(solution_locale, data, pere, cour);
			// solution_locale.evaluate(data, adN2, false);
			solution_locale.evaluate(data, a<b?a:b);
			// evaluate(solution_locale, data, 0, false);

			// si le cout de la solution locale est meilleur que celui de la solution connue
			if (solution_locale.getMakespan() < une_solution.getMakespan()) {
				// on modifie la solution
				une_solution.Copie(solution_locale, data);
				//System.out.println(solution_locale.getMakespan()+" "+cptTotal);
				// on reaffecte cour et pere
				cpt = 0;
			} else if (solution_locale.getMakespan() == une_solution.getMakespan()) {
				// on modifie la solution
				une_solution.Copie(solution_locale, data);

				// on reaffecte cour et pere
			}

		}
		return cptTotal;
	}

	// variante Mining
	public static long[] recherche_locale_naive_DM2(Solution une_solution,  Solution solution_locale, Data data, MersenneTwisterFast r, int nbiter, Mining mining, boolean doDM, int printEveryNbit) {
		// variables locales
		//int cpt = 0;
		int cptTotal=0;
		long[] res=new long[nbiter/printEveryNbit];
		// tant qu'on n'a pas tout parcouru
		if (doDM) {

			while (cptTotal < nbiter) {
				cptTotal++;

				//recoding
				int first = r.nextInt(data.getSize());
				int minSecond=first;
				int maxSecond=first;
				boolean searchMax=true;
				boolean searchMin=true;
				do { //could be improved
					first = r.nextInt(data.getSize());
					minSecond=first;
					maxSecond=first;
					searchMax=true;
					searchMin=true;
					while (searchMin || searchMax) {
						//maxSec
						if (maxSecond>=data.getSize()-1 ||une_solution.getVecteur(maxSecond+1) == une_solution.getVecteur(first)) {
							searchMax=false;
						}else if(doDM && mining.pattern[une_solution.getOpNumber(first)][une_solution.getOpNumber(maxSecond+1)] ) {
							searchMax=false;
						}else {
							maxSecond++;
						}
						//minSec
						if (minSecond<=0 ||une_solution.getVecteur(minSecond-1) == une_solution.getVecteur(first)) {
							searchMin=false;
						}else if(doDM && mining.pattern[une_solution.getOpNumber(minSecond-1)][une_solution.getOpNumber(first)] ) {
							searchMin=false;
						}else {
							minSecond--;
						}
					}

				}while(minSecond==maxSecond) ;
				//pick a random number in the good range
				int second=minSecond+r.nextInt(maxSecond-minSecond);
				if (second>=first) {
					second++;
				}

				solution_locale.Copie(une_solution, data); // on sauvegarde la solution connue
				// on inverse les donnÃ©es dans la solution locale
				Neighbourhood.echanger(solution_locale, first, second);
				solution_locale.evaluate(data, (first<second?first:second));
				// evaluate(solution_locale, data, 0, false);

				// si le cout de la solution locale est meilleur que celui de la solution connue
				if (solution_locale.getMakespan() < une_solution.getMakespan()) {
					// on modifie la solution
					une_solution.Copie(solution_locale, data);	
					cptTotal = 1;//test avec nbit fixe

				} else if (solution_locale.getMakespan() == une_solution.getMakespan()) {
					// on modifie la solution
					une_solution.Copie(solution_locale, data);

					// on reaffecte cour et pere
				}
				if (cptTotal%printEveryNbit==0) {
					res[(cptTotal/printEveryNbit)-1]=une_solution.getMakespan();

					//System.out.print(une_solution.getMakespan()+"\t");
				}
			}
		} else {
			recherche_locale_naive2(une_solution, solution_locale, data, r, nbiter);
		}
		return res;
	}
}

