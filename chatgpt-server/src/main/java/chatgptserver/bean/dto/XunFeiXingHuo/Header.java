package chatgptserver.bean.dto.XunFeiXingHuo;

/**
 * @Author：chenzhenyu
 * @Date：2024/1/12 9:27
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copyright 2024 json.cn
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Header {

    @ApiModelProperty(value = "应用的app_id，需要在飞云交互平台申请，\"maxLength\":8", required = true)
    private String app_id;

    @ApiModelProperty(value = "每个用户的id，非必传字段，用于后续扩展 ，\"maxLength\":32", required = false)
    private String uid;

    public Header (String app_id) {
        this.app_id = app_id;
    }

}
