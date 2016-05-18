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

public class PlainProvider implements Provider
{

    @Override
    public ShieldRequest parse ( final ParseContext ctx ) throws ShieldRequestException
    {
        return new ShieldRequest ( ctx.getPath ().poll (), ctx.getPath ().poll () );
    }

}
