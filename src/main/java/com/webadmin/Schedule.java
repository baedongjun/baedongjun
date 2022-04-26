package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ImgCon;
import com.common.member.MemberDTO4;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
@RequestMapping("/views/schedule")
public class Schedule {

	private final String DIR_ROOT = "schedule";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource
	private ImgCon imgCon;


	//대표이사, 공용품 스케쥴 정보 가져오기
	@RequestMapping(value = "/calendar.run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getcalendar(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".calendar", Common.paramToMap(request.getParameterMap()))));

		return resultMap;
	}

	//대표이사, 공용품 삭제
	@RequestMapping(value = "/delete.run", method = RequestMethod.POST, produces = "application/json")
	@Transactional
	@ResponseBody
	public Map<String, Object> ceo_deleteRun(HttpServletRequest request, HttpServletResponse response) {
		dbConn2.recordSet(QUERY_ROOT + ".delete", (Common.paramToMap(request.getParameterMap()).get("id")));

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("resultCode", "0000");
		resultMap.put("resultMsg", "삭제 되었습니다.");

		return resultMap;
	}

	//대표이사, 공용품 신규
	@RequestMapping(value = "/insert.run", produces = "application/json")
	@Transactional
	@ResponseBody
	public Map<String, Object> ceo_updateinsert(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("writer", ((MemberDTO4) authentication.getPrincipal()).getUser_name());

		Map<String, Object> resultMap = new HashMap<>();
		//중복일정

		if (Integer.parseInt(((Map<String, Object>) dbConn2.recordSet(QUERY_ROOT + ".duplcnt", map).get(0)).get("cnt").toString()) > 0) {
			resultMap.put("resultCode", "0001");
			resultMap.put("resultMsg", "중복된 일정입니다.\n확인해주시기 바랍니다.");
		} else {
			dbConn2.recordSet(QUERY_ROOT + ".insert", map);
			resultMap.put("resultCode", "0000");
			resultMap.put("resultMsg", "등록 되었습니다.");
		}

		return resultMap;
	}

	//수정
	@RequestMapping(value = "/update.run", method = RequestMethod.POST, produces = "application/json")
	@Transactional
	@ResponseBody
	public Map<String, Object> ceo_update(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		dbConn2.recordSet(QUERY_ROOT + ".update", Common.paramToMap(request.getParameterMap()));
		resultMap.put("resultCode", "0000");
		resultMap.put("resultMsg", "수정 되었습니다.");

		return resultMap;
	}

	//전체수정
	@RequestMapping(value = "/update2.run", method = RequestMethod.POST, produces = "application/json")
	@Transactional
	@ResponseBody
	public Map<String, Object> ceo_update2(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("writer", ((MemberDTO4) authentication.getPrincipal()).getUser_name());

		Map<String, Object> resultMap = new HashMap<>();
		dbConn2.recordSet(QUERY_ROOT + ".update2", map);
		resultMap.put("resultCode", "0000");
		resultMap.put("resultMsg", "수정 되었습니다.");

		return resultMap;
	}

	//회의실
	@RequestMapping(value = "/discuss_calendar.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getDiscussCalendar(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".discuss_calendar", Common.paramToMap(request.getParameterMap()))));

		return resultMap;
	}

