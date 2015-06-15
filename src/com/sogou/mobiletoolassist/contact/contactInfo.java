package com.sogou.mobiletoolassist.contact;

public class contactInfo {
	public String cnname = null;
	private String mailAddr = null;
	private String ipv4addr = null;
	private String id = null;
	private String groupid = null;
	public String getMailAddr() {
		return mailAddr;
	}
	public void setMailAddr(String mailAddr) {
		//TODO check if invalid
		this.mailAddr = mailAddr;
	}
	public String getIpv4addr() {
		return ipv4addr;
	}
	public void setIpv4addr(String ipv4addr) {
		//TODO check if invalid
		this.ipv4addr = ipv4addr;
	}
	
}
