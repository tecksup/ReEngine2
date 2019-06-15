#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

const float offset = 1.0 / 128.0;

void main() {
    vec4 col = texture2D(u_texture, v_texCoords);
	
		if (col.a < 1.0)
			gl_FragColor = vec4(0.5, 0.5, 1.0, 1.0);
		else
			gl_FragColor = col;
}