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

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.glasspath.common.share.ShareException;

public class MailUtils {

	// TODO: Is not yet correctly parsing all input strings
	public static List<String> parseRecipients(String recipients) {

		List<String> parsed = new ArrayList<>();

		if (recipients != null) {

			recipients = recipients.toLowerCase();

			// https://stackoverflow.com/questions/201323/how-can-i-validate-an-email-address-using-a-regular-expression
			Matcher matcher = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])").matcher(recipients); //$NON-NLS-1$
			while (matcher.find()) {
				parsed.add(matcher.group());
			}

		}

		return parsed;

	}

	public static boolean isValidEmailAddress(String emailAddress) {
		return parseRecipients(emailAddress).size() == 1;
	}

	public static String getEmailAddress(String emailAddress) {
		List<String> parsed = parseRecipients(emailAddress);
		if (parsed.size() == 1) {
			return parsed.get(0);
		} else {
			return null;
		}
	}

	public static String getHostPart(String emailAddress) {

		if (emailAddress != null) {

			int i = emailAddress.indexOf("@"); //$NON-NLS-1$
			if (i >= 0) {
				return emailAddress.substring(i + 1);
			}

		}

		return null;

	}

	public static boolean isValidHost(String host) {
		return host != null; // TODO
	}

	public static boolean isValidPort(int port) {
		return port > 0;
	}

	public static String createElementsString(List<String> elements, String separator) {

		String s = ""; //$NON-NLS-1$

		if (elements != null && elements.size() > 0) {

			s = elements.get(0);

			if (elements.size() > 1) {
				for (int i = 1; i < elements.size(); i++) {
					s += separator + elements.get(i);
				}
			}

		}

		return s;

	}

	public static String createElementsString(List<String> elements, String prepend, String append, String separator) {

		String s = ""; //$NON-NLS-1$

		if (elements != null && elements.size() > 0) {

			s = prepend + elements.get(0) + append;

			if (elements.size() > 1) {
				for (int i = 1; i < elements.size(); i++) {
					s += separator + prepend + elements.get(i) + append;
				}
			}

		}

		return s;

	}

	public static URI createMailtoUri(Mailable mailable) throws ShareException {

		try {

			String mailto = "mailto:" + createElementsString(mailable.getTo(), ",");

			String subject = "";
			if (mailable.getSubject() != null) {
				subject = mailable.getSubject();
			}
			mailto += "?subject=" + subject;

			if (mailable.getCc() != null && mailable.getCc().size() > 0) {
				mailto += "&cc=" + createElementsString(mailable.getCc(), ",");
			}

			if (mailable.getBcc() != null && mailable.getBcc().size() > 0) {
				mailto += "&bcc=" + createElementsString(mailable.getBcc(), ",");
			}

			String body = "";
			if (mailable.getText() != null) {
				body = mailable.getText();
			}
			mailto += "&body=" + body;

			// TODO?
			mailto = mailto.replace(" ", "%20");
			mailto = mailto.replace("\n", "%0D%0A");

			return URI.create(mailto);

		} catch (Exception e) {
			throw new ShareException("Could not create mailto URI", e);
		}

	}

	public static URI createGmailMailtoUri(Mailable mailable) throws ShareException {

		try {

			String mailto = "mailto:" + createElementsString(mailable.getTo(), ",");

			String subject = "";
			if (mailable.getSubject() != null) {
				subject = mailable.getSubject();
			}
			mailto += "?subject=" + subject;

			if (mailable.getCc() != null && mailable.getCc().size() > 0) {
				mailto += "&cc=" + createElementsString(mailable.getCc(), ",");
			}

			if (mailable.getBcc() != null && mailable.getBcc().size() > 0) {
				mailto += "&bcc=" + createElementsString(mailable.getBcc(), ",");
			}

			String body = "";
			if (mailable.getText() != null) {
				body = mailable.getText();
			}
			mailto += "&body=" + body;

			mailto = URLEncoder.encode(mailto, StandardCharsets.UTF_8);

			return URI.create("https://mail.google.com/mail/?extsrc=mailto&url=" + mailto);

		} catch (Exception e) {
			throw new ShareException("Could not create gmail mailto URI", e);
		}

	}

	public static URI createOutlookLiveUri(Mailable mailable) throws ShareException {

		try {

			String arg = "";

			String subject = "";
			if (mailable.getSubject() != null) {
				subject = mailable.getSubject();
			}
			arg += "?subject=" + subject;

			String body = "";
			if (mailable.getText() != null) {
				body = mailable.getText();
			}
			arg += "&body=" + body;

			if (mailable.getTo() != null && mailable.getTo().size() > 0) {
				arg += "&to=" + createElementsString(mailable.getTo(), ",");
			}

			if (mailable.getCc() != null && mailable.getCc().size() > 0) {
				arg += "&cc=" + createElementsString(mailable.getCc(), ",");
			}

			if (mailable.getBcc() != null && mailable.getBcc().size() > 0) {
				arg += "&bcc=" + createElementsString(mailable.getBcc(), ",");
			}

			arg = arg.replace(" ", "%20");
			arg = arg.replace("\n", "%0D%0A");

			return URI.create("https://outlook.live.com/mail/0/deeplink/compose" + arg);

		} catch (Exception e) {
			throw new ShareException("Could not create outlook live URI", e);
		}

	}

}
