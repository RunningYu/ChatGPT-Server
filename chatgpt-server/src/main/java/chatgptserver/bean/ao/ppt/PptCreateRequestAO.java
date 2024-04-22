package chatgptserver.bean.ao.ppt;

import chatgptserver.bean.dto.XunFeiXingHuo.pptCreate.PptOutlineResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/4/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PptCreateRequestAO {

    private PptOutlineResponse outline;

    private Boolean isRebuild;

    private String cid;

    private String sid;

    private String content;

    private String userCode;
}
