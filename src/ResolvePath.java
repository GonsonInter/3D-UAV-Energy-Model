import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class ResolvePath {

    public int cost;
    public int[] path;          //GLNS生成的最短路径
    public String filepath;


    //初始化类就已经调用下面两个函数
    public ResolvePath(String filepath){
        this.filepath = filepath;
        getCostAndPath();
    }

    public HashMap<String, String> resolveFile(String filename) {

        ArrayList<String> arrayList = new ArrayList<>();
        try {

            File file = new File(filename);
            if (file.isFile() && file.exists())
            { // 判断文件是否存在
                InputStreamReader inputReader = null;
                inputReader = new InputStreamReader(new FileInputStream(file));
                BufferedReader bf = new BufferedReader(inputReader);
                String str;
                while ((str = bf.readLine()) != null) {
                    arrayList.add(str);
                }
                bf.close();
                inputReader.close();

            }
            else
            {
                System.out.println("找不到指定的文件");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 对ArrayList中存储的字符串进行处理,存入到hashmap
        int length = arrayList.size();

        HashMap<String, String> map = new HashMap();
        for (String str : arrayList) {

            if (null != str) {
                String[] param = str.split(":");
                map.put(param[0].trim(), param[1].trim());

            }
        }
        //System.out.println(map);
        return map;
    }

    public void getCostAndPath() {
        try {

            HashMap map = resolveFile(this.filepath);
            cost = Integer.parseInt(map.get("Tour Cost").toString());               //获取路径的代价
            String path_str = map.get("Tour").toString().substring(1, map.get("Tour").toString().length()-1);
            String[] _path = path_str.split(",");

            path = new int[_path.length];           //分配空间
            for (int i = 0; i < _path.length; i++)
                path[i] = Integer.parseInt(_path[i].trim()) - 1;        //同步输入和输出，GLNS的路径节点输出，比这里程序的路径节点要大1，因此要-1

            //System.out.print(cost);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
