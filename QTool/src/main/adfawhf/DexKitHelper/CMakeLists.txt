
cmake_minimum_required(VERSION 3.23.2)
project(dexkithelper)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Os ")
set(CMAKE_CXX_FLAGS "${CMAKE_C_FLAGS} -ffunction-sections -fdata-sections -std=c++20")

find_library(
        ANDROID_LIBS
        log)
add_subdirectory(../DexKit ${CMAKE_CURRENT_SOURCE_DIR})


add_library(
        dexkithelper SHARED
        dex_builder.cpp
)

target_link_libraries(dexkithelper dex_kit_static log)


