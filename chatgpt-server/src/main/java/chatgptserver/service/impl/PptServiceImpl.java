package chatgptserver.service.impl;

import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.MessagesAO;
import chatgptserver.bean.ao.ppt.PptCreateRequestAO;
import chatgptserver.bean.ao.ppt.PptOutlineResponseAO;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiAuthAlgorithm;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiClient;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.CreateResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ProgressResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.PptCoverResponseDTO;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.PptOutlineResponse;
import chatgptserver.bean.dto.ppt.PptColor;
import chatgptserver.bean.dto.ppt.PptColorListResponse;
import chatgptserver.bean.po.PptColorPO;
import chatgptserver.dao.PptMapper;
import chatgptserver.enums.GPTConstants;
import chatgptserver.service.MessageService;
import chatgptserver.service.PptService;
import chatgptserver.service.UserService;
import chatgptserver.service.XunFeiService;
import chatgptserver.utils.PptUtils;
import chatgptserver.utils.StorageUtils;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
@Slf4j
@Service
public class PptServiceImpl implements PptService {

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private MessageService messageService;

    @Autowired
    private XunFeiService xunFeiService;

    @Autowired
    private PptMapper pptMapper;

    @Autowired
    private UserService userService;

    @Override
    public JsonResult pptOutlineCreate(String userCode, String content, Boolean isRebuild, String cid) {
        log.info("PptServiceImpl pptOutlineCreate userCode:[{}], content:[{}], isRebuild:[{}], cid:[{}}", userCode, content, isRebuild, cid);
        if (userCode == null || "".equals(userCode)) {
            return JsonResult.error(500, "请先登录");
        }
        Date questionTime = new Date();
        if (isRebuild == false && (content == null || "".equals(content))) {
            log.info("XunFeiServiceImpl xfPptCreate 请先输入文字描述");

            return JsonResult.error(500, "请先输入文字描述");
        }
        String pptUrl = "", coverImgSrc = "", outLineStr = "";
        // 输入个人appId
        String appId = GPTConstants.GPT_KEY_MAP.get(GPTConstants.XF_XH_APPID_KEY);
        String secret = GPTConstants.GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY);
        long timestamp = System.currentTimeMillis() / 1000;
        String ts = String.valueOf(timestamp);
        // 获得鉴权信息
        ApiAuthAlgorithm auth = new ApiAuthAlgorithm();
        String signature = auth.getSignature(appId, secret, timestamp);
        System.out.println(signature);

        // 建立链接
        ApiClient client = new ApiClient(GPTConstants.XF_XH_PPT_CREATE_URL);
        // 检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(cid)) {
            StorageUtils.stopRequestMap.remove(cid);

