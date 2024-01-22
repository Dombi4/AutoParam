package org.example;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DirFiles extends SimpleFileVisitor<Path> {
    public static String USERDATE = "2023-10-10";
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException{
       if (file.toString().endsWith(".json") & file.getParent().endsWith("request")) {
          readWriteJson(file);
           System.out.println(file.toAbsolutePath());
           System.out.println(file.getParent());
       }
        return FileVisitResult.CONTINUE;
    }
    public static void readWriteJson(Path file){
        System.out.println(file.toAbsolutePath());
        System.out.println(file.getParent());
        String origFile = file.toAbsolutePath().toString();
        Path tempFile = Paths.get("src/test/temp/temp");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file.toAbsolutePath().toString()));
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile.toAbsolutePath().toString()));
            String orig;
            String newoig;
            String comma;
            int n = 0;
            int n1 = 0;
            int n2 = 0;
            int n3 = 0;
            while ((orig = br.readLine()) != null) {
                comma = orig.endsWith(",") ? "," : "";
                if (n<1 & orig.contains("\"loanRequestExtId\": \"")) {
                    int number = orig.indexOf(": \"");
                    newoig = orig.replace(orig.substring(number+3), "[customerRequestExtId]\"" + comma);
                    orig = newoig;
                    n++;
                }
                if (n1<1 & orig.contains("\"loanRequestId\": \"")) {
                    int number = orig.indexOf(": \"");
                    newoig = orig.replace(orig.substring(number+3), "[loanRequestId]\"" + comma);
                    orig = newoig;
                    n1++;
                }
                if (n2<1 & orig.contains("\"customerRequestExtId\": \"")) {
                    int number = orig.indexOf(": \"");
                    newoig = orig.replace(orig.substring(number+3), "[customerRequestExtId]\"" + comma);
                    orig = newoig;
                    n2++;
                }
                if (n3<1 & orig.contains("\"customerRequestId\": \"")) {
                    int number = orig.indexOf(": \"");
                    newoig = orig.replace(orig.substring(number+3), "[customerRequestId]\"" + comma);
                    orig = newoig;
                    n3++;
                }
                if (orig.contains("-")) {
                    int number = orig.indexOf(": \"");
                    char c;
                    char b;
                    char a;
                    if (number > 0) {
                        int num = number + 7;
                        try {
                            c = orig.charAt(num);
                            b = orig.charAt(num+3);
                        } catch (StringIndexOutOfBoundsException e){
                            c = ' ';
                            b = ' ';
                        }

                        if (c == '-' & b == '-') {
                            String substr = orig.substring(num - 4, num + 6);
                            newoig = orig.replace(substr, "[today"+ date(substr, "year") + date(substr, "month") + date(substr, "day") +"]");
                            //System.out.println(newoig);
                            orig = newoig;
                            //System.out.println(orig);
                        }
                    }
                }
                bw.write(orig + "\n");
            }
            br.close();
            bw.close();
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
    public static String date(String dateOld, String hron) {
        String manyYears = "1000";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(dateOld, formatter);
        LocalDate endDate = LocalDate.parse(USERDATE);
        Period period = Period.between(startDate, endDate);
        switch (hron){
            case "day":
                /*if (period.getDays()==0)
                    return "";*/
                if (period.getDays() <0)
                    return ("+"+ period.getDays()*(-1) + "d");
                return ("-"+ period.getDays() + "d");
            case "month":
                if (period.getMonths()==0)
                    return "";
                if (period.getMonths() <0)
                    return ("+"+ period.getMonths()*(-1) + "m");
                return ("-"+ period.getMonths()+ "m");
            case "year":
                if (period.getYears()==0)
                    return "";
                if (period.getYears()<-1000)
                    return ("+"+ manyYears + "y");
                if (period.getYears() <0)
                    return ("+"+ period.getYears()*(-1) + "y");
                return ("-"+ period.getYears()+ "y");
        }
        return "NON";
    }
}
