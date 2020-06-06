import java.util.ArrayList;

//public class mmm {
//
//    public static void main(String[] args) {
//
//        int[] vexs = {0, 1, 2, 3, 4, 5, 6};
//        final int INF = Integer.MAX_VALUE;
//        int matrix[][] = {
//                        /*0*//*1*//*2*//*3*//*4*//*5*//*6*/
//                /*0*/ {   0,  12, INF, INF, INF,  16,  14},
//                /*1*/ {  12,   0,  10, INF, INF,   7, INF},
//                /*2*/ { INF,  10,   0,   3,   5,   6, INF},
//                /*3*/ { INF, INF,   3,   0,   4, INF, INF},
//                /*4*/ { INF, INF,   5,   4,   0,   2,   8},
//                /*5*/ {  16,   7,   6, INF,   2,   0,   9},
//                /*6*/ {  14, INF, INF, INF,   8,   9,   0}};
//        Dijkstra dj = new Dijkstra(vexs, matrix);
//        dj.start(3);
//
//    }
//}

public class Dijkstra {
    final int INF = Integer.MAX_VALUE;         //最大数
    int numNodes;
    int[][] matrix;
    int[] prev ;
    int[] dist ;
    int[] vexs;
    ArrayList<Integer>[] path;    //路径数组

    //初始化图参数
    public  Dijkstra (int[] vexs, int[][] matrix) {
        this.vexs = vexs;
        this.matrix = matrix;
        numNodes = vexs.length;
        prev = new int[numNodes];
        dist = new int[numNodes];
        path = new ArrayList[numNodes];
    }


    // 包括三个数组 1.prev（包含当前节点的上一个父类节点） 2.dist（当前节点与原始节点的距离）
    //			3.原始矩阵matrix[][],储存图           4.isVisited[]标记数组
    public void start(int vs) {

        //System.out.println("======================================出发：节点" + vs + "======================================");

        //初始化类参数
        boolean[] isVisited = new boolean[numNodes ];
        for (int i = 0; i < isVisited.length; i++) {
            dist[i] = matrix[vs][i];
            prev[i] = -1;
            if(dist[i] != INF) {
                prev[i] = vs;
            }
        }
        isVisited[vs] = true;
        //两次循环
        for (int i = 0; i < isVisited.length; i++) {
            int min = INF;
            int k = 0;
            //找到最近的节点
            for (int j = 0; j < isVisited.length; j++) {
                if(!isVisited[j] && dist[j] < min ) {
                    min = dist[j];
                    k = j;
                }
            }

            isVisited[k] = true;
            //更新最近路径和父节点
            for (int j = 0; j < isVisited.length; j++) {

                if(!isVisited[j] && matrix[k][j] != INF) {
                    if(dist[j] > matrix[k][j] + dist[k]) {
                        dist[j] =  matrix[k][j] + dist[k] ;
                        prev[j] = k;
                    }

                }
            }
        }


        //存储节点、路径、距离
        for (int i = 0; i < isVisited.length; i++) {

            path[i] = new ArrayList<Integer>();

            //System.out.print( "节点" + i + "  " );
            int a = i;
            //System.out.print("路径：");


            path[i].add(a);
            while (a != vs) {
                //System.out.print(   prev[a] + "  " );
                a = prev[a];

                path[i].add(a);     //添加路径
            }

            path[i] = reverse(path[i]);
//            System.out.print(vs + "->" + i + ", 距离" + dist[i] + "  路径:");
//            for(int k=0; k<path[i].size(); k++)
//                System.out.print(   path[i].get(k) + "  " );
//            System.out.println();



        }

    }

    public ArrayList<Integer> reverse(ArrayList<Integer> arr)
    {

        for(int i=0;i<(arr.size())/2;i++)
        {	//把数组的值赋给一个临时变量
            int temp=arr.get(i);
            //把后面的值赋给前面的
            arr.set(i, arr.get(arr.size()-i-1));
            //把获取的临时变量赋给后面的
            arr.set(arr.size()-i-1, temp);
        }
        return arr;

    }

    //获取最短路径
    public int[] getMinDists() {
        return dist;
    }

    public ArrayList<Integer>[] getPath() {
        return path;
    }

}
