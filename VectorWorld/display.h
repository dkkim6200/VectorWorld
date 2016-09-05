#ifndef DISPLAY_H
#define DISPLAY_H

class Display {
protected:
    int width;
    int height;
    
    std::chrono::high_resolution_clock::time_point previousTime;
    std::chrono::high_resolution_clock::time_point currentTime;
    
public:
    Renderer *renderer;
    World *world;
    
    Display(int width, int height);
    
    void update();
};

#endif