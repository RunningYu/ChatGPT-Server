package chatgptserver.service;

import chatgptserver.bean.ao.JsonResult;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/25
 */
public interface GptService {

    JsonResult gptList();

    JsonResult defaultList(String gptCode);

    JsonResult requestStop(String cid);
}
