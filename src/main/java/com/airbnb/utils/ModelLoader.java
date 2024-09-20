package com.airbnb.utils;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

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
    public static final String DATASET_MODEL_AIRBNB_IMAGE_MODEL_ONNX = "model/airbnb_image_model.onnx";

    @Getter
    private static final OrtEnvironment env;

    @Getter
    private static OrtSession session;

    static {
        env = OrtEnvironment.getEnvironment();
        try (InputStream inputStream = new ClassPathResource(DATASET_MODEL_AIRBNB_IMAGE_MODEL_ONNX).getInputStream()) {
            byte[] modelArray = inputStream.readAllBytes();
            session = env.createSession(modelArray, new OrtSession.SessionOptions());
        } catch (OrtException | IOException e) {
            log.error("Failed to load model: {}", DATASET_MODEL_AIRBNB_IMAGE_MODEL_ONNX, e);
        }
    }
}
