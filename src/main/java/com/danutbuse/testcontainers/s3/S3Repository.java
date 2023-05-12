package com.danutbuse.testcontainers.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import org.springframework.stereotype.Component;

import java.io.File;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class S3Repository {

  private final AmazonS3 s3Client;

  public PutObjectResult uploadFile(String bucket, String key, File file) {
    return s3Client.putObject(
        new PutObjectRequest(
            bucket, key, file
        )
    );
  }

  public S3Object downloadFile(String bucket, String key) {
    return s3Client.getObject(bucket, key);
  }

  public void deleteFile(String bucket, String key) {
    s3Client.deleteObject(bucket, key);
  }
}
