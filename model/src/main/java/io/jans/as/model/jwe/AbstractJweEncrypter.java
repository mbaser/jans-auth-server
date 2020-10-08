/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package io.jans.as.model.jwe;

import io.jans.as.model.crypto.encryption.BlockEncryptionAlgorithm;
import io.jans.as.model.crypto.encryption.KeyEncryptionAlgorithm;

/**
 * @author Javier Rojas Blum
 * @version November 20, 2018
 */
public abstract class AbstractJweEncrypter implements JweEncrypter {

    private KeyEncryptionAlgorithm keyEncryptionAlgorithm;
    private BlockEncryptionAlgorithm blockEncryptionAlgorithm;

    protected AbstractJweEncrypter(KeyEncryptionAlgorithm keyEncryptionAlgorithm, BlockEncryptionAlgorithm blockEncryptionAlgorithm) {
        this.keyEncryptionAlgorithm = keyEncryptionAlgorithm;
        this.blockEncryptionAlgorithm = blockEncryptionAlgorithm;
    }

    public KeyEncryptionAlgorithm getKeyEncryptionAlgorithm() {
        return keyEncryptionAlgorithm;
    }

    public BlockEncryptionAlgorithm getBlockEncryptionAlgorithm() {
        return blockEncryptionAlgorithm;
    }
}