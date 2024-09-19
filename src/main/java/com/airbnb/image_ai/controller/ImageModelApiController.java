package com.airbnb.image_ai.controller;

import com.airbnb.image_ai.service.ImageModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.InputStream;

/**
 * packageName    : com.airbnb.image_ai.controller
 * fileName       : ImageModelApiController
 * author         : ipeac
 * date           : 24. 9. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 16.        ipeac       최초 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
@Slf4j
public class ImageModelApiController {
    private final ImageModelService imageModelService;

    @PostMapping(value = "/classify")
    public Mono<String> classifyImage(@RequestPart("imageFile") Mono<FilePart> filePartMono) {
        return filePartMono
                .flatMap(filePart -> DataBufferUtils.join(filePart.content())
                        .map(dataBuffer -> {
                            InputStream is = dataBuffer.asInputStream(true);
                            DataBufferUtils.release(dataBuffer); // dataBuffer 메모리 누수 방지
                            return is;
                        }))
                .flatMap(imageModelService::classifyImage)
                .onErrorResume(e -> {
                    log.error("Image classification failed", e);
                    return Mono.just("Image classification failed");
                });
    }
}
