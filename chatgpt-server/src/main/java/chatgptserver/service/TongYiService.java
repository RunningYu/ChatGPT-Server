package chatgptserver.service;


import org.springframework.web.multipart.MultipartFile;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/27
 */
public interface TongYiService {

    String tyImageUnderstand(MultipartFile image, String content, String userCode, String chatCode);


    String tyQuestion(String userCode, String chatCode, String content);
}
