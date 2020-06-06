import java.util.ArrayList;


//使用最原始的方法计算cost
public class CalculateCost {

    public int ap_switch_num = 0;       //切换ap的次数

    public int getCost (ArrayList<Widget> sequence) {

        int cost = 0;

        Direction currentDir = Direction.NONE;
        for (int i = 0; i < sequence.size() - 1; i++) {
            Action action = getAction(sequence.get(i), sequence.get(i+1), currentDir);
            currentDir = getNextDirection(sequence.get(i), sequence.get(i+1));
            int cost_one_step = 0;
            switch (action) {
                case STRAIGHT:
                    cost_one_step = Em;
                    break;
                case TO_STRAIGHT:
                    cost_one_step = Em + Et/2;
                    break;
                case TURN:
                case BACK_TURN:
                    cost_one_step = Em + Et;
                    break;
                case ASCEND:
                    cost_one_step = Ea + Et/2;
                    break;
                case DESCEND:
                    cost_one_step = Ed + Et/2;
                    break;
                case ASCEND_ON:
                    cost_one_step = Ea;
                    break;
                case DESCEND_ON:
                    cost_one_step = Ed;
                    break;
                case AP_SWITCHING:
                    cost_one_step = Es;
                    this.ap_switch_num += 1;
                    break;
                case A_DESCEND:
                    cost_one_step = Ea_d + Ed;
                    break;
                case D_ASCEND:
                    cost_one_step = Ed_a + Ea;
                    break;
                default:
                    cost_one_step = Em;

            }

            cost += cost_one_step;

        }

        return cost;

    }

    //w1是当前点，w2是下一个点
    public Action getAction (Widget w1, Widget w2, Direction currentDir) {

        if(w1.ap != w2.ap)      return Action.AP_SWITCHING;

        switch (currentDir){
            case NONE:
                if (Math.abs(w2.loc_y - w1.loc_y) == 1 || Math.abs(w2.loc_x - w1.loc_x) == 1)     return Action.TO_STRAIGHT;
                if (w2.loc_z == w1.loc_z + 1)   return Action.ASCEND;
                if (w2.loc_z == w1.loc_z - 1)   return Action.DESCEND;
                break;
            case X_POSITIVE:
                if (w2.loc_x == w1.loc_x + 1)    return Action.STRAIGHT;
                if (w2.loc_x == w1.loc_x - 1)    return Action.BACK_TURN;
                if (Math.abs(w2.loc_y - w1.loc_y) == 1)     return Action.TURN;
                if (w2.loc_z == w1.loc_z + 1)   return Action.ASCEND;
                if (w2.loc_z == w1.loc_z - 1)   return Action.DESCEND;
                break;
            case X_NEGATIVE:
                if (w2.loc_x == w1.loc_x - 1)    return Action.STRAIGHT;
                if (w2.loc_x == w1.loc_x + 1)    return Action.BACK_TURN;
                if (Math.abs(w2.loc_y - w1.loc_y) == 1)     return Action.TURN;
                if (w2.loc_z == w1.loc_z + 1)   return Action.ASCEND;
                if (w2.loc_z == w1.loc_z - 1)   return Action.DESCEND;
                break;
            case Y_POSITIVE:
                if (w2.loc_y == w1.loc_y + 1)    return Action.STRAIGHT;
                if (w2.loc_y == w1.loc_y - 1)    return Action.BACK_TURN;
                if (Math.abs(w2.loc_x - w1.loc_x) == 1)     return Action.TURN;
                if (w2.loc_z == w1.loc_z + 1)   return Action.ASCEND;
                if (w2.loc_z == w1.loc_z - 1)   return Action.DESCEND;
                break;
            case Y_NEGATIVE:
                if (w2.loc_y == w1.loc_y - 1)    return Action.STRAIGHT;
                if (w2.loc_y == w1.loc_y + 1)    return Action.BACK_TURN;
                if (Math.abs(w2.loc_x - w1.loc_x) == 1)     return Action.TURN;
                if (w2.loc_z == w1.loc_z + 1)   return Action.ASCEND;
                if (w2.loc_z == w1.loc_z - 1)   return Action.DESCEND;
                break;
            case Z_POSITIVE:
                if (w2.loc_z == w1.loc_z + 1)   return Action.ASCEND_ON;
                if (w2.loc_z == w1.loc_z - 1)   return Action.A_DESCEND;
                return Action.TO_STRAIGHT;
            case Z_NEGATIVE:
                if (w2.loc_z == w1.loc_z - 1)   return Action.DESCEND_ON;
                if (w2.loc_z == w1.loc_z + 1)   return Action.D_ASCEND;
                return Action.TO_STRAIGHT;
            default:
                return Action.STRAIGHT;

        }

        return Action.STRAIGHT;

    }

    public Direction getNextDirection (Widget w1, Widget w2) {

        if (w1.ap != w2.ap)                 return Direction.NONE;
        if (w2.loc_x - w1.loc_x == 1)       return Direction.X_POSITIVE;
        if (w2.loc_x - w1.loc_x == -1)      return Direction.X_NEGATIVE;
        if (w2.loc_y - w1.loc_y == 1)       return Direction.Y_POSITIVE;
        if (w2.loc_y - w1.loc_y == -1)      return Direction.Y_NEGATIVE;
        if (w2.loc_z - w1.loc_z == 1)       return Direction.Z_POSITIVE;
        if (w2.loc_z - w1.loc_z == -1)      return Direction.Z_NEGATIVE;
        return Direction.NONE;

    }

    //无人机的运动方式
    public enum Action {

        STRAIGHT,
        TO_STRAIGHT,
        TURN,
        BACK_TURN,
        ASCEND,
        DESCEND,
        ASCEND_ON,
        DESCEND_ON,
        AP_SWITCHING,
        A_DESCEND,
        D_ASCEND

    }

    //无人机的运动方向
    public enum Direction {
        X_POSITIVE,
        X_NEGATIVE,
        Y_POSITIVE,
        Y_NEGATIVE,
        Z_POSITIVE,
        Z_NEGATIVE,
        DIFF_AP,
        NONE,
    }

    //无人机的能耗
    public static int Em = 100;      //直行
    public static int Et = 150;      //90度
    public static int Eu = Et;      //180度
    public static int Ea = 300;      //爬升
    public static int Ed = 100;      //下降
    public static int Ea_d = 50;      //爬升转化为下降
    public static int Ed_a = 100;      //下降转化为爬升
    public static int Es = 500;      //切换AP
    public static int EMax = WidgetGraph.EMax;

//    public static int Em = WidgetGraph.Em;      //直行
//    public static int Et = WidgetGraph.Et;      //90度
//    public static int Eu = WidgetGraph.Eu;      //180度
//    public static int Ea = WidgetGraph.Ea;      //爬升
//    public static int Ed = WidgetGraph.Ed;      //下降
//    public static int Ea_d = WidgetGraph.Ea_d;      //爬升转化为下降
//    public static int Ed_a = WidgetGraph.Ed_a;      //下降转化为爬升
//    public static int Es = WidgetGraph.Es;      //切换AP
//    public static int EMax = WidgetGraph.EMax;

}
