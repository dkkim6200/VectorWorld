#include "main.h"

World::World() {
    ifstream teapotFile;
    teapotFile.open("/Users/DaekunKim/Documents/Programming Related/VectorWorld/VectorWorld/assets/teapot");
    
    string s;
    getline(teapotFile, s);
    numTeapotPatches = atoi(s.c_str());
    teapotPatches = new int*[numTeapotPatches];
    
    for (int i = 0; i < numTeapotPatches; i++) {
        teapotPatches[i] = new int[NUM_VERTICES_PER_PATCH];
        
        getline(teapotFile, s);
        stringstream ss(s);
        
        string token;
        for (int j = 0; getline(ss, token, ','); j++) {
            // Putting getline in the incremental side will do no good, since it will only get executed when the code block below is done executing, which means that the initial value of 'token' would be uninitialized, so atoi() will return 0 or other jiberish.
            teapotPatches[i][j] = atoi(token.c_str());
        }
    }
    
    getline(teapotFile, s);
    numTeapotVertices = atoi(s.c_str());
    teapotVertices = new Vector3[numTeapotVertices];
    
    for (int i = 0; i < numTeapotVertices; i++) {
        getline(teapotFile, s);
        stringstream ss(s);
        
        string token;
        
        getline(ss, token, ',');
        teapotVertices[i].x = atof(token.c_str());
        getline(ss, token, ',');
        teapotVertices[i].y = atof(token.c_str());
        getline(ss, token, ',');
        teapotVertices[i].z = atof(token.c_str());
    }
    
    teapotMesh = new Mesh(numTeapotVertices, teapotVertices, numTeapotPatches, teapotPatches);
    
    for (int i = 0; i < numTeapotPatches; i++) {
        for (int j = 0; j < NUM_VERTICES_PER_PATCH; j++) {
            cout << teapotPatches[i][j] << " ";
        }
        cout << endl;
    }
    
    rotation = 0;
    rotationAxis = Vector3(1, 1, 1);
    scale = 100;
    translation = Vector3(200, 200, 0);
}

World::~World() {
}

void World::update(Renderer *renderer) {
    Vector3 *manipulatedVertices = new Vector3[teapotMesh->getNumVertices()];
    
    for (int i = 0; i < teapotMesh->getNumVertices(); i++) {
        manipulatedVertices[i] = teapotMesh->vertices[i];
        manipulatedVertices[i] = manipulatedVertices[i] * scale;
        manipulatedVertices[i].rotate(Vector3(1, 0, 0), 270);
        manipulatedVertices[i].rotate(rotationAxis, rotation);
        manipulatedVertices[i] = manipulatedVertices[i] + translation;
    }
    
    Mesh resultMesh = Mesh(teapotMesh->getNumVertices(), manipulatedVertices, teapotMesh->getNumPatches(), teapotPatches);
    
    renderer->renderMesh(&resultMesh);
    rotation += 90.0 * Time::deltaTime;

//    scale += 0.1;
    
    delete [] manipulatedVertices;
}