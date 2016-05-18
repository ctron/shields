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
import java.util.Deque;

import javax.servlet.http.HttpServletRequest;

import okhttp3.Request;
import okhttp3.Response;

public interface ParseContext
{
    /**
     * Get the pre-parse path segments
     * <p>
     * The pre-parse path segments, without the leading servlet
     * context and shield provider id, may be an empty list but never
     * {@code null}
     * </p>
     *
     * @return the pre-parse path segments
     */
    public Deque<String> getPath ();

    /**
     * Get the original HTTP request
     *
     * @return the original request
     */
    public HttpServletRequest getRequest ();

    /**
     * Make a new HTTP call
     *
     * @param request
     *            the request to make
     * @return the result
     * @throws IOException
     *             in the case something goes wrong
     */
    public Response newCall ( Request request ) throws IOException;
}
