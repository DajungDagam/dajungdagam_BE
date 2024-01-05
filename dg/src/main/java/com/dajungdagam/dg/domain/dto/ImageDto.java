package com.dajungdagam.dg.domain.dto;

import com.dajungdagam.dg.domain.entity.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ImageDto {

    private String imagePath;
    private String imageName;
    private String uuid;
    private String imageType;
    private Long imageSize;
    private TradePost tradePost;



    public Image toEntity() {
        return Image.builder()
                .imagePath(imagePath)
                .imageName(imageName)
                .uuid(uuid)
                .imageType(imageType)
                .imageSize(imageSize)
                .tradePost(tradePost)
                .build();
    }

    @Builder
    public ImageDto(String imagePath, String imageName, String uuid,
                    String imageType, Long imageSize, TradePost tradePost)
    {
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.uuid = uuid;
        this.imageType = imageType;
        this.imageSize = imageSize;
        this.tradePost = tradePost;
    }


}
