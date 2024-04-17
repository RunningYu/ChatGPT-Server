package chatgptserver.bean.dto.tongYiQianWen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 其然乐衣Letitbe
 * @date : 2024/3/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TongYiImageCreateRequestDTO {

    private String model;

    private ImageCreateInput input;

    private ImageCreateParameters parameters;

    public static TongYiImageCreateRequestDTO buildTongYiImageCreateRequestDTO(String content) {
        TongYiImageCreateRequestDTO requestDTO = new TongYiImageCreateRequestDTO();
//        requestDTO.setModel("stable-diffusion-xl");
        // wanx-v1 模型是有免费额度的
        requestDTO.setModel("wanx-v1");
        ImageCreateInput input = new ImageCreateInput();
        input.setPrompt(content);
        requestDTO.setInput(input);
        ImageCreateParameters parameters = new ImageCreateParameters();
        parameters.setN(1);
        parameters.setSeed(42);
        parameters.setSize("1024*1024");
        requestDTO.setParameters(parameters);

        return requestDTO;
    }
}
