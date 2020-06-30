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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author lamy
 */
public class Solution implements Comparable<Solution>{

	public int vecteur[];
	private long makespan;
	final private long startTime;
	private long timeToBest;
	private long totalTime;
	private int opNumber[];
	private int macNumber[];
	private int endDate[];
	private int startDate[];
	private int pere[];
	private int pereDisj[];
	private int succDisj[];
	private int position[];
	private Boolean isUsed;
	private int opDisj[][];
	private int nbOpCritique;
	private int opCritiques[];
	private int nbOpCritique2;
	private int minLengthPath[];
	private int hashVal;
	private int nbIterDescentes;
	private int nbPattern;
	protected int timeSeeingJob[];
	protected int lastPieceOnMac[];
	protected int Job[];
	protected Boolean isValidVector;


	private TreeMap<Long, Long> courbeDescente; 
	//private TreeMap<Long, Long> courbeDescente10sec; 


	public void setNbIterDescentes(int i) {
		nbIterDescentes = i;
	}

	public int getNbIterDescentes() {
		return nbIterDescentes;
	}

	public void setNbPattern(int i) {
		nbPattern = i;
	}

	public int getNbPattern() {
		return nbPattern;
	}

	public int getNbOpCritique() {
		return nbOpCritique;
	}

	public int[] getOpDisj(int i) {
		return opDisj[i];
	}

	public long getMakespan() {
		return makespan;
	}

	public int getVecteur(int i) {
		return vecteur[i];
	}

	public void setVecteur(int i, int val) {
		vecteur[i] = val;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getTimeToBest() {
		return timeToBest;
	}

	public void setTimeToBest(long timeToBest) {
		this.timeToBest = TimeUnit.SECONDS.convert(timeToBest - startTime, TimeUnit.NANOSECONDS);
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = TimeUnit.SECONDS.convert(totalTime - startTime, TimeUnit.NANOSECONDS);
	}

	public int[] getOpNumber() {
		return opNumber;
	}

	public int getOpNumber(int i) {
		return opNumber[i];
	}

	public int[] getEndDate() {
		return endDate;
	}

	public int getEndDate(int i) {
		return endDate[i];
	}

	public int[] getPere() {
		return pere;
	}

	public int getPere(int i) {
		return pere[i];
	}

	public int getPereDisj(int i) {
		return pereDisj[i];
	}
	public int getSuccDisj(int i) {
		return succDisj[i];
	}

	public int[] getPosition() {
		return position;
	}

	public int getPosition(int i) {
		return position[i];
	}

	public void setPosition(int op, int i) {
		position[op]=i;
	}

	public int getNbOpCritiques2() {
		return nbOpCritique2;
	}
	public int getOpCritique2(int i) {
		return opCritiques[i];
	}

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean isUsed) {
		this.isUsed = isUsed;
	}

	public long getTTB() {
		return (timeToBest - startTime) / 1000000000;
	}

	public Solution(Data data) {
		vecteur = new int[data.getSize() + 1];
		minLengthPath = new int[data.getSize() + 1];
		opDisj = new int[data.getSize() + 1][2];
		startDate = new int [data.getSize()+1];
		endDate = new int[data.getSize() + 1];
		pere = new int[data.getSize() + 1];
		pereDisj = new int[data.getSize() + 1];
		succDisj = new int[data.getSize() + 1];
		opNumber = new int[data.getSize() + 1];
		macNumber = new int[data.getSize() + 1];
		opCritiques = new int[data.getSize()+1];
		position = new int[data.getSize() + 1];
		isUsed = false;
		timeSeeingJob = new int[data.getNbjob() + 1];
		lastPieceOnMac = new int[data.getNbmac() + 1];
		Job = new int[data.getNbjob() + 1];
		startTime = System.nanoTime();
		isValidVector = false;
		courbeDescente = new TreeMap<Long, Long>();
		//courbeDescente10sec = new TreeMap<Long, Long>();
	}

	public void setCourbeDescente(Long key, Long Makespan) {
		courbeDescente.put(key, makespan);
	}
	public TreeMap<Long, Long> getCourbeDescente() {
		return courbeDescente;
	}
	/*public void setCourbeDescente10sec(Long key, Long Makespan) {
		courbeDescente10sec.put(key, makespan);
	}*/

