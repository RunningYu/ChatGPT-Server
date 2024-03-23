package chatgptserver.service;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */
public interface MessageService {

    void recordHistory(String userCode, String chatCode, String message, String result);

}
