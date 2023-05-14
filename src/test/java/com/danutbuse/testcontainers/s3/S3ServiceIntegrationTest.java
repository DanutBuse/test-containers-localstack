package com.danutbuse.testcontainers.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.util.IOUtils;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Import(S3Repository.class)
class S3ServiceIntegrationTest extends S3BaseTest {

	private static final String BUCKET_NAME = "people";
	private static final String S3_FILE_NAME = "PersonalInformation";

	@Autowired
	S3Repository s3Repository;

	@Value("classpath:s3/PeoplePersonalInformation.json")
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
		var putObjectResult = s3Repository.uploadFile(BUCKET_NAME, S3_FILE_NAME, s3SampleFile);
		assertThat(putObjectResult.getETag()).isNotNull();

		// Download File
		var s3Object = s3Repository.downloadFile(BUCKET_NAME, S3_FILE_NAME);
		assertThat(IOUtils.toString(s3Object.getObjectContent())).isEqualTo(
				FileUtils.readFileToString(s3SampleFile, StandardCharsets.UTF_8)
		);

		// Delete File
		s3Repository.deleteFile(BUCKET_NAME, S3_FILE_NAME);
		assertThatThrownBy(() -> s3Repository.downloadFile(BUCKET_NAME, S3_FILE_NAME))
				.isInstanceOf(AmazonS3Exception.class);
	}

}
