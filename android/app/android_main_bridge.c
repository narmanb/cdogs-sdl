#include <SDL.h>
#include <SDL_main.h>

/*
 * Temporary first-pass Android bridge.
 * The CI compiler pass will tell us the cleanest way to expose upstream
 * cdogs' main target as libmain.so without changing desktop builds.
 */
int SDL_main(int argc, char *argv[])
{
    (void)argc;
    (void)argv;
    SDL_Log("C-Dogs RP5 Android native bridge loaded");
    return 0;
}
