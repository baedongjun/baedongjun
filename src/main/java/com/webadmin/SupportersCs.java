package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ImgCon;
import com.common.member.MemberDTO4;
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
@RequestMapping("/views/supporters/cs")
public class SupportersCs {

	private final String DIR_ROOT = "supportersCs";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource
	private ImgCon imgCon;
	@Resource
	private MemberDTO4 memberDTO4;

	@RequestMapping(value = "/notice_list")
	public ModelAndView noticeList(ModelAndView mv,HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("supporters/cs/notice_list.pq");
		return mv;
	}

	@RequestMapping(value = "/notice_list.run", method = {RequestMethod.POST},produces = "application/json")
	@ResponseBody
	public Map<String, Object> noticeList(HttpServletResponse response, HttpServletRequest request) {
		Map<String,String> param = new HashMap<>();
		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();

		String top_gb = Common.defaultValue(request.getParameter("top_gb"), "1");

		searchList2.add(dbConn.makeSearchSql("title",request.getParameterValues("searchContent"),"like"));
		searchList2.add(dbConn.makeSearchSql("content",request.getParameterValues("searchContent"),"like"));
		searchList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ",searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		searchList.add(dbConn.makeSearchSqlRange("wdate", request.getParameter("date1"), request.getParameter("date2")));
		searchList.add(dbConn.makeSearchSql("notice_gb", request.getParameterValues("notice_gb"), "="));
		searchList.add(dbConn.makeSearchSql("divide_supporters", request.getParameterValues("supporters_type"), "like"));
		if (top_gb.equals("0")) {
			searchList.add(" top_gb = 0 ");
		}
		searchList.removeAll(Collections.singleton(null));

		param.put("sqlItem", String.join(" and ", searchList) + sqlItem2);
		param.put("orderBy", "id desc");
		Common.PQmap(param,request);

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".noticeList", param);

