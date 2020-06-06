import java.util.ArrayList;
import java.util.HashMap;


//映射类
public class VertsMapping {

    public static int newVertsNum;

    //清除空心点,并且对新的生成和原来的生成做一个映射,
    //参数是原来的widget列表和原来的顶点数组，输出是映射后的列表
    public static int[] vertsMapping(ArrayList<Widget> widgets, int[] vertsList, int vertsInWidget) {

        HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();

        int[] newVertsList;

        int index = 0;      //处理后的位置

        for(int i = 0; i < vertsList.length; i++) {

            //如果AP覆盖，则为实心点
            if(widgets.get(vertsList[i] / vertsInWidget).isApCovered) {

                mapping.put(index++, vertsList[i]);     //处理完空心点后的映射

            }

        }

//        for (int i = 0; i < mapping.size(); i++) {
//            System.out.println(i + "  " + mapping.get(i));
//        }

        //处理后的图的顶点数量
        newVertsNum = mapping.size();
        newVertsList = new int[newVertsNum];

        for (int i = 0; i < newVertsNum; i++)
            newVertsList[i] = mapping.get(i);


        return newVertsList;

    }

    //清除空心点之后的邻接矩阵
    //参数为原来的邻接矩阵，widget列表，顶点列表，每个widget中vertex的数量
    //在调用该函数之前，必须要先调用vertsMapping函数，应为需要它的运算结果newVertsNum
    public static int[][] matProcess(int[][] adjMat, ArrayList<Widget> widgets, int[] vertsList, int vertsInWidget) {

        int[][] newMat = new int[newVertsNum][newVertsNum];

        int line = 0;       //行数控制

        for (int i = 0; i < adjMat.length; i++) {

            if (widgets.get(vertsList[i] / vertsInWidget).isApCovered) {

                int column = 0;

                for (int j = 0; j < adjMat.length; j++) {

                    if (widgets.get(vertsList[j] / vertsInWidget).isApCovered)
                        newMat[line][column++] = adjMat[i][j];

                }
                line++;
            }

        }


        return newMat;

    }


//    public static void main(String args[]) {
//
//        int[][] mat = Test.mat;
//        ArrayList<Widget> widgets = Test.widgets;
//        int[] vets = Test.vets;
//        int vertsInWidget = Test.vertsInWidget;
//        int vertnum = Test.vertnum;
//        WidgetGraph graph = Test.graph;
//
//        //把矩阵转换成完全图
//        for(int vert=0; vert<vertnum; vert++) {
//
//            Dijkstra dj = new Dijkstra(vets, mat);
//            dj.start(vert);
//            mat[vert] = dj.dist;        //dijkstra的结果赋给dist矩阵
//        }
//
//        //设置ap覆盖范围
////        widgets.get(0).isApCovered = false;
////        widgets.get(1).isApCovered = false;
////        widgets.get(90).isApCovered = false;
//        Test.setApCoverage(widgets,0,0,0,0);
//        Test.setApCoverage(widgets,1,2,1,0);
//        Test.setApCoverage(widgets,1,2,2,1);
//        Test.setApCoverage(widgets,0,0,2,1);
//
//
//
//        int[] newVerts = vertsMapping(widgets, vets, vertsInWidget);
//
//        int[][] newMat = matProcess(mat, widgets, vets, vertsInWidget);
//
//        for (int i=0; i < newMat.length; i++) {
//            for (int j = 0; j < newMat.length; j++)
//                System.out.print(String.format("%-5d", newMat[i][j]));
//            System.out.println();
//
//        }
//
//        System.out.println(newMat.length);
//        System.out.println();
//
//
//
//
//}


}
