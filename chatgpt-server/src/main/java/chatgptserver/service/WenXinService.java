package chatgptserver.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 15:32
 */
public interface WenXinService {

    String getMessageFromWenXin(String userCode, String chatCode, String message);

    String wxImageCreate(String userCode, String chatCode, String content);

}
