package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.QuestionRequestAO;
import chatgptserver.bean.ao.ppt.PptCreateRequestAO;
import chatgptserver.bean.ao.ppt.PptScoreRequestAO;
import chatgptserver.bean.ao.ppt.PptUploadRequestAO;
import chatgptserver.bean.po.CommentPO;
import chatgptserver.bean.po.ReplyPO;
import chatgptserver.service.PptService;
import chatgptserver.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
@Slf4j
@RestController
public class PptController {

    @Autowired
    private PptService pptService;

    @Autowired
    private UserService userService;

    @ApiOperation("PPT大纲生成")
    @PostMapping("/ppt/outline/create")
    public JsonResult pptOutlineCreate(HttpServletRequest httpServletRequest,
                                       @RequestBody QuestionRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptOutlineCreate request:[{}], token:[{}]", request, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptOutlineCreate(userCode, request.getContent(), request.getIsRebuild(), request.getCid());

        return response;
    }

    @ApiOperation("根据PPT大纲生成PPT")
    @PostMapping("/ppt/create/by/outline")
    public JsonResult pptCreateByOutline(HttpServletRequest httpServletRequest,
                                         @RequestBody PptCreateRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptCreateByOutline request:[{}] token:[{}]", request, token);
        request.setUserCode(userService.getUserCodeByToken(token));
        JsonResult response = pptService.pptCreateByOutline(request);

        return response;
    }

    @ApiOperation("获取主题颜色列表")
    @GetMapping("/ppt/color/list")
    public JsonResult pptColorList(HttpServletRequest httpServletRequest) {
        log.info("PptController pptColorList");
        JsonResult response = pptService.pptColorList();

        return response;
    }

    @ApiOperation("PPT上传")
    @PostMapping("/ppt/upload")
    public JsonResult pptUpload(HttpServletRequest httpServletRequest,
                                @Validated PptUploadRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptUpload request:[{}], token:[{}]", request, token);
        String userCode = userService.getUserCodeByToken(token);
        request.setUserCode(userCode);
        JsonResult response = pptService.pptUpload(request);

        return response;
    }

    @ApiOperation("PPT评分")
    @PostMapping("/ppt/scoring")
    public JsonResult pptScoring(HttpServletRequest httpServletRequest,
                                 @RequestBody PptScoreRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptScoring request:[{}], token:[{}]", request, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptSocring(userCode, request);

        return response;
    }

    @ApiOperation("获取分类数据")
    @GetMapping("/ppt/kind/list")
    public JsonResult pptKindList() {
        log.info("PptController pptKindList");
        JsonResult response = pptService.pptKindList();

        return response;
    }


    @ApiOperation("PPT分类查询、搜索")
    @GetMapping("/ppt/list/by/kind")
    public JsonResult pptListByKind(HttpServletRequest httpServletRequest,
                                    @RequestParam(value = "keyword", required = false) String keyword,
                                    @RequestParam(value = "firstKind", required = false) String firstKind, @RequestParam(value = "secondKind", required = false) String secondKind,
                                    @RequestParam("page") int page, @RequestParam("size") int size) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptList keyword:[{}], firstKind:[{}], secondKind:[{}], page:[{}], size:[{}], token:[{}]", keyword, firstKind, secondKind, page, size, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptListByKind(keyword, firstKind, secondKind, page, size, userCode);

