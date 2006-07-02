/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey, Maxym Mykhalchuk, and Kim Bruning
               Home page: http://www.omegat.org/omegat/omegat.html
               Support center: http://groups.yahoo.com/group/OmegaT/

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
**************************************************************************/

package org.omegat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Utility class for copying untranslatable files.
 *
 * @author Keith Godfrey
 * @author Kim Bruning
 */
public class LFileCopy
{
    /** Copies one file. Creates directories on the path to dest if necessary. */
    public static void copy(String src, String dest) throws IOException
    {
        File ifp = new File(src);
        File ofp = new File(dest);
        copy(ifp,ofp);
    }
    
    /** Copies one file. Creates directories on the path to dest if necessary. */
    public static void copy(File src, File dest) throws IOException
    {
        if (!src.exists())
        {
            throw new IOException(
                    MessageFormat.format(OStrings.getString("LFC_ERROR_FILE_DOESNT_EXIST"), new Object[] {src.getAbsolutePath()}));
        }
        FileInputStream fis = new FileInputStream(src);
        dest.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(dest);
        byte [] b = new byte[1024];
        int readBytes;
        while ( (readBytes = fis.read(b)) > 0)
            fos.write(b, 0, readBytes);
        fis.close();
        fos.close();
    }
}
