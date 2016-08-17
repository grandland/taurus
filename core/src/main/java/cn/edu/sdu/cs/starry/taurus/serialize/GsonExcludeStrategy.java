package cn.edu.sdu.cs.starry.taurus.serialize;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Created by yestin on 2016/5/10.
 */
public class GsonExcludeStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getName().startsWith("_");
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
