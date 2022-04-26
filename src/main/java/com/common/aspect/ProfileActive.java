package com.common.aspect;

import com.common.common.Common;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.ibatis.io.Resources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
public class ProfileActive {

	@Resource
	private Properties properties;

	public void ProfileActive(@Value("${spring.profiles.active}") String activeProfile) {
		try {
			properties.load(Resources.getResourceAsReader("config-" + activeProfile + ".properties"));
		} catch (IOException e) {
		}
	}

	public String getFolder(HttpServletRequest request, String filePath) {
		String upFolder = properties.getProperty("UPLOAD.folder");
		String inPath = Common.addString(upFolder, Common.FILE_ROOT_PATH, filePath);

		if (upFolder != null && (upFolder.contains(":") || upFolder.contains("\\\\192.168.0"))) {
			return new File(inPath).getAbsolutePath();
		} else {
			return request.getSession().getServletContext().getRealPath(inPath);
		}
	}
}