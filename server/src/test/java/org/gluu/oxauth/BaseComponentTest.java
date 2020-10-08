/*
 * Janssen Project software is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Janssen Project
 */

package org.gluu.oxauth;

import io.jans.as.client.BaseTest;

/**
 * @author Yuriy Zabrovarnyy
 * @author Yuriy Movchan
 * @version 0.9, 15/10/2012
 */

public abstract class BaseComponentTest extends BaseTest {

	public static void sleepSeconds(int p_seconds) {
		try {
			Thread.sleep(p_seconds * 1000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}