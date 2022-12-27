/*
 * This file is part of Glasspath Common.
 * Copyright (C) 2011 - 2022 Remco Poelstra
 * Authors: Remco Poelstra
 * 
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact us at https://glasspath.org. For AGPL licensing, see below.
 * 
 * AGPL licensing:
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.glasspath.common.share.appkit;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class NSObject {

	public static final NativeLong NS_OBJECT_CLASS = Foundation.INSTANCE.objc_getClass("NSObject");
	public static final Pointer ALLOC_SELECTOR = Foundation.INSTANCE.sel_registerName("alloc");
	public static final Pointer INIT_SELECTOR = Foundation.INSTANCE.sel_registerName("init");
	public static final Pointer PERFORM_ON_MAIN_THREAD_SELECTOR = Foundation.INSTANCE.sel_registerName("performSelectorOnMainThread:withObject:waitUntilDone:");
	public static final Pointer RELEASE_SELECTOR = Foundation.INSTANCE.sel_registerName("release");

	protected final NativeLong id;

	public NSObject(NativeLong id) {
		this.id = id;
	}

	public final NativeLong getId() {
		return id;
	}

	public NativeLong performSelectorOnMainThread(Pointer selector, NativeLong object, boolean waitUntilDone) {
		return Foundation.INSTANCE.objc_msgSend(id, NSObject.PERFORM_ON_MAIN_THREAD_SELECTOR, selector, object, waitUntilDone);
	}

	public void release() {
		Foundation.INSTANCE.objc_msgSend(id, RELEASE_SELECTOR);
	}

	/*
	@Override
	protected void finalize() throws Throwable {
		release();
		super.finalize();
	}
	*/

}
