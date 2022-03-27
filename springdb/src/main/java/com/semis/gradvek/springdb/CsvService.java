package com.semis.gradvek.springdb;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class CsvService {
    public class SimpleFile {
        public SimpleFile(String name, String contents) {
            this.name = name;
            this.contents = contents;
        }

        public String name;
        public String contents;
    }

    public SimpleFile save(MultipartFile file) {
        String contents = null;
        try {
            contents = new BufferedReader(new InputStreamReader(file.getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        SimpleFile sf = new SimpleFile(file.getOriginalFilename(), contents);
        return sf;
    }

//    public void test_save(MultipartFile file) {
//        Resource resource = new ClassPathResource("public/" + file.getOriginalFilename());
//        File file = new File(classLoader.getResource(".").getFile() + "/test.xml");
//        if (file.createNewFile()) {
//            System.out.println("File is created!");
//        } else {
//            System.out.println("File already exists.");
//        }
//    }
}
