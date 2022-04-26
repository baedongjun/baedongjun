package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.common.FileCon;
import com.common.common.ImgCon;
import com.common.member.MemberDTO4;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/views/site")
public class Site {
	private final String DIR_ROOT = "site";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name= "dbConn")
	private DbConn dbConn;
	@Resource
	private FileCon fileCon;
	@Resource
	private ImgCon imgCon;

	//게시판관리 > 공지사항/이벤트
	@RequestMapping(value = "/notice_list_new")
	public ModelAndView notice_list(ModelAndView mv, HttpServletRequest request){
		Map<String,String> param = Common.paramToMap(request.getParameterMap());
		Map<String,Object> map = new HashMap<>();

		List<String> searchList = new ArrayList<>();
		List<String> searchList2 = new ArrayList<>();
		List<String> searchList3 = new ArrayList<>();

		String sqlItem3 = "";

		List<String> searchRank = new ArrayList<>();

		String sql_item_inner = "";
		String[] category_request = {};
		String[] brand_request = {};

		if (!Common.isNullOrEmpty(request.getParameter("userrank"))){
				List<String> rankArr = Common.paramToArray(param.get("userrank"),", ");

			for (int i=0; i<rankArr.size(); i++){
				category_request =new String[]{Common.addString("@",rankArr.get(i),"@")};
				searchRank.add(dbConn.makeSearchSql("userRank",category_request, "like"));

				if (Common.isNullOrEmpty(sql_item_inner)){
					sql_item_inner = searchRank.get(i);
				}else{
					sql_item_inner = sql_item_inner + "or" + searchRank.get(i);
				}
			}
			searchList.add(Common.addString(" and idx in (select notice_ini_id from [petitelindb]..[tbl_board_notice_content] where 1=1 and ",sql_item_inner,")"));

		}else{
			searchList.add(" and idx in (select notice_ini_id from [petitelindb]..[tbl_board_notice_content] where 1=1)");
		}

		if (!Common.isNullOrEmpty(request.getParameter("MD_brand"))){
			List<String> brandArr = Common.paramToArray(param.get("MD_brand"),", ");

			for(int i=0; i<brandArr.size(); i++){
				brand_request = new String[]{Common.addString("@",brandArr.get(i),"@")};
				searchList3.add(dbConn.makeSearchSql("category_site",brand_request,"like"));

				sqlItem3 = String.join(" or ",searchList3);
				sqlItem3 = (!Common.isNullOrEmpty(sqlItem3)) ? " and (" + sqlItem3 + ")" : "";
			}
		}

		searchList.add(dbConn.makeSearchSql("title",request.getParameterValues("title"),"like"));
		searchList.add(dbConn.makeSearchSql("Notice_gb",request.getParameterValues("Notice_gb"),"="));
		searchList.removeAll(Collections.singleton(null));

		if (!Common.isNullOrEmpty(request.getParameter("wdate1")) || !Common.isNullOrEmpty(request.getParameter("wdate2"))){
			searchList2.add(dbConn.makeSearchSqlRange("startdate",request.getParameter("wdate1"),request.getParameter("wdate2")));
			searchList2.add(dbConn.makeSearchSqlRange("enddate",request.getParameter("wdate1"),request.getParameter("wdate2")));
			searchList2.removeAll(Collections.singleton(null));
		}
		String sqlItem2 = String.join(" or ",searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		map.put("sqlItem",String.join(" and ",searchList) + sqlItem2 + sqlItem3);
		map.put("orderBy","idx desc");
		map.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"),"0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"),"20"));

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".noticeList",map);

		String str_id = "";
		for (int i=0; i<list.size(); i++){

			if (Common.isNullOrEmpty(str_id)){
				str_id = str_id + list.get(i).get("idx");
			}else{
				str_id = str_id + "," + list.get(i).get("idx");
			}
		}

		List<Map> strId = new ArrayList<>();
		if (!Common.isNullOrEmpty(str_id)){
			strId = dbConn.recordSet(QUERY_ROOT+ ".strId",str_id);
		}

		map.put("totalRecords",list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam",map);
		mv.addObject("list", Common.nullToEmpty(list));
		mv.addObject("strId", Common.nullToEmpty(strId));

		mv.setViewName("site/notice_list_new.tiles");
		return mv;
	}

	//게시판관리 > 공지사항/이벤트 input 페이지
	@RequestMapping(value = "/notice_insert_new")
	public ModelAndView notice_insert(ModelAndView mv,HttpServletRequest request){
		Map<String,String> map = Common.paramToMap(request.getParameterMap());

		if (!Common.isNullOrEmpty(map.get("idx"))){ //idx값이 있으면 (신규등록이 아니면)
			List<Map> list = dbConn.recordSet(QUERY_ROOT + ".contentInsert",map);

			for (int i=0; i < list.size(); i++){
				mv.addObject("list"+(i+1),list.get(i));

				mv.addObject("content"+(i+1),list.get(i).get("content"));
				mv.addObject("userrank"+(i+1),String.valueOf(list.get(i).get("userRank")).replace("@",""));
				mv.addObject("content_id"+(i+1),list.get(i).get("content_id"));
				mv.addObject("subject_img"+(i+1),list.get(i).get("title_img"));
			}

			String category_site =String.valueOf(list.get(0).get("category_site")).replace("@@",", ").replace("@","");
			mv.addObject("category",category_site);
			mv.addObject("writer",list.get(0).get("writer"));

		}else{
			mv.addObject("writer","PETITELIN");
		}

		mv.setViewName("site/notice_insert_new.tiles");
		return mv;
	}

	//게시판관리 > 공지사항/이벤트 등록, 수정, 삭제
	@RequestMapping(value = "/notice_insert_new.run", method = {RequestMethod.POST}, produces = "application/json")
	public ModelAndView notice_insertDB(ModelAndView mv, HttpServletRequest request){
		Map<String,String> map = Common.paramToMap(request.getParameterMap());
		MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;

		map.put("htm_chk", Common.defaultValue(map.get("htm_chk"),""));
		map.put("top_gb", Common.defaultValue(map.get("top_gb"),""));
		map.put("main_gb", Common.defaultValue(map.get("main_gb"),""));

		if (!Common.isNullOrEmpty(map.get("MD_brand"))){ //적용 사이트가 빈값이 아니면
			map.put("category_site", Common.addString("@",map.get("MD_brand").replace(", ","@@"),"@"));
		}

		//상품코드
		String product_gubun = "";
		String product_product = "";

		List<String> product_gubun_Arr = Common.paramToArray(map.get("product_gubun"),", ");
		List<String> product_product_Arr = Common.paramToArray(map.get("product_product"),", ");
		for (int i = 0; i <product_gubun_Arr.size(); i++){
			if (!Common.isNullOrEmpty(product_gubun)){
				product_gubun = Common.addString(product_gubun,"||",product_gubun_Arr.get(i));
				product_product = Common.addString(product_product,"||",product_product_Arr.get(i));
			}else{
				product_gubun = product_gubun_Arr.get(i);
				product_product = product_product_Arr.get(i);
			}
		}
		map.put("product_gubun",product_gubun);
		map.put("product_product",product_product);


		//기존 사이트에 출력될 키값 임시 인서트 ..
		String ShowSiteTxt = "";
		String ShowSiteCode = "";
		String ShowSiteAll = map.get("MD_brand");

		if (ShowSiteAll.contains("38")){ ShowSiteCode = ShowSiteCode + "AMT, "; }
		if (ShowSiteAll.contains("36")){ ShowSiteCode = ShowSiteCode + "PBC, "; }
		if (ShowSiteAll.contains("31")){ ShowSiteCode = ShowSiteCode + "CDL, "; }
		if (ShowSiteAll.contains("28")){ ShowSiteCode = ShowSiteCode + "EPE, "; }
		if (ShowSiteAll.contains("29")){ ShowSiteCode = ShowSiteCode + "TT, "; }
		if (ShowSiteAll.contains("32")){ ShowSiteCode = ShowSiteCode + "KP, "; }
		if (ShowSiteAll.contains("33")){ ShowSiteCode = ShowSiteCode + "EVV, "; }
		if (ShowSiteAll.contains("43")){ ShowSiteCode = ShowSiteCode + "AT, "; }
		if (ShowSiteAll.contains("44")){ ShowSiteCode = ShowSiteCode + "MG, "; }
		if (ShowSiteAll.contains("45")){ ShowSiteCode = ShowSiteCode + "VI, "; }
		if (ShowSiteAll.contains("47")){ ShowSiteCode = ShowSiteCode + "JC, "; }
		map.put("ShowSiteCode",ShowSiteCode);

		if (ShowSiteCode.contains("AMT")){ ShowSiteTxt = ShowSiteTxt + "종합몰, "; }
		if (ShowSiteCode.contains("PBC")){ ShowSiteTxt = ShowSiteTxt + "파워블로거클럽, "; }
		if (ShowSiteCode.contains("CDL")){ ShowSiteTxt = ShowSiteTxt + "코들라이프, "; }
		if (ShowSiteCode.contains("EPE")){ ShowSiteTxt = ShowSiteTxt + "엘리펀트이어스, "; }
		if (ShowSiteCode.contains("TT")){ ShowSiteTxt = ShowSiteTxt + "타이니통스, "; }
		if (ShowSiteCode.contains("KP")){ ShowSiteTxt = ShowSiteTxt + "킨더스펠, "; }
		if (ShowSiteCode.contains("EVV")){ ShowSiteTxt = ShowSiteTxt + "에바비바, "; }
		if (ShowSiteCode.contains("at")){ ShowSiteTxt = ShowSiteTxt + "에티튜드, "; }
		if (ShowSiteCode.contains("MG")){ ShowSiteTxt = ShowSiteTxt + "밀로앤개비, "; }
		if (ShowSiteCode.contains("VI")){ ShowSiteTxt = ShowSiteTxt + "아이비, "; }
		if (ShowSiteCode.contains("JC")){ ShowSiteTxt = ShowSiteTxt + "젤리캣, "; }
		map.put("ShowSiteTxt",ShowSiteTxt);

		//파워블로거 심사 일 경우 midx
		String midx = "";
		if (map.get("PB_Simsa").equals("1")){
			midx = "92";
		}else if (ShowSiteAll.contains("36")){
			midx = "3";
		}else{
			midx = "9";
		}
		map.put("midx",midx);

		//내용1 _배너 이미지
		List<MultipartFile> mf = mr.getFiles("file1");
		try{
			List<String> upFile = fileCon.uploadFile(mf, request, "Notice_1", DIR_ROOT);
			for (String fileName : upFile) map.put("title_img1",fileName);
		}catch (IOException e){
			e.printStackTrace();
		}

		//내용2 _배너 이미지
		List<MultipartFile> mf2= mr.getFiles("file2");
		try{
			List<String> upFile = fileCon.uploadFile(mf2, request, "Notice_2", DIR_ROOT);
			for (String fileName : upFile) map.put("title_img2",fileName);
		}catch (IOException e){
			e.printStackTrace();
		}

		if (!Common.isNullOrEmpty(map.get("wdate2"))){
			map.put("wdate2", Common.addString(map.get("wdate2").substring(0,10)," 23:59:59"));
		}

		if (map.get("choice").equals("insert")){
			if (map.get("notice_gb").equals("1")){
				dbConn.recordSet(QUERY_ROOT + ".updateSort");
			}
			map.put("Maxsort","");
			List<Map<String,String>> ini_id = dbConn.recordSet(QUERY_ROOT + ".noticeInsert",map);

			map.put("ini_id",ini_id.get(0).get("ini_id")); //@@identity

			//내용입력
			if (!Common.isNullOrEmpty(map.get("content1"))){
				map.put("userRank1", Common.addString("@",map.get("userrank1").replace(", ","@,@"),"@"));

				dbConn.recordSet(QUERY_ROOT + ".content1Insert",map);
			}

			if (!Common.isNullOrEmpty(map.get("content2"))){
				map.put("userRank2", Common.addString("@",map.get("userrank2").replace(", ","@,@"),"@"));

				dbConn.recordSet(QUERY_ROOT + ".content2Insert",map);
			}
		}else if(map.get("choice").equals("modify")){
			//정보 업데이트
			if (map.get("notice_gb").equals(1)){

				try{
					Date enddate = new SimpleDateFormat("yyyy-MM-dd").parse(Common.getDate(map.get("enddate")));
					Date nowdate = new SimpleDateFormat("yyyy-MM-dd").parse(Common.nowDate());
					if (enddate.getTime() >= nowdate.getTime()){
						dbConn.recordSet(QUERY_ROOT + ".eventMaGam0",map.get("id"));
					}else{
						dbConn.recordSet(QUERY_ROOT + ".eventMaGam1",map.get("id"));
					}
				}catch (ParseException e){
					e.printStackTrace();
				}
			}
			dbConn.recordSet(QUERY_ROOT + ".noticeUpdate",map);

			//첫번째 내용 수정일 경우
			map.put("userRank1", Common.addString("@",map.get("userrank1").replace(", ","@,@"),"@"));
			dbConn.recordSet(QUERY_ROOT + ".content1Update",map);

			//두번째 내용 수정 또는 신규입력
			if (map.get("content2_choice").equals("insert")){ //신규입력
				map.put("userRank2", Common.addString("@",map.get("userrank2").replace(", ","@,@"),"@"));
				dbConn.recordSet(QUERY_ROOT + ".content2Insert2",map);
			}else if (map.get("content2_choice").equals("correct")){ //수정
				map.put("userRank2", Common.addString("@",map.get("userrank2").replace(", ","@,@"),"@"));
				dbConn.recordSet(QUERY_ROOT+ ".content2Update",map);
			}else if (map.get("content2_choice").equals("delete")){ //삭제
				dbConn.recordSet(QUERY_ROOT +".content2Delete",map.get("content_id2"));
			}
		}else if (map.get("choice").equals("delete")){
			dbConn.recordSet(QUERY_ROOT +".noticeDelete",map.get("id"));
		}

		mv.setViewName("redirect:/views/site/notice_list_new");
		return mv;
	}

	//종합몰 이벤트 순서 정하기 (notice_sort)
	@RequestMapping(value = "/notice_sort")
	public ModelAndView notice_sort(HttpServletRequest request,ModelAndView mv){
		Map<String,String> map = Common.paramToMap(request.getParameterMap());
		Map<String,Object> param = new HashMap<>();
		List<String> searchList = new ArrayList<>();

		//검색
		if (!Common.isNullOrEmpty(map.get("MD_brand"))){
			String[] category_request = new String[]{Common.addString("@",map.get("MD_brand").replace(", ","@, @"),"@")};

			searchList.add(dbConn.makeSearchSql("category_site",category_request,"like"));
		}
		searchList.add(dbConn.makeSearchSql("Notice_gb",new String[]{"1"},"="));
		searchList.add(dbConn.makeSearchSql("eventmagam",new String[]{"0"},"="));
		searchList.removeAll(Collections.singleton(null));

		param.put("sqlItem",String.join(" and ",searchList));
		param.put("orderBy","eventmagam,top_gb desc, sortNo, idx desc");
		param.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"),"0"));
		param.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		param.put("pageSize", Common.defaultValue(request.getParameter("pageSize"),"50"));

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".noticeSort",param);

		String str_id = "";
		for (int i=0; i<list.size(); i++){

			if (Common.isNullOrEmpty(str_id)){
				str_id = str_id + list.get(i).get("idx");
			}else{
				str_id = str_id + "," + list.get(i).get("idx");
			}
		}

		List<Map> strId = new ArrayList<>();
		if (!Common.isNullOrEmpty(str_id)){
			strId = dbConn.recordSet(QUERY_ROOT+ ".strId",str_id);
		}

		param.put("totalRecords", list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("returnParam",param);
		mv.addObject("pageSize", Common.defaultValue(request.getParameter("pageSize"),"50"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(list));
		mv.addObject("strId", Common.nullToEmpty(strId));

		mv.setViewName("site/notice_sort.tiles");
		return mv;
	}

	//notice_sort_change.asp
	@RequestMapping(value = "/noticeSortChange.run", method = {RequestMethod.POST}, produces = "application/json")
	public String noticeSortChange(HttpServletRequest request){
		Map<String,String> map = Common.paramToMap(request.getParameterMap());

		List<String> idx = Common.paramToArray(map.get("idx"),", ");
		List<String> sortNo = Common.paramToArray(map.get("sortNo"),", ");

		for (int i=0; i < idx.size(); i++){
			map.put("idx",idx.get(i));
			map.put("sortNo",sortNo.get(i));
			dbConn.recordSet(QUERY_ROOT + ".sortChange",map);
		}

		return "redirect:/views/site/notice_list_new";
	}

	//preView_new
	@RequestMapping(value = "/preView_new")
	public ModelAndView preView(ModelAndView mv, HttpServletRequest request){
		mv.addObject("id",request.getParameter("id"));
		mv.setViewName("site/preView_new.bare");

		return mv;
	}

	//========================================================================================================================================================================
	//게시판관리 > QandA 관리
	@RequestMapping(value = "/qanda_list")
	public ModelAndView qanda_list(ModelAndView mv, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		Map<String,String> param = Common.paramToMap(request.getParameterMap());

		List<String> searchList2 = new ArrayList<>();
		List<String> searchList = new ArrayList<>();

		if (!Common.isNullOrEmpty(request.getParameter("search3"))){  //꼭 다시 확인!!!!!!!!
			searchList2.add(dbConn.makeSearchSql("replace(userid,' ','')",new String[]{param.get("search3").replace(" ","")},"like"));
 			searchList2.add(dbConn.makeSearchSql("replace(username,' ','')",new String[]{param.get("search3").replace(" ","")},"like"));
 			searchList2.add(dbConn.makeSearchSql("replace(subject,'-','')",new String[]{param.get("search3").replace("-","")},"like"));
		}else{
			searchList2.add(dbConn.makeSearchSql("depth",new String[]{"a"},"="));
		}
		searchList2.removeAll(Collections.singleton(null));

		String sqlItem2 = String.join(" or ",searchList2);
		sqlItem2 = (!Common.isNullOrEmpty(sqlItem2)) ? " and (" + sqlItem2 + ")" : "";

		searchList.add(dbConn.makeSearchSql("id",request.getParameterValues("id"),"="));
		searchList.add(dbConn.makeSearchSql("userrank",request.getParameterValues("userrank"),"in"));
		searchList.add(dbConn.makeSearchSql("thisSite",request.getParameterValues("thisSite"),"="));
		searchList.add(dbConn.makeSearchSqlRange("convert(varchar(10),wdate,126)",request.getParameter("wdate1"),request.getParameter("wdate2")));
		searchList.add(dbConn.makeSearchSql("cnt",request.getParameterValues("cnt"),"="));

		if (!Common.isNullOrEmpty(request.getParameter("qanda_type"))){
			if (request.getParameter("qanda_type").equals("2")){
				searchList.add("isnull(jumun_info,'//')<>'//'");

			}else if (request.getParameter("qanda_type").equals("1")){
				searchList.add("isnull(jumun_info,'//')='//'");
			}
		}
		searchList.removeAll(Collections.singleton(null));

		map.put("sqlItem",String.join(" and ",searchList) + sqlItem2);
		map.put("orderBy","num desc,depth asc");
		map.put("totalRecords", Common.defaultValue(request.getParameter("totalRecords"),"0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		map.put("pageSize", Common.defaultValue(request.getParameter("pageSize"),"30"));

		List<Map> list = new ArrayList<>();
		list = dbConn.recordSet(QUERY_ROOT + ".qandaList",map);

		map.put("totalRecords",list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		String num = "";
		for (int i=0; i< list.size(); i++){
			if (Common.isNullOrEmpty(num)){
				num = num + list.get(i).get("num");
			}else{
				num= num + "," + list.get(i).get("num");
			}
		}

		Map<String,String> numList = new HashMap<>();
		numList.put("num",num);
		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".qandaNum",numList);

		mv.addObject("returnParam",map);
		mv.addObject("pageSize", Common.defaultValue(request.getParameter("pageSize"),"30"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("list", Common.nullToEmpty(list));
		mv.addObject("result", Common.nullToEmpty(result));

		mv.setViewName("site/qanda_list.tiles");
		return mv;
	}

	//게시판관리 > QandA 관리 > input
	@RequestMapping(value = "/qanda_input")
	public ModelAndView qanda_input(ModelAndView mv, HttpServletRequest request){
		Map<String,String> map = Common.paramToMap(request.getParameterMap());

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		mv.addObject("nowDate",dtf.format(LocalDateTime.now()));

		mv.addObject("qandatype",dbConn.recordSet(QUERY_ROOT + ".qandatype"));//array: qandtype
		mv.addObject("list",dbConn.recordSet(QUERY_ROOT + ".qandaInput",map));//list

		mv.setViewName("site/qanda_input.tiles");
		return mv;
	}

	//게시판관리 > QandA 관리 > insert, update   (qanda_insert_DB.asp)
	@RequestMapping(value = "/qanda_input.run", method = {RequestMethod.POST}, produces = "application/json")
	public ModelAndView qanda_inputDB(ModelAndView mv, HttpServletRequest request, Authentication authentication){
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		Map<String,String> map = Common.paramToMap(request.getParameterMap());

		map.put("telcheck", Common.defaultValue(request.getParameter("telcheck"),"n"));
		map.put("sendApp", Common.defaultValue(request.getParameter("sendApp"),"N"));

		if (Common.isNullOrEmpty(request.getParameter("id"))){
			map.put("userid",member.getUser_id());
			dbConn.recordSet(QUERY_ROOT +".qandaInsert",map);
		}else{
			dbConn.recordSet(QUERY_ROOT+".qandaUpdate",map);
		}

		if (!Common.isNullOrEmpty(request.getParameter("parent_id"))){
			dbConn.recordSet(QUERY_ROOT + ".qandaUpdateNull",request.getParameter("parent_id"));
		}

		/* ------------------------------ 이메일 발송 주석처리 / app push는 삭제 ------------------------------ */

		/* if (request.getParameter("sendMail").equals("Y")){
			매칭 데이터 배열로 보내기
			String[][] arrMember = new String[7][1];
			arrMember[0][0] = request.getParameter("username");
			arrMember[1][0] = request.getParameter("userid");
			arrMember[2][0] = request.getParameter("email");
			arrMember[3][0] = request.getParameter("subject");
			arrMember[4][0] = request.getParameter("content");
			arrMember[5][0] = request.getParameter("wdate").substring(0,4);
			arrMember[6][0] = request.getParameter("wdate").substring(5,7);
			arrMember[7][0] = request.getParameter("wdate").substring(8,10);
*/
			/* 문의질문 답변
			int mail_form_id = 23;
			dbConn.recordSet(QUERY_ROOT + ".mailForm",mail_form_id);
			dbConn.recordSet(QUERY_ROOT + ".mailMatching",mail_form_id);
		}*/

		mv.setViewName("redirect:/views/site/qanda_list");
		return mv;
	}

	//qanda_update_DB.asp
	@RequestMapping(value = "updateDB.run")
	@Transactional
	public void qanda_updateDB(HttpServletRequest request){
		Map<String,String> map = Common.paramToMap(request.getParameterMap());

		if (request.getParameter("chk").equals("true")){
			dbConn.recordSet(QUERY_ROOT + ".updateDBTrue",map);
		}else{
			dbConn.recordSet(QUERY_ROOT + ".updateDB",map.get("id"));
		}
	}

	//editor
	@RequestMapping(value = "/imgUpload.run", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String,Object> imgUpload(HttpServletRequest request, HttpServletResponse response) {
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



	//자사몰 전용>회원통계>등급별 회원현황
	@RequestMapping(value = "/member_rcv_stat")
	public ModelAndView member_rcv_stat(ModelAndView mv, HttpServletRequest request) {
		Map<String,String> map = Common.paramToMap(request.getParameterMap());
		List<List<Map<String, Object>>> list = dbConn.recordSet(QUERY_ROOT + ".member_rcv_stat", map);

		mv.addObject("levelCnt", list.get(0));
		mv.addObject("smsCnt", list.get(1));
		mv.addObject("mailCnt", list.get(2));

		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("site/member_rcv_stat.pq");
		return mv;
	}


	// 자사몰 전용 - 사이트관리 - 후기관리 - 상품후기 관리 리스트
	@RequestMapping(value = "/after_list")
	public ModelAndView after_list(ModelAndView mv, HttpServletRequest request) {
		List<String> sqlItemList = new ArrayList<>();

		String afterGubun = Common.defaultValue(request.getParameter("Aftergubun"),"1");

		if (afterGubun.equals("2")) {
			sqlItemList.add(dbConn.makeSearchSql("thisSite", new String[]{"app"}, "="));
		} else if (afterGubun.equals("3")) {
			sqlItemList.add(dbConn.makeSearchSql("thisSite", new String[]{"mobile"}, "="));
		} else {
			sqlItemList.add(dbConn.makeSearchSql("thisSite", new String[]{"app","mobile"}, "not in"));
		}

		sqlItemList.add(dbConn.makeSearchSql("replace(A.userid,' ','')", request.getParameterValues("userid"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("replace(D.username,' ','')", request.getParameterValues("username"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("replace(A.subject,' ','')", request.getParameterValues("subject"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("best_check", request.getParameterValues("best_check"), "="));
		sqlItemList.add(dbConn.makeSearchSql("A.category_site_id", request.getParameterValues("category_site_id"), "="));
		sqlItemList.add(dbConn.makeSearchSql("userrank", request.getParameterValues("userrank"), "="));
		sqlItemList.add(dbConn.makeSearchSqlRange("convert(varchar(10),A.wdate,126)", request.getParameter("wdate1"), request.getParameter("wdate2")));
		sqlItemList.add(dbConn.makeSearchSqlRange("jumsu", request.getParameter("jumsu1"), request.getParameter("jumsu2")));

		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, Object> map = new HashMap<>();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "A.wdate desc");
		map.put("totalRecords", Optional.ofNullable(request.getParameter("totalRecords")).orElse("0"));
		map.put("curPage", Common.defaultValue(request.getParameter("curPage"),"1"));
		map.put("pageSize", Optional.ofNullable(request.getParameter("pageSize")).orElse("18"));

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".after_list", map);

		map.put("totalRecords", list.size() > 0 ? list.get(0).get("totalRecords") : 0);

		mv.addObject("list", list);
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("returnParam", map);
		mv.addObject("Aftergubun", afterGubun);
		mv.addObject("category", dbConn.recordSet(QUERY_ROOT + ".category", null));
		mv.setViewName("site/after_list.tiles");
		return mv;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 상품후기 관리 리스트 - 상품사진
	@RequestMapping(value = "/getPhoto.run", produces = "Application/json")
	@ResponseBody
	public Map<String, Object> getPhoto(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> returnParam = new HashMap<>();

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".getPhoto", map);

		returnParam.put("data", Common.nullToEmpty(list));
		return returnParam;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 플러스리뷰 관리
	@RequestMapping(value = "/after_plus_list")
	public ModelAndView after_plus_list(ModelAndView mv, HttpServletRequest request) {
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.addObject("category", dbConn.recordSet(QUERY_ROOT + ".category", null));
		mv.setViewName("site/after_plus_list.pq");
		return mv;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 플러스리뷰 관리
	@RequestMapping(value = "/after_plus_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> after_plus_list(HttpServletRequest request,HttpServletResponse response) {
		List<String> sqlItemList = new ArrayList<>();

		sqlItemList.add(dbConn.makeSearchSql("replace(A.userid,' ','')", request.getParameterValues("userid"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("replace(B.goods_name,' ','')", request.getParameterValues("goods_name"), "like"));
		sqlItemList.add(dbConn.makeSearchSql("B.category_site_id", request.getParameterValues("category_site_id"), "="));
		sqlItemList.add(dbConn.makeSearchSql("isnull([petitelindb].[dbo].[fn_getUserRank](UserId),1)", request.getParameterValues("userrank"), "="));
		sqlItemList.add(dbConn.makeSearchSqlRange("A.wdate", request.getParameter("wdate1"), request.getParameter("wdate2")));

		sqlItemList.removeAll(Collections.singleton(null));

		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();

		map.put("sqlItem", String.join(" and ", sqlItemList));
		map.put("orderBy", "A.wdate desc");
		Common.PQmap(map,request);

		result = dbConn.recordSet(QUERY_ROOT + ".after_plus_list", map);

		Common.PQresultMap(resultMap,map.get("curPage"),result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}


	//자사몰 전용>회원통계>휴면계정 추이
	@RequestMapping(value = "/resting_month")
	public ModelAndView resting_month(ModelAndView mv, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("yearV", Common.defaultValue(map.get("yearV"), Common.nowDate().substring(0, 4)));
		mv.addObject("resting_month", dbConn.recordSet(QUERY_ROOT + ".resting_month", map));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("site/resting_month.tiles");
		return mv;
	}


	// 자사몰 전용 - 사이트관리 - 후기관리 - 실시간 포토후기 관리
	@RequestMapping(value = "/recent_after_list")
	public ModelAndView recent_after_list(ModelAndView mv, HttpServletRequest request) {
		mv.setViewName("site/recent_after_list.pq");
		return mv;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 실시간 포토후기 관리
	@RequestMapping(value = "/recent_after_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> recent_after_list(HttpServletRequest request,HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();
		map.put("orderBy", "A.wdate desc");
		Common.PQmap(map,request);

		result = dbConn.recordSet(QUERY_ROOT + ".recent_after_list", map);

		Common.PQresultMap(resultMap,map.get("curPage"),result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 실시간 포토후기 관리 제외 리스트
	@RequestMapping(value = "/recent_after_except_list")
	public ModelAndView recent_after_except_list(ModelAndView mv, HttpServletRequest request) {
		mv.setViewName("site/recent_after_except_list.pq");
		return mv;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 실시간 포토후기 관리 제외 리스트
	@RequestMapping(value = "/recent_after_except_list.run", method = {RequestMethod.POST}, produces = "application/json")
	@ResponseBody
	public Map<String, Object> recent_after_except_list(HttpServletRequest request,HttpServletResponse response) {
		Map<String, String> map = new HashMap<>();
		List<Map> result = new ArrayList<>();
		Map resultMap = new HashMap();
		Common.PQmap(map,request);

		result = dbConn.recordSet(QUERY_ROOT + ".recent_after_except_list", map);

		Common.PQresultMap(resultMap,map.get("curPage"),result);
		resultMap.put("data", Common.nullToEmpty(result));

		return resultMap;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 실시간 포토후기 저장
	@RequestMapping(value = "/recent_after_insert_DB.run", method = {RequestMethod.POST}, produces = "application/json")
	public String recent_after_insert_DB(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		dbConn.recordSet(QUERY_ROOT + ".recent_after_insert_DB", map);

		return "redirect:/views/site/recent_after_except_list";
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 실시간 포토후기 - 최근 포토리뷰 조회
	@RequestMapping(value = "/recent_after_list_user")
	public ModelAndView recent_after_list_user(ModelAndView mv, HttpServletRequest request){
		Map<String,String> map = Common.paramToMap(request.getParameterMap());
		mv.addObject("list",dbConn.recordSet(QUERY_ROOT + ".recent_after_list_user",map));
		mv.setViewName("site/recent_after_list_user.bare");
		return mv;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 상품후기관리 - 상세화면
	@RequestMapping(value = "/viewAfter")
	public ModelAndView viewAfter(ModelAndView mv, HttpServletRequest request){
		Map<String,String> map = Common.paramToMap(request.getParameterMap());

		List<Map> result = dbConn.recordSet(QUERY_ROOT + ".viewAfter", map);

		mv.addObject("result",result.get(0));
		mv.addObject("page",dbConn.recordSet(QUERY_ROOT + ".viewAfterPage",map).get(0));

		map.put("category_site_id",result.get(0).get("category_site_id").toString());
		map.put("category_name_id",result.get(0).get("category_name_id").toString());

		mv.addObject("category",dbConn.recordSet(QUERY_ROOT + ".viewAfterCategory",map));
		mv.setViewName("site/viewAfter.bare");
		return mv;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 상품후기관리 - 상세화면 카테고리
	@RequestMapping(value = "/add_promotionAdmin.run", produces = "Application/json")
	@ResponseBody
	public Map<String, Object> add_promotionAdmin(HttpServletResponse response, HttpServletRequest request) {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> returnParam = new HashMap<>();

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".add_promotionAdmin", map);
		returnParam.put("data", Common.nullToEmpty(list));
		return returnParam;
	}

	// 자사몰 전용 - 사이트관리 - 후기관리 - 상품후기관리 - 상세화면 등록
	@RequestMapping(value = "/best_insert_DB.run", produces = "Application/json")
	@ResponseBody
	public Map<String, Object> best_insert_DB(HttpServletResponse response, HttpServletRequest request,Authentication authentication) {
		MemberDTO4 member = (MemberDTO4) authentication.getPrincipal();
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		Map<String, Object> returnParam = new HashMap<>();

		map.put("userName",member.getUser_name());

		List<Map> list = dbConn.recordSet(QUERY_ROOT + ".best_insert_DB", map);
		returnParam.put("data", Common.nullToEmpty(list));
		return returnParam;
	}

	// 자사몰 전용 - 사이트관리 - 이벤트관리 - 이벤트 결과 - 체험할인 결과
	@RequestMapping(value = "/experience_sale")
	public ModelAndView experience_sale(ModelAndView mv,HttpServletRequest request, Authentication authentication) throws Exception {
		Map<String, String> map = Common.paramToMap(request.getParameterMap());
		map.put("goods_id", Common.defaultValue(request.getParameter("goods_id"),"0"));
		map.put("before_pack_id", Common.defaultValue(request.getParameter("before_pack_id"),"0"));

		if (!Common.isNullOrEmpty(request.getParameter("pack_id"))) {
			mv.addObject("list", dbConn.recordSet(QUERY_ROOT + ".experience_sale", map).get(0));
			mv.addObject("list2", dbConn.recordSet(QUERY_ROOT + ".experience_sale", map).get(1));
		}

		mv.addObject("pack_id", request.getParameter("pack_id"));
		mv.addObject("searchParam", Common.paramToSearch(request.getParameterMap()));
		mv.setViewName("site/experience_sale.tiles");
		return mv;
	}
}
