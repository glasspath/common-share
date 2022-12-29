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
package org.glasspath.common.share.uwp;

import java.io.File;

import javax.swing.JFrame;

import org.glasspath.common.Common;
import org.glasspath.common.share.ShareException;
import org.glasspath.common.share.ShareUtils;
import org.glasspath.common.share.mail.Mailable;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class UwpShareUtils {

	private UwpShareUtils() {

	}

	public static void showShareMenu(JFrame frame, Mailable mailable, String description, String assemblyResolvePath) throws ShareException {

		try {

			Common.LOGGER.info("Loading share-utils-interop.dll from: " + assemblyResolvePath);
			ShareUtilsInterop shareUtilsInterop = Native.load(assemblyResolvePath + "/share-utils-interop.dll", ShareUtilsInterop.class);

			Pointer componentPointer = Native.getComponentPointer(frame);
			long windowHandle = Pointer.nativeValue(componentPointer);

			Common.LOGGER.info("Setting assembly resolve path: " + assemblyResolvePath);
			System.out.println("SetAssemblyResolvePath: " + shareUtilsInterop.SetAssemblyResolvePath(assemblyResolvePath));

			String title = "";
			if (mailable.getSubject() != null) {
				title = mailable.getSubject();
			}

			String text = "";
			if (mailable.getText() != null) {
				text = mailable.getText();
			}

			String html = "";
			if (mailable.getHtml() != null && mailable.getHtml().length() > 0) {
				html = mailable.getHtml();
			}

			// TODO: Add all attachments
			String attachmentPath = ShareUtils.getFirstExistingFile(mailable.getAttachments(), false);
			if (description == null && attachmentPath != null) {
				description = new File(attachmentPath).getName();
			}

			if (attachmentPath == null) {
				attachmentPath = "";
			}

			if (description == null) {
				description = "";
			}

			Common.LOGGER.info("Showing UWP share menu");
			System.out.println(shareUtilsInterop.ShowEmailShareMenu(windowHandle, title, description, text, html, attachmentPath));

		} catch (UnsatisfiedLinkError e) {
			throw new ShareException("Could not show share menu", e);
		} catch (Exception e) {
			throw new ShareException("Could not show share menu", e);
		}

	}

}
