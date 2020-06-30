/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author damienl
 *
 * Méthodes utilisables
 */
public class Neighbourhood {

    public static final Logger log = Logger.getLogger(Main.class.getName());

    /* -------------------------------------------------------------------------------------------------------------------------
     ----------------------------------------------------------------------------------------------------------------------------
                                                            SWAP METHODS
     ----------------------------------------------------------------------------------------------------------------------------
     --------------------------------------------------------------------------------------------------------------------------*/
    // permutation dans Bierwirth
    public static void echanger(Solution une_solution, int indice1, int indice2) {
        int tmp = une_solution.getVecteur(indice1);
        une_solution.setVecteur(indice1, une_solution.getVecteur(indice2));
        une_solution.setVecteur(indice2, tmp);
    }
    //insertion dans bierwirth
    public static void insertion(Solution une_solution, Data data, int positionInsert, int op) {
        int tmp = data.getJobForOp(op);
        int posOp = une_solution.getPosition(op);
        
        if (posOp>positionInsert) {
        	for (int i = posOp;i > positionInsert; --i) {
            	une_solution.vecteur[i]=une_solution.vecteur[i-1];
            }
        	une_solution.vecteur[positionInsert]=data.getJobForOp(op);
        } else {
        	for (int i = posOp;i < positionInsert; ++i) {
            	une_solution.vecteur[i]=une_solution.vecteur[i+1];
            }
        	une_solution.vecteur[positionInsert]=data.getJobForOp(op);
        }
        
    }

    
 /* -------------------------------------------------------------------------------------------------------------------------
     ----------------------------------------------------------------------------------------------------------------------------
                                                            NEIGHBOUR GENERATION
     ----------------------------------------------------------------------------------------------------------------------------
     --------------------------------------------------------------------------------------------------------------------------*/
    // generation d'un voisin
    public static void generer_voisin(Solution une_solution, Solution voisin, Data data, MersenneTwisterFast r) {
        // on genere deux entiers aleatoires 
        int a = r.nextInt(data.getSize()), b = r.nextInt(data.getSize());
        int start = a;

        // tant qu'on n'a pas deux jobs differents
        while (une_solution.getVecteur(a) == une_solution.getVecteur(b)) {
            b = r.nextInt(data.getSize());		// on regenere un entier
        }
        if (start > b) {
            start = b;
        }
        voisin.Copie(une_solution, data);
        //voisin.Copie_light(une_solution, data, start);
        //on echange les deux valeures
        echanger(voisin, a, b);

        voisin.evaluate(data, start);

    }

    public static void generer_MultiVoisin( Solution voisin, Solution une_solution,Data data, MersenneTwisterFast r, Integer minGen, Integer maxGen) {
        int nbGen = minGen + r.nextInt(maxGen+1 - minGen);
        int start = data.getSize();
        voisin.Copie(une_solution, data);
        for (int i = 0; i < nbGen; ++i) {
            // on genere deux entiers aleatoires 
            int a = r.nextInt(data.getSize()), b = r.nextInt(data.getSize());

            // tant qu'on n'a pas deux jobs differents
            while (une_solution.getVecteur(a) == une_solution.getVecteur(b)) {
                b = r.nextInt(data.getSize());		// on regenere un entier
            }
            if (start > b) {
                start = b;
            }
            if (start > a) {
                start = a;
            }

            //voisin.Copie_light(une_solution, data, start);
            //on echange les deux valeurs
            echanger(voisin, a, b);

        }
        voisin.evaluate(data, start);

    }

}
