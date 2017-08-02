package com.geocreator;

import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * Serializable container used to store only needed data from {@link GeometricCanvas}.
 * It stores list of {@link PolyShape} and next free ID.
 * Used because we don't have to serialize whole GeometricCanvas
 * @see GeometricCanvas
 * @see PolyShape
 * @see Serializable
 */
public class GeometricCanvasSavedState implements Serializable{
    private static final long serialVersionUID = -7377550906292536893L;
    public LinkedList<PolyShape> polyShapes;   // holds all shapes to render
    public long nextFreePolyShapeID;    // next ID to be assigned to newly created PolyShape

    public GeometricCanvasSavedState(LinkedList<PolyShape> polyShapes, long nextFreePolyShapeID) {
        this.polyShapes = polyShapes;
        for (PolyShape pls : polyShapes) {
            pls.setActive(false);
        }
        this.nextFreePolyShapeID = nextFreePolyShapeID;
    }
}
