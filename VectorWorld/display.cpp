#include "main.h"

Display::Display(int width, int height) {
    this->width = width;
    this->height = height;
    
    renderer = new Renderer(width, height);
    world = new World();
    
    previousTime = std::chrono::high_resolution_clock::now();
    currentTime = std::chrono::high_resolution_clock::now();
}

void Display::update() {
    previousTime = currentTime;
    currentTime = std::chrono::high_resolution_clock::now();
    
    Time::deltaTime = std::chrono::duration_cast<std::chrono::duration<double>>(currentTime - previousTime).count();
    
    renderer->clear();
    world->update(renderer);
}