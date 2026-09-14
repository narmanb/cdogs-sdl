cmake_minimum_required(VERSION 3.22.1)
project(cdogs_android C CXX)

set(CMAKE_C_STANDARD 99)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

set(ROOT ${CMAKE_CURRENT_LIST_DIR}/../../../..)
set(SDL2MIXER_VENDORED ON CACHE BOOL "" FORCE)
set(SDL2MIXER_SAMPLES OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_INSTALL OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_FLAC OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_MOD OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_MIDI OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_OPUS OFF CACHE BOOL "" FORCE)
set(SDL2MIXER_WAVPACK OFF CACHE BOOL "" FORCE)

add_subdirectory(SDL SDL-build)
add_subdirectory(SDL_mixer SDL_mixer-build)

# C-Dogs uses normal filesystem I/O for its data. CdogsActivity extracts the
# APK assets into SDL's internal-storage directory before native startup, and
# the bridge chdirs there, so ./ is the correct Android data root.
set(CDOGS_DATA_DIR "./" CACHE STRING "C-Dogs Android data directory" FORCE)
set(BUILD_EDITOR OFF CACHE BOOL "" FORCE)
set(BUILD_TESTING OFF CACHE BOOL "" FORCE)
set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
add_subdirectory(${ROOT} cdogs-build EXCLUDE_FROM_ALL)

# SDLActivity loads libmain.so. Link the complete C-Dogs application target
# into it and fail the link if the real application entry point is unresolved.
add_library(main SHARED android_main_bridge.c)
target_include_directories(main PRIVATE ${ROOT}/src)
target_link_libraries(main PRIVATE cdogs-sdl SDL2::SDL2 SDL2_mixer::SDL2_mixer)
target_link_options(main PRIVATE "-Wl,--no-undefined")
