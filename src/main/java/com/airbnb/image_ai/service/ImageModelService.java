package com.airbnb.image_ai.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtSession;
import com.airbnb.utils.ModelLoader;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Collections;

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
public class ImageModelService {
    public static final String OUTPUT_KEY = "output0";
    public static final String INPUT_KEY = "images";

    private final OrtSession session;
    private final NativeImageLoader imageLoader;
    private final ImagePreProcessingScaler scaler;

    public ImageModelService() {
        this.session = ModelLoader.getSession();
        this.imageLoader = new NativeImageLoader(640, 640, 3); // 모델에 맞게 조정
        this.scaler = new ImagePreProcessingScaler(0, 1);
    }

    public Mono<String> classifyImage(InputStream imageStream) {
        return Mono.fromCallable(() -> {
            // 이미지 로드
            INDArray image = imageLoader.asMatrix(imageStream);

            // 이미지 전처리
            scaler.transform(image);

            // 배치 차원 추가 및 형상 조정
//            image = image.permute(0, 2, 3, 1); // (NCHW) -> (NHWC)로 변경 필요 시
            float[] imageData = image.data().asFloat();
            long[] shape = new long[]{1, 3, 640, 640}; // 배치 크기, 채널  수, 높이, 너비

            // 입력 텐서 생성
            OnnxTensor inputTensor = OnnxTensor.createTensor(ModelLoader.getEnv(), FloatBuffer.wrap(imageData), shape);

            // 모델 실행
            OrtSession.Result result = session.run(Collections.singletonMap(INPUT_KEY, inputTensor));

            // 결과 가져오기
            float[][] output = (float[][]) result.get(OUTPUT_KEY).get().getValue();

            // 가장 높은 확률의 클래스 인덱스 추출
            int predictedClass = getMaxIndex(output[0]);

            // 클래스 이름 매핑 (실제 클래스 이름으로 변경 필요)
            String[] classNames = {"예술", "음식", "인테리어", "자연", "야경", "인물"};

            return classNames[predictedClass];
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private int getMaxIndex(float[] probabilities) {
        int maxIndex = 0;

        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > probabilities[maxIndex]) {
                maxIndex = i;
            }
        }

        return maxIndex;
    }
}
