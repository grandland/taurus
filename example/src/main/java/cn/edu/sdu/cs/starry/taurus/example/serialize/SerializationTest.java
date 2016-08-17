package cn.edu.sdu.cs.starry.taurus.example.serialize;

import cn.edu.sdu.cs.starry.taurus.example.query.TestQueryRequest;
import cn.edu.sdu.cs.starry.taurus.serialize.SerializeException;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * Created by yestin on 2016/5/10.
 */
public class SerializationTest {
    public static void main(String[] args) throws SerializeException, IllegalAccessException, InstantiationException, UnsupportedEncodingException {
        TestQueryRequest testQueryRequest = new TestQueryRequest();
        testQueryRequest.setName("Hello");
        byte[] bytes = testQueryRequest.toBytes();
        TestQueryRequest query2 = (TestQueryRequest) TestQueryRequest.class.newInstance().fromBytes(bytes);
//        System.out.println(new String(bytes,"UTF-8"));
//        TestQueryRequest query2 = new Gson().fromJson(new String(bytes,"UTF-8"),TestQueryRequest.class);
        System.out.println(query2);
    }
}
