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
package org.glasspath.common.share.outlook;

import java.util.ArrayList;
import java.util.List;

import org.glasspath.common.share.mail.Mailable;
import org.glasspath.common.share.outlook.Outlook.Application;
import org.glasspath.common.share.outlook.Outlook.MailItem;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Ole32;

public class OutlookShareUtils {

	private OutlookShareUtils() {

	}

	public static void createEmail(Mailable mailable) {

		Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);

		try {

			Outlook outlook = new Outlook();
			// System.out.println(outlook.getVersion());

			Application app = outlook.getApplication();
			if (app != null) {

				MailItem mailItem = app.createMailItem();
				if (mailItem != null) {

					// TODO

					mailItem.addToRecipient("remco_poelstra@hotmail.com");
					mailItem.addCcRecipient("na@na.na");
					mailItem.addBccRecipient("TODO@TODO.TODO");

					mailItem.setSubject("Dit is een test subject");

					mailItem.addAttachment("C:\\temp\\test.txt");
					mailItem.addAttachment("C:\\werkmap remco\\screenshots\\sparck-camera-station-001.png", "sparck-camera-station-001.png");

					mailItem.setBodyFormat(Outlook.OL_BODY_FORMAT_OL_FORMAT_HTML);
					mailItem.setBody("Dit is een test");
					mailItem.setHTMLBody("<html><body>Dit is een <b>test</b><br>met <i>meerdere</i><br>regels<br>en een image: <img src=\"cid:sparck-camera-station-001.png\"></body></html>");

					mailItem.Display();

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Ole32.INSTANCE.CoUninitialize();
		}

	}

	public static void createCommandLineEmail(Mailable mailable, String outlookExePath) {

		if (outlookExePath == null || outlookExePath.length() == 0) {
			// TODO: Try to find executable, for now use the one that worked while creating this class
			outlookExePath = "C:/Program Files/Microsoft Office/root/Office16/outlook.exe";
		}

		List<String> command = new ArrayList<>();
		command.add(outlookExePath);
		command.add("/c");
		command.add("ipm.note");

		String mArg;

		// TODO: Can we add multiple to recipients?
		if (mailable.getTo().size() > 0) {
			mArg = mailable.getTo().get(0);
		} else {
			mArg = "to@to.to"; // TODO
		}

		if (mailable.getSubject().length() > 0) {
			mArg += "?subject=" + mailable.getSubject();
		} else {
			mArg += "?subject=Subject";
		}

		// TODO: Can we add multiple CC recipients?
		if (mailable.getCc().size() > 0) {
			mArg += "&cc=" + mailable.getCc().get(0);
		}

		// TODO: Can we add multiple BCC recipients?
		if (mailable.getBcc().size() > 0) {
			mArg += "&bcc=" + mailable.getBcc().get(0);
		}

		// TODO: Can we set a html body?
		mArg += "&body=" + mailable.getText().replace("\n", "%0D%0A");

		command.add("/m");
		command.add(mArg);

		// TODO: Can we add multiple attachements?
		if (mailable.getAttachments().size() > 0) {
			command.add("/a");
			command.add(mailable.getAttachments().get(0));
		}

		try {

			new ProcessBuilder(command).inheritIO().start();

			// Process process = new ProcessBuilder(command).inheritIO().start();
			// process.waitFor();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
