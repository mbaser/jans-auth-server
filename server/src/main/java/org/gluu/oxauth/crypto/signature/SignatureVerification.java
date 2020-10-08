/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package org.gluu.oxauth.crypto.signature;

import io.jans.as.model.exception.SignatureException;

import java.security.PublicKey;
import java.security.cert.X509Certificate;

public interface SignatureVerification {

	boolean checkSignature(X509Certificate attestationCertificate, byte[] signedBytes, byte[] signature) throws SignatureException;

    boolean checkSignature(PublicKey publicKey, byte[] signedBytes, byte[] signature) throws SignatureException;

    PublicKey decodePublicKey(byte[] encodedPublicKey) throws SignatureException;

    byte[] hash(byte[] bytes);

    byte[] hash(String str);

}