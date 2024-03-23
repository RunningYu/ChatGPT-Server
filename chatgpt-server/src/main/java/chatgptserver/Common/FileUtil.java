package chatgptserver.Common;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */
public class FileUtil {

    /**
     * 文件类型转换
     * File类型 转为 MultipartFile类型
     */
//    public static MultipartFile getMultipartFile(File file) {
//        FileItem item = new DiskFileItemFactory().createItem("file"
//                , MediaType.MULTIPART_FORM_DATA_VALUE
//                , true
//                , file.getName());
//        try (InputStream input = new FileInputStream(file);
//             OutputStream os = item.getOutputStream()) {
//            // 流转移
//            IOUtils.copy(input, os);
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid file: " + e, e);
//        }
//
//        return new CommonsMultipartFile(item);
//    }

//    public static MultipartFile getMultipartFile(File file) {
//        DiskFileItem item = new DiskFileItem("file"
//                , MediaType.MULTIPART_FORM_DATA_VALUE
//                , true
//                , file.getName()
//                , (int)file.length()
//                , file.getParentFile());
//        try {
//            OutputStream os = item.getOutputStream();
//            os.write(FileUtils.readFileToByteArray(file));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new CommonsMultipartFile(item);
//    }

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



}
