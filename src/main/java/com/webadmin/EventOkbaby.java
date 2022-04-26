package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/event/okbaby")
public class EventOkbaby {
	private String DIR_ROOT = "eventOkbaby";
	private String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	@RequestMapping(value = "/changeUp")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();

		String yearV = Common.defaultValue(request.getParameter("yearV"), "");
		if (Common.isNullOrEmpty(yearV)) {
			yearV = Common.nowDate().substring(0, 4);
		}

		map.put("yearV", yearV);

		mv.addObject("result", Common.nullToEmpty(dbConn.recordSet(QUERY_ROOT + ".result", map)));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("event/okbaby/changeUp.pq");
		return mv;
	}

	@RequestMapping(value = "changeUp.run")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();

		map.put("yearV", request.getParameter("yearV"));

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".list", map);

		Map<String, Object> returnParam = new HashMap<>();
		Common.PQresultMap(returnParam, map.get("curPage"), list);
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}

	@RequestMapping(value = "/changeUp_2022")
	public ModelAndView list_2022(ModelAndView mv, HttpServletRequest request) {
		mv.setViewName("event/okbaby/changeUp_2022.pq");
		return mv;
	}

	@RequestMapping(value = "changeUp_2022.run")
	@ResponseBody
	public Map<String, Object> list_2022(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".list_2022", map);
		Map<String, Object> returnParam = new HashMap<>();
		returnParam.put("data", Common.nullToEmpty(list));
		return returnParam;
	}

	//ÀÏ°ýµî·Ï
	@RequestMapping(value = "/addUserInfo.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView addUserInfo(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = new HashMap<>();
		map.put("admin_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
		try {
			List<String> dbField = Arrays.asList("no","timestamp","username", "mobile", "market", "goods_name", "jumuncode", "etc", "userid");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("addFile"), dbField, "NO.");
			Map<String, Object> totalMap = new HashMap<>();
			totalMap.put("param", map);
			totalMap.put("list", list);
			dbConn.recordSet(QUERY_ROOT + ".insert_2022", totalMap);
		} catch (IOException e) {}
		mv.setViewName("redirect:/views/event/okbaby/changeUp_2022");
		return mv;
	}

}
