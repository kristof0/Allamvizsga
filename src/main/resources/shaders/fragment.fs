#version 450 core
in vec2 texcoord;
uniform sampler2DArray tex1;
uniform int layer = 1;
uniform int marked=1;
out layout (location = 0) vec4 out_color;
void main() {
    out_color = texture(tex1, vec3(texcoord, layer))+vec4(marked,0,0,0);
}