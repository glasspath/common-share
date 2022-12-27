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

import org.glasspath.common.share.mail.Mailable;

import com.sun.jna.Platform;
import com.sun.jna.WString;

public class MapiShareUtils {

	private MapiShareUtils() {

	}

	public static void createEmail(Mailable mailable) {

		if (Platform.isWindows() && MapiLibrary.INSTANCE != null) {

			// TODO

			MapiMessageW message = new MapiMessageW();
			message.subject = new WString("Test subject");
			message.noteText = new WString("Dit is een test tekst");

			MapiRecipDescW[] receips = (MapiRecipDescW[]) new MapiRecipDescW().toArray(3);

			receips[0].ulRecipClass = 1;
			receips[0].lpszName = new WString("TODO-TO");
			receips[0].lpszAddress = new WString("SMTP:to@to.to");
			receips[0].write();

			receips[1].ulRecipClass = 2;
			receips[1].lpszName = new WString("TODO-CC");
			receips[1].lpszAddress = new WString("SMTP:cc@cc.cc");
			receips[1].write();

			receips[2].ulRecipClass = 3;
			receips[2].lpszName = new WString("TODO-BCC");
			receips[2].lpszAddress = new WString("SMTP:bcc@bcc.bcc");
			receips[2].write();

			message.receips = receips[0].getPointer();
			message.receipCount = 3;

			MapiFileDescW[] files = (MapiFileDescW[]) new MapiFileDescW().toArray(1);

			files[0].lpszPathName = new WString("C://temp//test.txt");
			files[0].lpszFileName = new WString("test.txt");
			files[0].write();

			message.files = files[0].getPointer();
			message.fileCount = 1;

			int result = MapiLibrary.INSTANCE.MAPISendMailW(null, null, message, 4 | 8, 0);
			// int result = MapiLibrary.INSTANCE.MAPISendMailW(null, hwnd, message, 4 | 8, 0);
			System.out.println("MAPISendMail result: " + result);

		}

	}

}
