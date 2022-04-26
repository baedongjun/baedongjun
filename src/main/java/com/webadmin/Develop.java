package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
import com.common.common.ImgCon;
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
public class Develop {

	private final String DIR_ROOT = "develop";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name="dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;

	//요청 진행 관리 - 화면
	@RequestMapping(value = "/develop_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("develop/develop_list.pq");
		return mv;
	}

	//요청 진행 관리 - 리스트
	@RequestMapping(value = "/develop_list.run", method = {RequestMethod.POST}, produces = "application/json;")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();
		List<String> sqlItemList3 = new ArrayList<>();

		sqlItemList2.add(dbConn2.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		sqlItemList2.add(dbConn2.makeSearchSql("convert(nvarchar(50),content1)", request.getParameterValues("subject"), "like"));
		sqlItemList2.removeAll(Collections.singleton(null));

		sqlItemList3.add(dbConn2.makeSearchSql("user_name", request.getParameterValues("writer"), "like"));
		sqlItemList3.add(dbConn2.makeSearchSql("developer", request.getParameterValues("writer"), "like"));
		sqlItemList3.removeAll(Collections.singleton(null));

		sqlItemList.add((!Common.isNullOrEmpty(request.getParameterValues(Common.paramIsArray("dbfile", request))) ? dbConn2.makeSearchSql("id in (select develop_process_id from [petitelin_MAIN]..[Develop_DB_file] where gubun1", request.getParameterValues(Common.paramIsArray("dbfile", request)), "=") + ")" : null));
		sqlItemList.add(dbConn2.makeSearchSql("req_ete", request.getParameterValues(Common.paramIsArray("req_ete", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("req_ok", request.getParameterValues(Common.paramIsArray("req_ok", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("team_gu", request.getParameterValues(Common.paramIsArray("team_gu", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("region_gu", request.getParameterValues("region_gu"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("company", request.getParameterValues(Common.paramIsArray("company", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("dist_status", request.getParameterValues(Common.paramIsArray("dist_status", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSqlRange("A.wdate", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ", sqlItemList2);
		String sqlItem3 = String.join(" or ", sqlItemList3);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";
		sqlItem3 = (!Common.isNullOrEmpty(sqlItem3)) ? " and (" + sqlItem3 + ")" : "";

		String wdate = request.getParameter("wdate1");

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList) + sqlItem2 + sqlItem3);
		map.put("orderBy", "case when req_ok in ('처리완료','보류','취소') then 99 else case when req_ok='대기' then 1 else case when req_ok='접수' then 2 else 3 end end end asc,case when req_ok in ('처리완료', '취소') then 1000 else isnull(sunser,0) end asc, wdate desc");
		map.put("user_id", request.getParameter("user_id"));
		map.put("com_dept", request.getParameter("com_dept"));
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//요청 진행 관리 - 등록 화면
	@RequestMapping(value = "/develop_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("region_gu", Common.defaultValue(map.get("region_gu"), "kor"));

		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".view", map));
		mv.addObject("resultAnswerMap", dbConn2.recordSet(QUERY_ROOT + ".answerView", map));
		mv.addObject("resultDBInfoMap", dbConn2.recordSet(QUERY_ROOT + ".dBInfoView", map));
		mv.addObject("returnParam", map);
		mv.setViewName("develop/develop_input.tiles");
		return mv;
	}

	//요청 진행 관리 - 등록
	@RequestMapping(value = "/develop_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("uploadFile", filename);
			}
		} catch (IOException e) {
		}

		dbConn2.recordSet(QUERY_ROOT + ".insert", map);
		mv.setViewName("redirect:/views/develop/develop_list");
		return mv;
	}

	//요청 진행 관리 - 수정
	@RequestMapping(value = "/develop_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("uploadFile", filename);
			}
		} catch (IOException e) {
		}

		map.put("content", map.get("content").replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));
		dbConn2.recordSet(QUERY_ROOT + ".update", map);

		if ("보류".equals(map.get("nowReqOku"))) {
			dbConn2.recordSet(QUERY_ROOT + ".holdUpdate", map);
			dbConn2.recordSet(QUERY_ROOT + ".reInsert", map);
		}

		mv.setViewName("redirect:/views/develop/develop_list");
		return mv;
	}

	//요청 진행 관리 - 댓글 등록
	@RequestMapping(value = "develop_answer.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView answer(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if("cancel".equals(map.get("choice"))){ map.put("req_ok", "취소"); }
		if (map.get("nowReqOk") != "보류" && !(Common.isNullOrEmpty(map.get("sdate"))) && !(Common.isNullOrEmpty(map.get("edate")))) { map.put("req_ok", "처리완료"); }

		String strQuery = "";
		if ("접수".equals(map.get("req_ok"))) {
			strQuery = " rdate = getdate(), ";
			strQuery = Common.addString(strQuery, " req_ete = '", map.get("req_ete"), "', ");
			strQuery = Common.addString(strQuery, " content2 = '", map.get("resLog"), ",", map.get("req_ok"), ":'+convert(varchar(10), getdate(), 126), ");
		} else if ("처리중".equals(map.get("req_ok"))) {
			if ("대기".equals(map.get("nowReqOk"))) {
				strQuery = " rdate = getdate(), ";
			}
			if (Common.isNullOrEmpty(map.get("sdate"))) {
				strQuery = Common.addString(strQuery, " sdate = convert(varchar(10), getdate(), 126), ");
				strQuery = Common.addString(strQuery, " content2 = '", map.get("resLog"), ",", map.get("req_ok"), ":'+convert(varchar(10), getdate(), 126), ");
			} else {
				strQuery = Common.addString(strQuery, " sdate = '", map.get("sdate"), "', ");
				strQuery = Common.addString(strQuery, " content2 = '", map.get("resLog"), ",", map.get("req_ok"), ":", map.get("sdate"), "', ");
			}
			strQuery = Common.addString(strQuery, " req_ete = '", map.get("req_ete"), "', ");
		} else if ("처리완료".equals(map.get("req_ok"))) {
			if ("대기".equals(map.get("nowReqOk"))) {
				strQuery = " rdate = getdate(), ";
			}
			if (Common.isNullOrEmpty(map.get("req_ete"))) {
				strQuery = Common.addString(strQuery, " req_ete = '즉시', ");
			} else {
				strQuery = Common.addString(strQuery, " req_ete = '", map.get("req_ete"), "', ");
			}
			if (Common.isNullOrEmpty(map.get("sdate"))) {
				strQuery = Common.addString(strQuery, " sdate = convert(varchar(10), getdate(), 126), ");
			} else {
				strQuery = Common.addString(strQuery, " sdate = '", map.get("sdate"), "', ");
			}
			if (Common.isNullOrEmpty(map.get("edate"))) {
				strQuery = Common.addString(strQuery, " edate = convert(varchar(10), getdate(), 126), ");
				strQuery = Common.addString(strQuery, " content2 = '", map.get("resLog"), ",", map.get("req_ok"), ":'+convert(varchar(10), getdate(), 126), ");
			} else {
				strQuery = Common.addString(strQuery, " edate = '", map.get("edate"), "', ");
				strQuery = Common.addString(strQuery, " content2 = '", map.get("resLog"), ",", map.get("req_ok"), ":", map.get("edate"), "', ");
			}
			if (!"처리완료".equals(map.get("nowReqOk"))) {
				if (!"no".equals(map.get("dist_server"))) {
					strQuery = Common.addString(strQuery, " dist_server = '", map.get("dist_server"), "', ");
					strQuery = Common.addString(strQuery, " dist_status = 1, ");
					strQuery = Common.addString(strQuery, " dist_nowar = '", Common.defaultValue(map.get("dist_nowar"), "n"), "', ");
				} else {
					strQuery = Common.addString(strQuery, " dist_server = null, ");
					strQuery = Common.addString(strQuery, " dist_status = null, ");
				}
			}


		} else if ("보류".equals(map.get("req_ok"))) {
			if ("대기".equals(map.get("nowReqOk"))) {
				strQuery = " rdate = getdate(), ";
			}
			strQuery = Common.addString(strQuery, " content2 = '", map.get("resLog"), ",", map.get("req_ok"), ":'+convert(varchar(10), getdate(), 126), ");
		} else if ("취소".equals(map.get("req_ok"))) {
			if ("대기".equals(map.get("nowReqOk"))) {
				strQuery = " rdate = getdate(), ";
			}
			strQuery = Common.addString(strQuery, " cdate = getdate(), ");
			strQuery = Common.addString(strQuery, " content2 = '", map.get("resLog"), ",", map.get("req_ok"), ":'+convert(varchar(10), getdate(), 126), ");
		}

		if ("보류".equals(map.get("nowReqOk")) && Common.isNullOrEmpty(map.get("req_ok"))) {
			map.put("req_ok", "접수");
			strQuery = Common.addString(strQuery, " content2 = '", map.get("resLog"), ",", map.get("req_ok"), ":'+convert(varchar(10), getdate(), 126), ");
		}

		if ("master".equals(map.get("user_id")) || "IT개발팀".equals(map.get("com_dept")) || "MOBILE_APP".equals(map.get("com_dept"))) {
			strQuery = Common.addString(strQuery, " developer = '", map.get("developer"), "', ");
		}

		strQuery = Common.addString(strQuery, " req_ok = '", map.get("req_ok"), "' ");

		map.put("strQuery", strQuery);
		map.put("content2", map.get("content2").replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));
		if (!Common.isNullOrEmpty(map.get("strQuery"))) {
			dbConn2.recordSet(QUERY_ROOT + ".answerUpdate", map);
		}
		if (!Common.isNullOrEmpty(map.get("content2"))) {
			dbConn2.recordSet(QUERY_ROOT + ".answerInsert", map);
		}
		mv.setViewName("redirect:/views/develop/develop_list");
		return mv;
	}

	//요청 진행 관리 - DB파일 등록 화면
	@RequestMapping(value = "/develop_DBfile")
	public ModelAndView dbFile(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".dbFileView", map));
		mv.addObject("returnParam", map);
		mv.setViewName("develop/develop_DBfile.bare");
		return mv;
	}

	//요청 진행 관리 - 순서 수정
	@RequestMapping(value = "/develop_sunser_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView sunserUpdateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String[] idArray = String.valueOf(map.get("id")).split(", ");
		String[] sunserArray = String.valueOf(map.get("sunser")).split(", ");

		List<Map<String, String>> listMap = new ArrayList<>();
		for (int i = 0; i < idArray.length; i++) {
			String id = idArray[i];
			String sunser = sunserArray[i];
			listMap.add(new HashMap() {{
				put("id", id);
				put("sunser", sunser);
			}});
		}

		dbConn2.recordSet(QUERY_ROOT + ".sunserUpdate", listMap);
		mv.setViewName("redirect:/views/develop/develop_list");
		return mv;
	}

	//요청 진행 관리 - 스케줄 등록
	@RequestMapping(value = "/develop_important_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView importantUpdateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		dbConn2.recordSet(QUERY_ROOT + ".importantUpdate", map);
		dbConn2.recordSet(QUERY_ROOT + ".importantFirstInsert", map);
		mv.setViewName("redirect:/views/develop/important_list");
		return mv;
	}

	//요청 진행 관리 - DB파일 등록
	@RequestMapping(value = "/develop_DBfile_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView dbFileInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String[] gubun2Array = String.valueOf(map.get("gubun2")).split(", ");
		String[] path_nameArray = String.valueOf(map.get("path_name")).split(", ");
		String[] helpArray = String.valueOf(map.get("help")).split(", ");

		List<Map<String, String>> listMap = new ArrayList<>();
		for (int i = 0; i < path_nameArray.length; i++) {
			String gubun2 = gubun2Array[i];
			String path_name = path_nameArray[i];
			String help = helpArray[i];
			String gubunKey = "";
			if (!"4".equals(gubun2)) {
				map.put("gubun1", "1");
				gubunKey = ".";
			} else {
				map.put("gubun1", "2");
				gubunKey = "/";
			}
			if (!Common.isNullOrEmpty(gubun2)) {
				map.put("dev_path", path_name.substring(0, path_name.lastIndexOf(gubunKey) + 1));
				map.put("dev_name", path_name.substring(path_name.lastIndexOf(gubunKey) + 1, path_name.length()));
			}
			listMap.add(new HashMap() {{
				put("id", map.get("id"));
				put("gubun1", map.get("gubun1"));
				put("dev_path", map.get("dev_path"));
				put("dev_name", map.get("dev_name"));
				put("gubun2", gubun2);
				put("help", help);
			}});
		}

		dbConn2.recordSet(QUERY_ROOT + ".dbFileDelete", map);
		dbConn2.recordSet(QUERY_ROOT + ".dbFileInsert", listMap);
		mv.setViewName("redirect:/views/develop/develop_list");
		return mv;
	}

	//업무프로세스 화면
	@RequestMapping(value = "/process_list")
	public ModelAndView processList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".processList", Common.paramToMap(request.getParameterMap()))));
		mv.setViewName("develop/process_list.tiles");
		return mv;
	}

	//중요 업무 화면
	@RequestMapping(value = "/important_list")
	public ModelAndView importantList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("develop/important_list.pq");
		return mv;
	}

	//중요 업무 리스트
	@RequestMapping(value = "/important_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> importantList(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("now_status", request.getParameterValues(Common.paramIsArray("req_ok", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("developer", request.getParameterValues("developer"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("important", new String[]{"y"}, "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		map.put("sqlItem", String.join(" and ", sqlItemList));

		Map resultMap = new HashMap();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".importantList", map)));

		return resultMap;
	}

