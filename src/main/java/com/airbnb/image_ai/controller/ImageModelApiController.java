package com.airbnb.image_ai.controller;

import com.airbnb.image_ai.service.ImageModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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

    @PostMapping(value = "/classify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> classifyImage(@RequestPart("imageFile") Mono<FilePart> filePartMono) {
        return filePartMono
                .flatMap(filePart -> filePart.content()
                        .map(dataBuffer -> dataBuffer.asInputStream(true))
                        .next())
                .flatMap(imageModelService::classifyImage);
    }
}
