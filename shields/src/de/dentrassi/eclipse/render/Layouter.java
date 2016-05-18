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
package de.dentrassi.eclipse.render;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Layouter
{
    private static final FontRenderContext FRC = new FontRenderContext ( null, false, true );

    private static final float FONT_SIZE_FACTOR = Float.parseFloat ( System.getProperty ( "shields.fontSizeFactor", "1.1" ) );

    public static class Layout
    {
        private final String text;

        private final float x;

        private final float width;

        private final float boundingX;

        private final float boundingWidth;

        private Layout ( final String text, final float x, final float width, final float boundingX, final float boundingWidth )
        {
            this.text = text;
            this.x = x;
            this.width = width;
            this.boundingX = boundingX;
            this.boundingWidth = boundingWidth;
        }

        public String getText ()
        {
            return this.text;
        }

        public float getX ()
        {
            return this.x;
        }

        public float getBoundingX ()
        {
            return this.boundingX;
        }

        public float getWidth ()
        {
            return this.width;
        }

        public float getBoundingWidth ()
        {
            return this.boundingWidth;
        }

        @Override
        public String toString ()
        {
            return String.format ( "[X: %s, W: %s, BX: %s, BW: %s]", this.x, this.width, this.boundingX, this.boundingWidth );
        }
    }

    public static class LayoutResult
    {
        private final List<Layout> layouts;

        private final float height;

        private final float ascent;

        private final float descent;

        private final float width;

        private LayoutResult ( final List<Layout> layouts, final float height, final float ascent, final float descent, final float width )
        {
            this.layouts = layouts;
            this.height = height;
            this.ascent = ascent;
            this.descent = descent;
            this.width = width;
        }

        public List<Layout> getLayouts ()
        {
            return this.layouts;
        }

        public float getHeight ()
        {
            return this.height;
        }

        public float getAscent ()
        {
            return this.ascent;
        }

        public float getDescent ()
        {
            return this.descent;
        }

        public float getWidth ()
        {
            return this.width;
        }
    }

    private final float padding;

    private final Font font;

    private final float spacing;

    private final List<String> texts = new LinkedList<> ();

    private final StringBuilder fullText = new StringBuilder ();

    public Layouter ( final float padding, final float spacing, final float fontSize )
    {
        this.padding = padding;
        this.spacing = spacing;
        this.font = loadFont ( fontSize );
    }

    private static Font loadFont ( final float fontSize )
    {
        try ( InputStream in = Layouter.class.getClassLoader ().getResourceAsStream ( "META-INF/font/OpenSans-Semibold.ttf" ) )
        {
            final Map<TextAttribute, Object> attrs = new HashMap<> ();
            attrs.put ( TextAttribute.SIZE, fontSize );
            attrs.put ( TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD );
            return Font.createFont ( Font.TRUETYPE_FONT, in ).deriveFont ( attrs );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException ( e );
        }
    }

    public void add ( final String text )
    {
        this.fullText.append ( text );
        this.texts.add ( text );
    }

    public LayoutResult layout ()
    {
        final LineMetrics lm = this.font.getLineMetrics ( this.fullText.toString (), FRC );

        final List<Layout> layouts = new ArrayList<> ( this.texts.size () );

        float currentX = 0.0f;
        final Iterator<String> i = this.texts.iterator ();

        while ( i.hasNext () )
        {
            final String text = i.next ();
            final Rectangle2D sb = this.font.getStringBounds ( text, FRC );

            final float startPadding = currentX == 0.0 ? this.padding : this.spacing;
            final float endPadding = i.hasNext () ? this.spacing : this.padding;

            final float width = (float)sb.getWidth () * FONT_SIZE_FACTOR;

            final Layout layout = new Layout ( text, currentX + startPadding, width, currentX, width + startPadding + endPadding );
            layouts.add ( layout );

            currentX += startPadding + width + endPadding;
        }

        return new LayoutResult ( layouts, lm.getHeight (), lm.getAscent (), lm.getDescent (), currentX );
    }

}
