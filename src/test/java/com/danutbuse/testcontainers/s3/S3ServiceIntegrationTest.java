package com.danutbuse.testcontainers.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.util.IOUtils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.IOException;

@Import(S3Repository.class)
class S3ServiceIntegrationTest extends S3BaseTest {

	private static final String BUCKET_NAME = "testbucket";
	private static final String S3_BASE_DIR = "base";

	@Autowired
	S3Repository s3Repository;

	@Value("classpath:s3/S3SampleFile.txt")
	File s3SampleFile;

	@BeforeAll
	static void createBucket() throws IOException, InterruptedException {
		localStack.execInContainer(
				"awslocal", "s3api", "create-bucket", "--bucket", BUCKET_NAME, "--region", localStack.getRegion()
		);
	}

	@Test
	void shouldTestS3Integration() throws IOException {
		// Upload File
		var putObjectResult = s3Repository.uploadFile(BUCKET_NAME, S3_BASE_DIR, s3SampleFile);
		assertThat(putObjectResult.getETag()).isNotNull();

		// Download File
		var s3Object = s3Repository.downloadFile(BUCKET_NAME, S3_BASE_DIR);
		assertThat(IOUtils.toString(s3Object.getObjectContent())).isEqualTo("TEST");

		// Delete File
		s3Repository.deleteFile(BUCKET_NAME, S3_BASE_DIR);
		assertThatThrownBy(() -> s3Repository.downloadFile(BUCKET_NAME, S3_BASE_DIR))
				.isInstanceOf(AmazonS3Exception.class);
	}

}
