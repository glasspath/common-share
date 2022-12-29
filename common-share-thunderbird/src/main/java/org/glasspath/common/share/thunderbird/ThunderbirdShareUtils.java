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
package org.glasspath.common.share.thunderbird;

import java.util.ArrayList;
import java.util.List;

import org.glasspath.common.share.ShareException;
import org.glasspath.common.share.ShareUtils;
import org.glasspath.common.share.mail.MailUtils;
import org.glasspath.common.share.mail.Mailable;

public class ThunderbirdShareUtils {

	private ThunderbirdShareUtils() {

	}

	public static String getExecutablePath() {

		// TODO: This is a bit of a hack..

		List<String> executablePaths = new ArrayList<>();

		// Windows (let's also check the D: partition)
		executablePaths.add("C:/Program Files/Mozilla Thunderbird/thunderbird.exe");
		executablePaths.add("C:/Program Files (x86)/Mozilla Thunderbird/thunderbird.exe");
		executablePaths.add("D:/Program Files/Mozilla Thunderbird/thunderbird.exe");
		executablePaths.add("D:/Program Files (x86)/Mozilla Thunderbird/thunderbird.exe");

		// TODO: How to handle MacOS and Linux?

		String executablePath = ShareUtils.getFirstExistingFile(executablePaths, false);

		if (executablePath == null) {
			executablePath = "thunderbird";
		}

		return executablePath;

	}

	public static void createCommandLineEmail(Mailable mailable) throws ShareException {
		createCommandLineEmail(getExecutablePath(), mailable);
	}

	public static void createCommandLineEmail(String executablePath, Mailable mailable) throws ShareException {

		if (executablePath != null && executablePath.length() > 0) {

			List<String> elements = new ArrayList<>();

			if (mailable.getTo() != null && mailable.getTo().size() > 0) {
				elements.add("to='" + MailUtils.createElementsString(mailable.getTo(), ",") + "'");
			}

			if (mailable.getCc() != null && mailable.getCc().size() > 0) {
				elements.add("cc='" + MailUtils.createElementsString(mailable.getCc(), ",") + "'");
			}

			if (mailable.getBcc() != null && mailable.getBcc().size() > 0) {
				elements.add("bcc='" + MailUtils.createElementsString(mailable.getBcc(), ",") + "'");
			}

			String subject = "";
			if (mailable.getSubject() != null) {
				subject = mailable.getSubject();
			}
			elements.add("subject='" + subject + "'");

			String body = "";
			if (mailable.getHtml() != null && mailable.getHtml().length() > 0) {
				body = mailable.getHtml();
			} else if (mailable.getText() != null) {
				body = mailable.getText();
			}
			elements.add("body='" + body + "'");

			if (mailable.getAttachments() != null && mailable.getAttachments().size() > 0) {
				elements.add("attachment='" + MailUtils.createElementsString(mailable.getAttachments(), "file:///", "", ",") + "'");
			}

			List<String> command = new ArrayList<>();
			command.add(executablePath);
			command.add("-compose");
			// command.add("to='john@example.com,kathy@example.com',cc='britney@example.com',subject='dinner',body='<html><body>How about<br><br>dinner tonight?</body></html>',attachment='file:///C:/project/test1.txt,file:///C:/project/test2.txt'");
			// command.add("to='john@example.com,kathy@example.com',cc='britney@example.com,test@test.nl',bcc='britney2@example.com,test2@test2.nl',subject='dinner',body='How about\n\ndinner tonight?',attachment='file:///C:/project/test1.txt,file:///C:/project/test2.txt'");
			command.add(MailUtils.createElementsString(elements, ","));

			try {

				new ProcessBuilder(command).inheritIO().start();

				// Process process = new ProcessBuilder(command).inheritIO().start();
				// process.waitFor();

			} catch (Exception e) {
				throw new ShareException("Could not create Thunderbird (command line) email", e);
			}

		} else {
			throw new ShareException("Could not create Thunderbird (command line) email because executablePath is not valid");
		}

	}

}