            return JsonResult.success();
        }

        try {
            // 查询PPT模板信息
            String templateResult = client.getTemplateList(appId, ts, signature);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("XunFeiServiceImpl xfPptCreate templateResult:[{}]", templateResult);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");

            // 发送生成PPT请求
            String query = content;
            String resp = client.createPPT(appId, ts, signature, query);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("XunFeiServiceImpl xfPptCreate resp:[{}]", resp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            CreateResponse response = JSON.parseObject(resp, CreateResponse.class);

            // 利用sid查询PPT生成进度
            int progress = 0;
            ProgressResponse progressResponse;
            while (progress < 100) {
                // 再次检查是否要取消生成
                if (StorageUtils.stopRequestMap.containsKey(cid)) {
                    StorageUtils.stopRequestMap.remove(cid);

                    return JsonResult.success();
                }
                String progressResult = client.checkProgress(appId, ts, signature, response.getData().getSid());
                progressResponse = JSON.parseObject(progressResult, ProgressResponse.class);
                progress = progressResponse.getData().getProcess();
                System.out.println("-->" + progressResult);

                if (progress < 100) {
                    Thread.sleep(5000);
                }
            }

            // 大纲生成
            String outlineQuery = query;

            String outlineResp = client.createOutline(appId, ts, signature, outlineQuery);
            System.out.println(outlineResp);
            CreateResponse outlineResponse = JSON.parseObject(outlineResp, CreateResponse.class);
            // 封装大纲
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            PptOutlineResponse outline = JSON.parseObject(outlineResponse.getData().getOutline(), PptOutlineResponse.class);
            outLineStr = PptUtils.buildOutLineMD(outline);
            System.out.println("生成的大纲如下：");
            log.info("XunFeiServiceImpl xfPptCreate outline:[{}]", outline);
//            log.info("XunFeiServiceImpl xfPptCreate Outline:[{}]", outlineResponse.getData().getOutline());
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            PptOutlineResponseAO result = new PptOutlineResponseAO(outline, outlineResponse.getData().getSid(), content);

            return JsonResult.success(result);
        } catch (Exception e) {
            return JsonResult.error(500, "系统异常");
        }
    }

    @Override
    public JsonResult pptCreateByOutline(PptCreateRequestAO request) {
        log.info("PptServiceImpl pptCreateByOutline request:[{}]", request);
        if (request.getUserCode() == null || "".equals(request.getUserCode())) {
            log.info("PptServiceImpl pptCreateByOutline 请先登录");

            return JsonResult.error(500, "请先登录");
        }

        Date questionTime = new Date();
        String pptUrl = "", coverImgSrc = "", outLineStr = "";
        String userCode = request.getUserCode();
        // 输入个人appId
        String appId = GPTConstants.GPT_KEY_MAP.get(GPTConstants.XF_XH_APPID_KEY);
        String secret = GPTConstants.GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY);
        long timestamp = System.currentTimeMillis() / 1000;
        String ts = String.valueOf(timestamp);
        // 获得鉴权信息
        ApiAuthAlgorithm auth = new ApiAuthAlgorithm();
        String signature = auth.getSignature(appId, secret, timestamp);
        System.out.println(signature);

        // 建立链接
        ApiClient client = new ApiClient(GPTConstants.XF_XH_PPT_CREATE_URL);
        // 检查是否要取消生成
        if (StorageUtils.stopRequestMap.containsKey(request.getCid())) {
            StorageUtils.stopRequestMap.remove(request.getCid());

            return JsonResult.success();
        }

        try {
            String sid = request.getSid();
            ProgressResponse progressResponse;
            // 基于sid和大纲生成ppt
            String sidResp = client.createPptBySid(appId, ts, signature, request.getSid());
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("PptServiceImpl pptCreateByOutline sidResp1:[{}]", sidResp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            CreateResponse sidResponse = JSON.parseObject(sidResp, CreateResponse.class);
            sidResp = client.createPptBySid(appId, ts, signature, request.getSid());
            System.out.println(sidResp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("PptServiceImpl pptCreateByOutline sidResp2:[{}]", sidResp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");

            sidResponse = JSON.parseObject(sidResp, CreateResponse.class);
            // 利用sid查询PPT生成进度
            int progress = 0;
            while (progress < 100) {
                // 再次检查是否要取消生成
                if (StorageUtils.stopRequestMap.containsKey(request.getCid())) {
                    StorageUtils.stopRequestMap.remove(request.getCid());

                    return JsonResult.success();
                }
                String progressResult = client.checkProgress(appId, ts, signature, sidResponse.getData().getSid());
                progressResponse = JSON.parseObject(progressResult, ProgressResponse.class);
                progress = progressResponse.getData().getProcess();
                System.out.println("-->" + progressResult);
                if (progress < 100) {
                    Thread.sleep(5000);
                }
            }
            // 再次检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(request.getCid())) {
                StorageUtils.stopRequestMap.remove(request.getCid());

                return JsonResult.success();
            }
            String outline = JSON.toJSONString(request.getOutline());
            // 基于大纲生成ppt
//            String pptResp = client.createPptByOutline(appId, ts, signature, outlineQuery, outlineResponse.getData().getOutline());
            String pptResp = client.createPptByOutline(appId, ts, signature, request.getContent(), outline, request.getColorTheme());
            // 解析 PPT封面
            PptCoverResponseDTO coverRes = JSON.parseObject(pptResp, PptCoverResponseDTO.class);
            coverImgSrc = coverRes.getData().getCoverImgSrc();
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            log.info("PptServiceImpl pptCreateByOutline pptResp:[{}]", pptResp);
            System.out.println("————————————————————————————————————————————————————————————————————————————————————————————————");
            CreateResponse pptResponse = JSON.parseObject(pptResp, CreateResponse.class);
            // 利用sid查询PPT生成进度
            progress = 0;
            while (progress < 100) {
                // 再次检查是否要取消生成
                if (StorageUtils.stopRequestMap.containsKey(request.getCid())) {
                    StorageUtils.stopRequestMap.remove(request.getCid());

                    return JsonResult.success();
                }
                String progressResult = client.checkProgress(appId, ts, signature, pptResponse.getData().getSid());
                progressResponse = JSON.parseObject(progressResult, ProgressResponse.class);
                progress = progressResponse.getData().getProcess();
                System.out.println("-->" + progressResult);
                pptUrl = progressResponse.getData().getPptUrl();
                if (progress < 100) {
                    Thread.sleep(5000);
                }
            }
            log.info("PptServiceImpl pptCreateByOutline pptUrl:[{}]", pptUrl);
            String replication = outLineStr + "\n" + coverImgSrc + "\n\n" + pptUrl + "\n\n" + GPTConstants.RESULT_CREATE_TAG;
            MessagesAO responseAO = messageService.buildMessageAO(userCode, null, request.getContent(), replication, questionTime);
            // 再次检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(request.getCid())) {
                StorageUtils.stopRequestMap.remove(request.getCid());

                return JsonResult.success();
            }
            messageService.recordHistory(userCode, request.getUserCode(), request.getContent(), replication, request.getIsRebuild(), questionTime);

            return JsonResult.success(responseAO);
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    @Override
    public JsonResult pptColorList() {
        log.info("PptServiceImpl pptColorList");
        List<PptColor> list = (List<PptColor>) caffeineCache.get("color", k -> {
            List<PptColorPO> pptColors = pptMapper.pptColorList();
            if (pptColors == null || pptColors.size() == 0) {
                // 输入个人appId
                String appId = GPTConstants.GPT_KEY_MAP.get(GPTConstants.XF_XH_APPID_KEY);
                String secret = GPTConstants.GPT_KEY_MAP.get(GPTConstants.XF_XH_API_SECRET_KEY);
                long timestamp = System.currentTimeMillis() / 1000;
                String ts = String.valueOf(timestamp);
                // 获得鉴权信息
                ApiAuthAlgorithm auth = new ApiAuthAlgorithm();
                String signature = auth.getSignature(appId, secret, timestamp);
                System.out.println(signature);

                // 建立链接
                ApiClient client = new ApiClient("https://zwapi.xfyun.cn");
                String templateResult = null;
                try {
                    // 查询PPT模板信息
                    templateResult = client.getTemplateList(appId, ts, signature);
                } catch (IOException e) {
                    return JsonResult.error(500, "系统异常");
                }
                log.info("PptServiceImpl pptCreateByOutline templateResult:[{}]", templateResult);
                PptColorListResponse response = JSON.parseObject(templateResult, PptColorListResponse.class);
                List<PptColor> pptColorList = response.getData();
                for (PptColor color : pptColorList) {
                    pptMapper.remainColor(color);
                }
                log.info("PptServiceImpl pptColorList select from OPEN_API");
            } else {
                log.info("PptServiceImpl pptColorList select from DB");
            }
            List<PptColor> pptColorList = new ArrayList<>();
            for (PptColorPO pptColorPO : pptColors) {
                PptColor pptColor = ConvertMapping.pptColorPO2PptColor(pptColorPO);
                pptColorList.add(pptColor);
            }

            return pptColors;
        });
        caffeineCache.put("color", list);
        log.info("PptServiceImpl pptCreateByOutline list:[{}]", list);

        return JsonResult.success(list);
    }
}
