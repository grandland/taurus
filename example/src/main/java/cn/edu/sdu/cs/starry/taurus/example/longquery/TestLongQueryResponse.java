package cn.edu.sdu.cs.starry.taurus.example.longquery;

import java.util.List;

import com.google.gson.Gson;

import cn.edu.sdu.cs.starry.taurus.response.LongQueryResponse;

public class TestLongQueryResponse extends LongQueryResponse {

	private List<Record> records;

	private  String error;
	
	//the page num this response returns.
	private int page;
	
	private int totalCount;
	
	public TestLongQueryResponse() {
		super();
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getSayHello(){
		return new Gson().toJson(this);
	}
	
}
