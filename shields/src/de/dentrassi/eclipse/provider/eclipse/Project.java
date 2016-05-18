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
package de.dentrassi.eclipse.provider.eclipse;

import java.util.LinkedList;
import java.util.List;

public class Project
{
    private String title;

    private List<Release> releases = new LinkedList<> ();

    public void setTitle ( final String title )
    {
        this.title = title;
    }

    public String getTitle ()
    {
        return this.title;
    }

    public List<Release> getReleases ()
    {
        return this.releases;
    }

    public void setReleases ( final List<Release> releases )
    {
        this.releases = releases;
    }
}
