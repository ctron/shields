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

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class ValueAdapter extends TypeAdapter<String>
{

    @Override
    public void write ( final JsonWriter out, final String value ) throws IOException
    {
        if ( value != null )
        {
            out.beginArray ();
            out.beginObject ();
            out.value ( value );
            out.endObject ();
            out.endArray ();
        }
    }

    @Override
    public String read ( final JsonReader in ) throws IOException
    {
        if ( in.peek () == JsonToken.NULL )
        {
            return null;
        }

        String result = null;
        in.beginArray ();
        while ( in.hasNext () )
        {
            in.beginObject ();
            while ( in.hasNext () )
            {
                final String name = in.nextName ();
                if ( "value".equals ( name ) )
                {
                    result = in.nextString ();
                }
                else
                {
                    in.skipValue ();
                }
            }
            in.endObject ();
        }
        in.endArray ();

        return result;
    }

}
