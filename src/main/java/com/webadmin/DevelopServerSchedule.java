package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
import java.io.IOException;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/develop")
public class DevelopServerSchedule {

	private final String DIR_ROOT = "developServerSchedule";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;

	//서버 스케줄 - 화면
	@RequestMapping(value = "/server_schedule_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("develop/server_schedule_list.pq");
		return mv;
	}

	//서버 스케줄 - 리스트
	@RequestMapping(value = "/server_schedule_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("server_gubun", request.getParameterValues(Common.paramIsArray("server_gubun", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("schedule_use", request.getParameterValues(Common.paramIsArray("schedule_use", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("schedule_program", request.getParameterValues(Common.paramIsArray("schedule_program", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("schedule_type", request.getParameterValues(Common.paramIsArray("schedule_type", request)), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".list", map)));

		return resultMap;
	}

	//서버 스케줄 - 등록 화면
	@RequestMapping(value = "/server_schedule_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".view", map));
		mv.addObject("returnParam", map);
		mv.setViewName("develop/server_schedule_input.tiles");
		return mv;
	}

	//서버 스케줄 - 등록
	@RequestMapping(value = "/server_schedule_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("uploadfile", filename);
			}
		} catch (IOException e) {
		}

		map.put("schedule_repeat", "day".equals(map.get("schedule_type")) ? map.get("schedule_repeat_day") : "week".equals(map.get("schedule_type")) ? map.get("schedule_repeat_week") : null);
		map.put("schedule_datename", "day".equals(map.get("schedule_type")) ? null : "week".equals(map.get("schedule_type")) ? map.get("schedule_datename_week") : map.get("schedule_datename_month"));
		map.put("schedule_end", "".equals(map.get("schedule_edate")) ? "n" : "y");

		dbConn2.recordSet(QUERY_ROOT + ".insert", map);
		if ("list".equals(map.get("gubun"))) {
			mv.setViewName("redirect:/views/develop/server_schedule_list?schedule_type=" + map.get("schedule_type"));
		} else {
			mv.setViewName("redirect:/views/develop/server_schedule");
		}
		return mv;
	}

	//서버 스케줄 - 수정
	@RequestMapping(value = "/server_schedule_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("uploadfile", filename);
			}
		} catch (IOException e) {
		}

		map.put("schedule_repeat", "day".equals(map.get("schedule_type")) ? map.get("schedule_repeat_day") : "week".equals(map.get("schedule_type")) ? map.get("schedule_repeat_week") : null);
		map.put("schedule_datename", "day".equals(map.get("schedule_type")) ? null : "week".equals(map.get("schedule_type")) ? map.get("schedule_datename_week") : map.get("schedule_datename_month"));
		map.put("schedule_end", "".equals(map.get("schedule_edate")) ? "n" : "y");

		dbConn2.recordSet(QUERY_ROOT + ".update", map);
		if ("list".equals(map.get("gubun"))) {
			mv.setViewName("redirect:/views/develop/server_schedule_list?schedule_type=" + map.get("schedule_type"));
		} else {
			mv.addObject("close", "y");
			mv.setViewName("redirect:/views/develop/server_schedule");
		}

		return mv;
	}

	//서버 스케줄 - 삭제
	@RequestMapping(value = "/server_schedule_delete.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView deleteDB(ModelAndView mv, HttpServletRequest request) {
		String[] selectField = new String[]{"chkId"};
		List<Map<String, String>> listMap = Common.paramToList(selectField, request.getParameterMap());
		dbConn2.recordSet(QUERY_ROOT + ".delete", listMap);
		mv.setViewName("redirect:/views/develop/server_schedule_list");
		return mv;
	}

	//서버 스케줄(정기) - 화면
	@RequestMapping(value = "/server_schedule")
	public ModelAndView serverSchedule(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("searchParam2", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("develop/server_schedule.tiles");
		return mv;
	}


	//달력 조회
	@RequestMapping(value = "/calendar.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getCalendar(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".viewList", Common.paramToMap(request.getParameterMap()))));

		return resultMap;
	}

	//서버 스케줄정보 - 화면
	@RequestMapping(value = "/server_schedule_summary")
	public ModelAndView serverScheduleSummary(ModelAndView mv, HttpServletRequest request) {
		mv.setViewName("develop/server_schedule_summary.pq");
		return mv;
	}

	//서버 스케줄(정기) - 리스트
	@RequestMapping(value = "/server_schedule_summary.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> serverScheduleSummary(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		map.put("server_gubun", "121.125.70.36, 121.125.70.37, 121.125.70.39, 121.125.70.41, 121.125.70.40, 221.143.49.36, 221.143.49.57, 121.125.70.18, 121.125.70.16");
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".summaryList", map)));

		return resultMap;
	}
}
