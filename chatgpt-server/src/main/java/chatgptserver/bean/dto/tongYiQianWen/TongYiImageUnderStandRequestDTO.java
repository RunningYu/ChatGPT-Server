package chatgptserver.bean.dto.tongYiQianWen;

import lombok.Data;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/27
 */
@Data
public class TongYiImageUnderStandRequestDTO {

    private String model;

    private Input input;

    public TongYiImageUnderStandRequestDTO(String model) {
        this.model = model;
    }
}
