package chatgptserver.controller;

import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.QuestionRequestAO;
import chatgptserver.bean.po.UserPO;
import chatgptserver.service.UserService;
import chatgptserver.service.WenXinService;
import chatgptserver.utils.JwtUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 9:15
 */
@Slf4j
@RestController
public class WenXinYiYanController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private WenXinService wenXinService;

    @Autowired
    private UserService userService;

    @ApiOperation("文心一言：文本问答")
    @PostMapping("/chat/wenXin/question")
    public JsonResult wenXinChat(HttpServletRequest httpServletRequest,
                                 @RequestBody QuestionRequestAO requestAO) {
        String token = httpServletRequest.getHeader("token");
        log.info("ChatGptController wenXinChat token:[{}]", token);
        String userCode = userService.getUserCodeByToken(token);
        log.info("WenXinYiYanController wenXinChat requestAO:[{}]", requestAO);
        JsonResult response = wenXinService.getMessageFromWenXin(userCode, requestAO.getChatCode(), requestAO.getContent(), requestAO.getIsRebuild(), requestAO.getCid());

        return response;
    }

    @ApiOperation("文心一言：图片生成")
    @PostMapping("/chat/wenXin/image/create")
    public JsonResult wenXinImageCreate(HttpServletRequest httpServletRequest,
                                        @RequestBody QuestionRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController wenXinImageCreate token:[{}]", token);
        UserPO tokenUser = jwtUtils.getUserFromToken(token);
        log.info("WenXinYiYanController wenXinImageCreate tokenUser:[{}] request:[{}]", tokenUser, request);
        JsonResult response = wenXinService.wxImageCreate(tokenUser.getUserCode(), request.getChatCode(), request.getContent(), request.getIsRebuild(), request.getCid());

        return response;
    }

    @ApiOperation("文心一言：图片理解【不支持多轮对话】")
    @PostMapping("/chat/wenXin/image/understand")
    public JsonResult wenXinImageUnderstand(HttpServletRequest httpServletRequest,
                                            @RequestParam(value = "image", required = false) MultipartFile image,
                                            @RequestParam("chatCode") String chatCode,
                                            @RequestParam("content") String content,
                                            @RequestParam("isRebuild") Boolean isRebuild,
                                            @RequestParam("cid") String cid) {
        log.info("WenXinYiYanController wenXinImageUnderstand chatCode:[{}], image:[{}], content:[{}], isRebuild:[{}], cid:[{}]", chatCode, image, content, isRebuild, cid);
        String token = httpServletRequest.getHeader("token");
        log.info("WenXinYiYanController wenXinImageUnderstand token:[{}]", token);
        JsonResult response = wenXinService.wenXinImageUnderstand(token, chatCode, image, content, isRebuild, cid);

        return response;
    }



}
