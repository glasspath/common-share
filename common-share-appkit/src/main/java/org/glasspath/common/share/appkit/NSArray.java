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

public class NSArray extends NSObject {

	public static final NativeLong NS_ARRAY_CLASS = Foundation.INSTANCE.objc_getClass("NSArray");
	public static final Pointer ARRAY_WITH_OBJECT_SELECTOR = Foundation.INSTANCE.sel_registerName("arrayWithObject:");
	public static final Pointer ARRAY_BY_ADDING_OBJECT_SELECTOR = Foundation.INSTANCE.sel_registerName("arrayByAddingObject:");
	public static final Pointer COUNT_SELECTOR = Foundation.INSTANCE.sel_registerName("count");

	private NSArray(NativeLong id) {
		super(id);
	}

	public NSArray(NSObject object) {
		super(Foundation.INSTANCE.objc_msgSend(NS_ARRAY_CLASS, ARRAY_WITH_OBJECT_SELECTOR, object.getId()));
		// TODO: Remove (used for testing unhandled exceptions)
		// super(Foundation.INSTANCE.objc_msgSend(NS_ARRAY_CLASS, ARRAY_WITH_OBJECT_SELECTOR, Foundation.NULL));
	}

	// TODO: Find out how to create a new NSArray with multiple objects, arrayWithObjects: was not working correctly..
	// For now we just create a new array by adding a object

	public NSArray arrayByAddingObject(NSObject object) {
		return new NSArray(Foundation.INSTANCE.objc_msgSend(id, ARRAY_BY_ADDING_OBJECT_SELECTOR, object.getId()));
	}

	public int count() {
		return Foundation.INSTANCE.objc_msgSend(id, COUNT_SELECTOR).intValue();
	}

}
