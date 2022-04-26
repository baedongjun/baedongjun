package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
import com.common.common.ImgCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@RequestMapping("/views/design")
public class Design {

	private final String DIR_ROOT = "design";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;
	@Resource
	private MemberDTO4 memberDTO4;

	// 업무 접수현황
	@RequestMapping(value = "/process_list")
	public ModelAndView process_list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".processList")));
		mv.setViewName("design/process_list.tiles");
		return mv;
	}

	// 나의 요청/접수
	@RequestMapping(value = "/myDesign")
	public ModelAndView myDesign(ModelAndView mv, Authentication authentication) {
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();

		Map<String, String> map = new HashMap<>();
		map.put("adminId",member.get_Id());
		map.put("UserName",member.getUser_name());
		map.put("UserDept",member.getCom_dept());
		map.put("UserRoot",member.getCom_root());

		mv.addObject("list", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".myDesign",map)));

		mv.setViewName("design/myDesign.tiles");
		return mv;
	}
	// 요청 진행 관리 - 리스트
	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("deptList", dbConn.recordSet(QUERY_ROOT + ".getComDept", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("design/list.pq");
		return mv;
	}

	// 요청 진행 관리 - 리스트
	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json;")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();
		List<String> sqlItemList3 = new ArrayList<>();

		String[] notProcess = {"24","25","27"};

		sqlItemList2.add(dbConn2.makeSearchSql("A.subject", request.getParameterValues("subject"), "like"));
		sqlItemList2.add(dbConn2.makeSearchSql("B.user_name", request.getParameterValues("subject"), "like"));

		sqlItemList.add(dbConn2.makeSearchSql("A.id", request.getParameterValues("design_process_id"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("process", request.getParameterValues("process"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("(CASE WHEN ISNULL(fdate, '1900-01-01') = '1900-01-01' THEN 1 ELSE 2 END)", request.getParameterValues("complete"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("result", request.getParameterValues("result"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("divide", request.getParameterValues("divide"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("market", request.getParameterValues("market"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("A.process", notProcess, "not in"));

		try{
			if (!Common.isNullOrEmpty(request.getParameter("andOr"))) {
				if (request.getParameter("andOr").equals("or")) {
					sqlItemList3.add(dbConn2.makeSearchSqlRange("wdate", request.getParameter("wdate1"),request.getParameter("wdate2")));
					sqlItemList3.add(dbConn2.makeSearchSqlRange("fdate", request.getParameter("fdate1"),request.getParameter("fdate2")));
				} else {
					sqlItemList.add(dbConn2.makeSearchSqlRange("wdate", request.getParameter("wdate1"),request.getParameter("wdate2")));
					sqlItemList.add(dbConn2.makeSearchSqlRange("fdate", request.getParameter("fdate1"),request.getParameter("fdate2")));
				}
			}
		} catch (Exception e) {
		}

		sqlItemList.removeAll(Collections.singleton(null));
		sqlItemList2.removeAll(Collections.singleton(null));
		sqlItemList3.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", sqlItemList2);
		String sqlItem3 = String.join(" or ", sqlItemList3);

		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";
		sqlItem3 = (!Common.isNullOrEmpty(sqlItem3)) ? " and (" + sqlItem3 + ")" : "";

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList) + sqlItem2 + sqlItem3);
		map.put("orderBy", "CASE WHEN emergency = 'Y' AND result IN ('대기', '접수') THEN 1 ELSE CASE WHEN result = '대기' THEN 2 ELSE CASE WHEN result = '접수' THEN 3 ELSE 4 END END END ASC, CASE WHEN result <> '처리완료' THEN A.wdate END ASC, A.id DESC");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 요청 진행 관리 - 등록 화면
	@RequestMapping(value = "/input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		try{
			Map<String, String> map = new HashMap<>();
			map.put("id",request.getParameter("id"));
			map.put("adminID",((MemberDTO4) authentication.getPrincipal()).get_Id());
			map.put("admin_member_id",request.getParameter("admin_member_id"));

			if (request.getParameter("id") != null) {
				dbConn2.recordSet(QUERY_ROOT + ".updateReply", map);
			}

			List<List<Map<String, Object>>> list = dbConn2.recordSet(QUERY_ROOT + ".viewList", map);

			List<Map<String, Object>> designMember = list.get(2);

			if (designMember.size() > 0) {
				map.put("admin_member_id",String.valueOf(designMember.get(0).get("admin_member_id")));
			}

			List<Map<String, Object>> reply = list.get(1);

			for (int i=0; i<reply.size(); i++) {
				String replymsg = String.valueOf(reply.get(i).get("reply"));

				if (replymsg.contains("[") && replymsg.contains("]")) {
					// [ ] 안 나스경로 추출
					Pattern pattern = Pattern.compile("[\\[](.*?)[\\]]");
					Matcher matcher = pattern.matcher(replymsg);

					while (matcher.find()) {
						replymsg = replymsg.replace(matcher.group(0),"<a href='"+matcher.group(1)+"' target='_new' shape=''>"+matcher.group(0)+"</a>");
					}

					Map<String,Object> replyMap = reply.get(i);
					replyMap.put("reply",replymsg);
					reply.set(i,replyMap);
				}
			}

			mv.addObject("viewMap", list.get(0).size() > 0 ? list.get(0) : null);
			mv.addObject("reply", reply);
			mv.addObject("designMember", designMember);
			mv.addObject("addTime", dbConn2.recordSet(QUERY_ROOT + ".addTime", map));
			mv.addObject("adminList", list.get(3));

			mv.setViewName("design/input.tiles");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

	// 요청 진행 관리 - 카테고리 선택 화면
	@RequestMapping(value = "/category")
	public ModelAndView category(ModelAndView mv) {
		mv.setViewName("design/category.bare");
		return mv;
	}

	// 요청 진행 관리 - 시간 추가 화면
	@RequestMapping(value = "/addTime")
	public ModelAndView addTime(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("design_process_id", request.getParameter("design_process_id"));
		mv.setViewName("design/addTime.bare");
		return mv;
	}

	// 요청 진행 관리 - 댓글 등록
  	@RequestMapping(value = "/comment_insert.run", method = RequestMethod.POST)
  	@Transactional
  	public ModelAndView commentInsertRun(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
	    Map<String, String> map = Common.paramToMap(request.getParameterMap());
	    map.put("writer", ((MemberDTO4) authentication.getPrincipal()).getUser_name());
	    map.put("adminID", ((MemberDTO4) authentication.getPrincipal()).get_Id());
	    map.put("dept", ((MemberDTO4) authentication.getPrincipal()).getCom_dept());

			try {
				List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("replyFile"), request, "req", DIR_ROOT);
				String attachFiles = "";
				for (int i=0; i<addFile.size(); i++) {
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

    	mv.setViewName("redirect:/views/design/input?id=" + map.get("design_process_id"));
    	return mv;
  	}

  	// 요청 진행 관리 - 보류, 접수, 취소, 시간추가
  	@RequestMapping(value = "/update.run", method = RequestMethod.POST)
  	@Transactional
  	public ModelAndView update(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
	    Map<String, String> map = Common.paramToMap(request.getParameterMap());
	    map.put("adminID", ((MemberDTO4) authentication.getPrincipal()).get_Id());
	    map.put("id", map.get("design_process_id"));

	    if (map.get("choice").equals("hold")) {
	    	dbConn2.recordSet(QUERY_ROOT + ".updateHold", map);
	    } else if (map.get("choice").equals("accept")) {
	    	List<Map<String, Object>> designMemberList = dbConn2.recordSet(QUERY_ROOT + ".designMember", map);

	    	if (designMemberList.size() > 0) {
	    		long workTimeChange = Math.round(Integer.parseInt(map.get("workTimeChange")) * designMemberList.size() + 0.5) / (designMemberList.size() + 1);
	    		map.put("workTimeChange",String.valueOf(workTimeChange));
					dbConn2.recordSet(QUERY_ROOT + ".updateAccept2", map);
				} else {
	    		map.put("workTimeChange",map.get("workTimeChange"));
					dbConn2.recordSet(QUERY_ROOT + ".updateAccept1", map);
				}
			dbConn2.recordSet(QUERY_ROOT + ".updateAccept3", map);


			} else if (map.get("choice").equals("complete")) {
	    	dbConn2.recordSet(QUERY_ROOT + ".updateComplete", map);
			} else if (map.get("choice").equals("cancel")) {
	    	List<Map<String, Object>> designMemberList = dbConn2.recordSet(QUERY_ROOT + ".designMember", map);
				// 혼자 접수한 경우
				if (designMemberList.size() == 1) {
	    		dbConn2.recordSet(QUERY_ROOT + ".updateCancel1", map);
				} else {
	    		// 다중 접수된 경우
	    		dbConn2.recordSet(QUERY_ROOT + ".updateCancel2", map);
				}
			} else if (map.get("choice").equals("addTime")) {
	    	dbConn2.recordSet(QUERY_ROOT + ".updateAddTime", map);
			}

    	mv.setViewName("redirect:/views/design/input?id=" + map.get("design_process_id"));
    	return mv;
  	}

	// 요청 진행 관리 - 등록하기
	@RequestMapping(value = "/insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insert(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("id",((MemberDTO4) authentication.getPrincipal()).get_Id());

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("attachFile", filename);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (map.get("choice").equals("insert")) {
			dbConn2.recordSet(QUERY_ROOT + ".insert", map);
		} else if (map.get("choice").equals("correct")) {
			dbConn2.recordSet(QUERY_ROOT + ".correct", map);
		} else if (map.get("choice").equals("cancel")) {
			dbConn2.recordSet(QUERY_ROOT + ".cancel", map);
		}

		mv.setViewName("redirect:/views/design/list");
		return mv;
	}

	// 자체 진행 관리 - 리스트 화면
	@RequestMapping(value = "/list2")
	public ModelAndView list2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("design/list2.pq");
		return mv;
	}

	// 자체 진행 관리 - 리스트 호출
	@RequestMapping(value = "/list2.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list2(HttpServletRequest request,HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();

		String[] notProcess = {"24","25","27"};
		String[] cancelResult = {"취소"};

		sqlItemList2.add(dbConn2.makeSearchSql("A.subject", request.getParameterValues("subject"), "like"));
		sqlItemList2.add(dbConn2.makeSearchSql("B.user_name", request.getParameterValues("subject"), "like"));
		sqlItemList2.removeAll(Collections.singleton(null));

		sqlItemList.add(dbConn2.makeSearchSql("A.id", request.getParameterValues("design_process_id"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("process", request.getParameterValues("process"), "="));
		sqlItemList.add(dbConn2.makeSearchSqlRange("wdate", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.add(dbConn2.makeSearchSql("A.process", notProcess, "in"));
		sqlItemList.add(dbConn2.makeSearchSql("A.result", cancelResult, "not in"));
		sqlItemList.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", sqlItemList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList) + sqlItem2);
		map.put("orderBy", "A.id DESC");
		Common.PQmap(map,request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list2", map);

		Common.PQresultMap(resultMap,map.get("curPage"),result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 자체 진행 관리 - 등록 화면
	@RequestMapping(value = "/input2")
	public ModelAndView input2(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("adminList", dbConn2.recordSet(QUERY_ROOT + ".adminList"));
		mv.setViewName("design/input2.tiles");
		return mv;
	}

	// 자체 진행 관리 - 등록하기
	@RequestMapping(value = "/insert2.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insert2(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("list", Common.paramToList(new String[]{"admin_member_id"}, request.getParameterMap()));
		paramMap.put("param", map);
		paramMap.put("writer", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		dbConn2.recordSet(QUERY_ROOT + ".insert2", paramMap);
		mv.setViewName("redirect:/views/design/list2");
		return mv;
	}

	// 요청 기획서 가이드
	@RequestMapping(value = "/planning_down")
	public ModelAndView planningDown(ModelAndView mv) {
		mv.setViewName("design/planning_down.tiles");
		return mv;
	}

	// 업무별 진행현황
	@RequestMapping(value = "/viewPart")
	public ModelAndView viewPart(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();

		map.put("yearV", Common.defaultValue(request.getParameter("yearV"), Common.nowDate().substring(0,4)));
		map.put("monthV", Common.defaultValue(request.getParameter("monthV"), Common.nowDate().substring(5,7)));

		mv.addObject("list", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".viewPart", map)));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("design/viewPart.tiles");
		return mv;
	}

	// 중요업무 현황
	@RequestMapping(value = "/viewWeek")
	public ModelAndView viewWeek(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();

		map.put("yearV", Common.defaultValue(request.getParameter("yearV"), Common.nowDate().substring(0,4)));
		map.put("monthV", Common.defaultValue(request.getParameter("monthV"), Common.nowDate().substring(5,7)));

		List<List<Map<String, Object>>> list = dbConn2.recordSet(QUERY_ROOT + ".viewWeek", map);

		mv.addObject("list", list.get(0));
		mv.addObject("detailList", list.get(1));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("design/viewWeek.tiles");
		return mv;
	}

	// 디자이너 접수업무
	@RequestMapping(value = "/viewPer")
	public ModelAndView viewPer(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if(Common.isNullOrEmpty(map.get("yearV")) || Common.isNullOrEmpty(map.get("monthV"))){
			map.put("startDate", LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			map.put("endDate", YearMonth.now().atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		}else{
			map.put("startDate", LocalDate.of(Integer.parseInt(map.get("yearV")), Integer.parseInt(map.get("monthV")), 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			map.put("endDate", YearMonth.of(Integer.parseInt(map.get("yearV2")), Integer.parseInt(map.get("monthV2"))).atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		}

		List<List<Map<String, Object>>> list = dbConn2.recordSet(QUERY_ROOT + ".viewPer", map);

		mv.addObject("adminList", list.get(0));
		mv.addObject("list", list.get(1));
		mv.addObject("manageList", list.get(2));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("design/viewPer.tiles");
		return mv;
	}

    // 지정 완료일 요청
	@RequestMapping(value = "/viewDate")
	public ModelAndView viewDate(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearV", Common.defaultValue(map.get("yearV"), Common.nowDate().substring(0,4)));
		map.put("monthV", Common.defaultValue(map.get("monthV"), Common.nowDate().substring(5,7)));
		map.put("sqlItem",dbConn2.makeSearchSql("result", request.getParameterValues("result"), "="));

		mv.addObject("holiday", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".holiday", map)));
		mv.addObject("list", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".viewDate", map)));
		mv.addObject("yearV", map.get("yearV"));
		mv.addObject("monthV", map.get("monthV"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("design/viewDate.tiles");
		return mv;
	}

	// 디자이너 월별 누적
	@RequestMapping(value = "/statistics1")
	public ModelAndView statistics1(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearV", Common.defaultValue(map.get("yearV"), Common.nowDate().substring(0,4)));
		map.put("admin_member_id",request.getParameter("admin_member_id"));

		mv.addObject("adminList", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".adminList", map)));
		mv.addObject("list", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".statistics1", map)));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("design/statistics1.tiles");
		return mv;
	}

	// 기간별 총 누적
	@RequestMapping(value = "/statistics2")
	public ModelAndView statistics2(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if(Common.isNullOrEmpty(map.get("yearV")) || Common.isNullOrEmpty(map.get("monthV"))){
			map.put("startDate", LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			map.put("endDate", YearMonth.now().atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		}else{
			map.put("startDate", LocalDate.of(Integer.parseInt(map.get("yearV")), Integer.parseInt(map.get("monthV")), 1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			map.put("endDate", YearMonth.of(Integer.parseInt(map.get("yearV2")), Integer.parseInt(map.get("monthV2"))).atEndOfMonth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		}
		map.put("yearV", Common.defaultValue(map.get("yearV"), Common.nowDate().substring(0,4)));
		map.put("sqlItem",dbConn2.makeSearchSql("lastQuality", request.getParameterValues(Common.paramIsArray("lastQuality", request)), "in"));

		List<List<Map<String, Object>>> list = dbConn2.recordSet(QUERY_ROOT + ".statistics2", map);

		mv.addObject("adminList", list.get(0));
		mv.addObject("list", list.get(1));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("design/statistics2.tiles");
		return mv;
	}

	// 디자이너 누적진행
	@RequestMapping(value = "/viewMember")
	public ModelAndView viewMember(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearV", Common.defaultValue(map.get("yearV"), Common.nowDate().substring(0,4)));
		map.put("monthV", Common.defaultValue(map.get("monthV"), Common.nowDate().substring(5,7)));
		map.put("user_id", ((MemberDTO4) authentication.getPrincipal()).getUser_id());
		map.put("user_duty", ((MemberDTO4) authentication.getPrincipal()).getUser_duty());
		map.put("user_dept", ((MemberDTO4) authentication.getPrincipal()).getCom_dept());
		map.put("user_name",((MemberDTO4) authentication.getPrincipal()).getUser_name());

		List<List<Map<String, Object>>> list = dbConn2.recordSet(QUERY_ROOT + ".viewMember", map);

		mv.addObject("adminList", list.get(0));
		mv.addObject("list", list.get(1));
		mv.addObject("holiday", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".holiday", map)));
		mv.addObject("yearV", map.get("yearV"));
		mv.addObject("monthV", map.get("monthV"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("design/viewMember.tiles");
		return mv;
	}

	// 채점결과 추이
	@RequestMapping(value = "/quality_portfolio")
	public ModelAndView quality_portfolio(ModelAndView mv) {
		mv.addObject("list", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".quality_portfolio")));
		mv.setViewName("design/quality_portfolio.tiles");
		return mv;
	}

	// 디자인 채점
	@RequestMapping(value = "/quality_list")
	public ModelAndView quality_list(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		List<String> sqlItemList = new ArrayList<>();

		String yearV = Common.defaultValue(request.getParameter("yearV"), Common.nowDate().substring(0,4));
		String monthV = Common.defaultValue(request.getParameter("monthV"), Common.nowDate().substring(5,7));

		String[] adminID = {((MemberDTO4) authentication.getPrincipal()).get_Id()};
		String[] yearValue = {yearV};
		String[] monthValue = {monthV};

		if (((MemberDTO4) authentication.getPrincipal()).getUser_id().equals("master") || (((MemberDTO4) authentication.getPrincipal()).getUser_duty().equals("3") &&  ((MemberDTO4) authentication.getPrincipal()).getCom_dept().equals("웹디자인팀"))) {
			sqlItemList.add(dbConn2.makeSearchSql("yearValue", yearValue, "="));
			sqlItemList.add(dbConn2.makeSearchSql("monthValue", monthValue, "="));
		} else {
			sqlItemList.add(dbConn2.makeSearchSql("yearValue", yearValue, "="));
			sqlItemList.add(dbConn2.makeSearchSql("A.admin_member_id", adminID, "="));
		}

		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("adminID", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		mv.addObject("list", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".quality_list", map)));
		mv.addObject("cnt", dbConn2.recordSet(QUERY_ROOT + ".quality_list_cnt", map).get(0));
		mv.addObject("yearV", yearV);
		mv.addObject("monthV", monthV);
		mv.addObject("nowYear", Common.nowDate().substring(0,4));
		mv.addObject("nowMonth", Common.nowDate().substring(5,7));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));

		mv.setViewName("design/quality_list.tiles");
		return mv;
	}

	// 디자인 채점 - 상세화면
	@RequestMapping(value = "/quality_view")
	public ModelAndView quality_view(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		try{
			Map<String, String> map = new HashMap<>();
			map.put("adminID",((MemberDTO4) authentication.getPrincipal()).get_Id());
			map.put("id",request.getParameter("id"));

			List<Map<String,String>> id = dbConn2.recordSet(QUERY_ROOT + ".quality_score_list_id", map);

			if (id.size() > 0) {
				map.put("id",id.get(0).get("id"));
			}

			List<Map<String,Object>> viewMap = dbConn2.recordSet(QUERY_ROOT + ".quality_view", map);

			if (viewMap.size() > 0 && viewMap.get(0).get("score1") == null && viewMap.get(0).get("score2") == null) {
				mv.addObject("contentsCorrect","true");
			} else {
				mv.addObject("contentsCorrect","false");
			}
			System.out.println(viewMap.get(0).get("img1").toString());
			System.out.println(viewMap.get(0).get("img2").toString());


			mv.addObject("img1Bool", Common.fileExtValidate(viewMap.get(0).get("img1").toString(), "img"));
			mv.addObject("img2Bool", Common.fileExtValidate(viewMap.get(0).get("img2").toString(), "img"));
			mv.addObject("viewMap", viewMap.size() > 0 ? viewMap : null);
			mv.addObject("nowYear", Common.nowDate().substring(0,4));
			mv.addObject("nowMonth", Common.nowDate().substring(5,7));
			mv.addObject("id",request.getParameter("id"));
		} catch (Exception e){
			e.printStackTrace();
		}

		mv.setViewName("design/quality_view.tiles");
		return mv;
	}

	// 디자인 채점 - 등록하기
	@RequestMapping(value = "/quality_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView quality_insert(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String adminID = ((MemberDTO4) authentication.getPrincipal()).get_Id();
		map.put("adminID",adminID);
		map.put("nowYear", Common.nowDate().substring(0,4));
		map.put("nowMonth", Common.nowDate().substring(5,7));

		try {
			List<String> addFile1 = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file1"), request, Common.addString("img1_",adminID), "design_score");
			for (String fileName : addFile1) map.put("img1", fileName);

			List<String> addFile2 = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file2"), request, Common.addString("img2_",adminID), "design_score");
			for (String fileName : addFile2) map.put("img2", fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		dbConn2.recordSet(QUERY_ROOT + ".quality_insert", map);

		mv.setViewName("redirect:/views/design/quality_list");
		return mv;
	}

	// 디자인 채점 - 수정하기
	@RequestMapping(value = "/quality_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView qualityUpdate(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String adminID = ((MemberDTO4) authentication.getPrincipal()).get_Id();
		map.put("adminID",adminID);
		map.put("nowYear", Common.nowDate().substring(0,4));
		map.put("nowMonth", Common.nowDate().substring(5,7));

		try {
			List<String> addFile1 = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file1"), request, Common.addString("img1_",adminID), "design_score");
			for (String fileName : addFile1) map.put("img1", fileName);

			List<String> addFile2 = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file2"), request, Common.addString("img2_",adminID), "design_score");
			for (String fileName : addFile2) map.put("img2", fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		dbConn2.recordSet(QUERY_ROOT + ".quality_update", map);

		mv.setViewName("redirect:/views/design/quality_list");
		return mv;
	}

	// 디자인 채점 - 채점하기 화면
	@RequestMapping(value = "/quality_score")
	public ModelAndView quality_score(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		List<String> sqlItemList = new ArrayList<>();

		String[] yearValue = {Common.nowDate().substring(0,4)};
		String[] monthValue = {String.valueOf(Integer.parseInt(Common.nowDate().substring(5,7)))};

		sqlItemList.add(dbConn2.makeSearchSql("yearValue", yearValue, "="));
		sqlItemList.add(dbConn2.makeSearchSql("monthValue", monthValue, "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("adminID",((MemberDTO4) authentication.getPrincipal()).get_Id());

		mv.addObject("list", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".quality_score", map)));
		mv.setViewName("design/quality_score.tiles");
		return mv;
	}

	// 디자인 채점 - 채점하기
	@RequestMapping(value = "/quality_score_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView quality_score_insert(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("adminID",((MemberDTO4) authentication.getPrincipal()).get_Id());
		try{
			for (String key : request.getParameterMap().keySet()) {
				if (key.indexOf("_sum") > 0) {
					map.put("gubun",key.split("_")[0]);
					map.put("design_score_list_id",key.split("_")[1]);
					map.put("score1",request.getParameterMap().get(key)[0].split(",")[0]);
					map.put("score2",request.getParameterMap().get(key)[0].split(",")[1]);
					map.put("score3",request.getParameterMap().get(key)[0].split(",")[2]);
					map.put("score4",request.getParameterMap().get(key)[0].split(",")[3]);

				 	if (!key.split("_")[2].equals("")) {
					    map.put("id",key.split("_")[2]);
						dbConn2.recordSet(QUERY_ROOT + ".quality_score_update", map);
					} else {
						dbConn2.recordSet(QUERY_ROOT + ".quality_score_insert", map);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mv.setViewName("redirect:/views/design/quality_list");
		return mv;
	}

	@RequestMapping(value = "/imgUpload.run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
  	public Map<String, Object> imgUpload(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();
		String filePath = Common.addString("editorImgUpload/images_", Common.nowDate().replace("-", ""));
		try {
		  List<String> upFileImg = imgCon.uploadImgFile(((MultipartHttpServletRequest) request).getFiles("upload"), request, "ed",  filePath, null);
		  for (String filename : upFileImg) { resultMap.put("fileName", filename); }
		  resultMap.put("uploaded", 1);
		  resultMap.put("url", Common.addString(request.getRequestURL().toString().replace(request.getRequestURI(),""), Common.FILE_ROOT_PATH, filePath, "/", resultMap.get("fileName").toString()));
		} catch (Exception e) {
		}
		return resultMap;
 	 }

 	// 디자인 알람 체크
	@RequestMapping(value = "/alarmCheck.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> alarmCheck(Authentication authentication) {
		Map<String, String> map = new HashMap<>();
		map.put("admin_member_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		List<Map> result = dbConn2.recordSet(QUERY_ROOT + ".alarmCheck", map);

		Map resultMap = new HashMap();
		resultMap.put("data", result.size() > 0 ? result.get(0) : null);
		return resultMap;
	}
}