		Map<String, Object> returnParam = new HashMap<>();
		Common.PQresultMap(returnParam,param.get("curPage"),list);
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}

	@RequestMapping(value = "/notice_input")
	public ModelAndView noticeInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".noticeView", param));
		mv.setViewName("supporters/cs/notice_input.tiles");
		return mv;
	}

	@RequestMapping(value = "/notice_insert.run", method = {RequestMethod.POST},produces = "application/json")
	public String noticeInsert(HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		param.put("disp_yn", Common.defaultValue(param.get("disp_yn"),"0"));
		param.put("top_gb", Common.defaultValue(param.get("top_gb"),"1"));
		String divide_supporters = Common.defaultValue(param.get("divide_supporters"),"");
		param.put("divide_supporters", Common.addString("|",divide_supporters.replace(", ","|, |"),"|"));

		if("0".equals(param.get("top_gb"))){ //최상단 노출일때
			List<Map<String,Object>> topCount = dbConn.recordSet(QUERY_ROOT+".topCntCount");
			param.put("cnt", String.valueOf(topCount.get(0).get("cnt")));
			if (Integer.parseInt(param.get("cnt"))> 10){
				dbConn.recordSet(QUERY_ROOT+".topCntUpdate",param);
			}
		}

		if (Common.isNullOrEmpty(param.get("id"))) {
			dbConn.recordSet(QUERY_ROOT + ".noticeInsert", param);
		} else {
			dbConn.recordSet(QUERY_ROOT + ".noticeUpdate", param);
		}
		return "redirect:/views/supporters/cs/notice_list";
	}

	@RequestMapping(value = "/notice_delete.run", method = {RequestMethod.POST},produces = "application/json")
	public String noticeDelete(HttpServletRequest request) {
		dbConn.recordSet(QUERY_ROOT + ".noticeDelete", request.getParameter("id"));
		return "redirect:/views/supporters/cs/notice_list";
	}

	////////faq

	@RequestMapping(value = "/faq_list")
	public ModelAndView faqList(ModelAndView mv,HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("supporters/cs/faq_list.pq");
		return mv;
	}

	@RequestMapping(value = "/faq_list.run", method = {RequestMethod.POST},produces = "application/json")
	@ResponseBody
	public Map<String, Object> faqList(HttpServletRequest request, HttpServletResponse response) {
		Map<String,String> param = new HashMap<>();
		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();

		searchList2.add(dbConn.makeSearchSql("title",request.getParameterValues("search3"),"like"));
		searchList2.add(dbConn.makeSearchSql("content",request.getParameterValues("search3"),"like"));
		searchList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ",searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? "and (" + sqlItem2 + ")" : "";

		searchList.add(dbConn.makeSearchSql("faq_gb", request.getParameterValues("faq_gb"), "="));
		searchList.add(dbConn.makeSearchSql("divide_supporters", request.getParameterValues("divide_supporters"), "like"));
		searchList.removeAll(Collections.singleton(null));

		param.put("sqlItem", String.join(" and ", searchList) + sqlItem2);
		param.put("orderBy", "id desc");
		Common.PQmap(param,request);

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".faqList", param);

		Map<String, Object> returnParam = new HashMap<>();
		Common.PQresultMap(returnParam,param.get("curPage"),list);
		returnParam.put("data", Common.nullToEmpty(list));

		return returnParam;
	}

	@RequestMapping(value = "/faq_input")
	public ModelAndView faqInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".faqView", param));
		mv.setViewName("supporters/cs/faq_input.tiles");

		return mv;
	}

	@RequestMapping(value = "/faq_insert.run", method = {RequestMethod.POST},produces = "application/json")
	public String faqInsert(HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		String divide_supporters = Common.defaultValue(param.get("divide_supporters"),"");
		param.put("divide_supporters", Common.addString("|",divide_supporters.replace(", ","|, |"),"|"));

		if (Common.isNullOrEmpty(param.get("id"))) {
			dbConn.recordSet(QUERY_ROOT + ".faqInsert", param);
		} else {
			dbConn.recordSet(QUERY_ROOT + ".faqUpdate", param);
		}
		return "redirect:/views/supporters/cs/faq_list";
	}

	@RequestMapping(value = "/faq_delete.run", method = {RequestMethod.POST},produces = "application/json")
	public String faqDelete(HttpServletRequest request) {
		dbConn.recordSet(QUERY_ROOT + ".faqDelete", request.getParameter("id"));
		return "redirect:/views/supporters/cs/faq_list";
	}

	////////// qanda

	@RequestMapping(value = "/qanda_list")
	public ModelAndView qandaList(HttpServletRequest request, ModelAndView mv) {
		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();

		String[] depth ={"a"};

		searchList2.add(dbConn.makeSearchSql("userid",request.getParameterValues("search3"),"like"));
		searchList2.add(dbConn.makeSearchSql("username",request.getParameterValues("search3"),"like"));
		searchList2.add(dbConn.makeSearchSql("title",request.getParameterValues("search3"),"like"));
		searchList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ",searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? "and (" + sqlItem2 + ")" : " and " + dbConn.makeSearchSql("depth",depth,"=");

		searchList.add(dbConn.makeSearchSqlRange("wdate", request.getParameter("date1"), request.getParameter("date2")));
		searchList.add(dbConn.makeSearchSql("cnt", request.getParameterValues("cnt"), "="));
		searchList.add(dbConn.makeSearchSql("qanda_gb", request.getParameterValues("qanda_gb"), "="));
		searchList.add(dbConn.makeSearchSql("divide_supporters", request.getParameterValues("supporters_type"), "="));
		searchList.removeAll(Collections.singleton(null));

		Map<String,Object> param = new HashMap<>();

		param.put("sqlItem", String.join(" and ", searchList) + sqlItem2);
		param.put("orderBy", "num desc, depth asc");
		param.put("totalRecords",Optional.ofNullable(request.getParameter("totalRecords")).orElse("0"));
		param.put("curPage", Optional.ofNullable(request.getParameter("curPage")).orElse("1"));
		param.put("pageSize", Optional.ofNullable(request.getParameter("pageSize")).orElse("10"));
		param.put("telchk", Common.defaultValue(param.get("telchk"),"n"));

		List<Map<String, Object>> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".qandaPaging", param);

		List<String> nums = new ArrayList<>();
		for (Map<String, Object> num : list) nums.add(String.valueOf(num.get("num")));

		Map<String, String> numList = new HashMap<>();
		numList.put("num", String.join(",", nums));

		List<Map> result = new ArrayList<>();
		result = dbConn.recordSet(QUERY_ROOT + ".qandaList", numList);

		param.put("totalRecords", list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("returnParam",param);
		mv.addObject("pageSize",Optional.ofNullable(request.getParameter("pageSize")).orElse("10"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(result));

		mv.setViewName("supporters/cs/qanda_list.tiles");
		return mv;
	}

	@RequestMapping(value = "/qanda_input")
	public ModelAndView qandaInsert(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".qandaView", param));
		mv.setViewName("supporters/cs/qanda_input.tiles");
		return mv;
	}

	@RequestMapping(value = "/qanda_input.run", method = {RequestMethod.POST},produces = "application/json")
	@Transactional
	public String qandaInsert(HttpServletRequest request, Authentication authentication) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());

		if ("y".equals(param.get("work"))){
			if ("true".equals(param.get("chk"))){
				dbConn.recordSet(QUERY_ROOT + ".workYChkTrue", param);
			}else{
				dbConn.recordSet(QUERY_ROOT + ".workChkFalse", param);
			}
		}else{
			param.put("id",request.getParameter("id"));
			if (Common.isNullOrEmpty(param.get("id"))){
				memberDTO4 = (MemberDTO4) authentication.getPrincipal();
				param.put("supporters_applicant_id",memberDTO4.get_Id());
				dbConn.recordSet(QUERY_ROOT + ".workNInsert", param); //댓글
			}else{ //id값이 있으면
				dbConn.recordSet(QUERY_ROOT + ".workNUpdate", param); //수정
			}

			if (!Common.isNullOrEmpty(param.get("parent_id"))) {
				dbConn.recordSet(QUERY_ROOT + ".parentNull", param);
			}
		}
		return "redirect:/views/supporters/cs/qanda_list";
	}

	//editor
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

}
