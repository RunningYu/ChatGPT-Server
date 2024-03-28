package chatgptserver.bean.dto.tongYiQianWen;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/28
 */
@Data
public class TongYiImageCreateResponseDTO {

    private String request_id;

    private ImageCreateResultOutput output;

    private ImageCreateUsage usage;

}