	//중요 업무 등록 화면
	@RequestMapping(value = "/important_view")
	public ModelAndView importantView(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("resultStatusMap", dbConn2.recordSet(QUERY_ROOT + ".importantStatusView", map));
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".importantView", map));
		mv.addObject("returnParam", map);
		mv.setViewName("develop/important_view.tiles");
		return mv;
	}

	//중요 업무 등록
	@RequestMapping(value = "/important_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView importantInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("content2", map.get("content2").replaceAll("(\r\n|\r|\n|\n\r)", "<br>"));
		dbConn2.recordSet(QUERY_ROOT + ".importantInsert", map);
		mv.setViewName("redirect:/views/develop/important_list");
		return mv;
	}

	//프로그램 진행 현황표 - 화면
	@RequestMapping(value = "/develop_graph")
	public ModelAndView GraphList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("develop/develop_graph.pq");
		return mv;
	}

	//프로그램 진행 현황표 - 리스트
	@RequestMapping(value = "/develop_graph.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> GraphList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSqlRange("wdate", request.getParameter("startDate"), request.getParameter("endDate")));
		sqlItemList.add(dbConn2.makeSearchSql("subject", request.getParameterValues("subject"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("developer", request.getParameterValues("writer"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("req_ok", request.getParameterValues(Common.paramIsArray("req_ok", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("team_gu", request.getParameterValues(Common.paramIsArray("team_gu", request)), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("startDate", map.get("startDate"));
		map.put("endDate", map.get("endDate"));
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".graphList", map)));

		return resultMap;
	}

	//배포 진행 관리 - 화면
	@RequestMapping(value = "/distribution_list")
	public ModelAndView distributionList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("itTeam",dbConn.recordSet(QUERY_ROOT + ".selectIt"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("develop/distribution_list.pq");
		return mv;
	}

	//배포 진행 관리 - 리스트
	@RequestMapping(value = "/distribution_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> distributionList(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("dist_status", request.getParameterValues(Common.paramIsArray("dist_status", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("dist_server", request.getParameterValues(Common.paramIsArray("dist_server", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSql("dist_nowar", request.getParameterValues(Common.paramIsArray("dist_nowar", request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("developer",request.getParameterValues("itTeam"),"="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "case when dist_status = '1' then 1 else case when dist_status = '2' then 2 end end asc, edate desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".distributionList", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//배포 진행 관리 - 수정
	@RequestMapping(value = "/distribution_update.run", method = RequestMethod.POST)
	public ModelAndView distributionUpdateDB(ModelAndView mv, HttpServletRequest request) {
		String[] selectField = new String[]{"chkId","chkCompany"};
		List<Map<String, String>> listMap = Common.paramToList(selectField, request.getParameterMap());
		dbConn2.recordSet(QUERY_ROOT + ".distributionUpdate", listMap);
		mv.setViewName("redirect:/views/develop/distribution_list?dist_status=1");
		return mv;
	}

	//배포 진행 관리 - 취소
	@RequestMapping(value = "/distribution_cancel.run", method = RequestMethod.POST)
	public ModelAndView distributionCancelDB(ModelAndView mv, HttpServletRequest request) {
		dbConn2.recordSet(QUERY_ROOT + ".distributionCancel", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("redirect:/views/develop/distribution_list?dist_status=1");
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