	public void initSolution(Data data) {
		makespan = 0;
		timeToBest = System.nanoTime();
		for (int i = 0; i < data.getSize() + 1; ++i) {
			vecteur[i] = 0;
			// startDate[i] = une_solution.startDate[i];
			endDate[i] = 0;
			pere[i] = 0;
			pereDisj[i] = 0;
			opNumber[i] = 0;
			position[i] = 0;
		}
		isUsed = false;
		isValidVector = false;
	}

	public void Copie(Solution une_solution, Data data) {
		makespan = une_solution.makespan;
		timeToBest = une_solution.timeToBest;
		for (int i = 0; i < data.getSize() + 1; ++i) {
			vecteur[i] = une_solution.vecteur[i];
			startDate[i] = une_solution.startDate[i];
			endDate[i] = une_solution.endDate[i];
			pere[i] = une_solution.pere[i];
			pereDisj[i] = une_solution.pereDisj[i];
			succDisj[i] = une_solution.succDisj[i];
			opNumber[i] = une_solution.opNumber[i];
			macNumber[i] = une_solution.macNumber[i];
			position[i] = une_solution.position[i];
		}
		isUsed = une_solution.isUsed;
		isValidVector = une_solution.isValidVector;
	}



	public void readSolutionFromFile(Data data, String fileName) {
		String[] mots = null;
		int i = 0;
		int startDate[] = new int[data.getSize()];
		try {
			InputStream ips = new FileInputStream(new File(fileName));

			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			mots = br.readLine().trim().split("\\s+");
			for (i = 0; i < data.getNbjob(); ++i) {
				this.timeSeeingJob[i] = 0;
			}
			mots = br.readLine().trim().split("\\s+");
			makespan = Integer.parseInt(mots[3]);
			for (i = 0; i < data.getSize(); ++i) {
				startDate[i] = 0;
				endDate[i] = data.getDurationForOp(i);
			}

			while ((mots = br.readLine().trim().split("\\s+")) != null) {
				// POUR LECTURE DEPUIS FICHIER .lp CUSTOM
				// int op =Integer.parseInt(mots[0])-1;
				// startDate[op]=Integer.parseInt(mots[1]);
				// endDate[op]= startDate[op]+data.durationForOp[op];

				// POUR LECTURE DEPUIS FICHIER .txt DE RESULTAT
				int job = Integer.parseInt(mots[0]);
				int op = data.getOpNumber(job, this.timeSeeingJob[job]);

				++this.timeSeeingJob[job];

				startDate[op] = Integer.parseInt(mots[2]);
				endDate[op] = Integer.parseInt(mots[2]) - data.getDurationForOp(op);
				// endDate[op]= startDate[op]+data.durationForOp[op];
			}
			br.close();
		} catch (Exception e) {
		}

		int date = 0;
		int nextDate = 9999;
		int insert = 0;
		while (insert < data.getSize()) {
			i = 0;
			nextDate = 9999;
			while (i < data.getSize()) {
				if (startDate[i] == date) {
					vecteur[insert] = data.getJobForOp(i);
					endDate[i] = startDate[i] - data.getDurationForOp(i);
					position[i] = insert;
					insert++;

				} else {
					if (startDate[i] > date && startDate[i] < nextDate) {
						nextDate = startDate[i];
					}
				}
				++i;
			}
			date = nextDate;
		}
	}

	// génération d'un vecteur de Bierwirth aléatoire
	public void generer_rand_bierwirth(Data data, MersenneTwisterFast r) {
		initSolution(data);
		int nbjob = data.getNbjob();

		// initialisation des tableaux
		for (int i = 0; i < nbjob; ++i) {
			this.timeSeeingJob[i] = data.getNbmac();
			this.Job[i] = i;
		}

		for (int i = 0; i < data.getSize(); ++i) {
			int j = r.nextInt(nbjob); // generation d'un entier aléatoire
			vecteur[i] = this.Job[j]; // stockage du job correspondant
			this.timeSeeingJob[j]--; // on decremente le nombre de machines pour le job j
			if (this.timeSeeingJob[j] == 0) {
				this.timeSeeingJob[j] = this.timeSeeingJob[nbjob - 1]; // on recupere le nombre de machines d'un job non terminé
				this.Job[j] = this.Job[--nbjob]; // N[j] reçoit un job non totalement traité et on décremente le nombre de
				// jobs restant à traiter
			}
		}
		isUsed = true;
		checkVecteur(data);
		evaluate(data, 0);
	}

