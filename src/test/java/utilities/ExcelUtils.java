package utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    // Read data from Excel file
    public static String[][] readData(String filePath, String sheetName) throws Exception {

        FileInputStream fis = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        int totalRows = sheet.getLastRowNum();
        int totalCols = sheet.getRow(0).getLastCellNum();

        String[][] data = new String[totalRows][totalCols];

        for (int i = 1; i <= totalRows; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < totalCols; j++) {
                Cell cell = row.getCell(j);
                data[i - 1][j] = cell.toString();
            }
        }

        workbook.close();
        fis.close();
        return data;
    }

    // Write data to Excel file
    public static void writeData(String filePath, String sheetName, List<String> dataList) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // Create header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Sr No");
        header.createCell(1).setCellValue("Data");

        // Write data rows
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(dataList.get(i));
        }

        FileOutputStream fos = new FileOutputStream(new File(filePath));
        workbook.write(fos);
        workbook.close();
        fos.close();

        System.out.println("Data written to Excel successfully: " + filePath);
    }

    // Write sports data to Excel — Name, Date, Price
    public static void writeSportsData(String filePath, String sheetName, List<String[]> dataList) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // Create header row
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Sport Name");
        header.createCell(1).setCellValue("Date");
        header.createCell(2).setCellValue("Price");

        // Write data rows
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(dataList.get(i)[0]);
            row.createCell(1).setCellValue(dataList.get(i)[1]);
            row.createCell(2).setCellValue(dataList.get(i)[2]);
        }

        FileOutputStream fos = new FileOutputStream(new File(filePath));
        workbook.write(fos);
        workbook.close();
        fos.close();

        System.out.println("Sports data written to Excel: " + filePath);
    }
}