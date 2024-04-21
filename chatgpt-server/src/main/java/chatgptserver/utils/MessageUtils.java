package chatgptserver.utils;


/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/21
 */
public class MessageUtils {
    public static String buildContent(String content) {
        if (content == null || "".equals(content)) {
            return "";
        }

        String[] contents = content.split("\n");
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < contents.length - 1; i ++) {
            if (!contents[i].equals("")) {
                sb.append(contents[i] + "\n");
            }
        }
        sb.append(contents[contents.length - 1]);

        return sb.toString();
    }
}
