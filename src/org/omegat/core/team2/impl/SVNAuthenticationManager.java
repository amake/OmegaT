/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2015 Alex Buloichik
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package org.omegat.core.team2.impl;

import java.util.logging.Logger;

import javax.net.ssl.TrustManager;

import org.omegat.core.Core;
import org.omegat.core.team2.TeamSettings;
import org.omegat.util.Log;
import org.omegat.util.OStrings;
import org.omegat.util.gui.DockingUI;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationProvider;
import org.tmatesoft.svn.core.auth.ISVNProxyManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.auth.SVNPasswordAuthentication;
import org.tmatesoft.svn.core.auth.SVNSSHAuthentication;
import org.tmatesoft.svn.core.auth.SVNUserNameAuthentication;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * Authentication manager for SVN. See details about authentication at the
 * http://wiki.svnkit.com/Authentication.
 * 
 * Only username+password authentication supported. Proxy not supported for https:// repositories.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class SVNAuthenticationManager implements ISVNAuthenticationManager {
    static final int CONNECT_TIMEOUT = 30 * 1000; // 30 seconds
    static final int READ_TIMEOUT = 60 * 1000; // 60 seconds
    static final String KEY_USERNAME_PREFIX = "login.username.";
    static final String KEY_PASSWORD_PREFIX = "login.password.";

    private static final Logger LOGGER = Logger.getLogger(SVNAuthenticationManager.class.getName());

    @Override
    public void acknowledgeAuthentication(boolean accepted, String kind, String realm,
            SVNErrorMessage errorMessage, SVNAuthentication authentication) throws SVNException {
        if (!accepted) {
            Log.logDebug(LOGGER, "SVN authentication error: {0}", errorMessage);
        }
    }

    @Override
    public void acknowledgeTrustManager(TrustManager manager) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getConnectTimeout(SVNRepository repository) {
        return CONNECT_TIMEOUT;
    }

    @Override
    public int getReadTimeout(SVNRepository repository) {
        return READ_TIMEOUT;
    }

    protected SVNAuthentication ask(String kind, SVNURL url, String message) throws SVNException {
        if (ISVNAuthenticationManager.PASSWORD.equals(kind)) {
            // ask username+password
        } else if (ISVNAuthenticationManager.SSH.equals(kind)) {
            // ask username+password
        } else if (ISVNAuthenticationManager.USERNAME.equals(kind)) {
            // user auth shouldn't be null.
            return new SVNUserNameAuthentication("", false, url, false);
        } else {
            // auth type not supported for OmegaT
            throw new SVNException(SVNErrorMessage.create(SVNErrorCode.RA_UNKNOWN_AUTH));
        }

        SVNUserPassDialog userPassDialog = new SVNUserPassDialog(Core.getMainWindow().getApplicationFrame());
        DockingUI.displayCentered(userPassDialog);
        userPassDialog.descriptionTextArea.setText(message);
        userPassDialog.cbSavePlainPassword.setSelected(true);
        userPassDialog.setVisible(true);
        if (userPassDialog.getReturnStatus() != SVNUserPassDialog.RET_OK) {
            return null;
        }

        String user = userPassDialog.userText.getText();
        String pass = new String(userPassDialog.passwordField.getPassword());

        if (userPassDialog.cbSavePlainPassword.isSelected()) {
            TeamSettings.set(KEY_USERNAME_PREFIX + url.toString(), user);
            TeamSettings.set(KEY_PASSWORD_PREFIX + url.toString(), pass);
        } else {
            TeamSettings.set(KEY_USERNAME_PREFIX + url.toString(), null);
            TeamSettings.set(KEY_PASSWORD_PREFIX + url.toString(), null);
        }

        if (ISVNAuthenticationManager.PASSWORD.equals(kind)) {
            return new SVNPasswordAuthentication(user, pass, false, url, false);
        } else if (ISVNAuthenticationManager.SSH.equals(kind)) {
            return new SVNSSHAuthentication(user, pass, -1, false, url, false);
        } else {
            // auth type not supported for OmegaT
            throw new SVNException(SVNErrorMessage.create(SVNErrorCode.RA_UNKNOWN_AUTH));
        }
    }

    @Override
    public SVNAuthentication getFirstAuthentication(String kind, String realm, SVNURL url)
            throws SVNException {
        String user = TeamSettings.get(KEY_USERNAME_PREFIX + url.toString());
        String pass = TeamSettings.get(KEY_PASSWORD_PREFIX + url.toString());
        if (user != null && pass != null) {
            if (ISVNAuthenticationManager.PASSWORD.equals(kind)) {
                return new SVNPasswordAuthentication(user, pass, false, url, false);
            } else if (ISVNAuthenticationManager.SSH.equals(kind)) {
                return new SVNSSHAuthentication(user, pass, -1, false, url, false);
            }
        }
        return ask(kind, url, OStrings.getString("TEAM_USERPASS_FIRST"));
    }

    public SVNAuthentication getNextAuthentication(String kind, String realm, SVNURL url) throws SVNException {
        return ask(kind, url, OStrings.getString("TEAM_USERPASS_WRONG"));
    };

    @Override
    public ISVNProxyManager getProxyManager(SVNURL url) throws SVNException {
        return NO_PROXY;
    }

    @Override
    public TrustManager getTrustManager(SVNURL url) throws SVNException {
        return null;
    }

    @Override
    public boolean isAuthenticationForced() {
        return false;
    }

    @Override
    public void setAuthenticationProvider(ISVNAuthenticationProvider provider) {
        throw new UnsupportedOperationException();
    }

    ISVNProxyManager NO_PROXY = new ISVNProxyManager() {
        public String getProxyHost() {
            return null;
        }

        public String getProxyPassword() {
            return null;
        }

        public int getProxyPort() {
            return -1;
        }

        public String getProxyUserName() {
            return null;
        }

        public void acknowledgeProxyContext(boolean accepted, SVNErrorMessage errorMessage) {
        }
    };
}
