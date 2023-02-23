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

import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.Variant.VARIANT;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMLateBindingObject;
import com.sun.jna.platform.win32.COM.IDispatch;

@SuppressWarnings("nls")
public class Outlook extends COMLateBindingObject {

	public static final int OL_MAIL_RECIPIENT_TYPE_OL_ORIGINATOR = 0;
	public static final int OL_MAIL_RECIPIENT_TYPE_OL_TO = 1;
	public static final int OL_MAIL_RECIPIENT_TYPE_OL_CC = 2;
	public static final int OL_MAIL_RECIPIENT_TYPE_OL_BCC = 3;

	public static final int OL_BODY_FORMAT_OL_FORMAT_UNSPECIFIED = 0;
	public static final int OL_BODY_FORMAT_OL_FORMAT_PLAIN = 1;
	public static final int OL_BODY_FORMAT_OL_FORMAT_HTML = 2;
	public static final int OL_BODY_FORMAT_OL_FORMAT_RICH_TEXT = 3;

	/*
	 * https://learn.microsoft.com/en-us/office/vba/api/outlook.mailitem.display
	 */

	public Outlook() throws COMException {
		super("Outlook.Application", false);
	}

	public String getVersion() throws COMException {
		return getStringProperty("Version");
	}

	public Application getApplication() throws COMException {
		IDispatch iDispatch = getAutomationProperty("Application");
		if (iDispatch != null) {
			return new Application(iDispatch);
		} else {
			return null;
		}
	}

	public static class Application extends COMLateBindingObject {

		public Application(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}

		public MailItem createMailItem() throws COMException {

			VARIANT result = invoke("CreateItem", new VARIANT(0));

			if (result != null && result.getVarType().intValue() == Variant.VT_DISPATCH) {
				return new MailItem((IDispatch) result.getValue());
			} else {
				return null;
			}

		}

	}

	public static class MailItem extends COMLateBindingObject {

		private Attachments attachments = null;
		private Recipients recipients = null;

		public MailItem(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}

		public Attachments getAttachments() throws COMException {

			if (attachments == null) {

				IDispatch iDispatch = getAutomationProperty("Attachments");
				if (iDispatch != null) {
					attachments = new Attachments(iDispatch);
				}

			}

			return attachments;

		}

		public Attachment addAttachment(String filePath) throws COMException {
			Attachments attachments = getAttachments();
			if (attachments != null) {
				return attachments.add(filePath);
			} else {
				return null;
			}
		}

		public Attachment addAttachment(String filePath, String cid) throws COMException {
			Attachments attachments = getAttachments();
			if (attachments != null) {
				return attachments.add(filePath, cid);
			} else {
				return null;
			}
		}

		public Recipients getRecipients() throws COMException {

			if (recipients == null) {

				IDispatch iDispatch = getAutomationProperty("Recipients");
				if (iDispatch != null) {
					recipients = new Recipients(iDispatch);
				}

			}

			return recipients;

		}

		public Recipient addRecipient(String name, int type) throws COMException {

			Recipients recipients = getRecipients();
			if (recipients != null) {

				Recipient recipient = recipients.add(name);
				if (recipient != null) {
					recipient.setType(type);
				}

				return recipient;

			} else {
				return null;
			}

		}

		public Recipient addToRecipient(String name) throws COMException {
			return addRecipient(name, OL_MAIL_RECIPIENT_TYPE_OL_TO);
		}

		public Recipient addCcRecipient(String name) throws COMException {
			return addRecipient(name, OL_MAIL_RECIPIENT_TYPE_OL_CC);
		}

		public Recipient addBccRecipient(String name) throws COMException {
			return addRecipient(name, OL_MAIL_RECIPIENT_TYPE_OL_BCC);
		}

		public AddressEntry getSender() throws COMException {
			IDispatch iDispatch = getAutomationProperty("Sender");
			if (iDispatch != null) {
				return new AddressEntry(iDispatch);
			} else {
				return null;
			}
		}

		public String getTo() throws COMException {
			return getStringProperty("To");
		}

