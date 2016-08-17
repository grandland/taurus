package cn.edu.sdu.cs.starry.taurus.example;

import cn.edu.sdu.cs.starry.taurus.common.RequestIdentification;

public class SimpleRequestIdentification extends RequestIdentification {
	private String businessName;
	private String id;

	public SimpleRequestIdentification(String businessName, String id) {
		this.businessName = businessName;
		this.id = id;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return id;
	}
}
