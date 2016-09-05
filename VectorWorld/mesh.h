#ifndef MESH_H
#define MESH_H

class Mesh {
public:
    Vector3 *vertices;
    int **patches;
    
    int numVertices;
    int numPatches;
    
    Mesh();
    Mesh(int numVertices, Vector3 *vertices, int numPatches, int **patches);
    
    int getNumVertices();
    int getNumPatches();
};

#endif