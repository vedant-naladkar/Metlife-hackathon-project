package Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	private static ExcelReader instance;
	private String filePath;
	private Workbook workbook;
	private static final Log log = Log.getLogger(ExcelReader.class);

	private ExcelReader() {
		filePath = System.getProperty("user.dir") + File.separator + "Data" + File.separator
				+ ConfigReader.getProperty("DataFileName") + ".xlsx";
		try {
			FileInputStream fis = new FileInputStream(filePath);
			workbook = new XSSFWorkbook(fis);
			fis.close();
		} catch (IOException e) {
			throw new RuntimeException("Failed to load Excel file: " + ConfigReader.getProperty("DataFileName") + ".xlsx", e);
		}
	}

	/**
	 * Singleton instance getter
	 */
	public static ExcelReader getInstance() {
		if (instance == null) {
			instance = new ExcelReader();
		}
		return instance;
	}

	/**
	 * Get cell data by SheetName, TestID, ColumnName.
	 */
	public String getCellData(String sheetName, String testID, String columnName) {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null)
			throw new RuntimeException("Sheet not found: " + sheetName);

		// Find header row (assume first row contains column names)
		Row headerRow = sheet.getRow(0);
		Map<String, Integer> colMap = new HashMap<>();
		for (Cell cell : headerRow) {
			colMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
		}

		Integer colIndex = colMap.get(columnName);
		if (colIndex == null)
			throw new RuntimeException("Column not found: " + columnName);

		// Find row with given testID (assume TestID is in first column)
		Iterator<Row> iterator = sheet.iterator();
		iterator.next(); // skip header
		while (iterator.hasNext()) {
			Row row = iterator.next();
			Cell idCell = row.getCell(0);
			if (idCell != null && idCell.getStringCellValue().trim().equalsIgnoreCase(testID)) {
				Cell targetCell = row.getCell(colIndex);
				return targetCell != null ? targetCell.getStringCellValue() : "";
			}
		}
		throw new RuntimeException("TestID not found: " + testID);
	}

	/**
	 * Set cell data by SheetName, TestID, ColumnName, and newValue.
	 */
	public void setCellData(String sheetName, String testID, String columnName, String newValue) {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null)
			throw new RuntimeException("Sheet not found: " + sheetName);

		Row headerRow = sheet.getRow(0);
		Map<String, Integer> colMap = new HashMap<>();
		for (Cell cell : headerRow) {
			colMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
		}

		Integer colIndex = colMap.get(columnName);
		if (colIndex == null)
			throw new RuntimeException("Column not found: " + columnName);

		Iterator<Row> iterator = sheet.iterator();
		iterator.next(); // skip header
		while (iterator.hasNext()) {
			Row row = iterator.next();
			Cell idCell = row.getCell(0);
			if (idCell != null && idCell.getStringCellValue().trim().equalsIgnoreCase(testID)) {
				Cell targetCell = row.getCell(colIndex);
				if (targetCell == null)
					targetCell = row.createCell(colIndex);
				targetCell.setCellValue(newValue);
				log.info("'" + newValue + "' has been set successfully for '" + testID + "' at '" + columnName
						+ "' column");
				try (FileOutputStream fos = new FileOutputStream(filePath)) {
					workbook.write(fos);
				} catch (IOException e) {
					throw new RuntimeException("Failed to write data to Excel file", e);
				}
				return;
			}
		}
		throw new RuntimeException("TestID not found: " + testID);
	}

	/**
	 * Close workbook
	 */
	public void close() {
		try {
			if (workbook != null)
				workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a list of TestIDs from the given sheet where Execute column is "YES"
	 * Preserves the insertion order.
	 *
	 * @param sheetName Excel sheet name
	 * @return List of TestIDs with Execute="YES"
	 */
	public List<String> getExecutableTestIDs() {
		List<String> testIDs = new ArrayList<>();
		String sheetName = "TestCases";
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null)
			throw new RuntimeException("Sheet not found: " + sheetName);

		Row headerRow = sheet.getRow(0);
		int testIDCol = -1;
		int executeCol = -1;

		// Map column indexes
		for (Cell cell : headerRow) {
			String colName = cell.getStringCellValue().trim();
			if (colName.equalsIgnoreCase("TestID"))
				testIDCol = cell.getColumnIndex();
			if (colName.equalsIgnoreCase("Execute"))
				executeCol = cell.getColumnIndex();
		}

		if (testIDCol == -1 || executeCol == -1) {
			throw new RuntimeException("TestID or Execute column not found in sheet: " + sheetName);
		}

		// Iterate rows and add TestID if Execute = YES
		Iterator<Row> rowIterator = sheet.iterator();
		rowIterator.next(); // skip header
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Cell executeCell = row.getCell(executeCol);
			if (executeCell != null && executeCell.getStringCellValue().trim().equalsIgnoreCase("YES")) {
				Cell testIDCell = row.getCell(testIDCol);
				if (testIDCell != null) {
					testIDs.add(testIDCell.getStringCellValue().trim());
					log.info("Added TestCase for Execution = " + testIDCell.getStringCellValue().trim());
				}
			}
		}

		return testIDs;
	}

}
