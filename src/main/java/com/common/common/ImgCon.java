package com.common.common;

import com.common.aspect.ProfileActive;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


@Controller
public class ImgCon {

	@Resource
	private CommonsMultipartResolver multipartResolver;
	@Resource
	private ProfileActive profileActive;

	public List uploadImgFile(List<MultipartFile> multipartFile, HttpServletRequest request, String prefix, String filePath, List<List<String>> tnumbnail)
		throws IOException {

		String realDir = profileActive.getFolder(request, filePath);
		File folder = new File(realDir);
		if (!folder.exists()) {
			folder.mkdirs();
		}

		List<String> list = new ArrayList<>();

		for (MultipartFile mf : multipartFile) {
			if (multipartResolver.getFileUpload().getFileSizeMax() >= mf.getSize() && mf.getSize() > 0) {
				String originFilename = mf.getOriginalFilename();
				String extName = FilenameUtils.getExtension(originFilename);
				String saveFileName = Common.makeFileName(prefix) + "." + extName;

				if (Common.fileExtValidate(extName, "img")) {
					File file = new File(realDir, saveFileName);
					mf.transferTo(file);

					BufferedImage srcImg = setOrientation(file);
					srcImg = setSize(srcImg, Common.FILE_SIZE, 0);
					ImageIO.write(srcImg, extName, file);

					if (!Common.isNullOrEmpty(tnumbnail)) { //썸네일이 있는 경우
						BufferedImage srcThumImg = ImageIO.read(file);
						for (List<String> thumb : tnumbnail) {
							String thumbDir = profileActive.getFolder(request, thumb.get(0));
							int thumbWidth = Integer.parseInt(thumb.get(1));
							int thumbHeight = Integer.parseInt(thumb.get(2));

							srcThumImg = setSize(srcThumImg, thumbWidth, 0); //가로우선 적용
							srcThumImg = setCrop(srcThumImg, thumbWidth, thumbHeight); //사이즈 변경 뒤에 세로 맞춤

							File thumbfolder = new File(thumbDir);
							if (!thumbfolder.exists()) {
								thumbfolder.mkdirs();
							}

							ImageIO.write(srcThumImg, extName, new File(thumbDir, saveFileName));
						}
					}

					list.add(saveFileName);
				}
			}
		}
		return list;
	}

	private BufferedImage setOrientation(File file) {
		int orientation = 1;
		BufferedImage srcImg = null;

		try {
			srcImg = ImageIO.read(file);

			Metadata metadata = ImageMetadataReader.readMetadata(file);
			Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

			if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
				orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
			}
		} catch (Exception e) {
			throw new RuntimeException("업로드된 이미지 orientation 확인 ERROR");
		}

		switch (orientation) {
			case 3:
				Scalr.rotate(srcImg, Scalr.Rotation.CW_180, Scalr.OP_ANTIALIAS);
				break;
			case 6:
				Scalr.rotate(srcImg, Scalr.Rotation.CW_90, Scalr.OP_ANTIALIAS);
				break;
			case 8:
				Scalr.rotate(srcImg, Scalr.Rotation.CW_270, Scalr.OP_ANTIALIAS);
				break;
		}

		return srcImg;
	}

	private BufferedImage setSize(BufferedImage srcImg, int fileSize, int thumbHeight) {
		if (thumbHeight == 0) { //세로에 한정이 없다면
			if (srcImg.getWidth() > fileSize) {
				return Scalr.resize(srcImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, fileSize); //가로만 확인해서 사이즈 축소
			} else {
				return srcImg;
			}
		} else {
			if (srcImg.getWidth() - fileSize > srcImg.getHeight() - thumbHeight) { //가로, 세로 중에서 더 큰 차이가 나는 부분을 기준으로 사이즈를 조정한다. 잘리는 부분을 최소화한다.
				return Scalr.resize(srcImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, fileSize);
			} else {
				return Scalr.resize(srcImg, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, thumbHeight);
			}
		}
	}

	private BufferedImage setCrop(BufferedImage srcImg, int thumbWidth, int thumbHeight) {
		srcImg = setSize(srcImg, thumbWidth, thumbHeight);
		if (srcImg.getWidth() > thumbWidth) { //가로가 정해진 썸네일보다 크면
			return Scalr.crop(srcImg, srcImg.getWidth() / 2 - thumbWidth / 2, 0, thumbWidth, srcImg.getHeight()); //가로를 자른다.
		} else if (srcImg.getHeight() > thumbHeight) {
			return Scalr.crop(srcImg, 0, srcImg.getHeight() / 2 - thumbHeight / 2, srcImg.getWidth(), thumbHeight);
		}
		return srcImg; //정해진 썸네일보다 작으면 그대로 쓴다.
	}
};