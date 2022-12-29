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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.glasspath.common.share.ShareException;
import org.glasspath.common.share.appkit.NSSharingService.NSSharingServiceName;
import org.glasspath.common.share.mail.Mailable;

public class AppKitShareUtils {

	private AppKitShareUtils() {

	}

	public static void createEmail(Mailable mailable) throws ShareException {

		List<NSObject> objects = new ArrayList<>();

		try {

			// Don't add the exceptionHandler to the objects that will be released, it's a static instance
			NSExceptionHandler exceptionHandler = new NSExceptionHandler();
			if (Foundation.isNull(exceptionHandler)) {
				throw new ShareException("Could not create NSExceptionHandler");
			}

			// https://stackoverflow.com/questions/9797922/uncaught-exception-handler-not-called
			// https://lists.gnu.org/archive/html/gnustep-dev/2009-02/msg00028.html

			// TODO: Try to implement a exception handler which prevents application-crashes
			// exceptionHandler.setExceptionHangingMask(0);

			NSSharingService sharingService = new NSSharingService(NSSharingServiceName.COMPOSE_EMAIL);
			if (!Foundation.addObject(sharingService, objects)) {
				throw new ShareException("Could not create NSSharingService");
			}

			if (mailable.getTo() != null && mailable.getTo().size() > 0) {

				String to = mailable.getTo().get(0);

				NSString toString = new NSString(to);
				if (!Foundation.addObject(toString, objects)) {
					throw new ShareException("Could not create NSString(to)");
				}

				NSArray recipientsArrays = new NSArray(toString);
				if (!Foundation.addObject(recipientsArrays, objects)) {
					throw new ShareException("Could not create NSArray(toString)");
				}

				for (int i = 1; i < mailable.getTo().size(); i++) {

					to = mailable.getTo().get(i);

					toString = new NSString(to);
					if (!Foundation.addObject(toString, objects)) {
						throw new ShareException("Could not create NSString(to)");
					}

					recipientsArrays = recipientsArrays.arrayByAddingObject(toString);
					if (!Foundation.addObject(recipientsArrays, objects)) {
						throw new ShareException("Could not create NSArray from recipientsArrays.arrayByAddingObject(toString)");
					}

				}

				sharingService.setRecipients(recipientsArrays);

			}

			String subject = "";
			if (mailable.getSubject() != null) {
				subject = mailable.getSubject();
			}

			NSString subjectString = new NSString(subject);
			if (!Foundation.addObject(subjectString, objects)) {
				throw new ShareException("Could not create NSString(subject)");
			}

			sharingService.setSubject(subjectString);
			
			String body = "";
			if (mailable.getHtml() != null && mailable.getHtml().length() > 0) {
				body = mailable.getHtml();
			} else if (mailable.getText() != null) {
				body = mailable.getText();
			}

			NSString bodyString = new NSString(body);
			if (!Foundation.addObject(bodyString, objects)) {
				throw new ShareException("Could not create NSString(body)");
			}

			NSArray itemsArrayBody = new NSArray(bodyString);
			if (!Foundation.addObject(itemsArrayBody, objects)) {
				throw new ShareException("Could not create NSArray(bodyString)");
			}

			// Let's make sure the attachments are valid (NSException will crash our application)
			List<String> attachments = new ArrayList<>();
			if (mailable.getAttachments() != null) {

				for (String attachment : mailable.getAttachments()) {

					if (attachment.length() > 0) {

						File attachmentFile = new File(attachment);
						if (attachmentFile.exists() && !attachmentFile.isDirectory()) {
							attachments.add(attachment);
						}

					}

				}

			}

			if (attachments.size() > 0) {

				NSString attachmentString = new NSString(attachments.get(0));
				if (!Foundation.addObject(attachmentString, objects)) {
					throw new ShareException("Could not create NSString(attachments.get(0))");
				}

				NSURL attachmentURL = new NSURL(attachmentString.getId());
				if (!Foundation.addObject(attachmentURL, objects)) {
					throw new ShareException("Could not create NSURL(attachmentString.getId())");
				}

				NSArray itemsArrayBodyAndAttachment = itemsArrayBody.arrayByAddingObject(attachmentURL);
				if (!Foundation.addObject(itemsArrayBodyAndAttachment, objects)) {
					throw new ShareException("Could not create NSArray from itemsArrayBody.arrayByAddingObject(attachmentURL)");
				}

				for (int i = 1; i < attachments.size(); i++) {

					attachmentString = new NSString(attachments.get(1));
					if (!Foundation.addObject(attachmentString, objects)) {
						throw new ShareException("Could not create NSString(attachments.get(1))");
					}

					attachmentURL = new NSURL(attachmentString.getId());
					if (!Foundation.addObject(attachmentURL, objects)) {
						throw new ShareException("Could not create NSURL(attachmentString.getId())");
					}

					itemsArrayBodyAndAttachment = itemsArrayBodyAndAttachment.arrayByAddingObject(attachmentURL);
					if (!Foundation.addObject(itemsArrayBodyAndAttachment, objects)) {
						throw new ShareException("Could not create NSArray from itemsArrayBodyAndAttachment.arrayByAddingObject(attachmentURL)");
					}

				}

				// TODO: Implement canPerformWithItems?
				sharingService.performWithItems(itemsArrayBodyAndAttachment);

				Foundation.releaseObjects(objects);

			} else {

				// TODO: Implement canPerformWithItems?
				sharingService.performWithItems(itemsArrayBody);

				Foundation.releaseObjects(objects);

			}

		} catch (UnsatisfiedLinkError e) {
			throw new ShareException("Could not create email through AppKit sharing service", e);
		} catch (Exception e) {
			throw new ShareException("Could not create email through AppKit sharing service", e);
		}

	}

}
