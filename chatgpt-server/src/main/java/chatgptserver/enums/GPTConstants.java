package chatgptserver.enums;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/4 9:49
 */
public class GPTConstants {
    public static Map<String, String> GPT_KEY_MAP = new HashMap<>();

//    -----------------------------------------文心一言---------------------------------------------------

    /**
     * 文本问答
     */
    public static final String WEN_XIN_GET_ACCESS_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=%s&client_secret=%s";

//    public static final String WEN_XIN_APP_ID = "46343402";
//
//    public static final String WEN_XIN_API_KEY = "yyNK54os4e9jF3fadlT47mFV";
//
//    public static final String WEN_XIN_SECRET_KEY = "Fb5T1AF8YiWrLEpQugmlHjew8zNuclVt";

    public static final String WEN_XIN_APP_ID = "58299734";

    public static final String WEN_XIN_API_KEY = "aHZkdH6mSkMz0q24rDSf655D";

    public static final String WEN_XIN_SECRET_KEY = "cVkrqCb5Z0KG5k9aqwsOgR9FX3wwICLs";

    public static final String WEN_XIN_API_KEY_NAME = "wenXinApiKey";

    public static final String WEN_XIN_SECRET_KEY_NAME = "wenXinSecretKey";


    /**
     * 【文本问答】
     * ERNIE-Bot-8K (有限免费，现在代充费状态)
     */
//    public static final String WEN_XIN_ASK_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie_bot_8k?access_token=%s";
    /**
     * 【文本问答】
     * Yi-34B-Chat (免费使用)
     */
    public static final String WEN_XIN_ASK_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/yi_34b_chat?access_token=%s";

    public static final String WEN_XIN_IMAGE_CREATE_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/text2image/sd_xl?access_token=%S";

    static {
        GPT_KEY_MAP.put(WEN_XIN_API_KEY_NAME, WEN_XIN_API_KEY);
        GPT_KEY_MAP.put(WEN_XIN_SECRET_KEY_NAME, WEN_XIN_SECRET_KEY);
    }

//    ----------------------------------------讯飞星火----------------------------------------------------

    public static final String XF_XH_APPID_KEY = "APPID";
    public static final String XF_XH_API_SECRET_KEY = "APISecret";
    public static final String XF_XH_API_KEY = "APIKey";
    static {
        GPT_KEY_MAP.put(XF_XH_APPID_KEY, "f6b93318");
        GPT_KEY_MAP.put(XF_XH_API_SECRET_KEY, "NjU2ZTc5ZmNkZWI5NmQwYWI1MDJiMzg4");
        GPT_KEY_MAP.put(XF_XH_API_KEY, "df704f2ff951c364c9bac34536811256");
    }

    /**
     * 图片理解
     */
    public static final String XF_XH_PICTURE_UNDERSTAND_URL = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/image";

    /**
     * 图片生成
     */
    public static final String XF_XH_PICTURE_CREATE_URL = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/tti";

    /**
     * 文本问答
     * 地址与鉴权信息  https://spark-api.xf-yun.com/v1.1/chat   1.5地址  domain参数为 general
     * 地址与鉴权信息  https://spark-api.xf-yun.com/v2.1/chat   2.0地址  domain参数为 generalv2
     * 地址与鉴权信息  https://spark-api.xf-yun.com/v3.5/chat   3.5地址  domain参数为 generalv3.5
     */
    public static final String XF_XH_QUESTION_URL = "https://spark-api.xf-yun.com/v3.5/chat";
    public static final String XF_XH_QUESTION_DOMAIN = "generalv3.5";

    /**
     * 讯飞星火：PPT生成
     */
    public static final String XF_XH_PPT_CREATE_URL = "https://zwapi.xfyun.cn";

//--------------------------------------通义千问-------------------------------------------------------

    public static final String TONG_YI_QIAN_WEN_API_KEY = "sk-353194803150480a97f010b150761f3a";

    /**
     * 通义千问.通义千问VL
     */
    public static final String TONG_YI_QIAN_WEN_IMAGE_UNDERSTAND_URL = " https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-generation/generation";

    /**
     * 通义千问开源系列.大语言模型
     */
    public static final String TONG_YI_QIAN_WEN_QUESTION_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    /**
     * StableDiffusion文生图模型
     * 作业提交接口调用
     */
    public static final String TONG_YI_QIAN_WEN_IMAGE_CREATE_POST_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis";
    /**
     * 文生图
     * 作业任务状态查询和结果获取接口
     */
    public static final String TONG_YI_QIAN_WEN_IMAGE_CREATE_GET_URL = " https://dashscope.aliyuncs.com/api/v1/tasks/%s";


}
