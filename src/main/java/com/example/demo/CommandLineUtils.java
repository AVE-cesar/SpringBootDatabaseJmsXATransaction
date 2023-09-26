package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pour afficher les paramètres reçcus via la ligne de commande
 * 
 * @author bave
 *
 */
public class CommandLineUtils {

	private final static Logger logger = LoggerFactory.getLogger(CommandLineUtils.class);
	
	public static void showCommandLineParameters(String... args) {
		// get command line parameters
		String inputFile = args[0];
		logger.info("Fichier source: {}", inputFile);
		String maxLines = args[1];
		logger.info("Limite du nombre de lignes du fichier source: {}", maxLines);
		// mode: FILE ou NQ
		String modeMQorFile = args[2];
		logger.info("Mode MQ ou Fichier en sortie: {}", modeMQorFile);

		// on stoppe sur la première erreur
		boolean strictMode = Boolean.parseBoolean(args[3]);
		logger.info("Mode strict sur la première erreur: {}", strictMode);

		int RETRY = Integer.parseInt(args[4]);
		logger.info("compteur de tentative: {}", RETRY);
	}
}
