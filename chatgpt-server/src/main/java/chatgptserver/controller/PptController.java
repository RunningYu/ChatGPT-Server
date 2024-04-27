package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.QuestionRequestAO;
import chatgptserver.bean.ao.ppt.PptCreateRequestAO;
import chatgptserver.bean.ao.ppt.PptUploadRequestAO;
import chatgptserver.service.PptService;
import chatgptserver.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

    /**
     * todo：未完成
     */
    @ApiOperation("PPT评分")
    @GetMapping("/ppt/scoring")
    public JsonResult pptScoring(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptScoring token:[{}]", token);

        return null;
    }

    @ApiOperation("获取分类数据")
    @GetMapping("/ppt/kind/list")
    public JsonResult pptKindList() {
        log.info("PptController pptKindList");
        JsonResult response = pptService.pptKindList();

        return response;
    }


    @ApiOperation("PPT分类查询")
    @GetMapping("/ppt/list/by/kind")
    public JsonResult pptListByKind(HttpServletRequest httpServletRequest,
                                    @RequestParam(value = "firstKind", required = false) String firstKind, @RequestParam(value = "secondKind", required = false) String secondKind,
                                    @RequestParam("page") int page, @RequestParam("size") int size) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptList firstKind:[{}], secondKind:[{}], page:[{}], size:[{}], token:[{}]", firstKind, secondKind, page, size, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptListByKind(firstKind, secondKind, page, size, userCode);

        return response;
    }

    @ApiOperation("PPT收藏")
    @GetMapping("/ppt/collect")
    public JsonResult pptCollect(HttpServletRequest httpServletRequest,
                                 @RequestParam("pptCode") String pptCode) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptCollect pptCode:[{}], token:[{}]", pptCode, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptCollect(userCode, pptCode);

        return response;
    }

    @ApiOperation("PPT收藏列表")
    @GetMapping("/ppt/collect/list")
    public JsonResult pptCollectList(HttpServletRequest httpServletRequest,
                                     @RequestParam("page") int page, @RequestParam("size") int size) {
        String token = httpServletRequest.getHeader("token");
        log.info("PptController pptCollectList page:[{}], size:[{}], token:[{}]", page, size, token);
        String userCode = userService.getUserCodeByToken(token);
        JsonResult response = pptService.pptCollectList(page, size, userCode);

        return response;
    }

}
