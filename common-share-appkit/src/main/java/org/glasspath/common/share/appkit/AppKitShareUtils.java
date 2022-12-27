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

import org.glasspath.common.share.appkit.NSSharingService.NSSharingServiceName;
import org.glasspath.common.share.mail.Mailable;

public class AppKitShareUtils {

	public static String TEST_ATTACHEMENT = "/Users/remcopoelstra/Documents/temp/export.csv";

	private AppKitShareUtils() {

	}

	public static int createEmail(Mailable mailable) {

		// TODO: Remove
		mailable.getAttachments().add(TEST_ATTACHEMENT);

		List<NSObject> objects = new ArrayList<>();

		// Don't add the exceptionHandler to the objects that will be released, it's a static instance
		NSExceptionHandler exceptionHandler = new NSExceptionHandler();
		if (Foundation.isNull(exceptionHandler)) {
			return -1;
		}

		// https://stackoverflow.com/questions/9797922/uncaught-exception-handler-not-called
		// https://lists.gnu.org/archive/html/gnustep-dev/2009-02/msg00028.html

		// TODO: Try to implement a exception handler which prevents application-crashes
		// exceptionHandler.setExceptionHangingMask(0);

		NSSharingService sharingService = new NSSharingService(NSSharingServiceName.COMPOSE_EMAIL);
		if (!Foundation.addObject(sharingService, objects)) {
			return -2;
		}

		String to = "";
		if (mailable.getTo().size() > 0) {
			// TODO: Add all 'to' recipients
			to = mailable.getTo().get(0);
		}

		NSString toString = new NSString(to);
		if (!Foundation.addObject(toString, objects)) {
			return -3;
		}

		NSArray recipientsArrays = new NSArray(toString);
		if (!Foundation.addObject(recipientsArrays, objects)) {
			return -4;
		}

		sharingService.setRecipients(recipientsArrays);

		NSString subjectString = new NSString(mailable.getSubject());
		if (!Foundation.addObject(subjectString, objects)) {
			return -5;
		}

		sharingService.setSubject(subjectString);

		String body = "";
		if (mailable.getHtml() != null && mailable.getHtml().length() > 0) {
			body = mailable.getHtml();
		} else {
			body = mailable.getText();
		}

		NSString bodyString = new NSString(body);
		if (!Foundation.addObject(bodyString, objects)) {
			return -6;
		}

		NSArray itemsArrayBody = new NSArray(bodyString);
		if (!Foundation.addObject(itemsArrayBody, objects)) {
			return -7;
		}

		// TODO: Add all attachments
		String attachmentPath = null;
		if (mailable.getAttachments().size() > 0) {

			String attachment = mailable.getAttachments().get(0);
			if (attachment.length() > 0) {

				File attachmentFile = new File(attachment);
				if (attachmentFile.exists() && !attachmentFile.isDirectory()) {
					attachmentPath = attachment;
				}

			}

		}

		if (attachmentPath != null) {

			NSString attachmentString = new NSString(attachmentPath);
			if (!Foundation.addObject(attachmentString, objects)) {
				return -8;
			}

			NSURL attachmentURL = new NSURL(attachmentString.getId());
			if (!Foundation.addObject(attachmentURL, objects)) {
				return -9;
			}

			NSArray itemsArrayBodyAndAttachment = itemsArrayBody.arrayByAddingObject(attachmentURL);
			if (!Foundation.addObject(itemsArrayBodyAndAttachment, objects)) {
				return -10;
			}

			// TODO: Implement canPerformWithItems?
			sharingService.performWithItems(itemsArrayBodyAndAttachment);

			Foundation.releaseObjects(objects);

		} else {

			// TODO: Implement canPerformWithItems?
			sharingService.performWithItems(itemsArrayBody);

			Foundation.releaseObjects(objects);

		}

		return 0;

	}

}
