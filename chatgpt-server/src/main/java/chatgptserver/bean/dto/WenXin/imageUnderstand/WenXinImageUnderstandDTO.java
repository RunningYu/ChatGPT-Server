package chatgptserver.bean.dto.WenXin.imageUnderstand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WenXinImageUnderstandDTO {

    /**
     * 问题
     */
    private String prompt;

    /**
     * Base64
     */
    private String image;
}
