precision mediump float;
uniform vec2 resolution;
uniform vec2 ship;
uniform float time;
uniform float ratio;
uniform float angle;
uniform float maxDist;
uniform float halfAngle;

float sinc(float x){
	x*=50.;
	return sin(x)/x;
}

void main() {
	/*draw ship shield*/
	vec2 ship1 = vec2(ship.x/(2.*ratio)+0.5,ship.y/2.+0.5);
	vec2 position = ( gl_FragCoord.xy / resolution.xy ) -ship1;
	float lengthFromCentre = length(position);
	float sincScale = sinc(position.x)*sinc(position.y);
	vec3 color = vec3(sincScale,0.,0.);
	gl_FragColor = vec4(color,1.0);
	/*draw clouds*/
	float c = 1.0;
	float inten = .05;
	float t;
	float d = distance((gl_FragCoord.yy/resolution.yy), vec2(0.5,0.5));
	float e = distance((gl_FragCoord.xy/resolution.xy), vec2(0.5,0.5));
	vec4 texColor = vec4(0.0, 0.1, 0.0, 1.0);
	vec2 v_texCoord = gl_FragCoord.xy / resolution;
	vec2 p =  v_texCoord * 8.0 - vec2(20.0);
	vec2 i = p;
	t = (time * 1.0)* (1.0 - (3.0 / float(0+1)));
	i = p + vec2(cos(t - i.x) + sin(t + i.y),sin(t - i.y) + cos(t + i.x));
	c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten), p.y / (cos(i.y+t)/inten)));
	t = time * (1.0 - (3.0 / float(1+1)));
	i = p + vec2(cos(t - i.x) + sin(t + i.y),sin(t - i.y) + cos(t + i.x));
	c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten), p.y / (cos(i.y+t)/inten)));
	t = time * (1.0 - (3.0 / float(2+1)));
	i = p + vec2(cos(t - i.x) + sin(t + i.y),sin(t - i.y) + cos(t + i.x));
	c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten), p.y / (cos(i.y+t)/inten)));
	t = time * (1.0 - (3.0 / float(3+1)));
	i = p + vec2(cos(t - i.x) + sin(t + i.y),sin(t - i.y) + cos(t + i.x));
	c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten), p.y / (cos(i.y+t)/inten)));
	c /= 4.0;
	c = 1.46 - sqrt(c);
	texColor.rgb *= (1.0 / (1.0 - (c + 0.05)));
	texColor *= smoothstep(0.5, 0.0, d);
	texColor *= smoothstep(0.6, 0.0, e);

	/*draw lighting*/
	vec2 up = normalize(vec2(cos(angle),sin(angle)));
	ship1.x = ship1.x-0.1*cos(angle);
	ship1.y = ship1.y-0.1*sin(angle);
	vec2 current = normalize(gl_FragCoord.xy/resolution.xy-ship1);
	float dotp = dot(up,current);
	float maxdot = cos(halfAngle);
	if(dotp>maxdot && lengthFromCentre<maxDist){
		texColor.rgb=texColor.rgb;
	}
	else{
		texColor.rgb=vec3(0.0,0.0,0.0);
	}
	/*texColor *= 10.*dotp;*/

	gl_FragColor += vec4(texColor.rgb, 1.0);
}




