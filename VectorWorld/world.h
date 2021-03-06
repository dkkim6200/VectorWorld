#ifndef WORLD_H
#define WORLD_H

class World {
protected:
    Vector3 *teapotVertices;
    int **teapotPatches;
    
    int numTeapotVertices;
    int numTeapotPatches;
    
    Mesh *teapotMesh;
    
    double rotation;
    Vector3 rotationAxis;
    double scale;
    Vector3 translation;
    
    Vector3 vertices[4];
    
public:
    World();
    ~World();
    
    void update(Renderer *renderer);
};

#endif
