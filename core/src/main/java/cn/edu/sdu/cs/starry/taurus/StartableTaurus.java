package cn.edu.sdu.cs.starry.taurus;

/**
 * Created by yestin on 2016/5/6.
 * Entry of taurus
 */
public class StartableTaurus {

    public static void startTaurus(String serverName, String confPath){
        Taurus.main(new String[]{"start",serverName, confPath});
    }

}
