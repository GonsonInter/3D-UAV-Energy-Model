import java.util.ArrayList;
import java.util.HashMap;

public class genWidgetPath {

    public static Map map = Test.map;

    public static WidgetGraph graph = Test.graph;
    public static int vertnum = Test.vertNum;
    public static int widgetNum = Test.widgetNum;
    public static int vertsInWidget = Test.vertsInWidget;
    public static int[] vets = Test.vets;      //顶点列表
    public static int[][] mat = Test.mat;
    public static ArrayList<Widget> widgets = Test.widgets;        //获取图中的widget列表
    public static int[] vertMapping;       //顶点映射，vertMapping[i]=k 表示GTSP结果i对应原来图中的顶点k
    public static ArrayList<ArrayList<Integer>[]> pathMat = new ArrayList<>();
    public static String pathFilename = "C:\\Users\\王子杰\\Desktop\\experiment\\tour.txt";                //解析文件路径
    public static int cost;

    public static Dijkstra dijkstra = new Dijkstra(vets, mat);


    public static void main(String args[]) {



/********************************************************此处设置ap覆盖范围,直接从Test.java复制***********************************************/

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


/***************************************************************************************************************************************/

        //顶点映射，vertMapping[i]=k 表示GTSP结果i对应原来图中的顶点k
        vertMapping = VertsMapping.vertsMapping(widgets, vets, vertsInWidget);

        ArrayList<Widget> path = calWidgetPath();       //获取路径序列

        //把Map信息、无人机的路径信息写入文件
        ToFile.writeMapToFile("map.txt", Test.map);
        ToFile.writePathToFile("path.txt", path);

        System.out.println("Widget路径总长度：" + path.size());
        //System.out.println("GLNS得出的能耗：" + cost);



        //使用通用方法计算能量消耗
        CalculateCost calculater = new CalculateCost();
        System.out.println("该条路径消耗的能量：" + calculater.getCost(path));
        System.out.println("AP切换的次数：" + calculater.ap_switch_num);

    }


    //获取具体的vert路径,即dijikstra生成的最短路径
    public static void initVertPath() {

        //把矩阵转换成完全图
        for(int vert=0; vert<vertnum; vert++) {

            Dijkstra dj = new Dijkstra(vets, mat);
            dj.start(vert);

            pathMat.add(dj.path);       //具体的vert路径

        }

    }

    //获取widget路径序列
    public static ArrayList<Widget> calWidgetPath() {

        //initVertPath();             //先得出具体的dijkstra路径矩阵


        ResolvePath rp = new ResolvePath(pathFilename);

        cost = rp.cost;     //获取路径的代价

        int[] abstractPath = rp.path;       //获取GLNS求出的顶点路径

        //System.out.println(abstractPath.length);

        for (int i = 0; i < abstractPath.length; i++)
            abstractPath[i] = vertMapping[abstractPath[i]];             //映射到真实的顶点


//        for (int vert = 0; vert < abstractPath.length; vert++)
//            System.out.print(abstractPath[vert] + " ");
//        System.out.println();

        ArrayList<Integer> concretePath = new ArrayList<>();
        ArrayList<Integer> widgetNumPath = new ArrayList<>();      //widget编号序列
        ArrayList<Widget> widgetPath = new ArrayList<>();      //最终返回的Widget序列

        for (int i = 0 ; i < abstractPath.length - 1 ; i++) {

            //ArrayList<Integer> tempPath = pathMat.get(abstractPath[i])[abstractPath[i+1]];

            dijkstra.start(abstractPath[i]);
            ArrayList<Integer> tempPath = dijkstra.path[abstractPath[i+1]];     //获取两个点之间的最短路径

            for (int j = 0; j < tempPath.size(); j++) {
                concretePath.add(tempPath.get(j));
            }

        }

        //计算widgetNum序列,widget序号序列
        widgetNumPath.add(concretePath.get(0) / vertsInWidget);
        for(int i = 1; i < concretePath.size(); i++) {
            int currentWidgetNum = concretePath.get(i) / vertsInWidget;       // 节点编号 / 6  就是widget编号

            //删除连续的相邻的重复widget编号
            if (currentWidgetNum != widgetNumPath.get(widgetNumPath.size()-1)) {
                widgetNumPath.add(currentWidgetNum);
            }
        }

        //计算widget序列
        for (int i = 0; i < widgetNumPath.size(); i++) {

            Widget widget = widgets.get(widgetNumPath.get(i));
            widgetPath.add(widget);
            //System.out.println(widget.loc_x + "," + widget.loc_y + "," + widget.loc_z + " : " + widget.ap);

        }

        return widgetPath;


    }



}
