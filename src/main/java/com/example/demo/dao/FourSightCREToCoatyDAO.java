package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.Record;

public interface FourSightCREToCoatyDAO {

	Record getRecordById(Long id);

	List<Record> getAllRecords();

	boolean deleteRecord(Record record);
	
	public int[][] updateBatchRecord(List<Record> records);

	boolean createRecord(Record record);
	
	public int[][] createBatchRecord(List<Record> record);

	List<Record> getRecordByFilenameAndByProcessed(String filename);
	
	public long getRecordCountByFilename(String filename);
}
