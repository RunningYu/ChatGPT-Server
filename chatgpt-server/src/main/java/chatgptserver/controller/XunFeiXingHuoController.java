package chatgptserver.controller;

import chatgptserver.Common.SseUtils;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.ao.QuestionRequestAO;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiAuthAlgorithm;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiClient;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.CreateResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ProgressResponse;
import chatgptserver.bean.po.UserPO;
import chatgptserver.enums.GPTConstants;
import chatgptserver.service.UserService;
import chatgptserver.service.XunFeiService;
import chatgptserver.utils.JwtUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static chatgptserver.Common.SseUtils.sseEmitterThreadLocal;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/22
 */

@ApiModel("讯飞星火")
@Slf4j
@RestController
public class XunFeiXingHuoController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private XunFeiService xunFeiService;

    @GetMapping("sse/test")
    public SseEmitter sseTest() {
//        SseEmitter sseEmitter = new SseEmitter();
//
//        try {
//            for (int i = 0; i < 10; i ++) {
//                sseEmitter.send(SseEmitter.event().comment("[" + i + "]"));
//                System.out.println("[" + i + "]");
//                Thread.sleep(1000);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException();
//        }


        //设置默认的超时时间60秒，超时之后服务端主动关闭连接。
//        SseEmitter sseEmitter = new SseEmitter(60 * 1000L);
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitterThreadLocal.set(sseEmitter);

        sseEmitter.onCompletion(new Runnable() {
            @Override
            public void run() {
                System.out.println("nnnnnnnnnnnnnnnnn");
                try {
                    for (int i = 0; i < 10; i ++) {
                        System.out.println("[" + i + "]");
                        sseEmitter.send(SseEmitter.event().comment("[" + i + "]"));
                        Thread.sleep(1000);
                    }
                } catch (IOException e) {
                    throw new RuntimeException();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        });

        return sseEmitter;
    }

//    @ApiOperation("讯飞星火：图片理解")
//    @GetMapping("/chat/xf/image/understander")
//    public SseEmitter xfImageUnderstand(@RequestParam("image") MultipartFile image, @RequestParam("content") String content,
//                                        @RequestParam("userCode") String userCode, @RequestParam("chatCode") String chatCode) {
//        log.info("ChatGptController xfPictureUnderstand image:[{}], content[{}], userCode:[{}], chatCode:[{}]", image, content, userCode, chatCode);
//        //设置默认的超时时间60秒，超时之后服务端主动关闭连接。
////        SseEmitter sseEmitter = new SseEmitter(60 * 1000L);
//        SseEmitter sseEmitter = new SseEmitter();
//        Long threadId = Thread.currentThread().getId();
//        SseUtils.sseEmittersMap.put(threadId, sseEmitter);
//        SseEmitter sseEmitter1 = xunFeiService.xfImageUnderstand(threadId, image, content, userCode, chatCode);
//        return sseEmitter;
//    }

    @ApiOperation("讯飞星火：图片理解")
    @PostMapping("/chat/xf/image/understander")
    public JsonResult xfImageUnderstand(HttpServletRequest httpServletRequest,
                                        @RequestParam(value = "image", required = false) MultipartFile image,
                                        @RequestParam("content") String content,
                                        @RequestParam("chatCode") String chatCode) {
        String token = httpServletRequest.getHeader("token");
        log.info("ChatGptController xfPictureUnderstand token:[{}]", token);
        log.info("ChatGptController xfPictureUnderstand token:[{}], image:[{}], content[{}], chatCode:[{}]", token, image, content, chatCode);
        Long threadId = Thread.currentThread().getId();
        JsonResult response = xunFeiService.xfImageUnderstand(threadId, image, content, token, chatCode);

        return response;
    }

    @ApiOperation("讯飞星火：图片生成")
    @PostMapping("/chat/xf/image/create")
    public JsonResult xfImageCreate(HttpServletRequest httpServletRequest,
                                    @RequestBody QuestionRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("ChatGptController xfImageCreate token:[{}], request:[{}]", token, request);
        JsonResult response = xunFeiService.xfImageCreate(request.getContent(), token, request.getChatCode());

        return response;
    }

//    @ApiOperation("讯飞星火：文本问答")
//    @PostMapping(value = "/chat/xf/question")
//    public SseEmitter xfQuestion(@RequestBody QuestionRequestAO request) {
//        log.info("ChatGptController xfQuestion request:[{}]", request);
//        SseEmitter sseEmitter = new SseEmitter();
//        Long threadId = Thread.currentThread().getId();
//        SseUtils.sseEmittersMap.put(threadId, sseEmitter);
//        xunFeiService.xfQuestion(threadId, request);
//
//        return sseEmitter;
//    }

    @ApiOperation("讯飞星火：文本问答")
    @PostMapping(value = "/chat/xf/question")
    public JsonResult xfQuestion(HttpServletRequest httpServletRequest,
                                 @RequestBody QuestionRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        String userCode = userService.getUserCodeByToken(token);
        request.setUserCode(userCode);
        log.info("ChatGptController xfQuestion userCode:[{}], request:[{}]", userCode, request);
        Long threadId = Thread.currentThread().getId();
        MessagesAO response = xunFeiService.xfQuestion(threadId, request);

        return JsonResult.success(response);
    }

    @ApiOperation("讯飞星火：PPT生成")
    @PostMapping("/chat/xf/ppt/create")
    public JsonResult xfPptCreate(HttpServletRequest httpServletRequest,
                                  @RequestBody QuestionRequestAO request) {
        String token = httpServletRequest.getHeader("token");
        log.info("ChatGptController xfPptCreate token:[{}], request:[{}]", token, request);
        JsonResult response = xunFeiService.xfPptCreate(request.getContent(), token, request.getChatCode());

        return response;
    }


}

