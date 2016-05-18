/*******************************************************************************
 * Copyright (c) 2016 Jens Reimann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package de.dentrassi.eclipse.provider;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.dentrassi.eclipse.ShieldRequest;
import de.dentrassi.eclipse.ShieldRequestException;
import de.dentrassi.eclipse.provider.eclipse.Locator;
import de.dentrassi.eclipse.provider.eclipse.Project;
import de.dentrassi.eclipse.provider.eclipse.ProjectsInformation;
import de.dentrassi.eclipse.provider.eclipse.Release;
import okhttp3.Request;
import okhttp3.Response;

public class ProjectProvider implements Provider
{
    @Override
    public ShieldRequest parse ( final ParseContext ctx ) throws ShieldRequestException
    {
        final String projectId = ctx.getPath ().pop ();

        try
        {
            final Project project = fetchProjectInformation ( ctx, projectId );
            if ( project == null )
            {
                return null;
            }

            return map ( project );
        }
        catch ( final IOException e )
        {
            throw new ShieldRequestException ( e, HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
    }

    private ShieldRequest map ( final Project project )
    {
        final Release release = Locator.latestRelease ( project );
        if ( release != null )
        {
            return new ShieldRequest ( project.getTitle (), release.getTitle () );
        }
        return new ShieldRequest ( project.getTitle (), null );
    }

    /**
     * Fetch project information from the PMI
     *
     * @param ctx
     *            the parse context
     * @param projectId
     *            the project ID
     * @return the project information, or {@code null} if the project could not
     *         be found
     * @throws IOException
     *             in case of any IO error
     */
    protected static Project fetchProjectInformation ( final ParseContext ctx, final String projectId ) throws IOException
    {
        final Request.Builder req = new Request.Builder ();
        req.url ( "https://projects.eclipse.org/json/project/" + UrlEscapers.urlPathSegmentEscaper ().escape ( projectId ) );

        final Response result = ctx.newCall ( req.build () );

        if ( !result.isSuccessful () )
        {
            if ( result.code () == 404 )
            {
                return null;
            }

            throw new ShieldRequestException ( result.message (), result.code () );
        }

        final GsonBuilder gb = new GsonBuilder ();
        final Gson gson = gb.create ();
        final ProjectsInformation projects = gson.fromJson ( result.body ().charStream (), ProjectsInformation.class );
        if ( projects.getProjects () == null )
        {
            return null;
        }
        return projects.getProjects ().get ( projectId );
    }

}
