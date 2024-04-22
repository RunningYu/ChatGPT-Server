package chatgptserver.service;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.ppt.PptCreateRequestAO;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
public interface PptService {

    JsonResult pptOutlineCreate(String userCode, String content, Boolean isRebuild, String cid);

    JsonResult pptCreateByOutline(PptCreateRequestAO request);

    JsonResult pptColorList();
}
