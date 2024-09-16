package com.airbnb.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.IOException;

/**
 * packageName    : com.airbnb.utils
 * fileName       : ModelLoader
 * author         : ipeac
 * date           : 24. 9. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 17.        ipeac       최초 생성
 */
@Getter
@Slf4j
public class ModelLoader {
    @Getter
    private static MultiLayerNetwork model;

    static {
        try {
            String modelPath = "/dataset/model/airbnb_image_model.onnx";
            model = ModelSerializer.restoreMultiLayerNetwork(modelPath);
        } catch (IOException e) {
            log.error("Failed to load model: {}", e.getMessage());
        }
    }
}
