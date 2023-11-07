package com.knocknock.domain.model.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AwsS3ImgLink {

    AWS_S3_BASIC_URL("https://a508knocknock.s3.ap-northeast-2.amazonaws.com/knocknock/");

    private String link;

    public static String getLink(String modelName) {
        StringBuilder sb = new StringBuilder(AWS_S3_BASIC_URL.getLink());
        sb.append(modelName).append(".png");
        return sb.toString();
    }

}
