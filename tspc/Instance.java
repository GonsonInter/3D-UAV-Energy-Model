package tspc;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.javatuples.*;

/**
 *
 * @author elkrari
 */
public class Instance {

    int dim;
    String name;
    ArrayList<Ville> villes;
    int[][] MatDis;
    int[] pos;
//    int[] cc;
    int[] clus;
    int nbClus;
    ArrayList<ArrayList<Integer>> cls;

    public Instance(File f) {
        try {
            dim = dimTSP(f);
            villes = fromTSP(f);
            MatDis = MatDist(f);
            name = f.getName();
            clus = getClusters(f);  /*Le numéro de cluster pour chaque ville*/
            nbClus = maxArray(clus);
            cls = clsArray();       /*Liste des villes pour chaque cluster*/
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Instance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Calcule la distEucance euclidienne entre deux villes (<code>v1</code> et
     * <code>v2</code>) à partir des coordonnées de ces dernières
     *
     * @param v1 {@code Ville} de départ
     * @param v2 {@code Ville} d'arrivée
     * @return la distEucance (valeur entière - {@code int}) entre
     * (<code>v1</code> et <code>v2</code>)
     */
    public static int distEuc(Ville v1, Ville v2) {
        return (int) (Math.sqrt(Math.pow((v2.x - v1.x), 2) + Math.pow((v2.y - v1.y), 2)) + 0.5);
    }

    public static int distGeo(Ville v1, Ville v2) {
        double RRR = 6378.388;

        double q1 = Math.cos(degreeToRadian(v1.y) - degreeToRadian(v2.y));
        double q2 = Math.cos(degreeToRadian(v1.x) - degreeToRadian(v2.x));
        double q3 = Math.cos(degreeToRadian(v1.x) + degreeToRadian(v2.x));
        return (int) (RRR * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);
    }

    private static double degreeToRadian(double coordinate) {
        int deg = (int) coordinate;
        double min = coordinate - deg;
        double rad = (Math.PI * (deg + (5 * min) / 3)) / 180;
        return rad;
    }

    /**
     * Extrait du fichier d'instance du PVC la dimension de cette dernière
     *
     * @param TSP {@code Fichier} d'instance
     * @return La dimension du PVC
     * @throws FileNotFoundException if <code>Instance</code> is <code>null</code>
     */
    public static int dimTSP(File TSP) throws FileNotFoundException {

        String lignedim = "azerty";
        try {
            FileInputStream fstream = new FileInputStream(TSP);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            do {
                lignedim = br.readLine();
            } while (!lignedim.contains("DIMENSION"));
            StringTokenizer st = new StringTokenizer(lignedim, ":");
            st.nextToken();
            lignedim = st.nextToken().substring(1);

            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return Integer.parseInt(lignedim);
    }

    public static String EdgeWeightType(File TSP) {
        String line = "";
        try {
            FileInputStream fstream = new FileInputStream(TSP);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            do {
                line = br.readLine();
            } while (!line.contains("EDGE_WEIGHT_TYPE"));
            StringTokenizer st = new StringTokenizer(line, ":");
            st.nextToken();
            line = st.nextToken().substring(1);

            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return line;
    }

    /**
     *
     * @param TSP
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static int[] getClusters(File TSP) throws FileNotFoundException {
        int[] liste = new int[dimTSP(TSP)];
        int i, e;
        String l;
        try {
            FileInputStream fstream = new FileInputStream(TSP);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while (!br.readLine().contains("GTSP_SET_SECTION:")) {
            }
            do {
                l = br.readLine();
                if (l.contains("EOF")) {
                    break;
                }
                StringTokenizer st = new StringTokenizer(l, " ");
                i = Integer.parseInt(st.nextToken());
                while (st.hasMoreTokens()) {
                    e = Integer.parseInt(st.nextToken());
                    if (e == -1) {
                        break;
                    }
                    liste[e - 1] = i;

                }
            } while (!l.contains("EOF"));

            in.close();
        } catch (IOException | NumberFormatException ex) {
            System.err.println("Error: " + ex.getMessage());
        }
        return liste;

    }

    /**
     * Construit la matrice des distEucances à partir du fichier d'instance du
     * PVC entré en paramètre
     *
     * @param TSP {@code Fichier} d'instance
     * @return La matrice des distEucances
     * @throws FileNotFoundException if <code>Instance</code> is <code>null</code>
     */
    public static int[][] MatDist(File TSP) throws FileNotFoundException {
        ArrayList<Ville> liste = fromTSP(TSP);
        int dim = dimTSP(TSP);
        String type = EdgeWeightType(TSP);
        int[] cl = getClusters(TSP);
        int Mat[][] = new int[dim][dim];
//        System.out.println("Instance Coord Type:" +type);
        if ("EUC_2D".equals(type)) {
            for (int i = 0; i < dim - 1; i++) {
                for (int j = i + 1; j < dim; j++) {
                    if (cl[i] == cl[j]) {
                        Mat[i][j] = Mat[j][i] = -1;
                    } else {
                        Mat[i][j] = Mat[j][i] = distEuc(liste.get(i), liste.get(j));
                    }
                }
            }
        } else if (type.equals("GEO")) {
            for (int i = 0; i < dim - 1; i++) {
                for (int j = i + 1; j < dim; j++) {
                    if (cl[i] == cl[j]) {
                        Mat[i][j] = Mat[j][i] = -1;
                    } else {
                        Mat[i][j] = Mat[j][i] = distGeo(liste.get(i), liste.get(j));
                    }
                }
            }
        }
        return Mat;
    }

    /**
     * Construit la liste des villes du PVC entré en paramètre
     *
     * @param TSP {@code Fichier} d'instance
     * @return La liste des villes de l'instance
     * @throws FileNotFoundException if <code>Instance</code> is <code>null</code>
     */
    public static ArrayList<Ville> fromTSP(File TSP) throws FileNotFoundException {
        ArrayList<Ville> liste = new ArrayList<>();
        try {
            FileInputStream fstream = new FileInputStream(TSP);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while (!br.readLine().contains("NODE_COORD_SECTION")) {
            }
            for (int i = 0; i < dimTSP(TSP); i++) {
                liste.add(LigneVille(br));
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return liste;

    }

    /**
     * Construit une instance de {@code Ville} à partir d'une ligne du fichier
     * d'instance se trouvant au Buffer
     *
     * @param br {@code BufferedReader} - contenant les attribu de la
     * {@code Ville}
     * @return Une instance de {@code Ville}
     * @throws IOException if <code>br</code> is <code>null</code>
     */
    public static Ville LigneVille(BufferedReader br) throws IOException {
        StringTokenizer ligne = new StringTokenizer(br.readLine(), " ");
        Ville V = new Ville();
        V.nom = Integer.parseInt(ligne.nextToken());
        V.x = Float.parseFloat(ligne.nextToken());
        V.y = Float.parseFloat(ligne.nextToken());
        return V;
    }

    private ArrayList<ArrayList<Integer>> clsArray() {
        cls = new ArrayList<>();
        for (int i = 0; i <= this.nbClus; i++) {
            cls.add(new ArrayList<Integer>());
            for (int j = 0; j < this.clus.length; j++) {
                if (this.clus[j] == i) {
                    cls.get(i).add(j + 1);
                }
            }
        }
        return cls;
    }

    /**
     * Retourne la ville la plus proche parmi une liste de villes
     *
     * @param V Ville pour laquelle on cherche le plus proche voisin
     * @param champ Matrice des distEucances de l'instance contenant la
     * permutation
     * @return Une le coût de la permutation
     */
    private int ppv(int V, ArrayList<Integer> champ) {
        int dist = 0;
        if (champ.contains(V)) {
            champ.remove(V);
        }
        int Vp = champ.get(0);
        for (int i = 0; i < champ.size(); i++) {
            if (/*dlc[clus[champ.get(i) - 1]-1]==0 && */this.MatDis[V - 1][champ.get(i) - 1] < dist || dist == 0) {
                Vp = champ.get(i);
                dist = this.MatDis[V - 1][Vp - 1];
            }
        }
        return Vp;
    }

    public ArrayList<Integer> copieList() {
        ArrayList<Integer> copie = new ArrayList<>();
        for (int i = 0; i < this.dim; i++) {
            copie.add(this.villes.get(i).nom);
        }
        return copie;
    }

    private int maxArray(int[] arr) {
        int m = 0;
        for (int i : arr) {
            if (i > m) {
                m = i;
            }
        }
        return m;
    }

    /**
     * Heuristique du plus proche voisin
     *
     * @param Vd Ville de départ
     * @return Une liste de villes dans l'ordre à suivre
     */
    public int[] gloutPPV(int Vd) {
        int i = 0, k = 0;
        int[] chemin = new int[nbClus];
//        int[] dlc = new int[nbClus];
        pos = new int[nbClus + 1];
//        cc = new int[nbClus];
        ArrayList<Integer> champ = this.copieList();
        int Vi = Vd, x;
        chemin[i] = (Vd);
        pos[clus[Vi - 1]] = i;
        champ.remove(Vd - 1);
        for (int j = 0; j < champ.size(); j++) {
            if (clus[Vd - 1] == clus[champ.get(j) - 1]) {
                champ.remove(j--);
            }
        }
//        dlc[clus[Vd-1]-1] = 1;
        while (++i < nbClus) {
            Vi = ppv(Vi, champ);
            pos[clus[Vi - 1]] = i;
            chemin[i] = (Vi);
            x = champ.indexOf(Vi);
            champ.remove(x);
            for (int j = 0; j < champ.size(); j++) {
                if (clus[Vi - 1] == clus[champ.get(j) - 1]) {
                    champ.remove(j--);
                }
            }
        }
        return chemin;
    }
    
    public int[] pseudoRandConstruction(){
        int[] tour = new int[this.nbClus];
        pos = new int[nbClus + 1];
        int k, t, n =tour.length;
        
        for(int j=0;j<n;j++)
            tour[j]=j+1;
        
        while(n > 0){
            k = (int) (Math.random() * n);
            t = tour[k];
            tour[k] = tour[n-1];
            tour[n-1] = t;
            pos[t] = n-1;
            tour[n-1] = cls.get(tour[n-1]).get(((int) (Math.random() * cls.get(tour[n-1]).size())));
            
            n--;
        }
        
//        tour[0] = cls.get(tour[0]).get(((int) (Math.random() * cls.get(tour[0]).size())));
        
        return tour;
    }
    
    LinkedList<Triplet<int[], Integer, Long>> initPop;

    LinkedList<Triplet<int[], Integer, Long>> makePopulation(int bks, int nbrD, double Lzero, double Pzero, int t) throws InterruptedException{
        cpu = getCpuTime();
        LinkedList<Triplet<int[], Integer, Long>> population = new LinkedList<>();
        initPop = new LinkedList<>();
        int tour[],k;
        Triplet<int[], Integer, Long> perm;
        
        for(int i=0;i<nbClus/2;i++){
            k=0;
            tour = pseudoRandConstruction();
            tour = clusterOpt(tour);
            
//            System.out.println("TC tour lenght: "+tour.length);
            sol = BLS(tour, bks, nbrD, Lzero, Pzero, t);
            perm = new Triplet<>(sol.getValue0(), sol.getValue1(), cpu);
            initPop.add(perm);
            
//            System.out.println("BLS tour lenght: "+sol.getValue0().length);
            while(k<i && population.get(k).getValue1() <  perm.getValue1())
                k++;
            population.add(k , perm);
        }
        
        return population;        
    }
    
    LinkedList<Triplet<int[], Integer, Long>> onePointX(LinkedList<Triplet<int[], Integer, Long>> pop,int bks, int nbrD, double Lzero, double Pzero, int t) throws InterruptedException{
        int x = (int) (Math.random() * pop.size()), y;
        Triplet<int[], Integer, Long> p1 = initPop.get(x);
        y=x;
        while(x==y)
            y = (int) (Math.random() * pop.size());
        Triplet<int[], Integer, Long> p2 = pop.get(y);
        Triplet<int[], Integer, Long> o1, o2;
        int     to1[] = new int[nbClus], 
                to2[] = new int[nbClus], 
                dlb1[] = new int[nbClus], 
                dlb2[] = new int[nbClus];
        
        int pt = (int) (Math.random() * this.nbClus);
        
        //Offspring 1
        for(int i=0;i<pt;i++){
            to1[i] = p1.getValue0()[i];
            pos[clus[to1[i]-1]] = i;
            dlb1[clus[to1[i]-1]-1] = 1;
        }
        int j=pt;
        for(int i=0;i<nbClus;i++){
            if(j==nbClus){
                break;
            }else if(dlb1[clus[p2.getValue0()[i]-1]-1] == 0){
                to1[j] = p2.getValue0()[i];
                pos[clus[to1[j]-1]] = j;
                j++;
            }
        }
        
        sol = BLS(to1, bks, nbrD, Lzero, Pzero, t);
        o1 = new Triplet<>(sol.getValue0(), sol.getValue1(), cpu);
        int k=0;
        while(k<pop.size() && pop.get(k).getValue1() <  sol.getValue1())
                k++;
        pop.add(k , o1);
        
        //Offspring 2
        for(int i=0;i<pt;i++){
            to2[i] = p2.getValue0()[i];
            pos[clus[to2[i]-1]] = i;
            dlb2[clus[to2[i]-1]-1] = 1;
        }
        j=pt;
        for(int i=0;i<nbClus;i++){
            if(j==nbClus){
                break;
            }else if(dlb2[clus[p1.getValue0()[i]-1]-1] == 0){
                to2[j] = p1.getValue0()[i];
                pos[clus[to2[j]-1]] = j;
                j++;
            }
        }
        sol = BLS(to2, bks, nbrD, Lzero, Pzero, t);
        o2 = new Triplet<>(sol.getValue0(), sol.getValue1(), cpu);
        k=0;
        while(k<pop.size() && pop.get(k).getValue1() <  sol.getValue1())
                k++;
        pop.add(k , o2);
        
        pop.removeLast();
        pop.removeLast();
        
        
        
        return pop;
    }
    
    LinkedList<Triplet<int[], Integer, Long>> uniformX(LinkedList<Triplet<int[], Integer, Long>> pop,int bks, int nbrD, double Lzero, double Pzero, int t) throws InterruptedException{
        int x = (int) (Math.random() * pop.size()), y;
        Triplet<int[], Integer, Long> p1 = tournamentSelection(pop, 3);
//        y=x;
//        while(x==y)
//            y = (int) (Math.random() * pop.size());
        Triplet<int[], Integer, Long> p2 = tournamentSelection(pop, 3);
        int k = 0;
        while(p1.getValue1()-p2.getValue1() == 0){
            p2 = tournamentSelection(pop, 3);
            if(k++ > 10)
                break;
        }
        Triplet<int[], Integer, Long> o1, o2;
        int     to1[] = new int[nbClus], 
                to2[] = new int[nbClus],
                dlb1[] = new int[nbClus], 
                dlb2[] = new int[nbClus];
        
        int mask[] = new int[nbClus];
        for(int i=0; i<nbClus;i++)
            mask[i] = Math.random() > 0.5 ? 1 : 0;
        
//        System.out.println("X: "+x+" Y: "+y+" mask: "+Arrays.toString(mask));
        
     
        
        //Offspring 1
        for(int i=0; i<nbClus;i++){
            if(mask[i]==1){
                to1[i] = p1.getValue0()[i];
                pos[clus[to1[i]-1]] = i;
                dlb1[clus[to1[i]-1]-1] = 1;
            }
        }
        int j=0;
        for(int i=0;i<nbClus;i++){
            if(mask[i] == 0){
                while( dlb1[clus[p2.getValue0()[j++]-1]-1] == 1){}
                to1[i] = p2.getValue0()[j-1];
                pos[clus[to1[i]-1]] = i;
            }
        }
//        System.out.println("Tour1: "+Arrays.toString(to1));
        Pair<int[], Integer> oo1 = doubleBridgeMove(new Pair(to1, coutArrayList(to1)), 0, nbClus / 8, true);
//        o1 = o1.setAt0(oo1.getValue0());
//        o1 = o1.setAt1(oo1.getValue1());

            sol = BLS(oo1.getValue0(), bks, nbrD, Lzero, Pzero, t);
            o1 = new Triplet<>(sol.getValue0(), sol.getValue1(), cpu);

//        int k=0;
//        while(k<pop.size() && pop.get(k).getValue1() <  sol.getValue1())
//                k++;
//        pop.add(k , o1);
        
        pop = addOrdered(pop, o1, true);
        
        //Offspring 2
        for(int i=0;i<nbClus;i++){
            if(mask[i]==0){
                to2[i] = p2.getValue0()[i];
                pos[clus[to2[i]-1]] = i;
                dlb2[clus[to2[i]-1]-1] = 1;
            }
        }
        j=0;
        for(int i=0;i<nbClus;i++){
            if(mask[i] == 1){
                while( dlb2[clus[p1.getValue0()[j++]-1]-1] == 1){}
                to2[i] = p1.getValue0()[j-1];
                pos[clus[to2[i]-1]] = i;
            }
        }
//        System.out.println("Before: "+Arrays.toString(to2));
        Pair<int[], Integer> oo2 = doubleBridgeMove(new Pair(to2, coutArrayList(to2)), 0, nbClus/ 8, true);
//        System.out.println("After: "+Arrays.toString(oo2.getValue0()));
//        o2 = o2.setAt0(oo2.getValue0());
//        o2 = o2.setAt1(oo2.getValue1());

            sol = BLS(oo2.getValue0(), bks, nbrD, Lzero, Pzero, t);
            o2 = new Triplet<>(sol.getValue0(), sol.getValue1(), cpu);
//        k=0;
//        while(k<pop.size() && pop.get(k).getValue1() <  o2.getValue1())
//                k++;
//        pop.add(k , o2);
        
        
        pop = addOrdered(pop, o2, true);
        
        pop.removeLast();
        pop.removeLast();
        
        
        
        return pop;
    }
    
    LinkedList<Triplet<int[], Integer, Long>> addOrdered(LinkedList<Triplet<int[], Integer, Long>> pop, Triplet<int[], Integer, Long> elm, boolean mut){
//        System.out.println("add");
        int k=0;
        while(k<pop.size() && pop.get(k).getValue1() <=  elm.getValue1()){
            k++;
        }
        
//        if(mut && k<pop.size() && k> 0 && pop.get(k-1).getValue1() -  elm.getValue1() == 0){
//            System.out.println(pop.get(k-1).getValue1()+" ? "+elm.getValue1());
//            elm = exchangeMutation(elm);
//            return addOrdered(pop, elm, false);
//        }
        pop.add(k , elm);
        return pop;
    }
    
    Triplet<int[], Integer, Long> exchangeMutation(Triplet<int[], Integer, Long> perm){
        int n = perm.getValue0().length;
        int j,i = j =(int) (Math.random()* n);
        while(i==j)
            j=(int) (Math.random()* n);
        if(i==0)    i++;
        if(j==0)    j++;
        int delta = -deltaSWAP(perm.getValue0(), i, j);
        perm = perm.setAt0(swap(perm.getValue0(), (((i) % n + n) % n), j));
        perm = perm.setAt1(perm.getValue1()+delta);
        return perm;
    }
    
    Triplet<int[], Integer, Long> tournamentSelection(LinkedList<Triplet<int[], Integer, Long>> pop, int s){
        int x = (int) (Math.random() * pop.size()), best = x;
        for(int i=0;i<s;i++){
            x = (int) (Math.random() * pop.size());
            if(pop.get(x).getValue1() < pop.get(best).getValue1())
                best = x;
        }
        return pop.get(best);
    }
    
    
    /**
     * Coût d'une solution
     *
     * @param liste Solution proposTée
     * @return Le coût de la solution proposTée
     */
    public int coutArrayList(int[] liste) {
        int cout = 0;
        for (int i = 0; i < this.nbClus; i++) {
            cout += this.MatDis[liste[i] - 1][liste[(((i + 1) % nbClus + nbClus) % nbClus)] - 1];
        }
        return cout;
    }

    public int[] CBLS(int[] tour) {
        int delta, deltab = 0;
        int nn = this.nbClus, u = 0, v = 0;
        boolean improved = false;
        for (int i = 0; i <= nn - 2; i++) {
            for (int j = i + 2; (i == 0 && j < nn - 1) || (i > 0 && j < nn); j++) {
                delta = -delta2Opt(tour, i, j);
                if (delta < deltab) {
                    improved = true;
                    u = i;
                    v = j;
                    deltab = delta;
//                        tour = swapAll(tour, i + 1, j);
//                        i=0; j=2;
                }
            }
        }
        if (improved) {
            tour = swapAll(tour, tour[u + 1], tour[v]);
        }
        return tour;
    }
    
    ArrayList<Integer> reduction = new ArrayList<>();
    int red;
    
    private void copy(ArrayList<Integer> src, ArrayList<Integer> tar){
        tar.clear();
        for(int e: src)
            tar.add(e);
    }
    
    public int[] firstClusterSelection(int[] tour){
        int min=1, n = this.nbClus;
        for(int i=1;i<cls.size();i++){
            if(cls.get(i).size()<cls.get(min).size())
                min = i;
        }
        tour = swap(tour, tour[0], tour[((pos[min]) % n)]);
        
        return tour;
    }
    
    public int[] firstClusterReduction(int[] tour){
//        System.out.println("FCR");
        int n = this.nbClus, min, dis, count=1;
        int i,j,k;
        tour = firstClusterSelection(tour);
//        System.out.println(Arrays.toString(tour) + " - " + coutArrayList(tour));
        int u=clus[tour[n-1]-1], 
                v=clus[tour[1]-1], 
                r=clus[tour[0]-1];
        copy(cls.get(r), reduction);
        red = r;
//        System.out.println("u = "+u+"; v = "+v+"; r = "+r);
        int c[][] = new int[cls.get(u).size()][cls.get(v).size()], l[][] = new int[cls.get(u).size()][cls.get(v).size()];
        for(i=0;i<cls.get(u).size();i++){
            for(j=0;j<cls.get(v).size();j++){
                min = MatDis[cls.get(u).get(i) - 1][cls.get(r).get(0) - 1] + MatDis[cls.get(r).get(0) - 1][cls.get(v).get(j) - 1] ;
                for(k=1;k<cls.get(r).size();k++){
                    dis = MatDis[cls.get(u).get(i) - 1][cls.get(r).get(k) - 1] + MatDis[cls.get(r).get(k) - 1][cls.get(v).get(j) - 1];
                    if(dis < min){
                        min = dis;
                        count =1;
                    }else if(dis == min){
                        count++;
                    }
                }
                l[i][j] = min;
                c[i][j] = count;
            }
        }
        kloop:
        for(k=0;k<cls.get(r).size();k++){
            for(i=0;i<cls.get(u).size();i++){
                for(j=0;j<cls.get(v).size();j++){
                    if(l[i][j]==MatDis[cls.get(u).get(i) - 1][cls.get(r).get(k) - 1] + MatDis[cls.get(r).get(k) - 1][cls.get(v).get(j) - 1]
                            && c[i][j]==1)
                        continue kloop;
                }
            }
            for(i=0;i<cls.get(u).size();i++){
                for(j=0;j<cls.get(v).size();j++){
                    if(l[i][j]==MatDis[cls.get(u).get(i) - 1][cls.get(r).get(k) - 1] + MatDis[cls.get(r).get(k) - 1][cls.get(v).get(j) - 1])
                        c[i][j]--;
                }
            }
//            System.out.println("→ "+cls.get(r).get(k));
            cls.get(r).remove(k);
            k--;
        }
        return tour;
    }
    

    public int[] clusterOpt(int[] tour) {
        int n = this.nbClus, u = 0, v = 0, w = 0, vv, dist, dist0, dist00, total = 0, delta, delta0, deltaMin;
        boolean improved = false;
        deltaMin = 0;
        tour = firstClusterReduction(tour);
        for (int j = 0; j < cls.get(clus[tour[0] - 1]).size(); j++) {
            for (int k = 0; k < cls.get(clus[tour[1] - 1]).size(); k++) {
//                System.out.println("Avant: "+Arrays.toString(tour) + " - " + coutArrayList(tour));
                delta = deltaPert(tour, new Couple(clus[tour[0] - 1], clus[tour[1] - 1]), cls.get(clus[tour[0] - 1]).get(j), cls.get(clus[tour[1] - 1]).get(k), false);
//                System.out.println("Après: "+Arrays.toString(tour) + " - " + coutArrayList(tour)+" Delta: "+delta);
                if (deltaMin > delta) {
                    improved = true;
                    u = j;
                    v = k;
                    deltaMin = delta;
//                        System.out.print(" -> "+dist+" ["+cls.get(clus[tour[0]-1]).get(u)+","+cls.get(clus[tour[1]-1]).get(v)+"]");
                }
            }
        }
        if (improved) {
            tour[0] = cls.get(clus[tour[0] - 1]).get(u);
            tour[1] = cls.get(clus[tour[1] - 1]).get(v);
            total += deltaMin;
//                System.out.println(Arrays.toString(tour) + " - " + coutArrayList(tour));
        }
         
        
        for (int i = 2; i < n ; i++) {
            deltaMin = 0; improved = false;
            for (int j = 0; j < cls.get(clus[tour[0] - 1]).size(); j++) {
//                System.out.println("AVANT: "+Arrays.toString(tour) + " - " + coutArrayList(tour));
                delta0 = deltaPert(tour, new Couple(clus[tour[0] - 1], clus[tour[1] - 1]), cls.get(clus[tour[0] - 1]).get(j), tour[1], false);
                vv = tour[0];
                tour[0] = cls.get(clus[tour[0] - 1]).get(j);
//                dist00 = this.MatDis[cls.get(clus[tour[0]-1]).get(j)-1][tour[1]-1] + this.MatDis[cls.get(clus[tour[0]-1]).get(j)-1][tour[n-1]-1];
                for (int k = 0; k < cls.get(clus[tour[(((i) % n + n) % n)] - 1]).size(); k++) {
                    for (int x = 0; x < cls.get(clus[tour[(((i - 1) % n + n) % n)] - 1]).size(); x++) {
//                        System.out.println("AVANT: "+Arrays.toString(tour) + " - " + coutArrayList(tour));
                        delta = delta0 + deltaPert(tour, new Couple(clus[tour[(((i - 1) % n + n) % n)] - 1],clus[tour[(((i) % n + n) % n)] - 1]),  cls.get(clus[tour[(((i - 1) % n + n) % n)] - 1]).get(x),cls.get(clus[tour[(((i) % n + n) % n)] - 1]).get(k), false);
//                        System.out.println("i: "+i+"; j: "+j+"; k: "+k+"; x: "+x);
//                        System.out.println("APRÈS: "+Arrays.toString(tour) + " - " + coutArrayList(tour)+" DELTA = "+delta);
                        
                        if (deltaMin > delta) {
                            improved = true;
                            u = j;
                            v = k;
                            w = x;
                            deltaMin = delta;
                        }
                    }
                }
                tour[0] = vv;
            }
            if (improved) {
                tour[0] = cls.get(clus[tour[0] - 1]).get(u);
                tour[(((i - 1) % n + n) % n)] = cls.get(clus[tour[(((i - 1) % n + n) % n)] - 1]).get(w);
                tour[(((i) % n + n) % n)] = cls.get(clus[tour[(((i) % n + n) % n)] - 1]).get(v);
                total += deltaMin;
//                System.out.println(Arrays.toString(tour) + " - " + coutArrayList(tour));
            }
        }
        copy(reduction, cls.get(red));
        return tour;
    }

    public int[] perturb(int[] tour) {
        int j, i = j = (int) (Math.random() * nbClus);
        do {
            j = (int) (Math.random() * nbClus);
        } while (i == j);
        tour = swap(tour, tour[i], tour[j]);
        return tour;
    }

    /**
     * Permute la posTition de deux villes adjacentes pour une solution
     * <code>S</code>
     *
     * @param S Solution proposTée
     * @param i le nom de la première ville
     * @return Une nouvelle solution
     */
    public int[] swap(int[] S, int i) {
        int x = pos[clus[i - 1]];
        S[x] = S[x + 1];
        S[x + 1] = i;
        pos[clus[i - 1]] = x + 1;
        pos[clus[S[x] - 1]] = x;
        return S;
    }

    /**
     * Permute la posTition de deux villes pour une solution <code>S</code>
     *
     * @param S Solution proposTée
     * @param i indice de la première ville
     * @param j indice de la deuxième ville
     * @return Une nouvelle solution
     */
    public int[] swap(int[] S, int i, int j) {
        int x = pos[clus[i - 1]], y = pos[clus[j - 1]];
//        System.out.println(Arrays.toString(S)+"swap("+i+"("+x+"),"+j+"("+y+"))");
        S[x] = j;
        S[y] = i;
        pos[clus[i - 1]] = y;
        pos[clus[j - 1]] = x;
//        System.out.println("After swap: "+Arrays.toString(S));
        return S;
    }

    /**
     * Inverse le sens d'une portion (entre deux villes) de la solution donné
     *
     * @param S Solution proposTée
     * @param i indice de la première ville
     * @param j indice de la deuxième ville
     * @return Une nouvelle solution
     */
    public int[] swapAll(int[] S, int i, int j) {
        int x = pos[clus[i - 1]], y = pos[clus[j - 1]];
        if (y < x) {
            int k = y;
            y = x;
            x = k;
        }
        while (x < y) {
            S = swap(S, S[x], S[y]);
            x++;
            y--;
        }
        return S;
    }

    public int delta2Opt(int[] S, int i, int j) {
        if (i > j) {
            int t = i;
            i = j;
            j = t;
        }
        int n = this.nbClus;
        return -this.MatDis[S[i] - 1][S[j] - 1]
                - this.MatDis[S[(((i + 1) % n + n) % n)] - 1][S[(((j + 1) % n + n) % n)] - 1]
                + this.MatDis[S[i] - 1][S[(((i + 1) % n + n) % n)] - 1]
                + this.MatDis[S[j] - 1][S[(((j + 1) % n + n) % n)] - 1];
    }

    int deltaSWAP(int[] S, int i, int j) {
        int n = this.nbClus;
        if (i == 0 && j == (n - 1)) {
            return -this.MatDis[S[0] - 1][S[n - 2] - 1]
                    - this.MatDis[S[n - 1] - 1][S[1] - 1]
                    + this.MatDis[S[0] - 1][S[1] - 1]
                    + this.MatDis[S[n - 1] - 1][S[n - 2] - 1];
        }
        if ((j % n + n) % n == ((i + 1) % n + n) % n || (j % n + n) % n == ((i - 1) % n + n) % n) {
            return -this.MatDis[S[(((i - 1) % n + n) % n)] - 1][S[j] - 1]
                    - this.MatDis[S[i] - 1][S[(((j + 1) % n + n) % n)] - 1]
                    + this.MatDis[S[(((i - 1) % n + n) % n)] - 1][S[i] - 1]
                    + this.MatDis[S[j] - 1][S[(((j + 1) % n + n) % n)] - 1];
        }
        return -this.MatDis[S[(((i - 1) % n + n) % n)] - 1][S[j] - 1]
                - this.MatDis[S[j] - 1][S[i + 1] - 1]
                - this.MatDis[S[j - 1] - 1][S[i] - 1]
                - this.MatDis[S[i] - 1][S[(((j + 1) % n + n) % n)] - 1]
                + this.MatDis[S[(((i - 1) % n + n) % n)] - 1][S[i] - 1]
                + this.MatDis[S[i] - 1][S[i + 1] - 1]
                + this.MatDis[S[j - 1] - 1][S[j] - 1]
                + this.MatDis[S[j] - 1][S[(((j + 1) % n + n) % n)] - 1];
    }

    int[] insert(int[] S, int i, int j) {
        int x = pos[clus[i - 1]], y = pos[clus[j - 1]];
        if (y < x) {
            int k = y;
            y = x;
            x = k;
        }
        for (int k = x; k < y; k++) {
            S[k] = S[k + 1];
            pos[clus[S[k] - 1]]--;
        }
        S[y] = i;
        pos[clus[i - 1]] = y;
        return S;
    }

    int deltaInsert(int[] S, int i, int j) {
        int n = this.nbClus;
        return this.MatDis[clus[S[(((pos[clus[i - 1]] - 1) % n + n) % n)] - 1] - 1][clus[S[(((pos[clus[i - 1]] + 1) % n + n) % n)] - 1] - 1]
                + this.MatDis[j - 1][i - 1]
                + this.MatDis[i - 1][clus[S[(((pos[clus[j - 1]] + 1) % n + n) % n)] - 1] - 1]
                - this.MatDis[clus[S[(((pos[clus[i - 1]] - 1) % n + n) % n)] - 1] - 1][i - 1]
                - this.MatDis[clus[S[(((pos[clus[i - 1]] + 1) % n + n) % n)] - 1] - 1][i - 1]
                - this.MatDis[clus[S[(((pos[clus[j - 1]] + 1) % n + n) % n)] - 1] - 1][j - 1];
    }

    private int deltaPert(int[] s, Couple c, int nv1, int nv2, boolean keep) {
        int delta, n = this.nbClus, v1, v2;

        v1 = s[pos[c.getV1()]];
        v2 = s[pos[c.getV2()]];

        if ((((pos[c.getV1()])%n+n)%n) == (((pos[c.getV2()] - 1)%n+n)%n) || ((((pos[c.getV1()])%n+n)%n) == (((pos[c.getV2()] + 1)%n+n)%n))) {
            delta = -this.MatDis[v1 - 1][s[(((pos[c.getV1()] - 1) % n + n) % n)] - 1] - this.MatDis[v1 - 1][s[(((pos[c.getV1()] + 1) % n + n) % n)] - 1]
                    - this.MatDis[v2 - 1][s[(((pos[c.getV2()] + 1) % n + n) % n)] - 1];
        } else {
            delta = -this.MatDis[v1 - 1][s[(((pos[c.getV1()] - 1) % n + n) % n)] - 1] - this.MatDis[v1 - 1][s[(((pos[c.getV1()] + 1) % n + n) % n)] - 1]
                    - this.MatDis[v2 - 1][s[(((pos[c.getV2()] - 1) % n + n) % n)] - 1] - this.MatDis[v2 - 1][s[(((pos[c.getV2()] + 1) % n + n) % n)] - 1];
        }

        s[pos[c.getV1()]] = nv1;
        s[pos[c.getV2()]] = nv2;

        if (pos[c.getV1()] == pos[c.getV2()] - 1 || (pos[c.getV1()] == pos[c.getV2()] + 1)) {
            delta += this.MatDis[nv1 - 1][s[(((pos[c.getV1()] - 1) % n + n) % n)] - 1] + this.MatDis[nv1 - 1][s[(((pos[c.getV1()] + 1) % n + n) % n)] - 1]
                    + this.MatDis[nv2 - 1][s[(((pos[c.getV2()] + 1) % n + n) % n)] - 1];
        } else {
            delta += this.MatDis[nv1 - 1][s[(((pos[c.getV1()] - 1) % n + n) % n)] - 1] + this.MatDis[nv1 - 1][s[(((pos[c.getV1()] + 1) % n + n) % n)] - 1]
                    + this.MatDis[nv2 - 1][s[(((pos[c.getV2()] - 1) % n + n) % n)] - 1] + this.MatDis[nv2 - 1][s[(((pos[c.getV2()] + 1) % n + n) % n)] - 1];
        }
        
        if(!keep){
            s[pos[c.getV1()]] = v1;
            s[pos[c.getV2()]] = v2;
        }

        return delta;
    }
    
    
    int[] opt2Strong(int[] t){
        boolean stop = false;
        int u,v,i,j,delta,deltaMin=0,n=t.length-1;
            while (!stop) {
                stop = true;
                u=0;v=0;
                for (i = 0; i <= n - 2; i++) {
                    for (j = i + 2; (i == 0 && j < n - 1) || (i > 0 && j < n); j++) {
                        delta = -delta2Opt(t, i, j);
                        if (delta < deltaMin) {
                            u=i;    v=j;
                            deltaMin = delta;
                            stop = false;
                        }
                    }
                }
                
                if(u!=0 && v!=0)
                    t=swapAll(t, t[u + 1], t[v]);
            }
        return t;
    }

    /**
     * Recherche Locale d'Evasion (Breakout Local Search)
     *
     * @param S Solution initiale
     * @return Une nouvelle solution (améliorée)
     */
    private static int[] Sb;
    private static int Cb, W, T, iter, nBdesc, Lw;
    private static int H[][];
    private static boolean dlb[];
    private static long cpu;
    /*
     sol est un Objet contenant quatres valeurs:
     Une solution S représenté dans une ArrayList
     Le cout de la solution
     Le temps CPU consommé pour trouvé la solution
     Le nombre de descentes effectué pour arriver à la solution
     */
    private Quartet<int[], Integer, Long, Integer> sol;

    public Quartet<int[], Integer, Long, Integer> BLS(int[] S, int bks, int nbrD, double Lzero, double Pzero, int t) throws InterruptedException {
//        ArrayList<ArrayList<Integer>> cls = clsArray();
        int n = this.dim, nn = this.nbClus, C = coutArrayList(S), Cp = C, Cc;
        final int L0 = 1;//(int) (Lzero * nn); //1;
        H = new int[nn][nn];
        int L = L0, NbP = (int) (n / 2), stp = 0, nbd = 0;
        iter = 1;
        nBdesc = 0;
        Cb = C;
        W = 0;
        T = t;
        Lw = 0;
        Sb = new int[nn];
        System.arraycopy(S, 0, Sb, 0, this.nbClus);
        sol = new Quartet(new int[nn], 0, 0, 0);
        Pair<int[], Integer> pair = new Pair<int[], Integer>(S, C);
        ArrayList<Couple> M;
        sol = sol.setAt0(Sb);
        sol = sol.setAt1(Cb);
        sol = sol.setAt2(getCpuTime() - cpu);
        sol = sol.setAt3(nBdesc);
//        ArrayList<Integer>[] test = delaunay();
//        for(ArrayList<Integer> k: test)
//            System.out.println(k);
//        System.out.println("*********************************");
        /*------------------DÉBUT---------------*/
        int i, j, u, v, delta, deltaMin;
        boolean stop, perturbed;
        while ((nBdesc <= n * nbrD) && Cb > bks) {
//            System.out.println("Start: "+Arrays.toString(pair.getValue0())+"\tCost: "+coutArrayList(pair.getValue0())+"\tC: "+C);
            nBdesc++;nbd++;
//            System.out.println("Descente N° "+nBdesc);
            stop = false;
            /*--Descente Forte 2-Opt--*/
//            System.out.println("Début de la descente forte: "+Arrays.toString(pair.getValue0())+" ["+pair.getValue1()+"]");
            while (!stop) {
                C = pair.getValue1();
                u = 0;
                v = 0;
                stop = true;
                deltaMin = 0;
//                System.out.println("\titération "+iter);
                for (i = 0; i <= nn - 2; i++) {
//                    System.out.println("---> i = "+i);
                    for (j = i + 2; (i == 0 && j < nn - 1) || (i > 0 && j < nn); j++) {
//                    for(int j : test[pair.getValue0()[i]-1]){
//                        if(pos[clus[j-1]]>=i+2 || pos[clus[j-1]]<=i-2){
                        delta = -delta2Opt(pair.getValue0(), i, j);
//                                System.out.println("--->"+delta);
                        if (delta < deltaMin) {
                            u = i;
                            v = j;
                            /*if(u>v){
                             int f=v;
                             v=u;
                             u=f;
                             }*/
                            stop = false;
                            deltaMin = delta;
                        }
                    }
//                    }
                }
                if (u != 0 || v != 0) {
//                    System.out.println(test[pair.getValue0()[u]-1]);
//                        System.out.println("\tMeilleure amélioration: ("+pair.getValue0()[u]+","+pair.getValue0()[v]+")");
//                        System.out.println(Arrays.toString(pair.getValue0()));
                    C += deltaMin;
//                    System.out.println("C: "+C);
                    pair = pair.setAt0(swapAll(pair.getValue0(), pair.getValue0()[u + 1], pair.getValue0()[v]));
                    pair = pair.setAt1(C);
//                        System.out.println("\t"+Arrays.toString(pair.getValue0())+" ["+pair.getValue1()+"]");
                    H[clus[pair.getValue0()[u] - 1] - 1][clus[pair.getValue0()[(((u + 1) % nn + nn) % nn)] - 1] - 1] = iter;
//                    H[pair.getValue0()[(((u + 1) % n + n) % n)] - 1][pair.getValue0()[u] - 1] = iter;
                    H[clus[pair.getValue0()[v] - 1] - 1][clus[pair.getValue0()[(((v + 1) % nn + nn) % nn)] - 1] - 1] = iter;
//                    H[pair.getValue0()[(((v + 1) % n + n) % n)] - 1][pair.getValue0()[v] - 1] = iter;
                    pair = pair.setAt0(clusterOpt(pair.getValue0()));
                    Cc = coutArrayList(pair.getValue0());
                    pair = pair.setAt1(Cc);
//                    System.out.println("Cc: "+Cc);
                    C = Cc;
                }
//                Cc = coutArrayList(pair.getValue0());
                
//                if (Cc < C) {
//                    stop = false;
//                }
                
                iter++;
//                System.out.println(((float) nBdesc / (n * nbrD)*100)+"%\t"+""+nBdesc +"/"+ (n * nbrD));

            }

//            System.out.println("Fin de la descente");
//            System.out.println("Matrice H des historiques...");
//            printMat(H);
            /*-----Fin de la descente forte-----*/
            perturbed = false;
            if (C < Cb) {
//                System.out.println("Amélioration de la meilleur solution: " + Cb + " → " + pair.getValue1() + " - " + 100 * (float) (pair.getValue1() - bks) / bks + " - " + ((float) nBdesc / (n * nbrD) * 100) + "%\t");
                System.arraycopy(pair.getValue0(), 0, Sb, 0, nn);
                Cb = pair.getValue1();
                sol = sol.setAt0(Sb);
                sol = sol.setAt1(Cb);
                sol = sol.setAt2(getCpuTime() - cpu);
                sol = sol.setAt3(nBdesc);
                W = 0;
//                nBdesc = nBdesc - (nBdesc / 2);
                //NbP=(int) (n / 2);
            } else /*if (Cp <= C)*/ {
//                System.out.println("Pas d'amélioration");
                W++;
            }
            if (W >= T) {
//                System.out.println("Exécution de la forte perturbation "+stp+" "+((float) nBdesc / (n * nbrD)*100)+"%\t"+""+nBdesc +"/"+ (n * nbrD));
//                if (nBdesc / (nn * nbrD) % 10 == 0) System.out.println((nBdesc / (nn * nbrD))+"%\t");
                stp++;
                perturbed = true;
                M = updateSetMswap(pair, Pzero);
                pair = swapPerturb(pair, L * nn, M, Pzero);
//                M = updateSetMopt(pair, Pzero);
//                pair = optPerturb(pair, L*nn, M, Pzero);
//                pair =dbmPerturb(pair, n/2);
                W = 0;
//                nBdesc = nBdesc - (nBdesc / 8);
            } else if (C == Cp) {
                Lw++;
                L = L0 + (Lw / 3);
            } else {
                Lw = 0;
            }
            Cp = C;
            if (!perturbed) {
//                System.out.println("Exécution de "+L+" perturbation(s)");
//                pair = perturbation(pair, L, Pzero);
//                M = updateSetMopt(pair, Pzero);
//                pair = optPerturb(pair, Lw, M, Pzero);
                M = updateSetMswap(pair, Pzero);
                pair = swapPerturb(pair, Lw, M, Pzero);
            }
        }
        /*-------------------FIN----------------*/
        cpu = getCpuTime() - cpu;
//        System.out.println(Cb + "\t" + cpu / 1000000 + "\tStrong: " + stp);
        return sol;
    }

    private Pair<int[], Integer> perturbation(Pair<int[], Integer> S, int L, double Pzero) {
        ArrayList<Couple> M = updateSetMopt(S, Pzero);
        if (W < T) {
            if (Lw % 3 == 0) {
//                System.out.println("\tPerturbation par voisinage 2-Opt");
                S = optPerturb(S, L, M, Pzero);
            } else if (Lw % 3 == 1) {
//                System.out.println("\tPerturbation par voisinage insert");
                S = swapPerturb(S, L, M, Pzero);
            } else if (Lw % 3 == 2) {
//                System.out.println("\tPerturbation par voisinage swap");
                S = swapPerturb(S, L, M, Pzero);
            }
        }
        return S;
    }

    private Pair<int[], Integer> strongPerturbation(Pair<int[], Integer> S) {
        //System.out.println("strongPerturbation");
        int i, k = 0;
        int[] Sp = new int[this.nbClus];
        for (i = 0; i < this.nbClus; i = i + 2) {
            pos[clus[S.getValue0()[i] - 1]] = k;
            Sp[k++] = (clus[S.getValue0()[i] - 1]);
        }
        for (i = 1; i < this.nbClus; i = i + 2) {
            pos[clus[S.getValue0()[i] - 1]] = k;
            Sp[k++] = (clus[S.getValue0()[i] - 1]);
        }
        S = S.setAt0(Sp);
        S = S.setAt1(coutArrayList(Sp));
        return S;
    }

    private Pair<int[], Integer> dbmPerturb(Pair<int[], Integer> S, int L) {
        for (int i = 0; i < L; i++) {
            S = doubleBridgeMove(S, 0, dim / 8, true);
            if (S.getValue1() < Cb) {
                System.arraycopy(S.getValue0(), 0, Sb, 0, this.nbClus);
                Cb = S.getValue1();
                W = 0;
                sol = sol.setAt0(Sb);
                sol = sol.setAt1(Cb);
                sol = sol.setAt2(getCpuTime() - cpu);
                sol = sol.setAt3(nBdesc);
//                nBdesc = nBdesc - (nBdesc / 2);
            }
        }
        return S;
    }

    private Pair<int[], Integer> swapPerturb(Pair<int[], Integer> S, int L, ArrayList<Couple> M, double Pzero) {
        Couple c;
        int C = S.getValue1(), delta, n = this.nbClus;
        int[] s = S.getValue0();
        int v1, nv1, v2, nv2;
        for (int i = 0; i < L; i++) {
//            if(M.isEmpty()){
//                S = dbmPerturb(S, 1);
//            }
//            else{
//                System.out.println("\t\t\tSaut N° "+(i+1)+"/"+L);
//                System.out.println("\t\t\tListe des candidats: "+M.toString());
            c = M.get((int) (Math.random() * M.size()));
//                System.out.println("\t\t\tCouple sélectionné: "+c);
            delta = -deltaSWAP(s, pos[c.getV1()], pos[c.getV2()]);
//                s = swap(s, c.getV1(), c.getV2());
            s = swap(s, s[(((pos[c.getV1()]) % n + n) % n)], s[pos[c.getV2()]]);
            C += delta;
            H[clus[s[(((pos[c.getV1()] - 1) % n + n) % n)] - 1] - 1][c.getV1() - 1] = iter;  //H[clus[c.getV1()-1]- 1][s[(((pos[clus[c.getV1()-1]]-1)%n+n)%n)]-1] = iter;
            H[c.getV1() - 1][clus[s[(((pos[c.getV1()] + 1) % n + n) % n)] - 1] - 1] = iter;  //H[s[(((pos[clus[c.getV1()-1]]+1)%n+n)%n)]-1][clus[c.getV1()-1]- 1] = iter;
            H[clus[s[(((pos[c.getV2()] - 1) % n + n) % n)] - 1] - 1][c.getV2() - 1] = iter;  //H[clus[c.getV2()-1]- 1][s[(((pos[clus[c.getV2()-1]]-1)%n+n)%n)]-1] = iter;
            H[c.getV2() - 1][clus[s[(((pos[c.getV2()] + 1) % n + n) % n)] - 1] - 1] = iter;  //H[s[(((pos[clus[c.getV2()-1]]+1)%n+n)%n)]-1][clus[c.getV2()-1]- 1] = iter;
//            System.out.println("cls: "+cls.get(c.getV1()));
            nv1 = cls.get(c.getV1()).get(((int) (Math.random() * cls.get(c.getV1()).size())));
            nv2 = cls.get(c.getV2()).get(((int) (Math.random() * cls.get(c.getV2()).size())));

            C += deltaPert(s, c, nv1, nv2, false);

            s[pos[c.getV1()]] = nv1;
            s[pos[c.getV2()]] = nv2;
//                C = coutArrayList(s);

            M = updateSetMswap(S, Pzero);
            iter++;
            if (C < Cb) {
                System.arraycopy(s, 0, Sb, 0, n);
                Cb = C;
                W = 0;
                sol = sol.setAt0(Sb);
                sol = sol.setAt1(Cb);
                sol = sol.setAt2(getCpuTime() - cpu);
                sol = sol.setAt3(nBdesc);
//                nBdesc = nBdesc - (nBdesc / 2);
            }
//            }
        }
        S = S.setAt0(s);
        S = S.setAt1(C);
        return S;
    }

    private Pair<int[], Integer> optPerturb(Pair<int[], Integer> S, int L, ArrayList<Couple> M, double Pzero) {
        Couple c;
        int C = S.getValue1(), n = this.nbClus, delta;
        int[] s = S.getValue0();
        int v1, v2, nv1, nv2;
        for (int i = 0; i < L; i++) {
//            if(M.isEmpty()){
//                S = dbmPerturb(S, 1);
//            }
//            else{
            //            System.out.println("\t\t\tSaut N° "+(i+1)+"/"+L);
            //            System.out.println("\t\t\tListe des candidats: "+M.toString());
            c = M.get((int) (Math.random() * M.size()));
            //            System.out.println("\t\t\tCouple sélectionné: "+c);
            if (pos[c.getV1()] == (pos[c.getV2()] - 1) || (pos[c.getV1()] == 0 && pos[c.getV2()] == n - 1)) {
                System.out.println("SWAP dans OPT");
                delta = -deltaSWAP(s, pos[clus[c.getV1() - 1]], pos[clus[c.getV2() - 1]]);
                s = swap(s, c.getV1(), c.getV2());
            } else {
                delta = -delta2Opt(s, pos[c.getV1()], pos[c.getV2()]);
                s = swapAll(s, s[(((pos[c.getV1()] + 1) % n + n) % n)], s[pos[c.getV2()]]);
            }
            C += delta;
//                s[pos[c.getV1()]] = cls.get(c.getV1()-1).get(0);
//                s[pos[c.getV2()]] = cls.get(c.getV2()-1).get(0);

            nv1 = cls.get(c.getV1()).get(((int) (Math.random() * cls.get(c.getV1()).size())));
            nv2 = cls.get(c.getV2()).get(((int) (Math.random() * cls.get(c.getV2()).size())));

            C += deltaPert(s, c, nv1, nv2, false);

            s[pos[c.getV1()]] = nv1;
            s[pos[c.getV2()]] = nv2;
//                System.out.println("DELTA = "+delta);
//                System.out.println("Pert\tC(DELTA): "+C+"\tC(Cout): "+coutArrayList(s));

            //H[clus[c.getV1()-1]- 1][clus[c.getV2()-1]- 1] = iter;
//                H[clus[c.getV1()-1]- 1][s[(((pos[c.getV1()]+1)%n+n)%n)]-1] = iter;  //H[s[(((pos[c.getV1()]+1)%n+n)%n)]-1][c.getV1()- 1] = iter;
            H[c.getV1() - 1][clus[s[(((pos[c.getV1()] + 1) % n + n) % n)] - 1] - 1] = iter;
            H[c.getV2() - 1][clus[s[(((pos[c.getV2()] + 1) % n + n) % n)] - 1] - 1] = iter;
//                H[clus[c.getV2()-1]- 1][s[(((pos[c.getV2()]+1)%n+n)%n)]-1] = iter;  //H[s[(((pos[c.getV2()]+1)%n+n)%n)]-1][c.getV2()- 1] = iter;
//                H[clus[c.getV1()-1]- 1][s[(((pos[c.getV1()]+1)%n+n)%n)]-1] = iter;  //H[s[(((pos[clus[c.getV1()-1]]+1)%n+n)%n)]-1][clus[c.getV1()-1]- 1] = iter;
//                H[clus[c.getV2()-1]- 1][s[(((pos[c.getV2()]+1)%n+n)%n)]-1] = iter;  //H[s[(((pos[clus[c.getV2()-1]]+1)%n+n)%n)]-1][clus[c.getV2()-1]- 1] = iter;
            M = updateSetMopt(S, Pzero);
            iter++;

            if (C < Cb) {
                System.arraycopy(s, 0, Sb, 0, this.nbClus);
                System.out.println(Cb + " at perturb");
                Cb = C;
                W = 0;
                sol = sol.setAt0(Sb);
                sol = sol.setAt1(Cb);
                sol = sol.setAt2(getCpuTime() - cpu);
                sol = sol.setAt3(nBdesc);
//                nBdesc = nBdesc - (nBdesc / 2);
            }
//            }
        }
        S = S.setAt0(s);
        S = S.setAt1(C);
        return S;
    }

    private Pair<int[], Integer> insertPerturb(Pair<int[], Integer> S, int L, ArrayList<Couple> M, double Pzero) {
        Couple c;
        int C = S.getValue1(), delta, n = this.nbClus;
        int[] s = S.getValue0();
        for (int i = 0; i < L; i++) {
//            if(M.isEmpty()){
//                S = dbmPerturb(S, 1);
//            }
//            else{
            //            System.out.println("\t\t\tSaut N° "+(i+1)+"/"+L);
            //            System.out.println("\t\t\tListe des candidats: "+M.toString());
            c = M.get((int) (Math.random() * M.size()));
            //            System.out.println("\t\t\tCouple sélectionné: "+c);
            delta = deltaInsert(s, c.getV1(), c.getV2());
            s = insert(s, c.getV1(), c.getV2());
            C += delta;
            H[clus[c.getV1() - 1] - 1][clus[s[(((pos[clus[c.getV1() - 1]] + 1) % n + n) % n)] - 1] - 1] = iter;
            H[clus[c.getV1() - 1] - 1][clus[s[(((pos[clus[c.getV1() - 1]] - 1) % n + n) % n)] - 1] - 1] = iter;
//                H[clus[c.getV1()-1]- 1][s[(((pos[clus[c.getV1()-1]]+1)%n+n)%n)]-1] = iter;  //H[s[(((pos[clus[c.getV1()-1]]+1)%n+n)%n)]-1][clus[c.getV1()-1]- 1] = iter;
//                H[clus[c.getV1()-1]- 1][s[(((pos[clus[c.getV1()-1]]-1)%n+n)%n)]-1] = iter;  //H[s[(((pos[clus[c.getV1()-1]]-1)%n+n)%n)]-1][clus[c.getV1()-1]- 1] = iter;
//                H[clus[c.getV2()-1]- 1][s[(((pos[clus[c.getV2()-1]]+1)%n+n)%n)]-1] = iter;  //H[s[(((pos[clus[c.getV2()-1]]+1)%n+n)%n)]-1][clus[c.getV2()-1]- 1] = iter;
            H[clus[c.getV2() - 1] - 1][clus[s[(((pos[clus[c.getV2() - 1]] + 1) % n + n) % n)] - 1] - 1] = iter;
            M = updateSetMinsert(S, Pzero);
            iter++;
            if (C < Cb) {
                System.arraycopy(s, 0, Sb, 0, n);
                Cb = C;
                W = 0;
                sol = sol.setAt0(Sb);
                sol = sol.setAt1(Cb);
                sol = sol.setAt2(getCpuTime() - cpu);
                sol = sol.setAt3(nBdesc);
//                nBdesc = nBdesc - (nBdesc / 2);
            }
//            }
        }
        S = S.setAt0(s);
        S = S.setAt1(C);
        return S;
    }

    private ArrayList<Couple> updateSetMopt(Pair<int[], Integer> S, double Pzero) {
        ArrayList<Couple> M = new ArrayList<Couple>();
        final double P0 = Pzero, Q = 0.7;
        double Pb = Math.exp(-W / T);
        if (Pb <= P0) {
            Pb = P0;
        }
        final int n = this.nbClus, G = n * n / 10;
        double randP = Math.random();
        if (randP < Pb && G <= iter) {
            int i, j, d = n, C = S.getValue1(), min = C, Cinit = min;

//            for (i = 0; i <= d - 2; i++) {
//                for (j = i + 2; (i == 0 && j < d - 1) || (i > 0 && j < d); j++) {
            for (int k = 0; k <= d ; k++){
                i = (int) (Math.random() * (n-1));
                j = (int) (Math.random() * (n-3))+3;
                if(Math.abs(i-j)>2 && Math.abs(i-j)<n-2){
                    C -= delta2Opt(S.getValue0(), i, j);
                    if (C < Cinit && (H[clus[S.getValue0()[i] - 1] - 1][clus[S.getValue0()[(((j/*i+1*/) % n + n) % n)] - 1] - 1] + G < iter
                            /*|| H[clus[S.getValue0()[j]-1] - 1][S.getValue0()[(((j+1)%n+n)%n)] - 1] + G < iter*/ || C < Cb)) {
                        if (Cinit - C == min) {
                            M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                        }
                        if (Cinit - C < min) {
                            min = Cinit - C;
                            M.clear();
                            M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                        }
                    }
                    C += delta2Opt(S.getValue0(), i, j);
                }
            }
        }
        if (randP < (1 - Pb) * Q || M.isEmpty()) {
            int i, j, d = this.nbClus, min = H[0][0];

//            for (i = 0; i <= d - 2; i++) {
//                for (j = i + 2; (i == 0 && j < d - 1) || (i > 0 && j < d); j++) {
            for (int k = 0; k <= d ; k++){
                i = (int) (Math.random() * (n-1));
                j = (int) (Math.random() * (n-3))+3;
                if(Math.abs(i-j)>2 && Math.abs(i-j)<n-2){
                    if (H[clus[S.getValue0()[i] - 1] - 1][clus[S.getValue0()[j] - 1] - 1] < min) {
                        min = H[clus[S.getValue0()[i] - 1] - 1][clus[S.getValue0()[j] - 1] - 1];
                        M.clear();
                        M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                    } else {
                        M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                    }
                }
            }

        } else {
            int i, j, d = this.nbClus;
//            for (i = 0; i <= d - 2; i++) {
//                for (j = i + 2; (i == 0 && j < d - 1) || (i > 0 && j < d); j++) {
            for (int k = 0; k <= d ; k++){
                i = (int) (Math.random() * (n-1));
                j = (int) (Math.random() * (n-3))+3;
                if(Math.abs(i-j)>2 && Math.abs(i-j)<n-2){
                    M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                }
            }
        }
        return M;
    }
    
     private ArrayList<Couple> updateSetMswap(Pair<int[], Integer> S, double Pzero) {
        ArrayList<Couple> M = new ArrayList<>();
        final double P0 = Pzero, Q = 0.7;
        double Pb = Math.exp(-W / T);
        if (Pb <= P0) {
            Pb = P0;
        }
        final int n = this.nbClus, G = n * n / 10;
        double randP = Math.random();
        if (randP < Pb && G <= iter) {
            int i, j, d = n, C = S.getValue1(), min = C, Cinit = min;

//            for (i = 0; i <= d - 2; i++) {
//                for (j = i + 3; (i == 0 && j < d - 2) || (i > 0 && j < d - 1); j++) {
            for (int k = 0; k <= d ; k++){
                i = (int) (Math.random() * (n-1));
                j = (int) (Math.random() * (n-3))+3;
                if(Math.abs(i-j)>2 && Math.abs(i-j)<n-2){
                    C -= deltaSWAP(S.getValue0(), i, j);
                    if (C < Cinit && (H[clus[S.getValue0()[i] - 1] - 1][clus[S.getValue0()[(((j) % n + n) % n)] - 1] - 1] + G < iter
                            || /*H[clus[S.getValue0()[i]-1] - 1][S.getValue0()[(((i-1)%n+n)%n)] - 1] + G < iter || 
                             H[clus[S.getValue0()[j]-1] - 1][S.getValue0()[(((j+1)%n+n)%n)] - 1] + G < iter || 
                             H[clus[S.getValue0()[j]-1] - 1][S.getValue0()[(((j-1)%n+n)%n)] - 1] + G < iter ||*/ C < Cb)) {
                        if (Cinit - C == min) {
                            M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                        }
                        if (Cinit - C < min) {
                            min = Cinit - C;
                            M.clear();
                            M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                        }
                    }
                    C += deltaSWAP(S.getValue0(), i, j);
                }
            }

        }
        if (randP < (1 - Pb) * Q || M.isEmpty()) {
            int i, j, d = this.nbClus, min = H[0][0];

//            for (i = 0; i <= d - 2; i++) {
//                for (j = i + 3; (i == 0 && j < d - 2) || (i > 0 && j < d - 1); j++) {
            for (int k = 0; k <= d ; k++){
                i = (int) (Math.random() * (n-1));
                j = (int) (Math.random() * (n-3))+3;
                if(Math.abs(i-j)>2 && Math.abs(i-j)<n-2){
                    if (H[clus[S.getValue0()[i] - 1] - 1][clus[S.getValue0()[j] - 1] - 1] < min) {
                        min = H[clus[S.getValue0()[i] - 1] - 1][clus[S.getValue0()[j] - 1] - 1];
                        M.clear();
                        M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                    } else {
                        M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                    }
                }
            }

        } else {
            int i, j, d = this.nbClus;
//            for (i = 0; i <= d - 2; i++) {
//                for (j = i + 3; (i == 0 && j < d - 2) || (i > 0 && j < d - 1); j++) {
            for (int k = 0; k <= d ; k++){
                i = (int) (Math.random() * (n-1));
                j = (int) (Math.random() * (n-3))+3;
                if(Math.abs(i-j)>2 && Math.abs(i-j)<n-2){
                    M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                }
            }
        }
        return M;
    }

    private ArrayList<Couple> updateSetMinsert(Pair<int[], Integer> S, double Pzero) {
        ArrayList<Couple> M = new ArrayList<Couple>();
        final double P0 = Pzero, Q = 0.7;
        double Pb = Math.exp(-W / T);
        if (Pb <= P0) {
            Pb = P0;
        }
        final int n = this.nbClus, G = n * n / 10;
        double randP = Math.random();
        if (randP < Pb && G <= iter) {
            int i, j, d = n, C = S.getValue1(), min = C, Cinit = min;

            for (i = 1; i <= d - 2; i++) {
                for (j = i + 2; (i == 0 && j < d - 1) || (i > 0 && j < d); j++) {
                    C -= deltaInsert(S.getValue0(), i, j);
                    if (C < Cinit && (H[clus[S.getValue0()[i] - 1] - 1][S.getValue0()[(((j/*i+1*/) % n + n) % n)] - 1] + G < iter
                            || /*H[clus[S.getValue0()[i]-1] - 1][S.getValue0()[(((i-1)%n+n)%n)] - 1] + G < iter || 
                             H[clus[S.getValue0()[j]-1] - 1][S.getValue0()[(((j+1)%n+n)%n)] - 1] + G < iter ||*/ C < Cb)) {
                        if (Cinit - C == min) {
                            M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                        }
                        if (Cinit - C < min) {
                            min = Cinit - C;
                            M.clear();
                            M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                        }
                    }
                    C += deltaInsert(S.getValue0(), i, j);
                }
            }

        }
        if (randP < (1 - Pb) * Q || M.isEmpty()) {
            int i, j, d = this.nbClus, min = H[0][0];

            for (i = 0; i <= d - 2; i++) {
                for (j = i + 2; (i == 0 && j < d - 1) || (i > 0 && j < d); j++) {
                    if (H[clus[S.getValue0()[i] - 1] - 1][clus[S.getValue0()[j] - 1] - 1] < min) {
                        min = H[clus[S.getValue0()[i] - 1] - 1][clus[S.getValue0()[j] - 1] - 1];
                        M.clear();
                        M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                    } else {
                        M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                    }
                }
            }

        } else {
            int i, j, d = this.nbClus;
            for (i = 0; i <= d - 2; i++) {
                for (j = i + 2; (i == 0 && j < d - 1) || (i > 0 && j < d); j++) {
                    M.add(new Couple(clus[S.getValue0()[i] - 1], clus[S.getValue0()[j] - 1]));
                }
            }
        }
        return M;
    }

    public Pair<int[], Integer> doubleBridgeMove(Pair<int[], Integer> S, int range, int radius, boolean dlbFlag) {
        Pair<int[], Integer> solDBM;
        range = Math.abs(range);
        int i, j, g, h, x, y, n = this.nbClus;
        ArrayList<Integer> posT = new ArrayList<>();
        int[] db = new int[n];
        solDBM = new Pair<>(db, 0);
        i = (int) (Math.random() * n);
        if (range == 0) {
            j = i;
            while (j > i - 2 && j < i + 2) {
                j = (int) (Math.random() * n);
            }
            g = j;
            while ((g > i - 2 && g < i + 2) || (g > j - 2 && g < j + 2)) {
                g = (int) (Math.random() * n);
            }
            h = g;
            while ((h > i - 2 && h < i + 2) || (h > j - 2 && h < j + 2) || (h > g - 2 && h < g + 2)) {
                h = (int) (Math.random() * n);
            }
        } else {
            if (range > n) {
                range = n;
            }
            j = i;
            while (j > i - 2 && j < i + 2) {
                j = (int) (Math.random() * range + i) % n;
            }
            g = j;
            while ((g > i - 2 && g < i + 2) || (g > j - 2 && g < j + 2)) {
                g = (int) (Math.random() * range + i) % n;
            }
            h = g;
            while ((h > i - 2 && h < i + 2) || (h > j - 2 && h < j + 2) || (h > g - 2 && h < g + 2)) {
                h = (int) (Math.random() * range + i) % n;
            }
        }

        posT.add(i);
        posT.add(j);
        posT.add(g);
        posT.add(h);
        Collections.sort(posT);
        if (dlbFlag) {
            i = (((posT.get(0) - radius + 1) % n) + n) % n;
            j = (((posT.get(1) - radius + 1) % n) + n) % n;
            g = (((posT.get(2) - radius + 1) % n) + n) % n;
            h = (((posT.get(3) - radius + 1) % n) + n) % n;
            for (x = 0; x < 2 * radius; x++) {
                i++;
                j++;
                g++;
                h++;
            }
        }
        i = posT.get(0);
        j = posT.get(1);
        g = posT.get(2);
        h = posT.get(3);
        x = 0;
        y = 0;
        int k = 0;
        while (y <= i) {
            pos[clus[S.getValue0()[y] - 1]] = k;
            db[k++] = S.getValue0()[y];
            x++;
            y++;
        }
        y = g + 1;
        while (y <= h) {
            pos[clus[S.getValue0()[y] - 1]] = k;
            db[k++] = S.getValue0()[y];
            x++;
            y++;
        }
        y = j + 1;
        while (y <= g) {
            pos[clus[S.getValue0()[y] - 1]] = k;
            db[k++] = S.getValue0()[y];
            x++;
            y++;
        }
        y = i + 1;
        while (y <= j) {
            pos[clus[S.getValue0()[y] - 1]] = k;
            db[k++] = S.getValue0()[y];
            x++;
            y++;
        }
        y = h + 1;
        while (y < n) {
            pos[clus[S.getValue0()[y] - 1]] = k;
            db[k++] = S.getValue0()[y];
            x++;
            y++;
        }
        /*
         Calclul du delta DBM
         */
        int deltaDBM = -this.MatDis[S.getValue0()[(i)] - 1][S.getValue0()[(i + 1)] - 1]
                - this.MatDis[S.getValue0()[j] - 1][S.getValue0()[(j + 1)] - 1]
                - this.MatDis[S.getValue0()[g] - 1][S.getValue0()[(g + 1)] - 1]
                - this.MatDis[S.getValue0()[h] - 1][S.getValue0()[((((h + 1) % n) + n) % n)] - 1]
                + this.MatDis[S.getValue0()[i] - 1][S.getValue0()[(g + 1)] - 1]
                + this.MatDis[S.getValue0()[h] - 1][S.getValue0()[(j + 1)] - 1]
                + this.MatDis[S.getValue0()[g] - 1][S.getValue0()[(i + 1)] - 1]
                + this.MatDis[S.getValue0()[j] - 1][S.getValue0()[((((h + 1) % n) + n) % n)] - 1];
        solDBM = solDBM.setAt0(db);
        solDBM = solDBM.setAt1(S.getValue1() + deltaDBM);
        return solDBM;
    }

    private int getMinMat(int[][] H) {
        int min = 1;
        for (int[] H1 : H) {
            for (int H2 : H1) {
                if (H2 < min) {
                    min = H2;
                }
            }
        }
        return min;
    }

    public static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported()
                ? bean.getCurrentThreadCpuTime() : 0L;
    }

    public void printMat(int[][] M) {
        for (int[] M1 : M) {
            for (int M2 : M1) {
                System.out.print("\t" + M2);
            }
            System.out.println();
        }
    }

    /**
     * get delaunay candidates
     *
     * @return
     */
    public ArrayList<Integer>[] delaunay() throws InterruptedException {

        try {
            // write coordinates
            String fileNameCoord = "./delau/" + this.name + ".coord";
            File fileCoord = new File(fileNameCoord);
            PrintWriter coordWriter = new PrintWriter(fileCoord);
            coordWriter.println(this.nbClus);
            for (Ville V : this.villes) {
                coordWriter.println(V.getX() + " " + V.getY());
            }
            coordWriter.close();

            // execute delaunay program
            String[] cmd = {"delau/dct.sh"/*,"eil51.tsp.coord"*/, fileNameCoord};
            //String[] cmd = {"ls", "./delau/"};
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(cmd);

            // read output from Delaunay program
            BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));

      // store candidates in a hash table
            // and instantiate all sub lists
            ArrayList<Integer>[] arcTable = new ArrayList[dim];
            for (int i = 0; i < dim; i++) {
                arcTable[i] = new ArrayList();
            }

            int node1, node2;
            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                String[] parts = line.split("\\s+");

                node1 = Integer.parseInt(parts[0]);
                node2 = Integer.parseInt(parts[1]);

                arcTable[node1].add(node2);
                arcTable[node2].add(node1);
            }
            int exitVal = proc.waitFor();
            //System.out.println("Exited with error code "+exitVal);
            br.close();

      // delete coordinates file
//      fileCoord.delete();
            return arcTable;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
