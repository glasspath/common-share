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

import javax.swing.JFrame;

import org.glasspath.common.Common;
import org.glasspath.common.share.mail.Mailable;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class UwpShareUtils {

	private UwpShareUtils() {

	}

	public static void showShareMenu(JFrame frame, Mailable mailable, String assemblyResolvePath) {

		try {

			// assemblyResolvePath = "C:\\project\\invoice\\eclipse workspace\\revenue\\revenue-main\\target\\jpackage-output\\Revenue\\app";
			// assemblyResolvePath = "C:\\project\\invoice\\eclipse workspace\\Tests";

			Common.LOGGER.info("Loading share-utils-interop.dll from: " + assemblyResolvePath);
			ShareUtilsInterop shareUtilsInterop = Native.load(assemblyResolvePath + "/share-utils-interop.dll", ShareUtilsInterop.class);

			Pointer componentPointer = Native.getComponentPointer(frame);

			// assemblyResolvePath = assemblyResolvePath.replace("/", "\\");
			Common.LOGGER.info("Setting assembly resolve path: " + assemblyResolvePath);
			System.out.println("SetAssemblyResolvePath: " + shareUtilsInterop.SetAssemblyResolvePath(assemblyResolvePath));

			/*
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			*/

			// TODO
			Common.LOGGER.info("Showing UWP share menu");
			System.out.println(shareUtilsInterop.ShowEmailShareMenu(Pointer.nativeValue(componentPointer), mailable.getSubject(), "TestDescription", mailable.getText(), mailable.getHtml(), "C:\\temp\\test.txt"));

		} catch (Exception e) {
			Common.LOGGER.error("Exception while showing UWP share menu: " + e);
		}

	}

}
