package chatgptserver.service.impl;

import chatgptserver.Common.FileUtil;
import chatgptserver.Mapping.ConvertMapping;
import chatgptserver.bean.ao.JsonResult;
import chatgptserver.bean.ao.ppt.*;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiAuthAlgorithm;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ApiClient;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.CreateResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.XunFeiPptCreate.ProgressResponse;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.PptCoverResponseDTO;
import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.PptOutlineResponse;
import chatgptserver.bean.dto.ppt.PptColor;
import chatgptserver.bean.dto.ppt.PptColorListResponse;
import chatgptserver.bean.po.*;
import chatgptserver.dao.PptMapper;
import chatgptserver.enums.GPTConstants;
import chatgptserver.service.MessageService;
import chatgptserver.service.PptService;
import chatgptserver.service.UserService;
import chatgptserver.service.XunFeiService;
import chatgptserver.utils.MinioUtil;
import chatgptserver.utils.PptUtils;
import chatgptserver.utils.StorageUtils;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
@Slf4j
@Service
public class PptServiceImpl implements PptService {

    @Autowired
    private MinioUtil minioUtil;

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
            ProgressResponse progressResponse;
            int progress = 0;
            String outline = JSON.toJSONString(request.getOutline());
            // 基于大纲生成ppt
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
            PptCreateResponseAO responseAO = messageService.buildPptCreateResponseAO(userCode, coverImgSrc, request.getContent(), pptUrl, questionTime);

            // 再次检查是否要取消生成
            if (StorageUtils.stopRequestMap.containsKey(request.getCid())) {
                StorageUtils.stopRequestMap.remove(request.getCid());

                return JsonResult.success();
            }
            messageService.recordHistory(userCode, request.getUserCode(), request.getContent(), replication, request.getIsRebuild(), questionTime);

