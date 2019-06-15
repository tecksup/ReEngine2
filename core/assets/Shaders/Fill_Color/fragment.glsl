#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

uniform float outline_Color[4];

void main() {
    vec4 col = texture2D(u_texture, v_texCoords);
	
		if (col.a < 1.0)
			gl_FragColor = vec4(outline_Color[0], outline_Color[1], outline_Color[2], outline_Color[3]);
		else
			gl_FragColor = col;
}