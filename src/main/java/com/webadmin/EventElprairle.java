package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("views/event/elprairle")
public class EventElprairle {
	private String DIR_ROOT = "eventElprairle";
	private String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//list
	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv) {
		mv.setViewName("event/elprairle/list.pq");
		return mv;
	}

	@RequestMapping(value = "list.run")
	@ResponseBody
	public Map<String, Object> list(HttpServletResponse response) {

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".list");

		Map<String, Object> returnParam = new HashMap<>();
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}

	//view
	@RequestMapping(value = "/view")
	public ModelAndView view(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		mv.addObject("id", map.get("id"));
		mv.setViewName("event/elprairle/view.pq");
		return mv;
	}

	@RequestMapping(value = "view.run")
	@ResponseBody
	public Map<String, Object> view(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".view", map);

		String iniId = "userid";
		Map resultSub = Common.subQuery(list,iniId);

		String[] resultSub2 = resultSub.get("userid").toString().split(",");

		resultSub2 = new HashSet<String>(Arrays.asList(resultSub2)).toArray(new String[0]);
		List<Map<String,String>> listMap = new ArrayList<>();
		for (int i=0; i<resultSub2.length; i++){
			String userid = resultSub2[i];
			listMap.add(new HashMap(){{
				put("userid",userid);
			}});
		}

		List<Map> listSub = new ArrayList<>();
		listSub = dbConn.recordSet(QUERY_ROOT+".gift",listMap);

		Map<String, Object> returnParam = new HashMap<>();
		returnParam.put("data", Common.nullToEmpty(Common.combineRecordSet(iniId,list,listSub)));

		return returnParam;
	}

	@RequestMapping(value = "/elprairleExcelDown.run")
	public void downExcel(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> subQuery = Common.paramToMap(request.getParameterMap());

		List<String> dbField = Arrays.asList("userid", "itemName", "cnt", "wdate", "money");
		List<String> cellName = Arrays.asList("쁘띠엘린 아이디", "제품주문내역", "구매갯수", "주문일자", "실결제액");
		try {
			excelCon.downExcelFile(response, dbConn, QUERY_ROOT + ".view", subQuery, dbField, cellName);
		} catch (IOException e) {
		}
	}
}
