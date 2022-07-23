cmake_minimum_required(VERSION 3.23.2)
project(dexfinder)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Os ")
set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS} -ffunction-sections -fdata-sections -std=c++20")

find_library(
        ANDROID_LIBS
        log)
add_subdirectory(DexBuilder)
add_library(
        dexfinder SHARED
        biliroaming.cc
)
target_link_libraries(dexfinder dex_builder_static)
target_link_options(${PROJECT_NAME} PRIVATE -flto)

