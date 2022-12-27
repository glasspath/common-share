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

import java.nio.charset.Charset;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class NSString extends NSObject {

	public static final NativeLong NS_STRING_CLASS = Foundation.INSTANCE.objc_getClass("NSString");
	public static final Pointer STRING_SELECTOR = Foundation.INSTANCE.sel_registerName("string");
	public static final Pointer INIT_WITH_BYTES_SELECTOR = Foundation.INSTANCE.sel_registerName("initWithBytes:length:encoding:");
	public static final Pointer INIT_WITH_CSTRING_SELECTOR = Foundation.INSTANCE.sel_registerName("initWithCString:encoding:");

	public static final Charset UTF_16LE_CHARSET = Charset.forName("UTF-16LE");
	public static final long NS_UTF16_LITTLE_ENDIAN_ENCODING = 0x94000100;

	public NSString(String string) {
		super(createNSString(string));
	}

	private static NativeLong createNSString(String s) {

		if (s != null && s.length() > 0) {

			byte[] bytes = s.getBytes(UTF_16LE_CHARSET);

			return Foundation.INSTANCE.objc_msgSend(Foundation.INSTANCE.objc_msgSend(NS_STRING_CLASS, ALLOC_SELECTOR), INIT_WITH_BYTES_SELECTOR, bytes, bytes.length, NS_UTF16_LITTLE_ENDIAN_ENCODING);

		} else {
			return Foundation.INSTANCE.objc_msgSend(NS_STRING_CLASS, STRING_SELECTOR);
		}

	}

}
