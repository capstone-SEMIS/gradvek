package com.semis.gradvek.csv;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvService {
    private static CsvService instance;
    private List<CsvFile> files;

    private CsvService() {
        this.files = new ArrayList<>();
    }

    public static CsvService getInstance() {
        if (instance == null) {
            instance = new CsvService();
        }
        return instance;
    }

    public String put(MultipartFile file) {
        int index = files.size();

        String firstLine = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            firstLine = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> columns = Stream.of(firstLine.split(",")).map(s -> s.trim()).collect(Collectors.toList());

        try {
            InputStream stream = file.getInputStream();
            File tmpFile = File.createTempFile("csv", null);
            FileUtils.copyInputStreamToFile(stream, tmpFile);
            stream.close();
            files.add(new CsvFile(tmpFile, file.getOriginalFilename(), columns));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Integer.toString(index);
    }

    public CsvFile get(String fileId) {
        int index = Integer.parseInt(fileId);
        return files.get(index);
    }
}
