#version 330 core
layout (location = 0) in vec3 aPos;

out vec3 TexCoords;

uniform mat4 model_mat;
uniform mat4 view_mat;
uniform mat4 proj_mat;

void main()
{
  //  TexCoords = vec3(aPos.x, 1.0 - aPos.y, aPos.z);
	TexCoords = vec3(aPos.x, 1.0 - aPos.y, aPos.z);
    vec4 pos  = proj_mat*view_mat*model_mat*vec4(aPos, 1.0);
    gl_Position = pos.xyww;
}  