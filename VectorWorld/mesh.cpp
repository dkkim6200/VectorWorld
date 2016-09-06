#include "main.h"

Mesh::Mesh() {
    vertices = NULL;
    patches = NULL;
}

Mesh::Mesh(int numVertices, Vector3 *vertices, int numPatches, int **patches) {
    this->numVertices = numVertices;
    this->vertices = vertices;
    
    this->numPatches = numPatches;
    this->patches = patches;
}

Mesh::~Mesh() {
    delete [] vertices;
    vertices = NULL;
    
    for (int i = 0; i < numPatches; i++) {
        delete [] patches[i];
    }
    delete [] patches;
    patches = NULL;
}

int Mesh::getNumVertices() {
    return numVertices;
}

int Mesh::getNumPatches() {
    return numPatches;
}