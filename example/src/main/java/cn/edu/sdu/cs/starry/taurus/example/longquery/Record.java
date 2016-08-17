package cn.edu.sdu.cs.starry.taurus.example.longquery;

import java.util.Date;

import com.google.gson.Gson;

public class Record {
	
	public Date time;
	
	public String value;

	public Record(Date time, String value) {
		super();
		this.time = time;
		this.value = value;
	}
	
	public static Record fromBytes(byte[] bytes){
		return new Gson().fromJson(new String(bytes), Record.class);
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public byte[] toBytes(){
		return new Gson().toJson(this).getBytes();
	}
	
}
