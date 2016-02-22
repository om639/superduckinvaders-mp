uniform sampler2D u_texture;
uniform vec2 u_centre;
uniform float u_powerupWidth;
uniform float u_currentAngle;
 
varying vec4 v_color;
varying vec2 v_texCoord0;
 
void main() {
	//sample the powerup
	vec4 powerupColor = texture2D(u_texture, v_texCoord0);
	float transparency = 0.2;
	float distanceBetweenPixelAndCentre = length(u_centre - gl_FragCoord.xy);
	float PI = 3.14159265;
	
	//Gets bearing (angle from north) of the pixel using vector dot product
	float angleBetweenNorthAndCentre = acos((dot(gl_FragCoord.xy - u_centre, vec2(0., 1.)))/length(gl_FragCoord.xy - u_centre));
	
	bool eastOfCenter = gl_FragCoord.x >= u_centre.x;
	
	// If pixel is within the range of the powerup sprite
	if (distanceBetweenPixelAndCentre <= u_powerupWidth) {
	
		// Different angle behaviour depending on whether you're on the right side of the powerup or the left
		if (eastOfCenter && angleBetweenNorthAndCentre < u_currentAngle && powerupColor.a != 0.) {
			powerupColor.a = transparency;
		}
		else if ((2.*PI - angleBetweenNorthAndCentre) < u_currentAngle && powerupColor.a != 0.) {
			powerupColor.a = transparency;
		}
	}
	gl_FragColor = powerupColor;
}