	public void checkVecteur(Data data) {

		// initialisation des tableaux
		for (int i = 0; i < data.getNbjob(); ++i) {
			this.Job[i] = 0;
		}

		for (int i = 0; i < data.getSize(); ++i) {
			this.Job[vecteur[i]]++;
		}
		for (int i = 0; i < data.getNbjob(); ++i) {
			if (this.Job[i] < data.getNbmac() || this.Job[i] > data.getNbmac()) {
				isValidVector = false;
				//return false;
				break;
			}
		}
		isValidVector = true;
		//return true;
	}

	// fonction d'evaluation
	public void evaluate(Data data, int start) {
		// variables locales
		// lastPieceOnMac[i] : numero de la derniere piece passée sur la machine i
		// timeSeeingPiece[i] : nombre de fois où on a trouvé la piece i
		// i,j : variables de boucle
		// nbmac, nbjob, n : nombre de machines, de jobs, et de sommets
		// piece, machine : numero de la piece et de la machine en cours
		// pos_piece : position de la piece
		// piece_prec : position de la piece precedente
		// pos_prec_hor, pos_prec_disj : position horizontal ou disjonctive
		// prec_disj:
		// datePH : date de passage horizontal
		// datePD : date de passage disjonctif
		// d1, d2 : dates de passage à comparer
		// d : date de passage retenue
		// pere : pere du sommet en cours
		// temps : durée finale
		int operation = 0;
		int op_disj = 0, machine = 0;
		int job_position = 0, op_conj = 0;
		int dateConj = 0, dateDisj = 0, date = 0, pere = 0, temps = 0;
		int job = 0, indiceJob = 0;
		try {
			// initialisation des tableaux necessaires
			for (machine = 0; machine < data.getNbmac(); ++machine) {
				this.lastPieceOnMac[machine] = -1;
			}

			for (job = 0; job < data.getNbjob(); ++job) {
				this.timeSeeingJob[job] = 0;
			}
			hashVal = 0;
			/*
			 * for (indiceJob = 0; indiceJob < start; ++indiceJob) { job =
			 * vecteur[indiceJob]; operation = data.getOpNumber(job,
			 * this.timeSeeingJob[job]);
			 * this.lastPieceOnMac[data.getMachineForOp(operation)] = operation;
			 * ++this.timeSeeingJob[job];
			 * hashVal=(hashVal+endDate[operation]*endDate[operation])%data.getMaxHash(); }
			 */
			//une_solution.hash_L=0;    
			if (start > 0 ) {     // on red�pile les informations
				for (int piece=0; piece< start; ++piece) { 
					operation = data.getOpNumber(vecteur[piece],timeSeeingJob[vecteur[piece]]);

					lastPieceOnMac[data.getMachineForOp(operation)] = operation;
					++timeSeeingJob[vecteur[piece]];
				}
				//une_solution.hash_L = une_solution.hashValue[start-1];
			}
			for (indiceJob = start; indiceJob < data.getSize(); ++indiceJob) {
				// initialisations des durées
				job = vecteur[indiceJob]; // numero de la piece dans bierwirth
				job_position = this.timeSeeingJob[job]; // position de la piece dans la gamme
				operation = data.getOpNumber(job, job_position);
				machine = data.getMachineForOp(operation); // numero de machine ou se trouve la piece

				if (job_position != 0) { // si la piece n'est pas en debut de gamme
					op_conj = data.getPrecConj(operation); // on recupere la piece precedente dans la gamme
					dateConj = endDate[op_conj]; // on recupere la date de fin au plus tot de la piece precedente dans la meme
					// gamme
				} else {
					dateConj = 0;
					op_conj = -1;
				}

				if (this.lastPieceOnMac[machine] != -1) { // si une autre piece est passe avant sur la meme machine
					op_disj = this.lastPieceOnMac[machine]; // on recupere le numero de la piece passe avant sur la machine
					dateDisj = endDate[op_disj]; // on recupere la date de fin au plus tot de la piece precedente sur la meme
					// machine
				} else {
					dateDisj = 0;
					op_disj = -1;
				}

				date = dateDisj; // on retient d2
				pere = op_disj; // on retient le pere
				if (dateConj > date) { // si d1 est alors superieure à d
					date = dateConj; // on retient d1
					pere = op_conj; // on retient le pere
				}

				// on sauvegarde les données dans la solution
				startDate[operation] = date;
				endDate[operation] = date + data.getDurationForOp(operation);
				opNumber[indiceJob] = operation;
				macNumber[indiceJob] = machine;
				hashVal = (hashVal + endDate[operation] * endDate[operation]) % ((int) data.getMaxHash());
				this.pere[operation] = pere;
				this.pereDisj[operation] = op_disj;
				this.succDisj[operation] = data.INFINITE_C;
				if (this.lastPieceOnMac[machine]!=-1) {
					this.succDisj[this.lastPieceOnMac[machine]]=operation;
				}
				position[operation] = indiceJob;
				this.lastPieceOnMac[machine] = operation; // la piece en cours est la derniere à etre passée sur la machine
				++this.timeSeeingJob[job];

			}

			// phase de calcul du COUT selon la durée des arcs terminaux
			temps = 0;
			makespan = endDate[data.getOpNumber(0, data.getNbmac() - 1)];
			for (job = 1; job < data.getNbjob(); ++job) {
				date = endDate[data.getOpNumber(job, data.getNbmac() - 1)];
				if (date > makespan) {
					makespan = date;
					this.pere[data.getSize()] = data.getOpNumber(job, data.getNbmac() - 1);
				}
			}
			setTimeToBest(System.nanoTime());
			// writeGANTT_SVG(data);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void writeGANTT_SVG(Data data) {
		int i, job, mac;
		int nbmac = data.getNbmac(), nbjob = data.getNbjob();
		int margin = 60;
		int marginBetweenRect = 2;
		int fleche = 20;
		int rectWidth = 50;
		int xStart = 0, xLength = 0, yStart = 0, yLength = 0;
		Random rand = new Random();

		try {
			File ff = new File(data.getResultsDir() + data.getpName() + "_gantt.svg"); // définir l'arborescence

			ff.createNewFile();
			FileWriter ffw = new FileWriter(ff);
			// ffw.write(data.pName + "\n"); // écrire une ligne dans le fichier
			// resultat.txt

			/*----------------------------------------------------------------------
			Ratio pour taille des rectangles du diagramme
			----------------------------------------------------------------------*/
			int ratio = 2;
			/*
			 * if (cout <= 30) { ratio = 20; } else if (cout < 100) { ratio = 10; } else if
			 * (cout < 250) { ratio = 5; } else if (cout < 500) { ratio = 2; }
			 */
			int maxDiagLength = (int) ((ratio) * makespan + 2 * margin + fleche + data.getNbmac());
			int maxDiagHeight = (rectWidth + marginBetweenRect) * data.getNbmac() + margin + fleche + 10;

			ffw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
			ffw.write(" <svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" width=\"" + maxDiagLength + "\" height=\"" + maxDiagHeight + "\">\n");
			ffw.write("<title> Diagramme de Gantt du Probleme: " + data.getpName() + "</title>\n");
			ffw.write("<desc> Cette figure représente le planning des opérations du problème traité.</desc>\n");

			/*----------------------------------------------------------------------
			    Création de la palette de couleurs
			----------------------------------------------------------------------*/
			int[][] colors = new int[nbjob][3];
			rand.setSeed(1);
			for (i = 0; i < nbjob; ++i) {
				colors[i][0] = rand.nextInt(256);
				colors[i][1] = rand.nextInt(256);
				colors[i][2] = rand.nextInt(256);
			}

			for (i = 0; i < data.getSize(); i++) {

				job = data.getJobForOp(i);
				mac = data.getMachineForOp(i);

				xStart = margin + (ratio) * (endDate[i] - data.getDurationForOp(i)) + marginBetweenRect; // +dd1*macVisited[mac]
				xLength = (ratio) * (data.getDurationForOp(i)) - marginBetweenRect; // +dd1
				yStart = margin + mac * rectWidth + marginBetweenRect;
				yLength = rectWidth - marginBetweenRect;

				ffw.write("<rect style=\"fill:rgb(" + colors[job][0] + "," + colors[job][1] + "," + colors[job][2] + ");fill-opacity:0.5;\" width=\"" + xLength + "\" height=\"" + yLength + "\" x=\"" + xStart + "\" y=\"" + yStart + "\"/>\n");

				ffw.write("<text x=\"" + ((xLength + 2 * xStart) / 2) + "\" y=\"" + (yStart + 15) + "\" font-family=\"sans-serif\" font-size=\"10px\" text-anchor=\"middle\">J" + job + "-Op" + i + "</text>");
				ffw.write("<text x=\"" + ((xLength + 2 * xStart) / 2) + "\" y=\"" + (yStart + 25) + "\" font-family=\"sans-serif\" font-size=\"10px\" text-anchor=\"middle\">" + (endDate[i] - data.getDurationForOp(i)) + "</text>");
				ffw.write("<text x=\"" + ((xLength + 2 * xStart) / 2) + "\" y=\"" + (yStart + 35) + "\" font-family=\"sans-serif\" font-size=\"10px\" text-anchor=\"middle\">" + endDate[i] + "</text>");

			}

			// buffer.setColor(Color.DARK_GRAY);
			// vertical
			ffw.write("<line x1=\"" + margin + "\" y1=\"" + (margin - fleche) + "\" x2=\"" + margin + "\" y2=\"" + (margin + nbmac * rectWidth + marginBetweenRect) + "\" stroke=\"dimgrey\" />");
			ffw.write("<line x1=\"" + margin + "\" y1=\"" + (margin - fleche) + "\" x2=\"" + (margin + 10) + "\" y2=\"" + (margin - fleche + 10) + "\" stroke=\"dimgrey\" />");

			for (i = 1; i <= nbmac; ++i) {
				ffw.write("<text x=\"" + (margin - 25) + "\" y=\"" + (margin - fleche + i * rectWidth) + "\" font-family=\"sans-serif\" font-size=\"10px\" text-anchor=\"middle\">M." + i + "</text>");

			}

			// horizontal
			ffw.write("<line x1=\"" + margin + "\" y1=\"" + (margin + nbmac * rectWidth + marginBetweenRect) + "\" x2=\"" + (margin + ratio * makespan + fleche) + "\" y2=\"" + (margin + nbmac * rectWidth + marginBetweenRect) + "\" stroke=\"dimgrey\" />");
			ffw.write("<line x1=\"" + (margin + ratio * makespan + fleche - 10) + "\" y1=\"" + (margin + nbmac * rectWidth + marginBetweenRect - 10) + "\" x2=\"" + (margin + ratio * makespan + fleche) + "\" y2=\"" + (margin + nbmac * rectWidth + marginBetweenRect) + "\" stroke=\"dimgrey\" />");
			ffw.write("<line x1=\"" + (margin + ratio * makespan) + "\" y1=\"" + (margin + nbmac * rectWidth + marginBetweenRect + 2) + "\" x2=\"" + (margin + ratio * makespan) + "\" y2=\"" + (margin + nbmac * rectWidth + marginBetweenRect + 10) + "\" stroke=\"dimgrey\" />");

			ffw.write("<text x=\"" + (margin + 10) + "\" y=\"" + (margin - fleche - 5) + "\" font-family=\"sans-serif\" font-size=\"20px\" text-anchor=\"middle\" fill=\"dimgrey\">Machines</text>");
			ffw.write("<text x=\"" + (margin + ratio * makespan + 2 * fleche) + "\" y=\"" + (margin + nbmac * rectWidth + marginBetweenRect) + "\" font-family=\"sans-serif\" font-size=\"20px\" text-anchor=\"middle\" fill=\"dimgrey\">Time</text>");

			ffw.write("<text x=\"" + (maxDiagLength / 2) + "\" y=\"" + (margin + nbmac * rectWidth + marginBetweenRect + 30) + "\" font-family=\"sans-serif\" font-size=\"30px\" text-anchor=\"middle\" fill=\"dimgrey\">Diagramme de Gantt du problème: " + data.getpName() + " - makespan: " + makespan + "</text>");

			// Fin du fichier
			ffw.write("</svg>\n");
			ffw.close(); // fermer le fichier à la fin des traitements
		} catch (Exception e) {
		}
	}

	public ArrayList<Integer> solutionToArraylist(Data data) {
		ArrayList<Integer> pat = new ArrayList<Integer>();
		for (int i = 0; i < data.getSize(); i++) {
			pat.add(opNumber[i]);
		}
		// System.out.println(pat.size());
		return pat;
	}

	// obtenir la longueur des plus long chemin de chaque op�ration vers le puits
	public void compute_lPath_Mining(Data data, Mining mining) {

		try {
			int piece, machine, pos_piece;
			int indice;
			int dateEndPD = 0, date = 0;
			int sommetCourant;
			/*
			 * for (int i = 0; i < data.getNbjob(); ++i) { for (int j = 0; j <
			 * data.getNbmac(); ++j) { minLengthPath[i][j] = 0; } }
			 */
			for (int i = 0; i < data.getSize(); ++i) {
				minLengthPath[i] = 0;

			}
			for (machine = 0; machine < data.getNbmac(); ++machine) {
				lastPieceOnMac[machine] = -1;
			}
			for (piece = 0; piece < data.getNbjob(); ++piece) {
				timeSeeingJob[piece] = data.getNbmac() - 1;
			}

			for (indice = data.getSize() - 1; indice >= 0; --indice) {
				// initialisations durées+pere
				dateEndPD = 0;
				date = 0;
				// informations sur le sommet
				piece = vecteur[indice]; // numero de la piece dans bierwirth
				pos_piece = timeSeeingJob[piece]; // position de la piece dans la gamme
				sommetCourant = data.getOpNumber(piece, pos_piece);
				machine = data.getMachineForOp(sommetCourant); // numero de machine/BU ou se trouve la piece
				if (pos_piece < data.getNbmac() - 1) { // recuperation longueur du chemin à partir du predecesseur conjonctif
					date = minLengthPath[sommetCourant + 1];
				}

				if (lastPieceOnMac[machine] != -1) { // recuperation longueur chemin predecesseur disjonctif
					dateEndPD = minLengthPath[lastPieceOnMac[machine]];
				}

				if (date <= dateEndPD) { // si la durée disj est superieure à horiz
					date = dateEndPD; // on retient date disj
				}

				minLengthPath[sommetCourant] = date + data.getDurationForOp(sommetCourant);

				lastPieceOnMac[machine] = sommetCourant; // la piece en cours est le dernier sommet à etre passée sur la machine
				--timeSeeingJob[piece]; // on incrémente le nombre de fois où on a vu la piece
			}
			if (mining.pattern != null) {
				setOpCritique_Mining(data, mining);
			} else {
				setOpCritique(data);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// getDisjunctions(seq,data);
		// getMachinesCP(seq, data);
		// getDisjunctionsNowicki(seq, data);
	}


	// obtenir la longueur des plus long chemin de chaque op�ration vers le puits
	public void compute_lPath(Data data) {

		try {
			int piece, machine, pos_piece;
			int indice;
			int dateEndPD = 0, date = 0;
			int sommetCourant;
			/*
			 * for (int i = 0; i < data.getNbjob(); ++i) { for (int j = 0; j <
			 * data.getNbmac(); ++j) { minLengthPath[i][j] = 0; } }
			 */
			for (int i = 0; i < data.getSize(); ++i) {
				minLengthPath[i] = 0;

			}
			for (machine = 0; machine < data.getNbmac(); ++machine) {
				lastPieceOnMac[machine] = -1;
			}
			for (piece = 0; piece < data.getNbjob(); ++piece) {
				timeSeeingJob[piece] = data.getNbmac() - 1;
			}

			for (indice = data.getSize() - 1; indice >= 0; --indice) {
				// initialisations durées+pere
				dateEndPD = 0;
				date = 0;
				// informations sur le sommet
				piece = vecteur[indice]; // numero de la piece dans bierwirth
				pos_piece = timeSeeingJob[piece]; // position de la piece dans la gamme
				sommetCourant = data.getOpNumber(piece, pos_piece);
				machine = data.getMachineForOp(sommetCourant); // numero de machine/BU ou se trouve la piece
				if (pos_piece < data.getNbmac() - 1) { // recuperation longueur du chemin à partir du predecesseur conjonctif
					date = minLengthPath[sommetCourant + 1];
				}

				if (lastPieceOnMac[machine] != -1) { // recuperation longueur chemin predecesseur disjonctif
					dateEndPD = minLengthPath[lastPieceOnMac[machine]];
				}

				if (date <= dateEndPD) { // si la durée disj est superieure à horiz
					date = dateEndPD; // on retient date disj
				}

				minLengthPath[sommetCourant] = date + data.getDurationForOp(sommetCourant);

				lastPieceOnMac[machine] = sommetCourant; // la piece en cours est le dernier sommet à etre passée sur la machine
				--timeSeeingJob[piece]; // on incrémente le nombre de fois où on a vu la piece
			}

			setOpCritique(data);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// getDisjunctions(seq,data);
		// getMachinesCP(seq, data);
		// getDisjunctionsNowicki(seq, data);
	}
	public void setOpCritique(Data data) {
		nbOpCritique = 0; nbOpCritique2=0;
		//this.writeGANTT_SVG(data);
		for (int op = 0; op < data.getSize(); ++op) {
			if (pereDisj[op] != -1 && endDate[pereDisj[op]] + minLengthPath[op] == makespan) {
				opDisj[nbOpCritique][0] = pereDisj[op];
				opDisj[nbOpCritique][1] = op;
				++nbOpCritique;
			}
			if (startDate[op] + minLengthPath[op] == makespan) {
				opCritiques[nbOpCritique2++]=op;
			}
		}

	}

	// si une op est critique alors on peut l'ajouter � la liste
	public void setOpCritique_Mining(Data data, Mining mining) {
		nbOpCritique = 0;
		//writeGANTT_SVG(data);
		for (int op = 0; op < data.getSize(); ++op) {
			if (pereDisj[op] != -1 && endDate[pereDisj[op]] + minLengthPath[op] == makespan && !mining.pattern[pereDisj[op]][op]) {
				opDisj[nbOpCritique][0] = pereDisj[op];
				opDisj[nbOpCritique][1] = op;
				++nbOpCritique;
			}
		}
	}

	public int getHashVal() {
		return hashVal;
	}

	public void setHashVal(int hashVal) {
		this.hashVal = hashVal;
	}

	public void swap(int i, int j) {
		int tmp = vecteur[i];
		vecteur[i]=vecteur[j];
		vecteur[j]=tmp;
	}

	public void toString(String toOut, int i) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("myfile_"+i+".txt", true)));
			out.println(toOut);
			out.close();
		} catch (IOException e) {
			//exception handling left as an exercise for the reader
		}
	}
	public void toString2(int param1, int param2, int param3, int param4, int param5) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("myfile.txt", true)));
			out.println(this.makespan + ";"+ TimeUnit.SECONDS.convert(this.getTimeToBest()-this.getStartTime(), TimeUnit.NANOSECONDS)+";"+param1+";"+param2+";"+param3+";"+param4+";"+param5);
			out.close();
		} catch (IOException e) {
			//exception handling left as an exercise for the reader
		}
	}


	//procedure d'echange entre deux op�rations critiques -> prend en compte la structure particuliere de bierwirth
	//peu utilis� en pratique, coute du temps pour un gain non significatif
	void exchange_op(Data data, int opFather, int opSon) {
		boolean ok = false;
		int pos_F = position[opFather], pos_S = position[opSon];

		if ((data.getStageForOp(opFather) < data.getNbmac() && position[opFather + 1] < pos_S) || (data.getStageForOp(opSon) > 0 && position[opSon - 1] > pos_F)) {
			int nbOp = pos_S - pos_F + 1;
			int pos_S2 = pos_S - 1;
			int opNumberNext = pos_F + 1;
			while (pos_F < pos_S2) {
				if (endDate[opNumber[opNumberNext]] <= endDate[opFather]) { // directement opNumberNext++?
					swap(pos_F, pos_F + 1); ++pos_F;
					++opNumberNext;
					// r�ecrire dans position, numeroOp les infos en se basant sur opNumberNext : seq.position[seq.opNumber[opNumberNext]]=pos_F
					// et incr�menter pos_F apr�s
				}
				else { // if (seq.endDate[seq.opNumber[pos_F + 1]] >= seq.endDate[opSon]) 
					int tmp = vecteur[pos_F + 1]; // le job � sauvegarde
					int start = pos_F + 1;
					while (start < pos_S) { //permutation circulaire
						vecteur[start] = vecteur[start + 1]; ++start;
					}
					vecteur[start] = tmp;
					--pos_S2;
					++opNumberNext;
				}
			}
			swap(pos_F, pos_S2 + 1); // pas d'operations � probl�mes entre les deux
		}
		else {
			swap(pos_F, pos_S); // pas d'operations � probl�mes entre les deux
		}
		//evaluate(seq, data, pos_F); // quand il y a des swap, swapper aussi les infos sur position/numerooperation etc, 
		//et evaluer � partir de la nouvelle position de l'operation opFather - CA VA ETRE COMPLIQUE ...
	}

	@Override
	public int compareTo(Solution solution) {
		return (this.makespan < solution.makespan) ? -1 : ((this.makespan == solution.makespan) ? 0 : 1);
	}
}
