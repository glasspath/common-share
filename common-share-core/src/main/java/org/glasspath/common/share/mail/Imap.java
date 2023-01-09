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
package org.glasspath.common.share.mail;

@SuppressWarnings("nls")
public class Imap {

	public static enum Protocol {

		IMAP("IMAP", "imap"),
		IMAPS("IMAPS", "imaps");

		public final String name;
		public final String key;

		private Protocol(String name, String key) {
			this.name = name;
			this.key = key;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	public static final String[] COMMON_URLS = {
			"imap.",
			"mail.",
			"imap.mail.",
			"mail.imap."
	};

	public static final int[] COMMON_PORTS = {
			993,
			143
	};

	public static final String[] COMMON_SENT_FOLDER_NAMES = {
			"Sent",
			"Sent Items",
			"Sent Messages"
	};

}
