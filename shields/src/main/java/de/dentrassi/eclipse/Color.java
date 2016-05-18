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

public enum Color
{
    GRAY ( "#555" ),
    GREEN ( "#61b700" );

    private String color;

    private Color ( final String color )
    {
        this.color = color;
    }

    public String getColor ()
    {
        return this.color;
    }

}
