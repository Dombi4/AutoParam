package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        Files.walkFileTree(Paths.get("src/main/resources"), new DirFiles());
    }
}