precision mediump float;
uniform vec4 vColor;
uniform vec2 resolution;
uniform vec2 ship;
uniform float angle;
uniform float maxDist;
uniform float halfAngle;

void main() {
	float ratio=resolution.x/resolution.y;
	vec2 ship1 = vec2(ship.x/(2.*ratio)+0.5,ship.y/2.+0.5);
	vec2 position = ( gl_FragCoord.xy / resolution.xy ) -ship1;
	float lengthFromCentre = length(position);
	vec2 up = normalize(vec2(cos(angle),sin(angle)));
	ship1.x = ship1.x-0.1*cos(angle);
	ship1.y = ship1.y-0.1*sin(angle);
	vec2 current = normalize(gl_FragCoord.xy/resolution.xy-ship1);
	float dotp = dot(up,current);
	float maxdot = cos(halfAngle);
	vec3 color = vec3(vColor.rgb);
	if(dotp>maxdot && lengthFromCentre<maxDist){
			color=color;
	}
	else{
		color=vec3(0.0,0.0,0.0);
	}
	gl_FragColor = vec4(color,1.0);
}
