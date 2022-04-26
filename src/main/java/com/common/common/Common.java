package com.common.common;

import com.common.aspect.ExcuteAOP;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class Common {

	//�⺻���ð�//////////////////////////////////////////////////////////////////////////////////////////////////////

	//÷�ε� ������ ��Ʈ ���丮
	public static final String FILE_ROOT_PATH = "/assets/files/";

	//÷�ε� �̹��������� �ִ� ���� ������
	public static final int FILE_SIZE = 2000;

	//÷�ε� ���ϸ��� �ƴ� ������ ���ϸ����� �����Ѵ�.
	public static String makeFileName(String sourceVal) {
		return sourceVal + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	}
	public static String makeFileName() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
	}

	//÷�ε� ������ Ȯ���ڸ� �̹���, ����, ��Ÿ�� �����Ѵ�.
	public static Boolean fileExtValidate(String fileExt, String isPattern) {
		switch (isPattern) {
			case "img":
				return fileExt.matches("^((?i)(bmp|jpg|gif|png|jpeg|heic|heif|webp|tiff)$)");
			case "xls":
				return fileExt.matches("^((?i)(xls|xlsx|csv)$)");
			case "reject":
				return fileExt.matches("^((?i)(exe|js|class|jsp|sh)$)");
			default:
				return false;
		}
	}

	//���丮 ����
	public void makeDir(String dir) {
		File folder = new File(dir);
		if (!folder.exists()) { folder.mkdirs(); }
	}

	//�Ϲ�//////////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean isNullOrEmpty(final Collection<?> c) {
		return CollectionUtils.isEmpty(c);
	}

	public static boolean isNullOrEmpty(final Map<?, ?> m) {
		return MapUtils.isEmpty(m);
	}
	public static boolean isNullOrEmpty(final String s) { return !StringUtils.hasText(s); }
	public static boolean isNullOrEmpty(final Object o) {
		return ObjectUtils.isEmpty(o);
	}

	public static boolean isNullOrEmpty(final String[] s) {
		boolean strEmpty = true;
		if (!(s == null || s.length == 0)) {
			for (String str : s) {
				if (!isNullOrEmpty(str)) {
					strEmpty = false;
					break;
				}
			}
		}
		return strEmpty;
	}

	public static String defaultValue(String val, String val2) {
		if (isNullOrEmpty(val) || val.equals("null")) {
			return val2;
		} else {
			return val;
		}
	}

	public static String defaultValue(Object val, String val2) {
		if (isNullOrEmpty(val)) {
			return val2;
		} else {
			return String.valueOf(val);
		}
	}

	//���ó�¥ ���ϱ�
	public static String nowDate() {
		LocalDateTime currentDate = LocalDateTime.now();
		return currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	//1900-01-01 �������� ��¥ ����
	public static String getDate(String val) {
		if (!isDate(val) || isNullOrEmpty(val)) {
			return nowDate();
		} else {
			return val.substring(0, 10);
		}
	}

	//��¥���� ���������� Ȯ��
	public static boolean isDate(String val) {
		Boolean thisResult = false;
		try {
			LocalDate result = LocalDate.parse(val);
			thisResult = true;
		} catch (java.time.format.DateTimeParseException e) {
		} finally {
			return thisResult;
		}
	}

	enum DayOfTheWeek {
		MONDAY("��"), TUESDAY("ȭ"), WEDNESDAY("��"), THURSDAY("��"), FRIDAY("��"), SATURDAY("��"), SUNDAY("��");

		private String value;

		DayOfTheWeek(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public static String getWeekName(String sourceVal) {
		DayOfTheWeek result = DayOfTheWeek.valueOf(LocalDate.parse(sourceVal).getDayOfWeek().name());
		return result.getValue();
	}

	//ip ��������
	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null) ip = request.getHeader("Proxy-Client-IP");
		if (ip == null) ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip == null) ip = request.getHeader("HTTP_CLIENT_IP");
		if (ip == null) ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		if (ip == null) ip = request.getRemoteAddr();

		return ip;
	}

	//String ��ġ��
	public static String addString(String... addStr) {
		StringBuilder result = new StringBuilder();
		for (String str : addStr) {
			result.append(str);
		}
		return result.toString();
	}

	//���ϴ� �ڸ����� �� ) ä���
	public static String selectZero(int sourceVal, String formatValue) {
		return new DecimalFormat(formatValue).format(sourceVal);
	}


	//���ϴ� �ڸ����� ���� ���� ���� ���� gubun:1, ����+�빮�� gubun:2, ����+��ҹ��� gubun:3

	public static String randomValue(int length, int gubun) {
		StringBuffer result = new StringBuffer();
		Random rnd = new Random();
		for (int i=0; i<length; i++){
			int rIndex = rnd.nextInt(gubun);
			switch (rIndex){
				case 0 :
					result.append(rnd.nextInt(10));
					break;
				case 1 :
					result.append((char)((int) (rnd.nextInt(26)) + 65));//�빮��
					break;
				case 2 :
					result.append((char)((int) (rnd.nextInt(26)) + 97));//�ҹ���
					break;
			}
		}
		return result.toString();
	}


	//collection ó�� //////////////////////////////////////////////////////////////////////////////////////////////////////
	public static List<Map> collectionSearch(List<Map> list, String val1, String val2) {
		List<Map> returnValue = new ArrayList<>();
		for (Map map : list) if (map.get(val1).toString().equals(val2)) returnValue.add(map);

		return returnValue;
	}

	//collection���� String���� ����
	public static String collectionGetString(List<Map> list, String val1, String toNum, String countNum) {
		List returnValue = new ArrayList();
		int start_num = Integer.parseInt(defaultValue(toNum,"1")) - 1;
		int end_num = start_num + Integer.parseInt(defaultValue(countNum,String.valueOf(list.size())));
		end_num = end_num > list.size() ? list.size() : end_num;

		for (int i=start_num ; i<end_num ; i++) returnValue.add(list.get(i).get(val1).toString());

		return String.join(",", returnValue);
	}

	//�������
	public static void printError(String val) {
		LogFactory.getLog(ExcuteAOP.class).error(val);
	}

	public static void printListMap(List<Map> list) {
		if(list.size()==0) return;

		int listLen = list.get(0).size();
		int maxTitleLen = 0;
		for (Object key : list.get(0).keySet()) if (String.valueOf(key).length()>maxTitleLen) maxTitleLen=String.valueOf(key).length();
		String titleRatio = "";
		List lineLen = new ArrayList();
		for (int i=0 ; i<(maxTitleLen+1)*listLen ; i++) lineLen.add("=");
		String line = String.join("",lineLen);

		String[] title = new String[listLen];
		for (int i=0, len=list.get(0).size() ; i<len ; i++) titleRatio = titleRatio + "%-"+ String.valueOf(maxTitleLen+1) +"s";
		titleRatio = titleRatio + "\n";

		System.out.println(line);
		int i = 0;
		for (Object key : list.get(0).keySet()) {
			title[i] =  String.valueOf(key);
			i++;
		}
		System.out.printf(titleRatio, title);
		System.out.println(line);

		String[] content = new String[listLen];
		for (Map map : list) {
			i = 0;
			for (Object key : map.keySet()) {
				content[i] =  String.valueOf(map.get(key));
				i++;
			}
			System.out.printf(titleRatio, content);
		}

		System.out.println(line);
	}

	//DB������ ó�� //////////////////////////////////////////////////////////////////////////////////////////////////////
	public static Integer getInt( Object value ) {
		if( value != null ) {
			if( value instanceof BigDecimal || value instanceof String ) {
				return new BigDecimal( value.toString().replaceAll(",","") ).intValue();
			} else if( value instanceof BigInteger ) {
				return new BigDecimal( (BigInteger) value ).intValue();
			} else if( value instanceof Number ) {
				return new BigDecimal( ((Number)value).doubleValue() ).intValue();
			} else {
				return 0;
			}
		}
		return 0;
	}


	//parameter�� String[]���� String���� Ȯ��
	public static String paramIsArray(String parameter, HttpServletRequest request) {
		String result = "";
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			if (name.equals(parameter + "[]")) {
				result = parameter + "[]";
				break;
			}
			if (name.equals(parameter)) {
				result = parameter;
				break;
			}
		}

		return result;
	}


	//�˻����� üũ�� ���� parameter String ��ȯ
	public static String paramToSearch(Map<String, ?> map) {
		StringBuffer result = new StringBuffer();

		for (String key : map.keySet()) {
			String name = key;
			String value;
			if (map.get(key) instanceof String[]) {
				value = String.join(", ", (String[]) map.get(key));
			} else {
				value = (String) map.get(key);
			}

			if (!value.equals("") && !key.equals("curPage") && !key.equals("pageSize")) {
				if (value.indexOf(",") > -1) {
					result.append("split_Rs($$$." + key + ",\"" + value + "\",\", \");");
				} else {
					result.append("check_Rs($$$." + key + ",\"" + value + "\");");
				}
			}
		}

		return result.toString();
	}

	//parameter map ��ȯ
	public static Map<String, String> paramToMap(Map<String, String[]> map) {
		Map<String, String> returnMap = new HashMap<>();
		for (String key : map.keySet()) {
			returnMap.put(key, String.join(", ", map.get(key)));
		}

		return returnMap;
	}

	//parameter list ��ȯ
	public static List<Map<String, String>> paramToList(String[] selectField, Map<String, String[]> map) {
		List resultList = new ArrayList<>();
		int maxLength = map.get(selectField[0]).length;

		for (int i = 0; i < maxLength; i++) {
			Map<String, String> thisMap = new HashMap<>();
			for (int j = 0, lenSelectField = selectField.length; j < lenSelectField; j++) { thisMap.put(selectField[j], map.get(selectField[j])[i]); }
			resultList.add(thisMap);
		}

		return resultList;
	}

	//parameter�� split�� �����ʵ�
	public static List<Map> paramToMulti(String[] selectField, String[] splitString, String divide) {
		List resultList = new ArrayList<>();
		if (splitString!=null) {
			for (int i = 0; i < splitString.length; i++) {
				String[] splitStr = splitString[i].split(divide);
				Map<String, String> thisMap = new HashMap<>();
				for (int j = 0, lenSplitStr = splitStr.length; j < lenSplitStr; j++) { thisMap.put(selectField[j], splitStr[j]); }
				resultList.add(thisMap);
			}
		}
		return resultList;
	}

	//parameter�� split�� �����ʵ�
	public static List paramToArray(String splitString, String divide) {
		List<String> resultList = new ArrayList();
		String[] splitStr = splitString.split(divide,-1);

		for (int j = 0, lenSplitStr = splitStr.length; j < lenSplitStr; j++) { resultList.add(splitStr[j]); }

		return resultList;
	}


	public static String mapToJson(Object result) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		return mapper.writeValueAsString(result);
	}

	//���ڵ�� null ó��
	public static Map<String, Object> nullToEmpty(Map<String, Object> map) {
		for (String key : map.keySet()) {
			if (isNullOrEmpty(map.get(key)) || map.get(key).equals("1900-01-01")) {
				map.put(key, "");
			}
		}
		return map;
	}

	public static List<Map> nullToEmpty(List<Map> list) {
		list.forEach(item -> {nullToEmpty(item);});
		return list;
	}

	public static Map subQuery(List<Map> result, String id) {
		String returnValue = result.stream().map(x -> x.get(id).toString()).collect(Collectors.joining(","));
		return new HashMap<String, String>() {{
			put(id, returnValue);
		}};
	}

	public static List<Map> combineRecordSet(String id, List<Map> iniResult, List<Map>... result) {
		for (Map iniMap : iniResult) {
			for (List<Map> list : result) {
				for (Map map : list) {
					if (map.get(id).equals(iniMap.get(id))) {
						iniMap.putAll(map);
						break;
					}
				}
			}
		}
		return iniResult;
	}

	//�տ��� ���� ��(������ ����� length�� �� �� ����)�� Ư�����ڷ� �����ؼ� �ش� �ε��� �� return
	public static String paramInIdx(String str,int idx) {
		String[] strArray = str.split(", ");

		if (strArray.length==1) {
			return strArray[0];
		} else {
			if (strArray.length<idx) {
				return strArray[idx];
			} else {
				return strArray[strArray.length-1];
			}
		}
	}

	//�տ��� ���� ��(������ ����� length�� ����, �󰪵� Ư�����ڰ� length��ŭ ��. ex.��ȭ��ȣ)�� Ư�����ڷ� �����ؼ� �ش� �ε��� �� return
	public static String splitReturn(String val, String divide, int idx) {
		String[] splitResult = val.split(divide);
		return (splitResult.length > idx) ? splitResult[idx]: "";
	}

	//pqGrid//////////////////////////////////////////////////////////////////////////////////////////////////////

	//pqGrid�� ����¡ ���
	public static Map<String, String> PQmap(Map<String, String> map, HttpServletRequest request) {
		String cpCookie = Optional.ofNullable(request.getParameter("curPageCookie")).orElse("0");
		map.put("totalRecords", Optional.ofNullable(request.getParameter("totalRecords")).orElse("0"));
		if (Integer.parseInt(cpCookie) > 0 && request.getParameter("totalRecordsCookie").equals("")) {
			map.put("curPage", cpCookie);
		} else {
			map.put("curPage", request.getParameter("pq_curpage"));
		}
		map.put("pageSize", request.getParameter("pq_rpp"));

		return map;
	}

	public static Map PQresultMap(Map resultMap, String curPage, List<Map> result) {
		resultMap.put("curPage", curPage);
		resultMap.put("totalRecords", result.size() > 0 ? result.get(0).get("totalRecords") : 0);

		return resultMap;
	}





	//����� Java���� ���
	//��Ű�� ó�� //////////////////////////////////////////////////////////////////////////////////////////////////////
	//��Ű�� �����ϱ�
	public static void setCookie(HttpServletResponse response, String name, String value) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
	}

	//��Ű�� ��������
	public static String getCookie(Cookie[] cookies, String name) {
		if (isNullOrEmpty(cookies)) return "";

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}

		return "";
	}

	public static synchronized String AjaxInJava(String postURL, Map<String, String> param) throws Exception {
		PostMethod postMethod = new PostMethod(postURL);

		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		if (!isNullOrEmpty(param.get("access_token"))) {
			postMethod.setRequestHeader("Authorization", "Bearer "+ param.get("access_token"));
		} else {
			int index = 0;
			NameValuePair[] data = new NameValuePair[param.size()];
			for (String key : param.keySet()) {
				data[index] = new NameValuePair(key, param.get(key));
				index++;
			}

			postMethod.setRequestBody(data);
		}
		new HttpClient().executeMethod(postMethod);

		if (postMethod.getStatusCode() == HttpStatus.SC_OK) { //200�̸� ����
			return postMethod.getResponseBodyAsString();
		} else {
			printError(postMethod.getResponseBodyAsString());
			return "";
		}
	}



}