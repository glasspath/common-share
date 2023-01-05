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

		ShareException exception = null;
		String sentFolderPath = null;

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

										Folder[] folders = store.getPersonalNamespaces();
										for (Folder folder : folders) {

											String folderName = folder.getName();

											if (folderName != null && folderName.toLowerCase().contains("sent")) { //$NON-NLS-1$

												try {

													folder.open(Folder.READ_WRITE);
													folder.close();

													sentFolderPath = folderName;

													break;

												} catch (Exception e) {
													e.printStackTrace();
												}

											} else {

												try {

													Folder sentFolder = folder.getFolder("Sent"); //$NON-NLS-1$
													if (sentFolder != null) {

														sentFolder.open(Folder.READ_WRITE);
														sentFolder.close();

														sentFolderPath = folderName + "/Sent"; //$NON-NLS-1$

														break;

													}

												} catch (Exception e) {
													e.printStackTrace();
												}

												try {

													Folder sentFolder = folder.getFolder("Sent Items"); //$NON-NLS-1$
													if (sentFolder != null) {

														sentFolder.open(Folder.READ_WRITE);
														sentFolder.close();

														sentFolderPath = folderName + "/Sent Items"; //$NON-NLS-1$

														break;

													}

												} catch (Exception e) {
													e.printStackTrace();
												}

											}

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

		return sentFolderPath;

	}

	public static void saveSimpleEmailToImapFolder(Email email, Account account, String password, int timeout) throws ShareException {

		ShareException exception = null;

		if (email != null && account != null && account.getSmtpConfiguration() != null && account.getImapConfiguration() != null && account.isValid() && password != null) {

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

										String[] folderNames = account.getImapConfiguration().getSentFolderPath().split("/");
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
														exception = new ShareException("Could not append message to folder", e); //$NON-NLS-1$
													}

												} else {
													exception = new ShareException("folder is null"); //$NON-NLS-1$
												}

											} catch (Exception e) {
												exception = new ShareException("Could not get folder", e); //$NON-NLS-1$
											}

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