            return JsonResult.success(responseAO);
        } catch (Exception e) {
            PptCreateResponseAO responseAO1 = buildDeault();

            return JsonResult.success(responseAO1);
//            throw new RuntimeException();
        }

    }

    private PptCreateResponseAO buildDeault() {
        PptCreateResponseAO response = new PptCreateResponseAO();
        response.setUserCode("123");
        response.setQuestion("授课模板PPT");
        response.setCoverUrl("https://bjcdn.openstorage.cn/xinghuo-privatedata/2x8wv4xs.jpg");
        response.setReplication("https://bjcdn.openstorage.cn/xinghuo-privatedata/%2Ftmp/apiTempFilec7fac625ccd943d9899c4f177cd4aedd1668730078262484467/%E6%8E%88%E8%AF%BE%E6%A8%A1%E6%9D%BF%E8%AE%BE%E8%AE%A1%E8%A6%81%E7%82%B9.pptx");
        response.setCreateTime(new Date());
        response.setReplyTime(new Date());

        return response;
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

    @Override
    public JsonResult pptUpload(PptUploadRequestAO request) {
        log.info("PptServiceImpl pptUpload request:[{}]", request);
        if (request.getUserCode() == null || "".equals(request.getUserCode())) {
            log.info("PptServiceImpl pptUpload 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        PptPO pptPO = null;
        try {
            String pptUrl = minioUtil.upLoadFileToURL(request.getPptFile());
            // 截取PPT第一页作为封面
            String coverUrl = minioUtil.upLoadFileToURL(request.getPptCoverFile());
//            coverUrl = cutPptCover(request.getPptFile());
            pptPO = ConvertMapping.buildPptPO(request, pptUrl, coverUrl);
//            UserPO userPO = userService.getUserByCode(request.getUserCode());
//            pptPO.setUsername(userPO.getUsername());
            log.info("PptServiceImpl pptUpload pptPO:[{}]", pptPO);
        } catch (Exception e) {
            log.info("PptServiceImpl pptUpload 文件上传失败");
            return JsonResult.error(500, "文件上传失败");
        }
        int id = pptMapper.pptUpload(pptPO);
        String pptCode = "ppt_" + pptPO.getId();
        pptMapper.updatePptCodeById(pptCode, pptPO.getId());

        return JsonResult.success("上传成功");
    }

//    private String cutPptCover(MultipartFile pptFile) {
//
////        MultipartFile multipartFile = FileUtil.cutPptConver(pptFile);
//
//    }

    @Override
    public JsonResult pptKindList() {
        log.info("PptServiceImpl pptKindList");
        List<String> firstKindList = pptMapper.firstKindList();
        List<PptKindResponseAO> response = new ArrayList<>();
        for (String firstKind : firstKindList) {
            PptKindResponseAO responseAO = new PptKindResponseAO(firstKind);
            List<String> secondKindList = pptMapper.secondListByFirstKind(firstKind);
            responseAO.setSecondKinds(secondKindList.stream().collect(Collectors.toList()));
            response.add(responseAO);
        }
        log.info("PptServiceImpl pptKindList response:[{}]", response);

        return JsonResult.success(response);
    }

    @Override
    public JsonResult pptListByKind(String keyword, String firstKind, String secondKind, int page, int size, String userCode) {
        log.info("PptServiceImpl pptList keyword:[{}], firstKind:[{}], secondKind:[{}], page:[{}], size:[{}], userCode:[{}]", keyword, firstKind, secondKind, page, size, userCode);
        int startIndex = (page - 1) * size;
        List<PptPO> pptPOList = pptMapper.pptListByKind(keyword, firstKind, secondKind, startIndex, size);
        List<PptAO> pptAOList = new ArrayList<>();
        for (PptPO pptPO : pptPOList) {
            PptAO pptAO = ConvertMapping.pptPO2PptAO(pptPO);
            pptAO.setEnableScore(pptPO.getUserCode().equals(userCode) ? false : true);
            int hasScored = pptMapper.hasScored(userCode, pptPO.getPptCode());
            pptAO.setHaveScored(hasScored == 0 ? false : true);
            UserPO userPO = userService.getUserByCode(pptPO.getUserCode());
            if (userPO != null) {
                pptAO.setUsername(userPO.getUsername());
                pptAO.setHeadshot(userPO.getHeadshot());
            }
            Integer isCollected = pptMapper.isCollected(userCode, pptPO.getPptCode());
            pptAO.setIsCollected((isCollected == null || isCollected == 0) ? false : true);
            pptAOList.add(pptAO);
        }
        int total = pptMapper.totalOfpptListByKind(keyword, firstKind, secondKind);
        PptKindListResponseAO response = new PptKindListResponseAO(total, pptAOList);
        log.info("PptServiceImpl pptListByKind pptPOList:[{}]", pptPOList);

        return JsonResult.success(response);
    }

    @Override
    public JsonResult pptCollect(String folderCode, String userCode, String pptCode) {
        log.info("PptServiceImpl pptCollect folderCode:[{}], userCode:[{}], pptCode:[{}]", folderCode, userCode, pptCode);
        if (userCode == null || "".equals(userCode)) {
            log.info("PptServiceImpl pptCollect 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        Integer isCollected = null;
        isCollected = pptMapper.pptIsCollected(folderCode, userCode, pptCode);
        if (isCollected == null || isCollected == 0) {
            pptMapper.pptCollect(folderCode, userCode, pptCode);
            pptMapper.updateCollectAmount(pptCode, 1);

            return JsonResult.success("收藏成功");
        } else {
            pptMapper.pptDisCollect(folderCode, userCode, pptCode);
            pptMapper.updateCollectAmount(pptCode, -1);

            return JsonResult.success("取消收藏成功");
        }
    }

    @Override
    public JsonResult pptCollectList(String folderCode, int page, int size, String userCode) {
        log.info("PptServiceImpl pptCollectList folderCode:[{}], userCode:[{}]", folderCode, userCode);
        if (userCode == null || "".equals(userCode)) {
            log.info("PptServiceImpl pptCollectList 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        int startIndex = (page - 1) * size;
        List<PptPO> list = pptMapper.pptCollectList(folderCode, userCode, startIndex, size);
        int total = pptMapper.pptCollectListTotal(folderCode, userCode);
        List<PptAO> pptAOList = new ArrayList<>();
        for (PptPO pptPO : list) {
            PptAO pptAO = ConvertMapping.pptPO2PptAO(pptPO);
            pptAO.setEnableScore(pptPO.getUserCode().equals(userCode) ? false : true);
            int hasScored = pptMapper.hasScored(userCode, pptPO.getPptCode());
            pptAO.setHaveScored(hasScored == 0 ? false : true);
            UserPO userPO = userService.getUserByCode(pptPO.getUserCode());
            if (userPO != null) {
                pptAO.setUsername(userPO.getUsername());
                pptAO.setHeadshot(userPO.getHeadshot());
            }
            pptAO.setIsCollected(true);
            pptAOList.add(pptAO);
        }
        PptCollectListResponseAO response = new PptCollectListResponseAO(total, pptAOList);
        log.info("PptServiceImpl pptCollectList response:[{}]", response);

        return JsonResult.success(response);
    }

    @Override
    public JsonResult pptFolderCreate(String folder, String userCode) {
        log.info("PptServiceImpl pptFolderCreate folder:[{}], userCode:[{}]", folder, userCode);
        if (userCode == null || "".equals(userCode)) {
            log.info("PptServiceImpl pptFolderCreate 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        FolderPO folderPO = new FolderPO(userCode, folder);
        folderPO.setIsDefault(0);
        pptMapper.folderCreate(folderPO);
        String folderCode = "folder_" + folderPO.getId();
        pptMapper.updateFolderCode(folderCode, folderPO.getId());
        folderPO.setFolderCode(folderCode);

        return JsonResult.success(folderPO);
    }

    @Override
    public JsonResult pptCollectFolderList(String pptCode, String userCode) {
        log.info("PptServiceImpl pptCollectFolderList pptCode:[{}], userCode:[{}]", pptCode, userCode);
        if (userCode == null || "".equals(userCode)) {
            log.info("PptServiceImpl pptCollectFolderList 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        List<FolderPO> list = pptMapper.folderList(userCode);
        List<FolderAO> response = new ArrayList<>();
        for (FolderPO folderPO : list) {
            FolderAO folderAO = ConvertMapping.folderPO2FolderAO(folderPO);
            if (pptCode != null && !"".equals(pptCode)) {
                Integer isCollected = null;
                isCollected = pptMapper.pptIsCollected(folderPO.getFolderCode(), userCode, pptCode);
                folderAO.setIsCollected((isCollected == null || isCollected == 0) ? false : true);
            }
            int amount = pptMapper.countAmount(folderPO.getFolderCode());
            folderAO.setAmount(amount);
            response.add(folderAO);
        }

        return JsonResult.success(response);
    }

    @Override
    public JsonResult pptFolderUpdate(String userCode, String folderCode, String folder) {
        log.info("PptServiceImpl pptFolderUpdate folderCode:[{}], folder:[{}], userCode:[{}]", folderCode, folder, userCode);
        pptMapper.folderUpdate(folderCode, folder);

        return JsonResult.success("修改成功");
    }

    @Override
    public JsonResult pptFolderDelete(String userCode, String folderCode) {
        log.info("PptServiceImpl pptFolderDelete folderCode:[{}], userCode:[{}]", folderCode, userCode);
        int isDelete = pptMapper.folderDelete(folderCode);
        if (isDelete == 0) {
            log.info("PptServiceImpl pptFolderDelete isDelete:[{}] 默认收藏夹不可删除", isDelete);
            return JsonResult.error(500, "默认收藏夹不可删除");
        }
        log.info("PptServiceImpl pptFolderDelete isDelete:[{}] 删除成功", isDelete);
        // collectAmount -1
        pptMapper.updateCollectAmountAfterFolderDelete(folderCode);
        // 删除文件夹下收藏的记录
        pptMapper.collectRecordDelete(folderCode);

        return JsonResult.success("删除成功");
    }

    @Override
    public void createDefaultFolder(String userCode) {
        log.info("PptServiceImpl createDefaultFolder userCode:[{}]", userCode);
        FolderPO folderPO = new FolderPO(userCode, "默认收藏夹");
        folderPO.setIsDefault(1);
        pptMapper.folderCreate(folderPO);
        String folderCode = "folder_" + folderPO.getId();
        pptMapper.updateFolderCode(folderCode, folderPO.getId());
    }

    @Override
    public JsonResult pptMeList(String userCode, int page, int size) {
        log.info("PptServiceImpl pptMeList userCode:[{}]", userCode);
        int startIndex = (page - 1) * size;
        List<PptPO> list = pptMapper.pptMeList(userCode, startIndex, size);
        List<PptAO> pptAOList = new ArrayList<>();
        for (PptPO pptPO : list) {
            PptAO pptAO = ConvertMapping.pptPO2PptAO(pptPO);
            pptAO.setEnableScore(pptPO.getUserCode().equals(userCode) ? false : true);
            int hasScored = pptMapper.hasScored(userCode, pptPO.getPptCode());
            pptAO.setHaveScored(hasScored == 0 ? false : true);
            UserPO userPO = userService.getUserByCode(userCode);
            if (userPO != null) {
                pptAO.setUsername(userPO.getUsername());
                pptAO.setHeadshot(userPO.getHeadshot());
            }
            Integer isCollected = pptMapper.isCollected(userCode, pptPO.getPptCode());
            pptAO.setIsCollected((isCollected == null || isCollected == 0) ? false : true);
            pptAOList.add(pptAO);
        }
        log.info("PptServiceImpl pptMeList pptAOList:[{}]", pptAOList);
        int total = pptMapper.totalOfPptMeList(userCode);
        PptKindListResponseAO response = new PptKindListResponseAO(total, pptAOList);

        return JsonResult.success(response);
    }

    @Override
    public JsonResult pptDelete(String userCode, String pptCode) {
        log.info("PptServiceImpl pptDelete userCode:[{}], pptCode:[{}]", userCode, pptCode);
        pptMapper.pptDelete(pptCode);
        // 删除收藏过这份PPT的记录
        pptMapper.collectRecordDeleteByPptCode(pptCode);

        return JsonResult.success("删除成功");
    }

    @Override
    public JsonResult pptSocring(String userCode, PptScoreRequestAO request) {
        log.info("PptServiceImpl pptSocring userCode:[{}], request:[{}]", userCode, request);
        if (userCode == null || "".equals(userCode)) {
            log.info("PptServiceImpl pptCollectFolderList 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        int hasScored = pptMapper.hasScored(userCode, request.getPptCode());
        if (hasScored != 0) {
            log.info("PptServiceImpl pptCollectFolderList 不可重复或修改评分");
            return JsonResult.error(500, "不可重复或修改评分");
        }
        PptPO pptPO = pptMapper.pptInfoByPptCode(request.getPptCode());
        if (pptPO != null && userCode.equals(pptPO.getUserCode())) {
            log.info("PptServiceImpl pptCollectFolderList 不可给自己评分");
            return JsonResult.error(500, "不可给自己评分");
        }
        double totalScore = pptMapper.getTotalScore(request.getPptCode());
        log.info("PptServiceImpl pptSocring before totalScore:[{}]", totalScore);
        totalScore += request.getScore1() + request.getScore2() + request.getScore3();
        log.info("PptServiceImpl pptSocring after totalScore:[{}]", totalScore);
        double score = totalScore / 5;
        score = (score % (int)score < 0.5) ? (int)score : ((int)score + 0.5);
        log.info("PptServiceImpl pptSocring score:[{}]", score);
        pptMapper.updateScore(request.getPptCode(), score, totalScore);
        pptMapper.scoreRecord(userCode, request.getPptCode());

        return JsonResult.success("评分成功");
    }

    @Override
    public JsonResult pptSee(String pptCode) {
        log.info("PptServiceImpl pptSee pptCode:[{}]", pptCode);
        pptMapper.pptSeeAmountAdd(pptCode);

        return JsonResult.success();
    }

    @Override
    public JsonResult pptComment(CommentPO request) {
        log.info("PptServiceImpl pptComment request:[{}]", request);
        if (request.getUserCode() == null || "".equals(request.getUserCode())) {
            log.info("PptServiceImpl pptComment 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        int n = pptMapper.pptComment(request);
        String commentCode = "comment_" + request.getId();
        pptMapper.updateCommentCode(request.getId(), commentCode);
        pptMapper.pptCommentAmountAdd(request.getPptCode());
        CommentAO commentAO = new CommentAO();
        commentAO.setCommentCode(commentCode);
        commentAO.setPptCode(request.getPptCode());
        commentAO.setUserCode(request.getUserCode());
        UserPO userPO = userService.getUserByCode(request.getUserCode());
        if (userPO != null) {
            commentAO.setUsername(userPO.getUsername());
            commentAO.setHeadshot(userPO.getHeadshot());
        }
        commentAO.setContent(request.getContent());
        commentAO.setReplyAmount(0);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        String[] times = date.split(" ")[0].split("-");
        String createTime = times[1] + "-" + times[2];
        commentAO.setCreateTime(createTime);

        return JsonResult.success(commentAO);
    }

    @Override
    public JsonResult pptReply(ReplyPO request) {
        log.info("PptServiceImpl pptReply request:[{}]", request);
        if (request.getUserCode() == null || "".equals(request.getUserCode())) {
            log.info("PptServiceImpl pptReply 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        int n = pptMapper.pptReply(request);
        String replyCode = "reply_" + request.getId();
        pptMapper.updateReplyCode(request.getId(), replyCode);
        pptMapper.pptReplyAmountAdd(request.getCommentCode());
        pptMapper.pptCommentAmountAdd(request.getPptCode());

        ReplyAO replyAO = new ReplyAO();
        replyAO.setReplyCode(replyCode);
        replyAO.setCommentCode(request.getCommentCode());
        replyAO.setPptCode(request.getPptCode());
        replyAO.setUserCode(request.getUserCode());
        UserPO userPO = userService.getUserByCode(request.getUserCode());
        if (userPO != null) {
            replyAO.setUsername(userPO.getUsername());
            replyAO.setHeadshot(userPO.getHeadshot());
        }
        replyAO.setContent(request.getContent());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format(new Date());
        String[] times = date.split(" ")[0].split("-");
        String createTime = times[1] + "-" + times[2];
        replyAO.setCreateTime(createTime);

        return JsonResult.success(replyAO);
    }

    @Override
    public JsonResult pptCommentList(String userCode, String pptCode, int page, int size) {
        log.info("PptServiceImpl pptCommentList userCode:[{}], pptCode:[{}], page:[{}], size:[{}]", userCode, pptCode, page, size);
        if (userCode == null || "".equals(userCode)) {
            log.info("PptServiceImpl pptCommentList 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        int startIndex = (page - 1) * size;
        List<CommentPO> commentPOList = pptMapper.pptCommentList(pptCode, startIndex, size);
        int total = pptMapper.commentTotal(pptCode);
        boolean hasMore = (startIndex + size) < total ? true : false;
        List<CommentAO> list = new ArrayList<>();
        for(CommentPO commentPO : commentPOList) {
            UserPO userPO = userService.getUserByCode(commentPO.getUserCode());
            CommentAO commentAO = ConvertMapping.commentPO2CommentAO(commentPO);
            if (userPO != null) {
                commentAO.setUsername(userPO.getUsername());
                commentAO.setHeadshot(userPO.getHeadshot());
            }
            list.add(commentAO);
        }
        CommentResponseAO response = new CommentResponseAO(total, hasMore, list);

        return JsonResult.success(response);
    }

    @Override
    public JsonResult pptReplyList(String userCode, String commentCode, int page, int size) {
        log.info("PptServiceImpl pptReplyList userCode:[{}], pptCode:[{}], page:[{}], size:[{}]", userCode, commentCode, page, size);
        if (userCode == null || "".equals(userCode)) {
            log.info("PptServiceImpl pptReplyList 请先登录");
            return JsonResult.error(500, "请先登录");
        }
        int startIndex = (page - 1) * size;
        List<ReplyPO> relyList = pptMapper.relyList(commentCode, startIndex, size);
        int total = pptMapper.replyTotal(commentCode);
        boolean hasMore = (startIndex + size) < total ? true : false;
        List<ReplyAO> list = new ArrayList<>();
        for(ReplyPO replyPO : relyList) {
            UserPO userPO = userService.getUserByCode(replyPO.getUserCode());
            ReplyAO replyAO = ConvertMapping.replyPO2ReplyAO(replyPO);
            if (userPO != null) {
                replyAO.setUsername(userPO.getUsername());
                replyAO.setHeadshot(userPO.getHeadshot());
            }
            list.add(replyAO);
        }
        ReplyResponseAO response = new ReplyResponseAO(total, hasMore, list);

        return JsonResult.success(response);
    }

}
