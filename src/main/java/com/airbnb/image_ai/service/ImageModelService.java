package com.airbnb.image_ai.service;

import com.airbnb.utils.ModelLoader;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;

/**
 * packageName    : com.airbnb.image_ai.service
 * fileName       : ImageModelService
 * author         : ipeac
 * date           : 24. 9. 17.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 17.        ipeac       최초 생성
 */
@Service("imageModelService")
@Validated
public class ImageModelService {
    private final MultiLayerNetwork model;
    private final NativeImageLoader imageLoader;
    private final DataNormalization scaler;

    public ImageModelService() {
        this.model = ModelLoader.getModel();
        this.imageLoader = new NativeImageLoader(28, 28, 1);
        this.scaler = new ImagePreProcessingScaler(0, 1);
    }

    public Mono<String> classifyImage(InputStream imageStream) {
        return Mono.fromCallable(() -> {
            // 이미지 로드
            INDArray image = imageLoader.asMatrix(imageStream);
            // 이미지 전처리 (정규화)
            scaler.transform(image);
            // 모델 예측
            INDArray output = model.output(image);
            // 가장 높은 확률의 클래스 인덱스 추출
            int predictedClass = output.argMax(1).getInt(0);
            // 클래스 이름 매핑 (실제 클래스 이름으로 변경 필요)
            String[] classNames = {"Class A", "Class B", "Class C"};
            return classNames[predictedClass];
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
