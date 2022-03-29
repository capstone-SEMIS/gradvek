package com.semis.gradvek.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class CsvFile {
    private final File file;
    private final String name;

    public CsvFile(File file, String name) {
        this.file = file;
        this.name = name;
    }

    public FileInputStream getInputStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public long length() {
        return file.length();
    }

    public String getName() {
        return name;
    }
}
