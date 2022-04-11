package com.semis.gradvek.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.logging.Logger;

public class CsvService {
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private static CsvService instance;
    private final List<CsvFile> files;

    private CsvService() {
        this.files = new ArrayList<>();
    }

    public static CsvService getInstance() {
        if (instance == null) {
            instance = new CsvService();
        }
        return instance;
    }

    public List<String> put(MultipartFile file) {
        int index = files.size();
        List<String> indexList = new ArrayList<>();

        // Open a CSV file reader
        CSVReader csvReader;
        try {
            csvReader = new CSVReader(new BufferedReader(new InputStreamReader(file.getInputStream())));
        } catch (IOException e) {
            logger.severe("IO error reading file " + file.getOriginalFilename());
            return new ArrayList<>();
        }

        String[] currentLine;
        String datatype = null;
        String[] columns = null;
        Map<String, CSVWriter> writers = new HashMap<>();

        // Read each line
        while (true) {
            try {
                if ((currentLine = csvReader.readNext()) == null) break;
            } catch (IOException e) {
                logger.severe("IO error when reading file " + file.getOriginalFilename());
                return new ArrayList<>();
            } catch (CsvValidationException e) {
                logger.severe("CSV validation error when reading file " + file.getOriginalFilename());
                return new ArrayList<>();
            }

            if (columns == null) {

                // Save the first line as column headings
                columns = currentLine;

                // The first column heading is the data type
                datatype = currentLine[0];

            } else {

                // The first column is the label (node label or relationship type)
                String label = currentLine[0];

                // Create a writer for each label
                if (!writers.containsKey(label)) {
                    File tmpFile;
                    try {
                        tmpFile = File.createTempFile("csv", null);
                    } catch (IOException e) {
                        logger.severe("IO error creating temp CSV for label " + label +
                                " of file " + file.getOriginalFilename());
                        return new ArrayList<>();
                    }

                    CSVWriter writer;
                    try {
                        writer = new CSVWriter(new BufferedWriter(new FileWriter(tmpFile)));
                    } catch (IOException e) {
                        logger.severe("IO error writing to temp CSV " + tmpFile.getAbsolutePath() +
                                " for label " + label + " of file " + file.getOriginalFilename());
                        return new ArrayList<>();
                    }
                    writers.put(label, writer);

                    // Register the file
                    String filename = FilenameUtils.getBaseName(file.getOriginalFilename()) + "_" + label;
                    files.add(new CsvFile(tmpFile, filename, file.getOriginalFilename(), datatype, label, Arrays.asList(columns)));
                    indexList.add(Integer.toString(index++));
                }

                // Write each line to a new file based on its label
                CSVWriter writer = writers.get(label);
                writer.writeNext(currentLine, false);
            }
        }

        // Close all the writers
        for (Map.Entry<String, CSVWriter> entry : writers.entrySet()) {
            try {
                entry.getValue().close();
            } catch (IOException e) {
                logger.info("IO error closing CSV writer for label " + entry.getKey() +
                        " of file " + file.getOriginalFilename());
            }
        }

        return indexList;
    }

    public CsvFile get(String fileId) {
        int index = Integer.parseInt(fileId);
        return files.get(index);
    }
}
