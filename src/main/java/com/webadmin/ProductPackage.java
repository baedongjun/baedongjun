package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ImgCon;
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
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "views/product")
public class ProductPackage {

	private final String DIR_ROOT = "product";
	private final String QUERY_ROOT = DIR_ROOT + "Package.query";
	private final String FILE_PATH = "/_vir0001/product_img/ini_product";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "imgCon")
	private ImgCon imgCon;


	// 단품/팩키지 리스트
	@RequestMapping(value = "/package_list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("code1", dbConn.recordSet("common.query.code1", null));
		mv.addObject("categorySite", dbConn.recordSet("common.query.categorySite", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("product/package_list.pq");
		return mv;
	}

	// 단품/팩키지 리스트
	@RequestMapping(value = "/package_list.run")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		List<String> sqlItemList2 = new ArrayList<>();

		sqlItemList.add(dbConn.makeSearchSql("id", request.getParameterValues("pack_content_id"), "="));
		sqlItemList.add(dbConn.makeSearchSql("goods_name", request.getParameterValues("goods_name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("brand", request.getParameterValues("brand"), "="));
		sqlItemList.removeAll(Collections.singleton(null));

		sqlItemList2.add(dbConn.makeSearchSql("code1", request.getParameterValues("code1"), "="));
		sqlItemList2.add(dbConn.makeSearchSql("code2", request.getParameterValues("code2"), "="));
		sqlItemList2.add(dbConn.makeSearchSql("category_site_id", request.getParameterValues("categorySite"), "="));
		sqlItemList2.add(dbConn.makeSearchSql("category_name_id", request.getParameterValues("categoryName"), "="));
		sqlItemList2.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		List<List<Map>> result0 = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("sqlItem2", String.join(" and ", sqlItemList2));
		map.put("orderBy", "A.id desc");
		Common.PQmap(map, request);

		String iniID = "pack_content_id";
		result = dbConn.recordSet(QUERY_ROOT + ".list", map);
		result0 = dbConn.recordSet(QUERY_ROOT + ".listSub", Common.subQuery(result, iniID));

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(Common.combineRecordSet(iniID, result, result0.get(0), result0.get(1), result0.get(2), result0.get(3), result0.get(4))));

		return resultMap;
	}

	//단품/팩키지 신규등록 폼
	@RequestMapping(value = "/package_inputNew")
	public ModelAndView input(ModelAndView mv) {
		mv.addObject("categoryFull", dbConn.recordSet("common.query.categoryFull", null));
		mv.addObject("code1", dbConn.recordSet("common.query.code1", null));
		mv.setViewName("product/package_input.tiles");
		return mv;
	}

	//단품/팩키지 상세정보
	@RequestMapping(value = "/package_input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		String id = request.getParameter("id");
		String mall = request.getParameter("mall");
		List<List<Map>> list = dbConn.recordSet(QUERY_ROOT + ".view", id);

		mv.addObject("code1", dbConn.recordSet("common.query.code1", null));
		mv.addObject("categoryFull", dbConn.recordSet("common.query.categoryFull", null));
		mv.addObject("view", list.get(0));
		mv.addObject("photo", list.get(1));
		List<Map> inProduct = list.get(2);
		mv.addObject("inProduct", inProduct);
		mv.addObject("productId", list.get(3));
		mv.addObject("inCategory", list.get(4));

		mv.addObject("id", id);
		mv.addObject("mall", mall);
		mv.setViewName("product/package_input.tiles");
		return mv;
	}


	//단품/팩키지 등록
	@RequestMapping(value = "/packageInsert.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map map = Common.paramToMap(request.getParameterMap());

		String[] productField = new String[]{"sunser", "free", "code1", "code2", "code3"};
		List<Map> productValue = Common.paramToMulti(productField, request.getParameterValues(Common.paramIsArray("product_name",request)), "/");

		String[] selectField = new String[]{"sunser", "photo_ini"};
		List<Map<String, String>> wasPhoto = Common.paramToList(selectField, request.getParameterMap());

		String resultID = dbConn.recordSet(QUERY_ROOT + ".insert", map).get(0).toString();

		String[] sunser = request.getParameterValues("sunser");
		String[] photo_id = request.getParameterValues("photo_id");
		Product product = new Product(imgCon);
		List<Map<String, String>> newPhoto = product.uploadImg(resultID, request, sunser, photo_id);

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("id", resultID);
		totalMap.put("newPhoto", newPhoto);
		totalMap.put("wasPhoto", wasPhoto);
		totalMap.put("param", map);
		totalMap.put("product_name", productValue);
		totalMap.put("product_id", Common.paramToArray(request.getParameter("product_id"),","));
		totalMap.put("category_name", request.getParameterValues(Common.paramIsArray("category_name",request)));
		dbConn.recordSet(QUERY_ROOT + ".insertSub", totalMap);

		mv.setViewName("redirect:/views/product/package_input?id=" + resultID);
		return mv;
	}

	//단품/팩키지 수정
	@RequestMapping(value = "/packageUpdate.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String resultID = request.getParameter("pack_content_id");

		String[] selectField = new String[]{"sunser", "delimg", "photo_id", "photo_ini"};
		List<Map<String, String>> wasPhoto = Common.paramToList(selectField, request.getParameterMap());

		String[] sunser = request.getParameterValues("sunser");
		String[] photo_id = request.getParameterValues("photo_id");
		Product product = new Product(imgCon);
		List<Map<String, String>> newPhoto = product.uploadImg(resultID, request, sunser, photo_id);

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("id", resultID);
		totalMap.put("newPhoto", newPhoto);
		totalMap.put("wasPhoto", wasPhoto);
		totalMap.put("param", map);
		totalMap.put("category_name", request.getParameterValues(Common.paramIsArray("category_name",request)));
		dbConn.recordSet(QUERY_ROOT + ".update", totalMap);

		mv.setViewName("redirect:/views/product/package_input?id=" + resultID);
		return mv;
	}



	//기초상품 추가
	@RequestMapping(value = "/addOption1.run")
	@Transactional
	@ResponseBody
	public Map<String, Object> addOption1(HttpServletRequest request, HttpServletResponse response) {
		String[] selectField = new String[]{"sunser", "free", "code1", "code2", "code3"};
		List<Map> list = Common.paramToMulti(selectField, request.getParameterValues(Common.paramIsArray("product_name",request)), "/");
		List<Map> result = new ArrayList<>();
		List<Map> result0 = new ArrayList<>();

		Map resultMap = new HashMap();
		result = dbConn.recordSet(QUERY_ROOT + ".addProduct1", list);
		Map resultSub = Common.subQuery(result, "id");
		result0 = dbConn.recordSet(QUERY_ROOT + ".addProductSub", resultSub);

		resultMap.put("data", result);
		resultMap.put("data2", result0);
Exception exception = new Exception("asdfasdf");
		return resultMap;
	}

	//기초상품 추가(product_id 직접 추가)
	@RequestMapping(value = "/addOption2.run")
	@Transactional
	@ResponseBody
	public Map<String, Object> addOption2(HttpServletRequest request, HttpServletResponse response) {
		Map resultMap = new HashMap();
		List<Map> result = new ArrayList<>();
		List<Map> result0 = new ArrayList<>();

		result = dbConn.recordSet(QUERY_ROOT + ".addProduct2", new HashMap() {{
			put("product_id", request.getParameter("product_id"));
		}});
		Map resultSub = Common.subQuery(result, "id");
		result0 = dbConn.recordSet(QUERY_ROOT + ".addProductSub", resultSub);

		resultMap.put("data", result);
		resultMap.put("data2", result0);

		return resultMap;
	}

	//임의셋팅 기초상품 -> 단품/팩키지 매칭
	@RequestMapping(value = "/viewPackMatching.run")
	@ResponseBody
	public Map<String, Object> viewPackMatching(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> map = new HashMap();
		Map resultMap = new HashMap();

		map.put("product_id", request.getParameter("product_id"));
		map.put("pack_content_id", request.getParameter("art_con1"));

		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".viewPackMatching", map));

		return resultMap;
	}
}