		public void setTo(String to) throws COMException {
			setProperty("To", to);
		}

		public String getCC() throws COMException {
			return getStringProperty("CC");
		}

		public void setCC(String cc) throws COMException {
			setProperty("CC", cc);
		}

		public String getBCC() throws COMException {
			return getStringProperty("BCC");
		}

		public void setBCC(String bcc) throws COMException {
			setProperty("BCC", bcc);
		}

		public String getSubject() throws COMException {
			return getStringProperty("Subject");
		}

		public void setSubject(String subject) throws COMException {
			setProperty("Subject", subject);
		}

		public int geBodyFormat() throws COMException {
			return getIntProperty("BodyFormat");
		}

		public void setBodyFormat(int bodyFormat) throws COMException {
			setProperty("BodyFormat", bodyFormat);
		}

		public String geBody() throws COMException {
			return getStringProperty("Body");
		}

		public void setBody(String body) throws COMException {
			setProperty("Body", body);
		}

		public String geHTMLBody() throws COMException {
			return getStringProperty("HTMLBody");
		}

		public void setHTMLBody(String htmlBody) throws COMException {
			setProperty("HTMLBody", htmlBody);
		}

		public VARIANT Display() throws COMException {
			VARIANT result = invoke("Display", new VARIANT(0));
			// TODO?
			return result;
		}

	}

	public static class Attachments extends COMLateBindingObject {

		public Attachments(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}

		public Attachment add(String filePath) throws COMException {

			VARIANT result = invoke("Add", new VARIANT(filePath));

			if (result != null && result.getVarType().intValue() == Variant.VT_DISPATCH) {
				return new Attachment((IDispatch) result.getValue());
			} else {
				return null;
			}

		}

		public Attachment add(String filePath, String cid) throws COMException {

			Attachment attachment = add(filePath);

			if (attachment != null && cid != null) {

				PropertyAccessor propertyAccessor = attachment.getPropertyAccessor();
				if (propertyAccessor != null) {
					propertyAccessor.set("http://schemas.microsoft.com/mapi/proptag/0x3712001F", cid);
				}

			}

			return attachment;

		}

	}

	public static class Attachment extends COMLateBindingObject {

		public Attachment(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}

		public PropertyAccessor getPropertyAccessor() throws COMException {
			IDispatch iDispatch = getAutomationProperty("PropertyAccessor");
			if (iDispatch != null) {
				return new PropertyAccessor(iDispatch);
			} else {
				return null;
			}
		}

	}

	public static class PropertyAccessor extends COMLateBindingObject {

		public PropertyAccessor(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}

		public VARIANT set(String property, String value) throws COMException {
			VARIANT result = invoke("SetProperty", new VARIANT(property), new VARIANT(value));
			// TODO?
			return result;
		}

	}

	public static class Recipients extends COMLateBindingObject {

		public Recipients(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}

		public Recipient add(String name) throws COMException {

			VARIANT result = invoke("Add", new VARIANT(name));

			if (result != null && result.getVarType().intValue() == Variant.VT_DISPATCH) {
				return new Recipient((IDispatch) result.getValue());
			} else {
				return null;
			}

		}

	}

	public static class Recipient extends COMLateBindingObject {

		public Recipient(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}

		public PropertyAccessor getPropertyAccessor() throws COMException {
			IDispatch iDispatch = getAutomationProperty("PropertyAccessor");
			if (iDispatch != null) {
				return new PropertyAccessor(iDispatch);
			} else {
				return null;
			}
		}

		public int getType() throws COMException {
			return getIntProperty("Type");
		}

		public void setType(int to) throws COMException {
			setProperty("Type", to);
		}

	}

	public static class AddressEntry extends COMLateBindingObject {

		public AddressEntry(IDispatch iDispatch) throws COMException {
			super(iDispatch);
		}

		public String getAddress() throws COMException {
			if (getIDispatch() != null) {
				return getStringProperty("Address");
			} else {
				return null;
			}
		}

		public void setAddress(String address) throws COMException {
			setProperty("Address", address);
		}

	}

}