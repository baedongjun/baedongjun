package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import java.util.ArrayList;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/subsidiary_materials")
public class SubsidiaryMaterialsCompany {

	private final String DIR_ROOT = "subsidiary_materialsCompany";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;

	//업체관리 - 화면
	@RequestMapping(value = "/company")
	public ModelAndView company(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("subsidiary_materials/company.pq");
		return mv;
	}

	//업체관리 - 리스트
	@RequestMapping(value = "/company.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> company(HttpServletRequest request, HttpServletResponse response) {

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("person", request.getParameterValues("person"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("brand", request.getParameterValues(Common.paramIsArray("MD_brand", request)), "like"));

		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//업체관리 - 등록 화면
	@RequestMapping(value = "/company_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".view", Common.paramToMap(request.getParameterMap())));
		mv.setViewName("subsidiary_materials/company_input.tiles");
		return mv;
	}

	//업체관리 - 등록
	@RequestMapping(value = "/company_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (!Common.isNullOrEmpty(map.get("id"))) {
			dbConn2.recordSet(QUERY_ROOT + ".update", map);
		} else {
			dbConn2.recordSet(QUERY_ROOT + ".insert", map);
		}

		mv.setViewName("redirect:/views/subsidiary_materials/company");
		return mv;
	}
}
