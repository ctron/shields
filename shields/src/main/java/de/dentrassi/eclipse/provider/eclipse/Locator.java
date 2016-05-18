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

public final class Locator
{
    private Locator ()
    {
    }

    /**
     * Find the latest release
     *
     * @param project
     *            the project to work on
     * @return the latest release, or {@code null}
     */
    public static Release latestRelease ( final Project project )
    {
        for ( final Release release : project.getReleases () )
        {
            if ( "3".equals ( release.getType () ) )
            {
                // no review needed
                return release;
            }

            if ( release.getReview () == null )
            {
                continue;
            }

            if ( "success".equals ( release.getReview ().getType () ) )
            {
                return release;
            }
        }

        return null;
    }
}
