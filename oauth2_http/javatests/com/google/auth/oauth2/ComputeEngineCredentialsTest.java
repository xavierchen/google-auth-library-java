package com.google.auth.oauth2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.api.client.http.HttpStatusCodes;
import com.google.auth.TestUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Test case for {@link ComputeEngineCredentials}.
 */
@RunWith(JUnit4.class)
public class ComputeEngineCredentialsTest {

  private static final URI CALL_URI = URI.create("http://googleapis.com/testapi/v1/foo");

  @Test
  public void getRequestMetadata_hasAccessToken() throws IOException {
    final String accessToken = "1/MkSJoj1xsli0AccessToken_NKPY2";
    MockMetadataServerTransport transport = new MockMetadataServerTransport();
    transport.setAccessToken(accessToken);
    ComputeEngineCredentials credentials = new ComputeEngineCredentials(transport);

    Map<String, List<String>> metadata = credentials.getRequestMetadata(CALL_URI);

    TestUtils.assertContainsBearerToken(metadata, accessToken);
  }

  @Test
  public void getRequestMetadata_missingServiceAccount_throws() {
    final String accessToken = "1/MkSJoj1xsli0AccessToken_NKPY2";
    MockMetadataServerTransport transport = new MockMetadataServerTransport();
    transport.setAccessToken(accessToken);
    transport.setTokenRequestStatusCode(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
    ComputeEngineCredentials credentials = new ComputeEngineCredentials(transport);

    try {
      credentials.getRequestMetadata(CALL_URI);
      fail("Expected error refreshing token.");
    } catch (IOException expected) {
      String message = expected.getMessage();
      assertTrue(message.contains(Integer.toString(HttpStatusCodes.STATUS_CODE_NOT_FOUND)));
      // Message should mention scopes are missing on the VM.
      assertTrue(message.contains("scope"));
    }
  }

  public void getRequestMetadata_serverError_throws() {
    final String accessToken = "1/MkSJoj1xsli0AccessToken_NKPY2";
    MockMetadataServerTransport transport = new MockMetadataServerTransport();
    transport.setAccessToken(accessToken);
    transport.setTokenRequestStatusCode(HttpStatusCodes.STATUS_CODE_SERVER_ERROR);
    ComputeEngineCredentials credentials = new ComputeEngineCredentials(transport);

    try {
      credentials.getRequestMetadata(CALL_URI);
      fail("Expected error refreshing token.");
    } catch (IOException expected) {
      String message = expected.getMessage();
      assertTrue(message.contains(Integer.toString(HttpStatusCodes.STATUS_CODE_SERVER_ERROR)));
      assertTrue(message.contains("Unexpected"));
    }
  }
}
