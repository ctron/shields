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

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.dentrassi.eclipse.Color;
import de.dentrassi.eclipse.render.Layouter.Layout;
import de.dentrassi.eclipse.render.Layouter.LayoutResult;
import de.dentrassi.eclipse.util.Helper;

public class SvgRenderer
{
    private static class Segment
    {
        private final String text;

        private final Color color;

        public Segment ( final String text, final Color color )
        {
            this.text = text;
            this.color = color;
        }

        public Color getColor ()
        {
            return this.color;
        }

        public String getText ()
        {
            return this.text;
        }
    }

    private final List<Segment> segments = new ArrayList<> ();

    private final float paddingW;

    private final float paddingH;

    private final float spacing;

    private final float arc = 5f;

    private final float shadowOffsetX = 1f;

    private final float shadowOffsetY = 1f;

    public SvgRenderer ( final float paddingW, final float paddingH, final float spacing )
    {
        this.paddingW = paddingW;
        this.paddingH = paddingH;
        this.spacing = spacing;
    }

    public void append ( String text, final Color color )
    {
        if ( text == null )
        {
            text = "";
        }
        this.segments.add ( new Segment ( text, color ) );
    }

    public void render ( final Writer out )
    {
        try
        {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
            final DocumentBuilder db = dbf.newDocumentBuilder ();
            final Document doc = db.newDocument ();

            performRender ( doc );

            final TransformerFactory transformerFactory = TransformerFactory.newInstance ();
            final Transformer transformer = transformerFactory.newTransformer ();

            transformer.transform ( new DOMSource ( doc ), new StreamResult ( out ) );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException ( e );
        }
    }

    private void performRender ( final Document doc )
    {
        final Element svg = doc.createElementNS ( "http://www.w3.org/2000/svg", "svg" );
        doc.appendChild ( svg );

        final Element defs = doc.createElement ( "defs" );
        svg.appendChild ( defs );

        final float fontSize = 11;

        final Layouter l = new Layouter ( this.paddingW, this.spacing, fontSize );
        for ( final Segment seg : this.segments )
        {
            l.add ( seg.getText () );
        }
        final LayoutResult lr = l.layout ();

        createBackground ( doc, svg, defs, lr );

        final Element textGroup = doc.createElement ( "g" );
        svg.appendChild ( textGroup );
        textGroup.setAttribute ( "text-anchor", "middle" );
        textGroup.setAttribute ( "font-family", "DejaVu Sans,Verdana,Geneva,sans-serif" );
        textGroup.setAttribute ( "font-size", "" + fontSize );

        final float offsetY = lr.getHeight () * 0.5f + this.paddingH;

        for ( final Layout layout : lr.getLayouts () )
        {
            createTextSegment ( doc, textGroup, offsetY, layout );
        }

        svg.setAttribute ( "width", Helper.toString ( lr.getWidth () ) );
        svg.setAttribute ( "height", Helper.toString ( lr.getHeight () + 2.0f * this.paddingH ) );
    }

    private void createTextSegment ( final Document doc, final Element textGroup, final float y, final Layout layout )
    {
        final float x = layout.getX () + layout.getWidth () * 0.5f;

        final Element textShadow = doc.createElement ( "text" );
        textShadow.setTextContent ( layout.getText () );
        textShadow.setAttribute ( "x", Helper.toString ( x + this.shadowOffsetX ) );
        textShadow.setAttribute ( "y", Helper.toString ( y + this.shadowOffsetY ) );
        textShadow.setAttribute ( "fill", "#000" );
        textShadow.setAttribute ( "fill-opacity", ".3" );
        textShadow.setAttribute ( "dominant-baseline", "middle" );
        // text.setAttribute ( "textLength", Float.toString ( layout.getWidth () ) );
        textGroup.appendChild ( textShadow );

        final Element text = doc.createElement ( "text" );
        text.setTextContent ( layout.getText () );
        text.setAttribute ( "x", Helper.toString ( x ) );
        text.setAttribute ( "y", Helper.toString ( y ) );
        text.setAttribute ( "fill", "#fff" );
        text.setAttribute ( "dominant-baseline", "middle" );
        // text.setAttribute ( "textLength", Float.toString ( layout.getWidth () ) );
        textGroup.appendChild ( text );
    }

    private void createBackground ( final Document doc, final Element svg, final Element defs, final LayoutResult lr )
    {
        final float height = lr.getHeight () + 2.0f * this.paddingH;

        final Element mask = doc.createElement ( "clipPath" );
        defs.appendChild ( mask );
        mask.setAttribute ( "id", "r" );

        final Element maskRect = doc.createElement ( "rect" );
        mask.appendChild ( maskRect );
        maskRect.setAttribute ( "width", Helper.toString ( lr.getWidth () ) );
        maskRect.setAttribute ( "height", Helper.toString ( height ) );
        maskRect.setAttribute ( "rx", Helper.toString ( this.arc ) );
        maskRect.setAttribute ( "ry", Helper.toString ( this.arc ) );

        // create overlay

        final Element gradient = doc.createElement ( "linearGradient" );
        defs.appendChild ( gradient );
        gradient.setAttribute ( "id", "o" );
        gradient.setAttribute ( "x1", "0" );
        gradient.setAttribute ( "x2", "0" );
        gradient.setAttribute ( "y1", "0" );
        gradient.setAttribute ( "y2", "1" );
        addStop ( gradient, 0f, "white", .3f );
        addStop ( gradient, .7f, "white", .1f );

        final Element backgroundGroup = doc.createElement ( "g" );
        svg.appendChild ( backgroundGroup );
        backgroundGroup.setAttribute ( "clip-path", "url(#r)" );

        int i = 0;
        for ( final Layout layout : lr.getLayouts () )
        {
            final Segment seg = this.segments.get ( i );

            final Element rect = doc.createElement ( "rect" );
            backgroundGroup.appendChild ( rect );

            rect.setAttribute ( "x", Helper.toString ( layout.getBoundingX () ) );
            rect.setAttribute ( "width", Helper.toString ( layout.getBoundingWidth () ) );
            rect.setAttribute ( "height", Helper.toString ( height ) );
            rect.setAttribute ( "fill", seg.getColor ().getColor () );

            i++;
        }

        final Element overlay = doc.createElement ( "rect" );
        backgroundGroup.appendChild ( overlay );
        overlay.setAttribute ( "width", Helper.toString ( lr.getWidth () ) );
        overlay.setAttribute ( "height", Helper.toString ( height ) );
        overlay.setAttribute ( "fill", "url(#o)" );
    }

    private Element addStop ( final Element gradient, final float f, final String color, final Float opacity )
    {
        final Element stop = gradient.getOwnerDocument ().createElement ( "stop" );
        gradient.appendChild ( stop );

        stop.setAttribute ( "offset", String.format ( "%f%%", f * 100.0f ) );
        stop.setAttribute ( "stop-color", color );
        if ( opacity != null )
        {
            stop.setAttribute ( "stop-opacity", Helper.toString ( opacity ) );
        }

        return stop;
    }

}
