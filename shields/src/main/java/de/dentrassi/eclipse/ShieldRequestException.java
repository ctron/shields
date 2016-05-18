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
package de.dentrassi.eclipse;

public class ShieldRequestException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final int httpResponseCode;

    public ShieldRequestException ( final String message, final int httpResponseCode )
    {
        super ( message );
        this.httpResponseCode = httpResponseCode;
    }

    public ShieldRequestException ( final Throwable e, final int httpResponseCode )
    {
        super ( e );
        this.httpResponseCode = httpResponseCode;
    }

    public int getHttpResponseCode ()
    {
        return this.httpResponseCode;
    }
}
