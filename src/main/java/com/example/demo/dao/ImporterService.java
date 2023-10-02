package com.example.demo.service;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ImporterService {

	void doImport(String... args) throws FileNotFoundException, IOException;
}
