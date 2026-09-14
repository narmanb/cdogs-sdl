#define SDL_MAIN_HANDLED
#include <SDL.h>
#include <SDL_system.h>

#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/* The Android-only cdogs-sdl static target contains upstream's real main(). */
extern int main(int argc, char *argv[]);

int SDL_main(int argc, char *argv[])
{
    const char *internalPath = SDL_AndroidGetInternalStoragePath();
    if (internalPath == NULL || internalPath[0] == '\0')
    {
        SDL_Log("C-Dogs RP5: SDL returned no internal storage path");
        return EXIT_FAILURE;
    }

    if (chdir(internalPath) != 0)
    {
        SDL_Log(
            "C-Dogs RP5: chdir(%s) failed: %s",
            internalPath, strerror(errno));
        return EXIT_FAILURE;
    }

    if (setenv("HOME", internalPath, 1) != 0)
    {
        SDL_Log("C-Dogs RP5: setting HOME failed: %s", strerror(errno));
        return EXIT_FAILURE;
    }

    SDL_Log("C-Dogs RP5: launching real C-Dogs main from %s", internalPath);
    return main(argc, argv);
}
