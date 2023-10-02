package com.example.demo.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.StringUtils;
import com.example.demo.dao.FourSightCREToCoatyDAO;
import com.example.demo.model.Record;

@Service
public class ImporterServiceImpl implements ImporterService {

	private final static Logger logger = LoggerFactory.getLogger(ImporterServiceImpl.class);

	@Value("${batchmode.size}")
	public long batchModeSize;

	@Autowired
	FourSightCREToCoatyDAO fourSightCREToCoatyDAO;

	@Override
	public void doImport(String... args) throws FileNotFoundException, IOException {
		String filename = args[0];
		int nbLines = Integer.parseInt(args[1]);

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			long count = 0;

			List<Record> recordsForNextBatch = new ArrayList();

			while ((line = br.readLine()) != null) {

				// on supprime l'horodatage
				line = line.substring(26);

				// on convertit la ligne en un POJO
				Record record = new Record();
				record.setFilename(filename);
				record.setLine(line);
				record.setLineNumber(count);

				StringUtils.extractSensitiveInfoFromLine(line);

				if (record != null) {
					recordsForNextBatch.add(record);
				}

				if (recordsForNextBatch.size() >= batchModeSize) {
					logger.info("On traite un premier lot de taille: {}", recordsForNextBatch.size());

					// on sauvegarde par lots + envoi JMS
					List<Record> recordsForNextBatchCOPY = recordsForNextBatch.stream().collect(Collectors.toList());
					insertRowIntoDatabase(recordsForNextBatchCOPY);

					// on vide le stock pour le prochain lot
					recordsForNextBatch.clear();
				}

				count++;
				if (nbLines != -1 && count > nbLines) {
					break;
				}
			} // fin du while sur les lignes du fichier

			// il peut rester des lignes à traiter, un lot incomplet, on doit le traiter
			if (!recordsForNextBatch.isEmpty()) {
				logger.info("On traite le dernier lot incomplet de taille: {} < taille des lots: {}",
						recordsForNextBatch.size(), batchModeSize);

				// on sauvegarde par lots + envoi JMS
				List<Record> recordsForNextBatchCOPY = recordsForNextBatch.stream().collect(Collectors.toList());
				insertRowIntoDatabase(recordsForNextBatchCOPY);
			}
		}
	}

	@Transactional
	private void insertRowIntoDatabase(List<Record> records) {
		// on stocke par lots les records en DBs
		logger.info("Insertion d'un bloc de record de taille: {}", records.size());
		fourSightCREToCoatyDAO.createBatchRecord(records);
	}

}
