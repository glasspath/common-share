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
package org.glasspath.common.share.mail.account;

import org.glasspath.common.share.mail.Imap;
import org.glasspath.common.share.mail.Imap.Protocol;
import org.glasspath.common.share.mail.MailUtils;

public class ImapConfiguration {

	private String host = null;
	private int port = 0;
	private Protocol protocol = Imap.DEFAULT_PROTOCOL;
	private String sentFolderPath = null;

	public ImapConfiguration() {

	}

	public ImapConfiguration(ImapConfiguration imapConfiguration) {
		host = imapConfiguration.host;
		port = imapConfiguration.port;
		protocol = imapConfiguration.protocol;
		sentFolderPath = imapConfiguration.sentFolderPath;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public String getSentFolderPath() {
		return sentFolderPath;
	}

	public void setSentFolderPath(String sentFolderPath) {
		this.sentFolderPath = sentFolderPath;
	}

	public boolean isValid() {
		return MailUtils.isValidHost(host) && MailUtils.isValidPort(port) && protocol != null;
	}

}
