package com.semis.gradvek.csv;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

        try {
            InputStream stream = file.getInputStream();
            File tmpFile = File.createTempFile("csv", null);
            FileUtils.copyInputStreamToFile(stream, tmpFile);
            stream.close();
            files.add(new CsvFile(tmpFile, file.getOriginalFilename()));
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
