public class Widget {

    public int loc_x, loc_y, loc_z;        //坐标

    public int ap;       //当前连接的ap

    public boolean isApCovered;      //当前ap是否覆盖

    public int[] vertices = new int[6];        //表示六个方向,012345分别表示

    Widget(int x, int y, int z){
        loc_x = x;
        loc_y = y;
        loc_z = z;      //初始化
        ap = 0;         //默认连接第一个ap
        isApCovered = true;
    }

    Widget(int x, int y, int z, int ap){
        loc_x = x;
        loc_y = y;
        loc_z = z;      //初始化
        this.ap = ap;
        isApCovered = true;
    }

    public PosRelation getRelation(Widget nextWg) {

        if(this.loc_x == nextWg.loc_x-1 && this.loc_y == nextWg.loc_y && this.loc_z == nextWg.loc_z && this.ap == nextWg.ap)
            return PosRelation.X_POSITIVE;
        if(this.loc_x == nextWg.loc_x+1 && this.loc_y == nextWg.loc_y && this.loc_z == nextWg.loc_z && this.ap == nextWg.ap)
            return PosRelation.X_NEGATIVE;
        if(this.loc_x == nextWg.loc_x && this.loc_y == nextWg.loc_y-1 && this.loc_z == nextWg.loc_z && this.ap == nextWg.ap)
            return PosRelation.Y_POSITIVE;
        if(this.loc_x == nextWg.loc_x && this.loc_y == nextWg.loc_y+1 && this.loc_z == nextWg.loc_z && this.ap == nextWg.ap)
            return PosRelation.Y_NEGATIVE;
        if(this.loc_x == nextWg.loc_x && this.loc_y == nextWg.loc_y && this.loc_z == nextWg.loc_z-1 && this.ap == nextWg.ap)
            return PosRelation.Z_POSITIVE;
        if(this.loc_x == nextWg.loc_x && this.loc_y == nextWg.loc_y && this.loc_z == nextWg.loc_z+1 && this.ap == nextWg.ap)
            return PosRelation.Z_NEGATIVE;
        if(this.loc_x == nextWg.loc_x && this.loc_y == nextWg.loc_y && this.loc_z == nextWg.loc_z && this.ap != nextWg.ap)
            return PosRelation.DIFF_AP;

        return PosRelation.NOT_NEIGHBOR;

    }

    public enum PosRelation {

        X_POSITIVE,
        X_NEGATIVE,
        Y_POSITIVE,
        Y_NEGATIVE,
        Z_POSITIVE,
        Z_NEGATIVE,
        DIFF_AP,
        NOT_NEIGHBOR

    }

    public String toString() {
        return loc_x + "," + loc_y + "," + loc_z + "," + ap ;
    }


}
