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
package de.dentrassi.eclipse.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.dentrassi.eclipse.Color;
import de.dentrassi.eclipse.ShieldRequest;
import de.dentrassi.eclipse.provider.ParseContext;
import de.dentrassi.eclipse.provider.PlainProvider;
import de.dentrassi.eclipse.provider.ProjectProvider;
import de.dentrassi.eclipse.provider.Provider;
import de.dentrassi.eclipse.render.SvgRenderer;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

@WebServlet ( "/" )
public class ShieldServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    private final Map<String, Provider> providers = new HashMap<> ();

    private OkHttpClient httpClient;

    @Override
    public void init () throws ServletException
    {
        this.providers.put ( "plain", new PlainProvider () );
        this.providers.put ( "project", new ProjectProvider () );

        this.httpClient = makeNewHttpClient ();
        super.init ();
    }

    private static OkHttpClient makeNewHttpClient ()
    {
        final Builder builder = new OkHttpClient.Builder ();
        builder.sslSocketFactory ( (SSLSocketFactory)SSLSocketFactory.getDefault () );
        return builder.build ();
    }

    @Override
    protected void doGet ( final HttpServletRequest request, final HttpServletResponse response ) throws ServletException, IOException
    {
        final ShieldRequest sr = makeRequest ( request );
        if ( sr == null )
        {
            response.sendError ( HttpServletResponse.SC_NOT_FOUND, "Unknown provider" );
            return;
        }

        renderRequest ( sr, response );
    }

    private ShieldRequest makeRequest ( final HttpServletRequest request ) throws ServletException
    {
        String path;

        final String ctxPath = request.getContextPath ();
        if ( ctxPath != null )
        {
            final String req = request.getRequestURI ();
            if ( !req.startsWith ( ctxPath ) )
            {
                return null;
            }
            path = req.substring ( ctxPath.length () );
        }
        else
        {
            path = request.getRequestURI ();
        }

        path = path.replaceFirst ( "\\/+", "" );
        final LinkedList<String> segs = new LinkedList<> ();
        for ( final String seg : path.split ( "\\/+" ) )
        {
            try
            {
                segs.add ( URLDecoder.decode ( seg, "UTF-8" ) );
            }
            catch ( final UnsupportedEncodingException e )
            {
                throw new ServletException ( e );
            }
        }
        if ( segs.isEmpty () )
        {
            return null;
        }

        final String providerId = segs.pollFirst ();
        final Provider provider = this.providers.get ( providerId );
        if ( provider == null )
        {
            return null;
        }

        return provider.parse ( makeNewContext ( segs, request ) );
    }

    private ParseContext makeNewContext ( final LinkedList<String> path, final HttpServletRequest request )
    {
        return new ParseContext () {

            @Override
            public Response newCall ( final Request request ) throws IOException
            {
                return ShieldServlet.this.httpClient.newCall ( request ).execute ();
            }

            @Override
            public HttpServletRequest getRequest ()
            {
                return request;
            }

            @Override
            public Deque<String> getPath ()
            {
                return path;
            }
        };
    }

    protected void renderRequest ( final ShieldRequest request, final HttpServletResponse response ) throws IOException
    {
        response.setContentType ( "image/svg+xml" );

        final SvgRenderer segRenderer = new SvgRenderer ( 7, 3, 5 );
        segRenderer.append ( request.getModule (), Color.GRAY );
        segRenderer.append ( request.getQualifier (), request.getColorScheme () == null ? Color.GREEN : request.getColorScheme () );
        segRenderer.render ( response.getWriter () );
    }

}
