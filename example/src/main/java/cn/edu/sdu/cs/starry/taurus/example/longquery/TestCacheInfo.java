package cn.edu.sdu.cs.starry.taurus.example.longquery;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class TestCacheInfo {

	private int position;
	private int currentPage;
	private List<Record> currentLeftRecords = new ArrayList<Record>();
	private boolean finished;
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public List<Record> getCurrentLeftRecords() {
		return currentLeftRecords;
	}
	public void setCurrentLeftRecords(List<Record> currentLeftRecords) {
		this.currentLeftRecords = currentLeftRecords;
	}
	public boolean isFinished() {
		return finished;
	}
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public byte[] toBytes(){
		return new Gson().toJson(this).getBytes();
	}
	
	public static TestCacheInfo fromBytes(byte[] bytes){
		return new Gson().fromJson(new String(bytes) , TestCacheInfo.class);
	}
}
