#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
ANDROID="$ROOT/android"
PROJECT="$ANDROID/project"
SDL_DIR="$ANDROID/vendor/SDL"
MIXER_DIR="$ANDROID/vendor/SDL_mixer"

rm -rf "$PROJECT" "$ANDROID/vendor"
mkdir -p "$ANDROID/vendor"

git clone --depth 1 --branch release-2.32.10 https://github.com/libsdl-org/SDL.git "$SDL_DIR"
git clone --depth 1 --branch release-2.8.1 https://github.com/libsdl-org/SDL_mixer.git "$MIXER_DIR"

cp -a "$SDL_DIR/android-project" "$PROJECT"
cp -a "$SDL_DIR" "$PROJECT/app/jni/SDL"
cp -a "$MIXER_DIR" "$PROJECT/app/jni/SDL_mixer"
mkdir -p "$PROJECT/app/jni/cdogs"

# C-Dogs assets are copied into the APK assets directory. SDL extracts/opens
# packaged assets through Android's asset manager at runtime.
mkdir -p "$PROJECT/app/src/main/assets"
for d in data graphics sounds music dogfights missions; do
  if [ -d "$ROOT/$d" ]; then cp -a "$ROOT/$d" "$PROJECT/app/src/main/assets/"; fi
done

cp "$ANDROID/app/CMakeLists.txt" "$PROJECT/app/jni/CMakeLists.txt"
cp "$ANDROID/app/build.gradle" "$PROJECT/app/build.gradle"
cp "$ANDROID/app/AndroidManifest.xml" "$PROJECT/app/src/main/AndroidManifest.xml"
cp "$ANDROID/app/CdogsActivity.java" "$PROJECT/app/src/main/java/org/libsdl/app/CdogsActivity.java"
cp "$ANDROID/app/strings.xml" "$PROJECT/app/src/main/res/values/strings.xml"
cp "$ANDROID/app/settings.gradle" "$PROJECT/settings.gradle"
cp "$ANDROID/app/build.gradle.root" "$PROJECT/build.gradle"
cp "$ANDROID/app/gradle.properties" "$PROJECT/gradle.properties"

chmod +x "$PROJECT/gradlew"
echo "Android project bootstrapped at $PROJECT"
