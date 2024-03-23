package chatgptserver.bean.ao;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/23
 */
@Data
public class ImageCreateRequestAO {

    private String content;

    private String userCode;

    private String chatCode;

    private String userName;

    private String chatName;

}
