package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
import com.common.common.ImgCon;
import com.common.member.MemberDTO4;
import java.util.ArrayList;
import java.util.Calendar;
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
@RequestMapping("/views/md")
public class Md {

	private final String DIR_ROOT = "md";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;

	//정기 프로모션(월)-화면
	@RequestMapping(value = "/brandPromotion")
	public ModelAndView brandPromotion(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand());
		mv.setViewName("md/brand_promotion.tiles");
		return mv;
	}

	//정기 프로모션(월)-호출
	@RequestMapping(value = "/brandPromotion.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> brandPromotion(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<>();

		Map<String, String> map = new HashMap<>();

		map.put("sqlItem", !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("brand", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("brand", request))) : null);
		map.put("start", request.getParameter("start").substring(0, 10));
		map.put("end", request.getParameter("end").substring(0, 10));

		resultMap.put("data", dbConn2.recordSet(QUERY_ROOT + ".brandPromotion", map));

		return resultMap;
	}

	//정기 프로모션(월)-작성 화면
	@RequestMapping(value = "/brand_promotion_input")
	public ModelAndView promotionInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (!Common.isNullOrEmpty(map.get("id"))) {
			mv.addObject("viewMap", Common.nullToEmpty(dbConn2.recordSet(QUERY_ROOT + ".brandPromotionView", map)));
			//구성상품
			mv.addObject("pack",dbConn2.recordSet(QUERY_ROOT + ".promotion_pack",map));
			//증정상품
			mv.addObject("gift",dbConn2.recordSet(QUERY_ROOT+".promotion_gift", map));
		}

		mv.setViewName("md/brand_promotion_input.tiles");
		return mv;
	}

	//정기 프로모션(월)-등록
	@RequestMapping(value = "/brand_promotion_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView promotionInsert(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String,String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("param", map);
		totalMap.put("department", ((MemberDTO4) authentication.getPrincipal()).getCom_dept());
		totalMap.put("market", !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("market", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("market", request))) : null);
		totalMap.put("writer", ((MemberDTO4) authentication.getPrincipal()).getUser_name());

		List<Map<String,String>> schedule_id= dbConn2.recordSet(QUERY_ROOT + ".brand_promotion_insert", totalMap);

		List<String> pack_content_id = Common.paramToArray(map.get("pack_content_id"),",");
		List<String> basic_ratio = Common.paramToArray(map.get("basic_ratio"),",");
		List<String> change_ratio = Common.paramToArray(map.get("change_ratio"),",");

		for (int i=0; i < pack_content_id.size(); i++){
			map.put("schedule_id",schedule_id.get(0).get("schedule_id"));
			map.put("pack_content_id",pack_content_id.get(i));
			map.put("basic_ratio",basic_ratio.get(i));
			map.put("change_ratio",change_ratio.get(i));

			dbConn2.recordSet(QUERY_ROOT + ".pack_insert",map);
		}
		mv.setViewName("redirect:/views/md/brand_promotion");
		return mv;
	}

	//정기 프로모션(월)-수정
	@RequestMapping(value = "/brand_promotion_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView promotionUpdate(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("param", map);
		totalMap.put("department", ((MemberDTO4) authentication.getPrincipal()).getCom_dept());
		totalMap.put("market", !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("market", request))) ? String.join(",", request.getParameterValues(Common.paramIsArray("market", request))) : null);
		totalMap.put("writer", ((MemberDTO4) authentication.getPrincipal()).getUser_name());
		dbConn2.recordSet(QUERY_ROOT + ".brand_promotion_update", totalMap);
		mv.setViewName("redirect:/views/md/brand_promotion");
		return mv;
	}

	//정기 프로모션(월)-삭제
	@RequestMapping(value = "/brand_promotion_delete.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView promotionDelete(ModelAndView mv, HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		dbConn2.recordSet(QUERY_ROOT + ".brand_promotion_delete", map);
		mv.setViewName("redirect:/views/md/brand_promotion");
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

	//예상달성매출
	@RequestMapping(value = "/mall_sales")
	public ModelAndView listValue(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("market", Common.defaultValue(map.get("market"), "1"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.setViewName("md/mall_sales.pq");
		return mv;
	}

	@RequestMapping(value = "/mall_sales.run", produces = "application/json")
	@ResponseBody
	public Map<String, Object> listValue(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		map.put("wdate", map.get("yearV") + "-" + Common.selectZero(Integer.parseInt(map.get("monthV")), "00") + "-01");
		map.put("market", !Common.isNullOrEmpty(request.getParameter(Common.paramIsArray("market", request))) ? String.join(", ", request.getParameterValues(Common.paramIsArray("market", request))) : null);
		map.put("yesterday", Optional.ofNullable(map.get("yesterday")).orElse("0"));
		map.put("not_partner", Optional.ofNullable(map.get("not_partner")).orElse("0"));
		map.put("user_brand", ((MemberDTO4) authentication.getPrincipal()).getUser_brand());

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".mall_sales", map));

		return resultMap;
	}

	@RequestMapping(value = "/member_buy_count")
	public ModelAndView memberBuyCount(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("md/member_buy_count.pq");
		return mv;
	}

	@RequestMapping(value = "/member_buy_count.run", produces = "application/json")
	@ResponseBody
	public Map<String, Object> memberBuyCount(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("dist_status", request.getParameterValues(Common.paramIsArray("dist_status", request)), "="));
		sqlItemList.add(dbConn2.makeSearchSqlRange("A.wdate", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "A.buy_money");

		result = dbConn.recordSet(QUERY_ROOT + ".memberBuyCount", map);

		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//md_addProductList 구성상품
	@RequestMapping(value = "/md_addProductList")
	public ModelAndView mdAddProductList(ModelAndView mv, HttpServletRequest request){
		String search = Common.defaultValue(request.getParameter("search"),"noSearch");

		mv.addObject("product",dbConn.recordSet(QUERY_ROOT + ".addProductList",search));
		mv.addObject("gift",dbConn.recordSet(QUERY_ROOT + ".gift_product"));

		mv.setViewName("md/md_addProductList.bare");
		return mv;
	}

	//add_product.asp  구성상품>증정 ajax
	@RequestMapping(value = "/add_product.run")
	@ResponseBody
	public Map<String,Object> add_product(HttpServletRequest request){
		Map<String,String> map = Common.paramToMap(request.getParameterMap());

		String[] arr = map.get("c1").split("/");

		map.put("c1",arr[0]);
		map.put("c2",arr[1]);

		Map resultMap = new HashMap();
		if (map.get("selectfield").equals("p")){
			resultMap.put("data",dbConn.recordSet(QUERY_ROOT + ".addProductP",map));
		}else{
			resultMap.put("data",dbConn.recordSet(QUERY_ROOT + ".addProductElse",map));
		}

		return resultMap;
	}



	//정기 프로모션 (기간)
	@RequestMapping(value = "/brand_promotion_period")
	public ModelAndView brandPromotionPeriod(ModelAndView mv,HttpServletRequest request){
		List<String> sqlItemList = new ArrayList<>();
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		Calendar calendar = Calendar.getInstance();

		if(Common.isNullOrEmpty(map.get("sdate")) || Common.isNullOrEmpty(map.get("edate"))){
			String sDate = "";
			String eDate = "";
			sDate = Common.addString(Common.nowDate().substring(0,4),"-", Common.nowDate().substring(5,7),"-01");

			calendar.set(Integer.parseInt(Common.nowDate().substring(0,4)), Integer.parseInt(Common.nowDate().substring(4,6))-1,1);
			eDate = Common.addString(Common.nowDate().substring(0,4),"-", Common.nowDate().substring(5,7),"-",String.valueOf(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)));

			map.put("sdate",sDate);  //검색 sdate (검색 값이 없는 경우)
			map.put("edate",eDate); // 검색 edate (검색 값이 없는 경우)
		}

		sqlItemList.add(dbConn2.makeSearchSql("category_site_id",request.getParameterValues("brand"),"in"));
		//sqlItemList.add(dbConn2.makeSearchSql("subject",search,"like"));   //왜 있는지 확인해볼것 (asp에 있음)
		sqlItemList.removeAll(Collections.singleton(null));


		map.put("sqlItem",String.join(" and ",sqlItemList));
		map.put("sch_menu","MD1");

		List<Map> result = new ArrayList<>();
		result = dbConn2.recordSet(QUERY_ROOT + ".brand_promotion_period",map);

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(result));  //list 안에 sdate, edate 값 (list 각각 날짜 값)
		mv.addObject("brand",request.getParameter("brand"));
		mv.addObject("sch_menu","PMD1");
		mv.addObject("sDate",map.get("sdate"));
		mv.addObject("eDate",map.get("edate"));

		mv.setViewName("md/brand_promotion_period.tiles");
		return mv;
	}


	//자사몰 전용>회원통계>등급별 회원현황
	@RequestMapping(value = "/member_value")
	public ModelAndView member_value(ModelAndView mv, HttpServletRequest request) {
		Map<String,String> map = Common.paramToMap(request.getParameterMap());
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".member_value", map);

		mv.addObject("list", Common.nullToEmpty(result));
		mv.setViewName("md/member_value.tiles");
		return mv;
	}
}
