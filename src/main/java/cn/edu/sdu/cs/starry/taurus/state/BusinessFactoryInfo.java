package cn.edu.sdu.cs.starry.taurus.state;

/**
 * Contains single business factory information
 * @author ytchen*/
public class BusinessFactoryInfo {

    private int totalResources;

    private int currentResources;

    public int getTotalResources() {
        return totalResources;
    }

    public void setTotalResources(int totalResources) {
        this.totalResources = totalResources;
    }

    public int getCurrentResources() {
        return currentResources;
    }

    public void setCurrentResources(int currentResources) {
        this.currentResources = currentResources;
    }


}
