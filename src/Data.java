/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

//lecture instance
public class Data {

	public int replic;
	public boolean withDM;
	public static int INFINITE_C=99999;
    private String pathName;
    private String resultsDir;
    public int nbPertuFaible;
    public int nbpertuForte;
    private int maxHash;
    
    public String getPathName() {
        return pathName;
    }

    public String getResultsDir() {
        return resultsDir;
    }
    public int getMaxHash() {
        return maxHash;
    }
    private int error;
    private String strError;
    private int itermax;
    private String pName;
    private int size;
    private int nbjob;
    private int nbmac;
    private long biCriteria;
    private int opNumber[][];
    private int machineForOp[];
    private int durationForOp[];
    private int precConj[];
    protected int puissMax;
    protected int succConj[];
    private int jobForOp[];
    private int stageForOp[];
	public double pc;
	public double pm;
	public double pls;
     public int timeseeingpiece[];
    public int timeseeingpiece2[];
    public int macNumber[];
	public int bks;
	public int MAX_INSTANCES;
	public int MAX_REPLICATIONS;
	private double nbIterLS;
	public int timeMax;
	public int bigH;
	public String toOut;
	
	public Data() {
		toOut = "";
        this.error = 0;
        this.size = 0;
        this.itermax = 0;
        this.jobForOp = null;
        this.precConj = null;
        this.biCriteria = 0;
        this.nbjob = 0;
        this.nbmac = 0;
        this.opNumber = null;
        this.stageForOp = null;
        this.strError = null;
        this.pathName = null;
    }
   
    public Data(String pName, String extension, Logger log) {
        //lecture du fichier texte
        String[] mots = null;
        int k = 0, job = 0, mac = 0;

        String workingDir = System.getProperty("user.dir");
        System.out.println("Current problem : " + pName);

        MAX_INSTANCES=100;
        MAX_REPLICATIONS=10;
        try {
        	InputStream ips = Data.class.getResourceAsStream("/Instances/"+pName+extension);
            //InputStream ips = new FileInputStream(new File(file));
            //InputStream ips = new FileInputStream(workingDir+"\\src\\Instances\\" + pName);
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            String ligne = br.readLine();

            maxHash = 100000;
            mots = ligne.trim().split("\\s+");
            nbjob = Integer.parseInt(mots[0]);      //on stock le nombre de jobs
            nbmac = Integer.parseInt(mots[1]);      //on stock le nombre de machines
            bks = Integer.parseInt(mots[2]);      //on stock le nombre de machines
            size = nbjob * nbmac;
            timeMax = size;
            itermax = (int) ((size) / 2) + 5;
            nbIterLS = size*Math.log(2);
            opNumber = new int[nbjob + 1][nbmac + 1];
            machineForOp = new int[size + 1];
            stageForOp = new int[size + 1];
            durationForOp = new int[size + 1];
            precConj = new int[size + 1];
            succConj = new int[size + 1];
            jobForOp = new int[size + 1];
            timeseeingpiece = new int[size + 1];
            timeseeingpiece2 = new int[size + 1];
            macNumber = new int[nbmac+1];
            long worstSol = 0;
            for (job = 0; job < nbjob; job++) {
            	ligne = br.readLine();
                mots = ligne.trim().split("\\s+");
                for (mac = 0; mac < nbmac; ++mac) {
                    stageForOp[k] = mac;
                    opNumber[job][mac] = k;
                    machineForOp[k] = Integer.parseInt(mots[2 * mac]);
                    durationForOp[k] = Integer.parseInt(mots[2 * mac + 1]);
                    worstSol += durationForOp[k];
                    jobForOp[k] = job;

                    if (mac > 0) {
                        precConj[k] = k - 1;
                    } else {
                        precConj[k] = -1;
                    }
                    
                    if (mac == nbmac-1) {
                        succConj[k] = INFINITE_C;
                    } else {
                    	succConj[k] = k+1;
                    }

                    ++k;
                }
            }

            int puissance = 1;
            // calcul pour utilisation dans le nombre de TL brisés
            while (worstSol > puissance) {
                puissance *= 10;
            }
            bigH = puissance;
            pathName = jarPathName();
            resultsDir = createDir("results");
            
            long moyenneTempsOp = worstSol/size;
            nbPertuFaible=2;
            nbpertuForte=3;
            //if (moyenneTempsOp/5 > nbPertuFaible) nbPertuFaible=((int)moyenneTempsOp)/5;
            //if (moyenneTempsOp > nbpertuForte) nbpertuForte=((int)moyenneTempsOp);
            
            
            // on s'autorise un écart d'un zero pour bien différencier la partie gauche et droite du multicritère 
            biCriteria = puissance * 10;
            this.pName = pName;
            br.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, null, e);
            error = -1;
            strError = "Your file does not respect the format required";
        }
    }

    public String toString() {
        return "test";
    }
    
    public double getNbIterLS() {
    	return nbIterLS;
    }
    public void setNbIterLS(double i) {
    	 nbIterLS=i;
    }
    
    public void setTimeMax(int t) {
    	timeMax=t;
    }
    public int getTimeMax() {
    	return timeMax;
    }


    public String jarPathName() {
       String path = null;
        try {
            URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
            String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
            path = new File(jarPath).getParentFile().getPath();

        } catch (Exception ioe) {

        }

        return path;
    }
    
     public String createDir(String dirName) {
         String newDir = null;
         try {
            
            String fileSeparator = System.getProperty("file.separator");
            newDir = pathName + fileSeparator + dirName + fileSeparator;
            new File(newDir).mkdir();

        } catch (Exception ioe) {

        }

        return newDir;
    }

    public int getError() {
        return error;
    }

    public String getStrError() {
        return strError;
    }

    public int getItermax() {
        return itermax;
    }

    public String getpName() {
        return pName;
    }

    public int getSize() {
        return size;
    }

    public int getNbjob() {
        return nbjob;
    }

    public int getNbmac() {
        return nbmac;
    }

    public long getBiCriteria() {
        return biCriteria;
    }

    public int getOpNumber(int i, int j) {
        return opNumber[i][j];
    }

    public int getMachineForOp(int i) {
        return machineForOp[i];
    }

    public int getDurationForOp(int i) {
        return durationForOp[i];
    }

    public int getPrecConj(int i) {
        return precConj[i];
    }
    
    public int getSuccConj(int i) {
        return succConj[i];
    }

    public int getJobForOp(int i) {
        return jobForOp[i];
    }

    public int getStageForOp(int i) {
        return stageForOp[i];
    }
}
