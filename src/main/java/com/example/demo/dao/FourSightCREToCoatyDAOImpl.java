package com.example.demo.dao;

import java.sql.PreparedStatement;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.model.Record;
import com.example.demo.model.RecordMapper;

@Component
public class FourSightCREToCoatyDAOImpl implements FourSightCREToCoatyDAO {

	private final static Logger logger = LoggerFactory.getLogger(FourSightCREToCoatyDAOImpl.class);

	JdbcTemplate jdbcTemplate;

	private final String SQL_FIND_BY_ID = "SELECT * FROM CACEIS_OWN.CRE_4SIGHT_COATY WHERE id = ?";
	private final String SQL_FIND_BY_FILENAME_AND_PROCESSED = "SELECT * FROM CACEIS_OWN.CRE_4SIGHT_COATY WHERE filename = ? and processed = 0 order by id asc";
	private final String SQL_COUNT_BY_FILENAME = "SELECT count(*) FROM CACEIS_OWN.CRE_4SIGHT_COATY WHERE filename = ?";
	
	private final String SQL_UPDATE = "UPDATE CACEIS_OWN.CRE_4SIGHT_COATY SET processed = 1, MQ_CRE = ?, ID_QUEUE_MESSAGE = ?, update_datetime=sysdate WHERE id = ?";
	private final String SQL_INSERT = "INSERT INTO CACEIS_OWN.CRE_4SIGHT_COATY (id, filename, LINENUMBER, raw_cre, MQ_CRE, processed) VALUES (caceis_own.CRE_4SIGHT_COATY_SEQ.nextval, ?, ?, ?, ?, 0)";
	
	@Autowired
	public FourSightCREToCoatyDAOImpl(@Lazy DataSource ds) {
		jdbcTemplate = new JdbcTemplate(ds);
	}

	@Override
	public Record getRecordById(Long id) {
		logger.info("getCountryById: {}", id);
		return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new RecordMapper(), new Object[] { id });
	}
	
	@Override
	public List<Record> getRecordByFilenameAndByProcessed(String filename) {
		logger.info("getRecordByFilename: {}", filename);
		return jdbcTemplate.query(SQL_FIND_BY_FILENAME_AND_PROCESSED, new RecordMapper(), new Object[] { filename });
	}
	
	@Override
	public long getRecordCountByFilename(String filename) {
		logger.info("getRecordCountByFilename: {}", filename);
		return jdbcTemplate.queryForObject(SQL_COUNT_BY_FILENAME, Long.class, new Object[] { filename });
	}

	@Override
	public List<Record> getAllRecords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteRecord(Record record) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean updateRecord(Record record) {
		return jdbcTemplate.update(SQL_UPDATE, record.getFilename(), record.getId()) > 0;
	}
	
	@Override
	public int[][] updateBatchRecord(List<Record> records) {
		return jdbcTemplate.batchUpdate(SQL_UPDATE, records, 100, (PreparedStatement ps, Record record) -> {
			ps.setString(1, record.getMqCre());
			ps.setString(2, record.getIdQueueMessage());
			ps.setLong(3, record.getId());
		});
	}

	@Override
	public boolean createRecord(Record record) {
		return jdbcTemplate.update(SQL_INSERT, record.getFilename()) > 0;
	}
	
	@Override
	public int[][] createBatchRecord(List<Record> records) {
		return jdbcTemplate.batchUpdate(SQL_INSERT, records, 100, (PreparedStatement ps, Record record) -> {
			ps.setString(1, record.getFilename());
			ps.setLong(2, record.getLineNumber());
			ps.setString(3, record.getLine());
			ps.setString(4, record.getMqCre());
		});
	}
}
