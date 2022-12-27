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

public class NSSharingService extends NSObject {

	public static final NativeLong NS_SHARING_SERVICE_CLASS = Foundation.INSTANCE.objc_getClass("NSSharingService");
	public static final Pointer SHARING_SERVICE_NAMED_SELECTOR = Foundation.INSTANCE.sel_registerName("sharingServiceNamed:");
	public static final Pointer SET_RECIPIENTS_SELECTOR = Foundation.INSTANCE.sel_registerName("setRecipients:");
	public static final Pointer SET_SUBJECT_SELECTOR = Foundation.INSTANCE.sel_registerName("setSubject:");
	public static final Pointer CAN_PERFORM_WITH_ITEMS_SELECTOR = Foundation.INSTANCE.sel_registerName("canPerformWithItems:");
	public static final Pointer PERFORM_WITH_ITEMS_SELECTOR = Foundation.INSTANCE.sel_registerName("performWithItems:");

	public static enum NSSharingServiceName {

		COMPOSE_EMAIL("com.apple.share.Mail.compose");

		public final String name;

		NSSharingServiceName(String name) {
			this.name = name;
		}

	}

	public NSSharingService(NSSharingServiceName serviceName) {
		super(createNSSharingService(serviceName));
	}

	private static NativeLong createNSSharingService(NSSharingServiceName serviceName) {

		NSString serviceNameString = new NSString(serviceName.name);
		NativeLong sharingService = Foundation.INSTANCE.objc_msgSend(NS_SHARING_SERVICE_CLASS, SHARING_SERVICE_NAMED_SELECTOR, serviceNameString.getId());
		serviceNameString.release();

		return sharingService;

	}

	public void setRecipients(NSArray recipients) {
		Foundation.INSTANCE.objc_msgSend(id, SET_RECIPIENTS_SELECTOR, recipients.getId());
	}

	public void setSubject(NSString subject) {
		Foundation.INSTANCE.objc_msgSend(id, SET_SUBJECT_SELECTOR, subject.getId());
	}

	/* TODO: performSelectorOnMainThread cannot return a value, this would have to be done by constructing and passing a NSInvocation
	public boolean canPerformWithItems(NSArray items) {
		return performSelectorOnMainThread(CAN_PERFORM_WITH_ITEMS_SELECTOR, items.getId(), true).intValue() > 0;
	}
	*/

	public void performWithItems(NSArray items) {
		performSelectorOnMainThread(PERFORM_WITH_ITEMS_SELECTOR, items.getId(), true);
	}

}
