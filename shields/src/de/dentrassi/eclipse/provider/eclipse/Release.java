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

import com.google.gson.annotations.JsonAdapter;

public class Release
{
    private String title;

    @JsonAdapter ( ValueAdapter.class )
    private String type;

    private Review review;

    public void setTitle ( final String title )
    {
        this.title = title;
    }

    public String getTitle ()
    {
        return this.title;
    }

    public void setReview ( final Review review )
    {
        this.review = review;
    }

    public Review getReview ()
    {
        return this.review;
    }

    public void setType ( final String type )
    {
        this.type = type;
    }

    public String getType ()
    {
        return this.type;
    }
}
