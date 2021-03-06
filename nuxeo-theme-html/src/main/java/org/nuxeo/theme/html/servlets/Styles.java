/*
 * (C) Copyright 2006-2007 Nuxeo SAS <http://nuxeo.com> and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jean-Marc Orliaguet, Chalmers
 *
 * $Id$
 */

package org.nuxeo.theme.html.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.web.common.requestcontroller.filter.BufferingServletOutputStream;
import org.nuxeo.theme.ApplicationType;
import org.nuxeo.theme.Manager;
import org.nuxeo.theme.html.Utils;
import org.nuxeo.theme.html.ui.ThemeStyles;
import org.nuxeo.theme.themes.ThemeDescriptor;
import org.nuxeo.theme.themes.ThemeManager;
import org.nuxeo.theme.types.TypeFamily;

public final class Styles extends HttpServlet implements Serializable {

    private static final Log log = LogFactory.getLog(Styles.class);

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {

        // headers
        response.addHeader("content-type", "text/css");

        final String themeName = request.getParameter("theme");
        if (themeName == null) {
            response.sendError(404);
            log.error("Theme name not set");
            return;
        }

        final String collectionName = request.getParameter("collection");

        // Load theme if needed
        ThemeDescriptor themeDescriptor = ThemeManager.getThemeDescriptorByThemeName(themeName);
        if (themeDescriptor == null) {
            throw new IOException("Theme not found: " + themeName);
        }
        if (!themeDescriptor.isLoaded()) {
            ThemeManager.loadTheme(themeDescriptor);
        }

        // cache control
        final String applicationPath = request.getParameter("path");
        if (applicationPath != null) {
            final ApplicationType application = (ApplicationType) Manager.getTypeRegistry().lookup(
                    TypeFamily.APPLICATION, applicationPath);
            if (application != null) {
                Utils.setCacheHeaders(response, application.getStyleCaching());
            }
        }

        // compression
        OutputStream os = response.getOutputStream();
        BufferingServletOutputStream.stopBuffering(os);
        if (Utils.supportsGzip(request)) {
            response.setHeader("Content-Encoding", "gzip");
            // Needed by proxy servers
            response.setHeader("Vary", "Accept-Encoding");
            os = new GZIPOutputStream(os);
        }

        final String basePath = request.getParameter("basepath");

        final ThemeManager themeManager = Manager.getThemeManager();
        String rendered = themeManager.getCachedStyles(themeName, basePath,
                collectionName);

        if (rendered == null) {
            rendered = ThemeStyles.generateThemeStyles(themeName,
                    themeDescriptor, basePath, collectionName, true);
            themeManager.setCachedStyles(themeName, basePath, collectionName,
                    rendered);
        }

        os.write(rendered.getBytes());
        os.close();
    }
}
