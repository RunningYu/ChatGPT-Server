package chatgptserver.utils;
import chatgptserver.bean.ao.JsonResult;
import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/20
 */
public class StorageUtils {

    /**
     * 存储【监听扫码登录状态】的标识
     */
    public static ConcurrentHashMap<String, JsonResult> loginMap = new ConcurrentHashMap<>();

    /**
     * 存储【监听停止请求】的标识
     */
    public static ConcurrentHashMap<String, String> stopRequestMap = new ConcurrentHashMap<>();

}
