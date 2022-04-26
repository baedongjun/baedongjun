package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
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
@RequestMapping("/views/distribution/freightCar")
public class DistributionFreightCar {

	private final String DIR_ROOT = "distributionFreightCar";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource(name = "dbConn2")
	private DbConn dbConn2;
	@Resource
	private FileCon fileCon;

	//瘤沥厘家 包府 - 拳搁
	@RequestMapping(value = "/list")
	public ModelAndView list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("distribution/freightCar/list.pq");
		return mv;
	}

	//瘤沥厘家 包府 - 府胶飘
	@RequestMapping(value = "/list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("addr1, addr2", request.getParameterValues("addr"), "like"));
		sqlItemList.add(dbConn2.makeSearchSql("name", request.getParameterValues("name"), "like"));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".list", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//瘤沥厘家 包府 - 殿废 拳搁
	@RequestMapping(value = "/input")
	public ModelAndView input(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".view", Common.paramToMap(request.getParameterMap())));
		mv.setViewName("distribution/freightCar/input.tiles");
		return mv;
	}

	//瘤沥厘家 包府 - 殿废
	@RequestMapping(value = "/insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView insertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		if (!Common.isNullOrEmpty(map.get("id"))) {
			dbConn2.recordSet(QUERY_ROOT + ".update", map);
		} else {
			dbConn2.recordSet(QUERY_ROOT + ".insert", map);
		}

		mv.setViewName("redirect:/views/distribution/freightCar/list");
		return mv;
	}

	//瘤沥厘家 包府 - 昏力
	@RequestMapping(value = "/delete.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView deleteDB(ModelAndView mv, HttpServletRequest request) {
		dbConn2.recordSet(QUERY_ROOT + ".delete", request.getParameter("id"));
		mv.setViewName("redirect:/views/distribution/freightCar/list");
		return mv;
	}

	//款价夸没 包府 - 拳搁
	@RequestMapping(value = "/car_list")
	public ModelAndView carList(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("distribution/freightCar/car_list.pq");
		return mv;
	}

	//款价夸没 包府 - 府胶飘
	@RequestMapping(value = "/car_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> carList(HttpServletRequest request, HttpServletResponse response) {

		List<String> sqlItemList = new ArrayList<>();
		sqlItemList.add(dbConn2.makeSearchSql("gubun", request.getParameterValues("gubun"), "like"));
		sqlItemList.add(dbConn2.makeSearchSqlRange("convert(varchar(10),wdate,126)", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "id desc");
		Common.PQmap(map, request);

		result = dbConn2.recordSet(QUERY_ROOT + ".carList", map);

		Common.PQresultMap(resultMap, map.get("curPage"), result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	//款价夸没 包府 - 殿废 拳搁
	@RequestMapping(value = "/car_input")
	public ModelAndView carInput(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());

		mv.addObject("list", dbConn2.recordSet(QUERY_ROOT + ".carView", map));
		mv.addObject("resultFileList", dbConn2.recordSet(QUERY_ROOT + ".carViewFile", map));
		mv.addObject("placeList", dbConn2.recordSet(QUERY_ROOT + ".placeList", map));
		mv.setViewName("distribution/freightCar/car_input.tiles");
		return mv;
	}

	//款价夸没 包府 - 殿废
	@RequestMapping(value = "/car_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView carInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn2.recordSet(QUERY_ROOT + ".carInsert", map);
		dbConn.recordSet(QUERY_ROOT + ".carSmsInsert", map);
		mv.setViewName("redirect:/views/distribution/freightCar/car_list");
		return mv;
	}

	//款价夸没 包府 - 荐沥
	@RequestMapping(value = "/car_update.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView carUpdateDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		if (map.get("gubun").equals("1")) {
			dbConn2.recordSet(QUERY_ROOT + ".carUpdate1", map);
		} else if (map.get("gubun").equals("2") || map.get("gubun").equals("3")) {
			dbConn2.recordSet(QUERY_ROOT + ".carUpdate2", map);
			dbConn.recordSet(QUERY_ROOT + ".carSmsInsert", map);
		} else {
			dbConn2.recordSet(QUERY_ROOT + ".carUpdate3", map);
		}
		mv.setViewName("redirect:/views/distribution/freightCar/car_list");
		return mv;
	}

	//款价夸没 包府 - 颇老 梅何
	@RequestMapping(value = "/file_insert.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView fileInsertDB(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		String[] divideArray = String.valueOf(map.get("divide")).split(", ");

		List<Map<String, String>> uploadResult = new ArrayList<>();
		try {
			List<String> addFile = fileCon.uploadFile(((MultipartHttpServletRequest) request).getFiles("files"), request, "req", DIR_ROOT);
			for (int i = 0; i < addFile.size(); i++) {
				String divide = divideArray[i];
				String filename = addFile.get(i);
				uploadResult.add(new HashMap() {{
					put("freightCar_id", map.get("freightCar_id"));
					put("filename", filename);
					put("divide", divide);
				}});
			}
		} catch (IOException e) {
		}

		dbConn2.recordSet(QUERY_ROOT + ".insertFiles", uploadResult);
		mv.setViewName("redirect:/views/distribution/freightCar/car_input?id=" + map.get("freightCar_id"));
		return mv;
	}

	//款价夸没 包府 - 颇老 昏力
	@RequestMapping(value = "/file_delete.run", method = RequestMethod.POST)
	@Transactional
	public ModelAndView fileDeleteDB(ModelAndView mv, HttpServletRequest request) {
		String[] selectField = new String[]{"delCheck"};
		List<Map<String, String>> listMap = Common.paramToList(selectField, request.getParameterMap());
		dbConn2.recordSet(QUERY_ROOT + ".deleteFiles", listMap);
		mv.setViewName("redirect:/views/distribution/freightCar/car_input?id=" + request.getParameter("id"));
		return mv;
	}
}
