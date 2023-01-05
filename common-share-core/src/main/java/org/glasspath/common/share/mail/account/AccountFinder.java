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

import org.glasspath.common.Common;
import org.glasspath.common.share.mail.Imap;
import org.glasspath.common.share.mail.MailUtils;
import org.glasspath.common.share.mail.Smtp;

public abstract class AccountFinder {

	public AccountFinder() {

	}

	public Account findAccount(String email) {

		String host = MailUtils.getHostPart(email);

		Account account = new Account();
		account.setName(email);
		account.setEmail(email);

		SmtpConfiguration smptConfiguration = new SmtpConfiguration();
		account.setSmtpConfiguration(smptConfiguration);

		outerSmtpLoop: for (int smtpUrlIndex = 0; smtpUrlIndex < Smtp.COMMON_URLS.length; smtpUrlIndex++) {

			for (int smtpPortIndex = 0; smtpPortIndex < Smtp.COMMON_PORTS.length; smtpPortIndex++) {

				if (isCancelled()) {
					break outerSmtpLoop;
				} else {

					smptConfiguration.setHost(Smtp.COMMON_URLS[smtpUrlIndex] + host);
					smptConfiguration.setPort(Smtp.COMMON_PORTS[smtpPortIndex]);

					try {

						Common.LOGGER.info("SMTP: Trying " + smptConfiguration.getHost() + " on port " + smptConfiguration.getPort()); //$NON-NLS-1$ //$NON-NLS-2$
						if (testSmtpConfiguration(account)) {

							boolean imapConfigurationFound = false;

							ImapConfiguration imapConfguration = new ImapConfiguration();
							account.setImapConfiguration(imapConfguration);

							outerImapLoop: for (int imapUrlIndex = 0; imapUrlIndex < Imap.COMMON_URLS.length; imapUrlIndex++) {

								for (int imapPortIndex = 0; imapPortIndex < Imap.COMMON_PORTS.length; imapPortIndex++) {

									if (isCancelled()) {
										break outerSmtpLoop;
									} else {

										imapConfguration.setHost(Imap.COMMON_URLS[imapUrlIndex] + host);
										imapConfguration.setPort(Imap.COMMON_PORTS[imapPortIndex]);

										try {

											Common.LOGGER.info("IMAP: Trying " + imapConfguration.getHost() + " on port " + imapConfguration.getPort()); //$NON-NLS-1$ //$NON-NLS-2$
											String sentFolderPath = getImapSentFolderPath(account);
											if (sentFolderPath != null && sentFolderPath.length() > 0) {

												imapConfguration.setSentFolderPath(sentFolderPath);
												imapConfigurationFound = true;

												break outerImapLoop;

											}

										} catch (Exception e) {
											Common.LOGGER.error("Exception while finding imap configuration: ", e); //$NON-NLS-1$
										}

									}

								}

							}

							if (!imapConfigurationFound) {
								account.setImapConfiguration(null);
							}

							return account;

						}

					} catch (Exception e) {
						Common.LOGGER.error("Exception while finding smtp configuration: ", e); //$NON-NLS-1$
					}

				}

			}

		}

		return null;

	}

	protected boolean isCancelled() {
		return false;
	}

	protected abstract boolean testSmtpConfiguration(Account account);

	protected abstract String getImapSentFolderPath(Account account);

}
