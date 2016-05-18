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

import de.dentrassi.eclipse.ShieldRequest;
import de.dentrassi.eclipse.ShieldRequestException;

public interface Provider
{
    /**
     * Provide a shield request from an HTTP request, if possible
     *
     * @param path
     *            the pre-parse path segments, without the leading servlet
     *            context and shield provider id, may be an empty list but never
     *            {@code null}
     * @param request
     *            the original HTTP request
     * @return a shield request, or {@code null} in which case a 404 error will
     *         be returned
     * @throws ShieldRequestException
     *             if the provider is unable to create a shield request
     */
    public ShieldRequest parse ( ParseContext context ) throws ShieldRequestException;
}
