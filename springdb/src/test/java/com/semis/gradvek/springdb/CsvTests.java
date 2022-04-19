package com.semis.gradvek.springdb;

import com.semis.gradvek.csv.CsvFile;
import com.semis.gradvek.csv.CsvService;
import com.semis.gradvek.entity.Constants;
import com.semis.gradvek.graphdb.Neo4jDriver;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;

@SpringBootTest
@TestPropertySource(properties = "db.type=inmem")
class CsvTests implements Constants {
    @Autowired
    private Controller controller;

    @Test
    void loadCsvNeitherNodeNorRelationship() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("greeting", "object"));
        csvTest.addRow(List.of("hello", "world"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        assertThat(command).isNull();
    }

    @Test
    void loadCsvPlainNode() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("Node"));
        csvTest.addRow(List.of("hello"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        String expected = "LOAD CSV FROM 'https://example.com' AS line CALL { WITH line CREATE (:hello { dataset: 'test_hello' }) } IN TRANSACTIONS";
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void loadCsv1PropNode() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("Node", "firstProp"));
        csvTest.addRow(List.of("hello", "world"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        String expected = "LOAD CSV FROM 'https://example.com' AS line CALL { WITH line CREATE (:hello { dataset: 'test_hello', firstProp: line[1] }) } IN TRANSACTIONS";
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void loadCsv2PropNode() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("Node", "firstProp", "secondProp"));
        csvTest.addRow(List.of("hello", "beautiful", "world"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        String expected = "LOAD CSV FROM 'https://example.com' AS line CALL { WITH line CREATE (:hello { dataset: 'test_hello', firstProp: line[1], secondProp: line[2] }) } IN TRANSACTIONS";
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void loadCsvRelationshipNoIds() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("Relationship"));
        csvTest.addRow(List.of("hello"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        assertThat(command).isNull();
    }


    @Test
    void loadCsvRelationshipBadFirstId() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("Relationship", "not_a_meddraCode", DRUG_ID_STRING));
        csvTest.addRow(List.of("hello", "beautiful", "world"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        assertThat(command).isNull();
    }

    @Test
    void loadCsvRelationshipBadSecondId() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("Relationship", ADVERSE_EVENT_ID_STRING, "not_a_chembl_code"));
        csvTest.addRow(List.of("hello", "beautiful", "world"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        assertThat(command).isNull();
    }

    @Test
    void loadCsvRelationshipDrugGene() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("Relationship", DRUG_ID_STRING, GENE_ID_STRING));
        csvTest.addRow(List.of("hello", "beautiful", "world"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        String expected = "LOAD CSV FROM 'https://example.com' AS line CALL { WITH line MATCH (fromNode:Drug {" + DRUG_ID_STRING + ": line[1]}), (toNode:Gene {geneId: line[2]}) CREATE (fromNode)-[:hello { dataset: 'test_hello' }]->(toNode) } IN TRANSACTIONS";
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void loadCsvRelationshipTargetPathway1Prop() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("Relationship", TARGET_ID_STRING, PATHWAY_ID_STRING, "firstProp"));
        csvTest.addRow(List.of("hello", "strange", "beautiful", "world"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        String expected = "LOAD CSV FROM 'https://example.com' AS line CALL { WITH line MATCH (fromNode:Target {" + TARGET_ID_STRING + ": line[1]}), (toNode:Pathway {pathwayId: line[2]}) CREATE (fromNode)-[:hello { dataset: 'test_hello', firstProp: line[3] }]->(toNode) } IN TRANSACTIONS";
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void loadCsvRelationshipAeAe2Prop() {
        CsvService csvService = CsvService.getInstance();

        CsvTestFile csvTest = new CsvTestFile("test");
        csvTest.setHeaders(List.of("Relationship", ADVERSE_EVENT_ID_STRING, ADVERSE_EVENT_ID_STRING, "firstProp", "secondProp"));
        csvTest.addRow(List.of("hello", "strange", "beautiful", "bittersweet", "world"));

        CsvFile csv = csvService.get(csvService.put(csvTest).get(0));

        String command = Neo4jDriver.loadCsvCommand("https://example.com", csv);
        String expected = "LOAD CSV FROM 'https://example.com' AS line CALL { WITH line MATCH (fromNode:AdverseEvent {" + ADVERSE_EVENT_ID_STRING + ": line[1]}), (toNode:AdverseEvent {" + ADVERSE_EVENT_ID_STRING + ": line[2]}) CREATE (fromNode)-[:hello { dataset: 'test_hello', firstProp: line[3], secondProp: line[4] }]->(toNode) } IN TRANSACTIONS";
        assertThat(command).isEqualTo(expected);
    }

    @Test
    void csvPostBadRequest() {
        CsvTestFile file = new CsvTestFile("test");
        file.setHeaders(List.of("hello"));
        ResponseEntity<Map<String, String>> response = controller.csvPostProcess(file, "https://example.com", "/api");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void csvPostGet() throws IOException {
        CsvTestFile file = new CsvTestFile("test");
        file.setHeaders(List.of("Node"));
        file.addRow(List.of("hello"));

        ResponseEntity<Map<String, String>> postResponse = controller.csvPostProcess(file, "https://example.com", "/api");
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(postResponse.getBody()).isNotNull();
        assertThat(postResponse.getBody().size()).isEqualTo(1);
        String fileId = postResponse.getBody().keySet().iterator().next();
        assertThat(postResponse.getBody()).containsValue("test_hello");

        ResponseEntity<InputStreamResource> getResponse = controller.csvGet(fileId);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        String contents = new String(getResponse.getBody().getInputStream().readAllBytes());
        assertThat(contents).isEqualTo("hello\n");
    }

    @Test
    void csvFileNull() {
        CsvFile file = new CsvFile(null, "name", "originalName.csv", "type", "label", List.of(""));
        assertThat(file.getInputStream()).isNull();
    }

    @Test
    void csvServiceSingleton() {
        CsvService csvService1 = CsvService.getInstance();
        CsvService csvService2 = CsvService.getInstance();
        assertThatObject(csvService1).isSameAs(csvService2);
    }

    @Test
    void csvServiceSimpleCsv() {
        String header1 = "greeting";
        String header2 = "object";
        String headers = header1 + "," + header2 + "\n";

        String col1 = "hello";
        String col2 = "world";
        String cols = col1 + "," + col2 + "\n";

        String contents = headers + cols;
        String name = "test";
        String filename = name + "_" + col1;
        CsvTestFile original = new CsvTestFile(name, contents);

        CsvService csvService = CsvService.getInstance();
        List<String> indexes = csvService.put(original);
        assertThat(indexes).hasSize(1);

        String index = indexes.get(0);
        CsvFile copy = csvService.get(index);
        byte[] copyBytes = new byte[0];
        try {
            copyBytes = copy.getInputStream().readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String copyContents = new String(copyBytes);
        assertThat(copyContents).isEqualTo(cols);
        assertThat(copy.length()).isEqualTo(cols.length());
        assertThat(filename).isEqualTo(copy.getName());
        assertThat(copy.getColumns()).containsExactly(header1, header2);
        assertThat(copy.getType()).isEqualTo(header1);
        assertThat(copy.getLabel()).isEqualTo(col1);
        assertThat(copy.getOriginalName()).isEqualTo(name + ".csv");
    }

    @Test
    void csvServiceHeadersOnly() {
        String header1 = "greeting";
        String header2 = "object";
        String headers = header1 + "," + header2 + "\n";

        String name = "test";
        CsvTestFile original = new CsvTestFile(name, headers);

        CsvService csvService = CsvService.getInstance();
        List<String> indexes = csvService.put(original);
        assertThat(indexes.size()).isEqualTo(0);
    }

    @Test
    void csvServiceEmptyFile() {
        CsvTestFile original = new CsvTestFile("testFile", "");
        CsvService csvService = CsvService.getInstance();
        List<String> indexes = csvService.put(original);
        assertThat(indexes.size()).isEqualTo(0);
    }

    @Test
    void csvServiceMultipleLabels() {
        String header1 = "greeting";
        String header2 = "object";
        String headers = header1 + "," + header2 + "\n";

        String row1col1 = "hello";
        String row1col2 = "world";
        String row1cols = row1col1 + "," + row1col2 + "\n";

        String row2col1 = "hola";
        String row2col2 = "mundo";
        String row2cols = row2col1 + "," + row2col2 + "\n";

        String contents = headers + row1cols + row2cols;
        String name = "test";
        String filename1 = name + "_" + row1col1;
        String filename2 = name + "_" + row2col1;
        CsvTestFile original = new CsvTestFile(name, contents);

        CsvService csvService = CsvService.getInstance();
        List<String> indexes = csvService.put(original);
        assertThat(indexes).hasSize(2);

        CsvFile copy1 = csvService.get(indexes.get(0));
        CsvFile copy2 = csvService.get(indexes.get(1));

        byte[] copy1Bytes = new byte[0];
        byte[] copy2Bytes = new byte[0];
        try {
            copy1Bytes = copy1.getInputStream().readAllBytes();
            copy2Bytes = copy2.getInputStream().readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String copy1Contents = new String(copy1Bytes);
        String copy2Contents = new String(copy2Bytes);
        assertThat(copy1Contents).isEqualTo(row1cols);
        assertThat(copy2Contents).isEqualTo(row2cols);

        assertThat(copy1.length()).isEqualTo(row1cols.length());
        assertThat(copy2.length()).isEqualTo(row2cols.length());

        assertThat(filename1).isEqualTo(copy1.getName());
        assertThat(filename2).isEqualTo(copy2.getName());

        assertThat(copy1.getColumns()).containsExactly(header1, header2);
        assertThat(copy2.getColumns()).containsExactly(header1, header2);

        assertThat(copy1.getType()).isEqualTo(header1);
        assertThat(copy2.getType()).isEqualTo(header1);

        assertThat(copy1.getLabel()).isEqualTo(row1col1);
        assertThat(copy2.getLabel()).isEqualTo(row2col1);
    }
}

class CsvTestFile implements MultipartFile {
    private final String name;
    private String contents;

    CsvTestFile(String name) {
        this.name = name;
    }

    public void setHeaders(List<String> headers) {
        contents = "";
        for (int i = 0; i < headers.size(); ++i) {
            if (i == 0) {
                contents += headers.get(i);
            } else {
                contents = contents.concat("," + headers.get(i));
            }
        }
        contents += "\n";
    }

    public void addRow(List<String> elements) {
        for (int i = 0; i < elements.size(); ++i) {
            if (i == 0) {
                contents += elements.get(i);
            } else {
                contents = contents.concat("," + elements.get(i));
            }
        }
        contents += "\n";
    }

    CsvTestFile(String name, String contents) {
        this.name = name;
        this.contents = contents == null ? "" : contents;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    @NonNull
    public String getOriginalFilename() {
        return name + ".csv";
    }

    @Override
    public String getContentType() {
        return "text/csv";
    }

    @Override
    public boolean isEmpty() {
        return getSize() == 0;
    }

    @Override
    public long getSize() {
        return getBytes().length;
    }

    @Override
    @NonNull
    public byte[] getBytes() {
        return contents.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    @NonNull
    public InputStream getInputStream() {
        return IOUtils.toInputStream(contents, StandardCharsets.UTF_8);
    }

    @Override
    public void transferTo(@NonNull File dest) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }
}
