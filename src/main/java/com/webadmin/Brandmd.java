package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ImgCon;
import com.common.member.MemberDTO4;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/brandmd")
public class Brandmd {
	private final String DIR_ROOT = "brandMd";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private ImgCon imgCon;
	@Resource
	private MemberDTO4 memberDTO4;

	//price_guideline_list 브랜드 가격 가이드 라인 리스트
	@RequestMapping(value = "/price_guideline_list")
	public ModelAndView priceGuidelineList(ModelAndView mv, HttpServletRequest request) {
		List<String> searchList = new ArrayList<>();
		Map<String, String> map = new HashMap<>();

		Map<String,Object> textContent = new HashMap<>();
		try {
			String path = "C:/webadmin3/src/main/webapp/views/brandmd/notice.txt";
			FileInputStream fileInputStream =  null;

			fileInputStream = new FileInputStream(path);
			byte[] readBuffer = new byte[fileInputStream.available()];
			while (fileInputStream.read(readBuffer) != -1){}
			textContent.put("content",new String(readBuffer));
			fileInputStream.close();
		}catch (Exception e){
			e.getStackTrace();
		}

		String[] yearV = {Common.defaultValue(request.getParameter("yearV"), "")};
		String[] monthV = {Common.defaultValue(request.getParameter("monthV"), "")};

		if (Common.isNullOrEmpty(yearV) || Common.isNullOrEmpty(monthV)) {
			yearV = new String[]{Common.nowDate().substring(0, 4)};
			monthV = new String[]{Common.nowDate().substring(5, 7)};
		}

		String brand = Common.defaultValue(request.getParameter("brand"), "");
		String guideline_type = Common.defaultValue(request.getParameter("guideline_type"), "");

		searchList.add(dbConn2.makeSearchSql("yearValue", yearV, "="));
		searchList.add(dbConn2.makeSearchSql("monthValue", monthV, "="));
		searchList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", searchList));

		List<Map> list = new ArrayList<>();
		list = dbConn2.recordSet(QUERY_ROOT + ".priceGuidelineList", map);

		mv.addObject("txtContent", textContent);

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(list));
		mv.addObject("guideline_type", guideline_type);
		mv.addObject("yearV", yearV);
		mv.addObject("monthV", monthV);

