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

int Mesh::getNumVertices() {
    return numVertices;
}

int Mesh::getNumPatches() {
    return numPatches;
}