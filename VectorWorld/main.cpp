#ifdef WIN32
#include <windows.h>
#endif

#ifdef __APPLE__
#include <GLUT/glut.h>
#include <OpenGL/gl.h>
#include <OpenGL/glu.h>
#else
#include <GL/glut.h>
#include <GL/glu.h>
#include <GL/gl.h>
#endif

#include "main.h"

static int window = 0;

static Display *display;

void displayUpdate() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    display->update();
    glDrawPixels(SCREEN_WIDTH, SCREEN_HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, (void *) display->renderer->pixels);
    
    glutSwapBuffers();
}

void idle() {
    glutPostRedisplay();
}

void CreateGlutWindow() {
    glutInitDisplayMode (GLUT_DOUBLE | GLUT_RGBA);
    glutInitWindowPosition (10, 10);
    glutInitWindowSize (SCREEN_WIDTH, SCREEN_HEIGHT);
    window = glutCreateWindow ("VectorWorld");
}

void CreateGlutCallbacks() {
    glutDisplayFunc(displayUpdate);
    glutIdleFunc(idle);
}

void InitOpenGL() {
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
}

int main (int argc, char **argv)
{
    glutInit(&argc, argv);
    
    CreateGlutWindow();
    CreateGlutCallbacks();
    InitOpenGL();
    
    Time::deltaTime = 0;
    
    display = new Display(SCREEN_WIDTH, SCREEN_HEIGHT);
    
    glutMainLoop();
    
    return 0;		
}
