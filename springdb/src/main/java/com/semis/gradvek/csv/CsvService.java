package com.semis.gradvek.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

public class CsvService {
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

        try {
            // Open a CSV file reader
            CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(file.getInputStream())));

            String[] currentLine;
            String datatype = null;
            String[] columns = null;
            Map<String, CSVWriter> writers = new HashMap<>();

            // Read each line
            while ((currentLine = csvReader.readNext()) != null) {
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
                        File tmpFile = File.createTempFile("csv", null);
                        CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(tmpFile)));
                        writers.put(label, writer);
//                        writer.writeNext(columns);

                        // Register the file
                        String filename = FilenameUtils.getBaseName(file.getOriginalFilename()) + "_" + label;
                        files.add(new CsvFile(tmpFile, filename, datatype, label, Arrays.asList(columns)));
                        indexList.add(Integer.toString(index++));
                    }

                    // Write each line to a new file based on its label
                    CSVWriter writer = writers.get(label);
                    writer.writeNext(currentLine);
                }
            }

            // Close all the writers
            for (CSVWriter writer : writers.values()) {
                writer.close();
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return indexList;
    }

    public CsvFile get(String fileId) {
        int index = Integer.parseInt(fileId);
        return files.get(index);
    }
}
