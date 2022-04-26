package com.webadmin;

import com.common.common.Common;
import com.common.common.DbConn;
import com.common.member.MemberDTO4;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/accodianMenu")
public class AccodianMenu {

	private final String DIR_ROOT = "accodianMenu";
	private final String QUERY_ROOT = DIR_ROOT + ".query";

	@Resource(name = "dbConn")
	private DbConn dbConn;
	@Resource
	private MemberDTO4 memberDTO4;

	@RequestMapping(value = "list.run", method = {RequestMethod.POST})
	@ResponseBody
	public Map<String, Object> list(HttpServletRequest request, Authentication authentication) {
		Map<String, String> map = new HashMap<>();
		map.put("id", Common.defaultValue(((MemberDTO4) authentication.getPrincipal()).get_Id(), "1"));
		List<Map<String, Object>> list = dbConn.recordSet(QUERY_ROOT + ".accordianList", map);
//		try {
//			String serverIp = InetAddress.getLocalHost().getHostAddress();
//			String clientIp = request.getRemoteAddr();
//			if (clientIp.equals("0:0:0:0:0:0:0:1") || clientIp.equals("127.0.0.1") || serverIp.equals("192.168.0.43")) {
				for (int i = 0; i < list.size(); i++) {
					String urlLink = String.valueOf(list.get(i).get("url_link"));
					if (!Common.isNullOrEmpty(list.get(i).get("url_link"))) {
						list.get(i).put("url_link", urlLink.substring(urlLink.indexOf("/", 8)));
					}
				}
//			}
//		} catch (Exception e) {
//		}
		Map<String, Object> returnParam = new HashMap<>();
		returnParam.put("data", list);

		return returnParam;
	}
}
