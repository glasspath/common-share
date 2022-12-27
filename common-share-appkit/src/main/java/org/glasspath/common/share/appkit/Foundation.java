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

import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public interface Foundation extends Library {

	public static final Foundation INSTANCE = Native.load("Foundation", Foundation.class);

	public static final NativeLong NULL = new NativeLong(0L);

	Pointer sel_registerName(String name);

	NativeLong objc_getClass(String name);

	NativeLong objc_msgSend(NativeLong self, Pointer op);

	NativeLong objc_msgSend(NativeLong self, Pointer op, long arg1);

	NativeLong objc_msgSend(NativeLong self, Pointer op, Pointer arg1);

	NativeLong objc_msgSend(NativeLong self, Pointer op, NativeLong arg1);

	NativeLong objc_msgSend(NativeLong self, Pointer op, NativeLong arg1, NativeLong arg2);

	NativeLong objc_msgSend(NativeLong self, Pointer op, NativeLong arg1, NativeLong arg2, NativeLong arg3);

	NativeLong objc_msgSend(NativeLong self, Pointer op, Pointer arg1, NativeLong arg2, boolean arg3);

	NativeLong objc_msgSend(NativeLong self, Pointer op, byte[] arg1, int arg2, long arg3);

	public static boolean isNull(NSObject nsObject) {
		return NULL.equals(nsObject.id);
	}

	public static boolean addObject(NSObject object, List<NSObject> objects) {
		return addObject(object, objects, true);
	}

	public static boolean addObject(NSObject object, List<NSObject> objects, boolean releaseIfNull) {
		if (isNull(object)) {
			if (releaseIfNull) {
				releaseObjects(objects);
			}
			return false;
		} else {
			objects.add(object);
			return true;
		}
	}

	public static void releaseObjects(List<NSObject> objects) {
		for (int i = objects.size() - 1; i >= 0; i--) {
			objects.get(i).release();
		}
	}

}
