import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;

public class ToFile {

    //此函数第一阶段使用，将完全图写进文件
    //参数：文件名，维数（节点个数），划分集合数（widget个数），邻接矩阵。
    public static void writeGraph (String filename, int vertsNum, int widgetNum, int[][] adjMat) {

        try {

            //String filePath = System.getProperty("user.dir") + "\\GraphFile\\";

            String filePath = "C:\\Users\\王子杰\\Desktop\\experiment\\";

            File file = new File(filePath + filename);

            if(!file.exists()){

                file.createNewFile();

            }

            //使用true，即进行append file
            FileWriter fileWritter = new FileWriter(filePath + filename,false);

            //写入内容
            fileWritter.write("NAME : Graph GTSP" + "\n");
            fileWritter.write("TYPE : GTSP" + "\n");
            fileWritter.write("COMMENT : A test for a graph GTSP" + "\n");
            fileWritter.write("DIMENSION:" + vertsNum + "\n");
            fileWritter.write("GTSP_SETS :" + widgetNum + "\n");
            fileWritter.write("EDGE_WEIGHT_TYPE : EXPLICIT" + "\n");
            fileWritter.write("EDGE_WEIGHT_FORMAT : FULL_MATRIX" + "\n");
            fileWritter.write("EDGE_WEIGHT_SECTION" + "\n");
            //写入邻接矩阵
            for (int i = 0; i < vertsNum; i++) {
                for (int j = 0; j < vertsNum; j++)
                    fileWritter.write(String.format("%-10d", adjMat[i][j]));
                fileWritter.write("\n");
            }
            //写入GTSP划分集合
            fileWritter.write("GTSP_SET_SECTION\n");
            for (int i = 1; i <= widgetNum; i++) {
                fileWritter.write(i + " ");
                for (int j = 1; j <= 6; j++)
                    fileWritter.write(6 * (i - 1) + j + " ");

                fileWritter.write(-1 + "\n");
            }
            fileWritter.write("EOF");


            fileWritter.close();

            System.out.println("Graph To File finish");




        }catch (Exception e) {
            e.printStackTrace();
        }



    }


    //此函数在第二阶段使用，将地图信息写入文件
    public static void writeMapToFile (String filename, Map map) {

        try {

            //String filePath = System.getProperty("user.dir") + "\\GraphFile\\";

            String filePath = "C:\\Users\\王子杰\\Desktop\\experiment\\";

            File file = new File(filePath + filename);

            if(!file.exists()){

                file.createNewFile();

            }

            //使用true，即进行append file
            FileWriter fileWritter = new FileWriter(filePath + filename,false);


            int[][][] pos = map.pos;     //接收Map的坐标矩阵

            //把矩阵写进文件
            for(int i=0; i < map.X_COR; i++)
                for(int j=0; j < map.Y_COR; j++)
                    for(int k=0; k < map.Z_COR; k++)
                        if(pos[i][j][k] == 1){
                            fileWritter.write(i + "," + j + "," + k + "\n");
                        }


            fileWritter.close();

            System.out.println("map tofile finish");


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //此函数在第二阶段使用，将Widget路径信息写入文件
    public static void writePathToFile (String filename, ArrayList<Widget> path) {

        try {

            //String filePath = System.getProperty("user.dir") + "\\GraphFile\\";

            String filePath = "C:\\Users\\王子杰\\Desktop\\experiment\\";

            File file = new File(filePath + filename);

            if(!file.exists()){

                file.createNewFile();

            }

            //使用true，即进行append file
            FileWriter fileWritter = new FileWriter(filePath + filename,false);

            for (int i = 0; i < path.size(); i++)
                fileWritter.write(path.get(i).toString() + "\n");

            System.out.println("path tofile finish");

            fileWritter.close();


        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    //主函数，将生成的Widget路径写入文件
    public static void main(String args[]) {
        ArrayList<Widget> path = new ArrayList<>();
        path.add(new Widget(1,2,3,4));

        writePathToFile("path.txt", path);

    }

}
