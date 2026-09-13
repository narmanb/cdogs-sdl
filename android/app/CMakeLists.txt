cmake_minimum_required(VERSION 3.22.1)
project(cdogs_android C CXX)

set(CMAKE_C_STANDARD 99)
set(CMAKE_CXX_STANDARD 11)

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

# Build the upstream C-Dogs project as part of the Android shared library.
# The first CI pass is intentionally compiler-driven: any desktop assumptions
# exposed by Android are fixed on this branch rather than guessed in advance.
set(BUILD_EDITOR OFF CACHE BOOL "" FORCE)
set(BUILD_TESTING OFF CACHE BOOL "" FORCE)
set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
add_subdirectory(${ROOT} cdogs-build EXCLUDE_FROM_ALL)

# SDLActivity loads a library named main. C-Dogs' executable target supplies
# the real entry point; this bridge keeps Android packaging explicit while we
# adapt upstream's target into an Android shared object in subsequent fixes.
add_library(main SHARED android_main_bridge.c)
target_include_directories(main PRIVATE ${ROOT}/src)
target_link_libraries(main PRIVATE SDL2::SDL2 SDL2_mixer::SDL2_mixer)
