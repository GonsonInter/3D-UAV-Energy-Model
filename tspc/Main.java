package tspc;

import java.io.*;
import java.lang.management.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import org.apache.commons.io.FileUtils;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.jopendocument.dom.spreadsheet.*;

/**
 *
 * @author elkrari
 */
public class Main {

    public static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? bean.getCurrentThreadCpuTime() : 0L;
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException {
//        long start = System.currentTimeMillis();
        /*
         Paramètres et variables de l'algorithme BLS
         Ils seront passés dans la fonction lors de son appel
         Ceci permettera une gestion plus aisée au moment des tests:
         "nbrD" est le nombre de descentes, qui sera par la suite multiplé par N, tout comme "Lzero"
         "bks" est la meileure solution connue pour l'instance sur laquelle les tests sont en cours
         */
       
        
       int nbrD, t,bks,line;
        double Lzero = 0.1, Pzero = 0.75;
        String instance, rep;

//        try {
            /* Liste des instances à exécuter*/
//            FileInputStream fstream = new FileInputStream("ALL_tsp/aaa.list");
//            DataInputStream in = new DataInputStream(fstream);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//           do {
//                inst = br.readLine();
//                 if(inst.equals("EOF"))
//                     break;
//                StringTokenizer st = new StringTokenizer(inst, " : ");
                rep = "run_1905";
                instance = args[0];//instance = st.nextToken();
                bks = Integer.parseInt(args[1]);//bks = Integer.parseInt(st.nextToken());
                line = Integer.parseInt(args[2]);
                File file = new File("InstancesTSPLIBFormat/" + instance + ".txt");
                File desc;
                Instance gtsp = new Instance(file);
                nbrD=50;
                t = (int)((nbrD*gtsp.dim-1)/8+1);
//                t = nbrD*tsp.dim/4;
                

                int c = 0;
                float time = 0, sum = 0;

                int[] pv;
                Quartet<int[], Integer, Long, Integer> sol;

                if(line==0){
                    System.out.println("----------- instance: "+instance+" bks: "+bks+" T: "+t+" -----------");
                System.out.println("Dimension TSP : " + gtsp.dim);
                    File source = new File("ODS/BLS.ods");
                    //File temp = new File("ODS/BLS_temp.ods");
                    //File desc = new File("ODS/BLS_"+new SimpleDateFormat("dd-MM-yyyy_hh:mm:ss").format(new Date())+".ods");
                    desc = new File("ODS/"+rep+"/" + instance + ".ods");
                    FileUtils.copyFile(source, desc);
//                    final SpreadSheet spreadSheet = SpreadSheet.createFromFile(desc);
//                    final Sheet STSP = spreadSheet.getSheet(0);

                    try {
                        FileUtils.copyFile(source, desc);
                    } catch (IOException e) {
                        System.out.println(""+e);
                    }
                }
                else{
                    desc = new File("ODS/"+rep+"/" + instance + ".ods");
                    
                }
                try {
                    final SpreadSheet spreadSheet = SpreadSheet.createFromFile(desc);
                    final Sheet STSP = spreadSheet.getSheet(0);
                    
                    if(line==0){
                        /*
                     Saisie des paramètres dans le fichier
                     */
                    STSP.getCellAt("H1").setValue(instance);
                    STSP.getCellAt("E1").setValue(gtsp.dim);
                    STSP.getCellAt("E2").setValue(gtsp.nbClus);
                    STSP.getCellAt("J1").setValue(Instance.EdgeWeightType(file));
                    STSP.getCellAt("J2").setValue(bks);
//                    STSP.getCellAt("B9").setValue(t);
//                    STSP.getCellAt("B10").setValue(Pzero);
//                    STSP.getCellAt("B12").setValue(Lzero + "n");
//                    STSP.getCellAt("B13").setValue(nbrD + "n");
                    }
                    /*
                     Démarrage des exécutions
                     */
//                    for (int z = 0; z < 20; z++) {
                        pv = (gtsp.gloutPPV(((int) (Math.random() * gtsp.dim) + 1)));
                        System.out.println("Execution " + (line + 1));
                        System.out.print("\t");
                        LinkedList<Triplet<int[], Integer, Long>> pop = gtsp.makePopulation(bks, nbrD, Lzero, Pzero, t);
                        for(int x=0;x<gtsp.nbClus;x++){
                            if(pop.get(0).getValue1() == bks)
                                break;
                            pop = gtsp.uniformX(pop, bks, nbrD, Lzero, Pzero, t);
                        }
//                        sum = sum + sol.getValue1();
//                        time = time + sol.getValue2();
//                        if (sol.getValue1() == bks) {
//                            c++;
//                        }
                        
                        STSP.getCellAt("B" + (line + 5)).setValue(pop.get(0).getValue1());
                        STSP.getCellAt("D" + (line + 5)).setValue(pop.get(0).getValue2() / 1000000);
//                        STSP.getCellAt("E" + (line + 5)).setValue(pop.get(0).getValue3() / gtsp.dim);
                        
                        System.out.println(pop.get(0).getValue1() + "\t" + pop.get(0).getValue2() / 1000000 );
                        
//                    }
                    spreadSheet.saveAs(desc);
                } catch (IOException e) {
                    System.out.println(""+e);
                }
        

    }
}
