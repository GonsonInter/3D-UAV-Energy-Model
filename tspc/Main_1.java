package tspc;

import java.io.*;
import java.lang.management.*;
import java.util.StringTokenizer;
import org.apache.commons.io.FileUtils;
import org.javatuples.Quartet;
import org.jopendocument.dom.spreadsheet.*;

/**
 *
 * @author elkrari
 */
public class Main_1 {

    public static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? bean.getCurrentThreadCpuTime() : 0L;
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, IOException {
        long start = System.currentTimeMillis();
        /*
         Paramètres et variables de l'algorithme BLS
         Ils seront passés dans la fonction lors de son appel
         Ceci permettera une gestion plus aisée au moment des tests:
         "nbrD" est le nombre de descentes, qui sera par la suite multiplé par N, tout comme "Lzero"
         "bks" est la meileure solution connue pour l'instance sur laquelle les tests sont en cours
         */
        int nbrD, t,bks,line;
        double Lzero = 0.1, Pzero = 0.75;
        String instance, inst = "azerty";

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
                instance = args[0];//instance = st.nextToken();
                bks = Integer.parseInt(args[1]);//bks = Integer.parseInt(st.nextToken());
                line = Integer.parseInt(args[2]);
                File file = new File("ALL_tsp/" + instance + ".tsp");
                File desc;
                Instance tsp = new Instance(file);
                if(tsp.dim<200)
                    nbrD=50;
                else
                    nbrD=25;
                t = (int)((nbrD*tsp.dim-1)/8+1);
//                t = tsp.dim;
                

                int c = 0;
                float time = 0, sum = 0;

                int[] pv;
                Quartet<int[], Integer, Long, Integer> sol;

                if(line==0){
                    System.out.println("----------- instance: "+instance+" bks: "+bks+" T: "+t+" -----------");
                System.out.println("Dimension TSP : " + tsp.dim);
                    File source = new File("ODS/BLS.ods");
                    //File temp = new File("ODS/BLS_temp.ods");
                    //File desc = new File("ODS/BLS_"+new SimpleDateFormat("dd-MM-yyyy_hh:mm:ss").format(new Date())+".ods");
                    desc = new File("ODS/test1008/" + instance + ".ods");
                    FileUtils.copyFile(source, desc);
                    final SpreadSheet spreadSheet = SpreadSheet.createFromFile(desc);
                    final Sheet STSP = spreadSheet.getSheet(0);

                    try {
                        FileUtils.copyFile(source, desc);
                    } catch (IOException e) {
                        System.out.println(""+e);
                    }
                }
                else{
                    desc = new File("ODS/test1008/" + instance + ".ods");
                    
                }
                try {
                    final SpreadSheet spreadSheet = SpreadSheet.createFromFile(desc);
                    final Sheet STSP = spreadSheet.getSheet(0);
                    
                    if(line==0){
                        /*
                     Saisie des paramètres dans le fichier
                     */
                    STSP.getCellAt("B1").setValue(instance);
                    STSP.getCellAt("B2").setValue(instance.replaceAll("[^0-9]", "").toString());
                    STSP.getCellAt("B4").setValue(bks);
                    STSP.getCellAt("B9").setValue(t);
                    STSP.getCellAt("B10").setValue(Pzero);
                    STSP.getCellAt("B12").setValue(Lzero + "n");
                    STSP.getCellAt("B13").setValue(nbrD + "n");
                    }
                    /*
                     Démarrage des exécutions
                     */
//                    for (int z = 0; z < 20; z++) {
                        pv = (tsp.gloutPPV(((int) (Math.random() * tsp.dim) + 1)));
                        System.out.println("Execution " + (line + 1));
                        System.out.print("\t");
                        sol = tsp.BLS(pv, bks, nbrD, Lzero, Pzero, t);
//                        sum = sum + sol.getValue1();
//                        time = time + sol.getValue2();
//                        if (sol.getValue1() == bks) {
//                            c++;
//                        }
                        STSP.getCellAt("E" + (line + 2)).setValue(sol.getValue1());
                        STSP.getCellAt("G" + (line + 2)).setValue(sol.getValue2() / 1000000);
                        //STSP.getCellAt("E"+(z+3)).setValue(sol.getValue3());
//                    }
                    spreadSheet.saveAs(desc);
                } catch (IOException e) {
                    System.out.println(""+e);
                }
//                System.out.println("Nombre de fois trouvé: " + c);
//                System.out.println("\t" + (sum / 20) + "\t" + (time / 20000000));
//                System.out.println("Temps système: " + (System.currentTimeMillis() - start));
//                System.out.println("Temps CPU: " + (getCpuTime() / 1000000));
//                
//            } while (!inst.contains("EOF"));
            
//            in.close();
//        } catch (IOException e) {
//            System.err.println("Error: " + e.getMessage());
//        } catch (NumberFormatException e) {
//            System.err.println("Error: " + e.getMessage());
//        }

    }
}
