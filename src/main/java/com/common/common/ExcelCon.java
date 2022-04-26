package com.common.common;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


@Controller
public class ExcelCon {

	@Resource
	private CommonsMultipartResolver multipartResolver;

	public List uploadExcel(List<MultipartFile> multipartFile, List<String> dbField, String title) throws IOException {
		List<HashMap<String, String>> list = new ArrayList<>();

		for (MultipartFile mf : multipartFile) {
			if (multipartResolver.getFileUpload().getFileSizeMax() >= mf.getSize() && mf.getSize() > 0) {
				if (Common.fileExtValidate(FilenameUtils.getExtension(mf.getOriginalFilename()), "xls")) {
					Workbook wb = WorkbookFactory.create(mf.getInputStream());
					Sheet sheet = wb.getSheetAt(0);
					if (!Common.isNullOrEmpty(title) && sheet.getRow(0).getCell(0).getStringCellValue().equals(title)) sheet.removeRow(sheet.getRow(0));
					for (Row row : sheet) {
						HashMap<String, String> map = new HashMap<>();
						for (Cell cell : row) {
							map.put(dbField.get(cell.getColumnIndex()), getCellValue(cell));
						}
						list.add(map);
					}
					wb.close();
				}
			}
		}

		return list;
	}
	private String getCellValue(Cell cell) {
		switch (cell.getCellType()) {
			case NUMERIC:
				if(DateUtil.isCellDateFormatted(cell)){
					Date date = cell.getDateCellValue();
					return String.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(date));
				}else{
					NumberFormat f = NumberFormat.getInstance();
					f.setGroupingUsed(false);  //지수로 안나오게
					f.setMaximumFractionDigits(0);
					return String.valueOf(f.format(cell.getNumericCellValue()));
				}
			case STRING:
				return String.valueOf(cell.getStringCellValue());
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			default:
				return "";
		}
	}

	public void downExcelFile(HttpServletResponse response, DbConn dbconn, String queryString, Map<String, String> subQuery, List<String> dbField, List<String> cellName)
		throws IOException {
		SXSSFWorkbook wb = new SXSSFWorkbook(1000);

		SXSSFSheet sheet = wb.createSheet("Result Download");

		CellStyle headStyle = wb.createCellStyle();
		headStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
		headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headStyle.setAlignment(HorizontalAlignment.CENTER);

		Row row = sheet.createRow(0); //엑셀 타이틀
		for (String title : cellName) {
			Cell cell = row.createCell(cellName.indexOf(title));
			cell.setCellStyle(headStyle);
			cell.setCellValue(title);
		}

		List<Map<String, Object>> adoRs = dbconn.recordSet(queryString, subQuery);
		int i = 1;
		for (Map<String, Object> rsField : adoRs) {
			row = sheet.createRow(i);
			for (String field : dbField) {
				Cell cell = row.createCell(dbField.indexOf(field));
				cell.setCellValue(String.valueOf((Common.isNullOrEmpty(rsField.get(field)) ? "" : rsField.get(field))));
			}
			i++;
		}

		response.setContentType("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Set-Cookie", "fileDownload=true;path=/");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + Common.makeFileName("") + ".xlsx\""));
		wb.write(response.getOutputStream());
		wb.dispose();
	}
}