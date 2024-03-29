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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.glasspath.common.share.ShareException;
import org.glasspath.common.share.ShareUtils;
import org.glasspath.common.share.mail.MailUtils;
import org.glasspath.common.share.mail.Mailable;
import org.glasspath.common.share.outlook.Outlook.Application;
import org.glasspath.common.share.outlook.Outlook.MailItem;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Ole32;

public class OutlookShareUtils {

	private OutlookShareUtils() {

	}

	public static void createOutlookClassicEmail(Mailable mailable) throws ShareException {

		boolean inited = false;

		try {

			Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED);

			inited = true;

			Outlook outlook = new Outlook();
			// System.out.println(outlook.getVersion());

			Application app = outlook.getApplication();
			if (app != null) {

				MailItem mailItem = app.createMailItem();
				if (mailItem != null) {

					if (mailable.getTo() != null) {
						for (String to : mailable.getTo()) {
							mailItem.addToRecipient(to);
						}
					}

					if (mailable.getCc() != null) {
						for (String cc : mailable.getCc()) {
							mailItem.addCcRecipient(cc);
						}
					}

					if (mailable.getBcc() != null) {
						for (String bcc : mailable.getBcc()) {
							mailItem.addBccRecipient(bcc);
						}
					}

					if (mailable.getSubject() != null) {
						mailItem.setSubject(mailable.getSubject());
					} else {
						mailItem.setSubject("");
					}

					if (mailable.getHtml() != null && mailable.getHtml().length() > 0) {
						mailItem.setBodyFormat(Outlook.OL_BODY_FORMAT_OL_FORMAT_HTML);
						mailItem.setHTMLBody(mailable.getHtml());
					} else {
						mailItem.setBodyFormat(Outlook.OL_BODY_FORMAT_OL_FORMAT_PLAIN);
					}

					if (mailable.getText() != null) {
						mailItem.setBody(mailable.getText());
					}

					if (mailable.getImages() != null) {
						for (Entry<String, String> entry : mailable.getImages().entrySet()) {
							if (entry.getValue() != null) {
								mailItem.addAttachment(entry.getValue(), entry.getKey());
							}
						}
					}

					if (mailable.getAttachments() != null) {
						for (String attachment : mailable.getAttachments()) {
							mailItem.addAttachment(attachment);
						}
					}

					mailItem.Display();

				} else {
					throw new ShareException("Could not create Outlook Classic (COM) email because mailItem is null");
				}

			} else {
				throw new ShareException("Could not create Outlook Classic (COM) email because app is null");
			}

		} catch (Exception e) {
			throw new ShareException("Could not create Outlook Classic (COM) email", e);
		} finally {
			if (inited) {
				Ole32.INSTANCE.CoUninitialize();
			}
		}

	}

	public static String getOutlookClassicExecutablePath() {

		// TODO: This is a bit of a hack..

		String executablePath = null;

		List<String> rootDirPaths = new ArrayList<>();

		// Path used during development: "C:/Program Files/Microsoft Office/root/Office16/outlook.exe"
		// Let's also check the D: partition
		rootDirPaths.add("C:/Program Files/Microsoft Office/root");
		rootDirPaths.add("C:/Program Files (x86)/Microsoft Office/root");
		rootDirPaths.add("D:/Program Files/Microsoft Office/root");
		rootDirPaths.add("D:/Program Files (x86)/Microsoft Office/root");

		String rootDirPath = ShareUtils.getFirstExistingFile(rootDirPaths, true);
		if (rootDirPath != null) {

			List<File> officeDirs = ShareUtils.findDirectories(new File(rootDirPath), "office");
			if (officeDirs != null) {

				for (File officeDir : officeDirs) {

					try {

						File outlookFile = new File(officeDir, "outlook.exe");
						if (outlookFile.exists() && !outlookFile.isDirectory()) {
							executablePath = outlookFile.getAbsolutePath();
							break;
						}

					} catch (Exception e) {
						// TODO?
					}

				}

			}

		}

		if (executablePath == null) {
			executablePath = "outlook";
		}

		return executablePath;

	}

	public static void createOutlookClassicCommandLineEmail(Mailable mailable) throws ShareException {
		createOutlookClassicCommandLineEmail(getOutlookClassicExecutablePath(), mailable);
	}

	public static void createOutlookClassicCommandLineEmail(String executablePath, Mailable mailable) throws ShareException {

		if (executablePath != null && executablePath.length() > 0) {

			String arg = MailUtils.createElementsString(mailable.getTo(), ";");

			String p = "?";

			if (mailable.getSubject() != null && mailable.getSubject().length() > 0) {
				arg += p + "subject=" + mailable.getSubject();
				p = "&";
			}

			if (mailable.getCc() != null && mailable.getCc().size() > 0) {
				arg += p + "cc=" + MailUtils.createElementsString(mailable.getCc(), ";");
				p = "&";
			}

			if (mailable.getBcc() != null && mailable.getBcc().size() > 0) {
				arg += p + "bcc=" + MailUtils.createElementsString(mailable.getBcc(), ";");
				p = "&";
			}

			// TODO: Can we set a html body?
			if (mailable.getText() != null && mailable.getText().length() > 0) {
				arg += p + "body=" + mailable.getText().replace("\n", "%0D%0A");
			}

			List<String> command = new ArrayList<>();
			command.add(executablePath);
			command.add("/c");
			command.add("ipm.note");
			command.add("/m");
			command.add(arg);

			// TODO: Can we add multiple attachments?
			String attachment = ShareUtils.getFirstExistingFile(mailable.getAttachments(), false);
			if (attachment != null) {
				command.add("/a");
				command.add(attachment);
			}

			try {

				new ProcessBuilder(command).inheritIO().start();

				// Process process = new ProcessBuilder(command).inheritIO().start();
				// process.waitFor();

			} catch (Exception e) {
				throw new ShareException("Could not create Outlook Classic (command line) email", e);
			}

		} else {
			throw new ShareException("Could not create Outlook Classic (command line) email because executablePath is not valid");
		}

	}

}
