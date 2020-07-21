
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author damien.lamy
 */
public class Main {
    public static final Logger log = Logger.getLogger(Main.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        try {
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory : " + workingDir);
            String[] pNames = new String[200];
            OUT_RES out_res = new OUT_RES();
            InputStream ips = Data.class.getResourceAsStream("/Instances/instances_name.txt");
            InputStreamReader ipsr = new InputStreamReader(ips);
            BufferedReader br = new BufferedReader(ipsr);
            int cpt = 0;
            while ((pNames[cpt++] = br.readLine()) != null) ;

            String cas1 = "DOE";
            String cas2 = "ELS_MS_DM";
            String cas3 = "GRASP_ELS";
            String cas4 = "cplex";
            String cas5 = "LS";
            String cas6 = "memeticAlgo";
            int maxreplicDOE = 5;
            Boolean withDM = true;


            boolean withDM25 = false;
            int nbDescentes = 5;
            int nbVoisins = 19;

            switch (cas6) { // Plan d'exp

                case "GRASP_ELS":
                    // final InputStream is = Data.class.getResourceAsStream("/Instances/la01.txt");

                    System.out.println("_______________instances LS crit SANS patterns " +
                            "_______________________________ ");// + " totpop.size : "+totPop.listSol.size());
                    // choix des instances testées
                    for (int inst = 0; inst <= 0; ++inst) {

                        Data data = new Data(pNames[inst], ".txt", log);// la06_0_0,5
                        Solution returnedSol = new Solution(data);

                        out_res.name_Instance[inst] = pNames[inst];
                        out_res.lower_bound[inst] = data.bks;
                        data.setTimeMax(200);
                        System.out.println("_______________Instance : " + pNames[inst] + " ");// + " totpop.size :
                        // "+totPop.listSol.size());
                        String toOut = "";
                        for (int replic = 0; replic < 10; ++replic) {
                            returnedSol = Metaheuristic.GRASP_ELS(data, log, 500, 250, 23, 300, 100, replic, false);

                            out_res.stock_Res(data, returnedSol, inst, replic);
                            toOut += ";" + returnedSol.getMakespan() + ";" + (TimeUnit.SECONDS.convert(returnedSol.getTimeToBest() - returnedSol.getStartTime(), TimeUnit.NANOSECONDS));
                        }

                        returnedSol.toString(toOut, inst);
                        out_res.out_Results_CSV(data, inst);

                    }
                    break;
                case "cplex":

                    for (int inst = 39; inst <= 77; ++inst) {//svw : 58-77
                        Data data = new Data(pNames[inst], ".txt", log);// la06_0_0,5
                        withDM = true;
                        Metaheuristic.cplex_t(data, log, 50000, 300, nbDescentes, nbVoisins, withDM, 2); //2.6
                        ProgLin pl = new ProgLin(data);
                        //pl.build_paramFile(data, 1.65);
                        //pl.writeLP_JSP(data);
                        //pl.execLP(data);

                    }
                    break;
                case "memeticAlgo":
                    // final InputStream is = Data.class.getResourceAsStream("/Instances/la01.txt");

                    System.out.println("_______________instances JSP Memetic Algo _______________________________ ");
                    // + " totpop.size : "+totPop.listSol.size());
                    // choix des instances testées

                    long[] solOptiConnue = new long[]{666, 655, 597, 590, 593, 926, 890, 863, 951, 958, 1222, 1039,
                            1150, 1292, 1207, 945, 784, 848, 842, 902, 1046, 927, 1032, 935, 977, 1218, 1235, 1216,
                            1152, 1355, 1784, 1850, 1719, 1721, 1888, 1268, 1397, 1196, 1233, 1222};
                    for (int inst = 26; inst <= 26; ++inst) {

                        Data data = new Data(pNames[inst], ".txt", log);// la06_0_0,5
                        Solution returnedSol = new Solution(data);

                        out_res.name_Instance[inst] = pNames[inst];
                        out_res.lower_bound[inst] = data.bks;
                        data.setTimeMax(200);
                        System.out.println("_______________Instance : " + pNames[inst]);// + " totpop.size : "+totPop
						// .listSol.size());
                        String toOut = pNames[inst];
                        for (int replic = 0; replic <10; ++replic) {
                            System.out.println("Replication : " + replic);
                            returnedSol = EvolutionaryAlgo.MemeticAlgo(data, log, Boolean.parseBoolean(args[0]), false, 0.7, 1.0, Double.parseDouble(args[1]), 300, 100
									, 0, 0, 0, replic, 50, solOptiConnue[inst]);

                            out_res.stock_Res(data, returnedSol, inst, replic);
                            toOut += ";" + returnedSol.getMakespan() + ";" + returnedSol.getTimeToBest();
                        }

                        returnedSol.toString(toOut, inst);
                        out_res.out_Results_CSV(data, inst);


                    }
                    break;
            }

            // data.toStringToFile("tutu");
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
