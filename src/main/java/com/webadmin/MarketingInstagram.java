package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
import com.common.common.ImgCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/marketing/instagram")
public class MarketingInstagram {

	private final String DIR_ROOT = "marketingInstagram";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;

	//인스타그램 요청 리스트
	@RequestMapping(value = "/list")
	public ModelAndView listValue(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("reqestTeamMap", dbConn.recordSet(QUERY_ROOT + ".reqestTeam", null));   //요청부서
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));           //검색조건
		mv.setViewName("marketing/instagram/list.pq");
		return mv;
	}

	// pq목록 Ajax로 불러오기 json
	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> listValue(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("A.subject", request.getParameterValues("subject"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("B.userName", request.getParameterValues("userName"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("A.gubun", request.getParameterValues(Common.paramIsArray("gubun", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("A.category_site_id", request.getParameterValues(Common.paramIsArray("category_site_id", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("A.process", request.getParameterValues(Common.paramIsArray("process", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("B.com_root+'>'+B.com_dept", request.getParameterValues(Common.paramIsArray("team", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("B.com_root+'>'+B.com_dept", request.getParameterValues("team[]"), "="));
		sqlItemList.add(dbConn2.makeSearchSqlRange("convert(varchar(10),A.post_date,126)", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map<String, Object> resultMap = new HashMap<>();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "case when A.emergency='y' and A.process in (1,2,3) then 0 else 1 end asc, case when A.process in (1,2,3) then 0 else 1 end asc, post_date desc, A.id");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", result);

		return resultMap;
	}

	//요청 상세내역
	@RequestMapping(value = "/input")
	public ModelAndView viewValue(ModelAndView mv, HttpServletRequest request, HttpSession session) {
		session.setAttribute("flag", request.getParameter("flag"));

		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (!Common.isNullOrEmpty(map.get("id"))) {
			mv.addObject("viewMap", dbConn2.recordSet(QUERY_ROOT + ".view", map).get(0));            //요청내용
			mv.addObject("commentMap", dbConn2.recordSet(QUERY_ROOT + ".commentView", map));     //처리내역
		}
		mv.addObject("post_date", Common.getDate(map.get("post_date")));
		mv.setViewName("marketing/instagram/input.tiles");
		return mv;
	}

	//처리내역 코멘트 등록
	@RequestMapping(value = "/comment_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView commentInsertRun(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("writer", ((MemberDTO4) authentication.getPrincipal()).getUser_name());

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT);
			String attachFiles = "";
			for (int i = 0, lenFile = addFile.size(); i < lenFile; i++) {
				if (i == 0) {
					attachFiles = addFile.get(i);
				} else {
					attachFiles = attachFiles + "," + addFile.get(i);
				}
			}
			map.put("attachFile", attachFiles);
		} catch (IOException e) {
			e.printStackTrace();
		}

		dbConn2.recordSet(QUERY_ROOT + ".commentInsert", map);
		mv.setViewName("redirect:/views/marketing/instagram/input?id=" + map.get("id"));
		return mv;
	}

	//요청 신규, 수정, 취소, 접수, 처리완료
	@RequestMapping(value = "/insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertRun(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String choice = map.get("choice");

		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		map.put("writer", member.getUser_name());
		map.put("user_id", member.getUser_id());

		if ("cancel".equals(choice)) {                   //취소
			dbConn2.recordSet(QUERY_ROOT + ".cancel", map);
		} else if ("accept".equals(choice)) {            //접수
			dbConn2.recordSet(QUERY_ROOT + ".accept", map);
		} else if ("complete".equals(choice)) {         //처리완료
			dbConn2.recordSet(QUERY_ROOT + ".complete", map);
		} else if ("insert".equals(choice) || "modify".equals(choice)) {            //신규, 수정
			try {
				List<String> upFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "insta", DIR_ROOT);
				for (String fileName : upFile) {
					map.put("uploadfile", fileName);
				}
			} catch (IOException e) {
			}

			if ("insert".equals(choice)) {
				dbConn2.recordSet(QUERY_ROOT + ".insert", map);
			} else {
				dbConn2.recordSet(QUERY_ROOT + ".modify", map);
			}

		}
		if ("마케팅팀".equals(member.getCom_dept())) {
			mv.setViewName("redirect:/views/marketing/instagram/list");
		} else {
			mv.setViewName("redirect:/views/marketing/instagram/calendar?flag="+map.get("flag"));
		}

		return mv;
	}

	//인스타그램 요청서 게시글에 따른 등록 가능 여부.
	@RequestMapping(value = "/post_check.run", method = {RequestMethod.GET}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getPostCheck(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", dbConn2.recordSet(QUERY_ROOT + ".postCheck", Common.paramToMap(request.getParameterMap())));

		return resultMap;
	}


	//인스타그램 요청서 현황 달력
	@RequestMapping(value = "/calendar.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getCalendar(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		Map<String, String> map =  Common.paramToMap(request.getParameterMap());
		map.put("flag", Common.defaultValue(request.getParameter("0"), ""));

		resultMap.put("data", dbConn2.recordSet(QUERY_ROOT + ".calendar",map));

		return resultMap;
	}

	//요청 진행 현황 통계
	@RequestMapping(value = "/statistics")
	public ModelAndView statisticsChgValue(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("wdate1", Optional.ofNullable(map.get("wdate1")).orElse((Common.nowDate().toString()).substring(0, 8) + "01")); //현재 날짜의 첫일
		map.put("wdate2", Optional.ofNullable(map.get("wdate2")).orElse(Common.nowDate()));

		mv.addObject("wdate1", map.get("wdate1"));
		mv.addObject("wdate2", map.get("wdate2"));
		mv.setViewName("marketing/instagram/statistics.pq");
		return mv;
	}


	// pq목록 Ajax로 불러오기 json
	@RequestMapping(value = "/statistics", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> statisticsChgValue(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", dbConn2.recordSet(QUERY_ROOT + ".statistics", map));

		return resultMap;
	}

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
