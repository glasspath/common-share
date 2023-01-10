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

import org.glasspath.common.share.ShareException;
import org.glasspath.common.share.mail.account.Account;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;

import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;

public abstract class ImapStoreOperation {

	public ImapStoreOperation(Account account, String password, int timeout) throws ShareException {

		ShareException exception = null;

		if (account != null && account.getSmtpConfiguration() != null && account.getImapConfiguration() != null && account.isValid() && password != null) {

			Mailer mailer = MailerBuilder
					.withSMTPServer(account.getSmtpConfiguration().getHost(), account.getSmtpConfiguration().getPort(), account.getEmail(), password)
					.withTransportStrategy(MailShareUtils.getTransportStrategy(account.getSmtpConfiguration().getProtocol()))
					.withSessionTimeout(timeout)
					.buildMailer();

			Session session = mailer.getSession();
			if (session != null) {

				try {

					Transport transport = session.getTransport(account.getSmtpConfiguration().getProtocol().key);
					if (transport != null) {

						try {

							transport.connect();

							try {

								Store store = session.getStore(account.getImapConfiguration().getProtocol().key);
								if (store != null) {

									try {

										store.connect(account.getImapConfiguration().getHost(), account.getImapConfiguration().getPort(), account.getEmail(), password);

										try {
											performOperation(store);
										} catch (Exception e) {
											exception = new ShareException("Could not perform operation", e); //$NON-NLS-1$
										}

										store.close();

									} catch (Exception e) {
										exception = new ShareException("Could not access store", e); //$NON-NLS-1$
									}

								} else {
									exception = new ShareException("store is null"); //$NON-NLS-1$
								}

							} catch (Exception e) {
								exception = new ShareException("Could not get store", e); //$NON-NLS-1$
							}

							transport.close();

						} catch (Exception e) {
							exception = new ShareException("Could not connect transport", e); //$NON-NLS-1$
						}

					} else {
						exception = new ShareException("transport is null"); //$NON-NLS-1$
					}

				} catch (Exception e) {
					exception = new ShareException("Could not get transport", e); //$NON-NLS-1$
				}

			} else {
				exception = new ShareException("session is null"); //$NON-NLS-1$
			}

		} else {
			exception = new ShareException("Invalid arguments passed"); //$NON-NLS-1$
		}

		if (exception != null) {
			throw exception;
		}

	}

	protected abstract void performOperation(Store store) throws ShareException;

}
