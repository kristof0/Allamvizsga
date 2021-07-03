#version 450 core
in layout (location = 0) vec3 in_position;
in layout (location = 1) vec2 in_texcoord;
out vec2 texcoord;
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
void main() {
    gl_Position = projectionMatrix * modelViewMatrix * vec4(in_position, 1.0);
    texcoord = in_texcoord;
}