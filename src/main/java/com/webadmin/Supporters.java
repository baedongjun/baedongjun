package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/supporters")
public class Supporters {
	private final String DIR_ROOT = "supporters";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;

	// 모집관리
	@RequestMapping(value = "/ini_list")
	public ModelAndView iniList(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("supporters_type", Common.defaultValue(map.get("supporters_type"), "1"));
		map.put("nowDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("supporters/ini_list.pq");
		return mv;
	}

	// 모집관리 - 리스트 json
	@RequestMapping(value = "/ini_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> iniList(HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("divide_supporters", request.getParameterValues("supporters_type"), "="));
		sqlItemList.add(dbConn.makeSearchSql("title", request.getParameterValues("title"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("description", request.getParameterValues("description"), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "rec_sdate desc, rec_edate");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".iniList", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// CS->MD 긴급요청 - 리스트 json
	@RequestMapping(value = "/send_sms_insert_DB.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> sendSmsInsert_DB(HttpServletRequest request) {
		Map<String,String> map = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		dbConn.recordSet(QUERY_ROOT + ".sendSmsInsert_DB", map);
		return resultMap;
	}

	//서포터즈 정보
	@RequestMapping(value = "/supporters_view")
	public ModelAndView supportersView(ModelAndView mv, HttpServletRequest request){
		Map<String,String> param = Common.paramToMap(request.getParameterMap()); //id
		mv.addObject("view",Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT+".view",param)));
		mv.setViewName("supporters/supporters_view.tiles");
		return mv;
	}

	//서포터즈 정보 수정
	@RequestMapping(value = "/supporters_update.run", method = {RequestMethod.POST}, produces = "application/json")
	public String supportersUpdate(HttpServletRequest request){
		Map<String,String> param = Common.paramToMap(request.getParameterMap());
		param.put("Mobile",Common.addString(param.get("mobile1"),"-",param.get("mobile2"),"-",param.get("mobile3")));
		param.put("blogurl",param.get("blogurl").replace("www.","").replace("http://",""));
		param.put("getDate",Common.nowDate().substring(0,10));
		dbConn.recordSet(QUERY_ROOT+".update",param);

		return "redirect:/views/supporters/cs/qanda_list";
	}
}
