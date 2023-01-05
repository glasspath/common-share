package org.glasspath.common.share.mail;

import org.glasspath.common.share.ShareException;
import org.glasspath.common.share.mail.account.Account;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
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
					.withTransportStrategy(TransportStrategy.SMTPS)
					.withSessionTimeout(timeout)
					.buildMailer();

			Session session = mailer.getSession();
			if (session != null) {

				try {

					Transport transport = session.getTransport("smtps"); //$NON-NLS-1$
					if (transport != null) {

						try {

							transport.connect();

							try {

								Store store = session.getStore("imaps"); //$NON-NLS-1$
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
