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

    public static final String WEN_XIN_API_KEY = "yyNK54os4e9jF3fadlT47mFV";

    public static final String WEN_XIN_SECRET_KEY = "Fb5T1AF8YiWrLEpQugmlHjew8zNuclVt";

    public static final String WEN_XIN_API_KEY_NAME = "wenXinApiKey";

    public static final String WEN_XIN_SECRET_KEY_NAME = "wenXinSecretKey";


    /**
     * ERNIE-Bot-8K
     */
    public static final String WEN_XIN_ASK_URL = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie_bot_8k?access_token=%s";

    static {
        GPT_KEY_MAP.put(WEN_XIN_API_KEY_NAME, WEN_XIN_API_KEY);
        GPT_KEY_MAP.put(WEN_XIN_SECRET_KEY_NAME, WEN_XIN_SECRET_KEY);
    }

//    ----------------------------------------讯飞星火----------------------------------------------------

    /**
     * 图片理解
     */
    public static final String XF_XH_PICTURE_UNDERSTAND_URL = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/image";
    public static final String XF_XH_APPID_KEY = "APPID";
    public static final String XF_XH_API_SECRET_KEY = "APISecret";
    public static final String XF_XH_API_KEY = "APIKey";
    static {
        GPT_KEY_MAP.put(XF_XH_APPID_KEY, "f6b93318");
        GPT_KEY_MAP.put(XF_XH_API_SECRET_KEY, "NjU2ZTc5ZmNkZWI5NmQwYWI1MDJiMzg4");
        GPT_KEY_MAP.put(XF_XH_API_KEY, "df704f2ff951c364c9bac34536811256");
    }

    /**
     * 图片生成
     */
    public static final String XF_XH_PICTURE_CREATE_URL = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/tti";


}
