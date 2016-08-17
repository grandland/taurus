package cn.edu.sdu.cs.starry.taurus.server;

public interface TaurusListener {
    public void onStartup();

    public void onShutdown();
    
    public void onRestart();
}
