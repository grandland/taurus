package cn.edu.sdu.cs.starry.taurus.server;

/**
 * @author SDU.xccui
 */
public interface CacheTool {
    public byte[] get(String key);

    public void set(String key, byte[] bytes);

    public void set(String key, byte[] bytes, int time);

    public void delete(String key);

    public void shutdown();
}
