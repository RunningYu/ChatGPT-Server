package chatgptserver.bean.dto.XunFeiXingHuo.pptCreate;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/13
 */
@Data
public class PptCoverResponseDTO {

    private Boolean flag;

    private Integer code;

    private String desc;

    private String count;

    private PptCover data;
}