		mv.setViewName("brandmd/price_guideline_list.tiles");
		return mv;
	}

	@RequestMapping(value = "/price_guideline_list.run")
	public String priceGuidelineList(HttpServletRequest request){
		BufferedOutputStream bufferedOutputStream = null;
		try {
			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("C:/webadmin3/src/main/webapp/views/brandmd/notice.txt"));
			String str = request.getParameter("content");
			bufferedOutputStream.write(str.getBytes());
		}catch (Exception e){
			e.getStackTrace();
		}finally {
			try {
				bufferedOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "redirect:/views/brandmd/price_guideline_list";
	}


	//price_guideline_view 브랜드 가격 가이드 라인 view
	@RequestMapping(value = "/price_guideline_view")
	public ModelAndView priceGuidelineView(ModelAndView mv, HttpServletRequest request) {
		List<String> searchList = new ArrayList<>();
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		//검색 년, 검색 월
		String[] yearV = {Common.defaultValue(request.getParameter("yearV"), "")};
		String[] monthV = {Common.defaultValue(request.getParameter("monthV"), "")};

		//검색조건 없을시 현재 년, 월로 셋팅
		if (Common.isNullOrEmpty(yearV)) {
			yearV = new String[]{Common.nowDate().substring(0, 4)};
			monthV = new String[]{Common.nowDate().substring(5, 7)};
		}

		String[] guideline_type = {Common.defaultValue(request.getParameter("guideline_type"), "")};

		searchList.add(dbConn2.makeSearchSql("yearValue", yearV, "="));
		searchList.add(dbConn2.makeSearchSql("monthValue", monthV, "="));
		if (!Common.isNullOrEmpty(map.get("brand"))){
			String[] brand2 = map.get("brand").split(",");
			searchList.add(dbConn2.makeSearchSql("brand", brand2, "in"));
		}else{
			mv.setViewName("redirect:/views/brandmd/price_guideline_list?yearV=" + yearV[0] + "&monthV=" + monthV[0]);
			return mv;
		}
		searchList.add(dbConn2.makeSearchSql("gubun", guideline_type, "in"));
		searchList.removeAll(Collections.singleton(null));

		map.put("sqlItem", String.join(" and ", searchList));

		List<Map> list = new ArrayList<>();
		list = dbConn2.recordSet(QUERY_ROOT + ".priceGuidelineView", map);

		//calendar
		List<String> searchCal = new ArrayList<>();
		searchCal.add(dbConn2.makeSearchSql("yearValue", yearV, "="));
		searchCal.add(dbConn2.makeSearchSql("monthValue", monthV, "="));
		searchCal.removeAll(Collections.singleton(null));

		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		param.put("sqlItem", String.join(" and ", searchCal));

		List<Map> cal = new ArrayList<>();
		cal = dbConn2.recordSet(QUERY_ROOT+".priceGuidelineCalendar",param);

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(list));
		mv.addObject("cal",cal);

		mv.setViewName("brandmd/price_guideline_view.tiles");
		return mv;
	}

	//view calendar //schedule_monthly.asp
	@RequestMapping(value = "/calendar.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getCalendar(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", dbConn2.recordSet(QUERY_ROOT + ".calendar", map));

		return resultMap;
	}


	//price_guideline_input 브랜드 가격 가이드 라인 input 보기
	@RequestMapping(value = "/price_guideline_input")
	public ModelAndView priceGuidelineInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		List<String> searchList = new ArrayList<>();

		String[] yearY = {Common.defaultValue(map.get("yearV"), "")};
		String[] monthV = {Common.defaultValue(map.get("monthV"), "")};
		String[] brand = {Common.defaultValue(map.get("brand"), "28")};

		if (Common.isNullOrEmpty(yearY) || Common.isNullOrEmpty(monthV)) {
			yearY = new String[]{Common.nowDate().substring(0, 4)};
			monthV = new String[]{Common.nowDate().substring(5, 7)};
		}

		if (!Common.isNullOrEmpty(yearY) && !Common.isNullOrEmpty(monthV) && !Common.isNullOrEmpty(brand)) {
			searchList.add(dbConn2.makeSearchSql("yearValue", yearY, "="));
			searchList.add(dbConn2.makeSearchSql("monthValue", monthV, "="));
			searchList.add(dbConn2.makeSearchSql("brand", brand, "in"));
			searchList.removeAll(Collections.singleton(null));

			map.put("sqlItem", String.join(" and ", searchList));

			List<Map> list = new ArrayList<>();
			list = dbConn2.recordSet(QUERY_ROOT + ".priceGuidelineInput", map);

			mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
			mv.addObject("list", Common.nullToEmpty(list));
		}
		mv.setViewName("brandmd/price_guideline_input.tiles");
		return mv;
	}

	//price_guideline_input 브랜드 가격 가이드 라인 등록, 수정, 삭제
	@RequestMapping(value = "/price_guideline_insert.run", method = {RequestMethod.POST}, produces = "application/json")
	public String priceGuidelineInsert(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		map.put("admin_member_id", memberDTO4.get_Id());
		map.put("monthValue", map.get("monthV"));
		map.put("yearValue", map.get("yearV"));

		List<String> gid = Common.paramToArray(map.get("gid"),",");
		List<String> brand = Common.paramToArray(map.get("guideline_type"),",");

		for (int i = 0; i < brand.size(); i++) {
			if (Common.isNullOrEmpty(gid.get(i).trim()) && !Common.isNullOrEmpty(Common.defaultValue(request.getParameter("content" + i), ""))) {
				//gid가 없고, content 내용이 있으면 insert
				map.put("gubun", brand.get(i));
				map.put("content", Common.defaultValue(request.getParameter("content" + i), ""));

				dbConn2.recordSet(QUERY_ROOT + ".priceGuidelineInsert", map);
			} else if (!Common.isNullOrEmpty(gid.get(i).trim()) && Common.defaultValue(request.getParameter("content" + i), "<P>&nbsp;</P>").trim() == "<P>&nbsp;</P>") {
				//gid가 있고, content 내용이 없으면 delete
				map.put("gid", gid.get(i));
				dbConn2.recordSet(QUERY_ROOT + ".priceGuidelineDelete", map);
			} else if (!Common.isNullOrEmpty(gid.get(i).trim()) && !Common.isNullOrEmpty(Common.defaultValue(request.getParameter("content" + i), "").trim())) {
				//gid, content 둘 다 있을때  수정여부 비교
				map.put("gid", gid.get(i));
				List<Map> list = dbConn2.recordSet(QUERY_ROOT + ".priceGuidelineSelect", map);

				String content = request.getParameter("content" + i);
				if (list.get(0).get("content").equals(request.getParameter("content" + i))) {
					//content가 같으므로 (같은 내용이므로) 수정 처리 할 필요 없음
				} else {
					//수정된 내용이므로 수정일자 및 수정한 사람의 정보 update
					map.put("cdate", Common.nowDate());
					map.put("content", Common.defaultValue(request.getParameter("content" + i), ""));
					map.put("gid", gid.get(i));

					dbConn2.recordSet(QUERY_ROOT + ".priceGuidelineUpdate", map);

				}
			}
		}
		return "redirect:/views/brandmd/price_guideline_list";
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

	@RequestMapping(value = "/schedule_monthly_input")
	public ModelAndView schedule_monthly_input(HttpServletRequest request, ModelAndView mv) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String id = request.getParameter("id");

		if (!Common.isNullOrEmpty(id)) {
			mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".scheduleMonthlyInput", id));
		}

		mv.setViewName("brandmd/schedule_monthly_input.bare");
		return mv;
	}

	@RequestMapping(value = "/work_insert_DB.run", method = {RequestMethod.POST}, produces = "application/json")
	public String schedule_monthly_input(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		memberDTO4 = (MemberDTO4) authentication.getPrincipal();
		map.put("writer", memberDTO4.get_Id());


		if (map.get("choice").equals("insert")) {
			dbConn2.recordSet(QUERY_ROOT + ".scheduleMonthlyInsert", map);
		} else if (map.get("choice").equals("correct")) {
			dbConn2.recordSet(QUERY_ROOT + ".scheduleMonthlyUpdate", map);
		} else if (map.get("choice").equals("delete")) {
			dbConn2.recordSet(QUERY_ROOT + ".scheduleMonthlyDelete", map.get("id"));
		}

		return "redirect:/views/brandmd/price_guideline_view";
	}
}
