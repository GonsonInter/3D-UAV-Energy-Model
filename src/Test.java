import java.util.ArrayList;

public class Test {

    //在这里初始化地图和障碍物
//    public static Map map = new Map(10, 8, 3).setObstacle(0,0,2,2)
//                                        .setObstacle(5, 6, 1, 2);

    public static Map map = new Map(4, 4, 3);


    public static WidgetGraph graph = new WidgetGraph(map);
    public static int vertNum = graph.vertNum;
    public static ArrayList<Widget> widgets = graph.widgets;
    public static int vertsInWidget = graph.vertsInWidget;
    public static int widgetNum = graph.widgetNum;
    public static int[] vets = graph.vertsList;
    public static int[][] mat = graph.adjMat;
    //public static ArrayList<ArrayList<Integer>[]> pathMat = new ArrayList<>();



    public static void main(String args[]) {

        //把矩阵转换成完全图
        for(int vert=0; vert<vertNum; vert++) {

            Dijkstra dj = new Dijkstra(vets, mat);
            dj.start(vert);
            mat[vert] = dj.dist;        //dijkstra的结果赋给dist矩阵

            //pathMat.add(dj.path);

        }


        ToFile.writeMapToFile("map.txt", map);

/********************************************************此处设置ap覆盖范围***************************************************************/

//        WidgetGraph.setApCoverage(widgets,0,0,0,0);
//        WidgetGraph.setApCoverage(widgets,0,0,0,1);
//        WidgetGraph.setApCoverage(widgets,1,2,1,0);
//        WidgetGraph.setApCoverage(widgets,1,2,1,1);
//        WidgetGraph.setApCoverage(widgets,1,2,2,1);
//        WidgetGraph.setApCoverage(widgets,0,0,2,1);


        //1
        WidgetGraph.setApCoverage(widgets,1,3,1,0);
        WidgetGraph.setApCoverage(widgets,2,3,1,0);
        WidgetGraph.setApCoverage(widgets,3,2,1,0);
        WidgetGraph.setApCoverage(widgets,3,3,1,0);
        for(int i=0; i<4; i++)
            for (int j=0; j<4; j++)
                if((i==0&&j==0) || (i==1&&j==0) || (i==0&&j==1))
                    continue;
                else
                    WidgetGraph.setApCoverage(widgets, i, j, 2, 0);


        //2
//        WidgetGraph.setApCoverage(widgets,0,0,0,1);
//        WidgetGraph.setApCoverage(widgets,0,0,1,1);
//        WidgetGraph.setApCoverage(widgets,0,0,2,1);
//        WidgetGraph.setApCoverage(widgets,1,0,0,1);
//        WidgetGraph.setApCoverage(widgets,2,0,0,1);
//        WidgetGraph.setApCoverage(widgets,0,1,0,1);
//        WidgetGraph.setApCoverage(widgets,0,2,0,1);
//        WidgetGraph.setApCoverage(widgets,1,1,0,1);
//        WidgetGraph.setApCoverage(widgets,1,0,1,1);
//        WidgetGraph.setApCoverage(widgets,0,1,1,1);


        //3
//        WidgetGraph.setApCoverage(widgets,0,0,0,2);
//        WidgetGraph.setApCoverage(widgets,1,0,0,2);
//        WidgetGraph.setApCoverage(widgets,2,0,0,2);
//        WidgetGraph.setApCoverage(widgets,3,0,0,2);
//        WidgetGraph.setApCoverage(widgets,0,0,1,2);
//        WidgetGraph.setApCoverage(widgets,0,0,2,2);
//        WidgetGraph.setApCoverage(widgets,3,3,0,2);
//        WidgetGraph.setApCoverage(widgets,3,3,1,2);
//        WidgetGraph.setApCoverage(widgets,3,3,2,2);


/****************************************************************************************************************************************/

        //此处需要区去除空心节点
        int[] newVets = VertsMapping.vertsMapping(widgets, vets, vertsInWidget);
        mat = VertsMapping.matProcess(mat, widgets, vets, vertsInWidget);
        vertNum = mat.length;
        widgetNum = vertNum / vertsInWidget;


        //将生成的完全图写入文件
        String filename = "1.gtsp";
        ToFile.writeGraph(filename, vertNum, widgetNum, mat);


    }



}
