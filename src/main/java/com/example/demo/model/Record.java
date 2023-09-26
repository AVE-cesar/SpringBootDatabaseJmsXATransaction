package com.example.demo.model;

import java.sql.Date;

public class Record {

	private Long id;
	private String filename;
	private Date lotDate;
	private long lineNumber;
	private String line;
	private String mqCre;
	private String idQueueMessage;

	public Record() {
	}

	public Record(Long id, String filename) {
		this.id = id;
		this.filename = filename;
	}

	public Record(String filename) {
		this.id = null;
		this.filename = filename;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Date getLotDate() {
		return lotDate;
	}

	public void setLotDate(Date lotDate) {
		this.lotDate = lotDate;
	}

	public long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getMqCre() {
		return mqCre;
	}

	public void setMqCre(String mqCre) {
		this.mqCre = mqCre;
	}

	public String getIdQueueMessage() {
		return idQueueMessage;
	}

	public void setIdQueueMessage(String idQueueMessage) {
		this.idQueueMessage = idQueueMessage;
	}

}
