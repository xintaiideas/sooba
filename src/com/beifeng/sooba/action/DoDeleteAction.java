package com.beifeng.sooba.action;

import com.beifeng.sooba.biz.DelDocumentAndRecord;
import com.opensymphony.xwork2.ActionSupport;

public class DoDeleteAction extends ActionSupport{
	
	private DelDocumentAndRecord delDocAndRecord;
	@Override
	public String execute() throws Exception {
//		System.out.println("-------DoDeleteAction-------");
//        for(String str:delchk)
//        	System.out.println(str);
		delDocAndRecord.delList(delchk);
		
		return SUCCESS;
	}

	private String delchk[];

	public String[] getDelchk() {
		return delchk;
	}

	public void setDelchk(String[] delchk) {
		this.delchk = delchk;
	}

	public void setDelDocAndRecord(DelDocumentAndRecord delDocAndRecord) {
		this.delDocAndRecord = delDocAndRecord;
	}

	
}
