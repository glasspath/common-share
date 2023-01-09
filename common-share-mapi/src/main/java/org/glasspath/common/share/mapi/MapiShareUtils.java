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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.glasspath.common.share.ShareException;
import org.glasspath.common.share.mail.Mailable;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.WString;

public class MapiShareUtils {

	private MapiShareUtils() {

	}

	public static void createEmail(Mailable mailable) throws ShareException {

		if (Platform.isWindows()) {

			try {

				MapiLibrary mapi = Platform.isWindows() ? (MapiLibrary) Native.load("mapi32", MapiLibrary.class) : null;

				MapiMessageW message = new MapiMessageW();

				int recipientCount = 0;

				if (mailable.getFrom() != null) {
					recipientCount += 1;
				}
				if (mailable.getTo() != null) {
					recipientCount += mailable.getTo().size();
				}
				if (mailable.getCc() != null) {
					recipientCount += mailable.getCc().size();
				}
				if (mailable.getBcc() != null) {
					recipientCount += mailable.getBcc().size();
				}

				if (recipientCount > 0) {

					MapiRecipDescW[] recipients = (MapiRecipDescW[]) new MapiRecipDescW().toArray(recipientCount);

					int i = 0;

					if (mailable.getFrom() != null) {

						recipients[i].ulRecipClass = MapiRecipDescW.MAPI_ORIG;
						recipients[i].lpszName = new WString(mailable.getFrom());
						recipients[i].lpszAddress = new WString("SMTP:" + mailable.getFrom());
						recipients[i].write();

						i++;

					}

					if (mailable.getTo() != null) {

						for (String to : mailable.getTo()) {

							recipients[i].ulRecipClass = MapiRecipDescW.MAPI_TO;
							recipients[i].lpszName = new WString(to);
							recipients[i].lpszAddress = new WString("SMTP:" + to);
							recipients[i].write();

							i++;

						}

					}

					if (mailable.getCc() != null) {

						for (String cc : mailable.getCc()) {

							recipients[i].ulRecipClass = MapiRecipDescW.MAPI_CC;
							recipients[i].lpszName = new WString(cc);
							recipients[i].lpszAddress = new WString("SMTP:" + cc);
							recipients[i].write();

							i++;

						}

					}

					if (mailable.getBcc() != null) {

						for (String bcc : mailable.getBcc()) {

							recipients[i].ulRecipClass = MapiRecipDescW.MAPI_BCC;
							recipients[i].lpszName = new WString(bcc);
							recipients[i].lpszAddress = new WString("SMTP:" + bcc);
							recipients[i].write();

							i++;

						}

					}

					message.receips = recipients[0].getPointer();
					message.receipCount = recipientCount;

				}

				if (mailable.getSubject() != null) {
					message.subject = new WString(mailable.getSubject());
				} else {
					message.subject = new WString("");
				}

				if (mailable.getText() != null) {
					message.noteText = new WString(mailable.getText());
				} else {
					message.noteText = new WString("");
				}

				if (mailable.getAttachments() != null && mailable.getAttachments().size() > 0) {

					Map<String, String> attachments = new HashMap<>();

					for (String attachment : mailable.getAttachments()) {

						if (attachment.length() > 0) {

							File attachmentFile = new File(attachment);
							if (attachmentFile.exists() && !attachmentFile.isDirectory()) {
								attachments.put(attachment, attachmentFile.getName());
							}

						}

					}

					if (attachments.size() > 0) {

						MapiFileDescW[] files = (MapiFileDescW[]) new MapiFileDescW().toArray(attachments.size());

						int i = 0;

						for (Entry<String, String> entry : mailable.getImages().entrySet()) {

							files[i].lpszPathName = new WString(entry.getKey());
							files[i].lpszFileName = new WString(entry.getValue());
							files[i].write();

							i++;

						}

						message.fileCount = attachments.size();
						message.files = files[0].getPointer();

					}

				}

				// TODO: Explain arguments
				int result = mapi.MAPISendMailW(null, null, message, 4 | 8, 0);
				// int result = MapiLibrary.INSTANCE.MAPISendMailW(null, hwnd, message, 4 | 8, 0);

				if (result != 0) {
					throw new ShareException("MAPISendMailW returned error: " + result);
				}

			} catch (ShareException e) {
				throw e;
			} catch (UnsatisfiedLinkError e) {
				throw new ShareException("Could not create email via Mapi", e);
			} catch (Exception e) {
				throw new ShareException("Could not create email via Mapi", e);
			}

		}

	}

}
