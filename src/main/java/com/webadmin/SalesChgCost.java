package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/sales")
public class SalesChgCost {

	private final String DIR_ROOT = "salesChgCost";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//지정 원가 관리 - 화면
	@RequestMapping(value = "/chgCost_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("sales/chgCost_list.pq");
		return mv;
	}

	//지정 원가 관리 - 리스트
	@RequestMapping(value = "/chgCost_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("code1", request.getParameterValues("codeGroup"), "="));
		sqlItemList.add(dbConn.makeSearchSql("yearValue", request.getParameterValues("yearValue"), "="));
		sqlItemList.add(dbConn.makeSearchSql("monthValue", request.getParameterValues("monthValue"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "code1Name, code2Name");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//지정 원가 관리 - 등록
	@RequestMapping(value = "/insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

		try {
			List<String> dbField = Arrays.asList("file1", "file2", "file3", "file4", "file5", "file6", "file7", "file8", "file9", "file10", "file11", "file12", "file13", "file14", "file15");
			List<Map<String, String>> addValue = excelCon.uploadExcel(mr.getFiles("excels"), dbField, null);
			List<Map<String, String>> result = dbConn.recordSet(QUERY_ROOT + ".list2", map);
			for (int i = 0; i < addValue.size(); i++) {
				String file3 = addValue.get(i).get("file3");
				String file6 = addValue.get(i).get("file6");
				if (!Common.isNullOrEmpty(file3) && !("재고단가".equals(file6))) {
					result.add(new HashMap() {{
						put("yearValue", map.get("yearValue"));
						put("monthValue", map.get("monthValue"));
						put("codename2", file3);
						put("cost", file6);
					}});
				}
			}

			dbConn.recordSet(QUERY_ROOT + ".delete", map);
			dbConn.recordSet(QUERY_ROOT + ".insert", result);
		} catch (IOException e) {
		}

		mv.setViewName("redirect:/views/sales/chgCost_list");
		return mv;
	}
}