	//회의실 상세
	@RequestMapping(value = "/discuss_calendar_input")
	public ModelAndView getDiscuss(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (!Common.isNullOrEmpty(map.get("id"))) {
			mv.addObject("viewMap", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".discuss_view", map)).get(0));
		}
		mv.addObject("userGroupMap", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".discuss_group", map)));
		mv.addObject("returnParam", map);
		mv.setViewName("schedule/discuss_calendar_input.tiles");
		return mv;
	}

	//회의 신규, 수정, 취소
	@RequestMapping(value = "/discuss_calendar_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertRun(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String choice = map.get("choice");
		map.put("username", ((MemberDTO4) authentication.getPrincipal()).getUser_name());
		map.put("hdate", Common.defaultValue(map.get("hdate"), "0"));
		map.put("mdate", Common.defaultValue(map.get("mdate"), "0"));
		map.put("hdate2", Common.defaultValue(map.get("hdate2"), "0"));
		map.put("mdate2", Common.defaultValue(map.get("mdate2"), "0"));

		if ("insert".equals(choice)) {
			dbConn2.recordSet(QUERY_ROOT + ".discuss_insert", map);
		} else if ("modify".equals(choice)) {
			dbConn2.recordSet(QUERY_ROOT + ".discuss_modify", map);
		} else if ("cancel".equals(choice)) {                   //취소
			dbConn2.recordSet(QUERY_ROOT + ".discuss_cancel", map.get("id"));
		}
		mv.setViewName("redirect:/views/schedule/discuss_calendar");
		return mv;
	}


	//중복일정 체크
	@RequestMapping(value = "/discuss_check.run")
	@ResponseBody
	public Map<String, Object> getDiscussCheck(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("hdate", Optional.ofNullable(map.get("hdate")).orElse("0"));
		map.put("mdate", Optional.ofNullable(map.get("mdate")).orElse("0"));
		map.put("hdate2", Optional.ofNullable(map.get("hdate2")).orElse("0"));
		map.put("mdate2", Optional.ofNullable(map.get("mdate2")).orElse("0"));

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".discuss_duplcnt", map)).get(0));

		return resultMap;
	}

	//회의록 작성 관리-화면
	@RequestMapping(value = "/minutes_calendar")
	public ModelAndView getMinutesCalendar(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("comRootMap", dbConn.recordSet(QUERY_ROOT + ".comRoot", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("schedule/minutes_calendar.tiles");
		return mv;
	}

	//본부에 따른 부서 정보가져오기.
	@RequestMapping(value = "/com_dept.run", produces = "application/json")
	@ResponseBody
	public Map<String, Object> getComDept(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn.makeSearchSql("subj", request.getParameterValues(Common.paramIsArray("com_root", request)), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".comDept", map));

		return resultMap;
	}

	//회의록 작성 관리-달력
	@RequestMapping(value = "/minutes_calendar.run", produces = "application/json")
	@ResponseBody
	public Map<String, Object> getMinutesCalendar(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".minutesCalendar", Common.paramToMap(request.getParameterMap()))));

		return resultMap;
	}

	//회의록 작성 관리-상세
	@RequestMapping(value = "/minutes_calendar_input")
	public ModelAndView getMinutes(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (!Common.isNullOrEmpty(map.get("id"))) {
			mv.addObject("viewMap", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".minutesView", map)));
		}
		mv.addObject("userGroupMap", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".minutesGroup", map)));
		mv.addObject("returnParam", map);
		mv.setViewName("schedule/minutes_calendar_input.tiles");
		return mv;
	}

	//회의록 신규
	@RequestMapping(value = "/minutes_calendar_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView MinutesInsertRun(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("hdate", Common.defaultValue(map.get("hdate"), "0"));
		map.put("mdate", Common.defaultValue(map.get("mdate"), "0"));
		map.put("hdate2", Common.defaultValue(map.get("hdate2"), "0"));
		map.put("mdate2", Common.defaultValue(map.get("mdate2"),"0"));

		dbConn2.recordSet(QUERY_ROOT + ".minutesInsert", map);
		mv.setViewName("redirect:/views/schedule/minutes_calendar");
		return mv;
	}

	//회의록 신규
	@RequestMapping(value = "/minutes_calendar_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView MinutesUpdateRun(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("hdate", Common.defaultValue(map.get("hdate"), "0"));
		map.put("mdate", Common.defaultValue(map.get("mdate"), "0"));
		map.put("hdate2", Common.defaultValue(map.get("hdate2"), "0"));
		map.put("mdate2", Common.defaultValue(map.get("mdate2"),"0"));

		dbConn2.recordSet(QUERY_ROOT + ".minutesModify", map);
		mv.setViewName("redirect:/views/schedule/minutes_calendar");
		return mv;
	}

	//ckEditor imgUpload
	@RequestMapping(value = "/imgUpload.run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> imgUpload(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		String filePath = Common.addString("editorImgUpload/images_", Common.nowDate().replace("-", ""));
		try {
			List<String> upFileImg = imgCon.uploadImgFile(((MultipartHttpServletRequest) request).getFiles("upload"), request, "ed", filePath, null);
			for (String filename : upFileImg) {
				resultMap.put("fileName", filename);
			}
			resultMap.put("uploaded", 1);
			resultMap.put("url", Common.addString(request.getRequestURL().toString().replace(request.getRequestURI(), ""), Common.FILE_ROOT_PATH, filePath, "/", resultMap.get("fileName").toString()));
		} catch (Exception e) {
		}
		return resultMap;
	}
}