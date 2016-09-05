#ifndef MAIN_H
#define MAIN_H

#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <cmath>
#include <chrono>
#include <sstream>

using namespace std;

#include "time.h"
#include "vector3.h"
#include "matrix.h"
#include "mesh.h"
#include "renderer.h"
#include "world.h"
#include "display.h"

#define SCREEN_WIDTH 800
#define SCREEN_HEIGHT 600
#define COLOR_CHANNELS 3

#define COLOR_WHITE 0xFFFFFF
#define COLOR_BLACK 0x000000
#define COLOR_GREEN 0x27AE60

#define GRID_SIZE 50

#define NUM_VERTICES_PER_PATCH 16

#endif