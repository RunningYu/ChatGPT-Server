package chatgptserver.bean.dto.WenXin.imageCreate;

import chatgptserver.bean.dto.WenXin.Usage;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/26
 */

@Data
public class WenXinImageResponse {

    private String id;

    private String object;

    private Integer created;

    private List<ImageCreateData> data;

    private Usage usage;
}
