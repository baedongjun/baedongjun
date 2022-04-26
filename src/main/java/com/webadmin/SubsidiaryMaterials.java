package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
import com.common.common.ImgCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
@RequestMapping("/views/subsidiary_materials")
public class SubsidiaryMaterials {

	private final String DIR_ROOT = "subsidiary_materials";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;

	//업체관리 - 화면
	@RequestMapping(value = "/list")
	public ModelAndView company(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		mv.addObject("companyList", dbConn2.recordSet(QUERY_ROOT + ".companyList", Common.paramToMap(request.getParameterMap())));
		mv.addObject("admin_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());

		Map<String,String[]> paramMap = new HashMap();
		paramMap.put("gubun",new String[]{"1"});
		paramMap.putAll(request.getParameterMap());

		mv.addObject("searchParam", Common.paramToSearch(paramMap));
		mv.setViewName("subsidiary_materials/list.pq");

		return mv;
	}

	//업체관리 - 화면
	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("brand", request.getParameterValues("brand"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("company", request.getParameterValues("company"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("kind", request.getParameterValues("kind"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("work", request.getParameterValues("work"), "="));
		sqlItemList.add(dbConn2.makeSearchSql("gubun", request.getParameterValues("gubun"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		Map resultMap = new HashMap();
		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		List<Map> result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//업체관리 - 등록 화면
	@RequestMapping(value = "/list_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".view1", map));
		mv.addObject("admin_id", ((MemberDTO4) authentication.getPrincipal()).get_Id());
		mv.addObject("resultFileMap", dbConn2.recordSet(QUERY_ROOT + ".fileView", map));
		mv.addObject("companyList", dbConn2.recordSet(QUERY_ROOT + ".companyList", map));
		mv.setViewName("subsidiary_materials/list_input.tiles");
		return mv;
	}

	//업체관리 - 등록
	@RequestMapping(value = "/list_input_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<List<String>> tnumbnail = new ArrayList<>();

		if("2".equals(map.get("gu"))){
			if("1".equals(map.get("day_gu"))){
				map.put("day_step1",map.get("day_1"));
				map.put("day_step2",map.get("yoil"));
				map.put("s_date",map.get("s_date1"));
			}else if("2".equals(map.get("day_gu"))){
				map.put("day_step1",map.get("day_2"));
				map.put("day_step2",map.get("yoil2"));
				map.put("s_date",map.get("s_date2"));
			}
		}

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("estimate"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("new_estimate", filename);
			}
		} catch (IOException e) {
		}

		try {
			tnumbnail.add(0, Arrays.asList(DIR_ROOT+ "/thumb", "180", "200"));
			List<String> addFile = imgCon.uploadImgFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT, tnumbnail);
			for (String filename : addFile) {
				map.put("new_name", filename);
			}
		} catch (IOException e) {
		}

		if("2".equals(map.get("gu"))){
			dbConn2.recordSet(QUERY_ROOT + ".insert1", map);
		}else{
			dbConn2.recordSet(QUERY_ROOT + ".insert2", map);
		}
		if(!Common.isNullOrEmpty(map.get("url"))){
			dbConn2.recordSet(QUERY_ROOT + ".insertHistory", map);
		}

		mv.setViewName("redirect:/views/subsidiary_materials/list");
		return mv;
	}

	//업체관리 - 수정
	@RequestMapping(value = "/list_input_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<List<String>> tnumbnail = new ArrayList<>();

		if("2".equals(map.get("gu"))){
			if("1".equals(map.get("day_gu"))){
				map.put("day_step1",map.get("day_1"));
				map.put("day_step2",map.get("yoil"));
				map.put("s_date",map.get("s_date1"));
			}else if("2".equals(map.get("day_gu"))){
				map.put("day_step1",map.get("day_2"));
				map.put("day_step2",map.get("yoil2"));
				map.put("s_date",map.get("s_date2"));
			}
		}

		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("estimate"), request, "req", DIR_ROOT);
			for (String filename : addFile) {
				map.put("new_estimate", filename);
			}
		} catch (IOException e) {
		}

		try {
			tnumbnail.add(0, Arrays.asList(DIR_ROOT+ "/thumb", "180", "200"));
			List<String> addFile = imgCon.uploadImgFile(((MultipartHttpServletRequest) request).getFiles("file"), request, "req", DIR_ROOT, tnumbnail);
			for (String filename : addFile) {
				map.put("new_name", filename);
			}
		} catch (IOException e) {
		}

		if("2".equals(map.get("gu"))){
			dbConn2.recordSet(QUERY_ROOT + ".update1", map);
		}else{
			dbConn2.recordSet(QUERY_ROOT + ".update2", map);
		}

		if(!Common.isNullOrEmpty(map.get("url"))){
			dbConn2.recordSet(QUERY_ROOT + ".insertHistory", map);
		}

		mv.setViewName("redirect:/views/subsidiary_materials/list");
		return mv;
	}

	//업체관리 - 삭제
	@RequestMapping(value = "/list_input_delete.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView deleteDB(ModelAndView mv, HttpServletRequest request) {
		dbConn2.recordSet(QUERY_ROOT + ".delete", Common.paramToMap(request.getParameterMap()));
		mv.setViewName("redirect:/views/subsidiary_materials/list");
		return mv;
	}

	//업체관리 - 등록 화면
	@RequestMapping(value = "/list_input2")
	public ModelAndView input2(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String flag = request.getParameter("flag");
		List<Map> result = new ArrayList<>();
		if("1".equals(flag)){
			result = dbConn2.recordSet(QUERY_ROOT + ".view1", map);
		}else if ("2".equals(flag)){
			result = dbConn2.recordSet(QUERY_ROOT + ".view2", map);
		}else if ("0".equals(flag)){
			result = dbConn2.recordSet(QUERY_ROOT + ".view0", map);
		}

		mv.addObject("list", result);
		mv.addObject("resultFileMap", dbConn2.recordSet(QUERY_ROOT + ".fileView", map));
		mv.addObject("companyList", dbConn2.recordSet(QUERY_ROOT + ".companyList", map));
		mv.addObject("returnParam", map);
		mv.setViewName("subsidiary_materials/list_input2.tiles");
		return mv;
	}

	//업체관리 - 등록
	@RequestMapping(value = "/list_input2_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView input2InsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("chk1", "on".equals(map.get("chk1")) ? "checked" : "");
		map.put("chk2", "on".equals(map.get("chk2")) ? "checked" : "");
		map.put("chk_date1",Common.defaultValue(map.get("chk_date1"), ""));
		map.put("chk_date2",Common.defaultValue(map.get("chk_date2"), ""));
		dbConn2.recordSet(QUERY_ROOT + ".insert3", map);
		mv.setViewName("redirect:/views/subsidiary_materials/calendar_chk");
		return mv;
	}

	//업체관리 - 등록
	@RequestMapping(value = "/list_input2_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView input2UpdateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("chk1", "on".equals(map.get("chk1")) ? "checked" : "");
		map.put("chk2", "on".equals(map.get("chk2")) ? "checked" : "");
		map.put("chk_date1",Common.defaultValue(map.get("chk_date1"), ""));
		map.put("chk_date2",Common.defaultValue(map.get("chk_date2"), ""));
		dbConn2.recordSet(QUERY_ROOT + ".update3", map);
		mv.setViewName("redirect:/views/subsidiary_materials/calendar_chk");
		return mv;
	}
}
