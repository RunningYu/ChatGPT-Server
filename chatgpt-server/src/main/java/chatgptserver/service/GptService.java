package chatgptserver.service;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.po.GptPO;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/25
 */
public interface GptService {

    JsonResult gptList();

    JsonResult defaultList();
}
