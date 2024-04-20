package chatgptserver.utils;
import chatgptserver.bean.ao.JsonResult;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/20
 */
public class StorageUtils {

    public static ConcurrentHashMap<String, JsonResult> loginMap = new ConcurrentHashMap<>();

}
