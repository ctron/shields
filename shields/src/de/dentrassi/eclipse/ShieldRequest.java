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

public class ShieldRequest
{
    private String module;

    private String qualifier;

    private Color colorScheme;

    public ShieldRequest ()
    {
    }

    public ShieldRequest ( final String module, final String qualifier )
    {
        this.module = module;
        this.qualifier = qualifier;
    }

    public String getModule ()
    {
        return this.module;
    }

    public void setModule ( final String module )
    {
        this.module = module;
    }

    public String getQualifier ()
    {
        return this.qualifier;
    }

    public void setQualifier ( final String qualifier )
    {
        this.qualifier = qualifier;
    }

    public Color getColorScheme ()
    {
        return this.colorScheme;
    }

    public void setColorScheme ( final Color colorScheme )
    {
        this.colorScheme = colorScheme;
    }

}
