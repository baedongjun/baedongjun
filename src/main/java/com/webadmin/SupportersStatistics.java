package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/supporters/statistics")
public class SupportersStatistics {

	private final String DIR_ROOT = "supportersStatistics";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	//ü�� �귣�庰 ��Ȳ
	@RequestMapping(value = "/simsa_brand_statistics")
	public ModelAndView simsa_brand_statistics(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();

		map.put("searchDate", Common.addString(Common.defaultValue(request.getParameter("yearV"), Common.nowDate().substring(0, 4)), "-", Common.selectZero(Integer.parseInt(Common.defaultValue(request.getParameter("monthV"), Common.nowDate().substring(5, 7))), "00")));
		map.put("divide_supporters", Common.defaultValue(request.getParameter("divide_supporters"), "1"));

		mv.addObject("list", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".simsa_brand_statistics", map)));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("supporters/statistics/simsa_brand_statistics.tiles");
		return mv;
	}


	//ü�� ��ǰ�� ��Ȳ
	@RequestMapping(value = "/simsa_pack_statistics")
	public ModelAndView simsa_pack_statistics(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();

		map.put("searchDate", Common.addString(Common.defaultValue(request.getParameter("yearV"), Common.nowDate().substring(0, 4)), "-", Common.selectZero(Integer.parseInt(Common.defaultValue(request.getParameter("monthV"), Common.nowDate().substring(5, 7))), "00")));
		map.put("divide_supporters", Common.defaultValue(request.getParameter("divide_supporters"), "1"));

		mv.addObject("list", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".simsa_pack_statistics", map)));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));

		mv.setViewName("supporters/statistics/simsa_pack_statistics.tiles");
		return mv;
	}


	//�� �� �̺�Ʈ ��Ȳ
	@RequestMapping(value = "/nosimsa_statistics")
	public ModelAndView nosimsa_statistics(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();

		map.put("searchDate", Common.addString(Common.defaultValue(request.getParameter("yearV"), Common.nowDate().substring(0, 4)), "-", Common.selectZero(Integer.parseInt(Common.defaultValue(request.getParameter("monthV"), Common.nowDate().substring(5, 7))), "00")));
		map.put("divide_supporters", Common.defaultValue(request.getParameter("divide_supporters"), "1"));

		mv.addObject("list", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".nosimsa_statistics", map)));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));

		mv.setViewName("supporters/statistics/nosimsa_statistics.tiles");
		return mv;
	}

}
