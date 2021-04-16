package com.auth0.android.lock.utils;

import com.auth0.android.request.DefaultClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.tls.HandshakeCertificates;
import okhttp3.tls.HeldCertificate;

/**
 * Utility object for executing tests that use the networking client over HTTPS on localhost.
 */
public class SSLTestUtils {
    private final HeldCertificate localhostCertificate;
    private final HandshakeCertificates serverCertificates;
    private final HandshakeCertificates clientCertificates;
    public final DefaultClient testClient;

    public SSLTestUtils() throws UnknownHostException {
        String localhost = InetAddress.getByName("localhost").getCanonicalHostName();

        localhostCertificate = new HeldCertificate.Builder()
                .addSubjectAlternativeName(localhost)
                .build();

        clientCertificates = new HandshakeCertificates.Builder()
                .addTrustedCertificate(localhostCertificate.certificate())
                .build();

        serverCertificates = new HandshakeCertificates.Builder()
                .heldCertificate(localhostCertificate)
                .build();

        testClient = new DefaultClient(
                10,
                10,
                Collections.emptyMap(),
                false,
                clientCertificates.sslSocketFactory(),
                clientCertificates.trustManager()
        );
    }

    MockWebServer createMockWebServer() {
        MockWebServer mockServer = new MockWebServer();
        mockServer.useHttps(serverCertificates.sslSocketFactory(), false);
        return mockServer;
    }
}