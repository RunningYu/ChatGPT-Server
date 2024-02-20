package chatgptserver.bean.ao;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/2/7
 */

@ApiModel(description = "UploadResponse")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {

    private String minIoUrl;

    private String nginxUrl;
}
