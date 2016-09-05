#include "main.h"

Renderer::Renderer(int width, int height) {
    this->width = width;
    this->height = height;
    
    pixels = new char[width * height * COLOR_CHANNELS];
    for (int i = 0; i < width * height * COLOR_CHANNELS; i++) {
        pixels[i] = 0;
    }
}

Renderer::~Renderer() {
    delete [] pixels;
}

void Renderer::clear() {
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            plot(i, j, COLOR_WHITE);
        }
    }
    
    for (int i = 0; i < width / GRID_SIZE; i++) {
        renderVector(Vector3(i * GRID_SIZE, 0, 0), Vector3(0, width, 0), COLOR_GREEN);
    }
    
    for (int i = 0; i < height / GRID_SIZE; i++) {
        renderVector(Vector3(0, i * GRID_SIZE, 0), Vector3(width, 0, 0), COLOR_GREEN);
    }
}

void Renderer::plot(int x, int y, int color) {
    if (x < SCREEN_WIDTH && x >= 0 &&
        y < SCREEN_HEIGHT && y >= 0) {
        pixels[(y * SCREEN_WIDTH + x) * 3 + 0] = color >> 16 & 255;
        pixels[(y * SCREEN_WIDTH + x) * 3 + 1] = color >> 8 & 255;
        pixels[(y * SCREEN_WIDTH + x) * 3 + 2] = color & 255;
    }
}

void Renderer::renderVector(Vector3 offset, Vector3 vec, int color) {
    if (vec.x >= 0) {
        for (int x = (int) offset.x; x < (int) (offset.x + vec.x); x++) {
            int y = (int) (offset.y + vec.y * (x - offset.x) / vec.x);
            
            plot(x, y, color);
        }
    } else {
        for (int x = (int) (offset.x + vec.x); x < (int) offset.x; x++) {
            int y = (int) (offset.y + vec.y * (x - offset.x) / vec.x);
            
            plot(x, y, color);
        }
    }
    
    if (vec.y >= 0) {
        for (int y = (int) offset.y; y < (int) (offset.y + vec.y); y++) {
            int x = (int) (offset.x + vec.x * (y - offset.y) / vec.y);
            
            plot(x, y, color);
        }
    } else {
        for (int y = (int) (offset.y + vec.y); y < (int) offset.y; y++) {
            int x = (int) (offset.x + vec.x * (y - offset.y) / vec.y);
            
            plot(x, y, color);
        }
    }
}

void Renderer::renderLine(Vector3 p1, Vector3 p2, int color) {
    renderVector(p1, p2 - p1, color);
}

Vector3 Renderer::getBezierCurve(Vector3 *p, double t) {
    // compute coefficients
    double k1 = (1 - t) * (1 - t) * (1 - t);
    double k2 = 3 * (1 - t) * (1 - t) * t;
    double k3 = 3 * (1 - t) * t * t;
    double k4 = t * t * t;
    
    // weight the four control points using coefficients
    return p[0] * k1 + p[1] * k2 + p[2] * k3 + p[3] * k4;
}

Vector3 Renderer::getBezierPatch(Vector3 *controlPoints, double u, double v) {
    Vector3 uCurve[4];
    
    for (int i = 0; i < 4; i++){
        uCurve[i] = getBezierCurve(controlPoints + (i * 4), u);
    }
    
    return getBezierCurve(uCurve, v);
}

void Renderer::renderMesh(Mesh *mesh) {
    int divs = 16; // Must be >= 16
    
    Vector3 *controlPoints = new Vector3[divs];
    Vector3 *resultVertices = new Vector3[(divs + 1) * (divs + 1)];
    int vertexIndex[divs * divs][4];
    
    for (int i = 0; i < mesh->getNumPatches(); i++) { // numTeapotPatches
        for (int j = 0; j < NUM_VERTICES_PER_PATCH; j++) {
            controlPoints[j] = mesh->vertices[mesh->patches[i][j] - 1];
        }
        
        for (int j = 0, k = 0; j < divs + 1; j++) {
            for (int l = 0; l < divs + 1; l++, k++) {
                resultVertices[k] = getBezierPatch(controlPoints, l / (double)divs, j / (double)divs);
            }
        }
        
        for (int j = 0, k = 0; j < divs; j++) {
            for (int l = 0; l < divs; l++, k++) {
                vertexIndex[k][0] = (divs + 1) * j + l;
                vertexIndex[k][1] = (divs + 1) * (j + 1) + l;
                vertexIndex[k][2] = (divs + 1) * (j + 1) + l + 1;
                vertexIndex[k][3] = (divs + 1) * j + l + 1;
            }
        }
        
        for (int j = 0; j < divs * divs; j++) { // divs * divs is the length of vertexIndex
            for (int k = 0; k < 4 - 1; k++) { // 4 is the length of vertexIndex[j]
                renderLine(resultVertices[vertexIndex[j][k]],
                           resultVertices[vertexIndex[j][k + 1]], COLOR_BLACK);
            }
        }
    }
    
    delete [] controlPoints;
    delete [] resultVertices;
}