package com.common.common;

import com.common.aspect.ProfileActive;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Controller
public class FileCon {

	@Resource
	private CommonsMultipartResolver multipartResolver;
	@Resource
	private ProfileActive profileActive;

	public List<String> uploadFile(List<MultipartFile> multipartFile, HttpServletRequest request, String prefix, String filePath) throws IOException {

		String realDir = profileActive.getFolder(request, filePath);
		File folder = new File(realDir);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		List<String> list = new ArrayList<>();
		for (MultipartFile mf : multipartFile) {
			if (multipartResolver.getFileUpload().getFileSizeMax() >= mf.getSize() && mf.getSize() > 0) {
				if (!Common.fileExtValidate(FilenameUtils.getExtension(mf.getOriginalFilename()), "reject")) {
					String originFilename = mf.getOriginalFilename();
					String extName = FilenameUtils.getExtension(originFilename);
					String saveFileName = Common.addString(Common.makeFileName(prefix), ".", extName);
					mf.transferTo(new File(realDir, saveFileName));

					list.add(saveFileName);
				}
			}
		}
		return list;
	}

	@GetMapping(path = "/download.do")
	public void fileDownload(@RequestParam("path") String filePath, @RequestParam("fileName") String fileName, HttpServletResponse response, HttpServletRequest request) {
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Pragma", "no-cache;");
		response.setHeader("Expires", "-1;");

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(profileActive.getFolder(request, filePath), fileName));
			OutputStream outputStream = response.getOutputStream();

			int readCount = 0;
			byte[] buffer = new byte[1024];
			while ((readCount = fileInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, readCount);
			}
			fileInputStream.close();
			outputStream.close();
		} catch (Exception ex) {
		}
	}
}
