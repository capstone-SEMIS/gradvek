package com.semis.gradvek.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class CsvFile {
    private final File file;
    private final String name;
    private final List<String> columns;

    public CsvFile(File file, String name, List<String> columns) {
        this.file = file;
        this.name = name;
        this.columns = columns;
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

    public List<String> getColumns() {
        return columns;
    }
}
