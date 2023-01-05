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
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.glasspath.common.Common;
import org.glasspath.common.share.ShareException;
import org.glasspath.common.share.mail.account.Account;
import org.glasspath.common.share.mail.account.ImapConfiguration;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.converter.EmailConverter;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import jakarta.activation.FileDataSource;
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

public class MailShareUtils {

	private MailShareUtils() {

	}

	public static void testSmtpConfiguration(Account account, String password, int timeout) throws ShareException {

		if (account != null && account.getSmtpConfiguration() != null && account.isValid() && password != null) {

			try {

				Mailer mailer = MailerBuilder
						.withSMTPServer(account.getSmtpConfiguration().getHost(), account.getSmtpConfiguration().getPort(), account.getEmail(), password)
						.withTransportStrategy(TransportStrategy.SMTPS)
						.withSessionTimeout(timeout)
						.buildMailer();

				Common.LOGGER.info("Testing connection, host: " + account.getSmtpConfiguration().getHost() + ", port: " + account.getSmtpConfiguration().getPort()); //$NON-NLS-1$ //$NON-NLS-2$
				mailer.testConnection();
				Common.LOGGER.info("Testing connection finished"); //$NON-NLS-1$

			} catch (Exception e) {
				throw new ShareException("Testing of account failed", e);
			}

		} else {
			throw new ShareException("Illegal argument passed for testing account");
		}

	}

	public static org.simplejavamail.api.email.Email createSimpleEmail(Mailable mailable, Account account) throws ShareException {

		try {

			EmailPopulatingBuilder builder = EmailBuilder.startingBlank()
					.withHeader("X-Unsent", "1") // Seems to work with outlook, but not with windows mail App..
					.withHeader("X-Uniform-Type-Identifier", "com.apple.mail-draft") // For apple?
					.withHeader("X-Mozilla-Draft-Info", "internal/draft; vcard=0; receipt=0; DSN=0; uuencode=0"); // Thunderbird?

			if (account != null && account.isValid()) {
				builder.from(account.getName() != null ? account.getName() : account.getEmail(), account.getEmail());
			}

			if (mailable.getTo() != null) {
				for (String to : mailable.getTo()) {
					builder.to(to);
				}
			}

			if (mailable.getCc() != null) {
				for (String cc : mailable.getCc()) {
					builder.cc(cc);
				}
			}

			if (mailable.getBcc() != null) {
				for (String bcc : mailable.getBcc()) {
					builder.bcc(bcc);
				}
			}

			if (mailable.getSubject() != null) {
				builder.withSubject(mailable.getSubject());
			}

			if (mailable.getText() != null) {
				builder.withPlainText(mailable.getText());
			}

			if (mailable.getHtml() != null && mailable.getHtml().length() > 0) {
				builder.withHTMLText(mailable.getHtml());
			}

			if (mailable.getImages() != null) {

				for (Entry<String, String> entry : mailable.getImages().entrySet()) {

					if (entry.getValue() != null) {

						File imageFile = new File(entry.getValue());
						if (imageFile.exists() && !imageFile.isDirectory()) {
							builder.withEmbeddedImage(entry.getKey(), new FileDataSource(imageFile));
						}

					}

				}

			}

			if (mailable.getAttachments() != null) {

				for (String attachment : mailable.getAttachments()) {
					builder.withAttachment(null, new FileDataSource(attachment));
				}

			}

			return builder.buildEmail();

		} catch (Exception e) {
			throw new ShareException("Could not create simple email", e); //$NON-NLS-1$
		}

	}

