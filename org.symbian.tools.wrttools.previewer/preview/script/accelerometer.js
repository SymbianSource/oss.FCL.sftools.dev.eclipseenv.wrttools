function RotationSupport(accelerationCallback) {
	this.controls = new RotationControls(accelerationCallback);
}

function RotationControls(accelCallback) {
	var width = 50, height = 100, depth = 10;
	var margin = 5, bottomMargin = 15;
	var angleX = 180;
	var angleY = 180;
	var angleZ = 180;
	var accelerationCallback = accelCallback;

	$("#sliderX").slider( {
		slide : updateAngleX,
		animate : true,
		max : 360,
		min : 0,
		value : angleX
	});
	$("#sliderY").slider( {
		slide : updateAngleY,
		animate : true,
		max : 360,
		min : 0,
		value : angleY
	});
	$("#sliderZ").slider( {
		slide : updateAngleZ,
		animate : true,
		max : 360,
		min : 0,
		value : angleZ
	});
	renderUI();

	function translateFace(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = depth/2;
		var points = [ {
			x : px,
			y : py,
			z : pz
		}, {
			x : px,
			y : -py,
			z : pz
		}, {
			x : -px,
			y : -py,
			z : pz
		}, {
			x : -px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}
	
	function translateScreen(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = depth/2;
		var points = [ {
			x : px - margin,
			y : py - bottomMargin,
			z : pz
		}, {
			x : px - margin,
			y : -py + bottomMargin,
			z : pz
		}, {
			x : -px + margin,
			y : -py + margin,
			z : pz
		}, {
			x : -px + margin,
			y : py - margin,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}
	
	function translateBack(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = -depth/2;
		var points = [ {
			x : px,
			y : py,
			z : pz
		}, {
			x : px,
			y : -py,
			z : pz
		}, {
			x : -px,
			y : -py,
			z : pz
		}, {
			x : -px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}
	
	function translateTop(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = depth/2;
		var points = [ {
			x : px,
			y : -py,
			z : pz
		}, {
			x : px,
			y : -py,
			z : -pz
		}, {
			x : px,
			y : py,
			z : -pz
		}, {
			x : px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}
	
	function translateBottom(xy, xz, yz) {
		var px = -width / 2;
		var py = height / 2;
		var pz = depth/2;
		var points = [ {
			x : px,
			y : -py,
			z : pz
		}, {
			x : px,
			y : -py,
			z : -pz
		}, {
			x : px,
			y : py,
			z : -pz
		}, {
			x : px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}
	
	function translateLeft(xy, xz, yz) {
		var px = width / 2;
		var py = height / 2;
		var pz = depth/2;
		var points = [ {
			x : px,
			y : py,
			z : pz
		}, {
			x : px,
			y : py,
			z : -pz
		}, {
			x : -px,
			y : py,
			z : -pz
		}, {
			x : -px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}
	
	function translateRight(xy, xz, yz) {
		var px = width / 2;
		var py = -height / 2;
		var pz = depth/2;
		var points = [ {
			x : px,
			y : py,
			z : pz
		}, {
			x : px,
			y : py,
			z : -pz
		}, {
			x : -px,
			y : py,
			z : -pz
		}, {
			x : -px,
			y : py,
			z : pz
		} ];
		return rotate(points, xy, xz, yz);
	}
	
	
	function renderUI() {
		var canvas = document.getElementById("phoneposition");
		var ctx = canvas.getContext("2d");
		ctx.clearRect(0, 0, canvas.width, canvas.height);

		var r = 62;

		var xy = (180 - angleX) * Math.PI / 180;
		var yz = (angleY - 180) * Math.PI / 180;
		var xz = (180 - angleZ) * Math.PI / 180 + Math.PI / 2;

		var back = translateBack(xy, xz, yz);
		if ((back[0].z + back[2].z)/2 < 0) {
			paint(canvas, ctx, back);
		} else {
			paint(canvas, ctx, translateFace(xy, xz, yz));
			paintScreen(canvas, ctx, translateScreen(xy, xz, yz));
		}

		var dz = 0;
		if (back[0].z > back[3].z) {
			var bottom = translateBottom(xy, xz, yz);
			paint(canvas, ctx, bottom);
			dz = bottom[1].y - bottom[0].y;
		} else if (back[0].z != back[3].z) {
			var top = translateTop(xy, xz, yz);
			paint(canvas, ctx, top);
			dz = top[1].y - top[0].y;
		}
		
		if (back[1].z > back[0].z) {
			paint(canvas, ctx, translateLeft(xy, xz, yz));
		} else if (back[1].z != back[0].z) {
			paint(canvas, ctx, translateRight(xy, xz, yz));
		}
		var accelX = (back[1].y - back[0].y) / width;
		var accelY = (back[0].y - back[3].y) / height;
		var accelZ = dz / depth;
		
		notifyAcceleration(accelX, accelY, accelZ);	
	}

	function rotate(points, xy, xz, yz) {
		var res = new Array();
		for ( var p in points) {
			var px = points[p].x;
			var py = points[p].y;
			var pz = points[p].z;

			var rx = Math.sqrt(px * px + py * py);
			var angXY = Math.atan(px / py) + (px < 0 ? Math.PI : 0);

			px = rx * Math.sin(angXY + xy);
			py = rx * Math.cos(angXY + xy);

			var rz = Math.sqrt(px * px + pz * pz);
			var angXZ = (px == 0 ? 0 : Math.atan(pz / px))
					+ (px < 0 ? Math.PI : 0);

			px = rz * Math.sin(angXZ + xz);
			pz = rz * Math.cos(angXZ + xz);

			var ry = Math.sqrt(py * py + pz * pz);
			var angYZ = (pz == 0 ? 0 : Math.atan(py / pz))
					+ (pz < 0 ? Math.PI : 0);

			py = ry * Math.sin(angYZ + yz);
			pz = ry * Math.cos(angYZ + yz);

			res.push( {
				x : px,
				y : py,
				z : pz
			});
		}
		return res;
	}

	function updateAngleX(event, ui) {
		angleX = ui.value;
		renderUI();
	}

	function updateAngleY(event, ui) {
		angleY = ui.value;
		renderUI();
	}

	function updateAngleZ(event, ui) {
		angleZ = ui.value;
		renderUI();
	}

	function paint(canvas, ctx, points) {
		var xcoord = canvas.width / 2;
		var ycoord = canvas.height / 2;

		ctx.fillStyle = "yellow";
		ctx.strokeStyle = "black";
		ctx.beginPath();
		ctx.moveTo(xcoord + points[3].x, ycoord + points[3].y);
		for (point in points) {
			ctx.lineTo(xcoord + points[point].x, ycoord + points[point].y);
		}
		ctx.fill();
		ctx.beginPath();
		ctx.moveTo(xcoord + points[3].x, ycoord + points[3].y);
		for (point in points) {
			ctx.lineTo(xcoord + points[point].x, ycoord + points[point].y);
		}
		ctx.stroke();
	}

	function paintScreen(canvas, ctx, screen) {
		var xcoord = canvas.width / 2;
		var ycoord = canvas.height / 2;

		ctx.fillStyle = "grey";
		ctx.beginPath();
		ctx.moveTo(xcoord + screen[3].x, ycoord + screen[3].y);
		for (point in screen) {
			ctx.lineTo(xcoord + screen[point].x, ycoord + screen[point].y);
		}
		ctx.fill();
	}
	
	function notifyAcceleration(x, y, z) {
		accelerationCallback(x, y, z);
	}
}
