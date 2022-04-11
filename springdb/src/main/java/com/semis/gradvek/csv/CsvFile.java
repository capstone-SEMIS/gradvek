package com.semis.gradvek.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.logging.Logger;

public class CsvFile {
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
    private final File file;
    private final String name;
    private final String originalName;
    private final String type;
    private final String label;
    private final List<String> columns;

    public CsvFile(File file, String name, String originalName, String type, String label, List<String> columns) {
        this.file = file;
        this.name = name;
        this.originalName= originalName;
        this.type = type;
        this.label = label;
        this.columns = columns;
    }

    /**
     * An input stream of the file contents.
     */
    public FileInputStream getInputStream() {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException | NullPointerException ex) {
            logger.severe("File \"" + getName() + "\" not found");
        }
        return stream;
    }

    /**
     * The size of the file contents.
     */
    public long length() {
        return file.length();
    }

    /**
     * The internal name of the file, such as "Node_Drug".
     */
    public String getName() {
        return name;
    }

    /**
     * The name of the file uploaded to create this CSV.
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * The list of column headers from the original file.
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     * The type of file, for example "Node" or "Relationship".  From the first column header.
     */
    public String getType() {
        return type;
    }

    /**
     * The data label, for example "Drug" or "AssociatedWith".  From the data in the first column.
     */
    public String getLabel() {
        return label;
    }
}
