package chatgptserver.Common;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */
public class FileUtil {

    /**
     * 文件类型转换
     * File类型 转为 MultipartFile类型
     */
    public static MultipartFile ConvertFileToMultipartFile(File file) {
        if (Objects.isNull(file)) {
            throw new RuntimeException("file不能为空");
        }
        try {
            MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "image/png", new FileInputStream(file));

            return multipartFile;
        } catch (IOException e) {
            throw new RuntimeException("File转换MultipartFile失败");
        }
    }

    public static FileItem createFileItem(String filePath, String fileName){
        String fieldName = "file";
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem(fieldName, "text/plain", false,fileName);
        File newfile = new File(filePath);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try (FileInputStream fis = new FileInputStream(newfile);
             OutputStream os = item.getOutputStream()) {
            while ((bytesRead = fis.read(buffer, 0, 8192))!= -1)
            {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }


//    public static MultipartFile cutPptConver(MultipartFile pptFile) throws IOException {
//        ByteArrayInputStream bis = new ByteArrayInputStream(pptData);
//        HSLFSlideShow slideShow = new HSLFSlideShow(bis);
//
//        Slide[] slides = slideShow.getSlides();
//        Slide firstSlide = slides[0];
//
//        BufferedImage image = new BufferedImage(960, 720, BufferedImage.TYPE_INT_RGB);
//        Graphics2D graphics = image.createGraphics();
//        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        firstSlide.draw(graphics);
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(image, "jpg", bos);
//
//        return bos.toByteArray();
//    }
}