	public static CompletableFuture<Void> sendSimpleEmail(Email email, Account account, String password, int timeout, boolean async) throws ShareException {

		if (account != null && account.getSmtpConfiguration() != null && account.isValid() && password != null) {

			Mailer mailer = MailerBuilder
					.withSMTPServer(account.getSmtpConfiguration().getHost(), account.getSmtpConfiguration().getPort(), account.getEmail(), password)
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

				return mailer.sendMail(email, async);

			} catch (Exception e) {
				throw new ShareException("Could not send simple email (smtp)", e); //$NON-NLS-1$
			}

		} else {
			throw new ShareException("Could not send simple email (smtp), invalid arguments"); //$NON-NLS-1$
		}

	}

	public static String findImapSentFolderPath(Account account, String password, int timeout) throws ShareException {

		// Create a temporary configuration where we can store the String (we need final Object for inline method)
		ImapConfiguration conf = new ImapConfiguration();

		try {

			new ImapStoreOperation(account, password, timeout) {

				@Override
				protected void performOperation(Store store) throws ShareException {

					try {

						Folder[] folders = store.getPersonalNamespaces();
						outerLoop: for (Folder folder : folders) {

							String folderName = folder.getName();

							if (folderName != null && folderName.toLowerCase().contains("sent")) { //$NON-NLS-1$

								try {

									folder.open(Folder.READ_WRITE);
									folder.close();

									conf.setSentFolderPath(folderName);

									break outerLoop;

								} catch (Exception e) {
									Common.LOGGER.warn("Exception while finding imap sent folder path", e); //$NON-NLS-1$
								}

							} else {

								for (String sentFolderName : Imap.COMMON_SENT_FOLDER_NAMES) {

									try {

										Folder sentFolder = folder.getFolder(sentFolderName);
										if (sentFolder != null) {

											sentFolder.open(Folder.READ_WRITE);
											sentFolder.close();

											conf.setSentFolderPath(folderName + "/" + sentFolderName); //$NON-NLS-1$

											break outerLoop;

										}

									} catch (Exception e) {
										Common.LOGGER.warn("Exception while finding imap sent folder path", e); //$NON-NLS-1$
									}

								}

							}

						}

					} catch (Exception e) {
						throw new ShareException("Exception while finding imap sent folder path", e); //$NON-NLS-1$
					}

				}
			};

		} catch (Exception e) {
			throw new ShareException("Could not find imap sent folder path", e); //$NON-NLS-1$
		}

		return conf.getSentFolderPath();

	}

	public static void saveSimpleEmailToImapFolder(Email email, Account account, String password, int timeout) throws ShareException {

		if (email != null && account != null && account.getImapConfiguration() != null && account.getImapConfiguration().getSentFolderPath() != null && account.getImapConfiguration().getSentFolderPath().length() > 0) {

			try {

				new ImapStoreOperation(account, password, timeout) {

					@Override
					protected void performOperation(Store store) throws ShareException {

						String[] folderNames = account.getImapConfiguration().getSentFolderPath().split("/"); //$NON-NLS-1$
						if (folderNames.length > 0) {

							try {

								Folder folder = store.getFolder(folderNames[0]);

								for (int i = 1; i < folderNames.length; i++) {
									if (folder != null) {
										// TODO: Should we call close?
										folder = folder.getFolder(folderNames[i]);
									}
								}

								if (folder != null) {

									try {

										folder.open(Folder.READ_WRITE);

										MimeMessage message = EmailConverter.emailToMimeMessage(email);
										message.setFlag(Flags.Flag.SEEN, true);

										Message[] messages = new Message[1];
										messages[0] = message;

										folder.appendMessages(messages);

										folder.close();

									} catch (Exception e) {
										throw new ShareException("Could not append message to folder", e); //$NON-NLS-1$
									}

								} else {
									throw new ShareException("folder is null"); //$NON-NLS-1$
								}

							} catch (Exception e) {
								throw new ShareException("Could not get folder", e); //$NON-NLS-1$
							}

						}

					}
				};

			} catch (Exception e) {
				throw new ShareException("Could not save email to imap folder", e); //$NON-NLS-1$
			}

		} else {
			throw new ShareException("Invalid arguments passed"); //$NON-NLS-1$
		}

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
