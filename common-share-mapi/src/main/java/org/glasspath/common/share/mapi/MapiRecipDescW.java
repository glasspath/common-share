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
package org.glasspath.common.share.mapi;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;

public class MapiRecipDescW extends Structure {

	public static final int MAPI_ORIG = 0;
	public static final int MAPI_TO = 1;
	public static final int MAPI_CC = 2;
	public static final int MAPI_BCC = 3;

	public int ulReserved = 0;
	public int ulRecipClass = 0;
	public WString lpszName = null;
	public WString lpszAddress = null;
	public int ulEIDSize = 0;
	public Pointer lpEntryID = null;

	public MapiRecipDescW() {

	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("ulReserved", "ulRecipClass", "lpszName", "lpszAddress", "ulEIDSize", "lpEntryID");
	}

}
