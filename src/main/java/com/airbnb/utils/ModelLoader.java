package com.airbnb.utils;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
@Component
@Slf4j
public class ModelLoader {
    public static final String DATASET_MODEL_AIRBNB_IMAGE_MODEL_ONNX = "./dataset/model/airbnb_image_model.onnx";

    @Getter
    private static final OrtEnvironment env;

    @Getter
    private static OrtSession session;

    static {
        env = OrtEnvironment.getEnvironment();
        try {
            session = env.createSession(DATASET_MODEL_AIRBNB_IMAGE_MODEL_ONNX, new OrtSession.SessionOptions());
        } catch (OrtException e) {
            log.error("Failed to load model: {}", DATASET_MODEL_AIRBNB_IMAGE_MODEL_ONNX, e);
        }
    }
}