        return response;
    }

    @ApiOperation("PPT收藏")
    @GetMapping("/ppt/collect")
    public JsonResult pptCollect(HttpServletRequest httpServletRequest,
                                 @RequestParam("folderCode") String folderCode, @RequestParam("pptCode") String pptCode) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptCollect folderCode:[{}], pptCode:[{}], token:[{}]", folderCode, pptCode, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptCollect(folderCode, userCode, pptCode);

        return response;
    }

    @ApiOperation("PPT收藏列表")
    @GetMapping("/ppt/collect/list")
    public JsonResult pptCollectList(HttpServletRequest httpServletRequest,
                                     @RequestParam("folderCode") String folderCode,
                                     @RequestParam("page") int page, @RequestParam("size") int size) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptCollectList folderCode:[{}], page:[{}], size:[{}], token:[{}]", page, size, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptCollectList(folderCode, page, size, userCode);

        return response;
    }

    @ApiOperation("文件夹列表")
    @GetMapping("/ppt/collect/folder/list")
    public JsonResult pptCollectFolderList(HttpServletRequest httpServletRequest,
                                     @RequestParam(value = "pptCode", required = false) String pptCode) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptCollectFolderList pptCode:[{}], token:[{}]", pptCode, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptCollectFolderList(pptCode, userCode);

        return response;
    }

    @ApiOperation("新建收藏文件夹")
    @GetMapping("/ppt/folder/create")
    public JsonResult pptFolderCreate(HttpServletRequest httpServletRequest,
                                     @RequestParam("folder") String folder) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptFolderCreate folder:[{}], token:[{}]", folder, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptFolderCreate(folder, userCode);

        return response;
    }

    @ApiOperation("文件夹名字修改")
    @GetMapping("/ppt/folder/update")
    public JsonResult pptFolderUpdate(HttpServletRequest httpServletRequest,
                                      @RequestParam("folderCode") String folderCode, @RequestParam("folder") String folder) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptFolderUpdate folderCode:[{}], folder:[{}], token:[{}]", folderCode, folder, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptFolderUpdate(userCode, folderCode, folder);

        return response;
    }

    @ApiOperation("文件夹删除")
    @GetMapping("/ppt/folder/delete")
    public JsonResult pptFolderDelete(HttpServletRequest httpServletRequest,
                                      @RequestParam("folderCode") String folderCode) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptFolderDelete folderCode:[{}], token:[{}]", folderCode, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptFolderDelete(userCode, folderCode);

        return response;
    }

    @ApiOperation("我的上传列表")
    @GetMapping("/ppt/me/list")
    public JsonResult pptMeList(HttpServletRequest httpServletRequest,
                                @RequestParam("page") int page, @RequestParam("size") int size) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptMeList page:[{}], size:[{}], token:[{}]", page, size, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptMeList(userCode, page, size);

        return response;
    }

    @ApiOperation("PPT删除")
    @GetMapping("/ppt/delete")
    public JsonResult pptDelete(HttpServletRequest httpServletRequest,
                                @RequestParam("pptCode") String pptCode) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptDelete pptCode:[{}] token:[{}]", pptCode, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptDelete(userCode, pptCode);

        return response;
    }

    @ApiOperation("PPT 浏览量+1")
    @GetMapping("/ppt/see")
    public JsonResult pptSee(@RequestParam("pptCode") String pptCode) {
        log.info("PptController pptSee pptCode:[{}]", pptCode);
        JsonResult response = pptService.pptSee(pptCode);

        return response;
    }

    @ApiOperation("评论")
    @PostMapping("/ppt/comment")
    public JsonResult pptComment(HttpServletRequest httpServletRequest,
                                 @RequestBody CommentPO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptComment request:[{}], token:[{}]", request, token);
        String userCode = userService.getUserCodeByToken(token);
        request.setUserCode(userCode);
        JsonResult response = pptService.pptComment(request);

        return response;
    }

    @ApiOperation("回复")
    @PostMapping("/ppt/reply")
    public JsonResult pptReply(HttpServletRequest httpServletRequest,
                                 @RequestBody ReplyPO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptReply request:[{}], token:[{}]", request, token);
        String userCode = userService.getUserCodeByToken(token);
        request.setUserCode(userCode);
        JsonResult response = pptService.pptReply(request);

        return response;
    }

    @ApiOperation("评论列表")
    @GetMapping("/ppt/comment/list")
    public JsonResult pptCommentList(HttpServletRequest httpServletRequest,
                                     @RequestParam("pptCode") String pptCode,
                                     @RequestParam("page") int page, @RequestParam("size") int size) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptCommentList pptCode:[{}], page:[{}], size:[{}], token:[{}]", pptCode, page, size, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptCommentList(userCode, pptCode, page, size);

        return response;
    }

    @ApiOperation("回复列表")
    @GetMapping("/ppt/reply/list")
    public JsonResult pptReplyList(HttpServletRequest httpServletRequest,
                                   @RequestParam("commentCode") String commentCode,
                                   @RequestParam("page") int page, @RequestParam("size") int size) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptReplyList commentCode:[{}], page:[{}], size:[{}], token:[{}]", commentCode, page, size, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptReplyList(userCode, commentCode, page, size);

        return response;
    }


}
