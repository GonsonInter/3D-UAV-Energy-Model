public class Map {

    public int X_COR ;       //坐标范围
    public int Y_COR ;
    public int Z_COR ;

    public int[][][] pos;          //三维坐标,0表示有障碍物,1表示没有障碍物

    Map(){

        X_COR = 5;       //坐标范围
        Y_COR = 5;
        Z_COR = 3;
        pos = new int[X_COR][Y_COR][Z_COR];
        init();
    }

    Map(int X_COR, int Y_COR, int Z_COR){

        this.X_COR = X_COR;
        this.Y_COR = Y_COR;
        this.Z_COR = Z_COR;

        pos = new int[X_COR][Y_COR][Z_COR];
        init();

    }


    //初始化地图
    public void init() {
        for(int i=0; i < X_COR; i++)
            for(int j=0; j < Y_COR; j++)
                for(int k=0; k < Z_COR; k++)
                    pos[i][j][k] = 1;         //表示可以有无人机飞到


    }

    //设置障碍物，参数x, y, x跨度, y跨度
    public Map setObstacle(int x, int y, int x_span, int y_span) {
        for (int i = x; i < x+x_span; i++)
            for (int j = y; j < y+y_span; j++)
                for (int k = 0; k < Z_COR; k++)
                    pos[i][j][k] = 0;        //表示无人机不可飞到

        return this;

    }

    //获取map中有多少个顶点
    public int getPointNum() {

        int num = 0;
        for(int i=0; i < X_COR; i++)
            for(int j=0; j < Y_COR; j++)
                for(int k=0; k < Z_COR; k++)
                    if (pos[i][j][k] == 1)
                        num++;

        return num;
    }

}
