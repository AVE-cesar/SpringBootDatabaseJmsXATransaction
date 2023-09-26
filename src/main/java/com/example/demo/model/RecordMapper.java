package com.example.demo.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class RecordMapper implements RowMapper<Record>{

	/**
	 * Permet de convertir les données SQL en données Java de l'objet Record.
	 */
	@Override
	public Record mapRow(ResultSet resultSet, int i) throws SQLException {
		Record record = new Record();
		
		record.setId(resultSet.getLong("id"));
		record.setFilename(resultSet.getString("filename"));
		record.setLineNumber(resultSet.getLong("linenumber"));
		record.setLine(resultSet.getString("raw_cre"));
		record.setMqCre(resultSet.getString("mq_cre"));
		record.setIdQueueMessage(resultSet.getString("id_queue_message"));
		
		return record;
	}

}
