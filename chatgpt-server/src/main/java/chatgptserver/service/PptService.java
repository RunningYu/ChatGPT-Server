package chatgptserver.service;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.ppt.PptCreateRequestAO;
import chatgptserver.bean.ao.ppt.PptScoreRequestAO;
import chatgptserver.bean.ao.ppt.PptUploadRequestAO;
import chatgptserver.bean.po.CommentPO;
import chatgptserver.bean.po.ReplyPO;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
public interface PptService {

    JsonResult pptOutlineCreate(String userCode, String content, Boolean isRebuild, String cid);

    JsonResult pptCreateByOutline(PptCreateRequestAO request);

    JsonResult pptColorList();

    JsonResult pptUpload(PptUploadRequestAO request);

    JsonResult pptKindList();

    JsonResult pptListByKind(String keyword, String firstKind, String secondKind, int page, int size, String userCode);

    JsonResult pptCollect(String folderCode, String userCode, String pptCode);

    JsonResult pptCollectList(String folderCode, int page, int size, String userCode);

    JsonResult pptFolderCreate(String folder, String userCode);

    JsonResult pptCollectFolderList(String pptCode, String userCode);

    JsonResult pptFolderUpdate(String userCode, String folderCode, String folder);

    JsonResult pptFolderDelete(String userCode, String folderCode);

    void createDefaultFolder(String userCode);

    JsonResult pptMeList(String userCode, int page, int size);

    JsonResult pptDelete(String userCode, String pptCode);

    JsonResult pptSocring(String userCode, PptScoreRequestAO request);

    JsonResult pptSee(String pptCode);

    JsonResult pptComment(CommentPO request);

    JsonResult pptReply(ReplyPO request);

    JsonResult pptCommentList(String userCode, String pptCode, int page, int size);

    JsonResult pptReplyList(String userCode, String commentCode, int page, int size);
}
