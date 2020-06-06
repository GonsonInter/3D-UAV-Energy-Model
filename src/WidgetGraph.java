import java.util.ArrayList;

public class WidgetGraph {

    public int widgetNum = 0;
    public int vertNum = 0;
    public int[] apList = {0};      //ap列表
    public int apNum = apList.length;       //ap个数
    public ArrayList<Widget> widgets = new ArrayList<Widget>();     //widget列表
    public int[] vertsList;                                    //顶点列表
    public int[][] adjMat;

    public int vertsInWidget = 6;       //一个widget有多少vertex

    public WidgetGraph(Map map) {
        initGraph(map);
    }

    //初始化
    public void initGraph(Map map) {

        //初始化widget
        for(int a=0; a<apList.length; a++)
            for(int i=0; i<map.Z_COR; i++)
                for(int j=0; j<map.Y_COR; j++)
                    for(int k=0; k<map.X_COR; k++)
                        if(map.pos[k][j][i] == 1)
                            widgets.add(new Widget(k, j, i, a));

        widgetNum = widgets.size();

        //确定顶点数量
        vertNum = widgetNum * vertsInWidget;

        //初始化顶点列表
        vertsList = new int[vertNum];
        for(int i=0; i<vertNum; i++)
            vertsList[i] = i;

        adjMat = new int[vertNum][vertNum];

        //初始化邻接矩阵
        for(int i=0; i<vertNum; i++){

            for(int j=0; j<vertNum; j++)
                adjMat[i][j] = EMax;        //首先全部设置不可达

            adjMat[i][i] = 0;       //对角线清零
        }


        //设置边
        for(int i=0; i<widgetNum; i++) {

            //widget之内的边
            adjMat[i*vertsInWidget][i*vertsInWidget + 1] = adjMat[i*vertsInWidget + 1][i*vertsInWidget] =
                    adjMat[i*vertsInWidget + 2][i*vertsInWidget + 3] = adjMat[i*vertsInWidget + 3][i*vertsInWidget + 2] = Et;
            adjMat[i*vertsInWidget][i*vertsInWidget + 2] = adjMat[i*vertsInWidget + 2][i*vertsInWidget] =
                    adjMat[i*vertsInWidget][i*vertsInWidget + 3] = adjMat[i*vertsInWidget + 3][i*vertsInWidget] =
            adjMat[i*vertsInWidget + 1][i*vertsInWidget + 2] = adjMat[i*vertsInWidget + 2][i*vertsInWidget + 1] =
                    adjMat[i*vertsInWidget + 1][i*vertsInWidget + 3] = adjMat[i*vertsInWidget + 3][i*vertsInWidget + 1] = Et;
            adjMat[i*vertsInWidget + 4][i*vertsInWidget + 5] = Ea_d;      //从上升状态转换为下降状态的耗能
            adjMat[i*vertsInWidget + 5][i*vertsInWidget + 4] = Ed_a;      //从下降状态转换为上升状态的耗能
            for(int k=0; k<4; k++) {
                adjMat[i*vertsInWidget + 4][i*vertsInWidget + k] = Et/2;
                adjMat[i*vertsInWidget + k][i*vertsInWidget + 4] = Et/2;
                adjMat[i*vertsInWidget + 5][i*vertsInWidget + k] = Et/2;
                adjMat[i*vertsInWidget + k][i*vertsInWidget + 5] = Et/2;
            }


            //widget之间的边
            for(int j=0; j<widgetNum; j++){

                if(i==j)    continue;

                Widget w1 = widgets.get(i);
                Widget w2 = widgets.get(j);

                Widget.PosRelation posRelation = w1.getRelation(w2);        //判断w1，w2之间的关系

                switch(posRelation) {

                    case X_POSITIVE:
                        adjMat[i*vertsInWidget][j*vertsInWidget] = Em;
                        break;
                    case X_NEGATIVE:
                        adjMat[i*vertsInWidget + 1][j*vertsInWidget + 1] = Em;
                        break;
                    case Y_POSITIVE:
                        adjMat[i*vertsInWidget + 2][j*vertsInWidget + 2] = Em;
                        break;
                    case Y_NEGATIVE:
                        adjMat[i*vertsInWidget + 3][j*vertsInWidget + 3] = Em;
                        break;
                    case Z_POSITIVE:
                        adjMat[i*vertsInWidget + 4][j*vertsInWidget + 4] = Ea;
                        break;
                    case Z_NEGATIVE:
                        adjMat[i*vertsInWidget + 5][j*vertsInWidget + 5] = Ed;
                        break;
                    case DIFF_AP:
                        for(int k=0; k<vertsInWidget; k++)
                            adjMat[i*vertsInWidget+k][j*vertsInWidget+k] = Es;

                }

            }
        }

    }

    //根据坐标和ap，设置ap的覆盖范围
    public static void setApCoverage(ArrayList<Widget> widgets, int x, int y, int z, int ap) {

        Widget current = new Widget(x, y, z, ap);
        //先判断在widget列表中的位置
        int index = 0;
        while (index < widgets.size())  {
            if (isEqual(current, widgets.get(index)))
                break;
            index++;
        }

        if (index < widgets.size())
            widgets.get(index).isApCovered = false;

    }

    public static boolean isEqual(Widget w1, Widget w2) {
        if (w1.loc_x != w2.loc_x) return false;
        if (w1.loc_y != w2.loc_y) return false;
        if (w1.loc_z != w2.loc_z) return false;
        if (w1.ap != w2.ap) return false;
        return true;
    }


    //无人机的能耗
    public static final int Em = 100;      //直行     100
    public static final int Et = 0;      //90度     150
    public static final int Eu = Et;      //180度
    public static final int Ea = 100;      //爬升     300
    public static final int Ed = 100;      //下降     100
    public static final int Ea_d = 0;      //爬升转化为下降      50
    public static final int Ed_a = 0;      //下降转化为爬升      100
    public static final int Es = 500;      //切换AP      500
    public static final int EMax = Integer.MAX_VALUE;

}
