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

import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;

import org.glasspath.common.Common;
import org.glasspath.common.share.ShareException;
import org.glasspath.common.share.mail.account.SmtpAccount;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.converter.EmailConverter;
import org.simplejavamail.mailer.MailerBuilder;

public class MailShareUtils {

	private MailShareUtils() {

	}

	public static void testAccount(SmtpAccount account, String password, int timeout) throws ShareException {

		if (account != null && account.isValid() && password != null) {

			try {

				Mailer mailer = MailerBuilder
						.withSMTPServer(account.getHost(), account.getPort(), account.getEmail(), password)
						.withTransportStrategy(TransportStrategy.SMTPS)
						.withSessionTimeout(timeout)
						.buildMailer();

				Common.LOGGER.info("Testing connection, host: " + account.getHost() + ", port: " + account.getPort()); //$NON-NLS-1$ //$NON-NLS-2$
				mailer.testConnection();
				Common.LOGGER.info("Testing connection finished"); //$NON-NLS-1$

			} catch (Exception e) {
				throw new ShareException("Testing of account failed", e);
			}

		} else {
			throw new ShareException("Illegal argument passed for testing account");
		}

	}

	public static CompletableFuture<Void> sendSimpleEmail(Email email, SmtpAccount account, String password, int timeout) throws ShareException {

		if (account != null && account.isValid() && password != null) {

			Mailer mailer = MailerBuilder
					.withSMTPServer(account.getHost(), account.getPort(), account.getEmail(), password)
					.withTransportStrategy(TransportStrategy.SMTPS)
					.withSessionTimeout(timeout)
					.buildMailer();

			// mailer.testConnection();
			Common.LOGGER.info("Sending email with id: " + email.getId()); //$NON-NLS-1$

			try {

				/*
				TransportRunner.setListener(new Listener() {
				
					@Override
					public void transportSelected(Transport transport) {
				
						transport.addConnectionListener(new ConnectionListener() {
				
							@Override
							public void opened(ConnectionEvent e) {
								System.out.println("opened");
							}
				
							@Override
							public void disconnected(ConnectionEvent e) {
								System.out.println("disconnected");
							}
				
							@Override
							public void closed(ConnectionEvent e) {
								System.out.println("closed");
							}
						});
				
						transport.addTransportListener(new TransportListener() {
				
							@Override
							public void messagePartiallyDelivered(TransportEvent e) {
								System.out.println("messagePartiallyDelivered");
							}
				
							@Override
							public void messageNotDelivered(TransportEvent e) {
								System.out.println("messageNotDelivered");
							}
				
							@Override
							public void messageDelivered(TransportEvent e) {
								System.out.println("messageDelivered");
							}
						});
				
					}
				});
				*/

				return mailer.sendMail(email);

			} catch (Exception e) {
				throw new ShareException("Could not send simple email (smtp)", e); //$NON-NLS-1$
			}

		}

		return null;

	}

	public static void exportToEml(Email email, File emlFile) throws ShareException {

		try {

			String eml = EmailConverter.emailToEML(email);

			try (PrintWriter out = new PrintWriter(emlFile)) {
				out.println(eml);
			}

		} catch (Exception e) {
			throw new ShareException("Could not export email to eml", e); //$NON-NLS-1$
		}

	}

}
