#ifndef RENDERER_H
#define RENDERER_H

class Renderer {
private:
    bool isPointOnLine(Vector3 p, Vector3 p1, Vector3 p2);
    
public:
    int width;
    int height;
    
    char *pixels;
    
    Renderer(int width, int height);
    ~Renderer();
    
    void clear();
    void plot(int x, int y, int color);
    
    /**
     * Renders vector on the screen.
     *
     * <p>
     * The formula is same as below:
     * r = offset + (0 ~ 1)vec
     * </p>
     *
     * @param offset Position vector that the vector will be drawn from
     * @param vec Direction vector(?) to be drawn
     * @param color HTML color value
     * @return No return value
     */
    void renderVector(Vector3 offset, Vector3 vec, int color);
    
    /**
     * Renders line on the screen.
     *
     * <p>
     * This is a wrapper function of Renderer::renderVector(Vector3, Vector3, int).
     * </p>
     *
     * @param p1 Point where the line starts from
     * @param p2 Point where the line ends
     * @param color HTML color value
     * @return No return value
     */
    void renderLine(Vector3 p1, Vector3 p2, int color);
    
    /**
     * Evaluates the point on Bezier curve with parameter (t)
     *
     * <p>
     * Source
     * 1. https://www.scratchapixel.com/lessons/advanced-rendering/bezier-curve-rendering-utah-teapot/bezier-curve
     * </p>
     *
     * @param p Array of Bezier control points
     * @param t t variable contained in range [0, 1]
     * @return Vector of point on Bezier curve with parameter (t)
     */
    Vector3 getBezierCurve(Vector3 *p, double t);
    
    /**
     * Evaluates the point on Bezier patch with parameter (u, v)
     *
     * <p>
     * Source
     * 1. https://www.scratchapixel.com/lessons/advanced-rendering/bezier-curve-rendering-utah-teapot/bezier-surface
     * </p>
     *
     * @param controlPoints Bezier control points
     * @param u u variable contained in range [0, 1]
     * @param v v variable contained in range [0, 1]
     * @return Vector of point on Bezier patch with parameter (u, v)
     */
    Vector3 getBezierPatch(Vector3 *controlPoints, double u, double v);
    
    /**
     * Draws 3D object from its vertices and Bezier patches.
     *
     * <p>
     * Source
     * 1. https://www.scratchapixel.com/lessons/advanced-rendering/bezier-curve-rendering-utah-teapot/bezier-surface
     * </p>
     * @param objVertices 3D object's vertices
     * @param objPatches 3D object's bezier patches
     */
    void renderMesh(Mesh *mesh);
    
    /**
     * TODO: render filled polygon
     *
     */
    void renderPolygon(Vector3 *vertices, int numVertices, int yMin, int yMax);
};

#endif