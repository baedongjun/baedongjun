package com.common.common;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/")
public class CommonProcess {

	@Resource(name = "dbConn")
	private DbConn dbConn;

	@Resource(name = "dbConn2")
	private DbConn dbConn2;


	@RequestMapping(value = "/")
	public ModelAndView startPage(ModelAndView mv) {
		mv.setViewName("login.bare");
		return mv;
	}

	@RequestMapping(value = "/index")
	public ModelAndView index(ModelAndView mv) {
		mv.setViewName("index.tiles");
		return mv;
	}

	@RequestMapping(value = "/login")
	public ModelAndView login(ModelAndView mv) {
		mv.setViewName("login.bare");
		return mv;
	}

	@RequestMapping(value = "/searchZIP")
	public ModelAndView zipSearch(ModelAndView mv) {
		mv.setViewName("common/searchZIP.bare");
		return mv;
	}




	//기초코드 브랜드별 품목
	@RequestMapping(value = "/code2.run")
	@ResponseBody
	public Map<String, Object> code2(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();

		resultMap.put("code2",dbConn.recordSet("common.query.code2", new HashMap(){{
			put("code1",request.getParameter("code1"));
		}}));

		return resultMap;
	}

	//기초코드 브랜드별 하위 항목
	@RequestMapping(value = "/code.run")
	@ResponseBody
	public Map<String, Object> code(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();

		Map map = Common.paramToMap(request.getParameterMap());
		resultMap.put("code", dbConn.recordSet("common.query.code" + (map.size()+1), map));

		return resultMap;
	}

	//SITE child 카테고리
	@RequestMapping(value = "/categoryName.run")
	@ResponseBody
	public Map<String, Object> categoryName(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();

		resultMap.put("categoryName",dbConn.recordSet("common.query.categoryName", new HashMap(){{
			put("categorySite",request.getParameter("categorySite"));
		}}));

		return resultMap;
	}

	@RequestMapping(value = "/multiselectTest")
	public ModelAndView multiselectTest(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("multiselectTest.tiles");
		return mv;
	}
}