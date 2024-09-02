package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirFiles extends SimpleFileVisitor<Path> {
    public static String USERDATE = "2024-05-29";

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
       if (file.toString().endsWith(".json") & file.getParent().endsWith("request")) {
          readWriteJson(file);
//           System.out.println(file.toAbsolutePath());
//           System.out.println(file.getParent());
       }
        return FileVisitResult.CONTINUE;
    }
    public static void readWriteJson(Path file){
        System.out.println(file.toAbsolutePath());
        System.out.println(file.getParent());
//        String origFile = file.toAbsolutePath().toString();
        Path tempFile = Paths.get("src/test/temp/temp");
        try {
            String orig = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);

            Pattern compile;
            Matcher matcher;
            String group;
            String ptrn;

            ptrn = "\"loanRequestExtId\" ?: \".*\"";
            compile = Pattern.compile(ptrn);
            matcher = compile.matcher(orig);
            while (matcher.find()) {
                group = matcher.group();
                orig = orig.replace(group, "\"loanRequestExtId\": \"[customerRequestExtId]\"");
            }

            ptrn = "\"loanRequestId\" ?: \".*\"";
            compile = Pattern.compile(ptrn);
            matcher = compile.matcher(orig);
            while (matcher.find()) {
                group = matcher.group();
                orig = orig.replace(group, "\"loanRequestId\": \"[loanRequestId]\"");
            }

            ptrn = "\"customerRequestExtId\" ?: \".*\"";
            compile = Pattern.compile(ptrn);
            matcher = compile.matcher(orig);
            while (matcher.find()) {
                group = matcher.group();
                orig = orig.replace(group, "\"customerRequestExtId\": \"[customerRequestExtId]\"");
            }

            ptrn = "\"customerRequestId\" ?: \".*\"";
            compile = Pattern.compile(ptrn);
            matcher = compile.matcher(orig);
            while (matcher.find()) {
                group = matcher.group();
                orig = orig.replace(group, "\"customerRequestId\": \"[customerRequestId]\"");
            }

            String datePattern = "\\d{4}-\\d{2}-\\d{2}"; // "yyyy-MM-dd"
            compile = Pattern.compile(datePattern);
            matcher = compile.matcher(orig);

            while (matcher.find()) {
                String dateStr = matcher.group();
                orig = orig.replace(dateStr, "[today" + date(dateStr) + "]");
            }

            Files.write(tempFile, orig.getBytes(StandardCharsets.UTF_8));

            try {
                Files.copy(tempFile.toAbsolutePath(),
                        file.toAbsolutePath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e){
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String date(String dateOld) {
        String manyYears = "1000";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(dateOld, formatter);
        LocalDate endDate = LocalDate.parse(USERDATE);
        Period period = Period.between(startDate, endDate);
        String paramDate = "";

        if (period.getYears()==0) {}
        else if (period.getYears()<-1000)
            paramDate += "+"+ manyYears + "y";
        else if (period.getYears() < 0)
            paramDate += "+"+ period.getYears()*(-1) + "y";
        else
            paramDate += "-"+ period.getYears()+ "y";

        if (period.getMonths()==0) {}
        else if (period.getMonths() <0)
            paramDate += "+"+ period.getMonths()*(-1) + "m";
        else
            paramDate += "-"+ period.getMonths()+ "m";

        if (period.getDays() <0)
            paramDate += "+"+ period.getDays()*(-1) + "d";
        else
            paramDate += "-"+ period.getDays() + "d";

        return paramDate;
    }
}
