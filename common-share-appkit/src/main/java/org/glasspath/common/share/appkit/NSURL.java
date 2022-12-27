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

public class NSURL extends NSObject {

	public static final NativeLong NS_URL_CLASS = Foundation.INSTANCE.objc_getClass("NSURL");
	public static final Pointer INIT_WITH_STRING_SELECTOR = Foundation.INSTANCE.sel_registerName("fileURLWithPath:");

	public NSURL(NativeLong urlString) {
		super(Foundation.INSTANCE.objc_msgSend(NS_URL_CLASS, INIT_WITH_STRING_SELECTOR, urlString));
	}

}
