package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/management")
public class ManagementCost {

	private final String DIR_ROOT = "managementCost";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//기초상품 원가표 - 화면
	@RequestMapping(value = "/cost_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("management/cost_list.pq");
		return mv;
	}

	//기초상품 원가표 - 리스트
	@RequestMapping(value = "/cost_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		Map resultMap = new HashMap();

		map.put("orderBy", "code1Name,codename2");
		Common.PQmap(map, request);
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//기초상품 원가표 - 엑셀다운로드
	@RequestMapping(value = "/costExcelDown.run")
	public void downExcel(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> subQuery = new HashMap<String, String>();
		List<String> dbField = Arrays.asList("id", "barcodeCompletion", "code1Name", "code2Name", "codename2", "name", "code4Name", "rrp", "cost", "cdate", "wdate","realStock","pbrand");
		List<String> cellName = Arrays.asList("id", "바코드", "브랜드", "카테고리", "품목코드", "품목명", "옵션", "RRP", "원가", "최초등록일", "최근수정일","실제재고","노출여부");
		try {
			excelCon.downExcelFile(response, dbConn,QUERY_ROOT + ".listExcelDown", subQuery, dbField, cellName);
		} catch (IOException e) {
		}
	}
}
