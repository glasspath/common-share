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

import org.glasspath.common.share.mail.MailUtils;
import org.glasspath.common.share.mail.Smtp;
import org.glasspath.common.share.mail.Smtp.Protocol;

public class SmtpConfiguration {

	private String host = null;
	private int port = 0;
	private Protocol protocol = Smtp.DEFAULT_PROTOCOL;

	public SmtpConfiguration() {

	}

	public SmtpConfiguration(SmtpConfiguration smtpConfiguration) {
		host = smtpConfiguration.host;
		port = smtpConfiguration.port;
		protocol = smtpConfiguration.protocol;
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

	public boolean isValid() {
		return MailUtils.isValidHost(host) && MailUtils.isValidPort(port) && protocol != null;
	}

}
