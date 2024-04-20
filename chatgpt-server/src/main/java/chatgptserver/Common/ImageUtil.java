package chatgptserver.Common;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class ImageUtil {
    public static byte[] read(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        byte[] data = inputStream2ByteArray(in);
        in.close();
        return data;
    }
    private static byte[] inputStream2ByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /**
     * Base64字符串转图片
     * @param base64String
     * @param imageFileName
     */
    public static File convertBase64StrToImage(String base64String, String imageFileName) {
        ByteArrayInputStream bais = null;
        try {
            //获取图片类型
            String suffix = imageFileName.substring(imageFileName.lastIndexOf(".") + 1);
            //获取JDK8里的解码器Base64.Decoder,将base64字符串转为字节数组
            byte[] bytes = Base64.getDecoder().decode(base64String);
            //构建字节数组输入流
            bais = new ByteArrayInputStream(bytes);
            //通过ImageIO把字节数组输入流转为BufferedImage
            BufferedImage bufferedImage = ImageIO.read(bais);

//            String iamgePath = "D:\\Github\\GitProjects\\chatgpt-server\\Gpt-Server\\chatgpt-server\\src\\main\\resources\\images\\";
            String imagePath = "src\\main\\resources\\images\\" + imageFileName;

            //构建文件
            File imageFile = new File(imagePath);
            //写入生成文件
            ImageIO.write(bufferedImage, suffix, imageFile);

            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bais != null) {
                    bais.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 将MultipartFile 图片文件编码为base64
     */
    public static String imageMultipartFileToBase64(MultipartFile mFile){
        if (mFile == null || mFile.isEmpty()) {
            throw new RuntimeException("图片文件不能为空！");
        }

        try {
            InputStream inputStream = mFile.getInputStream();
            byte[] bytes = inputStream2ByteArray(inputStream);

            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    /**
     * 将图片转为file
     * @param url 图片url
     */
    public static File imageUrlToFile(String url) {
        //对本地文件命名
        String fileName = url.substring(url.lastIndexOf("."),url.length());
        File file = null;

        URL urlfile;
        InputStream inStream = null;
        OutputStream os = null;
        try {
            file = File.createTempFile("net_url", fileName);
            //下载
            urlfile = new URL(url);
            inStream = urlfile.openStream();
            os = new FileOutputStream(file);

            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
                if (null != inStream) {
                    inStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }


    /**
     * 获取图片二进制
     */
    public static byte[] downloadPicture(String url) {
        URL urlConnection = null;
        HttpURLConnection httpURLConnection = null;
        try {
            urlConnection = new URL(url);
            httpURLConnection = (HttpURLConnection) urlConnection.openConnection();
            InputStream in = httpURLConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }
        return null;
    }

    /**
     * 图片url --> base64
     * @param url
     * @return
     */
    public static String imageUrlToBase64(String url) {
        byte[] bytes = downloadPicture(url);

        MultipartFile mfile = null;
        ByteArrayInputStream in = null;
        try {
            in = new ByteArrayInputStream(bytes);
            FileItemFactory factory = new DiskFileItemFactory(16, null);
            FileItem fileItem = factory.createItem("mainFile", "text/plain", false, "name");
            IOUtils.copy(new ByteArrayInputStream(bytes), fileItem.getOutputStream());
            mfile = new CommonsMultipartFile(fileItem);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String base64Image = ImageUtil.imageMultipartFileToBase64(mfile);

        return base64Image;
    }

    public static MultipartFile imageUrlToMultipartFile(String url) throws IOException {
        String base64 = ImageUtil.imageUrlToBase64(url);
        File imageFile = ImageUtil.convertBase64StrToImage(base64, System.currentTimeMillis() + "AiPicture.jpg");
        MultipartFile multipartFile = new MockMultipartFile("file", imageFile.getName(), "image/png", new FileInputStream(imageFile));

        return multipartFile;
    }



}
