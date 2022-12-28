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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.glasspath.common.share.ShareException;

public class MailUtils {

	public static List<String> parseRecipients(String recipients) {

		List<String> parsed = new ArrayList<>();

		if (recipients != null) {

			Matcher matcher = Pattern.compile("\\w+@\\w+.\\w+").matcher(recipients); //$NON-NLS-1$
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

	public static String createRecipientsString(List<String> recipients, String separator) {

		String s = ""; //$NON-NLS-1$

		if (recipients.size() > 0) {

			s = recipients.get(0);

			if (recipients.size() > 1) {
				for (int i = 1; i < recipients.size(); i++) {
					s += separator + recipients.get(i);
				}
			}

		}

		return s;

	}

	public static URI createMailtoUri(Mailable mailable) throws ShareException {

		try {

			String mailto = "mailto:" + createRecipientsString(mailable.getTo(), ",");

			mailto += "?subject=" + mailable.getSubject();

			if (mailable.getCc().size() > 0) {
				mailto += "&cc=" + createRecipientsString(mailable.getCc(), ",");
			}

			if (mailable.getBcc().size() > 0) {
				mailto += "&bcc=" + createRecipientsString(mailable.getBcc(), ",");
			}

			mailto += "&body=" + mailable.getText();

			// TODO?
			mailto = mailto.replace(" ", "%20");
			mailto = mailto.replace("\n", "%0D%0A");

			return URI.create(mailto);

		} catch (Exception e) {
			throw new ShareException("Could not create mailto URI", e);
		}

	}

}
