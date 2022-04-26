package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.ExcelCon;
import com.common.common.ImgCon;
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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "views/product")
public class Product {

	private final String DIR_ROOT = "product";
	private final String QUERY_ROOT = DIR_ROOT + ".query";
	private final String FILE_PATH = "/_vir0001/product_img/ini_product";


	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "imgCon")
	private ImgCon imgCon;
	@Resource(name = "excelCon")
	private ExcelCon excelCon;

	public Product(ImgCon imgCon) {this.imgCon = imgCon;}

	// 기초상품 리스트
	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("code1", dbConn.recordSet("common.query.code1", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("product/list.pq");
		return mv;
	}

	// 기초상품 리스트
	@RequestMapping(value = "/list.run")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		Map<String,String> barcodeCompletion = new HashMap();
		barcodeCompletion.put("n","barcodeCompletion is null");

		sqlItemList.add(dbConn.makeSearchSql2(request.getParameterValues("barcodeCompletion"), barcodeCompletion, null));
		sqlItemList.add(dbConn.makeSearchSql("code1", request.getParameterValues("code1"), "="));
		sqlItemList.add(dbConn.makeSearchSql("code2", request.getParameterValues("code2"), "="));
		sqlItemList.add(dbConn.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("codename", request.getParameterValues("codename"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("id", request.getParameterValues("serialId"), "="));
		sqlItemList.add(dbConn.makeSearchSql("pbrand", request.getParameterValues(Common.paramIsArray("brand",request)), "="));
		sqlItemList.add(dbConn.makeSearchSql("cost", request.getParameterValues("cost"), "="));
		sqlItemList.add(dbConn.makeSearchSql("wms_master", request.getParameterValues("wms_master"), "="));
		sqlItemList.add(dbConn.makeSearchSqlRange("rrp", request.getParameter("rrp1"), request.getParameter("rrp2")));
		sqlItemList.add(dbConn.makeSearchSqlRange("realStock", request.getParameter("realstock1"), request.getParameter("realstock2")));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		List<List<Map>> result0 = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "A.wdate desc");
		Common.PQmap(map, request);

		String iniID = "id";
		result = dbConn.recordSet(QUERY_ROOT + ".list", map);
		Map resultSub = Common.subQuery(result, iniID);
		result0 = dbConn.recordSet(QUERY_ROOT + ".listSub", resultSub);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(Common.combineRecordSet(iniID, result, result0.get(0), result0.get(1))));

		return resultMap;
	}


	//기초상품 신규등록 폼
	@RequestMapping(value = "/inputNew")
	public ModelAndView input(ModelAndView mv) {
		List<List<Map>> list = dbConn.recordSet(QUERY_ROOT + ".view", null);
		mv.addObject("code1", dbConn.recordSet("common.query.code1", null));
		mv.addObject("code4", list.get(0));

		mv.setViewName("product/input.tiles");
		return mv;
	}

	//기초상품 상세정보
	@RequestMapping(value = "/input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		String id = request.getParameter("id");

		List<List<Map>> list = dbConn.recordSet(QUERY_ROOT + ".view", id);
		mv.addObject("code1", dbConn.recordSet("common.query.code1", null));
		mv.addObject("code4", list.get(0));
		List<Map> view = list.get(1);
		mv.addObject("view", view);
		mv.addObject("rrp", list.get(2));
		mv.addObject("cost", list.get(3));
		mv.addObject("barcode", list.get(4));
		mv.addObject("inPack", list.get(5));

		List<Map> photo = list.get(6);
		mv.addObject("photo", photo);

		if (photo.size() == 0) {
			Map<String, Object> photoGetParam = new HashMap();
			photoGetParam.put("id", view.get(0).get("id"));
			photoGetParam.put("code1", view.get(0).get("code1"));
			photoGetParam.put("code2", view.get(0).get("code2"));
			photoGetParam.put("code3", view.get(0).get("code3"));

			mv.addObject("photoGet", dbConn.recordSet(QUERY_ROOT + ".photoGet", photoGetParam));
		}

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("id", id);
		mv.setViewName("product/input.tiles");
		return mv;
	}




	//기초상품 이미지 업로드
	protected List<Map<String, String>> uploadImg(String resultID, HttpServletRequest request, String[] sunser, String[] photo_id) {
		List<List<String>> tnumbnail = new ArrayList<>();
		List<Map<String, String>> uploadResult = new ArrayList<>();

		try {
			tnumbnail.add(Arrays.asList(FILE_PATH, "600", "600")); // 대상디렉토리, width, height
			tnumbnail.add(Arrays.asList(FILE_PATH + "/middle", "300", "300"));
			tnumbnail.add(Arrays.asList(FILE_PATH + "/small", "150", "150"));

			for (int i = 0, lenSunser = sunser.length; i < lenSunser; i++) {
				List<String> addFile = imgCon.uploadImgFile(
					new ArrayList<>(Arrays.asList(((MultipartHttpServletRequest) request).getFiles("photo").get(i))),
					request, resultID, FILE_PATH + "/large", tnumbnail
				);

				String sunserValue = sunser[i];
				String photo_idValue = photo_id[i];
				if (addFile.size() > 0) {
					uploadResult.add(new HashMap() {{
						put("sunser", sunserValue);
						put("photo_id", photo_idValue);
						put("filename", addFile.get(0));
					}});
				}
			}

			return uploadResult;
		} catch (IOException e) {
			return null;
		}
	}

	//기초상품 등록
	@RequestMapping(value = "/insert.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String[] selectField = new String[]{"sunser", "photo_ini"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());

		String resultID = dbConn.recordSet(QUERY_ROOT + ".insert", map).get(0).toString();

		String[] sunser = request.getParameterValues("sunser");
		String[] photo_id = request.getParameterValues("photo_id");
		List<Map<String, String>> uploadResult = uploadImg(resultID, request, sunser, photo_id);

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("id", resultID);
		totalMap.put("newPhoto", uploadResult);
		totalMap.put("wasPhoto", list);
		totalMap.put("param", map);
		dbConn.recordSet(QUERY_ROOT + ".insertSub", totalMap);

		mv.setViewName("redirect:/views/product/input?id=" + resultID);
		return mv;
	}

	//기초상품 일괄등록
	@RequestMapping(value = "/addProduct.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView insertProductDB(ModelAndView mv, HttpServletRequest request) {
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

		try {
			List<String> dbField = Arrays.asList("code","name","rrp","cost","stock","product","madein","width","height","depth");
			List<Map<String, String>> list = excelCon.uploadExcel(mr.getFiles("addFile"), dbField, "기초코드");
			dbConn.recordSet(QUERY_ROOT + ".excelProductInsert", list);
		} catch (IOException e) {}

		mv.setViewName("redirect:/views/product/list");
		return mv;
	}

	//기초상품 수정
	@RequestMapping(value = "/update.run", method = {RequestMethod.POST})
	@Transactional
	public ModelAndView updateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		String[] selectField = new String[]{"sunser", "delimg", "photo_id", "photo_ini"};
		List<Map<String, String>> list = Common.paramToList(selectField, request.getParameterMap());

		String resultID = request.getParameter("id");

		String[] sunser = request.getParameterValues("sunser");
		String[] photo_id = request.getParameterValues("photo_id");
		List<Map<String, String>> uploadResult = uploadImg(resultID, request, sunser, photo_id);

		Map<String, Object> totalMap = new HashMap<>();
		totalMap.put("newPhoto", uploadResult);
		totalMap.put("wasPhoto", list);
		totalMap.put("param", map);
		dbConn.recordSet(QUERY_ROOT + ".update", totalMap);

		mv.setViewName("redirect:/views/product/input?id=" + resultID);
		return mv;
	}

	//기초상품 코드 변경
	@RequestMapping(value = "/changeCode.run")
	@Transactional
	@ResponseBody
	public Map<String, Object> changeCode(HttpServletRequest request, HttpServletResponse response) {
		List<Map> map = dbConn.recordSet(QUERY_ROOT + ".changeCode", null);
		map.addAll(dbConn.recordSet("common.query.code2", new HashMap(){{
			put("code1",request.getParameter("code1"));
		}}));

		Map resultMap = new HashMap();
		resultMap.put("data", map);

		return resultMap;
	}

	//기초코드 중복확인
	@RequestMapping(value = "/code_confirm.run")
	@ResponseBody
	public Map<String, Object> code_confirm(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".codeConfirm", param));

		return resultMap;
	}

	//rrp, cost 변경
	@RequestMapping(value = "/addMoney.run")
	@Transactional
	@ResponseBody
	public Map<String, Object> addMoney(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".addMoney", param));

		return resultMap;
	}


	//바코드 리스트
	@RequestMapping(value = "/barcode")
	public ModelAndView barcodeList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("code1", dbConn.recordSet("common.query.code1", null));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("product/barcode.pq");
		return mv;
	}

	//바코드 리스트
	@RequestMapping(value = "/barcode.run")
	@ResponseBody
	public Map<String, Object> barcodeList(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();

		sqlItemList.add(dbConn.makeSearchSql("barcodeCompletion", request.getParameterValues("barcodeCompletion"), "="));
		sqlItemList.add(dbConn.makeSearchSql("code1", request.getParameterValues("code1"), "="));
		sqlItemList.add(dbConn.makeSearchSql("code2", request.getParameterValues("code2"), "="));
		sqlItemList.add(dbConn.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("codename", request.getParameterValues("codename"), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		Map resultMap = new HashMap();
		List<Map> result = new ArrayList<>();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "barcodeCompletion asc");
		Common.PQmap(map, request);

		result = dbConn.recordSet(QUERY_ROOT + ".barcode", map);
		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", result);

		return resultMap;
	}

	//바코드 할당
	@RequestMapping(value = "/barcodeInsert.run")
	@Transactional
	@ResponseBody
	public Map<String, Object> barcodeInsert(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> param = Common.paramToMap(request.getParameterMap());
		Map resultMap = new HashMap();
		resultMap.put("data", dbConn.recordSet(QUERY_ROOT + ".barcodeInsert", param));

		return resultMap;
	}
}