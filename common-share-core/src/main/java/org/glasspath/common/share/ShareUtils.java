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
package org.glasspath.common.share;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShareUtils {

	private ShareUtils() {

	}

	public static String getFirstExistingFile(List<String> filePaths, boolean dirAllowed) {

		if (filePaths != null) {

			for (String filePath : filePaths) {

				if (filePath.length() > 0) {

					try {

						File file = new File(filePath);
						if (file.exists() && (dirAllowed || !file.isDirectory())) {
							return filePath;
						}

					} catch (Exception e) {
						// TODO?
					}

				}

			}

		}

		return null;

	}

	public static List<File> findDirectories(File parentDir, String containingText) {

		List<File> dirs = new ArrayList<>();

		try {

			if (parentDir != null && parentDir.exists() && parentDir.isDirectory()) {

				containingText = containingText.toLowerCase();

				File[] files = parentDir.listFiles();
				for (File file : files) {

					if (file.isDirectory() && file.getName().toLowerCase().contains(containingText)) {
						dirs.add(file);
					}

				}

			}

		} catch (Exception e) {
			// TODO?
		}

		return dirs;

	}

}
