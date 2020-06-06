import static java.lang.Math.abs;

public class LocPoint {

    private int loc_x, loc_y, loc_z;

    private int ap;       //当前连接的ap

    LocPoint(int x, int y, int z){
        loc_x = x;
        loc_y = y;
        loc_z = z;      //初始化
        ap = 0;         //默认连接第一个ap
    }

    LocPoint(int x, int y, int z, int ap){
        loc_x = x;
        loc_y = y;
        loc_z = z;      //初始化
        this.ap = ap;
    }

    public int getLoc_x() {
        return loc_x;
    }

    public int getLoc_y() {
        return loc_y;
    }

    public int getLoc_z() {
        return loc_z;
    }

    public int getAp() { return ap; }

    public void setLoc_x(int loc_x) {
        this.loc_x = loc_x;
    }

    public void setLoc_y(int loc_y) {
        this.loc_y = loc_y;
    }

    public void setLoc_z(int loc_z) {
        this.loc_z = loc_z;
    }

    public void setAp(int ap) { this.ap = ap; }

    //判断两个点是否是邻居
    public boolean isNeighbor(LocPoint next) {

        if(abs(next.getLoc_x() - this.getLoc_x()) == 1 &&
            next.getLoc_y() == this.getLoc_y() &&
            next.getLoc_z() == this.getLoc_z())     return true;
        if(abs(next.getLoc_y() - this.getLoc_y()) == 1 &&
            next.getLoc_x() == this.getLoc_x() &&
            next.getLoc_z() == this.getLoc_z())     return true;
        if(abs(next.getLoc_z() - this.getLoc_z()) == 1 &&
            next.getLoc_x() == this.getLoc_y() &&
            next.getLoc_y() == this.getLoc_y())     return true;

        return false;
    }

    //判断运动类型，参数：当前运动方向，下一个目标点
    public MoveType getMoveType(MoveDirection  currentDirection, LocPoint nextLp, int nextAp) {

        if(nextAp != this.ap)       return MoveType.SWITCH;

        switch (currentDirection) {

            case X_POSITIVE:
                if(nextLp.loc_x - this.loc_x == 1)  return MoveType.STRAIGHT;
                if(nextLp.loc_x - this.loc_x == -1)  return MoveType.BACK_TURN;
                if(nextLp.loc_y - this.loc_y == 1)  return MoveType.LEFT_TURN;
                if(nextLp.loc_y - this.loc_y == -1)  return MoveType.RIGHT_TURN;
                if(nextLp.loc_z - this.loc_z == 1)  return MoveType.ASCEND;
                if(nextLp.loc_z - this.loc_z == -1)  return MoveType.DESCEND;
                break;
            case X_NEGATIVE:
                if(nextLp.loc_x - this.loc_x == -1)  return MoveType.STRAIGHT;
                if(nextLp.loc_x - this.loc_x == 1)  return MoveType.BACK_TURN;
                if(nextLp.loc_y - this.loc_y == -1)  return MoveType.LEFT_TURN;
                if(nextLp.loc_y - this.loc_y == 1)  return MoveType.RIGHT_TURN;
                if(nextLp.loc_z - this.loc_z == 1)  return MoveType.ASCEND;
                if(nextLp.loc_z - this.loc_z == -1)  return MoveType.DESCEND;
                break;
            case Y_POSITIVE:
                if(nextLp.loc_x - this.loc_x == 1)  return MoveType.RIGHT_TURN;
                if(nextLp.loc_x - this.loc_x == -1)  return MoveType.LEFT_TURN;
                if(nextLp.loc_y - this.loc_y == 1)  return MoveType.STRAIGHT;
                if(nextLp.loc_y - this.loc_y == -1)  return MoveType.BACK_TURN;
                if(nextLp.loc_z - this.loc_z == 1)  return MoveType.ASCEND;
                if(nextLp.loc_z - this.loc_z == -1)  return MoveType.DESCEND;
                break;
            case Y_NEGATIVE:
                if(nextLp.loc_x - this.loc_x == -1)  return MoveType.RIGHT_TURN;
                if(nextLp.loc_x - this.loc_x == 1)  return MoveType.LEFT_TURN;
                if(nextLp.loc_y - this.loc_y == -1)  return MoveType.STRAIGHT;
                if(nextLp.loc_y - this.loc_y == 1)  return MoveType.BACK_TURN;
                if(nextLp.loc_z - this.loc_z == 1)  return MoveType.ASCEND;
                if(nextLp.loc_z - this.loc_z == -1)  return MoveType.DESCEND;
                break;
            case Z_POSITIVE:
                if(nextLp.loc_z - this.loc_z == 1)  return MoveType.ASCEND;
                if(nextLp.loc_z - this.loc_z == -1)  return MoveType.DESCEND;
                return MoveType.STRAIGHT;
            case Z_NEGATIVE:
                if(nextLp.loc_z - this.loc_z == -1)  return MoveType.ASCEND;
                if(nextLp.loc_z - this.loc_z == 1)  return MoveType.DESCEND;
                return MoveType.STRAIGHT;

            default:    return MoveType.STRAIGHT;
        }
        return MoveType.STRAIGHT;

    }

    //计算一次运动的能量
    public int getMoveEnergy(MoveDirection  currentDirection, LocPoint nextLp, int nextAp) {

        int Ee = 0;     //额外能量，如果直行，不消耗额外能量

        MoveType mt = getMoveType(currentDirection, nextLp, nextAp);

        switch (mt) {

            case LEFT_TURN: case RIGHT_TURN:
                Ee = Et;
                break;
            case BACK_TURN:
                Ee = Eu;
                break;
            case ASCEND:
                Ee = Ea;
                break;
            case DESCEND:
                Ee = Ed;
                break;
            case SWITCH:
                Ee = Es;
                break;
            default:
                Ee = 0;

        }

        return Em + Ee;

    }

    //无人机的运动类型
    public enum MoveType {

        STRAIGHT,
        LEFT_TURN,
        RIGHT_TURN,
        BACK_TURN,
        ASCEND,
        DESCEND,
        HOVER,
        SWITCH     //切换AP

    }

    //无人机的运动方向
    public enum MoveDirection {

        X_POSITIVE,
        X_NEGATIVE,
        Y_POSITIVE,
        Y_NEGATIVE,
        Z_POSITIVE,
        Z_NEGATIVE,
        HOVER

    }

    //无人机的能耗
    public final int Em = 100;      //直行
    public final int Et = 150;      //90度
    public final int Eu = 200;      //180度
    public final int Ea = 300;      //爬升
    public final int Ed = 100;      //下降
    public final int Es = 500;      //切换AP

}

